/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.util;

import com.eaton.platform.integration.myeaton.services.MyEatonServiceConfiguration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility Class to help with MyEaton services */
public final class MyEatonUtil {
    private static final Logger LOG = LoggerFactory.getLogger(MyEatonUtil.class);
    private MyEatonUtil () {
        LOG.debug("Inside MyEatonUtil constructor");
    }

    /** Builds base64 encoded Basic Authorization string for HTTP Headers
     * @param username Username for Authorization
     * @param password Password for Authorization
     * @return Properly formatted Basic Authorization String
     */
    public static String encodeBasicAuthorization(String username, String password) {
        String joinedAuth = String.format("%s:%s", username, password);
        String encodedCreds = Base64.getEncoder()
            .encodeToString(joinedAuth.getBytes(StandardCharsets.UTF_8));

        return String.format("%s %s", "Basic", encodedCreds);
    }

    /** Make a basic HTTP Call using the GET method
     * @param endpointUrl URL to call
     * @param conMgr Pooling Connection to use for the call
     * @param httpFactory HttpClientBuilderFactory to use for the call
     * @param serviceConfiguration configuration for services
     * @return The response body
     */
    public static String callBasicHttpGet(
        String endpointUrl,
        PoolingHttpClientConnectionManager conMgr,
        HttpClientBuilderFactory httpFactory,
        MyEatonServiceConfiguration serviceConfiguration) {
        StringBuilder responseContent = new StringBuilder();

        try {
            final HttpClient client = httpFactory.newBuilder()
                .setConnectionManager(MyEatonUtil.getMultiThreadedConf(conMgr, serviceConfiguration))
                .build();

            final URIBuilder uriBuilder = new URIBuilder(endpointUrl);
            final URI build = uriBuilder.build();
            final HttpGet httpGet = new HttpGet(build);

            httpGet.setHeader("Authorization", MyEatonUtil.encodeBasicAuthorization(
                serviceConfiguration.getUsername(), serviceConfiguration.getPassword()));

            final HttpResponse execute = client.execute(httpGet);
            final String reasonPhrase = execute.getStatusLine().getReasonPhrase();

            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(execute.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    String line;
                    while (null != (line = bufferedReader.readLine())) {
                        responseContent.append(line);
                    }
                }
            }
            LOG.debug("reasonPhrase: {}", reasonPhrase);
        } catch (URISyntaxException e) {
            LOG.error("URISyntaxException while calling the api: {}", endpointUrl, e);
        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException while calling the api: {}", endpointUrl, e);
        } catch (IOException e) {
            LOG.error("IOException while calling the api: {}", endpointUrl, e);
        }

        LOG.info("End with callBasicHttpGet method Response :: {}", responseContent);
        return responseContent.toString();
    }

    /** Build a pooling http client connection manager for use with http calls
     * @param conMgr Connection manager to use
     * @param serviceConfiguration Configuration for services
     * @return Configured pooling http client connection manager
     */
    public static PoolingHttpClientConnectionManager getMultiThreadedConf(
        PoolingHttpClientConnectionManager conMgr, MyEatonServiceConfiguration serviceConfiguration) {

        if (conMgr == null) {
            conMgr = new PoolingHttpClientConnectionManager();
            conMgr.setMaxTotal(serviceConfiguration.getMaxConnections());
            conMgr.setDefaultMaxPerRoute(serviceConfiguration.getMaxHostConnections());
        }
        return conMgr;
    }

    /** Checks if a value does not exist within an array of Strings
     * @param value Value to look for
     * @param valueArray String Array to check against
     * @return true if the value does not exist
     */
    public static boolean valueExcludedFromArray(String value, String[] valueArray) {
        boolean excluded = true;
        for (String s : valueArray) {
            if (value.equals(s)) {
                excluded = false;
                break;
            }
        }

        return excluded;
    }
}
