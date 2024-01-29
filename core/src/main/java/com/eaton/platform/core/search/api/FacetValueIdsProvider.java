package com.eaton.platform.core.search.api;

/**
 * Provider that gives quick access to different facetValue Ids
 * Sometimes needed in the code, don't know why, but provided here to be sure we don't break things.
 */
public interface FacetValueIdsProvider {
    /**
     * @return the products facet id
     */
    String getProductsTabId();

    /**
     * @return the services facet id
     */
    String getServicesTabId();

    /**
     * @return the news facet id
     */
    String getNewsTabId();

    /**
     * @return the resources facet Id
     */
    String getResourcesTabId();
}
