package com.eaton.platform.integration.apigee.services.impl;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.apigee.constants.ApigeeConstants;
import com.eaton.platform.integration.apigee.services.ApigeeService;
import com.eaton.platform.integration.apigee.services.config.ApigeeServiceConfig;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.util.EatonAuthUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component(
    service = ApigeeService.class,
    immediate = true,
    property = {
            AEMConstants.SERVICE_VENDOR_EATON,
            AEMConstants.SERVICE_DESCRIPTION + "ApigeeServiceImpl",
            AEMConstants.PROCESS_LABEL + "ApigeeServiceImpl"
    }
)
/**
 * ApigeeServiceImpl class.
 */

public class ApigeeServiceImpl implements ApigeeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApigeeServiceImpl.class);
    @Reference
    private HttpClientBuilderFactory httpFactory;
    @Reference
    private CryptoSupport cryptoSupport;
    private PoolingHttpClientConnectionManager conMgr;
    private int maxConnections;
    private int maxHostConnections;
    private String baseUrl = StringUtils.EMPTY;
    private String key = StringUtils.EMPTY;
    private String secret = StringUtils.EMPTY;
    private String freeSampleOrderKey = StringUtils.EMPTY;
    private String freeSampleOrderSecret = StringUtils.EMPTY;
    private Cache<String, String> accessTokenCache;
    private Cache<URI, String> responseCache;
    private int responseTimeout;
    /**
     * This is a For Time
     */
    public final long FIFTY = 50L;


    @Activate
    @Modified
    protected final void activate(final ApigeeServiceConfig config) {
        LOGGER.debug("ApigeeServiceImpl :: activate() :: Started");

        if (null != config) {
            baseUrl = config.baseUrl();
            key = config.key();
            secret = config.secret();
            freeSampleOrderKey = config.freeSampleOrderKey();
            freeSampleOrderSecret = config.freeSampleOrderSecret();
            responseTimeout = config.responseTimeout();
            maxConnections = config.maxConnections();
            maxHostConnections = config.maxHostConnections();

            accessTokenCache = Caffeine.newBuilder()
                    .maximumSize(config.accessTokenCacheSize())
                    .expireAfterWrite(config.accessTokenCacheDuration(), TimeUnit.MINUTES)
                    .build();

            responseCache = Caffeine.newBuilder()
                    .maximumSize(config.responseCacheSize())
                    .expireAfterWrite(config.responseCacheDuration(), TimeUnit.HOURS)
                    .build();
        }

        LOGGER.debug("ApigeeServiceImpl :: activate() :: Ended");

    }
    
    @Override
    public int responseTimeout() {
        return responseTimeout;
    }
    
    /**
     * Get api response from apigee
     *
     * @param endpoint
     * @param params
     * @return response
     */
    @Override
    public String getData(final String endpoint, final List<NameValuePair> params, String processFlow) {
        LOGGER.debug("ApigeeServiceImpl :: getData() :: Started");

        String response = null;
        URI resourceUri = null;

        try {

            resourceUri = new URIBuilder(StringUtils.join(baseUrl, endpoint)).addParameters(params).build();
            
            LOGGER.debug("resource uri: {}", resourceUri);

            response = responseCache.getIfPresent(resourceUri);

        } catch (URISyntaxException e){
            LOGGER.error("URISyntaxException: {}", e.getMessage());
        }

        if (
                ( response == null || StringUtils.isEmpty(response) )
                        && ( resourceUri != null )

        ) {

            String accessToken = getAccessToken();
            conMgr = getMultiThreadedConf();

            try {
                if (StringUtils.isNotEmpty(accessToken)) {
                    final HttpClient httpClient = httpFactory.newBuilder().setConnectionManager(conMgr).build();
                    
                    final List<NameValuePair> headers = new ArrayList<>();
                    headers.add(new BasicNameValuePair(AuthConstants.AUTHORIZATION_HEADER_KEY, StringUtils.join(AuthConstants.BEARER_SPACE, accessToken)));
                    headers.add(new BasicNameValuePair(CommonConstants.ACCEPT, CommonConstants.APPLICATION_JSON));
                    if (processFlow != null) {
                        headers.add(new BasicNameValuePair(ApigeeConstants.X_PROCESS_FLOW, processFlow));
                        }
                    response = getResponse(httpClient, resourceUri, headers);
                }
            } finally {
                if (conMgr != null) {
                    conMgr.closeExpiredConnections();
                    conMgr.closeIdleConnections(FIFTY, TimeUnit.SECONDS);
                }
            }
            
        } else {
            LOGGER.error("resourceUri is null");
        }
        
        LOGGER.debug(ApigeeConstants.MSG_DEBUG_RESPONSE, response);
        LOGGER.debug("ApigeeServiceImpl :: getData() :: Ended");
        return response;
    }

    /**
     * Get cached access token if valid still. If not, refresh the access token's caches.
     *
     * @return accessToken
     */
    private String getAccessToken() {
        LOGGER.debug("ApigeeServiceImpl :: getAccessToken() :: Started");

        String accessToken = accessTokenCache.getIfPresent(AuthConstants.ACCESS_TOKEN_KEY);

        if (accessToken == null || StringUtils.isEmpty(accessToken)) {
            LOGGER.debug("Access token is not present in access token cache. Refreshing access token cache.");

            final List<NameValuePair> headers = new ArrayList<>();

            if(cryptoSupport.isProtected(secret)){
                try {
                    secret = cryptoSupport.unprotect(secret);
                } catch (CryptoException e) {
                    LOGGER.error("Encryption of api secret failed.", e.getMessage());
                }
            }
            
            headers.add(new BasicNameValuePair(CommonConstants.AUTHORIZATION, EatonAuthUtil.encodeBasicAuthorization(key, secret)));
            headers.add(new BasicNameValuePair(CommonConstants.CONTENT_TYPE, CommonConstants.APPLICATION_FORM_URLENCODED));

            final List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(AuthConstants.GRANT_TYPE_KEY, AuthConstants.GRANT_TYPE_CLIENT_CREDENTIALS));
            conMgr = getMultiThreadedConf();

            try {
                final HttpClient client = httpFactory.newBuilder()
                        .setConnectionManager(conMgr)
                        .build();

                final URI resourceUri = new URIBuilder(baseUrl.concat(CommonConstants.OAUTH_TOKEN_RESOURCE_PATH)).addParameters(params).build();
                final HttpPost httpPost = new HttpPost(resourceUri);

                for (NameValuePair header : headers) {
                    httpPost.setHeader(header.getName(), header.getValue());
                }

                final HttpResponse execute = client.execute(httpPost);
                final int statusCode = execute.getStatusLine().getStatusCode();
                final String statusLine = execute.getStatusLine().toString();
                final StringBuilder response = new StringBuilder();

                if (statusCode == HttpStatus.SC_OK) {
                    accessToken = bufferAccessToken(execute);
                }

                LOGGER.debug("Status :: {}", statusLine);
                LOGGER.debug("Response :: {}", response);

            } catch (URISyntaxException | IOException e) {
                LOGGER.error("Exception while calling the getToken api: ", e);
            } finally {
                if (conMgr != null) {
                    conMgr.closeExpiredConnections();
                    conMgr.closeIdleConnections(FIFTY, TimeUnit.SECONDS);
                }
            }
        }
        
        LOGGER.debug(ApigeeConstants.MSG_DEBUG_ACCESS_TOKEN, accessToken);
        LOGGER.debug("ApigeeServiceImpl :: getAccessToken() :: Ended");
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
     * Get request with headers and params
     *
     * @param httpClient
     * @param resourceUri
     * @param headers - if none, send empty Headers array
     * @return response
     */
    private String getResponse(final HttpClient httpClient, URI resourceUri, final List<NameValuePair> headers) {

        LOGGER.debug("ApigeeServiceImpl :: getResponse() :: Started");

        String response = null;
        
        try {

            final HttpGet httpGet = new HttpGet(resourceUri);

            if (!headers.isEmpty()) {
                for (NameValuePair header : headers) {
                    httpGet.setHeader(header.getName(), header.getValue());
                }
                LOGGER.debug("headers set");
            }

            final HttpResponse execute = httpClient.execute(httpGet);
            response = bufferResponse(execute, resourceUri);

        } catch (ClientProtocolException e) {
            LOGGER.error("ClientProtocolException while calling the api", e);
        } catch (IOException e) {
            LOGGER.error("IOException while calling the api", e);
        }

        LOGGER.debug("ApigeeServiceImpl :: getResponse() :: Ended");
        return response;
    }

    private String bufferResponse(HttpResponse execute, URI resourceUri) {

        String response = null;
        final int statusCode = execute.getStatusLine().getStatusCode();
        final String statusLine = execute.getStatusLine().toString();
        LOGGER.debug("status: {}", statusLine);

        final StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(execute.getEntity().getContent(), StandardCharsets.UTF_8))) {
            String line;
            while (null != (line = bufferedReader.readLine())) {
                responseBuilder.append(line);
            }

            response = responseBuilder.toString();

            if (statusCode == HttpStatus.SC_OK) {
                responseCache.put(resourceUri, response);
            }

        } catch (IOException e) {
            LOGGER.error("Unable to read data from api response{}", e.getMessage());
        }

        LOGGER.debug("Status of status line :: {}", statusLine);
        LOGGER.debug("Response of buffer responce :: {}", response);
        return response;
    }

    /**
     * Get api response from apigee
     *
     * @param endpoint
     * @param params
     * @return response
     */
    @Override
    public String getFreeSampleOrders(String endpoint, final List<NameValuePair> params) {
        LOGGER.debug("ApigeeServiceImpl :: getData() :: Started");
        String response = null;
        URI resourceUri = null;

        try {

            resourceUri = new URIBuilder(StringUtils.join(baseUrl, endpoint)).addParameters(params).build();

            LOGGER.debug("resource uri: {}", resourceUri);

        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException: {}", e.getMessage());
        }

        if (resourceUri != null) {

            String accessToken = getAccessTokenForSampleOrdersAPI();
            conMgr = getMultiThreadedConf();

            try {
                if (StringUtils.isNotEmpty(accessToken)) {
                    final HttpClient httpClient = httpFactory.newBuilder().setConnectionManager(conMgr).build();

                    final List<NameValuePair> headers = new ArrayList<>();
                    headers.add(new BasicNameValuePair(AuthConstants.AUTHORIZATION_HEADER_KEY,
                            StringUtils.join(AuthConstants.BEARER_SPACE, accessToken)));
                    headers.add(new BasicNameValuePair(CommonConstants.ACCEPT, CommonConstants.APPLICATION_JSON));

                    response = getResponse(httpClient, resourceUri, headers);
                }
            } finally {
                if (conMgr != null) {
                    conMgr.closeExpiredConnections();
                    conMgr.closeIdleConnections(FIFTY, TimeUnit.SECONDS);
                }
            }

        } else {
            LOGGER.error("resourceUri is null");
        }

        LOGGER.debug(ApigeeConstants.MSG_DEBUG_RESPONSE, response);
        LOGGER.debug("ApigeeServiceImpl :: getData() :: Ended");
        return response;
    }

    /**
     * Get cached access token if valid still. If not, refresh the access token's
     * caches.
     *
     * @return accessToken
     */
    private String getAccessTokenForSampleOrdersAPI() {
        LOGGER.debug("ApigeeServiceImpl :: getAccessToken() :: Started :: for free sample ");

        String accessToken = accessTokenCache.getIfPresent(AuthConstants.ACCESS_TOKEN_KEY);

        if (accessToken == null || StringUtils.isEmpty(accessToken)) {
            LOGGER.debug(
                    "Access token is not present in access token cache. Refreshing access token cache for free samples");

            final List<NameValuePair> headers = new ArrayList<>();

            headers.add(new BasicNameValuePair(CommonConstants.AUTHORIZATION,
                    EatonAuthUtil.encodeBasicAuthorization(freeSampleOrderKey, freeSampleOrderSecret)));
            headers.add(
                    new BasicNameValuePair(CommonConstants.CONTENT_TYPE, CommonConstants.APPLICATION_FORM_URLENCODED));

            final List<NameValuePair> params = new ArrayList<>();
            params.add(
                    new BasicNameValuePair(AuthConstants.GRANT_TYPE_KEY, AuthConstants.GRANT_TYPE_CLIENT_CREDENTIALS));
            conMgr = getMultiThreadedConf();

            try {
                final HttpClient client = httpFactory.newBuilder()
                        .setConnectionManager(conMgr)
                        .build();

                final URI resourceUri = new URIBuilder(baseUrl.concat(CommonConstants.OAUTH_TOKEN_RESOURCE_PATH))
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
                LOGGER.error("Exception while calling the getToken api for free sample: ", e);
            } finally {
                if (conMgr != null) {
                    conMgr.closeExpiredConnections();
                    conMgr.closeIdleConnections(FIFTY, TimeUnit.SECONDS);
                }
            }
        }

        LOGGER.debug(ApigeeConstants.MSG_DEBUG_ACCESS_TOKEN, accessToken);
        LOGGER.debug("ApigeeServiceImpl :: getAccessToken() :: Ended:: for free sample");
        return accessToken;
    }

    /**
     * Get the multi-threaded configuration parameters
     *
     * @return
     */
    private PoolingHttpClientConnectionManager getMultiThreadedConf() {
        LOGGER.debug("ApigeeServiceImpl :: getMultiThreadedConf() :: Started");

        if (conMgr == null) {
            conMgr = new PoolingHttpClientConnectionManager();
            conMgr.setMaxTotal(maxConnections);
            conMgr.setDefaultMaxPerRoute(maxHostConnections);
        }

        LOGGER.debug("ApigeeServiceImpl :: getMultiThreadedConf() :: Ended");
        return conMgr;
    }

}
