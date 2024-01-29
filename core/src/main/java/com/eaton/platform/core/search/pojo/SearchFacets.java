package com.eaton.platform.core.search.pojo;

import com.eaton.platform.core.search.api.FacetGroupMappingContext;
import com.eaton.platform.core.search.api.TranslationMappingContext;
import com.eaton.platform.core.search.api.VendorFacet;
import com.eaton.platform.core.search.api.VendorFacetGroup;
import com.eaton.platform.core.search.api.VendorFacetGroups;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * POJO for displaying the list of facet groups
 */
public class SearchFacets {
    private Set<SearchFacetsGroup> facetGroupList;

    /**
     * Factory method taking as arguments:
     *
     * @param facetGroups    the Vendor implementation of Facet groups
     * @param mappingContext the Facet Group mapping context
     * @param <T>            type of Vendor implementation
     * @param <FC>           type of facet context
     * @param <TC>           type of translation context
     * @return a completely filled in SearchFacets
     */
    public static <T extends VendorFacetGroups<? extends VendorFacetGroup<? extends VendorFacet>>
            , FC extends FacetGroupMappingContext<TC>, TC extends TranslationMappingContext> SearchFacets of(T facetGroups, FC mappingContext) {
        SearchFacets searchFacets = new SearchFacets();
        Set<? extends VendorFacetGroup<? extends VendorFacet>> listOfFacetGroups = facetGroups.getFacetGroup();
        if (listOfFacetGroups == null) {
            return searchFacets;
        }
        searchFacets.facetGroupList = new LinkedHashSet<>();
        final AtomicReference<SearchFacetsGroup> secured = new AtomicReference<>();
        final AtomicReference<SearchFacetsGroup> contentType = new AtomicReference<>();
        listOfFacetGroups.forEach(facetGroup -> {
            SearchFacetsGroup searchFacetsGroup = SearchFacetsGroup.of(facetGroup, mappingContext, mappingContext.getTranslationContext());
            checkIfSecured(searchFacetsGroup, secured);
            checkIfContetnType(searchFacetsGroup, contentType);
            if (!searchFacetsGroup.isContentType() && !searchFacetsGroup.isSecure()) {
                searchFacets.facetGroupList.add(searchFacetsGroup);
            }
        });
        return reOrderedIfNeeded(searchFacets, secured, contentType);
    }

    private static void checkIfContetnType(SearchFacetsGroup searchFacetsGroup, final AtomicReference<SearchFacetsGroup> contentType) {
        if (contentType.get() == null && searchFacetsGroup.isContentType()) {
            contentType.set(searchFacetsGroup);
        }
    }

    private static void checkIfSecured(SearchFacetsGroup searchFacetsGroup, final AtomicReference<SearchFacetsGroup> secured) {
        if (secured.get() == null && searchFacetsGroup.isSecure()) {
            secured.set(searchFacetsGroup);
        }
    }

    private static SearchFacets reOrderedIfNeeded(SearchFacets searchFacets, AtomicReference<SearchFacetsGroup> secured, AtomicReference<SearchFacetsGroup> contentType) {
        if (contentType.get() != null || secured.get() != null) {
            Set<SearchFacetsGroup> tempSet = searchFacets.facetGroupList;
            searchFacets.facetGroupList = new LinkedHashSet<>();
            if (secured.get() != null) {
                searchFacets.facetGroupList.add(secured.get());
            }
            if (contentType.get() != null) {
                searchFacets.facetGroupList.add(contentType.get());
            }
            searchFacets.facetGroupList.addAll(tempSet);
        }
        return searchFacets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchFacets that = (SearchFacets) o;
        return Objects.equals(facetGroupList, that.facetGroupList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facetGroupList);
    }

    @Override
    public String toString() {
        return "SearchFacets{" +
                "facetGroupList=" + facetGroupList +
                '}';
    }
}
