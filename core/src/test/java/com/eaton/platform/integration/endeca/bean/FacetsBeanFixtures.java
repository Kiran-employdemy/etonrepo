package com.eaton.platform.integration.endeca.bean;

import static com.eaton.platform.integration.endeca.bean.FacetGroupBeanFixtures.createFacetGroupBeansWithStatus;

public class FacetsBeanFixtures {

    public static FacetsBean facetsBeanWithSpecificStatusFacetsGroupId(String statusFacetsGroupId) {
        FacetsBean facetsBean = new FacetsBean();
        facetsBean.setFacetGroupList(createFacetGroupBeansWithStatus(statusFacetsGroupId));
        return facetsBean;
    }

}