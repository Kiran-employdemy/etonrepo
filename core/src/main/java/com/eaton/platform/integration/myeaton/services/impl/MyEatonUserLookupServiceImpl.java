/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.myeaton.services.MyEatonServiceConfiguration;
import com.eaton.platform.integration.myeaton.services.MyEatonUserLookupService;
import com.eaton.platform.integration.myeaton.util.MyEatonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for looking up MyEaton accounts */
@Component(
    service = MyEatonUserLookupService.class,
    immediate = true,
    property = {
        AEMConstants.SERVICE_VENDOR_EATON,
        AEMConstants.SERVICE_DESCRIPTION + "My Eaton - User Lookup Service",
        AEMConstants.PROCESS_LABEL + "MyEatonUserLookupServiceImpl"
    })
public class MyEatonUserLookupServiceImpl implements MyEatonUserLookupService {
    private static final Logger LOG = LoggerFactory.getLogger(MyEatonUserLookupServiceImpl.class);
    @Reference
    private HttpClientBuilderFactory httpFactory;

    @Reference
    private MyEatonServiceConfiguration serviceConfiguration;

    private PoolingHttpClientConnectionManager conMgr;

    private String userLookupEndpoint;

    @Activate
    @Modified
    protected  final void activate() {
        userLookupEndpoint = serviceConfiguration.getEndpointRoot()
            + serviceConfiguration.getUserLookupUrl();
    }

    @Override
    public boolean userLookup(String userEmail) {
        LOG.debug("Start with userLookup method ::");
        boolean activeUserExists = false;
        String responseContent = executeApi(userEmail);

        try {
            if(!StringUtils.isAllEmpty(responseContent)) {
                JSONObject responseJSON = new JSONObject(responseContent);

                if ((responseJSON.getString("eatonportalstatus") != null) && (responseJSON.getString("eatonstatus") != null) &&
                        (("ACTIVE").equalsIgnoreCase(responseJSON.getString("eatonportalstatus"))) &&
                        (("ACTIVE").equalsIgnoreCase(responseJSON.getString("eatonstatus")))) {
                    activeUserExists = true;
                }

            }
        } catch (JSONException e) {
            LOG.error("JSONException while parsing the JSON response from userLookup API call : {}", e.getMessage());
        }
        LOG.debug("End with userLookup method activeUserExists :: {}", activeUserExists);
        return activeUserExists;
    }

    private String executeApi(String userEmail) {
        LOG.debug("Start with user lookup method ::");

        // Append email address
        String fullUrl = userLookupEndpoint + "/" + userEmail;

        conMgr = MyEatonUtil.getMultiThreadedConf(conMgr, serviceConfiguration);

        String response = MyEatonUtil.callBasicHttpGet(fullUrl, conMgr, httpFactory,serviceConfiguration);

        LOG.info("End with user lookup method :: {}", response);
        return response;
    }
}
