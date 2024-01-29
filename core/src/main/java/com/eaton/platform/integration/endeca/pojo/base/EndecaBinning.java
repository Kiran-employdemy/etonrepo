package com.eaton.platform.integration.endeca.pojo.base;

import com.eaton.platform.core.search.api.VendorFacetGroup;

import java.util.Set;

/**
 * Endeca's implementation of VendorFacetGroup
 */
public class EndecaBinning implements VendorFacetGroup<EndecaBin> {
    private String label;
    private String bsId;
    private Set<EndecaBin> bin;

    public String getGroupLabel() {
        return label;
    }

    public String getGroupId() {
        return bsId;
    }

    public Set<EndecaBin> getFacets() {
        return bin;
    }
}
