package com.eaton.platform.integration.salesforce.services;

import java.util.List;

/**
 * Salesforce knowledge search service
 */
public interface SalesforceKnowledgeSearchService {

    boolean isGlobalEnabled();
    List<String> getTargetTags();
    String getSalesforceSiteUrl();
    String getSalesforceSearchPath();
    String getImagePath();

}
