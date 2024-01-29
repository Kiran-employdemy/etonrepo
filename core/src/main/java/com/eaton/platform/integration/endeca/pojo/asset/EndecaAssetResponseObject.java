package com.eaton.platform.integration.endeca.pojo.asset;

import com.eaton.platform.integration.endeca.pojo.base.AbstractEndecaResponseObject;

/**
 * Internal implementation detail for the Endeca's implementation of VendorResponse
 * Has some status fields, facets ( binning ) and the search results
 */
public class EndecaAssetResponseObject extends AbstractEndecaResponseObject<EndecaAssetSearchResults> {
    private EndecaAssetSearchResults searchResults;
    @Override
    public EndecaAssetSearchResults getSearchResults() {
        return searchResults;
    }
}
