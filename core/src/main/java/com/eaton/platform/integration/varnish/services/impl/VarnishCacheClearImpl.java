package com.eaton.platform.integration.varnish.services.impl;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationLog;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.varnish.constants.VarnishConstants;
import com.eaton.platform.integration.varnish.services.VarnishCacheClearService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Component(immediate = true, service = VarnishCacheClearService.class,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "=VarnishCacheClearImpl",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "VarnishCacheClearImpl"

        })
public class VarnishCacheClearImpl implements VarnishCacheClearService{

    private static final Logger LOGGER = LoggerFactory.getLogger(VarnishCacheClearImpl.class);


    HttpClientContext context = new HttpClientContext();

    @Reference
    AgentManager agentManager;

    @Override
    public final LinkedHashMap<Agent, ArrayList<String>> clearVarnishCache(final SlingHttpServletRequest request) {
        final LinkedHashMap<Agent, ArrayList<String>> result = new LinkedHashMap<Agent, ArrayList<String>>();
        final String varnishConfigPath = request.getParameter("path");

        //Get configured varnish flush paths
        final String[] varnishFlushPaths = getVarnishFlushPaths(varnishConfigPath, request);
        try {
            final Map<String, Agent> agents = agentManager.getAgents();
            for(Map.Entry<String, Agent> entry : agents.entrySet()) {
                Agent valueAgent = entry.getValue();
                if (valueAgent.isEnabled()) {
                    final ReplicationLog log = valueAgent.getLog();
                    final String domain = getVarnishDomain(valueAgent);
                    String varnishId = StringUtils.EMPTY;
                    if (domain.contains(VarnishConstants.HYPHEN)) {
                        varnishId = domain.split(VarnishConstants.HYPHEN)[0];
                    }
                    if (StringUtils.isNotBlank(domain) && StringUtils.isNotBlank(valueAgent.getConfiguration().getTransportURI())) {
                        final String varnishRestApiUrl = getVarnishRestApiUrl(valueAgent.getConfiguration().getTransportURI());
                        if (StringUtils.isNotBlank(varnishRestApiUrl)) {
                            final HttpPost loginrequest = new HttpPost(varnishRestApiUrl + "api/rest/login");
                            final HttpPost apirequest = new HttpPost(varnishRestApiUrl + "api/v1/group/" + varnishId + "/ban");
                            String userName = StringUtils.EMPTY;
                            String password = StringUtils.EMPTY;
                            if (StringUtils.isNotBlank(valueAgent.getConfiguration().getTransportUser())) {
                                userName = valueAgent.getConfiguration().getTransportUser();
                            }
                            if (StringUtils.isNotBlank(valueAgent.getConfiguration().getTransportPassword())) {
                                password = valueAgent.getConfiguration().getTransportPassword();
                            }
                            final ValueMap properties = valueAgent.getConfiguration().getProperties();
                            final String[] excludedPaths = CommonUtil.getExcludedPaths(properties);
                            //Varnish Agent authentication check
                            final HttpResponse response = loginSendRequest(loginrequest, userName, password);
                            if (response != null) {
                                final int statusCode = response.getStatusLine().getStatusCode();
                                log.info(response.toString());
                                log.info(VarnishConstants.END_LINE);
                                ArrayList<String> varnishResponse = new ArrayList<>();
                                if (statusCode == HttpStatus.SC_OK) {
                                    //Clear varnish cache
                                    varnishResponse =
                                            varnishBanClear(apirequest, userName, domain, varnishFlushPaths, log, excludedPaths);
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
            LOGGER.debug("Exception while clearing varnish cache", exc.getMessage());
        }

        return result;
    }

    private <T extends HttpRequestBase> HttpResponse loginSendRequest(final HttpPost request,
                                                                      final String user, final String password)
    {
        final String auth = VarnishConstants.USERNAME.concat(user).concat(VarnishConstants.PASSWORD).concat(password);
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        final StringEntity loginEntity = new StringEntity(auth,ContentType.DEFAULT_TEXT);
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

    private ArrayList<String> varnishBanClear(final HttpPost request, final String user,
                                              final String domain, final String[] varnishFlushPaths,
                                              final ReplicationLog log, final String[] excludedPaths) {

        ArrayList varnishResponse = new ArrayList();
        if (StringUtils.isNotBlank(domain)) {
            final String [] domainName = domain.split(VarnishConstants.HYPHEN);
            final String varnishDomain = domainName[0];
            final String groupName = CommonUtil.getVarnishGroupName(domainName);
            try {
                
            	JsonArray varnishFlushPathsArray = new JsonArray();
				Arrays.stream(varnishFlushPaths) 
                		.forEach( varnishFlushPath -> varnishFlushPathsArray.add(varnishFlushPath));
						
                final JsonArray purgeObjects = CommonUtil.getFilteredPurgePath(varnishFlushPathsArray, excludedPaths, log);
                varnishResponse = CommonUtil.clearVarnishCache(purgeObjects, request, context, log, user,
                        groupName, varnishDomain);

            } catch (IOException e) {
                LOGGER.debug(VarnishConstants.REQUEST_SEND_EXCEEPTION, e.getMessage());
            } catch(Exception e) {
                LOGGER.debug(VarnishConstants.CONTENT_BUILDER_EXCEPTION, e.getMessage());
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
        String domain = StringUtils.EMPTY;
        if (null != property) {
            domain = PropertiesUtil.toString(property.get(VarnishConstants.VARNISH_DOMAIN), StringUtils.EMPTY);
        }
        return domain;
    }





    private static String getVarnishRestApiUrl(final String transportUri) {
        String varnishRestApiUrl = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(transportUri)) {
            if(transportUri.contains(VarnishConstants.VARNISH_PROTOCOL)){
                varnishRestApiUrl = transportUri.replace(VarnishConstants.VARNISH_PROTOCOL,
                        VarnishConstants.HTTP_PROTOCOL);
            }
        }
        return varnishRestApiUrl;
    }
}
