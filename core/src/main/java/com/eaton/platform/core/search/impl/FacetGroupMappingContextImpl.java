package com.eaton.platform.core.search.impl;

import com.eaton.platform.core.search.api.FacetConfiguration;
import com.eaton.platform.core.search.api.FacetGroupMappingContext;

import java.util.List;

/**
 * Default implementation for the Facet group mapping context
 */
public class FacetGroupMappingContextImpl implements FacetGroupMappingContext<TranslationMappingContextImpl> {
    private final List<FacetConfiguration> facetConfigurations;
    private final FacetConfiguration defaultFacetConfiguration;
    private final TranslationMappingContextImpl translationMappingContext;

    /**
     * Constructor taking as arguments:
     * @param facetConfigurations to set
     * @param defaultFacetConfiguration to set
     * @param translationMappingContext to set
     */
    public FacetGroupMappingContextImpl(List<FacetConfiguration> facetConfigurations, FacetConfiguration defaultFacetConfiguration, TranslationMappingContextImpl translationMappingContext) {
        this.facetConfigurations = facetConfigurations;
        this.defaultFacetConfiguration = defaultFacetConfiguration;
        this.translationMappingContext = translationMappingContext;
    }

    @Override
    public FacetConfiguration findFacetConfigurationFor(String label) {
        return facetConfigurations.stream().filter(facetConfiguration -> label.equals(facetConfiguration.getFacet())).findFirst().orElse(defaultFacetConfiguration);
    }

    @Override
    public TranslationMappingContextImpl getTranslationContext() {
        return translationMappingContext;
    }

}
