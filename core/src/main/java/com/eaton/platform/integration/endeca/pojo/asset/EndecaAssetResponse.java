package com.eaton.platform.integration.endeca.pojo.asset;

import com.eaton.platform.core.search.api.VendorAssetResponse;
import com.eaton.platform.integration.endeca.bean.StatusDetailsBean;
import com.eaton.platform.integration.endeca.pojo.base.EndecaBinnings;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

/**
 * Endeca's implementation of VendorResponse
 */
public class EndecaAssetResponse implements VendorAssetResponse<EndecaAssetDocument> {
    private EndecaAssetResponseObject response;

    @Override
    public Set<EndecaAssetDocument> getDocuments() {
        if (response == null || response.getSearchResults() == null) {
            return new HashSet<>();
        }
        EndecaAssetSearchResults searchResults = response.getSearchResults();
        return searchResults.getDocument();
    }

    @Override
    public String getStatus() {
        return response.getStatus();
    }

    @Override
    public StatusDetailsBean getStatusDetails() {
        return response.getStatusDetails();
    }

    @Override
    public EndecaBinnings getFacetGroups() {
        return response.getBinning();
    }

    @Override
    public Long getTotalCount() {
        return response.getSearchResults().getTotalCount();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
