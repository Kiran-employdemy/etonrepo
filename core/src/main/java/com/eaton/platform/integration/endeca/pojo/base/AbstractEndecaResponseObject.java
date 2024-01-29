package com.eaton.platform.integration.endeca.pojo.base;

import com.eaton.platform.integration.endeca.bean.StatusDetailsBean;

/**
 * Internal abstract implementation detail for the Endeca's implementation of VendorResponse
 * Has some status fields, facets ( binning ) and the search results, needs to be implemented
 */
public abstract class AbstractEndecaResponseObject<T>{
    private String status;
    private StatusDetailsBean statusDetails;
    private EndecaBinnings binning;

    public String getStatus() {
        return status;
    }

    public StatusDetailsBean getStatusDetails() {
        return statusDetails;
    }

    public EndecaBinnings getBinning() {
        return binning;
    }

    public abstract T getSearchResults();
}
