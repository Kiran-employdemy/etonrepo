/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.myeaton.services.MyEatonServiceConfiguration;
import com.eaton.platform.integration.myeaton.services.MyEatonSupplierLookupService;
import com.eaton.platform.integration.myeaton.util.MyEatonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.json.JSONArray;
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
    service = MyEatonSupplierLookupService.class,
    immediate = true,
    property = {
        AEMConstants.SERVICE_VENDOR_EATON,
        AEMConstants.SERVICE_DESCRIPTION + "My Eaton - Supplier Lookup Service",
        AEMConstants.PROCESS_LABEL + "MyEatonSupplierLookupServiceImpl"
    })
public class MyEatonSupplierLookupServiceImpl implements MyEatonSupplierLookupService {
    private static final Logger LOG = LoggerFactory.getLogger(MyEatonSupplierLookupServiceImpl.class);
    @Reference
    private HttpClientBuilderFactory httpFactory;

    @Reference
    private MyEatonServiceConfiguration serviceConfiguration;

    private PoolingHttpClientConnectionManager conMgr;

    private String supplierLookupEndpoint;

    @Activate
    @Modified
    protected  final void activate() {
        supplierLookupEndpoint = serviceConfiguration.getEndpointRoot()
            + serviceConfiguration.getSupplierLookupUrl();
    }

    @Override
    public boolean supplierLookup(String supplierNumber) {
        LOG.debug("Start with supplierLookup method ::");
        boolean activeSupplierExists = false;
        String responseContent = executeApi(supplierNumber);

        try {
            if(!StringUtils.isAllEmpty(responseContent)) {
                JSONArray responseJSONArray = new JSONArray(responseContent);
                if(responseJSONArray.getJSONObject(0)!=null) {
                    JSONObject responseJSON = responseJSONArray.getJSONObject(0);
                    if (responseJSON.getString("nsUniqueId") != null) {
                        activeSupplierExists = true;
                    }
                }
            }
        } catch (JSONException e) {
            LOG.error("JSONException while parsing the JSON response from supplierLookup API call : {}", e.getMessage());
        }
        LOG.debug("End with supplierLookup method supplierExists :: {}", activeSupplierExists);
        return activeSupplierExists;
    }

    private String executeApi(String supplierNumber) {
        LOG.debug("Start with executeApi method ::");

        // Append Supplier Number
        String fullUrl = supplierLookupEndpoint + "/" + supplierNumber;
        conMgr = MyEatonUtil.getMultiThreadedConf(conMgr, serviceConfiguration);
        String response = MyEatonUtil.callBasicHttpGet(fullUrl, conMgr, httpFactory,serviceConfiguration);

        LOG.info("End with executeApi method :: {}", response);
        return response;
    }
}
