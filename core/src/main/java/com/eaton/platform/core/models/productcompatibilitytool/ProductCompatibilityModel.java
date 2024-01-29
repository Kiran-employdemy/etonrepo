package com.eaton.platform.core.models.productcompatibilitytool;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.*;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.helpers.FacetedNavigationHelperV2;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.*;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.FacetsBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityListResponseBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityModuleBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Class ProductCompatibilityModel.
 *
 * @author E0362677, Victoria Pershick
 */

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductCompatibilityModel {

	/** The facets key results. */
	private static final String ATTRIBUTE_LABEL = "attributeLabel";
	/** The facets key results. */
	private static final String ATTRIBUTE_VALUE = "attributeVaue";
	/** The facets key results. */
	private static final String FILTER_ATTRIBUTE_LABEL = "filterAttributeLabel";
	/** The facets key results. */
	private static final String FILTER_ATTRIBUTE_VALUE = "filterAttributeVaue";
	/** The facets key results. */
	private static final String FACETS_KEY_RESULTS = "results";

	/** The facets key relevance. */
	private static final String FACETS_KEY_RELEVENCE = "relevance";

	/** The facets key clear filters. */
	private static final String FACETS_KEY_CLEAR_FILTERS = "clearFilters";
	/** The facets key narrow results. */
	private static final String FACETS_KEY_NARROW_RESULTS = "narrowResults";
	/** The facets key view more. */
	private static final String FACETS_KEY_VIEW_MORE = "viewMore";
	/** The facets key new. */
	private static final String FACETS_KEY_NEW = "newKeyword";
	/** The facets key bestseller. */
	private static final String FACETS_KEY_BESTSELLER = "bestSeller";
	/** The card key load more. */
	private static final String CARD_KEY_LOAD_MORE = "loadMore";
	/** String false */
	private static final String FALSE = "false";
	/** String True */
	private static final String TRUE = "true";
	/** The product family details. */
	private ProductFamilyDetails productFamilyDetails;
	/** The facets key remove filters. */
	private static final String FACETS_KEY_REMOVE_FILTERS = "removeFilters";
	/** The facets key apply. */
	private static final String FACETS_KEY_APPLY = "apply";
	/** The facets key filters. */
	private static final String FACETS_KEY_FILTERS = "filters";
	/** The name of the searchTerm selector, as typically seen in the page URL */
	private static final String SEARCHTERM_LABEL = "searchTerm$";

	/** Facet Search enabled */
	public static final String FACET_GROUP_FACET_SEARCH_ENABLED = "facetSearchEnabled";
	/** Single facet enabled */
	public static final String SINGLE_FACET_ENABLED = "singleFacetEnabled";

	/** Relevance */
	private static final String RELEVANCESTR = "Relevance";

	/** specification */
	public static final String DIGITALTOOL_SPECIFICATION_TAB = "specification";

	/** resource */
	public static final String DIGITALTOOL_RESOURCE_TAB = "resource";

	/** Load More Flag. */
	private String loadMoreFlag;

	/** Load More Flag. */
	private String loadMoreLabel;

	/** Base sku path */
	private String baseSKUPath;

	/** The sku page name. */
	private String skuPageName;

	/** specificationTabTitle. */
	private String specificationTabTitle;

	/** resourceTabTitle. */
	private String resourceTabTitle;

	/** ProductCompatibilityModel Log reference */
	private static final Logger LOG = LoggerFactory.getLogger(ProductCompatibilityModel.class);

	/** The page type. */
	private String pageType;

	/** The faceted header navigation result bean. */
	private FacetedHeaderNavigationResultBean facetedHeaderNavigationResultBean;

	private FacetedNavigationHelperV2 facetedNavigationHelperV2;

	/** The family module bean list. */
	private List<ProductCompatibilityModuleBean> familyModuleBeanList;

	/** The family module bean list. */
	private List<ProductCompatibilityModuleBean> finalAttributeList;

	/** The load more attr json string. */
	private String loadMoreAttrJsonString;

	public List<ProductCompatibilityModuleBean> getFinalAttributeList() {
		return finalAttributeList;
	}

	/** The family module bean list. */
	private List<String> compatibilityExcelTable = new ArrayList<>();

	/** The site resource sling model. */
	private SiteResourceSlingModel siteResourceSlingModel;

	/** The product grid selectors. */
	private ProductGridSelectors productGridSelectors;

	/** componentValue. */

	@ValueMapValue(name = "componentValue", injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(values = "dimmerView")
	private String componentValue;

	/** The admin service. */
	@Inject
	protected AdminService adminService;
	/** The ProductFamilyDetailService */
	@Inject
	private ProductFamilyDetailService productFamilyDetailService;

	/** The Tag Manager. */
	@Inject
	protected TagManager tagManager;

	/** The Tag Manager. */
	@ValueMapValue(name = "lampManufacturerLabel", injectionStrategy = InjectionStrategy.OPTIONAL)
	private String lampManufacturerLabel;

	public String getLampManufacturerLabel() {
		return lampManufacturerLabel;
	}

	@ValueMapValue(name = "lampModelNumber", injectionStrategy = InjectionStrategy.OPTIONAL)
	private String lampModelNumber;

	public String getLampModelNumber() {
		return lampModelNumber;
	}

	@SlingObject
	private Resource resource;

	@Inject
	protected Page currentPage;

	@Inject
	protected ValueMap currentPageProperties;

	@Inject
	@Default(values = "33071")
	private String defaultSearchTerm;

	/** The SlingHttpServletRequest */
	@Inject
	@Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	/** The FacetURLBeanService */
	@Inject
	private FacetURLBeanService facetURLBeanService;

	/** The EndecaRequestService */
	@Inject
	private EndecaRequestService endecaRequestService;

	/** The EndecaService */
	@Inject
	private EndecaService endecaService;

	@Inject
	@Via("resource")
	private Resource res;

	protected FacetURLBean facetURLBean;

	protected List<FacetGroupBean> facetGroupList;

	protected List<FacetBean> activeFacetsList = new ArrayList<>();

	protected String currentSelectedTab;

	/** The EatonConfigService */
	@Inject
	protected EatonConfigService configService;

	@ChildResource
	private Resource attributeList;

	@ChildResource
	private Resource filterList;
	/** The attributeDynamicLists from multifield. */
	private Map<String, String> mapObj = new HashMap<>();
	/** The filterDynamicLists from multifield. */
	private Map<String, String> mapFilterObject = new HashMap<>();
	/** The attributeList from multifield. */
	private List<Attribute> attributeLists;
	/** The attributeValueList */
	private List<String> attributeValueList = new ArrayList<>();
	/** The filterList from multifield. */
	private List<Filter> filterLists = new ArrayList<>();

	List<Attribute> attributeL;

	/** The Cloud Config Service */
	@Inject
	protected CloudConfigService cloudConfigService;

	/** The Eaton Site Config Service */
	@Inject
	protected EatonSiteConfigService eatonSiteConfigService;

	private boolean isAuthenticated = false;

	@PostConstruct
	public void init() {

		LOG.debug("ProductCompatibilityModel : This is Entry into init() method");

		if (null != resource && adminService != null) {

			try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
				facetedHeaderNavigationResultBean = new FacetedHeaderNavigationResultBean();
				facetedNavigationHelperV2 = new FacetedNavigationHelperV2();
				if (null != filterList) {
					populateFilterDataFromDialog(filterList);
				}
				fetchProductCompatibilityData(adminResourceResolver);

				if (null != attributeList) {
					populateAttributeDataFromDialog(attributeList);
				}
				finalAttributeList = populateFinalAttributeList(familyModuleBeanList);
				if (null != configService) {
					skuPageName = configService.getConfigServiceBean().getSkupagename();
				}

				populateBaseSKUPath();
				populateLoadMoreJson();

			} catch (Exception e) {
				LOG.error("Product Compativbility Model Admin service catch block: exception", e);
			}

		}
	}

	/* Populate base SKU path. */
	private void populateBaseSKUPath() {
		baseSKUPath = CommonUtil.getSKUPagePath(currentPage, skuPageName);
	}

	/**
	 * Populate fetchProductCompatibilityData.
	 * This methods makes a call to endeca and gets the response
	 *
	 * @param adminResourceResolver adminResourceResolver
	 */
	public void fetchProductCompatibilityData(ResourceResolver adminResourceResolver) {
		String dlgReqpath = slingRequest.getRequestURI();
		String defaultTerm = null;
		if (defaultSearchTerm != null) {
			defaultTerm = defaultSearchTerm;
		}
		if (currentPage == null && dlgReqpath.contains("_cq_dialog.html")) {
			String currentPagePath = StringUtils.substringBetween(dlgReqpath, "/_cq_dialog.html", "/jcr:content");
			String componentResourcePath = StringUtils.substringAfter(dlgReqpath, "/_cq_dialog.html");
			if (componentResourcePath != null) {
				Resource cmpResc = adminResourceResolver.getResource(componentResourcePath);
				if (cmpResc != null) {
					ValueMap props = cmpResc.getValueMap();
					if (props.get("defaultSearchTerm", String.class) != null) {
						defaultTerm = props.get("defaultSearchTerm", String.class);
					}
				}
			}

			if (currentPagePath != null) {
				PageManager pm = adminResourceResolver.adaptTo(PageManager.class);
				currentPage = pm.getPage(currentPagePath);
			}
		}
		if (eatonSiteConfigService != null) {
			Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);
			if (siteConfig.isPresent()) {
				siteResourceSlingModel = siteConfig.get();

				final FacetURLBeanServiceResponse facetURLBeanResponse = facetURLBeanService.getFacetURLBeanResponse(
						slingRequest.getRequestPathInfo().getSelectors(), siteResourceSlingModel.getPageSize(),
						pageType, currentPage != null ? currentPage.getPath() : null);
				if (null != facetURLBeanResponse) {
					facetURLBean = facetURLBeanResponse.getFacetURLBean();
					productGridSelectors = facetURLBeanResponse.getProductGridSelectors();
				} else {
					productGridSelectors = null;
				}
			} else {
				siteResourceSlingModel = null;
				LOG.error("Product grid Model Site config was not authored : siteResourceSlingModel is null");
			}
		}
		if (endecaRequestService != null && null != currentPage && configService != null) {

			String searchTerm = StringUtils.EMPTY;
			if (null != productGridSelectors) {
				searchTerm = StringUtils.isNotBlank(productGridSelectors.getSearchTerm())
						? productGridSelectors.getSearchTerm()
						: defaultTerm;
			}
			final List<String> activeFacets = (productGridSelectors != null && productGridSelectors.getFacets() != null)
					? productGridSelectors
							.getFacets().stream().map(facet -> facet.getFacetID()).collect(Collectors.toList())
					: new ArrayList<>();

			final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
					.getProductCompatibilityEndecaRequestBean(currentPage, searchTerm, filterLists, activeFacets);
			final ProductCompatibilityListResponseBean skuDetailsResponse = endecaService
					.getProductCompatibilitySkuList(endecaServiceRequestBean, compatibilityExcelTable);
			familyModuleBeanList = skuDetailsResponse.getFamilyModuleResponse().getFamilyModule();
			int totalResultCnt = skuDetailsResponse.getFamilyModuleResponse().getTotalCount();
			FacetsBean allReturnedFacetsBean;
			allReturnedFacetsBean = skuDetailsResponse.getFamilyModuleResponse().getFacets();
			if (allReturnedFacetsBean != null) {
				setI18NValues();
				facetGroupList = allReturnedFacetsBean.getFacetGroupList();
				if (facetGroupList != null) {
					for (FacetGroupBean facetGroupBean : facetGroupList) {
						if (mapFilterObject.containsKey(facetGroupBean.getFacetGroupLabel())) {
							facetGroupBean
									.setFacetGroupLabel(mapFilterObject.get(facetGroupBean.getFacetGroupLabel()));
						}

					}
				}

			} else {
				facetGroupList = new ArrayList<>();
			}
			if (productGridSelectors != null) {
				activeFacetsList = populateActiveFacetsBeanList(productGridSelectors, facetURLBean,
						facetGroupList);
			}

			facetedHeaderNavigationResultBean.setTotalCount(totalResultCnt);

			compatibilityExcelTable = skuDetailsResponse.getCompatibilityTableHeaders();
			if (siteResourceSlingModel != null && facetedHeaderNavigationResultBean
					.getTotalCount() > siteResourceSlingModel.getPageSize()) {
				loadMoreFlag = TRUE;
			} else {
				loadMoreFlag = FALSE;
			}
			populateDialogDropdown(adminResourceResolver, slingRequest);

		}
	}

	/**
	 * Populate populateAttributeDataFromDialog.
	 * This methods reads all multifield values authored for attribute and builds
	 * alist
	 * This
	 *
	 * @return list
	 */
	public List<ProductCompatibilityModuleBean> populateFinalAttributeList(
			List<ProductCompatibilityModuleBean> familyModuleBeanList) {

		if (resource != null) {
			finalAttributeList = new ArrayList<>();
			for (int i = 0; i < familyModuleBeanList.size(); i++) {
				ProductCompatibilityModuleBean newBean = new ProductCompatibilityModuleBean();
				ProductCompatibilityModuleBean existingBean = familyModuleBeanList.get(i);
				for (int j = 0; j < attributeValueList.size(); j++) {
					String attributeValue = attributeValueList.get(j);
					if ("Mktg_Desc".equalsIgnoreCase(attributeValue)) {
						newBean.setMktgDesc(existingBean.getMktgDesc());
						newBean.setMktgDescLabel(mapObj.get("Mktg_Desc"));
					}
					if ("Country".equalsIgnoreCase(attributeValue)) {
						newBean.setCountry(existingBean.getCountry());
						newBean.setCountryLabel(mapObj.get("Country"));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_EXT_LAMP_MODEL_NUMBER)) {
						newBean.setExtLampModelNumber(existingBean.getExtLampModelNumber());
						newBean.setExtLampModelNumberLabel(mapObj.get(CommonConstants.TST_EXT_LAMP_MODEL_NUMBER));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_EXT_TYPE)) {
						newBean.setExtType(existingBean.getExtType());
						newBean.setExtTypeLabel(mapObj.get(CommonConstants.TST_EXT_TYPE));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_CONTROL_FAMILY)) {
						newBean.setControlFamily(existingBean.getControlFamily());
						newBean.setControlFamilyLabel(mapObj.get(CommonConstants.TST_CONTROL_FAMILY));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_FAMILY_ID)) {
						newBean.setFamilyID(existingBean.getFamilyID());
						newBean.setFamilyIDLabel(mapObj.get(CommonConstants.TST_FAMILY_ID));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_LOWES_ITEMS_NUMBER)) {
						newBean.setLowesItemNumber(existingBean.getLowesItemNumber());
						newBean.setLowesItemNumberLabel(mapObj.get(CommonConstants.TST_LOWES_ITEMS_NUMBER));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_EXT_LAMP_MODEL_NUMBER)) {
						newBean.setLowesModelNumber(existingBean.getLowesModelNumber());
						newBean.setLowesModelNumberLabel(mapObj.get(CommonConstants.TST_EXT_LAMP_MODEL_NUMBER));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_LOW_END_TRIM)) {
						newBean.setLowEndTrim(existingBean.getLowEndTrim());
						newBean.setLowEndTrimLabel(mapObj.get(CommonConstants.TST_LOW_END_TRIM));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_MAX_OF_LOADS)) {
						newBean.setMaxofloads(existingBean.getMaxofloads());
						newBean.setMaxofloadsLabel(mapObj.get(CommonConstants.TST_MAX_OF_LOADS));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_EXT_IMAGE_URL)) {
						newBean.setExtImageURL(existingBean.getExtImageURL());
						newBean.setExtImageURLLabel(mapObj.get(CommonConstants.TST_EXT_IMAGE_URL));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_EXT_LAMP_VOLTAGE)) {
						newBean.setExtLampVoltage(existingBean.getExtLampVoltage());
						newBean.setExtLampVoltageLabel(mapObj.get(CommonConstants.TST_EXT_LAMP_VOLTAGE));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_EXT_COLOR_TEMPERATURE)) {
						newBean.setExtColorTemperature(existingBean.getExtColorTemperature());
						newBean.setExtColorTemperatureLabel(mapObj.get(CommonConstants.TST_EXT_COLOR_TEMPERATURE));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_LED_COMPATIBLE_SCORE)) {
						newBean.setLedCompatibleScore(existingBean.getLedCompatibleScore());
						newBean.setLedCompatibleScoreLabel(mapObj.get(CommonConstants.TST_LED_COMPATIBLE_SCORE));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_EXT_BASE)) {
						newBean.setExtBase(existingBean.getExtBase());
						newBean.setExtBaseLabel(mapObj.get(CommonConstants.TST_EXT_BASE));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_EXT_DATE_CODE)) {
						newBean.setExtDateCode(existingBean.getExtDateCode());
						newBean.setExtDateCodeLabel(mapObj.get(CommonConstants.TST_EXT_DATE_CODE));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_EXT_LAMP_LUMEN_OUTPUT)) {
						newBean.setExtLampLumenOutput(existingBean.getExtLampLumenOutput());
						newBean.setExtLampLumenOutputLabel(mapObj.get(CommonConstants.TST_EXT_LAMP_LUMEN_OUTPUT));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_FLICKER)) {
						newBean.setFlicker(existingBean.getFlicker());
						newBean.setFlickerLabel(mapObj.get(CommonConstants.TST_FLICKER));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_LED_RATING)) {
						newBean.setLedRating(existingBean.getLedRating());
						newBean.setLedRatingLabel(mapObj.get(CommonConstants.TST_LED_RATING));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_LOW_END)) {
						newBean.setLowEnd(existingBean.getLowEnd());
						newBean.setLowEndLabel(mapObj.get(CommonConstants.TST_LOW_END));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_RELATIVE_LOW_END)) {
						newBean.setRelativeLowEnd(existingBean.getRelativeLowEnd());
						newBean.setRelativeLowEndLabel(mapObj.get(CommonConstants.TST_RELATIVE_LOW_END));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_PERCEIVED_DIMMING_RANGE)) {
						newBean.setPerceivedDimmingrange(existingBean.getPerceivedDimmingrange());
						newBean.setPerceivedDimmingrangeLabel(mapObj.get(CommonConstants.TST_PERCEIVED_DIMMING_RANGE));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_PERCEIVED_LOW_END)) {
						newBean.setPerceivedLowend(existingBean.getPerceivedLowend());
						newBean.setPerceivedLowendLabel(mapObj.get(CommonConstants.TST_PERCEIVED_LOW_END));
					}
					if (attributeValue.equalsIgnoreCase(CommonConstants.TST_TEST_NOTES)) {
						newBean.setTestNotes(existingBean.getTestNotes());
						newBean.setTestNotesLabel(mapObj.get(CommonConstants.TST_TEST_NOTES));
					}
				}
				newBean.setLEDImageURL(existingBean.getExtImageURL());
				newBean.setExtModelNumber(existingBean.getExtModelNumber());
				newBean.setExtLampManufacturer(existingBean.getExtLampManufacturer());
				newBean.setDimmerCatalogNumber(existingBean.getDimmerCatalogNumber());
				newBean.setDsktopRendition(existingBean.getDsktopRendition());
				newBean.setMobileRendition(existingBean.getMobileRendition());
				newBean.setLongDesc(existingBean.getLongDesc());

				finalAttributeList.add(newBean);
				LOG.info("finalAttributeList{}:", finalAttributeList);
			}

		}
		return this.finalAttributeList;
	}

	/**
	 * Populate populateAttributeDataFromDialog.
	 * This methods reads all multifield values authored for attribute and builds
	 * alist
	 * This
	 *
	 * @param resource -the resource
	 * @return List
	 */
	public List<Attribute> populateAttributeDataFromDialog(Resource resource) {
		if (resource != null) {
			attributeLists = new ArrayList<>();

			Iterator<Resource> childResources = resource.listChildren();
			while (childResources.hasNext()) {
				Resource childResource = childResources.next();
				if (childResource != null) {

					Attribute attribute = new Attribute();
					ValueMap properties = childResource.getValueMap();
					if (properties.get(ATTRIBUTE_LABEL, String.class) != null) {
						attribute.setAttributeLabel(properties.get(ATTRIBUTE_LABEL, String.class));
					}
					if (properties.get(ATTRIBUTE_VALUE, String.class) != null) {
						attribute.setAttributeVaue(properties.get(ATTRIBUTE_VALUE, String.class));
						attributeValueList.add(properties.get(ATTRIBUTE_VALUE, String.class));
					}
					attributeLists.add(attribute);
					mapObj.put(properties.get(ATTRIBUTE_VALUE, String.class),
							properties.get(ATTRIBUTE_LABEL, String.class));
				}
			}
		}
		return this.attributeLists;
	}

	/**
	 * Populate populateFilterDataFromDialog.
	 * This methods reads all multifield values authored for filter and builds alist
	 * This
	 *
	 * @param resource -the resource
	 * @return List
	 */
	public List<Filter> populateFilterDataFromDialog(Resource resource) {
		if (resource != null) {
			filterLists = new ArrayList<>();
			Iterator<Resource> childResources = resource.listChildren();
			while (childResources.hasNext()) {
				Resource childResource = childResources.next();
				if (childResource != null) {
					Filter filter = new Filter();
					ValueMap properties = childResource.getValueMap();
					if (properties.get(FILTER_ATTRIBUTE_LABEL, String.class) != null) {
						filter.setFilterAttributeLabel(properties.get(FILTER_ATTRIBUTE_LABEL, String.class));
					}
					if (properties.get(FILTER_ATTRIBUTE_VALUE, String.class) != null) {
						filter.setFilterAttributeVaue(properties.get(FILTER_ATTRIBUTE_VALUE, String.class));
					}
					mapFilterObject.put(properties.get(FILTER_ATTRIBUTE_VALUE, String.class),
							properties.get(FILTER_ATTRIBUTE_LABEL, String.class));
					filterLists.add(filter);

				}
			}
		}
		return this.filterLists;
	}

	/**
	 * Populate Data source dropdown.
	 * This methods reads all multifield values authored for filter and builds alist
	 * This
	 *
	 * @param adminResourceResolver adminResourceResolver
	 * @param request request
	 *
	 */
	public void populateDialogDropdown(ResourceResolver adminResourceResolver, SlingHttpServletRequest request) {
		List<Resource> elementResourceList = new ArrayList<>();
		ValueMap vm = null;
		for (int i = 0; i < compatibilityExcelTable.size(); i++) {
			vm = new ValueMapDecorator(new HashMap<>());
			String value = compatibilityExcelTable.get(i);
			String text = compatibilityExcelTable.get(i);
			vm.put("value", value);
			vm.put("text", text);

			elementResourceList.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(),
					JcrConstants.NT_UNSTRUCTURED, vm));
		}

		DataSource dataSource = new SimpleDataSource(elementResourceList.iterator());
		request.setAttribute(DataSource.class.getName(), dataSource);

	}

	/**
	 * Populate active facets bean list.
	 */
	private static List<FacetBean> populateActiveFacetsBeanList(ProductGridSelectors productGridSelectors,
			FacetURLBean facetURLBean, List<FacetGroupBean> facetGroupList) {
		LOG.debug("ProductGridModel : This Entry populateActiveFacetsBeanList() method");
		ArrayList<FacetBean> activeFacetsList = new ArrayList<>();
		List<FacetBean> activeFacetsReceivedList = productGridSelectors.getFacets();
		if (activeFacetsReceivedList != null) {
			for (FacetBean activeFacetsRecieved : activeFacetsReceivedList) {
				activeFacetsRecieved.setLabel(activeFacetsRecieved.getFacetID());
				activeFacetsRecieved.setFacetDeselectURL(facetURLBean.getContentPath()
						+ activeFacetsRecieved.getFacetDeselectURL() + facetURLBean.getFacetEndURL());
				if (facetGroupList != null) {
					for (FacetGroupBean facetGroupBean : facetGroupList) {
						List<FacetValueBean> facetValueList = facetGroupBean.getFacetValueList();
						for (FacetValueBean facetValueBean : facetValueList) {
							if (activeFacetsRecieved.getFacetID() != null && facetValueBean.getFacetValueId() != null
									&& activeFacetsRecieved.getFacetID().equals(facetValueBean.getFacetValueId())) {
								activeFacetsRecieved.setLabel(facetValueBean.getFacetValueLabel());
							}
						}
					}
				}
				activeFacetsList.add(activeFacetsRecieved);
			}
		}
		LOG.debug("ProductGridModel : This Exit populateActiveFacetsBeanList() method");
		return activeFacetsList;
	}

	/**
	 * Sets the I 18 N values.
	 */
	private void setI18NValues() {
		LOG.debug("ProductGridModel : This is Entry into setI18NValues() method");
		final String results = "Results";
		final String clearFilter = "Clear Filters";
		final String narrowResults = "Narrow Results";
		final String viewMore = "View More";
		final String viewLess = "View Less";
		final String newString = "New";
		final String bestSeller = "Best Seller";
		final String removeFilters = "Remove Filters";
		final String apply = "Apply";
		final String filters = "Filters";
		final String loadMore = "Load More";
		final String specification = "Specification";
		final String resourceStr = "Resource";

		try {

			facetedHeaderNavigationResultBean.setResults(
					CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_RESULTS, results));

			facetedHeaderNavigationResultBean.setRelevance(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, FACETS_KEY_RELEVENCE, RELEVANCESTR));

			facetedHeaderNavigationResultBean.setClearFilters(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, FACETS_KEY_CLEAR_FILTERS, clearFilter));
			facetedHeaderNavigationResultBean.setNarrowResults(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, FACETS_KEY_NARROW_RESULTS, narrowResults));
			facetedHeaderNavigationResultBean.setViewMore(
					CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_VIEW_MORE, viewMore));
			facetedHeaderNavigationResultBean.setViewLess(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, VGCommonConstants.VIEW_LESS, viewLess));
			facetedHeaderNavigationResultBean.setNewKeyword(
					CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_NEW, newString));
			facetedHeaderNavigationResultBean.setBestSeller(
					CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_BESTSELLER, bestSeller));
			facetedHeaderNavigationResultBean.setRemoveFilters(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, FACETS_KEY_REMOVE_FILTERS, removeFilters));
			facetedHeaderNavigationResultBean
					.setApply(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_APPLY, apply));
			facetedHeaderNavigationResultBean.setFilters(
					CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_FILTERS, filters));
			loadMoreLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CARD_KEY_LOAD_MORE,
					loadMore);
			specificationTabTitle = CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, DIGITALTOOL_SPECIFICATION_TAB, specification);
			resourceTabTitle = CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, DIGITALTOOL_RESOURCE_TAB, resourceStr);
		} catch (Exception e) {
			LOG.error(String.format("Exception in getting I18n values: %s ", e));
		}
		LOG.debug("ProductGridModel : This is Exit from setI18NValues() method");
	}

	private void populateLoadMoreJson() {
		LOG.debug("Product Compatibility Model : This Entry populateLoadMoreJson() method");

		try {
			JSONObject loadMoreAttrJson = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONObject languageJson = new JSONObject();
			JSONObject countryJson = new JSONObject();
			JSONObject pageTypeJson = new JSONObject();
			JSONObject primarySubCategoryTagJson = new JSONObject();
			JSONObject pageSizeJson = new JSONObject();
			JSONObject defaultSortOrderJson = new JSONObject();
			JSONObject defaultSortFacetJson = new JSONObject();
			JSONObject activeFacetsJson = new JSONObject();
			JSONObject currentLoadMoreJson = new JSONObject();
			JSONObject currentSortByJson = new JSONObject();
			JSONObject currentSortFacetJson = new JSONObject();
			JSONObject currentResourcePathJson = new JSONObject();
			JSONObject pimPrimaryImageJson = new JSONObject();
			JSONObject inventoryIdJson = new JSONObject();
			JSONObject selectorsJson = new JSONObject();
			JSONObject requestUriJson = new JSONObject();

			JSONObject currentPagePathJson = new JSONObject();
			JSONObject fallBackImageJson = new JSONObject();
			JSONObject longDecCheckJson = new JSONObject();
			JSONObject productTypeJson = new JSONObject();

			if (componentValue != null) {
				JSONObject componentValueJson = new JSONObject();
				componentValueJson.put(CommonConstants.COMPONENTVIEW, componentValue);
				jsonArray.add(componentValueJson);

				JSONObject componentJson = new JSONObject();
				componentJson.put(CommonConstants.COMPONENT, "compatible-product-tool");
				jsonArray.add(componentJson);
			}

			Locale languageValue = currentPage.getLanguage(false);
			if (languageValue != null && ((languageValue.getLanguage() != null)
					&& (!(StringUtils.EMPTY).equals(languageValue.getLanguage())))
					&& StringUtils.isNotEmpty(languageValue.getCountry())) {

				String country = "";
				country = languageValue.getCountry();
				languageJson.put(CommonConstants.CURRENT_LANGUAGE,
						CommonUtil.getUpdatedLocaleFromPagePath(currentPage));
				Page countryPage = currentPage.getAbsoluteParent(2);
				String countryString = countryPage.getName();
				if ((!country.equalsIgnoreCase(countryString))
						&& (!(CommonConstants.LANGUGAGEMASTERS).equals(countryString))) {
					country = countryString.toUpperCase();
				}
				countryJson.put(CommonConstants.CURRENTCOUNTRY, country);

			}

			pageSizeJson.put(CommonConstants.PAGESIZE, siteResourceSlingModel.getPageSize());

			currentLoadMoreJson.put(CommonConstants.CURRENTLOADMORE, productGridSelectors.getLoadMoreOption());

			currentResourcePathJson.put(CommonConstants.CURRENTRESOURCEPATH, res.getPath());
			if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
				pimPrimaryImageJson.put(CommonConstants.PIMPRIMARYIMAGE, productFamilyDetails.getPrimaryImage());

			}
			String[] tempSelectorsArray = null;
			String tempSelectors = StringUtils.EMPTY;
			if (slingRequest.getRequestPathInfo().getSelectors() != null) {
				tempSelectorsArray = slingRequest.getRequestPathInfo().getSelectors();
			}
			if (tempSelectorsArray != null && tempSelectorsArray.length > 0) {
				for (int k = 0; k < tempSelectorsArray.length; k++) {
					if (k == tempSelectorsArray.length - 1) {
						tempSelectors = tempSelectors.concat(tempSelectorsArray[k]);
					} else {
						tempSelectors = tempSelectors.concat(tempSelectorsArray[k] + CommonConstants.PIPE);
					}
				}
			} else {
				//in case no selectors are present, add the minimum selector searchTerm
				tempSelectors = SEARCHTERM_LABEL + this.defaultSearchTerm;
			}
			selectorsJson.put(CommonConstants.SELECTORS, tempSelectors);
			requestUriJson.put(CommonConstants.REQUESTURI, slingRequest.getRequestURI());
			if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
				productTypeJson.put(CommonConstants.PRODUCTTYPE, productFamilyDetails.getProductType());
				String skuCardAttr = StringUtils.EMPTY;

				if ((productFamilyDetails.getSkuCardAttributeList() != null)
						&& (!productFamilyDetails.getSkuCardAttributeList().isEmpty())) {
					for (int i = 0; i < productFamilyDetails.getSkuCardAttributeList().size(); i++) {

						if (i == 3) {
							break;
						}
						if (i == 2) {
							skuCardAttr = skuCardAttr.concat(productFamilyDetails.getSkuCardAttributeList().get(i));
						} else {
							skuCardAttr = skuCardAttr.concat(
									productFamilyDetails.getSkuCardAttributeList().get(i) + CommonConstants.PIPE);
						}
					}
				}

			}
			currentPagePathJson.put(CommonConstants.CURRENTPAGEPATH, currentPage.getPath());
			fallBackImageJson.put(CommonConstants.FALLBACKIMAGE, siteResourceSlingModel.getSkuFallBackImage());

			jsonArray.add(languageJson);
			jsonArray.add(countryJson);
			jsonArray.add(pageTypeJson);
			jsonArray.add(primarySubCategoryTagJson);
			jsonArray.add(pageSizeJson);
			jsonArray.add(defaultSortOrderJson);
			jsonArray.add(defaultSortFacetJson);
			jsonArray.add(activeFacetsJson);
			jsonArray.add(currentLoadMoreJson);
			jsonArray.add(currentSortByJson);
			jsonArray.add(currentSortFacetJson);
			jsonArray.add(currentResourcePathJson);
			jsonArray.add(pimPrimaryImageJson);
			jsonArray.add(inventoryIdJson);
			jsonArray.add(selectorsJson);
			jsonArray.add(requestUriJson);
			jsonArray.add(currentPagePathJson);
			jsonArray.add(fallBackImageJson);
			jsonArray.add(longDecCheckJson);
			jsonArray.add(productTypeJson);

			loadMoreAttrJson.put(CommonConstants.DATAATTRIBUTE, jsonArray);
			loadMoreAttrJsonString = loadMoreAttrJson.toJSONString();
		} catch (Exception e) {
			LOG.error(String.format("Exception in creating parameter for Load More Servlet: %s ", e));
		}
		LOG.debug("ProductGridModel : This Exit populateLoadMoreJson() method");

	}

	/**
	 * Gets the family module bean list.
	 *
	 * @return the family module bean list
	 */
	public List<ProductCompatibilityModuleBean> getFamilyModuleBeanList() {
		return familyModuleBeanList;
	}

	public List<FacetGroupBean> getFacetGroupList() {
		return facetGroupList;
	}

	public List<FacetBean> getActiveFacetsList() {
		return activeFacetsList;
	}

	public FacetURLBean getFacetURLBean() {
		return facetURLBean;
	}

	public String getFacetGroupListJson() {
		String facetGroupListJson = facetedNavigationHelperV2.getFacetGroupListJson(currentPage, facetGroupList,
				activeFacetsList, facetURLBean, currentSelectedTab, isAuthenticated);
		LOG.debug("ProductGridModel facet {}", facetGroupListJson);
		return facetGroupListJson;
	}

	/**
	 * Gets the facet value count for siteconfig.
	 *
	 * @return the facetvaluecount
	 */

	public int getFacetValueCount() {
		/** Facet value count from site config */
		return siteResourceSlingModel.getFacetValueCount();

	}

	public String getLoadMoreFlag() {
		return loadMoreFlag;
	}

	public ProductFamilyDetails getProductFamilyDetails() {
		return productFamilyDetails;
	}

	public String getLoadMoreLabel() {
		return loadMoreLabel;
	}

	public String getComponentValue() {
		return componentValue;
	}

	public String getBaseSKUPath() {
		return baseSKUPath;
	}

	public String getSkuPageName() {
		return skuPageName;
	}

	public String getLoadMoreAttrJsonString() {
		return loadMoreAttrJsonString;
	}

	public String getSpecificationTabTitle() {
		return specificationTabTitle;
	}

	public String getResourceTabTitle() {
		return resourceTabTitle;
	}

	/**
	 * Gets the site resource sling model.
	 *
	 * @return the site resource sling model
	 */
	public SiteResourceSlingModel getSiteResourceSlingModel() {
		return siteResourceSlingModel;
	}

	/**
	 * Gets the filterLists in the order specified in the component dialog.
	 *
	 * @return list of filters from dialog
	 */
	public List<Filter> getFilterLists() {
		return this.filterLists;
	}
	/**
	 * Gets the faceted header navigation result bean.
	 *
	 * @return the faceted header navigation result bean
	 */
	public FacetedHeaderNavigationResultBean getFacetedHeaderNavigationResultBean() {
		return facetedHeaderNavigationResultBean;
	}

	public String getFacetGroupOrdered() throws EatonApplicationException {

		JSONArray facetsArray = facetedNavigationHelperV2.getFacetGroupListJsonArray(currentPage, facetGroupList, activeFacetsList, facetURLBean, currentSelectedTab, false);

		ProductCompatibilityFacetGroupList productCompatibilityFacetGroupList = new ProductCompatibilityFacetGroupList(facetsArray.toJSONString(), filterLists);
		String facetsArrOrderedStr = productCompatibilityFacetGroupList.orderedFacetList();

		LOG.debug("ProductGridModel facet ordered by the order in the dialog {}", facetsArrOrderedStr);
		String finalResultFacetsArrOrdered = CommonUtil.encode(facetsArrOrderedStr, CommonConstants.UTF_8);
		return finalResultFacetsArrOrdered;
	}

}
