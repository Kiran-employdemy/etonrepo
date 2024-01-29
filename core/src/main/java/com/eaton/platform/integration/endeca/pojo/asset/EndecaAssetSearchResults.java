package com.eaton.platform.integration.endeca.pojo.asset;

import com.eaton.platform.integration.endeca.pojo.base.AbstractEndecaSearchResults;

import java.util.Set;

/**
 * Internal implementation detail for the Endeca's implementation of VendorResponse
 * Has the total count and the list of records found in the search
 */
public class EndecaAssetSearchResults extends AbstractEndecaSearchResults<EndecaAssetDocument> {

    private Set<EndecaAssetDocument> document;


    @Override
    public Set<EndecaAssetDocument> getDocument() {
        return document;
    }
}
