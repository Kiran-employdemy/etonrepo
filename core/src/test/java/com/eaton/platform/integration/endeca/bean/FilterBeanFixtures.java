package com.eaton.platform.integration.endeca.bean;

import java.util.Arrays;

public class FilterBeanFixtures {

    public static FilterBean filterBean(String name, String... values) {
        FilterBean filterBean = new FilterBean();
        filterBean.setFilterName(name);
        filterBean.setFilterValues(Arrays.asList(values));
        return filterBean;
    }

}
