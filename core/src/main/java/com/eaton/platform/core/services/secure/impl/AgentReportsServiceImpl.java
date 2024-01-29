package com.eaton.platform.core.services.secure.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.eaton.platform.core.bean.secure.AgentBean;
import com.eaton.platform.core.bean.secure.ResponseOracleDM;
import com.eaton.platform.core.services.secure.AgentReportsMapperService;
import com.eaton.platform.core.services.secure.AgentReportsService;
import com.eaton.platform.core.services.secure.AgentReportsServiceConfiguration;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.services.UserProfileService;
import com.eaton.platform.integration.auth.util.AgentReportsUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Component(service = AgentReportsService.class, immediate = true)
public class AgentReportsServiceImpl implements AgentReportsService {

	private static final Logger LOG = LoggerFactory.getLogger(AgentReportsServiceImpl.class);

	private static final String CAFFEINE_REPORTS_XML_KEY = "reportsCacheXml";

	private static final String XML_FEATURE_EXTERNAL_GENERAL_ENTRIES = "http://xml.org/sax/features/external-general-entities";
	private static final String XML_FEATURE_EXTERNAL_PARAMETER_ENTRIES= "http://xml.org/sax/features/external-parameter-entities";

	@Reference
	private AgentReportsServiceConfiguration agentReportsServiceConfiguration;

	@Reference
	private UserProfileService profileService;

	@Reference
	private HttpClientBuilderFactory httpFactory;

	private PoolingHttpClientConnectionManager conMgr;

	@Reference
	private AuthorizationService authorizationService;

	@Reference
	private AgentReportsMapperService agentReportsMapperService;

	private Cache<String, Map<String, String>> agentReportsAccessTokenCache;

	private Cache<String, Map<String, String>> agentReportsUserInfoCache;


	@Override
	public Map<String, String> getAgents(final SlingHttpServletRequest request) {
		LOG.debug("AgentReportsServiceImpl :: getAgents() :: Started");
		Map<String, String> agentIDs = new HashMap<>();
		String userEmail = AgentReportsUtil.getUserEmail(authorizationService.getTokenFromSlingRequest(request));
		if(!userEmail.isEmpty()){
			agentIDs = agentReportsUserInfoCache.getIfPresent(userEmail);
			if (null == agentIDs || agentIDs.isEmpty()) {
				LOG.debug("Agent IDs not found in cache for {}.", userEmail);
				JsonArray jsonArray = AgentReportsUtil.getUserInfo(conMgr, httpFactory,
						agentReportsServiceConfiguration, userEmail, getARAccessTokenMap());
				if(jsonArray != null) {
					agentIDs = getAgentList(jsonArray);
					agentReportsUserInfoCache.put(userEmail, agentIDs);
					LOG.debug("Agent IDs set in cache for {} :: {}", userEmail, agentIDs);
				}
			} else {
				LOG.debug("Agent IDs found in cache for {}: {}", userEmail, agentIDs);
			}
		}
		LOG.debug("AgentReportsServiceImpl :: getAgents() :: Ended");
		return agentIDs;
	}

	/**
	 * Get the list of Agent Id and its divisions from User Info json array
	 *
	 * @param jsonArray JsonArray
	 * @return agentList Map<String, String>
	 */
	public Map<String, String> getAgentList(JsonArray jsonArray){

		LOG.debug("AgentReportsServiceImpl :: getAgentList() :: Started");
		Map<String, String> agentList = new TreeMap<>(Comparator.comparingInt(Integer::parseInt));
		for (JsonElement jsonElement : jsonArray) {
			JsonObject accessJsonObj = jsonElement.getAsJsonObject();
			LOG.debug("accessJsonObj before converting to agentBean: {}", accessJsonObj);
			AgentBean agentBean = new Gson().fromJson(accessJsonObj, AgentBean.class);
			String mappingCode, divisions = StringUtils.EMPTY;

			//To filter only type agents and the org id(SAPUNI_0000300075_agent_1231_10_10) which contains 1231_10_10
			if("agent".equalsIgnoreCase(agentBean.getType()) && agentBean.getId().contains("agent_")){
				LOG.debug("Adding {} {} to agentList", agentBean.getType(), agentBean.getId());
				mappingCode = agentBean.getId().split("agent_")[1];
				LOG.debug("mapping code: {}", mappingCode);
				final String agentID = agentBean.getId().split("_")[1].replaceFirst("0000","");
				Map<String, String> divisionsMap = agentReportsMapperService.getDivisionByMappingCode(mappingCode);
				addAgent(divisionsMap, agentList, agentID, divisions);
			}
		}

		// Remove items which are null as value.
		LOG.debug("agentList before removing null values: {}", agentList);
		agentList.values().removeAll(Collections.singleton(StringUtils.EMPTY));
		LOG.debug("agentList after removing null values: {}", agentList);

		LOG.debug("AgentReportsServiceImpl :: getAgentList() :: Ended");
		return agentList;
	}

