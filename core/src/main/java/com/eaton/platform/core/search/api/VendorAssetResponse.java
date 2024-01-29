package com.eaton.platform.core.search.api;

/**
 * To be implemented for Search Vendor specific mapping, these are the fields needed by our system to be able to display the combination of
 * facets and results we have now in the website.
 * @param <D> the Asset Document implementation
 */
public interface VendorAssetResponse<D extends VendorAssetSearchRecord> extends VendorSearchResponse<VendorFacetGroups<? extends VendorFacetGroup<? extends VendorFacet>>, D> {

}
