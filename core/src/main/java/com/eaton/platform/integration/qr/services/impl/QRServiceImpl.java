package com.eaton.platform.integration.qr.services.impl;

import com.adobe.acs.commons.email.EmailService;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.apigee.constants.ApigeeConstants;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.util.EatonAuthUtil;
import com.eaton.platform.integration.qr.bean.QRSerialValidationBean;
import com.eaton.platform.integration.qr.constants.QRConstants;
import com.eaton.platform.integration.qr.services.QRService;
import com.eaton.platform.integration.qr.services.config.QRCodeConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component(service = QRService.class, immediate = true)
@Designate(ocd = QRCodeConfig.class)
public class QRServiceImpl implements QRService {

	private static final Logger LOGGER = LoggerFactory.getLogger(QRServiceImpl.class);
	private String validationUri ;
	private String authCodeUri;
	private String repIssueUri;
	private String catalogNumber;
	private String scanImage;
	private String registeredUser;
	private String userId;
	private String language;
	private String apiKey;
	private String apiSecret;
	private String mobileLocation;
	private int maxConnections;
	private int maxHostConnections;
	private String authenticationToolType;
	private PoolingHttpClientConnectionManager conMgr;
	private String geoLocationCountry;
	private String geoLocationCity;
	private String gpsLocation;
	private String geoLocationHeader;
	private String ipAddressHeader;
	private String countryCodeJsonPath;
	private String[] headerAttributesArr;
	private Map<String,String> headerAttrMap;
	private Cache<String, String> accessTokenCache;
	private static final Pattern REGEX_PATTERN = Pattern.compile("\\s");
	private String accessTokenUrl;

	private enum geoLocationAttributes {
		COUNTRY,
		CITY,
		LATITUDE,
		LONGITUDE
	}

	// Email Service Reference
	@Reference
	private EmailService emailService;

	@Reference
	private AdminService adminService;

	@Reference
	private HttpClientBuilderFactory httpFactory;

	@Activate
	@Modified
	protected void activate(final QRCodeConfig config) {

		this.validationUri = config.qr_validation_url();
		this.authCodeUri = config.qr_authCode_validation_url();
		this.repIssueUri = config.qr_reportissue_url();
		this.apiKey=config.qr_validation_apikey();
		this.apiSecret=config.qr_validation_apisecret();
		this.catalogNumber = config.catalog_number();
		this.authenticationToolType = config.authentication_tool_type();
		this.scanImage = config.scan_image();
		this.registeredUser = config.registered_user();
		this.userId = config.user_id();
		this.language = config.language();
		this.geoLocationHeader = config.geolocation_akamai_header();
		this.ipAddressHeader = config.true_client_ip_header();
		this.countryCodeJsonPath = config.country_code_mapping();
		this.headerAttributesArr = config.request_header_attributes();
		this.accessTokenUrl = config.qr_oauth_accessToken_url();
		maxConnections = config.maxConnections();
		maxHostConnections = config.maxHostConnections();
		accessTokenCache = Caffeine.newBuilder()
				.maximumSize(config.accessTokenCacheSize())
				.expireAfterWrite(config.accessTokenCacheDuration(), TimeUnit.MINUTES)
				.build();
	}

	@Override
	public JsonObject validateSerialNumber(String serialNumber, String eventID, String catalogNum, Map<String, String> headerMap) {
		LOGGER.debug("Start with validateSerialNumber method ::");
		if(null == catalogNum || catalogNum.isEmpty()){
			catalogNum = this.catalogNumber;
		}
		final String ipAddress = getIpAddressFromHeader(headerMap);
		final String geoLocationHeaderVal = headerMap.get(geoLocationHeader);
		setGeoLocationParam(geoLocationHeaderVal);
		QRSerialValidationBean qRSerialValidationBean = new QRSerialValidationBean();
		qRSerialValidationBean.setSerialNumber(serialNumber.toUpperCase(Locale.ENGLISH));
		qRSerialValidationBean.setCatalogNumber(catalogNum);
		qRSerialValidationBean.setIp4Address(StringUtils.EMPTY);
		qRSerialValidationBean.setIp6Address(ipAddress);
		qRSerialValidationBean.setGeolocationCountry(geoLocationCountry);
		qRSerialValidationBean.setGeolocationCity(geoLocationCity);
		qRSerialValidationBean.setGpsLocation(gpsLocation);
		qRSerialValidationBean.setMobileLocation(mobileLocation);
		qRSerialValidationBean.setAuthenticationToolType(authenticationToolType);
		qRSerialValidationBean.setScanImage(scanImage);
		qRSerialValidationBean.setRegisteredUser(registeredUser);
		qRSerialValidationBean.setUserId(userId);
		qRSerialValidationBean.setLanguage(language);
		qRSerialValidationBean.setEventId(eventID);
		LOGGER.debug("End with validateSerialNumber method ::");
		return getQRServiceResponse(qRSerialValidationBean,this.validationUri);
	}

