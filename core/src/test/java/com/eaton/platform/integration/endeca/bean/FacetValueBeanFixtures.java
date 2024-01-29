package com.eaton.platform.integration.endeca.bean;

import static org.junit.jupiter.api.Assertions.*;

class FacetValueBeanFixtures {

    public static FacetValueBean createFacetValue(String facetValueLabel, String facetValueId, int facetValueDocs) {
        FacetValueBean facetValueBean = new FacetValueBean();
        facetValueBean.setFacetValueLabel(facetValueLabel);
        facetValueBean.setFacetValueId(facetValueId);
        facetValueBean.setFacetValueDocs(facetValueDocs);
        return facetValueBean;
    }

}