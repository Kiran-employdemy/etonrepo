package com.eaton.platform.integration.endeca.bean.filters;

import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;

import java.util.List;
import java.util.function.Predicate;

/**
 * Predicate to filter out the FacetGroupBeans that have only one value that is not one of the active filters
 * In the process not filter out the secured filter as that one always has only one value, but we like for that one
 * to be present even if it is not selected.
 */
public class FacetGroupBeanFilter implements Predicate<FacetGroupBean> {
    private final List<FacetBean> activeFacetsList;
    private final Integer grandTotal;

    /**
     * Constructor taking
     * @param grandTotal to verify if a group must be removed, only when the number of docs are equal to the grand total it will be removed
     * @param activeFacetsList the list of active filters
     */
    public FacetGroupBeanFilter(Integer grandTotal, List<FacetBean> activeFacetsList) {
        this.grandTotal = grandTotal;
        this.activeFacetsList = activeFacetsList;
    }

    @Override
    public boolean test(FacetGroupBean facetGroupBean) {
        if(facetGroupBean.getFacetGroupId().equalsIgnoreCase(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL)){
            return true;
        }
        if (facetGroupBean.getFacetValueList().size() > 1) {
            return true;
        }
        if(activeFacetsList != null && !activeFacetsList.isEmpty()){
            if(facetGroupBean.getFacetGroupLabel().contains("content-type")){
                return true;
            }
            return activeFacetsList.stream().anyMatch(facetBean -> facetBean.getFacetID().equals(facetGroupBean.getFacetValueList().get(0).getFacetValueId()));
        }
        return grandTotal != facetGroupBean.getFacetValueList().get(0).getFacetValueDocs();
    }
}
