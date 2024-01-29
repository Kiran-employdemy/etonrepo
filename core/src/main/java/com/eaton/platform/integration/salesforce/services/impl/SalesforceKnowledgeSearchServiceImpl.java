package com.eaton.platform.integration.salesforce.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.salesforce.services.SalesforceKnowledgeSearchService;
import com.eaton.platform.integration.salesforce.services.config.SalesforceKnowledgeSearchConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Salesforce knowledge search service implementation
 */
@Component( service = SalesforceKnowledgeSearchService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "SalesforceKnowledgeSearchServiceImpl",
                AEMConstants.SERVICE_DESCRIPTION + "SalesforceKnowledgeSearchServiceImpl"
        })
@Designate(ocd = SalesforceKnowledgeSearchConfiguration.class)
public class SalesforceKnowledgeSearchServiceImpl implements SalesforceKnowledgeSearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesforceKnowledgeSearchServiceImpl.class);
    private boolean isGlobalEnabled;
    private List<String> targetTags;
    private String salesforceSiteUrl;
    private String salesforceSearchPath;
    private String imagePath;

    @Activate
    @Modified
    protected final void activate(final SalesforceKnowledgeSearchConfiguration config) {
        LOGGER.debug("activate: Start");

        if (null != config) {
            isGlobalEnabled = config.isGlobalEnabled();
            targetTags = Arrays.asList(config.targetTags());
            salesforceSiteUrl = config.salesforceSiteUrl();
            salesforceSearchPath = config.salesforceSearchPath();
            imagePath = config.imagePath();
        }

        LOGGER.debug("activate: End");

    }


    @Override
    public boolean isGlobalEnabled() {
        return isGlobalEnabled;
    }

    @Override
    public List<String> getTargetTags() {
        return targetTags;
    }

    @Override
    public String getSalesforceSiteUrl() {
        return salesforceSiteUrl;
    }

    @Override
    public String getSalesforceSearchPath() {
        return salesforceSearchPath;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }
}