	@Override
	public JsonObject validateAuthCode(final String authCode, final String serialNum, String catalogNumber, Map<String, String> headersMap) {
		LOGGER.debug("Start with validateAuthCode method ::");
		if (null == catalogNumber || catalogNumber.isEmpty()) {
			catalogNumber = this.catalogNumber;
		}
		final String ipAddress = getIpAddressFromHeader(headersMap);
		final String geoLocationHeaderVal = headersMap.get(geoLocationHeader);
		setGeoLocationParam(geoLocationHeaderVal);
		QRSerialValidationBean qRSerialValidationBean = new QRSerialValidationBean();
		qRSerialValidationBean.setSerialNumber(serialNum.toUpperCase(Locale.ENGLISH));
		qRSerialValidationBean.setAuthCode(authCode.toUpperCase(Locale.ENGLISH));
		qRSerialValidationBean.setCatalogNumber(catalogNumber);
		qRSerialValidationBean.setIp4Address(StringUtils.EMPTY);
		qRSerialValidationBean.setIp6Address(ipAddress);
		qRSerialValidationBean.setGeolocationCountry(geoLocationCountry);
		qRSerialValidationBean.setGeolocationCity(geoLocationCity);
		qRSerialValidationBean.setGpsLocation(gpsLocation);
		qRSerialValidationBean.setMobileLocation(mobileLocation);
		qRSerialValidationBean.setAuthenticationToolType(authenticationToolType);
		qRSerialValidationBean.setScanImage(scanImage);
		qRSerialValidationBean.setRegisteredUser(registeredUser);
		qRSerialValidationBean.setUserId(userId);
		qRSerialValidationBean.setLanguage(language);
		LOGGER.debug("End with validateAuthCode method ::");
		return getQRServiceResponse(qRSerialValidationBean,this.authCodeUri);
	}

	/**
	 * This method will set geoLocation parameters
	 * @param geoLocationHeaderVal header value
	 */
	private void setGeoLocationParam(String geoLocationHeaderVal) {
		LOGGER.debug("Start with setGeoLocationParam method ::");
		getHeaderAttrConfigMap(this.headerAttributesArr);
		if(StringUtils.isNotBlank(geoLocationHeaderVal)) {
			HashMap<String, String> countryMap = (HashMap<String, String>) Arrays.asList(geoLocationHeaderVal.split(CommonConstants.COMMA)).stream().map(s ->
					s.split(CommonConstants.EQUALS_SYMBOL)).collect(Collectors.toMap(e -> e[0].trim(), e -> e[1].trim()));
			this.geoLocationCountry = mapCountryCodeToName(countryMap.get(this.headerAttrMap.get(geoLocationAttributes.COUNTRY.toString())));
			this.geoLocationCity = countryMap.get(this.headerAttrMap.get(geoLocationAttributes.CITY.toString())) != null ?
											countryMap.get(this.headerAttrMap.get(geoLocationAttributes.CITY.toString())) : StringUtils.EMPTY;
			String latitude = countryMap.get(this.headerAttrMap.get(geoLocationAttributes.LATITUDE.toString())) != null ?
											countryMap.get(this.headerAttrMap.get(geoLocationAttributes.LATITUDE.toString())) : StringUtils.EMPTY;
			String longitude = countryMap.get(this.headerAttrMap.get(geoLocationAttributes.LONGITUDE.toString())) != null ?
											countryMap.get(this.headerAttrMap.get(geoLocationAttributes.LONGITUDE.toString())) : StringUtils.EMPTY;
			this.gpsLocation = latitude.concat(CommonConstants.COMMA).concat(longitude);
		}
		LOGGER.debug("End with setGeoLocationParam method ::");
		//Set mobile location after confirmation
	}


