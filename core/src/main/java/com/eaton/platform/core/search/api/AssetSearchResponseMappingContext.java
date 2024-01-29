package com.eaton.platform.core.search.api;

/**
 * Provides the context for mapping to SearchResponse eaton from Vendor asset search data
 * @param <F> context to help when mapping to facet groups
 * @param <R> context to help when mapping to search result
 * @param <V> provider for facet ids
 *           <em>NOTE:</em>
 *           <p>This crazy thing is only here because in the code the facet values are used directly.
 *            Nowhere it is used in the front-end, but I have seen it used in the backend code somewhere.
 *            I don't have time to investigate this yet, but we should see if we need this still. </p>
 */
public interface AssetSearchResponseMappingContext<F extends FacetGroupMappingContext<? extends TranslationMappingContext>
        , R extends AssetSearchResultMappingContext<? extends TranslationMappingContext>
        , V extends FacetValueIdsProvider> {
    /**
     * @return the number of visible facets
     */
    Integer getFacetsViewMoreOffset();

    /**
     * @return the number of search results per page
     */
    Integer getPageSize();

    /**
     * @return the number of search results per page
     */
    Long getLoadMoreOffset();

    /**
     * @return the context for mapping the facet group data
     */
    F getFacetGroupMappingContext();

    /**
     * @return the context for mapping the search result data
     */
    R getSearchResultMappingContext();

    /**
     * @return the valueIds provider, see NOTE
     */
    V getFacetValueIdsProvider();
}