	/**
	 *  Adds agent id's to the list.
	 * @param divisionsMap
	 * @param agentList
	 * @param agentID
	 * @param divisions
	 */
	private static void addAgent(Map<String, String> divisionsMap , Map<String, String> agentList, String agentID, String divisions){

		LOG.debug("AgentReportsServiceImpl :: addAgent() :: START");
		LOG.debug("agentID: {}", agentID);
		LOG.debug("agentList: {}", agentList);
		LOG.debug("divisionsMap: {}", divisionsMap);
		LOG.debug("divisions: {}", divisions);

		//filter the duplicate entries of division display text across the agent ID's
		if(agentList != null) {
			for (Map.Entry<String, String> entry : divisionsMap.entrySet()) {
				if (!agentList.isEmpty() && agentList.containsKey(agentID)) {
					String agentValue = agentList.get(agentID);
					LOG.debug("agentValue: {}", agentValue);
					if (agentValue.contains(entry.getKey())) {
						divisions = agentValue;
						continue;
					}
					divisions = agentValue;
				}
				LOG.debug("divisions: {}", divisions);
				if (!divisions.isEmpty()) {
					divisions = divisions + ",";
				}
				divisions += entry.getKey() + ":" + entry.getValue();
				LOG.debug("Agent Id: {} Divisions: {} before putting in agentList", agentID, divisions);
				agentList.put(agentID, divisions);
				LOG.debug("Agent Id: {} Divisions: {} put in agentList", agentID, divisions);
			}
		}

		LOG.debug("AgentReportsServiceImpl :: addAgent() :: END");
	}

