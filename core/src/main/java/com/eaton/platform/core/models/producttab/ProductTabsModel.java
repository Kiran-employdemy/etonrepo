package com.eaton.platform.core.models.producttab;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.HowToBuyBean;
import com.eaton.platform.core.bean.ProductDetailsTabsBean;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.Tabsbean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.familymodule.FamilyModuleBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductTabsModel {

	private static final Logger LOG = LoggerFactory.getLogger(ProductTabsModel.class);

	private static final String HTML_CONSTANT = ".html";
	private static final String VIEW = "view";
	private static final String LINKS_NAV = "links-navigation";
	private static final String TOGGLE = "toggle-graphic";
	public static final String SUPPORT_TAB = "Support";
	private ProductDetailsTabsBean productDetailsTabsBean = new ProductDetailsTabsBean();
	private List<Tabsbean> tabList = new ArrayList<>();
	private List<HowToBuyBean> howToBuylist = new ArrayList<>();
	private String pageType;
	private String modulesTabURL;
	private String modulesTabLabel;
	private String overviewTabLabel;
	private String resourceTabLabel;
	private String supportTabLabel;
	private String ModelLabel;
	private String ModelLabelFromPIM;
	protected SiteResourceSlingModel siteConfiguration;
	protected ProductFamilyPIMDetails productFamilyPIMDetails;

	@OSGiService
	private ProductFamilyDetailService productFamilyDetailService;

	@EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;

	@OSGiService
	private AdminService adminService;

	@OSGiService
	private EndecaRequestService endecaRequestService;

	@OSGiService
	private EndecaService endecaService;

	@Inject
	private Page currentPage;

	@Inject
	private SlingHttpServletRequest slingRequest;

	@Inject
	private Resource resource;


	@PostConstruct
	public void init() {
		try (ResourceResolver resourceResolver = adminService.getReadService()) {
			final ProductFamilyDetails productFamilyDetailsBean = productFamilyDetailService
					.getProductFamilyDetailsBean(currentPage);
			final String inventoryId = Optional.ofNullable(endecaRequestService.getInventoryId(currentPage,
					resourceResolver)).orElse(StringUtils.EMPTY);
			final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
			final EndecaServiceRequestBean endecaRequestBean = endecaRequestService.getEndecaRequestBean(currentPage,
					selectors, inventoryId);
			SKUListResponseBean skuListResponseBean = null;
			if (StringUtils.isNotBlank(inventoryId)) {
				skuListResponseBean = endecaService.getSKUList(endecaRequestBean);
			}
			if ((null != productFamilyDetailsBean && null != resource) && siteResourceSlingModel.isPresent()) {
				setComponentValues(productFamilyDetailsBean, resource, siteResourceSlingModel.get(),
						skuListResponseBean);
			}
		}
	}
	
	public void setComponentValues(final ProductFamilyDetails productFamilyDetails, final Resource currentResource,
								   final SiteResourceSlingModel siteConfiguration,
								   final SKUListResponseBean skuListResponseBean) {
		ValueMap valueMap = currentResource.getValueMap();
		ResourceResolver resourceResolver = resource.getResourceResolver();
		try(ResourceResolver adminResourceResolver = adminService.getReadService()) {

			if ((valueMap != null && null != resourceResolver) && (valueMap.get(VIEW).toString() != null && !(valueMap.get(VIEW).toString().isEmpty()))) {
				pageType = valueMap.get(VIEW).toString();
				if (pageType.equals(LINKS_NAV)) {
					productDetailsTabsBean.setGraphicToggle(true);
					productDetailsTabsBean.setUseDarktheme(true);
				} else if (pageType.equals(TOGGLE)) {
					productDetailsTabsBean.setGraphicToggle(true);
					productDetailsTabsBean.setUseDarktheme(false);
				}
			}

			// Getting attributes from Family Node in PIM
			if (productFamilyDetails != null) {
				// Setting EyeBrow Details
				// productFamilyDetails.getP
				if ((productFamilyDetails.getPrimarySubCategory() != null)) {

					if (!productFamilyDetails.getPrimarySubCategory().isEmpty()) {

						String categoryPageTitle = CommonUtil.getLinkTitle(null, productFamilyDetails.getPrimarySubCategory(),
								resourceResolver);
						productDetailsTabsBean.setEyebrowLink(CommonUtil.dotHtmlLink(productFamilyDetails.getPrimarySubCategory()));
						productDetailsTabsBean.setEyebrowtitle(categoryPageTitle);
					}

				} else {
					productDetailsTabsBean.setEyebrowLink(StringUtils.EMPTY);
					productDetailsTabsBean.setEyebrowtitle(StringUtils.EMPTY);
				}

				// Setting Product Name
				if ((productFamilyDetails.getProductFamilyName() != null) && (!productFamilyDetails.getProductFamilyName().isEmpty())) {
					productDetailsTabsBean.setProductName(productFamilyDetails.getProductFamilyName());
				}

				populateMiddleTab(productFamilyDetails);

				// Setting How to Buy options
				if ((productFamilyDetails.getHowToBuyList() != null)
						&& (!productFamilyDetails.getHowToBuyList().isEmpty())) {
					howToBuylist = productFamilyDetails.getHowToBuyList();
					if (howToBuylist.size() == 1) {
						HowToBuyBean howToBuyBean = howToBuylist.get(0);
						if (howToBuyBean != null) {
							if ((howToBuyBean.getTitle() != null) && (!howToBuyBean.getTitle().equals(""))) {
								productDetailsTabsBean.setHowToBuyLabel(howToBuyBean.getTitle());
							}
							if ((howToBuyBean.getLink() != null) && (!howToBuyBean.getLink().equals(""))) {
								productDetailsTabsBean.setHowToBuyLink(howToBuyBean.getLink());
							}

							productDetailsTabsBean.setHowToBuytarget(howToBuyBean.getOpenInNewWindow());
							productDetailsTabsBean.setModalEnabled(howToBuyBean.isModalEnabled());
							productDetailsTabsBean.setSuffixEnabled(howToBuyBean.isSuffixEnabled());
							productDetailsTabsBean.setSourceTrackingEnabled(howToBuyBean.isSourceTrackingEnabled());
							productDetailsTabsBean.setHowToBuyOptions(null);
						}

					} else {
						String howtoBuyLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.HOW_TO_BUY_LABEL, CommonConstants.HOW_TO_BUY_LABEL_DEFAULT);

						productDetailsTabsBean.setHowToBuyLabel(howtoBuyLabel);
						productDetailsTabsBean.setHowToBuyLink(null);
						productDetailsTabsBean.setHowToBuytarget(null);
						productDetailsTabsBean.setHowToBuyOptions(CommonUtil.filterSkuHowToBuyOptions(productFamilyDetails.getHowToBuyList(), false));
					}

					if (null != currentPage) {
						String mappedPagePath = adminResourceResolver.map(currentPage.getPath());
						if (StringUtils.isNotEmpty(mappedPagePath)) {
							mappedPagePath = CommonUtil.removeSiteContentRootPathPrefix(mappedPagePath);
							productDetailsTabsBean.setExternalMappedCurrentPagePath(mappedPagePath);
						}
					}
				}
			}
			if ((skuListResponseBean != null) && (productDetailsTabsBean.getProductName() == null && skuListResponseBean.getFamilyModuleResponse().getFamilyModule() != null)) {
				FamilyModuleBean familyModuleBean = skuListResponseBean.getFamilyModuleResponse().getFamilyModule().get(0);
				LOG.debug("Endeca SKU List Response:{} ", skuListResponseBean.getFamilyModuleResponse());
				productDetailsTabsBean.setProductName(familyModuleBean.getFamilyName());
			}

			if ((siteConfiguration != null) && (productDetailsTabsBean.getHowToBuyLabel() == null)) {
				String howtoBuyLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.HOW_TO_BUY_LABEL, CommonConstants.HOW_TO_BUY_LABEL_DEFAULT);

				productDetailsTabsBean.setHowToBuyLabel(howtoBuyLabel);
				if (siteConfiguration.getDefaultLinkHTB() != null) {
					productDetailsTabsBean.setHowToBuyLink(siteConfiguration.getDefaultLinkHTB());
				}

				productDetailsTabsBean.setHowToBuytarget(CommonConstants.TARGET_SELF);
				productDetailsTabsBean.setHowToBuyOptions(null);
			}


			// Setting Tab Details
			if (currentPage != null) {
				String currentPagePath = currentPage.getPath();
				String overviewTabURL = currentPagePath.concat(HTML_CONSTANT);
				overviewTabLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
						CommonConstants.OVERVIEW_TAB, CommonConstants.OVERVIEW_TAB);


				if (ModelLabel != null) {
					modulesTabURL = currentPagePath.concat(".").concat(ModelLabelFromPIM.toLowerCase()).concat(HTML_CONSTANT);
					modulesTabLabel = ModelLabel;
				} else {
					modulesTabURL = currentPagePath.concat(".").concat(CommonConstants.MODULES_TAB_SELECTOR)
							.concat(HTML_CONSTANT);
					modulesTabLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
							CommonConstants.MODULES_TAB, CommonConstants.MODULES_TAB);
				}

				String resourcesTabURL = currentPagePath.concat(".").concat(CommonConstants.RESOURCES_TAB_SELECTOR)
						.concat(HTML_CONSTANT);
				resourceTabLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
						CommonConstants.RESOURCES_TAB, CommonConstants.RESOURCES_TAB);

				String supportTabURL = currentPagePath.concat(".").concat(HTML_CONSTANT);
				supportTabLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
						SUPPORT_TAB, SUPPORT_TAB);

				setTabsValue(overviewTabURL, modulesTabURL, resourcesTabURL, supportTabURL);
			}
		}
	}

	private void populateMiddleTab(ProductFamilyDetails productFamilyDetails) {
		//middleTabValue
		ModelLabelFromPIM = productFamilyDetails.getModelTabName();
		if(ModelLabelFromPIM.equals("Services"))
		{
			ModelLabel = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,ModelLabelFromPIM,"Services");
		}
		else if (ModelLabelFromPIM.equals("Models"))
		{
			ModelLabel = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,ModelLabelFromPIM,"Models");
		}
		else if (ModelLabelFromPIM.equals("Specifications"))
		{
			ModelLabel = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,ModelLabelFromPIM,"Specifications");
		}
		else if (ModelLabelFromPIM.equals("Accessories"))
		{
			ModelLabel = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,ModelLabelFromPIM,"Accessories");
		}
		else if (ModelLabelFromPIM.equals("Configure"))
		{
			ModelLabel = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,ModelLabelFromPIM,"Configure");
		}
		else if (ModelLabelFromPIM.equals("Design"))
		{
			ModelLabel = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,ModelLabelFromPIM,"Design");
		}
		else if (ModelLabelFromPIM.equals("Options"))
		{
			ModelLabel = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,ModelLabelFromPIM,"Options");
		}
		else if (ModelLabelFromPIM.equals("Technical"))
		{
			ModelLabel = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,ModelLabelFromPIM,"Technical");
		}
		else if (ModelLabelFromPIM.equals("Use_cases"))
		{
			ModelLabel = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,ModelLabelFromPIM,"Use cases");
		}
	}
	private void setTabsValue(String overviewTabURL, String modulesTabURL, String resourcesTabURL, String supportTabURL) {

		Tabsbean overviewTab = new Tabsbean();
		overviewTab.setIcons(CommonConstants.OVERVIEW_TAB_ICON);
		overviewTab.setTitle(overviewTabLabel);
		overviewTab.setHref(overviewTabURL);
		overviewTab.setSelected(false);

		Tabsbean sepcificationsTab = new Tabsbean();
		sepcificationsTab.setIcons(CommonConstants.MODEL_TAB_ICON);
		sepcificationsTab.setTitle(modulesTabLabel);
		sepcificationsTab.setHref(modulesTabURL);
		sepcificationsTab.setSelected(false);

		Tabsbean resourcesTab = new Tabsbean();
		resourcesTab.setIcons(CommonConstants.RESOURCES_TAB_ICON);
		resourcesTab.setTitle(resourceTabLabel);
		resourcesTab.setHref(resourcesTabURL);
		resourcesTab.setSelected(false);

		Tabsbean supportTab = new Tabsbean();
		supportTab.setTitle(supportTabLabel);
		supportTab.setHref(supportTabURL);
		supportTab.setSelected(false);

		String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
		
		if (selectors.length != 0) {

			String selectorItem = selectors[0];

			if (selectorItem.equals(ModelLabelFromPIM.toLowerCase())) {
				sepcificationsTab.setSelected(true);
				productDetailsTabsBean.setIntraTabTitle(CommonUtil.getI18NFromResourceBundle(slingRequest,
						currentPage, CommonConstants.RESOURCES_TAB));
				productDetailsTabsBean.setIntraTabHref(resourcesTabURL);
				productDetailsTabsBean.setIntraTabTarget(CommonConstants.TARGET_SELF);
				productDetailsTabsBean.setShowMiddleTab(true);
			} else if (selectorItem.equals(CommonConstants.RESOURCES_TAB_SELECTOR)) {
				resourcesTab.setSelected(true);
				productDetailsTabsBean.setIntraTabTitle(StringUtils.EMPTY);
				productDetailsTabsBean.setIntraTabHref(StringUtils.EMPTY);
				productDetailsTabsBean.setIntraTabTarget(CommonConstants.TARGET_SELF);
				productDetailsTabsBean.setShowResourcesTab(true);
			} else {
				overviewTab.setSelected(true);
				productDetailsTabsBean.setIntraTabTitle(modulesTabLabel);
				productDetailsTabsBean.setIntraTabHref(modulesTabURL);
				productDetailsTabsBean.setIntraTabTarget(CommonConstants.TARGET_SELF);
				productDetailsTabsBean.setShowOverviewTab(true);
			}

		} else {
			overviewTab.setSelected(true);
			productDetailsTabsBean.setIntraTabTitle(modulesTabLabel);
			productDetailsTabsBean.setIntraTabHref(modulesTabURL);
			productDetailsTabsBean.setIntraTabTarget(CommonConstants.TARGET_SELF);
			productDetailsTabsBean.setShowOverviewTab(true);
		}
		tabList.add(overviewTab);
		tabList.add(sepcificationsTab);
		tabList.add(resourcesTab);
		tabList.add(supportTab);

		productDetailsTabsBean.setTabslist(tabList);
	}

	/**
	 * @return the productDetailsTabsBean
	 */
	public ProductDetailsTabsBean getProductDetailsTabsBean() {
		return productDetailsTabsBean;
	}

}