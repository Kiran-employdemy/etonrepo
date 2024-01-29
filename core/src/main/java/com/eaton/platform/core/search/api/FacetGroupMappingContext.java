package com.eaton.platform.core.search.api;

/**
 * Provides context to be used when mapping FacetGroup(s) data from Vendor to Eaton
 * @param <T> The context for mapping the FacetValue(s)
 */
public interface FacetGroupMappingContext<T extends TranslationMappingContext> {
    /**
     * Finds the eaton facet configuration for a given label. Generally that will be the tagId
     * @param label to find the facet configuration for
     * @return null if it doesn't find it
     */
    FacetConfiguration findFacetConfigurationFor(String label);

    /**
     * @return the context for Facet Value mapping
     */
    T getTranslationContext();
}
