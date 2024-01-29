package com.eaton.platform.core.search.impl;

import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.search.api.FacetValueIdsProvider;
import com.eaton.platform.core.search.api.LoadMoreOffsetProvider;
import com.eaton.platform.core.search.api.AssetSearchResponseMappingContext;

/**
 * The implementation of the Response Mapping context for Advanced Search
 * @param <T> The facet value id provider, see note in {@link AssetSearchResponseMappingContext}
 */
public class AssetSearchResponseMappingContextImpl<T extends FacetValueIdsProvider, L extends LoadMoreOffsetProvider> implements AssetSearchResponseMappingContext<FacetGroupMappingContextImpl
        , AdvancedSearchResultMappingContextImpl, T> {
    private final FacetGroupMappingContextImpl facetGroupMappingContext;
    private final AdvancedSearchResultMappingContextImpl advancedSearchResultMappingContext;
    private final SiteResourceSlingModel siteConfig;
    private final T facetValueIdsProvider;
    private final L loadMoreOffsetProvider;

    /**
     * Constructor taking as parameters:
     * @param facetGroupMappingContext context for facet group mapping
     * @param advancedSearchResultMappingContext context for search result mapping
     * @param siteConfig the configuration for global/local
     * @param facetValueIdsProvider the provider for facet value Ids
     */
    public AssetSearchResponseMappingContextImpl(FacetGroupMappingContextImpl facetGroupMappingContext, AdvancedSearchResultMappingContextImpl advancedSearchResultMappingContext
            , SiteResourceSlingModel siteConfig, T facetValueIdsProvider, L loadMoreOffsetProvider) {
        this.facetGroupMappingContext = facetGroupMappingContext;
        this.advancedSearchResultMappingContext = advancedSearchResultMappingContext;
        this.siteConfig = siteConfig;
        this.facetValueIdsProvider = facetValueIdsProvider;
        this.loadMoreOffsetProvider = loadMoreOffsetProvider;
    }

    @Override
    public Integer getFacetsViewMoreOffset() {
        return siteConfig.getFacetValueCount();
    }

    @Override
    public Integer getPageSize() {
        return siteConfig.getPageSize();
    }

    @Override
    public Long getLoadMoreOffset() {
        return loadMoreOffsetProvider.getLoadMoreOffset();
    }

    @Override
    public FacetGroupMappingContextImpl getFacetGroupMappingContext() {
        return facetGroupMappingContext;
    }

    @Override
    public AdvancedSearchResultMappingContextImpl getSearchResultMappingContext() {
        return advancedSearchResultMappingContext;
    }

    @Override
    public T getFacetValueIdsProvider() {
        return facetValueIdsProvider;
    }

}
