package com.eaton.platform.core.search.api;

/**
 * To be implemented for Search Vendor specific mapping, these are the fields needed by our system to be able to display the facets
 * we have now in the website.
 */
public interface VendorFacet {
    String getLabel();

    String getValue();

    Long getNumberOfDocs();

}
