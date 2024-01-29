package com.eaton.platform.integration.endeca.bean;

import com.eaton.platform.core.bean.FacetBean;

import java.util.ArrayList;
import java.util.List;

public class FacetBeanFixtures {
    public static List<FacetBean> resourcesFacet() {
        ArrayList<FacetBean> facetBeans = new ArrayList<>();
        facetBeans.add(facetBean("968989543", "/content/eaton/us/en-us/site-search.searchTerm$test.tabs$all.html", "Resources"));
        return facetBeans;
    }

    public static List<FacetBean> mountingGroupFacet() {
        ArrayList<FacetBean> facetBeans = new ArrayList<>();
        facetBeans.add(facetBean("3565711030", "/content/eaton/us/en-us/site-search.searchTerm$test.tabs$all.facets$968989543.html", "Horizontal"));
        return facetBeans;
    }

    private static FacetBean facetBean(String facetId, String facetDeselectUrl, String label) {
        FacetBean facetBean = new FacetBean();
        facetBean.setFacetID(facetId);
        facetBean.setFacetDeselectURL(facetDeselectUrl);
        facetBean.setLabel(label);
        return facetBean;
    }
}
