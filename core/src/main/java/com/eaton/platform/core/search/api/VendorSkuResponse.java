package com.eaton.platform.core.search.api;

/**
 * Market interface for the VendorResponse implementation for SKU Search results
 * @param <D> Type of VendorSkuSearchRecord to have in the list of results
 */
public interface VendorSkuResponse<D extends VendorSkuSearchRecord> extends VendorSearchResponse<VendorFacetGroups<? extends VendorFacetGroup<? extends VendorFacet>>, D> {

}
