/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.auth.util;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.models.UserProfileResponse;
import com.eaton.platform.integration.auth.models.impl.UserProfileImpl;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


/**
 * Utility Class to help with Eaton Auth services
 */
public final class EatonAuthUtil {
    private static final Logger LOG = LoggerFactory.getLogger(EatonAuthUtil.class);

    private EatonAuthUtil() {
        LOG.debug("Inside EatonAuthUtil constructor");
    }

    /**
     * Builds base64 encoded Basic Authorization string for HTTP Headers
     *
     * @param password Password for Authorization
     * @return Properly formatted Basic Authorization String
     */
    public static String encodeBasicAuthorization(String username, String password) {
        String joinedAuth = String.format("%s:%s", username, password);
        String encodedCreds = Base64.getEncoder()
                .encodeToString(joinedAuth.getBytes(StandardCharsets.UTF_8));

        return String.format("%s %s", "Basic", encodedCreds);
    }

    /**
     * Make a basic HTTP Call using the GET method
     *
     * @param endpointUrl          URL to call
     * @param conMgr               Pooling Connection to use for the call
     * @param httpFactory          HttpClientBuilderFactory to use for the call
     * @param serviceConfiguration configuration for services
     * @return The response body
     */
    public static UserProfile callUserProfileAPI(
            String endpointUrl,
            PoolingHttpClientConnectionManager conMgr,
            HttpClientBuilderFactory httpFactory,
            AuthenticationServiceConfiguration serviceConfiguration,
            String id) {

        LOG.debug("Start with callUserProfileAPI method :: EatonAuthUtil");
        StringBuilder responseContent = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        UserProfile userProfile = null;
        UserProfileResponse userProfileResponse = null;

        try {
            final HttpClient client = httpFactory.newBuilder()
                    .setConnectionManager(EatonAuthUtil.getMultiThreadedConf(conMgr, serviceConfiguration))
                    .build();

            LOG.debug("Endpoint url for getToken API call :: {}", endpointUrl);

            final URIBuilder uriBuilder = new URIBuilder(endpointUrl + "/" + id);
            final URI build = uriBuilder.build();
            final HttpGet httpGet = new HttpGet(build);

            httpGet.setHeader("Authorization", EatonAuthUtil.encodeBasicAuthorization(serviceConfiguration.getUserLookupAPIUsername(), serviceConfiguration.getUserLookupAPIPassword()));

            final HttpResponse execute = client.execute(httpGet);
            final String reasonPhrase = execute.getStatusLine().getReasonPhrase();
            LOG.debug("Reason Phrase received from UserProfile API call :: {}", reasonPhrase);

            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                LOG.debug("Valid Response received from UserProfile API call");
                userProfileResponse = objectMapper.readValue(execute.getEntity().getContent(), UserProfileResponse.class);
                if (userProfileResponse != null) {
                    LOG.debug("Response successfully mapped to UserProfileResponse object");
                    userProfile = EatonAuthUtil.getUserProfile(userProfileResponse);
                    LOG.debug("Response successfully mapped to UserProfile object");
                } else {
                    LOG.error("Response not mapped to UserProfileResponse object");
                }
            } else {
                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(execute.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    String line;
                    while (null != (line = bufferedReader.readLine())) {
                        responseContent.append(line);
                    }
                }
                LOG.error("Invalid Response received from UserProfile API call :: {}", responseContent.toString());
            }
        } catch (URISyntaxException ue) {
            LOG.error("URISyntaxException while calling the UserProfile api: {}", endpointUrl, ue);
        } catch (ClientProtocolException ce) {
            LOG.error("ClientProtocolException while calling the UserProfile api: {}", endpointUrl, ce);
        } catch (IOException ie) {
            LOG.error("IOException while calling the UserProfile api: {}", endpointUrl, ie);
        } catch (Exception e) {
            LOG.error("Exception while calling the UserProfile api: {}", endpointUrl, e);
        }

