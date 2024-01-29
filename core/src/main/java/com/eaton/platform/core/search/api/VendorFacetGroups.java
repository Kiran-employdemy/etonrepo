package com.eaton.platform.core.search.api;

import java.util.Set;

/**
 * To be implemented for Search Vendor specific mapping, these are the fields needed by our system to be able to display the group of facet groups
 * we have now in the website.
 * @param <T> The implementation of Facet Group
 */
public interface VendorFacetGroups<T extends VendorFacetGroup<? extends VendorFacet>> {
    Long getProductsCount();

    Long getServicesCount();

    Long getNewsCount();

    Long getResourcesCount();

    Set<T> getFacetGroup();
}
