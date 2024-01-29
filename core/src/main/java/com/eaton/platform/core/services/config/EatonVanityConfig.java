package com.eaton.platform.core.services.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eaton Vanity URL Configuration", description = "Vanity URL data-store and redirect paths configuration")
public @interface EatonVanityConfig {
    /**
     * To enable or disable custom vanity flow
     * **/
    @AttributeDefinition( name = "Enable Vanity Flow",description = "If true, custom vanity flow will be enabled")
    boolean enable_vanity_flow() default false;

    /**
     * To get intermediate page path
     * **/
    @AttributeDefinition(name = "Intermediate page name", description = "Enter absolute path for intermediate page name")
    String intermediate_page_name() default StringUtils.EMPTY;

    /**
     * To get data store DAM location path
     * **/
    @AttributeDefinition(name = "Vanity data store parent", description = "Enter parent jcr node for vanity data store")
    String vanity_data_store_location_path() default StringUtils.EMPTY;

    /**
     * To get file name for eaton.com domain
     * **/
    @AttributeDefinition(name = "Vanity data store - eaton", description = "Enter eaton file name for vanity data store")
    String eaton_com_data_store_filename() default StringUtils.EMPTY;

    /**
     * To get file name for eatoncummins domain
     * **/
    @AttributeDefinition(name = "Vanity data store - eatoncummins", description = "Enter eatoncummins file name for vanity data store")
    String eatoncummins_data_store_filename() default StringUtils.EMPTY;

    /**
     * To get file name for greenswitching domain
     * **/
    @AttributeDefinition(name = "Vanity data store - greenswitching", description = "Enter greenswitching file name for vanity data store")
    String greenswitching_data_store_filename() default StringUtils.EMPTY;

    /**
     * To get file name for phoenixtecpower domain
     * **/
    @AttributeDefinition(name = "Vanity data store - phoenixtecpower", description = "Enter phoenixtecpower file name for vanity data store")
    String phoenixtecpower_data_store_filename() default StringUtils.EMPTY;

    /**
     * To get top level domains
     * **/
    @AttributeDefinition(name = "Top level domain configuration", description = "Enter Top level domain with respective repository path in this format (no spaces) : Domain name | Repository Path")
    String [] top_level_domain_config() default {};

    /**
     * To get vanity lookup skip paths
     * **/
    @AttributeDefinition(name = "Vanity lookup paths to skip", description = "Enter the paths that will be skipped from vanity lookup ")
    String [] vanity_lookup_skip_paths() default {};
}
