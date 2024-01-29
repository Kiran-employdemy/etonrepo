package com.eaton.platform.core.search.api;

import java.util.Set;

/**
 * To be implemented for Search Vendor specific mapping, these are the fields needed by our system to be able to display the facet group
 * we have now in the website.
 * @param <T> The Facet implementation
 */
public interface VendorFacetGroup <T extends VendorFacet> {
    String getGroupId();

    String getGroupLabel();

    Set<T> getFacets();
}