	/**
	 * This method will set a map containing all required request header attributes.
	 * @param headerArray
	 */
	private void getHeaderAttrConfigMap(String[] headerArray) {
		this.headerAttrMap = new HashMap<>();
		if(headerArray != null){
			for(String element : headerArray){
				Matcher matcher = REGEX_PATTERN.matcher(element);
				String elementStr = matcher.replaceAll("");
				this.headerAttrMap.put(elementStr.split("=")[0].toUpperCase(Locale.ENGLISH),elementStr.split("=")[1]);
			}
		}
	}


	/**
	 * This method will get IP Address from request headers.
	 * @param headerMap to get IP headers
	 * @return ipAddress of the client
	 */
	private String getIpAddressFromHeader(Map<String, String> headerMap) {
		LOGGER.debug("Start with getIpAddressFromHeader method ::");
		String ipAddress = headerMap.get(ipAddressHeader);
		if(ipAddress == null || StringUtils.isBlank(ipAddress)){
			ipAddress = headerMap.get(QRConstants.X_FORWARDED_FOR) != null ? headerMap.get(QRConstants.X_FORWARDED_FOR) : StringUtils.EMPTY;

		}
		LOGGER.debug("End with getIpAddressFromHeader method ::");
		return ipAddress;
	}


	/**
	 * This method will get IP Address from request headers.
	 * @param countryCode to get country name
	 * @return countryName from geoLocation.
	 */
	private String mapCountryCodeToName(final String countryCode) {
		LOGGER.debug("Start with mapCountryCodeToName method ::");
		String countryName = StringUtils.EMPTY;
		if (null != adminService) {
			try (final ResourceResolver readServiceResourceResolver = adminService.getReadService()) {
				if (StringUtils.isNotBlank(countryCodeJsonPath)) {
					final String reqJsonString = CommonUtil.getResponseStringFromFile(readServiceResourceResolver, countryCodeJsonPath);
					if (StringUtils.isNotBlank(reqJsonString) && new JSONObject(reqJsonString).getJSONObject(countryCode) != null) {
						countryName = new JSONObject(reqJsonString).getJSONObject(countryCode).getString(QRConstants.COUNTRY_NAME);
					}
				}
			} catch (RepositoryException e) {
				LOGGER.error("Exception in mapCountryCodeToName :: RepositoryException",e);
			} catch (IOException e) {
				LOGGER.error("Exception in mapCountryCodeToName :: IOException",e);
			} catch (JSONException e) {
				LOGGER.error("Exception in mapCountryCodeToName :: JSONException",e);
			}
		}
		LOGGER.debug("End with mapCountryCodeToName method ::");
		return countryName;
	}


