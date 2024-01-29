package com.eaton.platform.integration.endeca.pojo.sku;

import com.eaton.platform.integration.endeca.pojo.base.AbstractEndecaResponseObject;

public class EndecaSkuResponseObject extends AbstractEndecaResponseObject<EndecaSkuSearchResults> {
    private EndecaSkuSearchResults searchResults;

    @Override
    public EndecaSkuSearchResults getSearchResults() {
        return searchResults;
    }
}
