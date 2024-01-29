package com.eaton.platform.integration.endeca.pojo.base;

import com.eaton.platform.core.search.api.VendorFacet;

/**
 * POJO for facets coming from Endeca
 */
public class EndecaBin implements VendorFacet {
    private String label;
    private String value;
    private Long ndocs;

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Long getNumberOfDocs() {
        return ndocs;
    }
}
