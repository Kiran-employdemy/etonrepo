package com.eaton.platform.integration.endeca.bean.filters;

import com.eaton.platform.integration.endeca.bean.FacetBeanFixtures;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBeanFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class FacetGroupBeanFilterTest {

    private static final int GRAND_TOTAL = 20;
    private static final int LESS_THAN_GRAND_TOTAL = 10;

    @Test
    @DisplayName("When activeFacets is null the list of facet Groups is not manipulated if no group has one value.")
    void testFacetGroupBeanListNotNullActiveFacetsNull() {
        FacetGroupBeanFilter facetGroupBeanFilter = new FacetGroupBeanFilter(GRAND_TOTAL, null);
        List<FacetGroupBean> facetGroupListToManipulate = FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilter().stream().filter(facetGroupBeanFilter).collect(Collectors.toList());
        Assertions.assertEquals(FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilter(), facetGroupListToManipulate, "Should be equal.");
    }

    @Test
    @DisplayName("When activeFacets is empty the list of facet Groups is not manipulated if no group has one value.")
    void testActiveFacetListEmpty() {
        FacetGroupBeanFilter facetGroupBeanFilter = new FacetGroupBeanFilter(GRAND_TOTAL, Collections.emptyList());
        List<FacetGroupBean> facetGroupListToManipulate =  FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilter().stream().filter(facetGroupBeanFilter).collect(Collectors.toList());
        Assertions.assertEquals(FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilter(), facetGroupListToManipulate, "Should be equal.");
    }

    @Test
    @DisplayName("When activeFacets is empty and there is one group with only one value with number of docs equal to total, that group is removed.")
    void testActiveFacetListNoActiveOneGroupWithOneFacetNumberOfDocsEqualToGrandTotal() {
        FacetGroupBeanFilter facetGroupBeanFilter = new FacetGroupBeanFilter(GRAND_TOTAL, Collections.emptyList());
        List<FacetGroupBean> facetGroupListToManipulate = FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilterAndMountingGroup(GRAND_TOTAL).stream().filter(facetGroupBeanFilter).collect(Collectors.toList());
        Assertions.assertEquals(FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilter(), facetGroupListToManipulate, "Should be without mounting group.");
    }

    @Test
    @DisplayName("When activeFacets is empty and there is one group with only one value with number of docs is less than total, that group is not removed.")
    void testActiveFacetListNoActiveOneGroupWithOneFacetNumberOfDocsLessThanGrandTotal() {
        FacetGroupBeanFilter facetGroupBeanFilter = new FacetGroupBeanFilter(GRAND_TOTAL, Collections.emptyList());
        List<FacetGroupBean> facetGroupListToManipulate = FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilterAndMountingGroup(LESS_THAN_GRAND_TOTAL).stream().filter(facetGroupBeanFilter).collect(Collectors.toList());
        Assertions.assertEquals(FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilterAndMountingGroup(LESS_THAN_GRAND_TOTAL), facetGroupListToManipulate, "Should be equal.");
    }

    @Test
    @DisplayName("When activeFacets is not empty and a group with one value does not hold that value, that group is removed, nothing else.")
    void testActiveFacetListOneActiveOfGroupWithOneFacet() {
        FacetGroupBeanFilter facetGroupBeanFilter = new FacetGroupBeanFilter(GRAND_TOTAL, FacetBeanFixtures.resourcesFacet());
        List<FacetGroupBean> facetGroupListToManipulate = FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilterAndMountingGroup(GRAND_TOTAL).stream().filter(facetGroupBeanFilter).collect(Collectors.toList());
        Assertions.assertEquals(FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilter(), facetGroupListToManipulate, "Should be without mounting group.");
    }

    @Test
    @DisplayName("When activeFacets is not empty and there is no group with sole value that facet, nothing is removed.")
    void testActiveFacetListOneActive() {
        FacetGroupBeanFilter facetGroupBeanFilter = new FacetGroupBeanFilter(GRAND_TOTAL, FacetBeanFixtures.resourcesFacet());
        List<FacetGroupBean> facetGroupListToManipulate = FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilter().stream().filter(facetGroupBeanFilter).collect(Collectors.toList());
        Assertions.assertEquals(FacetGroupBeanFixtures.coupleOfFacetGroupBeansWithSecureFilter(), facetGroupListToManipulate, "Should be equal.");
    }
}