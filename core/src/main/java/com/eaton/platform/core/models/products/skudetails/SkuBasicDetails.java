package com.eaton.platform.core.models.products.skudetails;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.AttrNmBean;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SkuUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.integration.endeca.util.EndecaUtil;

/**
 * GET SKU Details from ENDECA
 */

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SkuBasicDetails {

    private static final Logger LOG = LoggerFactory.getLogger(SkuBasicDetails.class);


    @Inject
	private Page currentPage;

    @OSGiService
	private EndecaRequestService endecaRequestService;

	@OSGiService
	private EndecaService endecaService;

    private String skuTitle;

    private String skuDescription;

    private String skuId;

    private String extensionId;

    private String inventoryId;

    private SKUDetailsResponseBean skuDetails;

    private Map<String, Map<String, AttrNmBean>> skuImages;

    @OSGiService
    private ProductFamilyDetailService productFamilyDetailService;
    
    @Inject
	protected AdminService adminService;

    private ProductFamilyPIMDetails productFamilyPIMDetails;

    @SlingObject
    private SlingHttpServletRequest slingRequest;

    /**
     * GET SKU Details from ENDECA
     */
    @PostConstruct
	public void init() {
        String[] selectors = getCatalogNumber();
        if(selectors.length > 0) {
            final ValueMap valueMap = currentPage.getContentResource().getValueMap();
            String pageType = valueMap.get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
            LOG.debug("SKU Id : {}", selectors[0]);
            final EndecaServiceRequestBean endecaRequestBean = endecaRequestService.getEndecaRequestBean(currentPage, selectors, StringUtils.EMPTY);
            EndecaUtil.resetLanguageAndCountryFilters(endecaRequestBean, currentPage);
            skuDetails = endecaService.getSKUDetails(endecaRequestBean);
            if(null != skuDetails.getSkuResponse().getSkuDetails()){
                skuTitle = skuDetails.getSkuResponse().getSkuDetails().getFamilyName();
                skuDescription = skuDetails.getSkuResponse().getSkuDetails().getMktgDesc();
                extensionId = skuDetails.getSkuResponse().getSkuDetails().getExtensionId();
                LOG.debug("Extension Id : {}", extensionId);
                inventoryId = skuDetails.getSkuResponse().getSkuDetails().getInventoryId();
                LOG.debug("Inventory Id : {}", inventoryId);
                skuImages = SkuUtil.getSkuImages(skuDetails.getSkuResponse().getSkuDetails(), adminService);
                productFamilyPIMDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetails.getSkuResponse().getSkuDetails(),currentPage);
                if(productFamilyPIMDetails.getSecondaryLinksList() != null){
                    productFamilyPIMDetails.setSecondaryLinksList(CommonUtil.replaceTokenForSecondaryLinkOptions((productFamilyPIMDetails.getSecondaryLinksList()), pageType, selectors[0]));
                    productFamilyPIMDetails.getSecondaryLinksList().forEach(item -> {
                        if (CommonUtil.getIsExternal(item.getPath())) {
                            item.setNewWindow(CommonConstants.BLANK_CONSTANT);
                            item.setIsExternal(true);
                        }
                    });
                }
                skuId = selectors[0];
            }
        }
    }
    
    /**
     * 
     * @return catalogNumber from URL or from the Dialog if present
     */
    private String[] getCatalogNumber(){
        if(slingRequest != null) {
            String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
            if(selectors.length >0){
                return selectors;
            }
        }
        return new String[]{};
    }

    public String getSkuTitle() {
        return skuTitle;
    }

    public String getSkuDescription() {
        return skuDescription;
    }

    public String getSkuId() {
        return skuId;
    }

    public String getExtensionId() {
        return extensionId;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public SKUDetailsResponseBean getSkuDetails() {
        return skuDetails;
    }

    public Map<String, Map<String, AttrNmBean>> getSkuImages() {
        return skuImages;
    }

    public ProductFamilyPIMDetails getProductFamilyPIMDetails() {
        return productFamilyPIMDetails;
    }   

    
}
