package com.eaton.platform.core.services.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AssetMetadataExposeServiceImpl")
public @interface AssetMetadataExposeServiceConfig {


    @AttributeDefinition(name = "includeAssetProperties")
    String [] middle_tab_config() default {};


}