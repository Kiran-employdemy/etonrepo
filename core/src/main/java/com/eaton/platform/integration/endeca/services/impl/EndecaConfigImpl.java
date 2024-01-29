package com.eaton.platform.integration.endeca.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.config.EndecaConfigImplConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class EndecaConfigImpl.
 */
@Designate(ocd = EndecaConfigImplConfiguration.class)
@Component(service = EndecaConfig.class, immediate = true, property = {
        AEMConstants.SERVICE_DESCRIPTION + "Endeca Config",
        AEMConstants.PROCESS_LABEL + "EndecaConfigImpl",
        AEMConstants.SERVICE_VENDOR_EATON,
})
public class EndecaConfigImpl implements EndecaConfig {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EndecaConfigImpl.class);

    /** The service reference. */
    private EndecaConfigServiceBean endecaConfigServiceBean;

    @Activate
    @Modified
    protected final void activate(final EndecaConfigImplConfiguration config) {
        this.endecaConfigServiceBean = new EndecaConfigServiceBean();
        initializeConfigurations(config);
    }

    /**
     * Deactivate.
     */
    @Deactivate
    protected void deactivate() {
        this.endecaConfigServiceBean = null;
    }

    private void initializeConfigurations(EndecaConfigImplConfiguration config) {
        LOGGER.info("Inside initializeConfigurations method");
        this.endecaConfigServiceBean.setEspServiceURL(config.esp_service_url());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("ESP_SERVICE_URL: %s", config.esp_service_url()));
        }

        this.endecaConfigServiceBean.setSkuDetailsAppName(config.sku_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SKU_DETAILS_APPLICATION: %s", config.sku_application_name()));
        }

        this.endecaConfigServiceBean.setCompatibilityAppName(config.product_compatibility_app_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("PRODUCT_COMPATIBILITY_APP_NAME: %s", config.product_compatibility_app_name()));
        }

        this.endecaConfigServiceBean.setEndecaUserAgentValue(config.endecaUserAgentValue());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Endeca User Agent Value Configured : %s", config.endecaUserAgentValue()));
        }

        this.endecaConfigServiceBean.setEspAppKey(config.esp_service_application_key());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("ESP_SERVICE_APPLICATION_KEY: %s", config.esp_service_application_key()));
        }

        this.endecaConfigServiceBean.setStubResponsePath(config.webservice_stub_dir());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("ESP_SERVICE_STUB_RSP_DIR: %s", config.webservice_stub_dir()));
        }

        this.endecaConfigServiceBean.setStubResEnabled(config.webservice_stub_enabled());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("ESP_SERVICE_STUB_ENABLED: %s", config.webservice_stub_enabled()));
        }

        this.endecaConfigServiceBean.setProductsTabId(config.products_tab_id());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SITE_SEARCH_PRODUCTS_TAB: %s", config.products_tab_id()));
        }

        this.endecaConfigServiceBean.setNewsTabId(config.news_tab_id());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SITE_SEARCH_NEWS_TAB: %s", config.news_tab_id()));
        }

        this.endecaConfigServiceBean.setServicesTabId(config.services_tab_id());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SITE_SEARCH_SERVICES_TAB: %s", config.services_tab_id()));
        }

        this.endecaConfigServiceBean.setResourcesTabId(config.resources_tab_id());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SITE_SEARCH_RESOURCES_TAB: %s", config.resources_tab_id()));
        }

        this.endecaConfigServiceBean.setProductfamilySitemapAppName(config.productfamily_sitemap_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("PRODUCT_FAMILY_SITEMAP_APP_NAME: %s",
                    config.productfamily_sitemap_application_name()));
        }

        this.endecaConfigServiceBean.setSkuSitemapAppName(config.skusitemap_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SKUSITEMAP_APP_NAME: %s", config.skusitemap_application_name()));
        }

        this.endecaConfigServiceBean
                .setPfSitemapNumberOfRecordsToReturn(config.productfamily_sitemap_numberOfRecordsToReturn());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("PRODUCT_FAMILY_SITEMAP_NUMBER_OF_RECORDS_TO_RETURN: %s",
                    config.productfamily_sitemap_numberOfRecordsToReturn()));
        }

        this.endecaConfigServiceBean.setSkuSitemapNumberOfRecordsToReturn(config.skusitemap_numberOfRecordsToReturn());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SKUSITEMAP_NUMBER_OF_RECORDS_TO_RETURN: %s",
                    config.skusitemap_numberOfRecordsToReturn()));
        }

        this.endecaConfigServiceBean.setProductfamilyAppName(config.productfamily_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("PRODUCT_FAMILY_APP_NAME: %s", config.productfamily_application_name()));
        }

        this.endecaConfigServiceBean.setSubcategoryAppName(config.subcategory_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SUBCATEGORY_APP_NAME: %s", config.subcategory_application_name()));
        }

        this.endecaConfigServiceBean.setFacetLearnPage(config.facet_value_learnpage());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("FACET_VALUE_FOR_LEARN_PAGE: %s", config.facet_value_learnpage()));
        }

        this.endecaConfigServiceBean.setSitesearchAppName(config.sitesearch_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SITESEARCH_APP_NAME: %s", config.sitesearch_application_name()));
        }

        this.endecaConfigServiceBean.setEndecaPDH1PDH2ServieURL(config.endeca_url_pdh1andpdh2());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("ENDECA_URL_FOR_PDH1_PDH2: %s", config.endeca_url_pdh1andpdh2()));
        }

        this.endecaConfigServiceBean.setClutchSelctorAppName(config.clutchselector_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("CLUTCHSELECTOR_APP_NAME: %s", config.clutchselector_application_name()));
        }

        this.endecaConfigServiceBean.setTorqueSelectorAppName(config.torqueselector_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("TORQUESELECTOR_APP_NAME: %s", config.torqueselector_application_name()));
        }

        this.endecaConfigServiceBean.setVgSelectorEndecaURL(config.vgselector_endeca_url());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("VG_SELECTOR_ENDECA_PATH: %s", config.vgselector_endeca_url()));
        }

        this.endecaConfigServiceBean.setSubmittalBuilderAppName(config.submitbuilder_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("SUBMITTAL_BUILDER_APP_NAME: %s", config.submitbuilder_application_name()));
        }

        this.endecaConfigServiceBean.setEatonContentHubAppName(config.eatoncontenthub_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("EATONCONTENTHUB_APP_NAME: %s", config.eatoncontenthub_application_name()));
        }

        this.endecaConfigServiceBean.setCrossReferenceAppName(config.crossreference_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("CROSS_REFERENCE_APP_NAME: %s", config.crossreference_application_name()));
        }

        this.endecaConfigServiceBean.setFileTypes(config.file_types());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("FILE_TYPES: %s", config.file_types()));
        }

        this.endecaConfigServiceBean.setComparisonAppName(config.comparison_application_name());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("COMPARISON_APP_NAME: %s", config.comparison_application_name()));
        }

        this.endecaConfigServiceBean.setProductFamilyActiveFacetID(config.productfamily_activefacet_id());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("PRODUCTFAMILY_ACTIVEFACET_ID: %s", config.productfamily_activefacet_id()));
        }

        LOGGER.info("End of initializeConfigurations method");
    }

    public EndecaConfigServiceBean getConfigServiceBean() {
        return endecaConfigServiceBean;
    }
}
