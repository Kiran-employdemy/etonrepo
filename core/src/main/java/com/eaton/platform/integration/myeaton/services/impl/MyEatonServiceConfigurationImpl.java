/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.myeaton.services.MyEatonServiceConfiguration;
import com.eaton.platform.integration.myeaton.services.config.MyEatonServiceConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

/** Service for handling up MyEaton configuration settings */
@Component(
    service = MyEatonServiceConfiguration.class,
    immediate = true,
    property = {
        AEMConstants.SERVICE_VENDOR_EATON,
        AEMConstants.SERVICE_DESCRIPTION + "My Eaton - Service Configuration",
        AEMConstants.PROCESS_LABEL + "MyEatonServiceConfiguration"
    })
@Designate(ocd = MyEatonServiceConfig.class)
public class MyEatonServiceConfigurationImpl implements MyEatonServiceConfiguration {
    private int maxConnections;
    private int maxHostConnections;

    private int maxCacheSize;
    private int cacheDuration;

    private String endpointRoot;
    private String userRegistrationUrl;
    private String userLookupUrl;
    private String supplierLookupUrl;
    private String updateUserUrl;

    private String fieldsUrl;

    private String username;
    private String password;

    private String metadataPath;
    private String fdmDefinitionsPath;

    @Activate
    @Modified
    protected  final void activate(final MyEatonServiceConfig config) {
        if (config != null) {
            maxConnections = config.maxConnections();
            maxHostConnections = config.maxHostConnections();

            maxCacheSize = config.maxCacheSize();
            cacheDuration = config.cacheDuration();

            endpointRoot = config.myEatonEndpointRoot();
            userRegistrationUrl = config.userRegistrationEndpoint();
            userLookupUrl = config.userLookupEndpoint();
            supplierLookupUrl = config.supplierLookupEndpoint();
            fieldsUrl = config.fieldsEndpoint();
            updateUserUrl = config.updateUserEndpoint();

            username = config.myEatonUsername();
            password = config.myEatonPassword();

            metadataPath = config.metadataPath();
            fdmDefinitionsPath = config.fdmDefinitionsPath();
        }
    }

    @Override
    public int getMaxConnections() {
        return maxConnections;
    }

    @Override
    public int getMaxHostConnections() {
        return maxHostConnections;
    }

    @Override
    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    @Override
    public int getCacheDuration() {
        return cacheDuration;
    }

    @Override
    public String getEndpointRoot() {
        return endpointRoot;
    }

    @Override
    public String getFieldsUrl() {
        return fieldsUrl;
    }

    @Override
    public String getUserRegistrationUrl() {
        return userRegistrationUrl;
    }

    @Override
    public String getUserLookupUrl() {
        return userLookupUrl;
    }

    @Override
    public String getSupplierLookupUrl() {
        return supplierLookupUrl;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getMetadataPath() {
        return metadataPath;
    }

    @Override
    public String getFdmDefinitionsPath() {
        return fdmDefinitionsPath;
    }

    @Override
    public String getUpdateUserUrl() {
        return updateUserUrl;
    }
}
