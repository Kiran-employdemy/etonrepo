/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services;

/** Configuration service for sharing common configuration data across services */
public interface MyEatonServiceConfiguration {

    int getMaxConnections();

    int getMaxHostConnections();

    int getMaxCacheSize();

    int getCacheDuration();

    String getEndpointRoot();

    String getUserRegistrationUrl();

    String getUserLookupUrl();

    String getSupplierLookupUrl();

    String getFieldsUrl();

    String getUsername();

    String getPassword();

    String getMetadataPath();

    String getFdmDefinitionsPath();
     String getUpdateUserUrl();
}
