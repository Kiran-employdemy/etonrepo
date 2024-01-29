package com.eaton.platform.core.models.productgrid;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.FilterUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.FacetsBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Helper class to help entangle the ProductGridModel
 */
public class ProductStatusFacetHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ProductStatusFacetHelper.class);
    private static final String CHECKED = "checked";
    private static final String RADIOS = "radios";
    private static final String MODELS = "models";
    private static final String FACETS = "Facets";
    public static final String FACETS_SELECTOR = "facets";

    private final EndecaServiceRequestBean endecaServiceRequestBean;
    private final SlingHttpServletRequest slingHttpServletRequest;

    private final Resource resource;

    private final EndecaService endecaService;

    private final FacetsBean allReturnedFacetsBean;

    /**
     * Constructor taking as parameters
     * @param endecaServiceRequestBean the request that is needed for calling Endeca a second time to fetch with filter Facets to valueId active
     * @param slingHttpServletRequest the slingRequest method to fetch the selectors from
     * @param resource to use to call to endecaService
     * @param endecaService the service to call to Endeca
     * @param allReturnedFacetsBean the facets that is needed to fetch the status Facets valueId of active from
     */
    public ProductStatusFacetHelper(EndecaServiceRequestBean endecaServiceRequestBean, SlingHttpServletRequest slingHttpServletRequest,
                                    Resource resource, EndecaService endecaService, FacetsBean allReturnedFacetsBean) {
        this.endecaServiceRequestBean = endecaServiceRequestBean;
        this.slingHttpServletRequest = slingHttpServletRequest;
        this.resource = resource;
        this.endecaService = endecaService;
        this.allReturnedFacetsBean = allReturnedFacetsBean;
    }

    /**
     * Calls endeca with status filter when models or models and cache are in selectors
     * @param facetGroupList I don't know... needed for executing some logic to delete the status facet from ( no Idea why that is for )
     * @param pageType yeah...
     * @return null if no call to endeca has been made, the response from endeca if it did
     */
    public SKUListResponseBean callEndecaWithStatusFilterWhenModelsInSelectors(List<FacetGroupBean> facetGroupList, String pageType) {
        FacetGroupBean statusfacetGroup = findStatusFacetGroupBeanAndSetInputTypeToRadiosPutCheckedOnValueActiveAndReturnIt(pageType);
        if (statusfacetGroup != null) {

            FacetGroupBean deleteStatusBean = new FacetGroupBean();
            for (FacetGroupBean checkStatusBean : facetGroupList) {
                if (checkStatusBean != null && checkStatusBean.getFacetGroupId() != null) {
                    LOG.debug("ProductGridModel : checkStatusBean is not null");
                    if (checkStatusBean.getFacetGroupId().equalsIgnoreCase(CommonConstants.PRODUCT_STATUS) || checkStatusBean.getFacetGroupId().equalsIgnoreCase(CommonConstants.CWC_PRODUCT_STATUS)) {
                        deleteStatusBean = checkStatusBean;
                        break;
                    }
                }
            }
            facetGroupList.remove(deleteStatusBean);
            facetGroupList.add(0, statusfacetGroup);
            final String[] selectorArray = slingHttpServletRequest.getRequestPathInfo()
                    .getSelectors();
            if (validateNeedOfExtraCallToOnlyShowActive(selectorArray)) {
                String statusFilter = statusfacetGroup.getFacetValueList().stream().filter(facetValue -> facetValue.getFacetValueLabel().equals(CommonConstants.STATUS_ACTIVE))
                        .findFirst().map(FacetValueBean::getFacetValueId).orElse(null);
                if (statusFilter != null) {
                    endecaServiceRequestBean.getFilters()
                            .add(FilterUtil.getFilterBean(FACETS, statusFilter));
                    return endecaService
                            .getSKUList(endecaServiceRequestBean, resource);
                }
            }
        }
        return null;
    }

    private static boolean validateNeedOfExtraCallToOnlyShowActive(String[] selectorArray) {
        List<String> selectorList = Arrays.asList(selectorArray);
        if (selectorList.isEmpty()) {
            return false;
        }
        if (selectorList.get(0).equals(MODELS)){
            return selectorList.stream().noneMatch(selector -> selector.contains(FACETS_SELECTOR));
        }
        return false;
    }

    private FacetGroupBean findStatusFacetGroupBeanAndSetInputTypeToRadiosPutCheckedOnValueActiveAndReturnIt(String pageType) {
        LOG.debug("ProductGridModel : This is Entry into populateStatusFacetOption() method");
        boolean showStatusFacet = true;
        String facetStatus;
        if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE)) {
            facetStatus = CommonConstants.CWC_PRODUCT_STATUS;
        } else {
            facetStatus = CommonConstants.PRODUCT_STATUS;
        }

        if (allReturnedFacetsBean != null) {
            final List<FacetGroupBean> facetList = allReturnedFacetsBean.getFacetGroupList();
            FacetGroupBean statusFacetGroupBean = facetList.stream().filter(facetGroupBean -> facetGroupBean.getFacetGroupId().equals(facetStatus)).findFirst().orElse(null);
            if (statusFacetGroupBean != null) {
                statusFacetGroupBean.setInputType(RADIOS);
                statusFacetGroupBean.setSingleFacetEnabled(showStatusFacet);
                statusFacetGroupBean.setFacetGroupLabel(CommonConstants.STATUS_LABEL);
                statusFacetGroupBean.getFacetValueList().stream()
                        .filter(facetValue -> facetValue.getFacetValueLabel().equals(CommonConstants.STATUS_ACTIVE))
                        .findFirst().ifPresent(active -> active.setActiveRadioButton(CHECKED));
            }
            return statusFacetGroupBean;
        }
        return null;
    }
}
