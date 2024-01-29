package com.eaton.platform.integration.endeca.helpers;

import com.eaton.platform.core.search.api.FacetValueIdsProvider;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;

/**
 * Endeca's implementation of the Facet value id provider
 */
public class EndecaFacetValueIdsProviderImpl implements FacetValueIdsProvider {
    private final EndecaConfigServiceBean endecaConfig;

    /**
     * Constructor taking as arguments:
     * @param endecaConfig to set
     */
    public EndecaFacetValueIdsProviderImpl(EndecaConfigServiceBean endecaConfig) {
        this.endecaConfig = endecaConfig;
    }

    @Override
    public String getProductsTabId() {
        return endecaConfig.getProductsTabId();
    }

    @Override
    public String getServicesTabId() {
        return endecaConfig.getServicesTabId();
    }

    @Override
    public String getNewsTabId() {
        return endecaConfig.getNewsTabId();
    }

    @Override
    public String getResourcesTabId() {
        return endecaConfig.getResourcesTabId();
    }
}
