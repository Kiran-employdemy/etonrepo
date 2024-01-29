package com.eaton.platform.integration.google.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.google.services.GoogleService;
import com.eaton.platform.integration.google.services.config.GoogleServiceConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component(
        service = GoogleService.class, 
        immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "GoogleServiceImpl",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "GoogleServiceImpl"
        })

public class GoogleServiceImpl implements GoogleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleServiceImpl.class);

    @Reference
    private HttpClientBuilderFactory httpFactory;

    private PoolingHttpClientConnectionManager conMgr;
    private int maxConnections;
    private int maxHostConnections;

    private String baseUrl = StringUtils.EMPTY;
    private String key = StringUtils.EMPTY;
    private Cache<URI, String> responseCache;
    private int responseTimeout = responseTimeout();


    @Activate
    @Modified
    protected void activate(final GoogleServiceConfig config) {
        LOGGER.debug("GoogleServiceImpl :: activate() :: Started");

        if (null != config) {
            baseUrl = config.baseUrl();
            key = config.key();
            maxConnections = config.maxConnections();
            maxHostConnections = config.maxHostConnections();
            responseTimeout = config.responseTimeout();
            
            responseCache = Caffeine.newBuilder()
                    .maximumSize(config.responseCacheSize())
                    .expireAfterWrite(config.responseCacheDuration(), TimeUnit.HOURS)
                    .build();
        }

        LOGGER.debug("GoogleServiceImpl :: activate() :: Ended");

    }
    
    @Override
    public int responseTimeout() {
        return responseTimeout;
    }

    /**
     * Get api response from google api
     *
     * @param endpoint
     * @param headers - if none, pass empty list
     * @param params - if none, pass empty list
     * @return response
     */
    @Override
    public String getResponse(final String endpoint, final List<NameValuePair> headers, final List<NameValuePair> params) {
        
        LOGGER.debug("GoogleServiceImpl :: getResponse() :: Started");
        
        String response = null;
        URI resourceUri = null;

        params.add(new BasicNameValuePair(CommonConstants.KEY, key));

        try {

            resourceUri = new URIBuilder(StringUtils.join(baseUrl, endpoint))
                    .addParameters(params)
                    .build();

            response = responseCache.getIfPresent(resourceUri);

        } catch (URISyntaxException | NullPointerException e){
            LOGGER.error("Exception while creating uri or getting uri from cache: ", e);
        }


        if ( ( response == null || StringUtils.isEmpty(response) )
                        && ( resourceUri != null ) ) {

            try {

                final HttpClient httpClient = httpFactory.newBuilder().setConnectionManager(getMultiThreadedConf()).build();
                final HttpGet httpGet = new HttpGet(resourceUri);
                
                if (!headers.isEmpty()) {
                    for (NameValuePair header : headers) {
                        httpGet.setHeader(header.getName(), header.getValue());
                    }
                    LOGGER.debug("headers set");
                }
                
                final HttpResponse execute = httpClient.execute(httpGet);
                response = bufferResponse(execute, resourceUri);
    
            } catch (IOException e) {
                LOGGER.error("Exception while calling the api: ", e);
            }
        }

        LOGGER.debug("GoogleServiceImpl :: getResponse() :: Ended");
        return response;
    }
    
    private String bufferResponse(HttpResponse execute, URI resourceUri) {
        
        String response = null;
        final int statusCode = execute.getStatusLine().getStatusCode();
        final String statusLine = execute.getStatusLine().toString();

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
            LOGGER.error("Unable to read data from api response");
        }

        LOGGER.debug("Status :: {}", statusLine);
        LOGGER.debug("Response :: {}", response);
        
        return response;
    }

    /**
     * Get the multi-threaded configuration parameters
     *
     * @return
     */
    private PoolingHttpClientConnectionManager getMultiThreadedConf() {
        LOGGER.debug("GoogleServiceImpl :: getMultiThreadedConf() :: Started");

        if (conMgr == null) {
            conMgr = new PoolingHttpClientConnectionManager();
            conMgr.setMaxTotal(maxConnections);
            conMgr.setDefaultMaxPerRoute(maxHostConnections);
        }

        LOGGER.debug("GoogleServiceImpl :: getMultiThreadedConf() :: Ended");
        return conMgr;
    }
}