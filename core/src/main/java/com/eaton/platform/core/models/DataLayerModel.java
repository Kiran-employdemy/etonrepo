package com.eaton.platform.core.models;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.DataLayerBean;
import com.eaton.platform.integration.auth.models.Eatoncustsite;
import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.FacetURLBeanService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DataLayerModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DataLayerModel.class);

	private AuthenticationToken authenticationToken;

	/** The web. */
	private static String location = "web";

	/** The product grid selectors. */
	@Inject
	private ProductGridSelectors productGridSelectors;

	/** The site search response. */
	@Inject
	private SiteSearchResponse siteSearchResponse;

	/** The ProductFamilyDetails. */
	@Inject
	private ProductFamilyDetails productfamilydetails;

	/** The ProductFamilyPIMDetails. */
	@Inject
	private ProductFamilyPIMDetails familypimobj;

	/** The SKUDetailsBean. */
	@Inject
	private SKUDetailsBean skubean;

	/** The facet group list. */

	private List<FacetGroupBean> facetGroupList;

	/** The SKU list response bean. */
	@Inject
	private SKUListResponseBean skuListResponseBean;

	/** The DataLayerBean. */
	@Inject
	private DataLayerBean datalayerbean;

	@Inject
	private Eatoncustsite eatoncustsite;

	@Inject
	@ScriptVariable
	private Page currentPage;

	@Inject
	private AuthorizationService authorizationService;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	/** The AdminService. */
	@Inject
	private AdminService adminService;

	/** The PageManager. */
	@Inject
	private PageManager pageManager;

	@Inject
	private EatonSiteConfigService eatonSiteConfigService;

	/** The SlingHttpServletRequest */
	@Inject
	@Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	/** The FacetURLBeanService */
	@Inject
	private FacetURLBeanService facetURLBeanService;

	/** The Externalizer */
	@Inject
	private Externalizer externalizer;

	/** The EndecaRequestService */
	@Inject
	private EndecaRequestService endecaRequestService;

	/** The EndecaService */
	@Inject
	private EndecaService endecaService;

	/** The ProductFamilyDetailService */
	@Inject
	private ProductFamilyDetailService productFamilyDetailService;

	String pageType=" ";

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private String dataLayerJson;

	@PostConstruct
	public void init() {

		LOG.debug("DataLayerModel : This is Entry into init() method");

		if(null != currentPage) {
			String templateType = currentPage.getTemplate().getPageTypePath();
			commonAttributes();
			if(null != templateType) {
				LOG.debug("DataLayerModel: Template type : {}", templateType);
				switch (templateType) {
					case CommonConstants.EATON_EDIT_TEMPLATE_PAGE:
						LOG.debug("DataLayerModel : EATON_EDIT_TEMPLATE_PAGE : entry");
						eatonEditTemplateAttr();
						getDatalayerbean();
						LOG.debug("DataLayerModel : EATON_EDIT_TEMPLATE_PAGE : exit");
						break;

					case CommonConstants.EATON_REGISTRATION_TEMPLATE_PAGE:
						LOG.debug("DataLayerModel : EATON_REGISTRATION_TEMPLATE_PAGE : entry");
						LOG.debug("DataLayerModel : EATON_REGISTRATION_TEMPLATE_PAGE : exit");
						break;

					case CommonConstants.EATON_EDIT_TEMPLATE_LOGIN_PAGE:
						LOG.debug("DataLayerModel : EATON_EDIT_TEMPLATE_LOGIN_PAGE : entry");
						LOG.debug("DataLayerModel : EATON_EDIT_TEMPLATE_LOGIN_PAGE : exit");
						break;

					case CommonConstants.EATON_CUMMINS_EDIT_TEMPLATE_PAGE:
						LOG.debug("DataLayerModel : EATON_CUMMINS_EDIT_TEMPLATE_PAGE : entry");
						LOG.debug("DataLayerModel : EATON_CUMMINS_EDIT_TEMPLATE_PAGE : exit");
						break;

					default:
						LOG.debug("DataLayerModel : default : entry");
						getDatalayerbean();
						LOG.debug("DataLayerModel : default : exit");
						break;

				}
			}
		}
		if(null == datalayerbean){
			datalayerbean = new DataLayerBean();
			LOG.debug("DataLayerModel: Initialize DataLayer Bean");
		}
		dataLayerJson = this.gson.toJson(datalayerbean);

		LOG.debug("DataLayerModel : This is Exit of init() method");
	}

	/*
	 * This are the common attributes used for Edit,Registration,Login Page and
	 * Cummins template and set attributes in datalayer bean for JSOn output.
	 */
	private void commonAttributes() {
		LOG.debug("DataLayerModel : commonAttributes : entry");
		buildProductGridSelector();
		datalayerbean = new DataLayerBean();
		setLanguageAndCountry();
		datalayerbean.setChannel(location);
		setPercolateContentId();
		setPageIntent();

		if (null != currentPage) {
			ValueMap pageProperties = currentPage.getProperties();
			if (pageProperties != null) {
				String pagetype = CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_TYPE).replace("-",
						CommonConstants.SPACE_STRING);
				if (!StringUtils.isEmpty(pagetype)) {
					String pageType=pagetype.toLowerCase();
					datalayerbean.setPageType(pageType);
				}
			}
		}
		setSearchResultPage();

		Set<String> sectorBusinessUnitSet = null;
		if (null != datalayerbean.getPageType()) {
			sectorBusinessUnitSet = setDataLayerPageTypeExists(sectorBusinessUnitSet);
		}
		String sectorBusinessVal = StringUtils.EMPTY;
		if (sectorBusinessUnitSet != null) {
			sectorBusinessVal = String.join(CommonConstants.DOUBLE_PIPE, sectorBusinessUnitSet);
		}
		if (!StringUtils.isEmpty(sectorBusinessVal)) {
			datalayerbean.setSectorBusiness(sectorBusinessVal);
		}
		setProductFamilyPage();

		boolean isVideoComponentResourceExist = isComponentExistsOnPage(currentPage,
				CommonConstants.BRIGHTCOVE_VIDEO_RESOURCE_TYPE);
		if(isVideoComponentResourceExist) {
			datalayerbean.setVideoComponent(CommonConstants.YES);
		}else {
			datalayerbean.setVideoComponent(CommonConstants.NO);
		}

		boolean isResourceListComponentResourceExist = isComponentExistsOnPage(currentPage,
				CommonConstants.RESOURCE_LIST_RESOURCE_TYPE);
		if(isResourceListComponentResourceExist) {
			datalayerbean.setResourceListComponent(CommonConstants.YES);
		}else {
			datalayerbean.setResourceListComponent(CommonConstants.NO);
		}
		boolean isEloquaFormComponentResourceExist = isComponentExistsOnPage(currentPage,
				CommonConstants.ELOQUA_FROM_RESOURCE_TYPE);
		if(isEloquaFormComponentResourceExist) {
			datalayerbean.setEloquaFormComponent(CommonConstants.YES);
		}else {
			datalayerbean.setEloquaFormComponent(CommonConstants.NO);
		}

		datalayerbean.setDomain(CommonUtil.getExternalizedDomain(externalizer, resourceResolver, currentPage.getPath()));

		LOG.debug("DataLayerModel : commonAttributes : exit");
	}

	private void setPercolateContentId() {
		LOG.debug("DataLayerModel : setPercolateContentId : entry");

		if (null != currentPage) {
			String percolateContentId = CommonUtil.getStringProperty(currentPage.getProperties(), CommonConstants.PERCOLATE_CON_ID);
			if (StringUtils.isNotEmpty(percolateContentId)) {
				LOG.debug("percolateContentId found in page properties: {}", percolateContentId);
				String percolateContentIdWithPrefix = org.apache.commons.lang3.StringUtils.join(CommonConstants.PERCOLATE_POST_NAME, percolateContentId);
				LOG.debug("setting percolateContentId with prefix in data layer as: {}", percolateContentIdWithPrefix);
				datalayerbean.setPercolateContentId(percolateContentIdWithPrefix);
			} else {
				LOG.warn("percolateContentId not found in page properties. percolateContentId not set in data layer.");
			}
		} else {
			LOG.error("currentPage is null. Unable to set percolateContentId. percolateContentId not set in data layer.");
		}

		LOG.debug("DataLayerModel : setPercolateContentId : exit");
	}

	private void setPageIntent() {
		LOG.debug("DataLayerModel : setPageIntent : entry");

		if (null != currentPage) {
			String pageIntent = CommonUtil.getStringProperty(currentPage.getProperties(), CommonConstants.PAGE_INTENT);
			if (StringUtils.isNotEmpty(pageIntent)) {
				LOG.debug("page intent found in page properties: {}", pageIntent);
				LOG.debug("setting page intent in data layer as: {}", pageIntent);
				datalayerbean.setPageIntent(pageIntent);
			} else {
				LOG.warn("page intent not found in page properties. page intent not set in data layer.");
			}
		} else {
			LOG.error("currentPage is null. page intent not set in data layer.");
		}

		LOG.debug("DataLayerModel : setPageIntent : exit");
	}

	/*
	 * This is method used only for edit template attributes
	 */
	private void eatonEditTemplateAttr() {
		LOG.debug("DataLayerModel : eatonEditTemplateAttr : entry");
		Set<String> topicClusterUnitSet = null;
		topicClusterUnitSet = CommonUtil.getPageTagNamesByBaseTagPath(adminService, currentPage,
				CommonConstants.EATON_NEWS_TOPIC_TAG_PATH);
		String topicClusterVal = StringUtils.EMPTY;
		if (topicClusterUnitSet != null) {
			topicClusterVal = String.join(CommonConstants.DOUBLE_PIPE, topicClusterUnitSet);
		}
		if (!StringUtils.isEmpty(topicClusterVal)) {
			datalayerbean.setTopicCluster(topicClusterVal);
		}
		if (null != slingRequest) {
			authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
		}
		ValueMap pageProperties = currentPage.getProperties();

		if (authenticationToken != null && authenticationToken.getUserProfile() != null) {
			setVisitorAccountType(pageProperties);
			setVisitorProductCategories(pageProperties);
			setVisitorApplicationAccess(pageProperties);
			setVisitorPartnerprogram(pageProperties);
			setVisitorTierlevel(pageProperties);
			setVisitorApprovedCountries(pageProperties);
		}
		LOG.debug("DataLayerModel : eatonEditTemplateAttr : exit");
	}

	/**
	 * Builds the productGridSelector for DataLayerModel.
	 *
	 * @return
	 */
	private void buildProductGridSelector() {
		LOG.debug("DataLayerModel : This is Entry into buildProductGridSelector() method");
		FacetURLBeanServiceResponse facetURLBeanResponse = null;
		if (eatonSiteConfigService != null && slingRequest != null) {
			String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
			if (currentPage != null) {
				Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);

				int pageSize=0;
				if(siteConfig.isPresent()) {
					siteConfig.get().getPageSize();
				}

				String contentResourcePath = StringUtils.EMPTY;
				String pageType = StringUtils.EMPTY;
				Resource contentResource = currentPage.getContentResource();

				if (contentResource != null) {
					contentResourcePath = contentResource.getPath();
					pageType = contentResource.getValueMap().get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
					setPageType(pageType);
				}
				contentResourcePath = CommonUtil.getMappedUrl(contentResourcePath, externalizer, slingRequest);
				facetURLBeanResponse = facetURLBeanService.getFacetURLBeanResponse(selectors, pageSize, pageType,
						contentResourcePath);
				if (facetURLBeanResponse != null) {
					productGridSelectors = facetURLBeanResponse.getProductGridSelectors();
					LOG.debug("DataLayerModel : buildProductGridSelector() method :productGridSelectors value set");
				}
			}
		}
		LOG.debug("DataLayerModel : This is Exit into buildProductGridSelector() method");
	}

	/**
	 * setLanguageAndCountry
	 */
	private void setLanguageAndCountry() {
		if (currentPage != null && resourceResolver != null) {
			String path = currentPage.getPath();
			Resource resource = resourceResolver.getResource(path);
			if (null != resource) {
				setLanguageCountry(resource);
			}
		}
	}

	/**
	 * @param resource
	 */
	private void setLanguageCountry(Resource resource) {
		LOG.debug("DataLayerModel : This is Entry into setLanguageCountry() method");

		Page targetPage = resource.adaptTo(Page.class);
		if (null != targetPage) {
			final String country = CommonUtil.getCountryFromPagePath(targetPage);
			if (null != country && StringUtils.isNotBlank(country)) {
				String countryLower=country.toLowerCase();
				datalayerbean.setCountry(countryLower);
			}
			final Locale pageLocaleForLanguage = targetPage.getLanguage(true);
			if (null != pageLocaleForLanguage && pageLocaleForLanguage.toLanguageTag() != null) {
				String language=pageLocaleForLanguage.toLanguageTag().toLowerCase();
				datalayerbean.setLanguage(language);
			}
		}

		LOG.debug("DataLayerModel : This is Exit setLanguageCountry() method");
	}

	/**
	 * Sets the search result page.
	 */
	public void setSearchResultPage() {

		LOG.debug("DataLayerModel : This is Entry into setSearchResultPage() method");

		if (null != productGridSelectors) {
			String searchquery = productGridSelectors.getSearchTerm();
			if ((null != searchquery) && (!(StringUtils.EMPTY).equals(searchquery))) {
				datalayerbean.setSearchQuery(searchquery);
			}
		}

		if (null != siteSearchResponse) {
			Integer searchresultcount = siteSearchResponse.getPageResponse().getTotalCount();
			if (null != searchresultcount && searchresultcount > 0) {
				datalayerbean.setSearchResultsCount(searchresultcount);
			} else {
				datalayerbean.setSearchResultsCount(0);
			}
		}

		if (null != productGridSelectors) {
			setDataLayerValuesProductGridExists();
		}
		LOG.debug("DataLayerModel : This is Exit of setSearchResultPage() method");
	}

	/**
	 *
	 */
	private void setDataLayerValuesProductGridExists() {
		List<FacetBean> activeFacetsRecievedList = productGridSelectors.getFacets();
		if (null != siteSearchResponse && null != activeFacetsRecievedList
				&& null != siteSearchResponse.getPageResponse().getFacets()) {
			facetGroupList = siteSearchResponse.getPageResponse().getFacets().getFacetGroupList();
			if (null != facetGroupList) {
				StringBuilder buildFacetIDString = new StringBuilder();
				buildFacetIDString = getFacetIDString(activeFacetsRecievedList, buildFacetIDString);
				if (buildFacetIDString != null) {
					datalayerbean.setSiteSearchFacets(buildFacetIDString.toString());
				}
			}
		}
	}

	/**
	 * @param activeFacetsRecievedList
	 * @param buildFacetIDString
	 * @return
	 */
	private StringBuilder getFacetIDString(List<FacetBean> activeFacetsRecievedList, StringBuilder buildFacetIDString) {
		for (FacetGroupBean facetGroupBean : facetGroupList) {
			List<FacetValueBean> facetValueList = facetGroupBean.getFacetValueList();
			if (null != facetValueList) {
				int ctr = 0;
				for (FacetValueBean facetValueBean : facetValueList) {
					buildFacetIDString = buildActiveFacetID(activeFacetsRecievedList, buildFacetIDString,
							facetGroupBean, ctr, facetValueBean);
				}
			}
		}
		return buildFacetIDString;
	}

	/**
	 * @param activeFacetsRecievedList
	 * @param buildFacetIDString
	 * @param facetGroupBean
	 * @param ctr
	 * @param facetValueBean
	 * @return
	 */
	private static StringBuilder buildActiveFacetID(List<FacetBean> activeFacetsRecievedList,
													StringBuilder buildFacetIDString, FacetGroupBean facetGroupBean, int ctr, FacetValueBean facetValueBean) {
		LOG.debug("DataLayerModel : buildActiveFacetID() method : Entry");
		for (FacetBean activeFacetsRecieved : activeFacetsRecievedList) {
			if (null != activeFacetsRecieved.getFacetID() && null != facetValueBean.getFacetValueId()
					&& activeFacetsRecieved.getFacetID().equals(facetValueBean.getFacetValueId())) {
				if (ctr == 0) {
					if (buildFacetIDString.length() > 0) {
						buildFacetIDString = buildFacetIDString(buildFacetIDString, facetGroupBean, facetValueBean);
					} else {
						buildFacetIDString = buildFacetIDString(buildFacetIDString, facetGroupBean, facetValueBean);
					}
				} else {
					buildFacetIDString = buildFacetIDString.append(CommonConstants.COMMA)
							.append(facetValueBean.getFacetValueLabel());
				}
				ctr++;
			}
		}
		LOG.debug("DataLayerModel : buildActiveFacetID() method : Exit");
		return buildFacetIDString;
	}

	/**
	 * @param buildFacetIDString
	 * @param facetGroupBean
	 * @param facetValueBean
	 * @return
	 */
	private static StringBuilder buildFacetIDString(StringBuilder buildFacetIDString, FacetGroupBean facetGroupBean,
													FacetValueBean facetValueBean) {

		if(buildFacetIDString.length() > 0) {
			return buildFacetIDString.append(CommonConstants.PIPE).append(facetGroupBean.getFacetGroupLabel()).append(CommonConstants.COLON)
					.append(facetValueBean.getFacetValueLabel());
		}

		return buildFacetIDString.append(facetGroupBean.getFacetGroupLabel()).append(CommonConstants.COLON)
				.append(facetValueBean.getFacetValueLabel());
	}

	/**
	 * @param sectorBusinessUnitSet
	 * @return
	 */
	private Set<String> setDataLayerPageTypeExists(Set<String> sectorBusinessUnitSet) {
		LOG.debug("DataLayerModel : This is Entry to setDataLayerPageTypeExists() method");
		if (datalayerbean != null) {
			if (!datalayerbean.getPageType().equalsIgnoreCase(CommonConstants.DATA_LAYER_PRODUCT_FAMILY)
					&& !datalayerbean.getPageType().equalsIgnoreCase(CommonConstants.DATA_LAYER_PRODUCT_SKU)) {
				sectorBusinessUnitSet = CommonUtil.getPageTagNamesByBaseTagPath(adminService, currentPage,
						CommonConstants.SECTOR_BUSINESS_UNIT_TAG_PATH);
			} else {
				if (datalayerbean.getPageType().equalsIgnoreCase(CommonConstants.DATA_LAYER_PRODUCT_FAMILY)) {
					sectorBusinessUnitSet = setProductFamilyNameForPIMPath(sectorBusinessUnitSet);
				}
				if (datalayerbean.getPageType().equalsIgnoreCase(CommonConstants.DATA_LAYER_PRODUCT_SKU)) {
					sectorBusinessUnitSet = setProductFamilyNameForSKU(sectorBusinessUnitSet);
				}
			}
		}
		LOG.debug("DataLayerModel : This is Exit of setDataLayerPageTypeExists() method");
		return sectorBusinessUnitSet;
	}

	/**
	 * @param sectorBusinessUnitSet
	 * @return
	 */
	private Set<String> setProductFamilyNameForPIMPath(Set<String> sectorBusinessUnitSet) {
		LOG.debug("DataLayerModel : setProductFamilyNameForPIMPath() method : Entry");
		if (currentPage != null && currentPage.getProperties().get(CommonConstants.PAGE_PIM_PATH) != null) {
			final String pimPagePath = currentPage.getProperties().get(CommonConstants.PAGE_PIM_PATH).toString();
			sectorBusinessUnitSet = CommonUtil.getPageTagNamesByPimTagPath(adminService, pimPagePath,
					CommonConstants.SECTOR_BUSINESS_UNIT_TAG_PATH);
			setProductFamilyName(pimPagePath);
		}
		LOG.debug("DataLayerModel : setProductFamilyNameForPIMPath() method : Exit");
		return sectorBusinessUnitSet;
	}

	/**
	 * @param sectorBusinessUnitSet
	 * @return
	 */
	private Set<String> setProductFamilyNameForSKU(Set<String> sectorBusinessUnitSet) {
		LOG.debug("DataLayerModel : setProductFamilyNameForSKU() method : Entry");
		ProductFamilyPIMDetails familyPimDetails = getFamilypimobj();
		if (familyPimDetails != null) {
			String productFamilyPath = familyPimDetails.getProductFamilyAEMPath();
			if (productFamilyPath != null && pageManager != null) {
				Page familyPage = pageManager.getPage(productFamilyPath);
				if (null != familyPage) {
					final String pimPath = familyPage.getProperties().get(CommonConstants.PAGE_PIM_PATH,
							StringUtils.EMPTY);
					sectorBusinessUnitSet = CommonUtil.getPageTagNamesByPimTagPath(adminService, pimPath,
							CommonConstants.SECTOR_BUSINESS_UNIT_TAG_PATH);
					setProductFamilyName(pimPath);

				}
			}
		}
		LOG.debug("DataLayerModel : setProductFamilyNameForSKU() method : Exit");
		return sectorBusinessUnitSet;
	}

	/**
	 * @param
	 * @param path
	 */
	private void setProductFamilyName(final String path) {
		LOG.debug("DataLayerModel : setProductFamilyName() method : Entry");
		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			if (adminResourceResolver != null && isNotNullOrEmpty(path)
					&& adminResourceResolver.getResource(path) != null) {
				Resource res = adminResourceResolver.getResource(path);
				if (null != res) {
					ValueMap valueMap = res.adaptTo(ValueMap.class);
					if (valueMap != null) {
						final String productName = (String) valueMap.get(CommonConstants.PIM_PRODUCT_NAME);
						if (!StringUtils.isEmpty(productName)) {
							String pageType=getPageType();
							if(pageType.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE) || pageType.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)){
								datalayerbean.setProductFamily(productName);
							}

						}
					}
				}
			}
			LOG.debug("DataLayerModel : setProductFamilyName() method : Exit");
		}
	}

	/**
	 * @param path
	 * @return
	 */
	private static boolean isNotNullOrEmpty(final String path) {
		return path != null && !path.isEmpty();
	}

	public ProductFamilyPIMDetails getFamilypimobj() {
		LOG.debug("DataLayerModel : This is Entry of getFamilypimobj() method");
		if (currentPage != null) {
			final ValueMap pageProperties = currentPage.getProperties();
			if (null != pageProperties && adminService != null && slingRequest != null
					&& endecaRequestService != null) {
				try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
					String pagetype = CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_TYPE);
					if (adminResourceResolver != null && (pagetype != null
							&& pagetype.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE))) {
						final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
						final String inventoryId = endecaRequestService.getInventoryId(currentPage,
								adminResourceResolver);
						if (inventoryId != null) {
							final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
									.getEndecaRequestBean(currentPage, selectors, inventoryId, productGridSelectors);
							if (endecaService != null) {
								final SKUDetailsResponseBean skuDetailsResponseBean = endecaService
										.getSKUDetails(endecaServiceRequestBean);
								if ((skuDetailsResponseBean != null && skuDetailsResponseBean.getSkuResponse() != null)
										&& productFamilyDetailService != null) {
									final SKUDetailsBean skuDetails = skuDetailsResponseBean.getSkuResponse()
											.getSkuDetails();
									familypimobj = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetails,
											currentPage);
									skubean = skuDetailsResponseBean.getSkuResponse().getSkuDetails();
									setProductDetailPages();
								}
							}
						}
					}
				}
			}
		}
		LOG.debug("DataLayerModel : This is Exit from getFamilypimobj() method");
		return familypimobj;
	}

	/**
	 * Sets the product detail pages.
	 */
	public void setProductDetailPages() {

		LOG.debug("DataLayerModel : This is Entry of setProductDetailPages() method");

		familyPimObjectExist();
		if (slingRequest != null) {
			String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
			if (selectors.length > 0) {
				String prodsku = slingRequest.getRequestPathInfo().getSelectors()[0];
				prodsku = CommonUtil.decodeSearchTermString(prodsku);
				if (null != prodsku && !(StringUtils.EMPTY).equals(prodsku)) {
					datalayerbean.setProductSku(prodsku);
				}
				if (null != skubean) {
					String proname = skubean.getTradeName();
					if (null != proname && !(StringUtils.EMPTY).equals(proname)) {
						datalayerbean.setProductName(proname);
					}
				}
			}
		}
		LOG.debug("DataLayerModel : This is Exit of setProductDetailPages() method");
	}

	/**
	 *
	 */
	private void familyPimObjectExist() {
		LOG.debug("DataLayerModel : familyPimObjectExist() method : Entry");
		if (null != familypimobj) {

			String family = familypimobj.getProductName();
			String subcategory = familypimobj.getPrimarySubCategory();

			if (null != family && !(StringUtils.EMPTY).equals(family)) {
				String pageType=getPageType();
				if(pageType.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE) || pageType.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)){
					datalayerbean.setProductFamily(family);
				}
			}
			if (null != subcategory && !(StringUtils.EMPTY).equals(subcategory)) {
				setSubCatPageProdCategory(subcategory);
			}
		}
		LOG.debug("DataLayerModel : familyPimObjectExist() method : Exit");
	}

	/**
	 * Set product Category in data layer We retrieve product category from the
	 * subCategory page. Since product category isn't always set in the same place,
	 * we follow a hierarchy of page fields to find the first mention of product
	 * category Order of checks: page.getNavigationTitle() -> page.getPageTitle() ->
	 * page.getTitle() -> page.getName() If none of those fields are filled, set
	 * product category to null
	 *
	 * @param subcategory page name where we look for product category
	 */
	private void setSubCatPageProdCategory(String subcategory) {
		LOG.debug("DataLayerModel : setSubCatPageProdCategory() method : Entry");
		if (pageManager != null) {
			Page page = pageManager.getPage(subcategory);
			String category;
			if (null != page) {
				if (page.getNavigationTitle() != null) {
					category = page.getNavigationTitle();
				} else if (page.getPageTitle() != null) {
					category = page.getPageTitle();
				} else if (page.getTitle() != null) {
					category = page.getTitle();
				} else if (page.getName() != null) {
					category = page.getName();
				} else {
					LOG.debug(
							"DataLayerModel: setSubCatPageProdCategory() method : product page has no NavigationTitle, pageTitle or Name. Setting category to null");
					category = null;
				}
				if (!StringUtils.isEmpty(category)) {
					datalayerbean.setProductCategory(category);
				}
			}
		}
		LOG.debug("DataLayerModel : setSubCatPageProdCategory() method : exit");
	}

	/**
	 * Sets the product family page.
	 */
	public void setProductFamilyPage() {

		LOG.debug("DataLayerModel : This is Entry into setProductFamilyPage() method");
		/**
		 * Just to ensure productFamily is set in dataLayer for non pdh product family
		 * pages
		 */
		if ((null == datalayerbean.getProductFamily() || datalayerbean.getProductFamily().isEmpty())
				&& (currentPage != null && currentPage.getTitle() != null)) {
			String productfamily=currentPage.getTitle().toLowerCase();
			String pageType=getPageType();
			if(pageType.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE) || pageType.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {
				datalayerbean.setProductFamily(productfamily);
			}
		}
		if (null != productfamilydetails) {
			setDataLayerBeanProdCategory();
		}
		if (null != productGridSelectors) {
			setDataLayerProductFacetApplied();
		}
		LOG.debug("DataLayerModel : This is Exit of setProductFamilyPage() method");
	}

	/**
	 * setDataLayerBeanProdCategory
	 */
	private void setDataLayerBeanProdCategory() {
		String subcategory = productfamilydetails.getPrimarySubCategory();
		if (null != subcategory && !(StringUtils.EMPTY).equals(subcategory)) {
			setSubCatPageProdCategory(subcategory);
		}
	}

	/**
	 * setDataLayerProductFacetApplied
	 */
	private void setDataLayerProductFacetApplied() {
		List<FacetBean> activeFacetsRecievedList = productGridSelectors.getFacets();
		if (null != skuListResponseBean && null != activeFacetsRecievedList) {
			if(null!= skuListResponseBean.getFamilyModuleResponse().getFacets()) {
				facetGroupList=skuListResponseBean.getFamilyModuleResponse().getFacets().getFacetGroupList();
			}else {
				facetGroupList=null;
			}
			if (null != facetGroupList) {
				StringBuilder buildFacetIDString = new StringBuilder();
				buildFacetIDString = getFacetIDString(activeFacetsRecievedList, buildFacetIDString);
				if (buildFacetIDString != null) {
					datalayerbean.setProductFacetsApplied(buildFacetIDString.toString());
				}
			}
		}
	}

	/**
	 *
	 * @param currentPage           - current page
	 * @param componentResourceType - component resource type
	 * @return the component resource is exist or not on the given page.
	 */
	private static boolean isComponentExistsOnPage(final Page currentPage, final String componentResourceType) {
		LOG.debug("DataLayerModel : isComponentExistsOnPage : entry");
		final ResourceDecorator resourceDecorator = currentPage.getContentResource().adaptTo(ResourceDecorator.class);
		if (null != resourceDecorator) {
			final Optional<Resource> componentResource = resourceDecorator.findByResourceType(componentResourceType);
			if (componentResource.isPresent()) {
				return true;
			}
		}
		LOG.debug("DataLayerModel : isComponentExistsOnPage() method : exit");
		return false;
	}

	private void setVisitorAccountType(ValueMap pageProperties) {
		LOG.debug("DataLayerModel : setVisitorAccountType() method : Entry");
		if (null != pageProperties) {
			final List<String> tagsList = new ArrayList<>();
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			String[] accGrpTags = CommonUtil.getStringArrayProperty(pageProperties, CommonConstants.ACCOUNT_TYPE);
			if (ArrayUtils.isNotEmpty(accGrpTags) && tagManager != null) {
				for (String accGrp : accGrpTags) {
					com.day.cq.tagging.Tag tag = tagManager.resolve(accGrp);
					if (null != tag) {
						tagsList.add(tag.getTitle());
					}
				}
			}
			String tagsString = String.join(", ", tagsList);
			if (!StringUtils.isEmpty(tagsString)) {
				datalayerbean.setVisitorAccountType(tagsString);
			}
			LOG.debug("DataLayerModel : setVisitorAccountType() method : Exit");
		}
	}

	private void setVisitorProductCategories(ValueMap pageProperties) {
		LOG.debug("DataLayerModel : setVisitorProductCategories() method : Entry");
		if (null != pageProperties) {
			final List<String> tagsList = new ArrayList<>();
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			String[] prdGrpTags = CommonUtil.getStringArrayProperty(pageProperties, CommonConstants.PRODUCT_CATEGORIES);
			if (ArrayUtils.isNotEmpty(prdGrpTags) && tagManager != null) {
				for (String prdGrp : prdGrpTags) {
					com.day.cq.tagging.Tag tag = tagManager.resolve(prdGrp);
					if (null != tag) {
						tagsList.add(tag.getTitle());
					}
				}
			}
			String tagsString = String.join(", ", tagsList);
			if (!StringUtils.isEmpty(tagsString)) {
				datalayerbean.setVisitorProductCategories(tagsString);
			}
			LOG.debug("DataLayerModel : setVisitorProductCategories() method : Exit");
		}
	}

	private void setVisitorApplicationAccess(ValueMap pageProperties) {
		LOG.debug("DataLayerModel : setVisitorApplicationAccess() method : Entry");
		if (null != pageProperties) {
			final List<String> tagsList = new ArrayList<>();
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			String[] appAccTags = CommonUtil.getStringArrayProperty(pageProperties, CommonConstants.APPLICATION_ACCESS);
			if (ArrayUtils.isNotEmpty(appAccTags) && tagManager != null) {
				for (String appacc : appAccTags) {
					com.day.cq.tagging.Tag tag = tagManager.resolve(appacc);
					if (null != tag) {
						tagsList.add(tag.getTitle());
					}
				}
			}
			String tagsString = String.join(", ", tagsList);
			if (!StringUtils.isEmpty(tagsString)) {
				datalayerbean.setVisitorApplicationAccess(tagsString);
			}
			LOG.debug("DataLayerModel : setVisitorApplicationAccess() method : Exit");
		}
	}

	private void setVisitorPartnerprogram(ValueMap pageProperties) {
		LOG.debug("DataLayerModel : setVisitorPartnerprogram() method : Entry");
		if (null != pageProperties) {
			final List<String> tagsList = new ArrayList<>();
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			String[] patPrgTag = CommonUtil.getStringArrayProperty(pageProperties,
					CommonConstants.PARTNER_PROGRAMME_TYPE);
			if (ArrayUtils.isNotEmpty(patPrgTag) && tagManager != null) {
				for (String patPrg : patPrgTag) {
					com.day.cq.tagging.Tag tag = tagManager.resolve(patPrg);
					if (null != tag) {
						tagsList.add(tag.getTitle());
					}
				}
			}
			String tagsString = String.join(", ", tagsList);
			if (!StringUtils.isEmpty(tagsString)) {
				datalayerbean.setVisitorPartnerprogram(tagsString);
			}
			LOG.debug("DataLayerModel : setVisitorPartnerprogram() method : Exit");
		}
	}

	private void setVisitorTierlevel(ValueMap pageProperties) {
		LOG.debug("DataLayerModel : setVisitorTierlevel() method : Entry");
		if (null != pageProperties) {
			final List<String> tagsList = new ArrayList<>();
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			String[] tierTag = CommonUtil.getStringArrayProperty(pageProperties, CommonConstants.TIER_LEVEL);
			if (ArrayUtils.isNotEmpty(tierTag) && tagManager != null) {
				for (String tierTaglel : tierTag) {
					com.day.cq.tagging.Tag tag = tagManager.resolve(tierTaglel);
					if (null != tag) {
						tagsList.add(tag.getTitle());
					}
				}
			}
			String tagsString = String.join(", ", tagsList);
			if (!StringUtils.isEmpty(tagsString)) {
				datalayerbean.setVisitorTierlevel(tagsString);
			}
			LOG.debug("DataLayerModel : setVisitorTierlevel() method : Exit");
		}
	}

	private void setVisitorApprovedCountries(ValueMap pageProperties) {
		LOG.debug("DataLayerModel : setVisitorApprovedCountries() method : Entry");
		if (null != pageProperties) {
			final List<String> tagsList = new ArrayList<>();
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			String[] countrytag = CommonUtil.getStringArrayProperty(pageProperties, CommonConstants.COUNTRIES);
			if (ArrayUtils.isNotEmpty(countrytag) && tagManager != null) {
				for (String country : countrytag) {
					com.day.cq.tagging.Tag tag = tagManager.resolve(country);
					if (null != tag) {
						tagsList.add(tag.getTitle());
					}
				}
			}
			String tagsString = String.join(", ", tagsList);
			if (!StringUtils.isEmpty(tagsString)) {
				datalayerbean.setVisitorApprovedCountries(tagsString);
			}
			LOG.debug("DataLayerModel : setVisitorApprovedCountries() ]method : Exit");
		}
	}

	/**
	 * Gets the datalayerbean.
	 *
	 * @return the datalayerbean
	 */
	public DataLayerBean getDatalayerbean() {
		LOG.debug("DataLayerModel : finally block : This is Entry of getDatalayerbean() method");
		if (currentPage != null) {
			ValueMap pageProperties = currentPage.getProperties();
			if (null != pageProperties && adminService != null && endecaRequestService != null) {
				try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
					String pagetype = CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_TYPE);

					if (adminResourceResolver != null && productGridSelectors != null && pagetype != null) {
						if (pagetype.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
							LOG.debug("DataLayerModel : getDatalayerbean() method : product-family");
							final String inventoryId = endecaRequestService.getInventoryId(currentPage,
									adminResourceResolver);
							if (StringUtils.isNotBlank(inventoryId) && slingRequest != null && endecaService != null) {
								final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
								final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
										.getEndecaRequestBean(currentPage, selectors, inventoryId,
												productGridSelectors);
								if (endecaServiceRequestBean != null) {
									skuListResponseBean = endecaService.getSKUList(endecaServiceRequestBean);
								}
							}
							if (productFamilyDetailService != null) {
								productfamilydetails = productFamilyDetailService
										.getProductFamilyDetailsBean(currentPage);
								setProductFamilyPage();
							}
							LOG.debug("DataLayerModel : getDatalayerbean() If block ends: product-family");
						}
						if (pagetype.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)
								&& slingRequest != null) {
							LOG.debug("DataLayerModel : getDatalayerbean() if : product-sku");
							final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
							final String inventoryId = endecaRequestService.getInventoryId(currentPage,
									adminResourceResolver);
							final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
									.getEndecaRequestBean(currentPage, selectors, inventoryId, productGridSelectors);
							if (endecaService != null) {
								final SKUDetailsResponseBean skuDetailsResponseBean = endecaService
										.getSKUDetails(endecaServiceRequestBean);
								if (skuDetailsResponseBean != null) {
									SKUDetailsBean skuDetails = skuDetailsResponseBean.getSkuResponse().getSkuDetails();
									if (productFamilyDetailService != null) {
										familypimobj = productFamilyDetailService
												.getProductFamilyPIMDetailsBean(skuDetails, currentPage);
									}
									skubean = skuDetailsResponseBean.getSkuResponse().getSkuDetails();
									setProductDetailPages();
								}
							}
							LOG.debug("DataLayerModel : getDatalayerbean() If block ends: product-sku");
						}

						if (pagetype.equalsIgnoreCase(CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE)
								&& slingRequest != null) {
							LOG.debug("DataLayerModel : getDatalayerbean() if : site-search");
							boolean isUnitedStatesDateFormat = false;
							Optional<SiteResourceSlingModel> siteResourceSlingModel = eatonSiteConfigService
									.getSiteConfig(currentPage);
							if (siteResourceSlingModel.isPresent()) {
								isUnitedStatesDateFormat = Boolean
										.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
							}
							LOG.debug("DataLayerModel : getDatalayerbean() if block ends: site-search");
						}
					}
				}
			}
		}
		LOG.debug("DataLayerModel : finally block : This is Exit of getDatalayerbean() method");
		return datalayerbean;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getDataLayerJson() {
		return dataLayerJson;
	}

	public void setDataLayerJson(String dataLayerJson) {
		this.dataLayerJson = dataLayerJson;
	}
}
