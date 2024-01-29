/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.myeaton.bean.MyEatonFieldsResponseBean;
import com.eaton.platform.integration.myeaton.services.MyEatonFieldsService;
import com.eaton.platform.integration.myeaton.services.MyEatonServiceConfiguration;
import com.eaton.platform.integration.myeaton.util.MyEatonUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.gson.Gson;
import java.util.concurrent.TimeUnit;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service retrieving valid fields for use in MyEaton form/services */
@Component(
    service = MyEatonFieldsService.class,
    immediate = true,
    property = {
        AEMConstants.SERVICE_VENDOR_EATON,
        AEMConstants.SERVICE_DESCRIPTION + "My Eaton - Fields Service",
        AEMConstants.PROCESS_LABEL + "MyEatonFieldsServiceImpl"
    })
public class MyEatonFieldsServiceImpl implements MyEatonFieldsService {
    private static final Logger LOG = LoggerFactory.getLogger(MyEatonFieldsServiceImpl.class);

    @Reference
    private HttpClientBuilderFactory httpFactory;

    @Reference
    private MyEatonServiceConfiguration serviceConfiguration;

    private PoolingHttpClientConnectionManager conMgr;

    private String fieldsEndpoint;

    private LoadingCache<String, MyEatonFieldsResponseBean> fieldsCache;

    @Activate
    @Modified
    protected  final void activate() {
        fieldsEndpoint = serviceConfiguration.getEndpointRoot()
            + serviceConfiguration.getFieldsUrl();

        fieldsCache = Caffeine.newBuilder()
            .maximumSize(serviceConfiguration.getMaxCacheSize())
            .expireAfterWrite(serviceConfiguration.getCacheDuration(), TimeUnit.SECONDS)
            .build(this::executeApi);
    }

    @Override
    public MyEatonFieldsResponseBean getFields() {
        return fieldsCache.get(fieldsEndpoint);
    }

    private MyEatonFieldsResponseBean executeApi(String serviceEdpoint) {
        LOG.debug("Start with fields method ::");

        MyEatonFieldsResponseBean myEatonFieldsResponseBean = new MyEatonFieldsResponseBean();

        conMgr = MyEatonUtil.getMultiThreadedConf(conMgr, serviceConfiguration);

        String responseContent = MyEatonUtil.callBasicHttpGet(fieldsEndpoint, conMgr, httpFactory,
            serviceConfiguration);

        LOG.info("End with fields method :: {}", responseContent);

        if (!responseContent.isEmpty()) {
            myEatonFieldsResponseBean = new Gson().fromJson(responseContent,
                MyEatonFieldsResponseBean.class);
        }
        return myEatonFieldsResponseBean;
    }
}
