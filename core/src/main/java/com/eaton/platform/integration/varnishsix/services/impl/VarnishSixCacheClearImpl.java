package com.eaton.platform.integration.varnishsix.services.impl;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationLog;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.varnish.constants.VarnishConstants;
import com.eaton.platform.integration.varnishsix.services.VarnishSixCacheClearService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import com.google.gson.JsonArray;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
@Component(immediate = true, service = VarnishSixCacheClearService.class,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "=VarnishSixCacheClearImpl",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "VarnishSixCacheClearImpl"
        })
public class VarnishSixCacheClearImpl implements VarnishSixCacheClearService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VarnishSixCacheClearImpl.class);
    HttpClientContext context = new HttpClientContext();
    private static final String PROPERTY_DOMAIN = "varnishSixDomain";
    @Reference
    AgentManager agentManager;
    @Override
    public final LinkedHashMap<Agent, ArrayList<String>> clearVarnishSixCache(final SlingHttpServletRequest request) {
        final LinkedHashMap<Agent, ArrayList<String>> result = new LinkedHashMap<>();
        final String varnishConfigPath = request.getParameter(VarnishConstants.PATH);
        //Get configured varnish flush paths
        final String[] varnishFlushPaths = getVarnishFlushPaths(varnishConfigPath, request);
        try {
            final Map<String, Agent> agents = agentManager.getAgents();
            for(Map.Entry<String, Agent> entry : agents.entrySet()) {
                Agent valueAgent = entry.getValue();
                if (valueAgent.isEnabled()) {
                    final ReplicationLog log = valueAgent.getLog();
                    final String domain = getVarnishDomain(valueAgent);

                    if (StringUtils.isNotBlank(domain) && StringUtils.isNotBlank(valueAgent.getConfiguration().getTransportURI())) {
                        final String varnishRestApiUrl = getVarnishRestApiUrl(valueAgent.getConfiguration().getTransportURI());
                        if (StringUtils.isNotBlank(varnishRestApiUrl)) {
                            final HttpPost loginrequest = new HttpPost(varnishRestApiUrl + "/api/v1/auth/login");
                            final HttpPost apirequest = new HttpPost(varnishRestApiUrl + "/api/v1/invalidations");
                            String userName = StringUtils.EMPTY;
                            String password = StringUtils.EMPTY;
                            String orgValue = StringUtils.EMPTY;
                            String varnishDomain = StringUtils.EMPTY;
                            int vclGroupId = 0;
                            if (StringUtils.isNotBlank(valueAgent.getConfiguration().getTransportUser())) {
                                userName = valueAgent.getConfiguration().getTransportUser();
                                LOGGER.debug("clearVarnishSixCache::userName::{} ", userName);
                            }
                            if (StringUtils.isNotBlank(valueAgent.getConfiguration().getTransportPassword())) {
                                password = valueAgent.getConfiguration().getTransportPassword();
                            }
                            final ValueMap properties = valueAgent.getConfiguration().getProperties();
                            String domains = String.valueOf(properties.get(PROPERTY_DOMAIN));
                            final String [] domainValues = domains.split(VarnishConstants.PIPE);
                            if(domainValues.length > 1) {
                                orgValue = domainValues[2].trim();
                                LOGGER.debug("clearVarnishSixCache::orgValue::{} ",orgValue);

                                vclGroupId = Integer.parseInt(domainValues[0].trim());
                                LOGGER.debug("clearVarnishSixCache::vclGroupId::{} ",vclGroupId);

                                varnishDomain = domainValues[1].trim();
                                LOGGER.debug("clearVarnishSixCache::varnishDomain::{} ",varnishDomain);
                            }
                            final String[] excludedPaths = CommonUtil.getExcludedPaths(properties);
                            //Varnish Agent authentication check
                            final HttpResponse response = loginSendRequest(loginrequest, userName, password, orgValue);
                            if (response != null) {
                                final int statusCode = response.getStatusLine().getStatusCode();
                                log.info(response.toString());
                                log.info(VarnishConstants.END_LINE);
                                ArrayList<String> varnishResponse = new ArrayList<>();
                                LOGGER.debug("clearVarnishSixCache::Status code:{} ", statusCode);
                                if (statusCode == HttpStatus.SC_OK) {
                                    String accessToken = bufferAccessToken(response);
                                    LOGGER.debug("clearVarnishSixCache:: Access Token:{} ", accessToken);
                                    //Clear varnish cache
                                    varnishResponse =
                                            varnishBanClear(apirequest, varnishFlushPaths, log, excludedPaths, vclGroupId, varnishDomain, accessToken);
                                    result.put(valueAgent, varnishResponse);
                                } else {
                                    varnishResponse.add(String.valueOf(statusCode));
                                    result.put(valueAgent, varnishResponse);
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception exc) {
            LOGGER.debug("Exception while clearing varnish cache{}", exc.getMessage());
        }
        return result;
    }
    public HttpResponse loginSendRequest(final HttpPost request,
                                         final String user, final String password, String orgValue) throws AuthenticationException, JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(VarnishConstants.ORG, orgValue);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, password);
        request.addHeader(new BasicScheme().authenticate(credentials, request, context));
        request.setHeader(VarnishConstants.CONTENT_TYPE, ContentType.DEFAULT_TEXT.toString());

        final StringEntity loginEntity = new StringEntity(jsonObj.toString(), ContentType.APPLICATION_JSON);
        request.setEntity(loginEntity);
        final HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = null;
        try {
            if (null != client) {
                response = client.execute(request,context);
            }
        } catch (IOException e) {
            LOGGER.error(VarnishConstants.IO_EXCEPTION, e);
        }
        return response;
    }
    private ArrayList<String> varnishBanClear(final HttpPost request, final String[] varnishFlushPaths,
                                              final ReplicationLog log, final String[] excludedPaths, int vclGroupId, String varnishDomains, String accessToken) {

        ArrayList<String> varnishResponse = new ArrayList<>();
        if (StringUtils.isNotBlank(varnishDomains)) {

            try {
            	JsonArray varnishFlushPathsArray = new JsonArray();
				Arrays.stream(varnishFlushPaths) 
                		.forEach(varnishFlushPathsArray::add);
						
                final JsonArray purgeObjects = CommonUtil.getFilteredPurgePath(varnishFlushPathsArray, excludedPaths, log);
                varnishResponse = CommonUtil.clearVarnishSixCache(purgeObjects, request, context, log,
                        vclGroupId, varnishDomains, accessToken);

            } catch (IOException e) {
                LOGGER.error(VarnishConstants.REQUEST_SEND_EXCEEPTION, e.getMessage());
            } catch(Exception e) {
                LOGGER.error(VarnishConstants.CONTENT_BUILDER_EXCEPTION, e.getMessage());
            }
        }
    return varnishResponse;
    }
    private static String[] getVarnishFlushPaths(final String varnishPathConfig,
                                                 final SlingHttpServletRequest request) {
        String[]varnishFlushPaths = null;
        if (StringUtils.isNotBlank(varnishPathConfig)) {
            final Resource resource = request.getResourceResolver().resolve(varnishPathConfig);
            if (null != resource) {
                final ValueMap flushPathMap = resource.getValueMap();
                if (null != flushPathMap) {
                    varnishFlushPaths = flushPathMap.get("paths",new String[0]);
                }
            }
        }
        return varnishFlushPaths;
    }
    private static String getVarnishDomain(final Agent valueAgent) {
        final ValueMap property = valueAgent.getConfiguration().getProperties();
        String varnishSixDomain= StringUtils.EMPTY;
        if (null != property) {
            String domain = PropertiesUtil.toString(property.get(VarnishConstants.VARNISH_SIX_DOMAIN), StringUtils.EMPTY);
            final String [] domainValues = domain.split(VarnishConstants.PIPE);
            if(domainValues.length > 1){
             varnishSixDomain = domainValues[1].trim();
            }
        }
        return varnishSixDomain;
    }
    private static String getVarnishRestApiUrl(final String transportUri) {
        String varnishRestApiUrl = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(transportUri) && transportUri.contains(VarnishConstants.VARNISH_PROTOCOL)) {
                varnishRestApiUrl = transportUri.replace(VarnishConstants.VARNISH_PROTOCOL,
                        VarnishConstants.HTTP_PROTOCOL);
                varnishRestApiUrl = varnishRestApiUrl + VarnishConstants.COLON_PORT;
        }
        return varnishRestApiUrl;
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
            String accessTokenVal = responseJson.getString(VarnishConstants.ACCESS_TOKEN_KEY);
            if (accessTokenVal != null && StringUtils.isNotEmpty(accessTokenVal)) {
                accessToken = accessTokenVal;
                LOGGER.debug(VarnishConstants.MSG_DEBUG_ACCESS_TOKEN, accessToken);
            }
        } catch (JSONException e) {
            LOGGER.error("Unable to parse access token from api: ", e);
        }
        return accessToken;
    }
}
