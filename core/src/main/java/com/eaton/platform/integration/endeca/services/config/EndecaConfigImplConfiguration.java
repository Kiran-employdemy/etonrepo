package com.eaton.platform.integration.endeca.services.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Endeca Config Properties ", description = "Endeca Config Properties")
public @interface EndecaConfigImplConfiguration {

    @AttributeDefinition(name = "ESP Service URL", description = "Service URL for ESP service")
    String esp_service_url() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Product compatibility app name", description = "PC application name")
    String product_compatibility_app_name() default "eatonledinfos";

    @AttributeDefinition(name = "SKU Details Application Name", description = "SKU Details Application Name")
    String sku_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "ESP Application Key", description = "Application Key for ESP service")
    String esp_service_application_key() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Endeca User Agent Value", description = "Endeca User Agent Value")
    String endecaUserAgentValue() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Response Stub Directory", description = "Response Stub Directory for ESP service")
    String webservice_stub_dir() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Response Stub Enabled", description = "Response Stub Enabled for ESP service")
    boolean webservice_stub_enabled() default false;

    @AttributeDefinition(name = "Products Tab Id", description = "Products Tab Id")
    String products_tab_id() default StringUtils.EMPTY;

    @AttributeDefinition(name = "News Tab Id", description = "News Tab Id")
    String news_tab_id() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Services Tab Id", description = "Services Tab Id")
    String services_tab_id() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Resources Tab Id", description = "Resources Tab Id")
    String resources_tab_id() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Product Family SKU Sitemap List Application Name", description = "Product Family SKU Sitemap List Application Name")
    String productfamily_sitemap_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Orphan SKU Sitemap List Application Name", description = "Orphan SKU Sitemap List Application Name")
    String skusitemap_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Product Family SKU Sitemap Number Of Records To Return", description = "Product Family SKU Sitemap Number Of Records To Return")
    String productfamily_sitemap_numberOfRecordsToReturn() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Orphan SKU Sitemap Number Of Records To Return", description = "Orphan SKU Sitemap Number Of Records To Return")
    String skusitemap_numberOfRecordsToReturn() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Product Family SKU List Application Name", description = "Product Family SKU List Application Name")
    String productfamily_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Sub-Category Application Name", description = "Sub-Category Application Name")
    String subcategory_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Site-Search Application Name", description = "Site-Search Application Name")
    String sitesearch_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Endeca Service URL for PDH1 and PDH2 applications", description = "Endeca Service URL for PDH1 and PDH2 applications")
    String endeca_url_pdh1andpdh2() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Endeca Path required for VG Product Selector", description = "Endeca Path to be pointed by AEM")
    String vgselector_endeca_url() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Clutch Selector Application Name", description = "Application Name for VG Clutch Selector")
    String clutchselector_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Torque Selector Application Name", description = "Application Name for VG Torque Selector")
    String torqueselector_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Submittal builder Application Name", description = "Submittal builder Application Name")
    String submitbuilder_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Eaton Content Hub Application Name", description = " Eaton Content Hub Application Name")
    String eatoncontenthub_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Cross Reference Application Name", description = " Cross Reference Application Name")
    String crossreference_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "File mime types", description = "Enter the file mime types")
    String[] file_types() default {};

    @AttributeDefinition(name = "Comparison Details Application Name", description = "Comparison Details Application Name")
    String comparison_application_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Facet Value for Learn Page", description = "Facet Value for Learn Page")
    String facet_value_learnpage() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Product Family Active Facet ID", description = "Active Facet ID for Product Family ")
    String productfamily_activefacet_id() default StringUtils.EMPTY;

}