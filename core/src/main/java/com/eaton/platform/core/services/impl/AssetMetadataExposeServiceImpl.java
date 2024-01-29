package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AssetMetadataExposeService;
import com.eaton.platform.core.services.config.AssetMetadataExposeServiceConfig;
import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import java.util.Map;

@Designate(ocd = AssetMetadataExposeServiceConfig.class)
@Component(service = AssetMetadataExposeService.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton Include Asset Properties",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "Eaton Include Asset Properties"
        })
public class AssetMetadataExposeServiceImpl implements AssetMetadataExposeService {
    public static final String INCLUDE_ASSET_PROPERTIES = "includeAssetProperties";
    private String[] includeAssetPropertiesList;
    public static final String INCLUDE_SECURE_ASSET_PROPERTIES = "includeSecureAssetProperties";
    private String[] includeSecureAssetPropertiesList;


    @Activate
    protected void activate(Map<String, Object> properties) {
        includeAssetPropertiesList = PropertiesUtil.toStringArray(properties.get(INCLUDE_ASSET_PROPERTIES), ArrayUtils.EMPTY_STRING_ARRAY);
        includeSecureAssetPropertiesList = PropertiesUtil.toStringArray(properties.get(INCLUDE_SECURE_ASSET_PROPERTIES), ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @Override
    public String[] getIncludeAssetProperties() {
        return includeAssetPropertiesList;
    }

    @Override
    public String[] getIncludeSecureAssetProperties() {
        return includeSecureAssetPropertiesList;
    }
}