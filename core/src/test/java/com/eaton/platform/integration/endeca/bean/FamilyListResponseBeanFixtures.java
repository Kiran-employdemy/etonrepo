package com.eaton.platform.integration.endeca.bean;

import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyResponseBean;

public class FamilyListResponseBeanFixtures {

    public static FamilyListResponseBean createResponseFixtureWithTotalCount(int count) {
        FamilyListResponseBean familyListResponseBean = new FamilyListResponseBean();
        familyListResponseBean.setResponse(createResponse(count));
        return familyListResponseBean;
    }

    public static FamilyResponseBean createResponse(int count) {
        FamilyResponseBean familyResponseBean = new FamilyResponseBean();
        familyResponseBean.setTotalCount(count);
        return familyResponseBean;
    }
}
