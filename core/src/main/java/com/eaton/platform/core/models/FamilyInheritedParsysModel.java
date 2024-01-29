package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.compress.utils.Lists;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FamilyInheritedParsysModel {
    private static final Logger LOG = LoggerFactory.getLogger(FamilyInheritedParsysModel.class);

    private static final String CONTENT_TAB_PATH = "jcr:content/root/responsivegrid/product_tabs/content-tab-1";
    private static final String RESOURCE_TYPE = "eaton/components/product/family-inherited-parsys";
    private static final String FAMILY_INHERITED = "familyInherited";
    private static final String FAMILY = "family";

    @Inject
    protected EndecaRequestService endecaRequestService;

    @Inject
    private Page currentPage;

    @Inject
    protected EndecaService endecaService;

    @Self
    private SlingHttpServletRequest slingRequest;

    @Inject
    private ProductFamilyDetailService productFamilyDetailService;

    private ProductFamilyPIMDetails productFamilyPIMDetails;
    private String path;


    @PostConstruct
    public void init() {
        LOG.debug("Post construct for inherited parsys model, fetching the productFamilyPimDetails if page type SKU.");
        String pageType = getPageType();
        if (CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE.equals(pageType)) {
            String[] selectors = Objects.requireNonNull(slingRequest).getRequestPathInfo().getSelectors();
            EndecaServiceRequestBean endecaRequestBean = Objects.requireNonNull(endecaRequestService).getEndecaRequestBean(currentPage, selectors, null);
            SKUDetailsResponseBean skuDetails = Objects.requireNonNull(endecaService).getSKUDetails(endecaRequestBean);
            productFamilyPIMDetails = Objects.requireNonNull(productFamilyDetailService).getProductFamilyPIMDetailsBean(skuDetails.getSkuDetails(), currentPage);
            String skuIdentifier = selectors[0];
            if (productFamilyPIMDetails != null) {
                LOG.debug("pageType was SKU fetched the productFamilyPIMDetails for SKU with id {}", skuIdentifier);
            } else {
                LOG.error("For SKU with id {} there was an unexpected error, no productFamilyPIMDetails found", skuIdentifier);
            }
        }
    }

    public String getPath() {
        if(path == null) {
            String pageType = getPageType();
            if (CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE.equals(pageType)) {
                path = FAMILY;
            }
            if (CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE.equals(pageType) && productFamilyPIMDetails != null) {
                String productFamilyPath = productFamilyPIMDetails.getProductFamilyAEMPath();
                if (productFamilyPath == null) {
                    LOG.error("unexpected error occurred: there is no productFamilyPath...");
                    path = FAMILY;
                    return path;
                }
                path = findProductFamilyPathAndReturnPathToInheritedParsysThereof(productFamilyPath);
            }
        }
        return path;
    }

    private String findProductFamilyPathAndReturnPathToInheritedParsysThereof(String productFamilyPath) {
        Resource pageResource = Objects.requireNonNull(slingRequest).getResourceResolver().getResource(productFamilyPath);
        if (pageResource != null) {
            Resource contentTabResource = pageResource.getChild(CONTENT_TAB_PATH);
            Optional<Resource> found = Lists.newArrayList(contentTabResource.getChildren().iterator()).stream()
                    .filter(resource -> RESOURCE_TYPE.equals(resource.getResourceType())).findFirst();
            return found.map((Resource resource) -> {
                if (resource.getChild(FAMILY_INHERITED) == null){
                    return FAMILY;
                }
                return resource.getPath().concat(CommonConstants.SLASH_STRING).concat(FAMILY_INHERITED);
            }).orElse(FAMILY);
        }
        return FAMILY;
    }

    private String getPageType() {
        Resource contentResource = Objects.requireNonNull(currentPage).getContentResource();
        return contentResource.getValueMap().get(CommonConstants.PAGE_TYPE, String.class);
    }
}
