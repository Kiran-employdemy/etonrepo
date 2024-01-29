package com.eaton.platform.core.search.api;

import com.eaton.platform.integration.endeca.bean.StatusDetailsBean;

import java.util.Set;

/**
 * To be implemented for Search Vendor specific mapping, these are the fields needed by our system to be able to display the combination of
 * facets and results we have now in the website.
 * @param <F> the facet group implementation
 * @param <T> the document implementation
 */
public interface VendorSearchResponse<F extends VendorFacetGroups<? extends VendorFacetGroup<? extends VendorFacet>>, T extends VendorSearchRecord> {
    Set<T> getDocuments();

    String getStatus();

    StatusDetailsBean getStatusDetails();

    F getFacetGroups();

    Long getTotalCount();

}
