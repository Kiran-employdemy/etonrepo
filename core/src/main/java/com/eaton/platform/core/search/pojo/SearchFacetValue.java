package com.eaton.platform.core.search.pojo;

import com.eaton.platform.core.search.api.TranslationMappingContext;
import com.eaton.platform.core.search.api.VendorFacet;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;

import java.util.Objects;

/**
 * POJO for displaying the Facet value with their fields
 */
public class SearchFacetValue {
    private String facetValueId;
    private String facetValueLabel;
    private Long facetValueDocs;
    private String facetSelected;

    /**
     * Factory method to use when we want localized tag titles as labels
     * @param facet vendor implementation of Facet
     * @param parentTagId the id of the parent tag, needed to search for the tag
     * @param mappingContext the facet value mapping context
     * @param <T> type of the vendor implementation
     * @param <TC> type of context for translation
     * @return the completely filled in facet value
     */
    public static <T extends VendorFacet, TC extends TranslationMappingContext>  SearchFacetValue withLocalizedTagLabel(T facet, String parentTagId, TC mappingContext) {
        SearchFacetValue searchFacetValue = new SearchFacetValue();
        searchFacetValue.facetValueLabel = mappingContext.locateTagAndRetrieveTitle(parentTagId + "/" + facet.getLabel(), facet.getLabel());
        return withValueAndDocs(facet, searchFacetValue);
    }

    private static <T extends VendorFacet> SearchFacetValue withValueAndDocs(T facet, SearchFacetValue searchFacetValue) {
        searchFacetValue.facetValueId = facet.getValue();
        searchFacetValue.facetValueDocs = facet.getNumberOfDocs();
        if (facet.getNumberOfDocs() == 0){
            searchFacetValue.facetSelected = EndecaConstants.FACET_VALUE_CHECKED;
        }
        return searchFacetValue;
    }

    /**
     * Factory method to use when we do not want localized tag titles as labels, instead we translate the label
     * @param facet vendor implementation of Facet
     * @param mappingContext the facet value mapping context
     * @param <T> type of the vendor implementation
     * @param <TC> context for translation
     * @return the completely filled in facet value
     */
    public static <T extends VendorFacet, TC extends TranslationMappingContext> SearchFacetValue translatedLabel(T facet, TC mappingContext) {
        SearchFacetValue searchFacetValue = new SearchFacetValue();
        searchFacetValue.facetValueLabel = mappingContext.retrieveFromI18n(facet.getLabel());
        return withValueAndDocs(facet, searchFacetValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        SearchFacetValue that = (SearchFacetValue) o;
        return Objects.equals(facetSelected,that.facetSelected) && Objects.equals(facetValueId, that.facetValueId) && Objects.equals(facetValueLabel, that.facetValueLabel)
                && Objects.equals(facetValueDocs, that.facetValueDocs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facetValueId, facetValueLabel, facetValueDocs, facetSelected);
    }

    @Override
    public String toString() {
        return "SearchFacetValue{" +
                "facetValueId='" + facetValueId + '\'' +
                ", facetValueLabel='" + facetValueLabel + '\'' +
                ", facetValueDocs=" + facetValueDocs +
                '}';
    }
}
