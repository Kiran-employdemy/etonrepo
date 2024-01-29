package com.eaton.platform.core.search.pojo;

import com.eaton.platform.core.search.api.FacetConfiguration;
import com.eaton.platform.core.search.api.FacetGroupMappingContext;
import com.eaton.platform.core.search.api.TranslationMappingContext;
import com.eaton.platform.core.search.api.VendorFacet;
import com.eaton.platform.core.search.api.VendorFacetGroup;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * POJO for displaying the Facet Groups with their facets and fields
 */
public class SearchFacetsGroup {
    private String facetGroupId;
    private String facetTempGroupId;
    private Integer sortOrder;
    private String gridFacet;
    private Set<SearchFacetValue> facetValueList;
    private String facetGroupLabel;
    private String singleFacetEnabled;
    private Boolean secure;
    private String facetSearchEnabled;

    /**
     * Factory method taking as arguments:
     * @param facetGroup the vendor implementation of facet group
     * @param mappingContext the facet group mapping context
     * @param translationContext the translation mapping context
     * @param <T> type of vendor implementation
     * @param <FC> type of facet group mapping context
     * @param <TC> type of translation mapping context
     * @return the completely filled in facet group and their facets
     */
    public static <T extends VendorFacetGroup<? extends VendorFacet>, FC extends FacetGroupMappingContext<TC>
            , TC extends TranslationMappingContext> SearchFacetsGroup of(T facetGroup, FC mappingContext, TC translationContext) {
        SearchFacetsGroup searchFacetsGroup = new SearchFacetsGroup();
        searchFacetsGroup.facetGroupId = facetGroup.getGroupLabel();
        searchFacetsGroup.facetTempGroupId = facetGroup.getGroupId();
        searchFacetsGroup.facetValueList = new LinkedHashSet<>();
        FacetConfiguration facetConfiguration = mappingContext.findFacetConfigurationFor(facetGroup.getGroupLabel());
        if (facetConfiguration != null) {
            searchFacetsGroup.gridFacet = facetConfiguration.getShowAsGrid();
            searchFacetsGroup.singleFacetEnabled = facetConfiguration.getSingleFacetEnabled();
            searchFacetsGroup.facetSearchEnabled = facetConfiguration.getFacetSearchEnabled();
        }
        String tagId = facetGroup.getGroupId().replace(":content-type", ":search-tabs_content-type")
                .replace("_", "/");
        searchFacetsGroup.facetGroupLabel = determineFacetGroupLabel(facetGroup, tagId, translationContext, searchFacetsGroup);
        searchFacetsGroup.secure = false;
        searchFacetsGroup.sortOrder = 0;
        facetGroup.getFacets().forEach(facet -> {
            if (searchFacetsGroup.isSecure()){
                searchFacetsGroup.facetValueList.add(SearchFacetValue.translatedLabel(facet, translationContext));
            } else {
                searchFacetsGroup.facetValueList.add(SearchFacetValue.withLocalizedTagLabel(facet, tagId, translationContext));
            }
        });
        return searchFacetsGroup;
    }

    private static <T extends VendorFacetGroup<? extends VendorFacet>> String determineFacetGroupLabel(T facetGroup, String tagId
            , TranslationMappingContext mappingContext, SearchFacetsGroup searchFacetsGroup) {
        if (searchFacetsGroup.isSecure()) {
            return mappingContext.retrieveFromI18n("EndecaLabel." + facetGroup.getGroupLabel());
        }
        return mappingContext.locateTagAndRetrieveTitle(tagId, facetGroup.getGroupLabel());
    }

    public boolean isSecure() {
        return "eaton-secure_attributes".equals(facetGroupId);
    }

    public boolean isContentType() {
        return "content-type".equals(facetGroupId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchFacetsGroup that = (SearchFacetsGroup) o;
        return Objects.equals(facetGroupId, that.facetGroupId) && Objects.equals(facetTempGroupId, that.facetTempGroupId) && Objects.equals(sortOrder, that.sortOrder)
                && Objects.equals(gridFacet, that.gridFacet) && Objects.equals(facetValueList, that.facetValueList)
                && Objects.equals(facetGroupLabel, that.facetGroupLabel) && Objects.equals(singleFacetEnabled, that.singleFacetEnabled)
                && Objects.equals(secure, that.secure) && Objects.equals(facetSearchEnabled, that.facetSearchEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facetGroupId, facetTempGroupId, sortOrder, gridFacet, facetValueList, facetGroupLabel, singleFacetEnabled
                , secure, facetSearchEnabled);
    }

    @Override
    public String toString() {
        return "SearchFacetsGroup{" +
                "facetGroupId='" + facetGroupId + '\'' +
                ", facetTempGroupId='" + facetTempGroupId + '\'' +
                ", sortOrder=" + sortOrder +
                ", gridFacet='" + gridFacet + '\'' +
                ", facetValueList=" + facetValueList +
                ", facetGroupLabel='" + facetGroupLabel + '\'' +
                ", singleFacetEnabled='" + singleFacetEnabled + '\'' +
                ", secure=" + secure +
                ", facetSearchEnabled='" + facetSearchEnabled + '\'' +
                '}';
    }
}
