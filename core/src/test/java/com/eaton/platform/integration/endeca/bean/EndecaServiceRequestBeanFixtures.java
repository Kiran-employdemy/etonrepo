package com.eaton.platform.integration.endeca.bean;

import java.util.ArrayList;
import java.util.List;

public class EndecaServiceRequestBeanFixtures {

    public static EndecaServiceRequestBean eatonPdh2SearchRequestWithFiltersAndStatusFilter() {
        EndecaServiceRequestBean endecaRequestBean = eatonPdh2SearchRequestWithFilters();
        endecaRequestBean.getFilters().add(FilterBeanFixtures.filterBean("Facets", "3562001493"));
        return endecaRequestBean;
    }

    public static EndecaServiceRequestBean eatonPdh2SearchRequestWithFilters() {
        EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
        endecaServiceRequestBean.setSearchApplication("eatonpdh2");
        endecaServiceRequestBean.setSearchApplicationKey("abc123");
        endecaServiceRequestBean.setFunction("seach");
        endecaServiceRequestBean.setSearchTerms("3426938");
        endecaServiceRequestBean.setLanguage("en_US");
        endecaServiceRequestBean.setStartingRecordNumber("0");
        endecaServiceRequestBean.setNumberOfRecordsToReturn("60");
        endecaServiceRequestBean.setFilters(createFilters());
        return endecaServiceRequestBean;
    }

    public static List<FilterBean> createFilters() {
        ArrayList<FilterBean> filterBeans = new ArrayList<>();
        filterBeans.add(FilterBeanFixtures.filterBean("Country", "US"));
        filterBeans.add(FilterBeanFixtures.filterBean("ReturnFacetsFor", "Input_Voltage", "Form_Factor", "Mounting_Direction", "Input_Connection", "Outlets", "Status"));
        filterBeans.add(FilterBeanFixtures.filterBean("SortBy", "asc"));
        filterBeans.add(FilterBeanFixtures.filterBean("Facets"));
        filterBeans.add(FilterBeanFixtures.filterBean("Product_Type", "Rack PDU"));
        filterBeans.add(FilterBeanFixtures.filterBean("SKUCardParameters"));
        return filterBeans;
    }


}