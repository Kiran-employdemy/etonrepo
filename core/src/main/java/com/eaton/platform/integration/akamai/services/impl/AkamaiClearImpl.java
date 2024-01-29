package com.eaton.platform.integration.akamai.services.impl;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor;
import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationException;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.akamai.config.AkamaiConfig;
import com.eaton.platform.integration.akamai.constants.AkamaiConstants;
import com.eaton.platform.integration.akamai.services.AkamaiClear;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


@Component(immediate = true, service = AkamaiClear.class,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "=AkamaiClearImpl",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "AkamaiClearImpl"

        })
@Designate(ocd = AkamaiConfig.class)
public class AkamaiClearImpl implements AkamaiClear {
    private static final Logger LOGGER = LoggerFactory.getLogger(AkamaiClearImpl.class);

    @Reference
    private HttpClientBuilderFactory httpFactory;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private AgentManager agentManager;

    private String baseURL = StringUtils.EMPTY;
    private String akamaiAccessToken = StringUtils.EMPTY;
    private String akamaiClientToken =  StringUtils.EMPTY;
    private String akamaiClientSecret = StringUtils.EMPTY;
    private String akamaiHost =  StringUtils.EMPTY;

    @Activate
    @Modified
    protected void activate(final AkamaiConfig config){
        baseURL = config.base_url();
        akamaiHost = config.akamai_host();
        akamaiAccessToken = config.access_token();
        akamaiClientToken = config.client_token();
        akamaiClientSecret = config.client_secret();
    }


    @Override
    public final LinkedHashMap<Agent, ArrayList<String>> clearAkamaiCache(final SlingHttpServletRequest request){
        ClientCredential clientCredential = ClientCredential.builder().accessToken(akamaiAccessToken).
                clientToken(akamaiClientToken).clientSecret(akamaiClientSecret).host(akamaiHost).build();
        final LinkedHashMap<Agent, ArrayList<String>> result = new LinkedHashMap<>();
        try {
            final Map<String, Agent> agents = agentManager.getAgents();
            for (Map.Entry<String, Agent> entry : agents.entrySet()) {
                Agent valueAgent = entry.getValue();
                if (valueAgent.isEnabled()) {
                    final ValueMap properties = valueAgent.getConfiguration().getProperties();
                    final String domain = properties.get(AkamaiConstants.DOMAIN, StringUtils.EMPTY);
                    final String type = properties.get(AkamaiConstants.TYPE, StringUtils.EMPTY);
                    final String action = properties.get(AkamaiConstants.ACTION, StringUtils.EMPTY);
                    final String[] cpcodes = properties.get(AkamaiConstants.CPCODES, String[].class);
                    if(cpcodes != null && cpcodes.length > 0){
                    final boolean mandatoryCondition = StringUtils.isNotBlank(domain) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(action);
                    if (mandatoryCondition && ((StringUtils.equals(type, AkamaiConstants.CPCODE) && cpcodes.length > 0) || !StringUtils.equals(type, AkamaiConstants.CPCODE))) {
                            String jsonObject = StringUtils.equals(type, AkamaiConstants.CPCODE) ? createPostBody(cpcodes, false) : createPostBody(getAkamaiFlushPaths(request.getParameter("path"), request), true);
                            result.putAll(getResult(getTransportURI(action, type, domain), jsonObject, clientCredential, valueAgent));
                    }
                    }
                }
            }
        } catch(Exception exc) {
            LOGGER.debug("Exception while clearing akamai cache {}", exc.getMessage());
        }
        return result;
    }

    private LinkedHashMap<Agent, ArrayList<String>> getResult(final String uri, final String jsonObject,
                                                              final ClientCredential clientCredential, final Agent valueAgent){
        final LinkedHashMap<Agent, ArrayList<String>> result =  new LinkedHashMap<>();
        LOGGER.info("URI: {}", uri);
        LOGGER.info("Post Body: {}", jsonObject);
        try {
            final HttpResponse response = sendRequest(uri, jsonObject, clientCredential);
                ArrayList<String> akamaiResponse = new ArrayList<>();
                final int statusCode = response.getStatusLine().getStatusCode();
                akamaiResponse.add(String.valueOf(statusCode));
                LOGGER.info("Response code recieved: {}", statusCode);
                result.put(valueAgent, akamaiResponse);
        } catch (Exception e) {
            LOGGER.error("Failed to post request to Akamai: {}", e.getMessage());
            ArrayList<String> akamaiResponse = new ArrayList<>();
            akamaiResponse.add("NULL FAILURE ON RESPONSE");
            result.put(valueAgent, akamaiResponse);
        }
        return result;
    }

    private HttpResponse sendRequest(final String uri, final String jsonObject, final ClientCredential clientCredential)
            throws ReplicationException {
        LOGGER.info("Inside Send Request method of Akamai");

        HttpClient client = httpFactory.newBuilder().addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(clientCredential))
                //.setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(clientCredential)) //This overrides the vpn setting passed from httpFactory.
                .build();

        HttpResponse response;
        HttpPost postConnection = CommonUtil.getHttpPostMethod(uri, CommonConstants.APPLICATION_JSON, CommonConstants.APPLICATION_JSON);
        StringEntity requestEntity = new StringEntity(jsonObject, ContentType.APPLICATION_JSON);
        postConnection.setEntity(requestEntity);
        try {
            LOGGER.info("Right before executing post");
            response = client.execute(postConnection);
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            LOGGER.info("Response Body: {}", responseBody);
        } catch (IOException e) {
            LOGGER.error("IO Exception in sendRequest");
            throw new ReplicationException("Could not send replication request.", e);
        }
        LOGGER.info("Sucessfully executed Send Request for Akamai");
        return response;
    }

    private String getTransportURI(String action, String type, String domain) {
        LOGGER.info("Entering getTransportURI method.");
        return AkamaiConstants.HTTPS_PROTOCOL + akamaiHost +
                AkamaiConstants.BASE_PATH + action + CommonConstants.SLASH_STRING + type + CommonConstants.SLASH_STRING + domain;
    }

    private String createPostBody(String[] objects, boolean url) throws ReplicationException {

        JSONObject json = new JSONObject();
        JSONArray purgeObjects = new JSONArray();

        if (url) {
            for (String object : objects) {
                purgeObjects.put(baseURL + object);
            }
        } else {
            for (String object : objects) {
                purgeObjects.put(object);
            }
        }

        if (purgeObjects.length() > 0) {
            try {
                json.put("objects", purgeObjects);
            } catch (JSONException e) {
                throw new ReplicationException("Could not build purge request content", e);
            }
        } else {
            throw new ReplicationException("No CP codes or pages to purge");
        }

        return json.toString();
    }

    private static String[] getAkamaiFlushPaths(final String akamaiConfigPath,
                                                 final SlingHttpServletRequest request) {
        String[]akamaiFlushPaths = null;
        if (StringUtils.isNotBlank(akamaiConfigPath)) {
            final Resource resource = request.getResourceResolver().resolve(akamaiConfigPath);
                final ValueMap flushPathMap = resource.getValueMap();
                    akamaiFlushPaths = flushPathMap.get("paths",new String[0]);
        }
        return akamaiFlushPaths;
    }

}