	@Override
	public ArrayList<ResponseOracleDM> getReport(String agentnumber, String division)
			throws IOException {
		LOG.debug("AgentReportsServiceImpl :: getReport() :: Started");
		ArrayList<ResponseOracleDM> advSearResList = new ArrayList<>();
		try {
			String outputString = "";
			// Check if data present in Cache
			URL url = new URL(agentReportsServiceConfiguration.getDmAPIEndPointURL());
			HttpURLConnection httpConn = getHttpUrlConnection(url);
			if (httpConn != null) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				String xmlInput = getXMLInput(agentnumber, SecureConstants.DIVISION_STATIC_VALUE + division);
				byte[] buffer = new byte[xmlInput.length()];
				buffer = xmlInput.getBytes();
				bout.write(buffer);
				byte[] b = bout.toByteArray();
				setConnProperty(httpConn, b);
				OutputStream out = httpConn.getOutputStream();
				// Write the content of the request to the outputstream of the HTTP Connection.
				out.write(b);
				out.close();
				// Ready with sending the request.//Read the response to read for report
				InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
				BufferedReader in = new BufferedReader(isr);
				outputString = IOUtils.toString(in);
				LOG.debug("Agent reports :: get report reponse :: {}", outputString);
			}
			// Parse the String output to a org.w3c.dom.Document and be able to reach every
			// node with the org.w3c.dom API.
			Document document = parseXmlFile(outputString);
			document.getDocumentElement().normalize();
			NodeList advancedSearchResult = document.getElementsByTagName(SecureConstants.ADVANCED_SEARCH_RESULT);
			retrieveAdvancedSearchResult(advSearResList, advancedSearchResult, agentnumber, division);
			LOG.info("advSearResList" + advSearResList.size());
			LOG.debug("AgentReportsServiceImpl :: getReport() :: Ended");
		} catch (Exception e) {
			LOG.error("Exception while getting report: {}", e.getMessage());
		}
		return advSearResList;
	}

	private HttpURLConnection getHttpUrlConnection(URL url) throws IOException {
		LOG.debug("AgentReportsServiceImpl :: getHttpUrlConnection() :: Started");
		try {
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			LOG.debug("AgentReportsServiceImpl :: getHttpUrlConnection() :: Ended");
			return httpConn;
		} catch (Exception exception) {
			LOG.error("ATG: GetHttpUrlConnection Exception - {}", exception.getMessage());
			return null;
		}

	}

	private void setConnProperty(HttpURLConnection httpConn, byte[] b) throws ProtocolException {
		LOG.debug("AgentReportsServiceImpl :: setConnProperty() :: Started");
		try {
			String SOAPAction = agentReportsServiceConfiguration.getDmAPIEndPointURL();
			httpConn.setRequestProperty(SecureConstants.CONTENT_LENGTH, String.valueOf(b.length));
			httpConn.setRequestProperty(SecureConstants.CONTENT_TYPE, "text/xml; charset=utf-8");
			httpConn.setRequestProperty(SecureConstants.SOAP_ACTION, SOAPAction);
			httpConn.setRequestMethod(SecureConstants.POST);
			httpConn.setDoOutput(SecureConstants.BOOLEAN_TRUE);
			httpConn.setDoInput(SecureConstants.BOOLEAN_TRUE);
			String userpass = agentReportsServiceConfiguration.getDmAPIUsername() + ":" + agentReportsServiceConfiguration.getDmAPIPassword();
			byte[] bytes = Base64.encodeBase64(userpass.getBytes());
			String authStr = new String(bytes);
			String basicAuth = SecureConstants.BASIC + authStr;
			httpConn.setRequestProperty(SecureConstants.AUTHORIZATION, basicAuth);
		} catch (Exception e) {
			LOG.error("Exception while setting connection propertied: {}", e.getMessage());
		}
		LOG.debug("AgentReportsServiceImpl :: setConnProperty() :: Ended");
	}

	private Document parseXmlFile(String in) {
		LOG.debug("AgentReportsServiceImpl :: parseXmlFile() :: Started");
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature(XML_FEATURE_EXTERNAL_GENERAL_ENTRIES, false);
			dbf.setFeature(XML_FEATURE_EXTERNAL_PARAMETER_ENTRIES, false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			LOG.debug("AgentReportsServiceImpl :: parseXmlFile() :: Ended");
			return db.parse(is);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private String getXMLInput(String agentnumber, String division) {
		LOG.debug("AgentReportsServiceImpl :: getXMLInput() :: Started");
	    String xmlInput =
	    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sear=\"http://www.stellent.com/Search/\">\n" +
	    "<soapenv:Header/>\n" +
	    "<soapenv:Body>\n" +
	    "<sear:AdvancedSearch>\n" +
	    "<sear:queryText>dDocTitle &lt;Starts&gt;`" + agentnumber + "`&lt;AND&gt; dDocAccount &lt;Matches&gt;`" + division + "`</sear:queryText>\n" +
	    "<sear:sortField>dInDate</sear:sortField>\n" +
	    "<sear:sortOrder>"+SecureConstants.DATE_SORT +"</sear:sortOrder>\n" +
	    "<sear:resultCount>"+agentReportsServiceConfiguration.getResultCount()+"</sear:resultCount>\n" +
	    "<sear:extraProps>\n" +
	    "<sear:property>\n" +
	    "<sear:name>"+agentReportsServiceConfiguration.getDmAPIUsername()+"</sear:name>\n" +
	    "<sear:value>"+agentReportsServiceConfiguration.getDmAPIPassword()+"</sear:value>\n" +
	    "</sear:property>\n" +
	    "</sear:extraProps>\n" +
	    "</sear:AdvancedSearch>\n" +
	    "</soapenv:Body>\n" +
	    "</soapenv:Envelope>\n";
	    LOG.debug("AgentReportsServiceImpl :: getXMLInput() :: Soap request for agent reports: {} ", xmlInput);
	    LOG.debug("AgentReportsServiceImpl :: getXMLInput() :: Ended");
		return xmlInput;
	}

	private void retrieveAdvancedSearchResult(ArrayList<ResponseOracleDM> advSearResList,
			NodeList advancedSearchResult, String agentnumber, String division) {
		LOG.debug("AgentReportsServiceImpl :: retrieveAdvancedSearchResult() :: Started");
		ResponseOracleDM sdvancedSearchResults;
		for (int temp = 0; temp < advancedSearchResult.getLength(); temp++) {
			LOG.info("First Node -------------------Item---------------------");
			Node node = advancedSearchResult.item(temp);
			NodeList searchResultsList = node.getChildNodes();
			for (int temps = 0; temps < searchResultsList.getLength(); temps++) {
				NodeList searchResul = searchResultsList.item(temps).getChildNodes();
				sdvancedSearchResults = new ResponseOracleDM();
				if (SecureConstants.IDC_SEARCHRESULT.equalsIgnoreCase(searchResultsList.item(temps).getNodeName())) {
					for (int tem = 0; tem < searchResul.getLength(); tem++) {

						if (SecureConstants.IDC_DID.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDID(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_DREVISIONID.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDRevisionID(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_DDOCNAME.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDDocName(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_DDOCTITLE.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDDocTitle(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_DDOC_TYPE.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDDocType(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_DDOC_AUTHOR.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDDocAuthor(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_DSECURITY_GRP.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDSecurityGroup(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_DDOC_ACCNT.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDDocAccount(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_DEXTN.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDExtension(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_DWEB_EXTN.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDWebExtension(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_D_REV_LABEL.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDRevLabel(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_D_IN_DATE.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDInDate(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_D_OUT_DATE.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDOutDate(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_D_FORMAT.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDFormat(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_D_ORIGINAL_NAME.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDOriginalName(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_URL.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setUrl(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_D_GIF.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDGif(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_WEB_FILE_SIZE.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setWebFileSize(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_VAULT_FILE_SIZE.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setVaultFileSize(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_ALTERNATE_FILE_SIZE.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setAlternateFileSize(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_ALTERNATE_FORMAT.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setAlternateFormat(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_D_PUBLISH_TYPE.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDPublishType(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_D_RENDITION_1.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDRendition1(searchResul.item(tem).getTextContent());
						} else if (SecureConstants.IDC_D_RENDITION_2.equalsIgnoreCase(searchResul.item(tem).getNodeName())) {
							sdvancedSearchResults.setDRendition2(searchResul.item(tem).getTextContent());
						}
					}
					if(sdvancedSearchResults.getDDocTitle().contains(agentnumber) && sdvancedSearchResults.getDDocAccount().contains(division) ) {
						advSearResList.add(sdvancedSearchResults);
					}
				}
			}
		}
		LOG.debug("AgentReportsServiceImpl :: retrieveAdvancedSearchResult() :: Ended");
	}

	@Override
	public Document getFileDownloaded(String documentTitle) throws IOException {
		LOG.debug("AgentReportsServiceImpl :: getFileDownloaded() :: Started");
		String responseString = "";
		String outputString = "";
		URL url = new URL(agentReportsServiceConfiguration.getDmAPIEndPointURL());
		HttpURLConnection httpConn = getHttpUrlConnection(url);
		Document document = null;
		if(httpConn != null) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			String xmlInput = getFileDownloadXML(documentTitle);
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			// Set the appropriate HTTP parameters.
			setFileDownloadconnProperty(httpConn, b);
			OutputStream out = httpConn.getOutputStream();
			// Write the content of the request to the outputstream of the HTTP Connection.
			out.write(b);
			out.close();
			// Read the response to read for report
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			// Write the SOAP message response to a String.
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			LOG.debug("Agent reports :: File download :: {}" + outputString);
			// Parse the String output to a org.w3c.dom.Document and be able to reach every
			// node with the org.w3c.dom API.
			document = parseXmlFile(outputString);
			document.getDocumentElement().normalize();
		}
		LOG.debug("AgentReportsServiceImpl :: getFileDownloaded() :: Ended");
		return document;

	}

	private String getFileDownloadXML(String documentTitle) {
		LOG.debug("AgentReportsServiceImpl :: getFileDownloadXML() :: Started");
	    String xmlInput =
	    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:get=\"http://www.stellent.com/GetFile/\">\n" +
	    "<soapenv:Header/>\n" +
	    "<soapenv:Body>\n" +
	    "<get:GetFileByName>\n" +
	    "<get:dDocName>" + documentTitle + "</get:dDocName>\n" +
	    "<get:revisionSelectionMethod>latestReleased</get:revisionSelectionMethod>\n" +
	    "<get:rendition>primary</get:rendition>\n" +
	    "<get:extraProps>\n" +
	    "<get:property>\n" +
	    "<get:name>"+agentReportsServiceConfiguration.getDmAPIUsername()+"</get:name>\n" +
	    "<get:value>"+agentReportsServiceConfiguration.getDmAPIPassword()+"</get:value>\n" +
	    "</get:property>\n" +
	    "</get:extraProps>\n" +
	    "</get:GetFileByName>\n" +
	    "</soapenv:Body>\n" +
	    "</soapenv:Envelope>\n";
	    LOG.debug("AgentReportsServiceImpl :: getXMLInput() :: Soap request for agent report download: {}" , xmlInput);
	    LOG.debug("AgentReportsServiceImpl :: getFileDownloadXML() :: Ended");
		return xmlInput;
	}

	private void setFileDownloadconnProperty(HttpURLConnection httpConn, byte[] b) throws ProtocolException {
		LOG.debug("AgentReportsServiceImpl :: setFileDownloadconnProperty() :: Started");
		try {
			String SOAPAction = agentReportsServiceConfiguration.getDmAPIEndPointURL();
			httpConn.setRequestProperty(SecureConstants.CONTENT_LENGTH, String.valueOf(b.length));
			httpConn.setRequestProperty(SecureConstants.CONTENT_TYPE, SecureConstants.CONTENT_TYPE_TEXT_XML_CHARSET);
			httpConn.setRequestProperty(SecureConstants.SOAP_ACTION, SOAPAction);
			httpConn.setRequestMethod(SecureConstants.POST);
			httpConn.setDoOutput(SecureConstants.BOOLEAN_TRUE);
			httpConn.setDoInput(SecureConstants.BOOLEAN_TRUE);
			String userpass = agentReportsServiceConfiguration.getDmAPIUsername() + ":" + agentReportsServiceConfiguration.getDmAPIPassword();
			byte[] bytes = Base64.encodeBase64(userpass.getBytes());
			String authStr = new String(bytes);
			String basicAuth = SecureConstants.BASIC + authStr;
			httpConn.setRequestProperty(SecureConstants.AUTHORIZATION, basicAuth);
			LOG.debug("AgentReportsServiceImpl :: setFileDownloadconnProperty() :: Ended");
		} catch (Exception e) {
			LOG.error("Exception while setting file downloading connection properties: {}",e.getMessage());
		}
	}

	private Map<String, String> getARAccessTokenMap() {
		LOG.debug("AgentReportsServiceImpl :: getARAccessTokenMap() :: Started");
		Map<String, String> arAccessTokenMap = agentReportsAccessTokenCache.getIfPresent(SecureConstants.AR_ACCESS_TOKEN_MAP);
		if (null == arAccessTokenMap || arAccessTokenMap.isEmpty()) {
			LOG.debug("Access token and jsession id cookie not found in cache. Getting new access token and jsession id.");
			arAccessTokenMap = AgentReportsUtil.getAccessTokenAPI(conMgr, httpFactory, agentReportsServiceConfiguration);
			agentReportsAccessTokenCache.put(SecureConstants.AR_ACCESS_TOKEN_MAP, arAccessTokenMap);
			LOG.debug("Set cache for access token: {}", arAccessTokenMap.get(SecureConstants.ACCESS_TOKEN));
			LOG.debug("Set cache for jsession id cookie: {}", arAccessTokenMap.get(SecureConstants.COOKIE));
		} else {
			LOG.debug("Access token and jsession id cookie found in cache");
			LOG.debug("access token: {}", arAccessTokenMap.get(SecureConstants.ACCESS_TOKEN));
			LOG.debug("jsession id cookie: {}", arAccessTokenMap.get(SecureConstants.COOKIE));
		}
		LOG.debug("AgentReportsServiceImpl :: getARAccessTokenMap() :: Ended");
		return arAccessTokenMap;
	}

	private static String getShortenAgentId(String id) {
		String value = StringUtils.EMPTY;
		try {
			int firstIndex = id.indexOf(SecureConstants.UNDER_SCORE);
			int secondIndex = id.indexOf(SecureConstants.UNDER_SCORE, firstIndex + 1);
			if(firstIndex < secondIndex) {
				value = (String) id.subSequence(firstIndex + 5, secondIndex);
			}
		}catch (Exception e) {
			LOG.error("Exception while getting short agentId : {}",e.getMessage());
		}
		return value;
	}


	@Activate
    @Modified
    protected final void activate() {
		LOG.debug("AgentReportsServiceImpl :: activate() :: Started");
		agentReportsAccessTokenCache = Caffeine.newBuilder()
                .maximumSize(agentReportsServiceConfiguration.getMaxCacheSize())
                .expireAfterWrite(agentReportsServiceConfiguration.getAccessTokenCacheTTL(), TimeUnit.SECONDS)
                .build();

		agentReportsUserInfoCache = Caffeine.newBuilder()
                .maximumSize(agentReportsServiceConfiguration.getMaxCacheSize())
                .expireAfterWrite(agentReportsServiceConfiguration.getUserInfoCacheTTL(), TimeUnit.SECONDS)
                .build();

		LOG.debug("AgentReportsServiceImpl :: activate() :: Ended");
    }

}
