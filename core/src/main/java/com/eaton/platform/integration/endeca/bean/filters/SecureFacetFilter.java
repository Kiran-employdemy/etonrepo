package com.eaton.platform.integration.endeca.bean.filters;

import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;

import java.util.function.Predicate;

import static com.eaton.platform.integration.endeca.constants.EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL;

/**
 * Predicate that is used to filter out the Secure FacetGroupBean when it is the only one and the count of that facet
 * is equal to the total count of the records in FamilyListResponseBean
 */
public class SecureFacetFilter implements Predicate<FacetGroupBean> {

    private final FamilyListResponseBean familyListResponseBean;

    /**
     * Constructor taking as parameter:
     * @param familyListResponseBean to use for evaluation if the facet has to be filtered out
     */
    public SecureFacetFilter(FamilyListResponseBean familyListResponseBean) {
        this.familyListResponseBean = familyListResponseBean;
    }

    @Override
    public boolean test(FacetGroupBean facetGroupBean) {
        if (familyListResponseBean == null) {
            throw new IllegalStateException("At this point you need to check your code, facetGroupBeans list must not be null");
        }

        if (!facetGroupBean.getFacetGroupLabel().equals(EATON_SECURE_FACET_GROUP_LABEL)) {
            return true;
        } else {
            return facetGroupBean.getFacetValueList().get(0).getFacetValueDocs() != familyListResponseBean.getResponse().getTotalCount();
        }
    }
}
