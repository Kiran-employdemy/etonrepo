package com.eaton.platform.core.search.pojo;

import com.eaton.platform.core.search.api.FacetGroupMappingContext;
import com.eaton.platform.core.search.api.FacetValueIdsProvider;
import com.eaton.platform.core.search.api.AssetSearchResponseMappingContext;
import com.eaton.platform.core.search.api.AssetSearchResultMappingContext;
import com.eaton.platform.core.search.api.TranslationMappingContext;
import com.eaton.platform.core.search.api.VendorAssetSearchRecord;
import com.eaton.platform.core.search.api.VendorFacet;
import com.eaton.platform.core.search.api.VendorFacetGroup;
import com.eaton.platform.core.search.api.VendorFacetGroups;
import com.eaton.platform.core.search.api.VendorAssetResponse;
import com.eaton.platform.integration.endeca.bean.StatusDetailsBean;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * POJO for displaying the search response fields with facets and search results.
 * This is the entry class for the whole tree of fields, facets and search results.
 */
public class AssetSearchResponse {
    private String productsId;
    private Long resourcesCount;
    private String servicesId;
    private Integer facetsViewMoreOffset;
    private Long totalCount;
    private SearchFacets facets;
    private Long loadMoreOffset;
    private Long servicesCount;
    private String newsId;
    private String resourcesId;
    private Set<AssetSearchRecord> siteSearchResults;
    private Long newsCount;
    private Long productsCount;
    private String status;
    private StatusDetailsBean statusDetails;
    private Long executionTime;

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    /**
     * Factory method taking as arguments:
     * It will call all factory methods of its children POJOs.
     *
     * @param vendorResponse vendor implementation for the Response
     * @param mappingContext response mapping context
     * @param <T> type of vendor implementation
     * @param <M> type of response mapping context
     * @return the completely filled in SearchResponse object
     */
    public static <T extends VendorAssetResponse<? extends VendorAssetSearchRecord>
            , M extends AssetSearchResponseMappingContext<? extends FacetGroupMappingContext<? extends TranslationMappingContext>
                        , ? extends AssetSearchResultMappingContext<? extends TranslationMappingContext>, ? extends FacetValueIdsProvider>>
    AssetSearchResponse of(T vendorResponse, M mappingContext) {
        AssetSearchResponse searchResponse = new AssetSearchResponse();
        searchResponse.status = vendorResponse.getStatus();
        searchResponse.statusDetails = vendorResponse.getStatusDetails();
        searchResponse.facetsViewMoreOffset = mappingContext.getFacetsViewMoreOffset();
        VendorFacetGroups<? extends VendorFacetGroup<? extends VendorFacet>> facetGroups = vendorResponse.getFacetGroups();
        FacetValueIdsProvider facetValueIdsProvider = mappingContext.getFacetValueIdsProvider();
        searchResponse.productsId = facetValueIdsProvider.getProductsTabId();
        searchResponse.productsCount = facetGroups.getProductsCount();
        searchResponse.servicesId = facetValueIdsProvider.getServicesTabId();
        searchResponse.servicesCount = facetGroups.getServicesCount();
        searchResponse.newsId = facetValueIdsProvider.getNewsTabId();
        searchResponse.newsCount = facetGroups.getNewsCount();
        searchResponse.resourcesId = facetValueIdsProvider.getResourcesTabId();
        searchResponse.resourcesCount = facetGroups.getResourcesCount();
        searchResponse.facets = SearchFacets.of(facetGroups, mappingContext.getFacetGroupMappingContext());
        Long totalCount = vendorResponse.getTotalCount();
        searchResponse.totalCount = totalCount;
        searchResponse.loadMoreOffset = determineLoadMoreOffset(totalCount, mappingContext);
        Set<? extends VendorAssetSearchRecord> documents = vendorResponse.getDocuments();
        if (documents == null || documents.isEmpty()) {
            return searchResponse;
        }
        searchResponse.siteSearchResults = new LinkedHashSet<>();
        documents.forEach(vendorDocument ->
                searchResponse.siteSearchResults.add(AssetSearchRecord.of(vendorDocument, mappingContext.getSearchResultMappingContext()))
        );
        return searchResponse;
    }

    public static AssetSearchResponse fail(String message) {
        AssetSearchResponse searchResponse = new AssetSearchResponse();
        searchResponse.status = message;
        return searchResponse;
    }

    private static <M extends AssetSearchResponseMappingContext<? extends FacetGroupMappingContext<? extends TranslationMappingContext>
                , ? extends AssetSearchResultMappingContext<? extends TranslationMappingContext>, ? extends FacetValueIdsProvider>>
    Long determineLoadMoreOffset(Long totalCount, M mappingContext) {
        Long loadMoreOffset = mappingContext.getLoadMoreOffset();
        if (loadMoreOffset + mappingContext.getPageSize() < totalCount){
            return loadMoreOffset + mappingContext.getPageSize();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssetSearchResponse that = (AssetSearchResponse) o;
        return Objects.equals(productsId, that.productsId) && Objects.equals(resourcesCount, that.resourcesCount)
                && Objects.equals(servicesId, that.servicesId) && Objects.equals(facetsViewMoreOffset, that.facetsViewMoreOffset)
                && Objects.equals(totalCount, that.totalCount) && Objects.equals(facets, that.facets)
                && Objects.equals(loadMoreOffset, that.loadMoreOffset) && Objects.equals(servicesCount, that.servicesCount)
                && Objects.equals(newsId, that.newsId) && Objects.equals(resourcesId, that.resourcesId)
                && Objects.equals(siteSearchResults, that.siteSearchResults) && Objects.equals(newsCount, that.newsCount)
                && Objects.equals(productsCount, that.productsCount) && Objects.equals(status, that.status)
                && Objects.equals(statusDetails, that.statusDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productsId, resourcesCount, servicesId, facetsViewMoreOffset, totalCount, facets, loadMoreOffset
                , servicesCount, newsId, resourcesId, siteSearchResults, newsCount, productsCount, status, statusDetails);
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
