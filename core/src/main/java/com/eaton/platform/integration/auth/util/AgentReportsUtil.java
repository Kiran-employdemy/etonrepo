package com.eaton.platform.integration.auth.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.bean.secure.AgentBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.secure.AgentReportsServiceConfiguration;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Utility Class to help with Agent Reports services */
public final class AgentReportsUtil {
	private static final Logger LOG = LoggerFactory.getLogger(AgentReportsUtil.class);

	private AgentReportsUtil() {
		LOG.debug("Inside AgentReportsUtil constructor");
	}

	public static Map<String, String> getAccessTokenAPI(PoolingHttpClientConnectionManager conMgr,
			HttpClientBuilderFactory httpFactory, AgentReportsServiceConfiguration serviceConfiguration) {

		LOG.debug("AgentReportsUtil : getAccessTokenAPI : START");
		StringBuilder responseContent = new StringBuilder();
		String endpointUrl = serviceConfiguration.getUserLookupUrl();
		LOG.debug("endpointUrl: {}", endpointUrl);
		Map<String, String> arAccessTokenMap = new HashMap<String, String>();
		try {
			final HttpClient client = httpFactory.newBuilder()
					.setConnectionManager(AgentReportsUtil.getMultiThreadedConf(conMgr, serviceConfiguration)).build();
			
			final URIBuilder uriBuilder = new URIBuilder(endpointUrl);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(SecureConstants.GRANT_TYPE_KEY, SecureConstants.PASSWORD));
			nameValuePairs.add(new BasicNameValuePair(SecureConstants.LOGIN, serviceConfiguration.getUserLookupAPIUsername()));
			nameValuePairs.add(new BasicNameValuePair(SecureConstants.PASSWORD, serviceConfiguration.getUserLookupAPIPassword()));

			uriBuilder.addParameters(nameValuePairs);

			final URI build = uriBuilder.build();
			final HttpPost httpPost = new HttpPost(build);

			httpPost.setHeader(CommonConstants.CONTENT_TYPE, CommonConstants.APPLICATION_FORM_URLENCODED);
			httpPost.setHeader(CommonConstants.ACCEPT, CommonConstants.APPLICATION_JSON);
			httpPost.setHeader(CommonConstants.AUTHORIZATION, EatonAuthUtil.encodeBasicAuthorization(
					serviceConfiguration.getUserLookupAPIUsername(), serviceConfiguration.getUserLookupAPIPassword()));
			
			LOG.debug("getToken api post request: {}", httpPost);

			final HttpResponse execute = client.execute(httpPost);

			Header[] respHeaders = execute.getHeaders(SecureConstants.SET_COOKIE);
			for (Header resHeader : respHeaders) {
				String cookieHeader = resHeader.getValue();
				LOG.debug("cookieHeader: {}", cookieHeader);
				String[] cookieHeaders = cookieHeader.split(";");
				for (String cookie : cookieHeaders) {
					if (cookie.contains(SecureConstants.JSESSIONID)) {
						LOG.debug("cookie: {}", cookie);
						arAccessTokenMap.put(SecureConstants.COOKIE, cookie);
						LOG.debug("Cookie added to arAccessTokenMap");
						break;
					}
				}

			}
			final String reasonPhrase = execute.getStatusLine().getReasonPhrase();
			LOG.debug("reasonPhrase: {}", reasonPhrase);
			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(execute.getEntity().getContent(), StandardCharsets.UTF_8))) {
				String line;
				while (null != (line = bufferedReader.readLine())) {
					responseContent.append(line);
				}
			}
			
			JSONObject responseJSON = new JSONObject(responseContent.toString());
			LOG.debug("responseJSON: {}", responseJSON);
			final String accessToken = responseJSON.getString(AuthConstants.ACCESS_TOKEN_KEY);
			if (accessToken != null) {
				LOG.debug("access token key found in responseJSON: {}", accessToken);
				arAccessTokenMap.put(SecureConstants.ACCESS_TOKEN, accessToken);
				LOG.debug("access token key added to arAccessTokenMap");
			}

		} catch (URISyntaxException ue) {
			LOG.error("URISyntaxException while calling the getToken api: {}", endpointUrl, ue);
		} catch (ClientProtocolException ce) {
			LOG.error("ClientProtocolException while calling the getToken api: {}", endpointUrl, ce);
		} catch (IOException ie) {
			LOG.error("IOException while calling the getToken api: {}", endpointUrl, ie);
		} catch (Exception e) {
			LOG.error("Exception while calling the getToken api: {}", endpointUrl, e);
		}

		
		LOG.debug("AgentReportsUtil : getAccessTokenAPI : END");

		return arAccessTokenMap;
	}

	/**
	 * Method to get the Agent User details
	 * @param conMgr PoolingHttpClientConnectionManager
	 * @param httpFactory HttpClientBuilderFactory
	 * @param serviceConfiguration AgentReportsServiceConfiguration
	 * @param email String
	 * @param arAccessTokenMap Map<String, String>
	 * @return jsonArray JsonArray
	 */
	public static JsonArray getUserInfo(PoolingHttpClientConnectionManager conMgr,
			   HttpClientBuilderFactory httpFactory, AgentReportsServiceConfiguration serviceConfiguration, String email, Map<String, String> arAccessTokenMap){

		LOG.debug("AgentReportsUtil :: getUserInfo :: START");
		final StringBuilder responseBody = new StringBuilder();
		String endPointUrl = serviceConfiguration.getUserInfoAPIEndPointURL();
		LOG.debug("Endpoint url for getUserInfo API call :: {}", endPointUrl);
		LOG.debug("email for getUserInfo API call :: {}", email);
		JsonArray jsonArray = null;
		try {
			final HttpClient client = httpFactory.newBuilder()
					.setConnectionManager(AgentReportsUtil.getMultiThreadedConf(conMgr, serviceConfiguration)).build();
			final URIBuilder uriBuilder = new URIBuilder(endPointUrl + email);
			final URI build = uriBuilder.build();
			final HttpGet httpGet = new HttpGet(build);
			httpGet.setHeader(CommonConstants.AUTHORIZATION, "Bearer " + arAccessTokenMap.get(SecureConstants.ACCESS_TOKEN));
			httpGet.setHeader("Cookie", arAccessTokenMap.get(SecureConstants.COOKIE));
			
			LOG.debug("Agent reports :: getUserInfo request :: {}", httpGet);

			final HttpResponse httpResponse = client.execute(httpGet);
			final String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
			LOG.debug("Reason Phrase received from getUserInfo API call :: {}", reasonPhrase);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8));
				String line;
				while (null != (line = bufferedReader.readLine())) {
					responseBody.append(line);
				}
				bufferedReader.close();
			} else {
				LOG.debug("getUserInfo API request returned the following status: {}", reasonPhrase);
			}
			if (responseBody != null) {
				JsonObject jsonObject = new JsonParser().parse(responseBody.toString()).getAsJsonObject();
				if (jsonObject.has("access")) {
					jsonArray = jsonObject.get("access").getAsJsonArray();
					LOG.debug("getUserInfo response : access jsonArray : {}", jsonArray);
				}
			}
		} catch (URISyntaxException ue) {
			LOG.error("URISyntaxException while calling the getUserInfo api: {}", endPointUrl, ue);
		} catch (IOException ie) {
			LOG.error("IOException while calling the getUserInfo api: {}", endPointUrl, ie);
		}
		
		LOG.debug("AgentReportsUtil :: getUserInfo :: END");
		return jsonArray;
	}

	/**
	 * Method to get the Org ID details
	 * @param agentId String
	 * @param conMgr PoolingHttpClientConnectionManager
	 * @param httpFactory HttpClientBuilderFactory
	 * @param serviceConfiguration AgentReportsServiceConfiguration
	 * @param arAccessTokenMap Map<String, String>
	 * @return jsonObject JsonObject
	 */
	public static JsonObject getOrgInfo(String agentId, PoolingHttpClientConnectionManager conMgr,
			HttpClientBuilderFactory httpFactory, AgentReportsServiceConfiguration serviceConfiguration, Map<String, String> arAccessTokenMap) {

		LOG.debug("Start with getOrgInfo API method :: AgentReportsUtil");
		final StringBuilder responseBody = new StringBuilder();
		StringBuilder responseContent = new StringBuilder();
		JsonObject jsonObject = new JsonObject();
		String endPointUrl = serviceConfiguration.getOrgInfoAPIEndPointURL();
		try {
			final HttpClient client = httpFactory.newBuilder()
					.setConnectionManager(AgentReportsUtil.getMultiThreadedConf(conMgr, serviceConfiguration)).build();

			final URIBuilder uriBuilder = new URIBuilder(endPointUrl + agentId);
			final URI build = uriBuilder.build();
			final HttpGet httpGet = new HttpGet(build);
			httpGet.setHeader(CommonConstants.AUTHORIZATION, "Bearer " + arAccessTokenMap.get(SecureConstants.ACCESS_TOKEN));
			httpGet.setHeader(SecureConstants.COOKIE, arAccessTokenMap.get(SecureConstants.COOKIE));

			final HttpResponse httpResponse = client.execute(httpGet);
			final String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
			LOG.debug("Reason Phrase received from getUserInfo API call :: {}", reasonPhrase);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8));
				String line;
				while (null != (line = bufferedReader.readLine())) {
					responseBody.append(line);
				}
				bufferedReader.close();
				jsonObject = new JsonParser().parse(responseBody.toString()).getAsJsonObject();
			} else {
				LOG.debug("API request returned the following status: {}", reasonPhrase);
			}
			LOG.debug("Agent reports :: OrgInfo reponse :: {}", jsonObject.toString());

		} catch (URISyntaxException ue) {
			LOG.error("URISyntaxException while calling the getOrgInfo api: {}", endPointUrl, ue);
		} catch (ClientProtocolException ce) {
			LOG.error("ClientProtocolException while calling the getOrgInfo api: {}", endPointUrl, ce);
		} catch (IOException ie) {
			LOG.error("IOException while calling the getOrgInfo api: {}", endPointUrl, ie);
		} catch (Exception e) {
			LOG.error("Exception while calling the getOrgInfo api: {}", endPointUrl, e);
		}

		LOG.info("End with getOrgInfo HttpGet method Response :: {}", responseContent);
		return jsonObject;
	}

	/**
	 * Build a pooling http client connection manager for use with http calls
	 * 
	 * @param conMgr               Connection manager to use
	 * @param serviceConfiguration Configuration for services
	 * @return Configured pooling http client connection manager
	 */
	public static PoolingHttpClientConnectionManager getMultiThreadedConf(PoolingHttpClientConnectionManager conMgr,
			AgentReportsServiceConfiguration serviceConfiguration) {
		LOG.debug("Start with getMultiThreadedConf method :: AgentReportsUtil");
		if (conMgr == null) {
			conMgr = new PoolingHttpClientConnectionManager();
			conMgr.setMaxTotal(serviceConfiguration.getMaxConnections());
			conMgr.setDefaultMaxPerRoute(serviceConfiguration.getMaxHostConnections());
		}
		LOG.debug("End with getMultiThreadedConf method :: AgentReportsUtil");
		return conMgr;
	}

	/**
	 * To get User email id from authtoken
	 *
	 * @param authenticationToken AuthenticationToken
	 * @return email String
	 */
	public static String getUserEmail(AuthenticationToken authenticationToken){
		if (authenticationToken != null) {
			UserProfile userProfile = authenticationToken.getUserProfile();
			if (userProfile != null && StringUtils.isNotEmpty(userProfile.getEmail())) {
				return userProfile.getEmail();
			}
		}
		return StringUtils.EMPTY;
	}

}
