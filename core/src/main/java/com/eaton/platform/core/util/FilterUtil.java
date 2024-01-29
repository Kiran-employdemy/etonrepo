package com.eaton.platform.core.util;

import com.eaton.platform.integration.endeca.bean.FilterBean;

import java.util.Arrays;
import java.util.List;

public class FilterUtil {

    public static FilterBean getFilterBean(final String key, final String value) {
        final FilterBean filterBean = new FilterBean();
        filterBean.setFilterName(key);
        filterBean.setFilterValues(Arrays.asList(value));
        return filterBean;
    }

    public static FilterBean getFilterBeanList(final String key, final List<String> values) {
        final FilterBean filterBean = new FilterBean();
        filterBean.setFilterName(key);
        filterBean.setFilterValues(values);
        return filterBean;
    }
}