	/**
	 * This method will get the response from EPAS for qr service
	 * @param qrSerialValidationBean qr bean parameter
	 * @param apiUrl EPAS api url
	 * @return responsejson
	 */
	private JsonObject getQRServiceResponse(final QRSerialValidationBean qrSerialValidationBean, final String apiUrl) {
		JsonObject responseJson = null;
		BufferedReader bufferedReader = null;
		String accessToken = getAccessTokenForValidationAPI();
		conMgr = getMultiThreadedConf();
		try {
			if (StringUtils.isNotEmpty(accessToken)) {
				final HttpClient httpClient = httpFactory.newBuilder().setConnectionManager(conMgr).build();

				final List<NameValuePair> headers = new ArrayList<>();
				headers.add(new BasicNameValuePair(AuthConstants.AUTHORIZATION_HEADER_KEY,
						StringUtils.join(AuthConstants.BEARER_SPACE, accessToken)));
				headers.add(new BasicNameValuePair(CommonConstants.ACCEPT, CommonConstants.APPLICATION_JSON));

				LOGGER.info("serial Number :: {}", qrSerialValidationBean.getSerialNumber());
				LOGGER.info("Catalog Number ::{}", qrSerialValidationBean.getCatalogNumber());
				final ObjectMapper obj = new ObjectMapper();
				final String jsonStr = obj.writeValueAsString(qrSerialValidationBean);

				HttpPost postConnection = CommonUtil.getHttpPostMethod(apiUrl, CommonConstants.APPLICATION_JSON, CommonConstants.APPLICATION_JSON);
				if (!headers.isEmpty()) {
					for (NameValuePair header : headers) {
						postConnection.setHeader(header.getName(), header.getValue());
					}
					LOGGER.debug("headers set");
				}
				StringEntity requestEntity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
				LOGGER.debug("Request Entity ::{}", jsonStr);
				postConnection.setEntity(requestEntity);

				final HttpResponse response = httpClient.execute(postConnection);
				final Integer responseCode = response.getStatusLine().getStatusCode();
				LOGGER.debug("Response Code ::{}", responseCode);
				if (responseCode == 200) {
					bufferedReader = new BufferedReader(
							new InputStreamReader(response.getEntity().getContent()));
					final StringBuilder content = new StringBuilder();
					String line;
					while (null != (line = bufferedReader.readLine())) {
						content.append(line);
					}
					if (content.length() > 0) {
						JsonParser jsonParser = new JsonParser();
						responseJson = (JsonObject) jsonParser.parse(content.toString());
					}
					bufferedReader.close();
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Response Json from getQRServiceResponse method :: {} ", (responseJson != null ? responseJson.toString() : null));
				}
			}
		} catch (MalformedURLException e) {
			LOGGER.error("MalformedURLException in getQRServiceResponse method ::{}",e.getMessage());
		}catch (IOException e) {
			LOGGER.error("IOException in getQRServiceResponse method ::{}",e.getMessage());
		}finally {
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					LOGGER.error("While closing bufferedReader :: {}",e.getMessage());
				}
			}
		}
		return responseJson;
	}

	public JsonObject reportIssue(final String fullname, final String reporterEmail,final String comments,final String serialNumber, final String catalogNumber,final String eventID,final String authCode,final String repIssueEmail){
		LOGGER.debug("Start with reportIssue method ::");
		JsonObject responseJson = null;
		String errorStatus = "ERRCD01";
		QRSerialValidationBean qRSerialValidationBean = new QRSerialValidationBean();
		qRSerialValidationBean.setSerialNumber(serialNumber);
		qRSerialValidationBean.setCatalogNumber(catalogNumber);
		qRSerialValidationBean.setEventId(eventID);
		qRSerialValidationBean.setcontactEmail(reporterEmail);
		qRSerialValidationBean.setfullName(fullname);
		qRSerialValidationBean.setComments(comments);
		qRSerialValidationBean.setAuthCode(authCode);

		responseJson=getQRServiceResponse(qRSerialValidationBean,this.repIssueUri);
		if(responseJson != null) {
			if (LOGGER.isDebugEnabled()) {   
				LOGGER.debug("Response Json from reportIssue method :: {} ", responseJson);
			}		
			String errCode = responseJson.get("errorCode").getAsString();				
			if(errorStatus.equalsIgnoreCase(errCode)){
				qRSerialValidationBean.setEventId("1");
				responseJson=getQRServiceResponse(qRSerialValidationBean,this.repIssueUri);
			}
		}
		sendEmail(qRSerialValidationBean,repIssueEmail,reporterEmail);
		LOGGER.debug("End with reportIssue method ::");

		return responseJson;
	}

	private void sendEmail(QRSerialValidationBean qRSerialValidationBean,final String repIssueEmail,final String reporterEmail){
		//Set the dynamic variables of your email template
		Map<String, String> emailParams = new HashMap<>();

		emailParams.put("serialNumber",qRSerialValidationBean.getSerialNumber());
		emailParams.put("catalogNumber",qRSerialValidationBean.getCatalogNumber());
		emailParams.put("fullName",qRSerialValidationBean.getfullName());
		emailParams.put("contactEmail",qRSerialValidationBean.getcontactEmail());
		emailParams.put("comments",qRSerialValidationBean.getComments());
		emailParams.put("eventID",qRSerialValidationBean.getEventId());
		emailParams.put("authCode",qRSerialValidationBean.getAuthCode());

		// Array of email recipients
		String[] recipients = {repIssueEmail};

		// Array of reporter email recipients
		String[] reporterRecipients = {reporterEmail};

		List<String> repFailureList = emailService.sendEmail(QRConstants.EMAIL_TEMPLATE_PAH, emailParams, recipients);

		List<String> reporterFailureList = emailService.sendEmail(QRConstants.REPORTER_EMAIL_TEMPLATE_PAH, emailParams, reporterRecipients);

		if (repFailureList.isEmpty() && reporterFailureList.isEmpty()) {
			LOGGER.debug("QRServiceImpl: sendEmail() :: Email sent successfully to the recipients");

		} else {
			LOGGER.debug("QRServiceImpl: sendEmail() :: Email sent failed {} {}", repFailureList, reporterFailureList);
		}
	}

	/**
	 * Get cached access token if valid still. If not, refresh the access token's
	 * caches.
	 *
	 * @return accessToken
	 */
	private String getAccessTokenForValidationAPI() {
		LOGGER.debug("QRServiceImpl :: getAccessToken() :: Started");

		String accessToken = accessTokenCache.getIfPresent(AuthConstants.ACCESS_TOKEN_KEY);

		if (accessToken == null || StringUtils.isEmpty(accessToken)) {
			LOGGER.debug(
					"Access token is not present in access token cache. Refreshing access token cache for QR Service");

			final List<NameValuePair> headers = new ArrayList<>();

			headers.add(new BasicNameValuePair(CommonConstants.AUTHORIZATION,
					EatonAuthUtil.encodeBasicAuthorization(apiKey, apiSecret)));
			headers.add(
					new BasicNameValuePair(CommonConstants.CONTENT_TYPE, CommonConstants.APPLICATION_JSON));

			final List<NameValuePair> params = new ArrayList<>();
			params.add(
					new BasicNameValuePair(AuthConstants.GRANT_TYPE_KEY, AuthConstants.GRANT_TYPE_CLIENT_CREDENTIALS));
			conMgr = getMultiThreadedConf();

			try {
				final HttpClient client = httpFactory.newBuilder()
						.setConnectionManager(conMgr)
						.build();

				final URI resourceUri = new URIBuilder(accessTokenUrl.concat(CommonConstants.ACCESS_TOKEN_RESOURCE_PATH))
						.addParameters(params).build();
				final HttpPost httpPost = new HttpPost(resourceUri);

				for (NameValuePair header : headers) {
					httpPost.setHeader(header.getName(), header.getValue());
				}

				final HttpResponse execute = client.execute(httpPost);

				final int statusCode = execute.getStatusLine().getStatusCode();
				final String statusLineofAPI = execute.getStatusLine().toString();
				final StringBuilder responseofAPI = new StringBuilder();

				if (statusCode == HttpStatus.SC_OK) {
					accessToken = bufferAccessToken(execute);
				}

				LOGGER.debug("Status :: {}", statusLineofAPI);
				LOGGER.debug("Response :: {}", responseofAPI);

			} catch (URISyntaxException | IOException e) {
				LOGGER.error("Exception while calling the getToken api for QR Service: {} ", e.getMessage());
			} finally {
				if (conMgr != null) {
					conMgr.closeExpiredConnections();
					conMgr.closeIdleConnections(50L, TimeUnit.SECONDS);
				}
			}
		}

		LOGGER.debug(ApigeeConstants.MSG_DEBUG_ACCESS_TOKEN, accessToken);
		LOGGER.debug("QRServiceImpl :: getAccessToken() :: Ended");
		return accessToken;
	}

	private String bufferAccessToken(HttpResponse execute){

		final StringBuilder response = new StringBuilder();
		String accessToken = null;

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(execute.getEntity().getContent(), StandardCharsets.UTF_8))) {
			String line;
			while (null != (line = bufferedReader.readLine())) {
				response.append(line);
			}
		} catch (IOException e) {
			LOGGER.error("Unable to read access token from api: ", e);
		}

		try {
			JSONObject responseJson = new JSONObject(response.toString());
			String accessTokenVal = responseJson.getString(AuthConstants.ACCESS_TOKEN_KEY);
			if (accessTokenVal != null && StringUtils.isNotEmpty(accessTokenVal)) {
				accessToken = accessTokenVal;
				LOGGER.debug(ApigeeConstants.MSG_DEBUG_ACCESS_TOKEN, accessToken);
				accessTokenCache.put(AuthConstants.ACCESS_TOKEN_KEY, accessToken);
			}
		} catch (JSONException e) {
			LOGGER.error("Unable to parse access token from api: ", e);
		}

		return accessToken;
	}

	/**
	 * Get the multi-threaded configuration parameters
	 *
	 * @return
	 */
	private PoolingHttpClientConnectionManager getMultiThreadedConf() {
		LOGGER.debug("QRServiceImpl :: getMultiThreadedConf() :: Started");

		if (conMgr == null) {
			conMgr = new PoolingHttpClientConnectionManager();
			conMgr.setMaxTotal(maxConnections);
			conMgr.setDefaultMaxPerRoute(maxHostConnections);
		}

		LOGGER.debug("QRServiceImpl :: getMultiThreadedConf() :: Ended");
		return conMgr;
	}

}
