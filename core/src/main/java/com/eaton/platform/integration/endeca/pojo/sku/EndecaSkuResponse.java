package com.eaton.platform.integration.endeca.pojo.sku;

import com.eaton.platform.core.search.api.VendorSkuResponse;
import com.eaton.platform.integration.endeca.bean.StatusDetailsBean;
import com.eaton.platform.integration.endeca.pojo.base.EndecaBinnings;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

/**
 * Endeca's implementation of VendorSkuResponse
 */
public class EndecaSkuResponse implements VendorSkuResponse<EndecaSkuDocument> {
    private EndecaSkuResponseObject response;

    public Set<EndecaSkuDocument> getDocuments() {
        if (response == null || response.getSearchResults() == null) {
            return new HashSet<>();
        }
        EndecaSkuSearchResults searchResults = response.getSearchResults();
        return searchResults.getDocument();
    }

    public String getStatus() {
        return response.getStatus();
    }

    public StatusDetailsBean getStatusDetails() {
        return response.getStatusDetails();
    }

    public EndecaBinnings getFacetGroups() {
        return response.getBinning();
    }

    public Long getTotalCount() {
        return response.getSearchResults().getTotalCount();
    }

    public String toString() {
        return new Gson().toJson(this);
    }

}
