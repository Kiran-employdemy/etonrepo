package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.SkuCardBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SkuCardXmlParser;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import java.util.Optional;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 
public class UpSellProductsModel{
	private SkuCardXmlParser skuCardXmlParser = new SkuCardXmlParser();
	private List<SkuCardBean> skuSellList = new ArrayList<>();
	private SKUDetailsBean skuDetailsBean = new SKUDetailsBean();
	private static final String GENERIC_IMAGE = "/content/dam/eaton/resources/default-sku-image.jpg";

    /** The familypimobj. */
	@Inject
    private ProductFamilyPIMDetails productFamilyPIMDetails ;

	@Inject
    protected AdminService adminService;
	
	@Inject @Source("sling-object")
    private SlingHttpServletRequest slingRequest;
	
	@Inject
	@ScriptVariable
	private Page currentPage;
		
	@Inject
	protected EatonSiteConfigService eatonSiteConfigService;
	
	@Inject
	protected EndecaRequestService endecaRequestService;
	
	@Inject
	protected EndecaService endecaService;
	
	@Inject
	protected ProductFamilyDetailService productFamilyDetailService;
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(UpSellProductsModel.class);

	@ValueMapValue
	private String viewType;

	@ValueMapValue
	private String view;
	@PostConstruct
	protected void init() {	
		LOG.debug("UpSellProductsModel :: init() :: Start");
		getProductFamilyPIMDetails();
        if (skuDetailsBean != null) {
            Optional<SiteResourceSlingModel> siteConfiguration = eatonSiteConfigService != null ? eatonSiteConfigService.getSiteConfig(currentPage) : Optional.empty();
            int countUpSellValue = siteConfiguration.isPresent() ? siteConfiguration.get().getCountUpSell() : 0;
            if (skuDetailsBean.getUpSell() != null) {
                for (int i = 0; i < countUpSellValue; i++) {
                    skuSellList = skuCardXmlParser.getSkuCardList("upSell", skuDetailsBean, adminService);
                    finalList();
                }

            }
        }
        LOG.debug("UpSellProductsModel :: init() :: Exit");
	}

	public void finalList() {
		if (skuSellList != null && !skuSellList.isEmpty()) {
			for (SkuCardBean item : skuSellList) {
				item.setProductName(item.getProductName());
				item.setProductFamily(item.getProductFamily());
				item.setCatalog(item.getCatalog());
				item.setLink(currentPage.getPath().concat(CommonConstants.PERIOD).concat(item.getCatalog()).concat(CommonConstants.HTML_EXTN));
				String imagePath = item.getImage();
				if(imagePath!=null && !(imagePath.isEmpty())){
				item.setImage(imagePath);
				}else
				{
					item.setImage(GENERIC_IMAGE);	
				}
			}
		}
	}

	public String getHeadlineValue() {
		String more = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.MORE, "More");
		String models = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.MODELS,
				"Models");
        productFamilyPIMDetails = getProductFamilyPIMDetails();
		if(productFamilyPIMDetails.getProductName()!=null && !(productFamilyPIMDetails.getProductName().isEmpty())) {
			String familyName = productFamilyPIMDetails.getProductName();
			return (more + CommonConstants.BLANK_SPACE + familyName + CommonConstants.BLANK_SPACE + models);
		} else if(skuDetailsBean.getFamilyName()!=null) {
			String familyName = skuDetailsBean.getFamilyName();
			return (more + CommonConstants.BLANK_SPACE + familyName + CommonConstants.BLANK_SPACE + models);
		} else {
			return (more + CommonConstants.BLANK_SPACE + models);
		}		
	}

    private ProductFamilyPIMDetails getProductFamilyPIMDetails() {
        if (null != adminService && null != currentPage && null != slingRequest) {
			try (ResourceResolver adminResourceResolver = adminService.getReadService()){
				final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
				final String inventoryId = endecaRequestService.getInventoryId(currentPage, adminResourceResolver);
				final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
                        .getEndecaRequestBean(currentPage, selectors, inventoryId);
				final SKUDetailsResponseBean skuDetailsResponseBean = endecaService.getSKUDetails(endecaServiceRequestBean);
				skuDetailsBean = skuDetailsResponseBean.getSkuResponse().getSkuDetails();
				productFamilyPIMDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);			
			}
		}
        return productFamilyPIMDetails;
    }

	/**
	 * @return the skuSellList
	 */
	public List<SkuCardBean> getSkuSellList() {
		return skuSellList;
	}
	public String getViewType() {
		return viewType;
	}
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}

}
