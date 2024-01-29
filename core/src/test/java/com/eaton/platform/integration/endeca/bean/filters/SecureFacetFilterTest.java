package com.eaton.platform.integration.endeca.bean.filters;

import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBeanFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.eaton.platform.integration.endeca.bean.FacetGroupBeanFixtures.*;
import static com.eaton.platform.integration.endeca.bean.FamilyListResponseBeanFixtures.createResponseFixtureWithTotalCount;

class SecureFacetFilterTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    @DisplayName("filter throws IllegalStateException if responseBean is null , not allowed")
    void testFilterOutSecureFacetIfNeededNullResponseBean() {
        Assertions.assertThrows(IllegalStateException.class
                , () -> createWithoutSecureFacetFixtureSingle().stream().filter(new SecureFacetFilter(null)).collect(Collectors.toList())
                , "should throw an IllegalStateException");
    }

    @Test
    @DisplayName("filterOutSecureFacetIfNeeded with an empty FacetGroupBean list yields an empty list")
    void testFilterOutSecureFacetIfNeededEmptyFacetGroupBeanList() {
        Assertions.assertEquals(Collections.emptyList()
                , Stream.<FacetGroupBean>empty().filter(new SecureFacetFilter(createResponseFixtureWithTotalCount(10))).collect(Collectors.toList())
                , "should yield an empty list");
    }

    @Test
    @DisplayName("filterOutSecureFacetIfNeeded with a FacetGroupBean single element list without secure asset facet yields the same list")
    void testFilterOutSecureFacetIfNeededNoSecureFacetSingleList() {
        Assertions.assertEquals(createWithoutSecureFacetFixtureSingle()
                , createWithoutSecureFacetFixtureSingle().stream().filter(new SecureFacetFilter(createResponseFixtureWithTotalCount(10))).collect(Collectors.toList())
                , "should yield the same list as input");
    }

    @Test
    @DisplayName("filterOutSecureFacetIfNeeded with a FacetGroupBean multiple element list without secure asset facet yields the same list")
    void testFilterOutSecureFacetIfNeededNoSecureFacetMultipleList() {
        Assertions.assertEquals(createWithoutSecureFacetFixtureMulti()
                , createWithoutSecureFacetFixtureMulti().stream().filter(new SecureFacetFilter(createResponseFixtureWithTotalCount(10))).collect(Collectors.toList())
                , "should yield the same list as input");
    }

    @Test
    @DisplayName("filterOutSecureFacetIfNeeded when the count in secure facet is not the same as the total count in response yields the same list")
    void testFilterOutSecureFacetIfNeededSecureFacetMultipleList() {
        Assertions.assertEquals(createWithSecureFacetFixtureMultiWithCount(5)
                , createWithSecureFacetFixtureMultiWithCount(5).stream().filter(new SecureFacetFilter(createResponseFixtureWithTotalCount(10))).collect(Collectors.toList())
                , "should yield the same list as input");
    }

    @Test
    @DisplayName("filterOutSecureFacetIfNeeded when in a single list the filter is secure facet and the count is the same as total count of response, the list will be empty")
    void testFilterOutSecureFacetIfNeededSecureFacetSameCount() {
        Assertions.assertEquals(Collections.emptyList()
                , createSingleSecureFacetFixtureWithCount(5).stream().filter(new SecureFacetFilter(createResponseFixtureWithTotalCount(5))).collect(Collectors.toList())
                , "should yield an empty list");
    }
    @Test
    void testTestMethodTrue() {
        Assertions.assertTrue(new SecureFacetFilter(createResponseFixtureWithTotalCount(10)).test(FacetGroupBeanFixtures.createFacetGroup("blah")), "should be true");
    }
    @Test
    void testTestMethodFalse() {
        Assertions.assertFalse(new SecureFacetFilter(createResponseFixtureWithTotalCount(10)).test(FacetGroupBeanFixtures.createSecureFacetWithCount(10)), "should be false");
    }
}