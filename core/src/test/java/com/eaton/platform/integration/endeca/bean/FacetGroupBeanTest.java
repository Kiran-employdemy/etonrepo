package com.eaton.platform.integration.endeca.bean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
class FacetGroupBeanTest {

    @Test
    @DisplayName("When SortOrder is not null, returns the value")
    void testGetSortOrderNonNull() {
        FacetGroupBean facetGroupBean = new FacetGroupBean();
        facetGroupBean.setSortOrder(1);
        assertEquals(1, facetGroupBean.getSortOrder(), "should be 1");
    }
    @Test
    @DisplayName("When SortOrder is null, returns 0")
    void testGetSortOrderNull() {
        FacetGroupBean facetGroupBean = new FacetGroupBean();
        facetGroupBean.setSortOrder(null);
        assertEquals(0, facetGroupBean.getSortOrder(), "should be 0");
    }
}