        LOG.info("End with callBasicHttpGet method Response :: {}", responseContent);
        return userProfile;
    }


    /**
     * Make a basic HTTP Call using the GET method
     *
     * @param endpointUrl          URL to call
     * @param conMgr               Pooling Connection to use for the call
     * @param httpFactory          HttpClientBuilderFactory to use for the call
     * @param serviceConfiguration configuration for services
     *                             * @param inputRequest Password Change request as JSONObject
     * @return The response body
     */
    public static JSONObject changePassword(
            String endpointUrl,
            PoolingHttpClientConnectionManager conMgr,
            HttpClientBuilderFactory httpFactory,
            AuthenticationServiceConfiguration serviceConfiguration,
            JSONObject inputRequest) {

        LOG.debug("Start with changePassword method :: EatonAuthUtil");
        StringBuilder responseContent = new StringBuilder();
        JSONObject response = new JSONObject();

        try {
            final HttpClient client = httpFactory.newBuilder()
                    .setConnectionManager(EatonAuthUtil.getMultiThreadedConf(conMgr, serviceConfiguration))
                    .build();

            LOG.debug("Endpoint url for getToken API call :: {}", endpointUrl);

            final URIBuilder uriBuilder = new URIBuilder(endpointUrl);
            final URI build = uriBuilder.build();
            final HttpPut httpPut = new HttpPut(build);
            httpPut.setHeader(CommonConstants.AUTHORIZATION, EatonAuthUtil.encodeBasicAuthorization(serviceConfiguration.getUserLookupAPIUsername(), serviceConfiguration.getUserLookupAPIPassword()));

            StringEntity stringEntity = new StringEntity(inputRequest.toString());
            stringEntity.setContentType(CommonConstants.APPLICATION_JSON);
            httpPut.setEntity(stringEntity);
            final HttpResponse execute = client.execute(httpPut);
            final String responseBody = EntityUtils.toString(execute.getEntity());
            LOG.debug("Response received from Change  Password call :: {}", responseBody);

            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                LOG.debug("Valid Response received from Change Password API call");
                if ("SUCCESS".equals(responseBody)) {
                    response.put(AuthConstants.ATTR_RESPONSE_STATUS, HttpStatus.SC_OK);
                    response.put(AuthConstants.ATTR_RESPONSE_MESSAGE, responseBody);
                }
            } else if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                JSONArray responseArray = new JSONArray(responseBody);
                if (responseArray.get(0) != null) {
                    String message = responseArray.get(0).toString();
                    response.put(AuthConstants.ATTR_RESPONSE_STATUS, HttpStatus.SC_BAD_REQUEST);
                    response.put(AuthConstants.ATTR_RESPONSE_MESSAGE, message);
                }
                LOG.error("Invalid Response received from Change Password API call :: {}", responseContent.toString());
            } else {
                response.put(AuthConstants.ATTR_RESPONSE_STATUS, HttpStatus.SC_INTERNAL_SERVER_ERROR);
                response.put(AuthConstants.ATTR_RESPONSE_MESSAGE, "Internal Server Error");
                LOG.error("Server ERROR from Change Password API call :: {}", responseContent.toString());
            }
        } catch (URISyntaxException ue) {
            LOG.error("URISyntaxException while calling the UserProfile api: {}", endpointUrl, ue);
        } catch (ClientProtocolException ce) {
            LOG.error("ClientProtocolException while calling the UserProfile api: {}", endpointUrl, ce);
        } catch (IOException ie) {
            LOG.error("IOException while calling the UserProfile api: {}", endpointUrl, ie);
        } catch (Exception e) {
            LOG.error("Exception while calling the UserProfile api: {}", endpointUrl, e);
        }

        LOG.info("End with callBasicHttpGet method Response :: {}", responseContent);
        return response;
    }

    public static JSONObject trackDownload(String trackingLookupEndpoint, PoolingHttpClientConnectionManager conMgr,
                                           HttpClientBuilderFactory httpFactory, AuthenticationServiceConfiguration authenticationServiceConfiguration,
                                           JSONObject trackUserData) {
        LOG.debug("Start with trackDownload method :: EatonAuthUtil");
        StringBuilder responseContent = new StringBuilder();
        JSONObject response = new JSONObject();
        try {
            final HttpClient client = httpFactory.newBuilder()
                    .setConnectionManager(EatonAuthUtil.getMultiThreadedConf(conMgr, authenticationServiceConfiguration))
                    .build();

            LOG.debug("Endpoint url for getToken API call :: {}", trackingLookupEndpoint);

            final URIBuilder uriBuilder = new URIBuilder(trackingLookupEndpoint);
            final URI build = uriBuilder.build();
            final HttpPost httpPut = new HttpPost(build);

            httpPut.setHeader(CommonConstants.AUTHORIZATION, EatonAuthUtil.encodeBasicAuthorization(authenticationServiceConfiguration.getUserLookupAPIUsername(), authenticationServiceConfiguration.getUserLookupAPIPassword()));
            httpPut.setHeader("Content-Type", CommonConstants.APPLICATION_JSON);
            StringEntity stringEntity = new StringEntity(trackUserData.toString());

            stringEntity.setContentType(CommonConstants.APPLICATION_JSON);

            httpPut.setEntity(stringEntity);

            final HttpResponse execute = client.execute(httpPut);

            final String responseBody = EntityUtils.toString(execute.getEntity());
            LOG.debug("Response received from TrackDownload :: {}", responseBody);

            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                LOG.debug("Valid Response received from TrackDownload API call");
                if ("SUCCESS".equals(responseBody)) {
                    response.put(AuthConstants.ATTR_RESPONSE_STATUS, HttpStatus.SC_OK);
                    response.put(AuthConstants.ATTR_RESPONSE_MESSAGE, responseBody);
                }
            } else if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                JSONArray responseArray = new JSONArray(responseBody);
                if (responseArray.get(0) != null) {
                    String message = responseArray.get(0).toString();
                    response.put(AuthConstants.ATTR_RESPONSE_STATUS, HttpStatus.SC_BAD_REQUEST);
                    response.put(AuthConstants.ATTR_RESPONSE_MESSAGE, message);
                }
                LOG.error("Invalid Response received from TrackDownload API call :: {}", responseContent.toString());
            } else {
                response.put(AuthConstants.ATTR_RESPONSE_STATUS, HttpStatus.SC_INTERNAL_SERVER_ERROR);
                response.put(AuthConstants.ATTR_RESPONSE_MESSAGE, "Internal Server Error");
                LOG.error("Server ERROR from TrackDownload API call :: {}", responseContent.toString());
            }
        } catch (URISyntaxException ue) {
            LOG.error("URISyntaxException while calling the UserProfile api: {}", trackingLookupEndpoint, ue);
        } catch (ClientProtocolException ce) {
            LOG.error("ClientProtocolException while calling the UserProfile api: {}", trackingLookupEndpoint, ce);
        } catch (IOException ie) {
            LOG.error("IOException while calling the UserProfile api: {}", trackingLookupEndpoint, ie);
        } catch (Exception e) {
            LOG.error("Exception while calling the UserProfile api: {}", trackingLookupEndpoint, e);
        }
        return response;
    }

    /**
     * Make a basic HTTP Call using the PUT method
     *
     * @param endpointUrl            URL to call
     * @param conMgr                 Pooling Connection to use for the call
     * @param httpFactory            HttpClientBuilderFactory to use for the call
     * @param serviceConfiguration   configuration for services
     * @param userProfileInformation Update request as JSON object
     */
    public static void updateLastLogin(String endpointUrl,
                                       PoolingHttpClientConnectionManager conMgr,
                                       HttpClientBuilderFactory httpFactory,
                                       AuthenticationServiceConfiguration serviceConfiguration,
                                       JSONObject userProfileInformation) {
        LOG.debug("Start with updateLastLogin method :: EatonAuthUtil");

        try {
            final HttpClient client = httpFactory.newBuilder()
                    .setConnectionManager(EatonAuthUtil.getMultiThreadedConf(conMgr, serviceConfiguration))
                    .build();
            LOG.debug("Endpoint url for updateLastLogin API call :: {}", endpointUrl);
            final URIBuilder uriBuilder = new URIBuilder(endpointUrl);
            final URI build = uriBuilder.build();
            final HttpPut httpPut = new HttpPut(build);
            httpPut.setHeader(CommonConstants.AUTHORIZATION, EatonAuthUtil.encodeBasicAuthorization(serviceConfiguration.getUserLookupAPIUsername(), serviceConfiguration.getUserLookupAPIPassword()));
            StringEntity stringEntity = new StringEntity(userProfileInformation.toString(),CommonConstants.UTF_8);
            stringEntity.setContentType(CommonConstants.APPLICATION_JSON_UTF_8);
            httpPut.setEntity(stringEntity);
            final HttpResponse execute = client.execute(httpPut);
            final String responseBody = EntityUtils.toString(execute.getEntity());
            LOG.debug("Response received from update last login call :: {}", responseBody);

        } catch (URISyntaxException ue) {
            LOG.error("URISyntaxException while calling the Direct User Update API: {}", endpointUrl, ue);
        } catch (ClientProtocolException ce) {
            LOG.error("ClientProtocolException while calling the Direct User Update API: {}", endpointUrl, ce);
        } catch (IOException ie) {
            LOG.error("IOException while calling the Direct User Update API: {}", endpointUrl, ie);
        } catch (Exception e) {
            LOG.error("Exception while calling the Direct User Update API: {}", endpointUrl, e);
        }
    }

    /**
     * Build a pooling http client connection manager for use with http calls
     *
     * @param conMgr               Connection manager to use
     * @param serviceConfiguration Configuration for services
     * @return Configured pooling http client connection manager
     */
    public static PoolingHttpClientConnectionManager getMultiThreadedConf(
            PoolingHttpClientConnectionManager conMgr, AuthenticationServiceConfiguration serviceConfiguration) {

        if (conMgr == null) {
            conMgr = new PoolingHttpClientConnectionManager();
            conMgr.setMaxTotal(serviceConfiguration.getMaxConnections());
            conMgr.setDefaultMaxPerRoute(serviceConfiguration.getMaxHostConnections());
        }
        return conMgr;
    }

    /**
     * Checks if a value does not exist within an array of Strings
     *
     * @param value      Value to look for
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

    public static String callGetTokenAPI(
            String endpointUrl,
            PoolingHttpClientConnectionManager conMgr,
            HttpClientBuilderFactory httpFactory,
            AuthenticationServiceConfiguration serviceConfiguration,
            String authorizationCode) {

        LOG.debug("Start with callGetTokenAPI method :: EatonAuthUtil");
        StringBuilder responseContent = new StringBuilder();

        try {
            final HttpClient client = httpFactory.newBuilder()
                    .setConnectionManager(EatonAuthUtil.getMultiThreadedConf(conMgr, serviceConfiguration))
                    .build();

            LOG.debug("Endpoint url for getToken API call :: {}", endpointUrl);
            LOG.debug("Redirect url for getToken API call :: {}", serviceConfiguration.getOktaRedirectURI());

            final URIBuilder uriBuilder = new URIBuilder(endpointUrl);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(AuthConstants.GRANT_TYPE_KEY, AuthConstants.GRANT_TYPE_VALUE));
            nameValuePairs.add(new BasicNameValuePair(AuthConstants.REDIRECT_URI_KEY, serviceConfiguration.getOktaRedirectURI()));
            nameValuePairs.add(new BasicNameValuePair(AuthConstants.AUTHORIZATION_CODE_KEY, authorizationCode));

            uriBuilder.addParameters(nameValuePairs);

            final URI build = uriBuilder.build();
            final HttpPost httpPost = new HttpPost(build);

            httpPost.setHeader(CommonConstants.CONTENT_TYPE, CommonConstants.APPLICATION_FORM_URLENCODED);
            httpPost.setHeader(CommonConstants.ACCEPT, CommonConstants.APPLICATION_JSON);
            httpPost.setHeader(CommonConstants.AUTHORIZATION, EatonAuthUtil.encodeBasicAuthorization(serviceConfiguration.getOktaClientID(), serviceConfiguration.getOktaClientSecret()));

            final HttpResponse execute = client.execute(httpPost);
            final String reasonPhrase = execute.getStatusLine().getReasonPhrase();

            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(execute.getEntity().getContent(), StandardCharsets.UTF_8))) {
                String line;
                while (null != (line = bufferedReader.readLine())) {
                    responseContent.append(line);
                }
            }

            LOG.debug("Reason Phrase received from getToken API call :: {}", reasonPhrase);
            LOG.debug("Response received from getToken API call :: {}", responseContent.toString());

        } catch (URISyntaxException ue) {
            LOG.error("URISyntaxException while calling the getToken api: {}", endpointUrl, ue);
        } catch (ClientProtocolException ce) {
            LOG.error("ClientProtocolException while calling the getToken api: {}", endpointUrl, ce);
        } catch (IOException ie) {
            LOG.error("IOException while calling the getToken api: {}", endpointUrl, ie);
        } catch (Exception e) {
            LOG.error("Exception while calling the getToken api: {}", endpointUrl, e);
        }

        LOG.debug("End with callGetTokenAPI method :: EatonAuthUtil");
        return responseContent.toString();
    }

    public static InputStream getPublicKeysHttpCall(
            String endpointUrl,
            PoolingHttpClientConnectionManager conMgr,
            HttpClientBuilderFactory httpFactory,
            AuthenticationServiceConfiguration serviceConfiguration) {

        try {

            final HttpClient client = httpFactory.newBuilder()
                    .setConnectionManager(EatonAuthUtil.getMultiThreadedConf(conMgr, serviceConfiguration))
                    .build();

            final URIBuilder uriBuilder = new URIBuilder(endpointUrl);
            final URI build = uriBuilder.build();
            final HttpGet httpGet = new HttpGet(build);

            final HttpResponse execute = client.execute(httpGet);

            LOG.info("End with callBasicHttpGet method Response :: {}", execute.getStatusLine().getStatusCode());

            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return execute.getEntity().getContent();
            }

            return null;

        } catch (URISyntaxException e) {
            LOG.error("URISyntaxException while calling the api: {}", endpointUrl, e);
        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException while calling the api: {}", endpointUrl, e);
        } catch (IOException e) {
            LOG.error("IOException while calling the api: {}", endpointUrl, e);
        }

        return null;
    }

    public static UserProfile getUserProfile(UserProfileResponse userProfileResponse) {

        UserProfile userProfile = new UserProfileImpl();
        userProfile.setId(userProfileResponse.getUid());
        userProfile.setLastName(userProfileResponse.getSn());
        userProfile.setGivenName(userProfileResponse.getGivenname());
        userProfile.setEmail(userProfileResponse.getMail());
        userProfile.setCountrycode(userProfileResponse.getC());
        userProfile.setProductCategories(userProfileResponse.getEatonproductcategory());
        userProfile.setApplicationAccessTags(userProfileResponse.getNsrole());
        userProfile.setAccountTypes(userProfileResponse.getRoles());
        userProfile.setEatonPersonType(userProfileResponse.getEatonpersontype());
        userProfile.setCompanyName(userProfileResponse.getCn());
        userProfile.setEatonCustSite(userProfileResponse.getEatoncustsite());
        userProfile.setCompanyAddress(userProfileResponse.getStreet());
        userProfile.setCompanyZipCode(userProfileResponse.getPostalcode());
        userProfile.setCompanyBusinessPhone(userProfileResponse.getTelephonenumber());
        userProfile.setCompanyMobileNumber(userProfileResponse.getMobile() != null ? userProfileResponse.getMobile().toString() :
                StringUtils.EMPTY);
        userProfile.setUid(userProfileResponse.getUid());
        userProfile.setNsUniqueId(userProfileResponse.getNsUniqueId());
        userProfile.setEatonSupplierCompanyId(userProfileResponse.getEatonsuppliercompanyid() != null ?
                userProfileResponse.getEatonsuppliercompanyid().toString() : StringUtils.EMPTY);
        userProfile.setPartnerProgramAndTierLevels(userProfileResponse.getPartnerProgramAndTierLevels());
        userProfile.setEatonEshopEulaAcceptDate(userProfileResponse.getEatonEshopEulaAcceptDate());
        userProfile.setEatonEulaAcceptDate(userProfileResponse.getEatonEulaAcceptDate());
        userProfile.setEatonIccVistaUid(userProfileResponse.getEatoniccvistauid());
        userProfile.setEatonDrcId(userProfileResponse.getEatondrcid());
        userProfile.setUserOktaGroups(userProfileResponse.getUserOktaGroups());
        userProfile.setUserOktaAppLinks(userProfileResponse.getUserOktaAppLinks());
        return userProfile;
    }

}
