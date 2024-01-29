package com.eaton.platform.integration.endeca.pojo.sku;

import com.eaton.platform.integration.endeca.pojo.base.AbstractEndecaSearchResults;

import java.util.Set;

public class EndecaSkuSearchResults extends AbstractEndecaSearchResults<EndecaSkuDocument> {

    private Set<EndecaSkuDocument> document;

    @Override
    protected Set<EndecaSkuDocument> getDocument() {
        return document;
    }
}
