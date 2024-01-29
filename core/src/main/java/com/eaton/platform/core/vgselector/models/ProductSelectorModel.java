package com.eaton.platform.core.vgselector.models;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.FacetURLBean;
import com.eaton.platform.core.bean.FacetedHeaderNavigationResultBean;
import com.eaton.platform.core.bean.SortByOptionValueBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.helpers.FacetedNavigationHelperV2;
import com.eaton.platform.core.models.ProductSelectorResultsModel;
import com.eaton.platform.core.models.vgSelector.ConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.core.vgselector.utils.VGSelectorUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchTool;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueTool;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * <html> Description: This class is invoked on the Product Selector Results
 * sightly component</html> .
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductSelectorModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ProductSelectorModel.class);

	/** The selector tool type. */
	private String selectorToolType;

	/** The country. */
	private String country;

	/** The sort option. */
	private String sortOption = "asc";

	/** The endeca service request bean. */
	private EndecaServiceRequestBean endecaServiceRequestBean = null;

	/** The clutch tool list. */
	private List<ClutchTool> clutchToolList;

	/** The torque tool list. */
	private List<TorqueTool> torqueToolList;

	/** The facets values. */
	private List<String> facetsValues;

	/** The FaceGroup Of Facet Value Selected */
	List<String> selectedFacetsValueGroupList;

	/** The facet list. */
	private List<FacetBean> facetList;

	/** The load more attr json string. */
	private String loadMoreAttrJsonString;

	/** The faceted header navigation result bean. */
	private FacetedHeaderNavigationResultBean facetedHeaderNavigationResultBean;
	
	private FacetedNavigationHelperV2 facetedNavigationHelperV2;

	/** The site resource sling model. */
	private ConfigModel siteResourceSlingModel;

	/** The base SKU path. */
	String baseSKUPath;

	/** The sku page name. */
	private String skuPageName;

	/** Load More Flag. */
	private String loadMoreFlag;

	/** The load more label. */
	private String loadMoreLabel;

	/** The return facets for. */
	List<String> returnFacetsFor = new ArrayList<>();

	/** The sku list response bean. */
	SKUListResponseBean skuListResponseBean;

	/** The sku card parameters. */
	List<String> skuCardParameters = new ArrayList<>();
	
	/** The ProductSelectorResultsModel. */
	ProductSelectorResultsModel productSelectorResultsModel;

	/** The language. */
	String language;

	/** The page size. */
	int pageSize;

	/** AdminService. */
	@Inject
	protected AdminService adminService;

	/** EndecaRequestService. */
	@Inject
	protected EndecaRequestService endecaRequestService;

	/** EndecaService. */
	@Inject
	protected EndecaService endecaService;

	/** Page. */
	@Inject
	protected Page currentPage;
	
	/** The SlingHttpServletRequest */
	@Inject
	SlingHttpServletRequest slingRequest;

	/** Resource. */
	@Inject
	private Resource res;
	
	/**hideGlobalFacetSearch */
	@Inject
	private String hideGlobalFacetSearch;
	
	/**SlingScriptHelper */
	@Inject
	protected ConfigurationManagerFactory configManagerFctry;
	
	/**FacetURLBean */
	protected FacetURLBean facetURLBean;
	
	/**List of FacetGroupBean */
	protected List<FacetGroupBean> facetGroupList;
	
	/**List of FacetBean */
	protected List<FacetBean> activeFacetsList;

	/**currentSelectedTab */
	protected String currentSelectedTab;
	
	/**ResourceResolver */
	protected ResourceResolver resourceResolver;
	
	/**Externalizer */
	protected Externalizer externalizer;
	
	/**EatonConfigService */
	@Inject
	protected EatonConfigService endecaConfigService;
	
	/**EatonConfig */
	@Inject
	protected EndecaConfig endecaConfig;
	
	/**Facet value count from site config*/
	private int facetValueCount;
	
	@PostConstruct
	public void init() {
		LOG.debug("ProductSelectorModel : This is Entry into init() method");
		if(res!=null)
		{
		productSelectorResultsModel = res.adaptTo(ProductSelectorResultsModel.class);
			if(null!=productSelectorResultsModel){
			   hideGlobalFacetSearch = productSelectorResultsModel.getHideGlobalFacetSearch();
			}
		}
		facetedHeaderNavigationResultBean = new FacetedHeaderNavigationResultBean();
		
			if (null != endecaConfigService) {
				skuPageName = endecaConfigService.getConfigServiceBean().getSkupagename();
			}
			if (null != adminService) {
				try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
					populateSiteConfiguration(adminResourceResolver);
					selectorToolType = siteResourceSlingModel.getSelectorToolType();
					parseURL();
					populateEndecaData(adminResourceResolver);
					setI18NValues();
					populateBaseSKUPath(adminResourceResolver);
					initialRadioFacets(adminResourceResolver);
					populateActiveFacetsBeanList();
					if (facetedHeaderNavigationResultBean.getTotalCount() > siteResourceSlingModel.getPageSize()) {
						loadMoreFlag = CommonConstants.TRUE;
					} else {
						loadMoreFlag = CommonConstants.FALSE;
					}
					loadMoreLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
							VGCommonConstants.CARD_KEY_LOAD_MORE, VGCommonConstants.LOAD_MORE_STRING);
					populateLoadMoreJson();
				}
			}
			
			facetedNavigationHelperV2=new FacetedNavigationHelperV2();
			
			LOG.debug("ProductSelectorModel : This is exit from init() method");
	}

	/**
	 * Initial radio facets.
	 * @param adminResourceResolver
	 */
	private void initialRadioFacets(ResourceResolver adminResourceResolver) {
		if (facetGroupList != null) {
			List<FacetGroupBean> updatedFacetGroupList = new ArrayList<>();

			for (int i = 0; i < facetGroupList.size(); i++) {
				FacetGroupBean productTypeFacetGroupBeanItem = facetGroupList.get(i); // getFacetValueList
				FacetGroupBean productTypeFacetGroupBean = setFacetGroupLabel(productTypeFacetGroupBeanItem, adminResourceResolver);
				List<FacetValueBean> facetValueList = productTypeFacetGroupBean.getFacetValueList();
				List<FacetValueBean> updatedfacetValueList = new ArrayList<>();
				for (int k = 0; k < facetValueList.size(); k++) {
					FacetValueBean facetValueBean = facetValueList.get(k);
					facetValueBean.setFacetStartURL(facetURLBean.getExtensionIdStartURL());
					facetValueBean.setFacetEndURL(facetURLBean.getExtensionIdEndURL());
					if (facetValueBean.getFacetValueId() != null && facetsValues != null
							&& facetsValues.contains(facetValueBean.getFacetValueId())) {
						facetValueBean.setActiveRadioButton(VGCommonConstants.RADIO_CHECKED);
					} else {
						facetValueBean.setActiveRadioButton(VGCommonConstants.RADIO_UNCHECKED);
					}
					updatedfacetValueList.add(facetValueBean);
				}
				if (!updatedfacetValueList.isEmpty()) {
					productTypeFacetGroupBean.setFacetValueList(updatedfacetValueList);
					updatedFacetGroupList.add(productTypeFacetGroupBean);
				}
			}
			facetGroupList = updatedFacetGroupList;
		}
	}

	/**
	 * Populate load more json.
	 */
	@SuppressWarnings("unchecked")
	private void populateLoadMoreJson() {

		try {
			JSONObject loadMoreAttrJson = new JSONObject();
			JSONArray jsonArray = new JSONArray();

			JSONObject formTypeJson = new JSONObject();
			JSONObject languageJson = new JSONObject();
			JSONObject countryJson = new JSONObject();
			JSONObject pageSizeJson = new JSONObject();
			JSONObject defaultSortOrderJson = new JSONObject();
			JSONObject currentLoadMoreJson = new JSONObject();
			JSONObject currentSortByJson = new JSONObject();
			JSONObject currentResourcePathJson = new JSONObject();
			JSONObject requestUriJson = new JSONObject();
			JSONObject skuCardParametersJson = new JSONObject();
			JSONObject currentPagePathJson = new JSONObject();
			JSONObject returnFacetsForJson = new JSONObject();
			JSONObject facetsValuesJson = new JSONObject();
			JSONObject fallBackImageJson = new JSONObject();

			if (StringUtils.EMPTY.equals(language)) {
				languageJson.put(VGCommonConstants.CURRENT_LANG, language); // language
			}
			if (!StringUtils.EMPTY.equals(country) && country != null) {
				countryJson.put(VGCommonConstants.CURRENT_COUNTRY, country); // country
			}
			currentResourcePathJson.put(VGCommonConstants.CURRENT_RESOURCE_PATH, res.getPath());
			requestUriJson.put(VGCommonConstants.REQUEST_URI, slingRequest.getRequestURI());
			currentPagePathJson.put(VGCommonConstants.CURRENT_PAGE_PATH, currentPage.getPath());
			String formType = siteResourceSlingModel.getSelectorToolType();
			formTypeJson.put(VGCommonConstants.FORM_TYPE, formType);
			pageSizeJson.put(VGCommonConstants.PAGE_SIZE, siteResourceSlingModel.getPageSize());
			fallBackImageJson.put(VGCommonConstants.FALL_BACK_IMAGE, siteResourceSlingModel.getFallbackImage());

			if (StringUtils.equals(VGCommonConstants.CLUTCH_PAGE, selectorToolType)
					|| StringUtils.equals(VGCommonConstants.TORQUE_PAGE, selectorToolType)) {
				defaultSortOrderJson.put(VGCommonConstants.DEFAULT_SORT_ORDER, VGCommonConstants.ASCENDING_SORT);
			}
			currentLoadMoreJson.put(VGCommonConstants.CURRENT_LOAD_MORE, VGCommonConstants.ZERO); // need to get the value
			currentSortByJson.put(VGCommonConstants.CURRENT_SORT_OPTION, sortOption);

			String facetsValuesStr = StringUtils.EMPTY;
			if (facetsValues != null) {
				facetsValuesStr = String.join(VGCommonConstants.COMMA, facetsValues);
			}
			facetsValuesJson.put(VGCommonConstants.FACET_VALUES, facetsValuesStr);

			String returnFacetsForStr = StringUtils.EMPTY;
			if (returnFacetsFor != null) {
				returnFacetsForStr = String.join(VGCommonConstants.COMMA, returnFacetsFor);
			}
			returnFacetsForJson.put(VGCommonConstants.RETURN_FACETS_FOR, returnFacetsForStr);
			String skuCardParametersStr = StringUtils.EMPTY;
			skuCardParametersStr = String.join(VGCommonConstants.COMMA, skuCardParametersStr);
			skuCardParametersJson.put(VGCommonConstants.SKU_CARD_PARAMETERS_LOAD_MORE, skuCardParametersStr);
			jsonArray.add(languageJson);
			jsonArray.add(countryJson);
			jsonArray.add(formTypeJson);
			jsonArray.add(pageSizeJson);
			jsonArray.add(defaultSortOrderJson);
			jsonArray.add(currentLoadMoreJson);
			jsonArray.add(currentSortByJson);
			jsonArray.add(currentResourcePathJson);
			jsonArray.add(skuCardParametersJson);
			jsonArray.add(requestUriJson);
			jsonArray.add(currentPagePathJson);
			jsonArray.add(facetsValuesJson);
			jsonArray.add(returnFacetsForJson);
			jsonArray.add(fallBackImageJson);

			loadMoreAttrJson.put(VGCommonConstants.DATA_ATTRIBUTE, jsonArray);
			loadMoreAttrJsonString = loadMoreAttrJson.toJSONString();
			loadMoreAttrJsonString = loadMoreAttrJsonString.replace(VGCommonConstants.AMPERSAND_SIGN,
					VGCommonConstants.AMPERSAND_STRING);
		} catch (Exception e) {
			LOG.error("Exception in creating parameter for Load More Servlet " , e);
		}
	}

	/**
	 * Populate active facets bean list.
	 */
	private void populateActiveFacetsBeanList() {
		activeFacetsList = new ArrayList<>();
		selectedFacetsValueGroupList = new ArrayList<>();
		List<FacetBean> activeFacetsRecievedList = facetList;

		if (activeFacetsRecievedList != null) {
			for (int i = 0; i < activeFacetsRecievedList.size(); i++) {
				FacetBean activeFacetsRecieved = activeFacetsRecievedList.get(i);
				activeFacetsRecieved.setLabel(activeFacetsRecieved.getFacetID());
				activeFacetsRecieved.setFacetDeselectURL(facetURLBean.getContentPath()
						+ activeFacetsRecieved.getFacetDeselectURL() + facetURLBean.getFacetEndURL());
				if (facetGroupList != null) {
					setActiveFacetRecievedLabelAndToList(activeFacetsRecieved);
				}
				activeFacetsList.add(activeFacetsRecieved);
				slingRequest.setAttribute(VGCommonConstants.ACTIVE_FACET_LIST, activeFacetsList);
			}
		}
	}

	/**
	 * @param activeFacetsRecieved
	 */
	private void setActiveFacetRecievedLabelAndToList(FacetBean activeFacetsRecieved) {
		for (int j = 0; j < facetGroupList.size(); j++) {
			FacetGroupBean facetGroupBean = facetGroupList.get(j);
			List<FacetValueBean> facetValueList = facetGroupBean.getFacetValueList();

			for (int k = 0; k < facetValueList.size(); k++) {
				FacetValueBean facetValueBean = facetValueList.get(k);
				if (activeFacetsRecieved.getFacetID() != null && facetValueBean.getFacetValueId() != null
						&& activeFacetsRecieved.getFacetID().equals(facetValueBean.getFacetValueId())) {
					activeFacetsRecieved.setLabel(facetValueBean.getFacetValueLabel());
					selectedFacetsValueGroupList.add(facetGroupList.get(j).getFacetGroupLabel());
				}
			}
		}
	}

	/**
	 * Populate base SKU path.
	 */
	private void populateBaseSKUPath(ResourceResolver resourceResolver) {
		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		homePagePath = CommonUtil.dotHtmlLinkSKU(homePagePath, resourceResolver);
		baseSKUPath = homePagePath + skuPageName;
	}

	/**
	 * Parses the URL.
	 */
	private void parseURL() {

		LOG.debug("Entry into parseURL method");

		String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
		String contentPath = slingRequest.getRequestURI();
		String[] contentPathList = contentPath.split(VGCommonConstants.DOT_DELIMITER);
		facetsValues = new ArrayList<>();
		facetURLBean = new FacetURLBean();

		if ((contentPathList != null) && (contentPathList.length > 0)) {
			final String basecontentPath = contentPathList[0];
			facetURLBean.setContentPath(getExternalizedPath(basecontentPath));
		}
		facetURLBean.setExtensionIdEndURL(VGCommonConstants.HTML_CONSTANT);
		facetURLBean.setFacetStartURL(VGCommonConstants.FACET_SELECTOR);
		facetURLBean.setFacetEndURL(VGCommonConstants.HTML_CONSTANT);
		facetURLBean.setSortStartURL(VGCommonConstants.SORT_SELECTOR);
		facetURLBean.setSortEndURL(VGCommonConstants.HTML_CONSTANT);
		facetURLBean.setLoadMoreStartURL(VGCommonConstants.LOADMORE_CONSTANT);
		facetURLBean.setLoadMoreEndURL(VGCommonConstants.LOAD_MORE_DEFAULT);

		if (null != selectors) {
			String facetStartURL = StringUtils.EMPTY;

			for (int i = 0; i < selectors.length; i++) {
				String selector = selectors[i];

				if (selector.startsWith(VGCommonConstants.FACETS)) {
					facetStartURL = selector;
					facetURLBean.setFacetStartURL(VGCommonConstants.DOT + facetStartURL);

					String sortStartURL = StringUtils.EMPTY;
					String loadMoreStartURL = StringUtils.EMPTY;
					if ((facetURLBean.getFacetStartURL() != null)
							&& (!facetURLBean.getFacetStartURL().equals(StringUtils.EMPTY))) {
						sortStartURL = facetURLBean.getFacetStartURL() + VGCommonConstants.SORT_SELECTOR;
						loadMoreStartURL = facetURLBean.getFacetStartURL() + VGCommonConstants.LOADMORE_CONSTANT;
					}

					facetURLBean.setSortStartURL(sortStartURL);
					facetURLBean.setLoadMoreStartURL(loadMoreStartURL);

					String[] facets = selectors[i].split(VGCommonConstants.DOLLOR_DELIMITER);
					if ((facets != null) && (facets.length > 1)) {
						facetList = new ArrayList<>();

						for (int j = 1; j < facets.length; j++) {
							FacetBean facetBean = new FacetBean();
							facetBean.setFacetID(facets[j]);
							String deselectURL = VGCommonConstants.DOT
									+ selector.replace(VGCommonConstants.DOLLOR + facets[j], StringUtils.EMPTY);
							if ((deselectURL != null) && (deselectURL.equals(VGCommonConstants.FACET_SELECTOR))) {
								facetBean.setFacetDeselectURL(StringUtils.EMPTY);
							} else {
								facetBean.setFacetDeselectURL(deselectURL);
							}
							facetList.add(facetBean);

							if (StringUtils.isNotBlank(facets[j])) {
								facetsValues.add(facets[j]);
							}
						}
					}
				} else if (selector.startsWith(VGCommonConstants.FACETS_KEY_SORT)) {

					String loadMoreStartURL = StringUtils.EMPTY;
					if ((facetStartURL != null) && (!facetStartURL.equals(StringUtils.EMPTY))) {
						loadMoreStartURL = VGCommonConstants.DOT + facetStartURL;
					}
					loadMoreStartURL = loadMoreStartURL + VGCommonConstants.DOT + selector
							+ VGCommonConstants.LOADMORE_CONSTANT;
					facetURLBean.setLoadMoreStartURL(loadMoreStartURL);

					String facetEndURL = VGCommonConstants.DOT + selector + VGCommonConstants.HTML_CONSTANT;
					facetURLBean.setFacetEndURL(facetEndURL);

					String[] sortByOptions = selectors[i].split(VGCommonConstants.DOLLOR_DELIMITER);
					if ((null != sortByOptions) && (sortByOptions.length > 1)) {
						sortOption = sortByOptions[1];
					}
				}
			}
		}
		LOG.debug("Exit from parseURL method");
	}

	/**
	 * Populate site configuration.
	 * @return the config model
	 */
	public ConfigModel populateSiteConfiguration(ResourceResolver adminResourceResolver) {
		LOG.debug("Entry into populateSiteConfiguration method");

		Resource siteCloudConfigRes = null;
		String formContentPath = getSubmitButtonPagePath(adminResourceResolver);
		// get site configuration object
		Resource currentPageResourse = adminResourceResolver.getResource(formContentPath);
		Configuration configObj = CommonUtil.getCloudConfigObj(configManagerFctry, adminResourceResolver,
				currentPageResourse, VGCommonConstants.VG_SITE_CLOUD_CONFIG_NODE_NAME);
		if (null != configObj) {
			siteCloudConfigRes = adminResourceResolver.resolve(
					configObj.getPath().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_STR));
			siteResourceSlingModel = siteCloudConfigRes.adaptTo(ConfigModel.class);
		}

		LOG.debug("Exit from populateSiteConfiguration method");
		return siteResourceSlingModel;
	}

	/**
	 * Populate endeca data.
	 * @param adminResourceResolver
	 */
	public void populateEndecaData(ResourceResolver adminResourceResolver) {
		if (null != endecaService) {
			try {
				endecaServiceRequestBean = new EndecaServiceRequestBean();
				// set the language in the endeca request
				setLanguageRequestBean();
				Resource formPathResource = prepareStandaloneFormResource(adminResourceResolver);
				returnFacetsFor = VGSelectorUtil.getFormElementNames(formPathResource, adminResourceResolver,
							VGCommonConstants.ENDECA_RESULT, selectorToolType);
				if (null != siteResourceSlingModel) {
					addAdditonalFacetInReturnFacetFor();
				}
				if (facetsValues.isEmpty()) {
					facetsValues.add(StringUtils.EMPTY);
				}
				endecaServiceRequestBean = VGSelectorUtil.populateEndecaServiceRequestBean(res, adminResourceResolver,
						endecaConfig, endecaServiceRequestBean, selectorToolType,
						VGCommonConstants.ENDECA_RESULT, returnFacetsFor, skuCardParameters, facetsValues, country, 0,
						pageSize, sortOption);

				LOG.debug("Endeca Request : {}", endecaServiceRequestBean);
				// Populate Clutch Tool Response
				if (StringUtils.equals(VGCommonConstants.CLUTCH_PAGE, selectorToolType)) {
					populateClutchToolResponse(adminResourceResolver);
				} else if (StringUtils.equals(VGCommonConstants.TORQUE_PAGE, selectorToolType)) {
					populateTorquePage(adminResourceResolver);
				}
				if (facetGroupList != null) {
					facetGroupList.stream().forEach((facetGroupBean -> facetGroupBean.setSingleFacetEnabled(true)));
				}
			} catch (Exception e) {
				LOG.error(String.format("Exception while fetching response from endeca %s" , e.getMessage()));
			}
		} else {
			LOG.error("Issue in getting Endeca Service instance");
		}

		LOG.debug("VGProductSelectorHelper :: populateEndecaData() :: End");
	}

	/**
	 * @param adminResourceResolver
	 */
	private void populateTorquePage(ResourceResolver adminResourceResolver) {
		TorqueSelectorResponse torqueSelectorResponseBean = endecaService
				.getTorqueToolDetails(endecaServiceRequestBean);
		LOG.debug(" Torque Details Response from Endeca : {}", torqueSelectorResponseBean);
		if ((null != torqueSelectorResponseBean)
				&& (null != torqueSelectorResponseBean.getTorqueToolResponse())
				&& (null != torqueSelectorResponseBean.getTorqueToolResponse().getStatus())
				&& (torqueSelectorResponseBean.getTorqueToolResponse().getStatus()
						.equalsIgnoreCase(VGCommonConstants.ENDECA_STATUS_SUCCESS))) {
			if (null != torqueSelectorResponseBean.getTorqueToolResponse().getFacets()) {
				facetGroupList = torqueSelectorResponseBean.getTorqueToolResponse().getFacets()
						.getFacetGroupList();
				if (null != facetGroupList) {
					List<FacetGroupBean> updatedFacetGroupList = new ArrayList<>();
					for (int i = 0; i < facetGroupList.size(); i++) {

						FacetGroupBean facetGroupBean = facetGroupList.get(i);
						facetGroupBean = setFacetGroupLabel(facetGroupBean, adminResourceResolver);
						updatedFacetGroupList.add(facetGroupBean);

						if (StringUtils.equals(VGCommonConstants.YEAR,
								facetGroupBean.getFacetGroupLabel())) {
							for (int k = 0; k < facetGroupBean.getFacetValueList().size(); k++) {
								facetGroupBean.getFacetValueList().get(k).setFacetValueId(
										facetGroupBean.getFacetValueList().get(k).getFacetValueLabel()
												+ VGCommonConstants.YEAR);
							}
						} else if (StringUtils.equals(VGCommonConstants.MAKE,
								facetGroupBean.getFacetGroupLabel())) {
							for (int k = 0; k < facetGroupBean.getFacetValueList().size(); k++) {
								facetGroupBean.getFacetValueList().get(k).setFacetValueId(
										facetGroupBean.getFacetValueList().get(k).getFacetValueLabel()
												+ VGCommonConstants.MAKE);
							}
						} else if (StringUtils.equals(VGCommonConstants.MODEL,
								facetGroupBean.getFacetGroupLabel())) {
							for (int k = 0; k < facetGroupBean.getFacetValueList().size(); k++) {
								facetGroupBean.getFacetValueList().get(k).setFacetValueId(
										facetGroupBean.getFacetValueList().get(k).getFacetValueLabel()
												+ VGCommonConstants.MODEL);
							}
						}
					}
					facetGroupList = updatedFacetGroupList;
				}
			}

			torqueToolList = torqueSelectorResponseBean.getTorqueToolResponse().getTorqueSearchResults();

			// add the encoded catalog number to the list
			List<TorqueTool> encodedFamilyModuleBeanList = new ArrayList<>();
			if (null != torqueToolList) {
				for (int i = 0; i < torqueToolList.size(); i++) {
					TorqueTool tempBean = torqueToolList.get(i);
					if (tempBean.getCatalogNumber() != null
							&& StringUtils.isNotBlank(tempBean.getCatalogNumber())) {
						tempBean.setEncodedCatalogNumber(
								CommonUtil.encodeURLString(tempBean.getCatalogNumber()));
					}
					encodedFamilyModuleBeanList.add(tempBean);
				}
				torqueToolList = encodedFamilyModuleBeanList;
				facetedHeaderNavigationResultBean
						.setTotalCount(torqueSelectorResponseBean.getTorqueToolResponse().getTotalCount());
			}
		}
	}

	/**
	 * Method populateClutchToolResponse
	 * @param adminResourceResolver
	 */
	private void populateClutchToolResponse(ResourceResolver adminResourceResolver) {
		ClutchSelectorResponse clutchSelectorResponseBean = endecaService
				.getClutchToolDetails(endecaServiceRequestBean);
		LOG.debug(" Clutch Details Response from Endeca : {}", clutchSelectorResponseBean);
		if ((null != clutchSelectorResponseBean)
				&& (null != clutchSelectorResponseBean.getClutchToolResponse())
				&& (null != clutchSelectorResponseBean.getClutchToolResponse().getStatus())
				&& (clutchSelectorResponseBean.getClutchToolResponse().getStatus()
						.equalsIgnoreCase(VGCommonConstants.ENDECA_STATUS_SUCCESS))) {
			if (null != clutchSelectorResponseBean.getClutchToolResponse().getFacets()) {
				facetGroupList = clutchSelectorResponseBean.getClutchToolResponse().getFacets()
						.getFacetGroupList();
				if (null != facetGroupList) {
					List<FacetGroupBean> updatedFacetGroupList = new ArrayList<>();
					for (int i = 0; i < facetGroupList.size(); i++) {

						FacetGroupBean facetGroupBeans = facetGroupList.get(i);
						FacetGroupBean facetGroupBean = setFacetGroupLabel(facetGroupBeans, adminResourceResolver);
						updatedFacetGroupList.add(facetGroupBean);
					}
					facetGroupList = updatedFacetGroupList;
				}
			}
			clutchToolList = clutchSelectorResponseBean.getClutchToolResponse().getClutchSearchResults();

			// add the encoded catalog number to the list
			List<ClutchTool> encodedFamilyModuleBeanList = new ArrayList<>();
			addEncodeCatalogNumberToList(encodedFamilyModuleBeanList);
			clutchToolList = encodedFamilyModuleBeanList;
			facetedHeaderNavigationResultBean
					.setTotalCount(clutchSelectorResponseBean.getClutchToolResponse().getTotalCount());
		}
	}

	/**
	 * @param encodedFamilyModuleBeanList
	 */
	private void addEncodeCatalogNumberToList(List<ClutchTool> encodedFamilyModuleBeanList) {
		for (int i = 0; i < clutchToolList.size(); i++) {

			ClutchTool tempBean = clutchToolList.get(i);
			if (null != tempBean.getCatalogNumber()
					&& StringUtils.isNotBlank(tempBean.getCatalogNumber())) {
				tempBean.setEncodedCatalogNumber(
						CommonUtil.encodeURLString(tempBean.getCatalogNumber()));
			}
			encodedFamilyModuleBeanList.add(tempBean);
		}
	}

	/**
	 * @throws ParseException
	 */
	private void addAdditonalFacetInReturnFacetFor() throws ParseException {
		// add the additional facets in "returnFacetsFor" list
		String[] additionalFacets = siteResourceSlingModel.getAdditional_facets();
		if (returnFacetsFor.isEmpty()) {
			String[] returnFacets = siteResourceSlingModel.getReturnFacetsFor();
			returnFacetsFor = prepareList(returnFacetsFor, returnFacets, VGCommonConstants.RETURN_FACETS);
		}
		returnFacetsFor = prepareList(returnFacetsFor, additionalFacets, VGCommonConstants.FACETS);

		// prepare the sku-card parameter list for endeca request
		String[] skuCards = siteResourceSlingModel.getSkucardattributes();
		skuCardParameters = prepareList(skuCardParameters, skuCards, VGCommonConstants.SKU_ATTRIBUTES);

		pageSize = siteResourceSlingModel.getPageSize();
	}

	/**
	 * @param arrayList
	 * @param vgConfigList
	 * @param attributeName
	 * @return
	 * @throws ParseException
	 */
	private List<String> prepareList(List<String> arrayList, String[] vgConfigList, String attributeName)
			throws ParseException {

		if (null != vgConfigList) {
			for (String item : vgConfigList) {

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(item);

				String attributeValue = obj.get(attributeName).toString();
				if (StringUtils.isNotBlank(attributeValue)) {
					arrayList.add(attributeValue);
				}
			}
		}
		return arrayList;
	}

	/**
	 * Prepare standalone form resource.
	 * @return the resource
	 */
	private Resource prepareStandaloneFormResource(ResourceResolver adminResourceResolver) {

		Resource formPathResource = null;

		String formContentPath = getSubmitButtonPagePath(adminResourceResolver);
		String aemFormCompanentPath = formContentPath + VGCommonConstants.AEM_FORM_ROOT_PATH;

		Resource aemFormCompanentResource = adminResourceResolver.resolve(aemFormCompanentPath);
		ValueMap properties = aemFormCompanentResource.getValueMap();
		String standaloneFormPath = CommonUtil.getStringProperty(properties, VGCommonConstants.REFERENCE_FORM_PATH);
		String formPath = StringUtils.replace(standaloneFormPath, VGCommonConstants.DAM_FORM_DOCS,
				VGCommonConstants.REFERENCE_FORM_AF_PATH);
		formPathResource = adminResourceResolver.resolve(formPath + VGCommonConstants.JCR_CONTENT);
		return formPathResource;
	}

	/**
	 * Gets the submit button page path.
	 * @return the submit button page path
	 * @param adminResourceResolver
	 */
	private String getSubmitButtonPagePath(ResourceResolver adminResourceResolver) {
		// get refererURL from request since current page is not available in
		// fixed path servelts
		String refererURL = CommonUtil.getRefererURL(slingRequest);
		// get content path
		return CommonUtil.getContentPath(adminResourceResolver, refererURL);
	}

	/**
	 * Sets the language request bean.
	 */
	public void setLanguageRequestBean() {
		// Setting Language Locale & country
		if (null != currentPage) {
			Locale languageValue = currentPage.getLanguage(false);
			if (languageValue != null && ((languageValue.getLanguage() != null)
					&& (!languageValue.getLanguage().equals(StringUtils.EMPTY)))) {
				if ((languageValue.getCountry() != null) && (!languageValue.getCountry().equals(StringUtils.EMPTY))) {
					country = languageValue.getCountry();
					language = CommonUtil.getUpdatedLocaleFromPagePath(currentPage);
					endecaServiceRequestBean.setLanguage(language);
					Page countryPage = currentPage.getAbsoluteParent(2);
					String countryString = countryPage.getName();
					if ((!country.equalsIgnoreCase(countryString))
							&& (!countryString.equals(VGCommonConstants.LANGUAGE_MASTERS))) {
						country = countryString.toUpperCase();
					}
				}
			}
		}
	}

	/**
	 * Sets the facet group label.
	 * @param facetGroupBean
	 *            the facet group bean
	 * @param adminResourceResolver
	 * @return the facet group bean
	 */
	private FacetGroupBean setFacetGroupLabel(FacetGroupBean facetGroupBean, ResourceResolver adminResourceResolver) {
		Locale locale = currentPage.getLanguage(false);
		String groupID = facetGroupBean.getFacetGroupId();
		TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);
		if (null != tagManager) {
			Resource tagResource = tagManager.getResourceResolver().getResource(VGCommonConstants.VGSELECTOR_TAGS_PATH);
			if (null != tagResource) {
				setFacetLabelFromTag(facetGroupBean, locale, groupID, tagResource);
			}
		}
		return facetGroupBean;
	}

	/**
	 * @param facetGroupBean
	 * @param locale
	 * @param groupID
	 * @param tagResource
	 */
	private void setFacetLabelFromTag(FacetGroupBean facetGroupBean, Locale locale, String groupID,
			Resource tagResource) {
		final Iterable<Resource> children = tagResource.getChildren();
		final Iterator<Resource> itr = children.iterator();
		List<String> tagList = new ArrayList<>();

		while (itr.hasNext()) {
			Resource item = itr.next();
			Tag tag = item.adaptTo(Tag.class);
			if (null != tag) {
				String tagName = tag.getName();
				String tagTitle = tag.getTitle();
				if (StringUtils.isNotBlank(tagTitle) && StringUtils.isNotBlank(tagName)) {
					tagList.add(tag.getTitle());
					if (tagName.equalsIgnoreCase(groupID)) {
						if (null != tag.getLocalizedTitle(locale)) {
							facetGroupBean.setFacetGroupLabel(tag.getLocalizedTitle(locale));
						} else if (null != tag.getTitle()) {
							facetGroupBean.setFacetGroupLabel(tagTitle);
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the clutch tool list.
	 * @return the clutch tool list
	 */
	public List<ClutchTool> getClutchToolList() {
		return clutchToolList;
	}

	/**
	 * Gets the torque tool list.
	 * @return the torque tool list
	 */
	public List<TorqueTool> getTorqueToolList() {
		return torqueToolList;
	}

	/**
	 * Gets the result list.
	 * @return the result list
	 */
	@SuppressWarnings("squid:S1452")
	public List<?> getFamilyModuleBeanList() {
		List<?> viewList = new ArrayList<>();
		if (StringUtils.equals(VGCommonConstants.CLUTCH_PAGE, selectorToolType)) {
			viewList = clutchToolList;
		} else if (StringUtils.equals(VGCommonConstants.TORQUE_PAGE, selectorToolType)) {
			viewList = torqueToolList;
		}
		return viewList;
	}

	/**
	 * Gets the faceted header navigation result bean.
	 * @return the faceted header navigation result bean
	 */
	public FacetedHeaderNavigationResultBean getFacetedHeaderNavigationResultBean() {
		return facetedHeaderNavigationResultBean;
	}

	/**
	 * Sets the I 18 N values.
	 */
	private void setI18NValues() {
		try {
			final String  FACETS_VALUE_SORT="Sort";
			final String  FACETS_VALUE_ASCENDING_SORT="A-Z";
			final String  FACETS_VALUE_DESCENDING_SORT="Z-A";
			final String  FACETS_VALUE_CLEAR_FILTERS="Clear Filters";
			final String  FACETS_VALUE_RESULTS="Results";
			final String  FACETS_VALUE_NARROW_RESULTS="Narrow Results";
			final String  FACETS_VALUE_VIEW_MORE="View More";
			final String  VIEW_LESS_VALUE="View Less";
			final String  FACETS_VALUE_REMOVE_FILTERS="Remove Filters";
			final String  FACETS_VALUE_APPLY="Apply";
			final String  FACETS_VALUE_FILTERS="Filters";
			
			if (StringUtils.equals(VGCommonConstants.TORQUE_PAGE, selectorToolType)) {
				List<SortByOptionValueBean> sortList = setSortByOptionList();
				facetedHeaderNavigationResultBean.setSortList(sortList);
				facetedHeaderNavigationResultBean.setSort(CommonUtil.getI18NFromResourceBundle(slingRequest,
						currentPage, VGCommonConstants.FACETS_KEY_SORT, FACETS_VALUE_SORT));
				facetedHeaderNavigationResultBean.setAssendingSort(CommonUtil.getI18NFromResourceBundle(slingRequest,
						currentPage, VGCommonConstants.FACETS_KEY_ASCENDING_SORT, FACETS_VALUE_ASCENDING_SORT));
				facetedHeaderNavigationResultBean.setDescendingSort(CommonUtil.getI18NFromResourceBundle(slingRequest,
						currentPage, VGCommonConstants.FACETS_KEY_DESCENDING_SORT, FACETS_VALUE_DESCENDING_SORT));
			}
			facetedHeaderNavigationResultBean.setClearFilters(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, VGCommonConstants.FACETS_KEY_CLEAR_FILTERS, FACETS_VALUE_CLEAR_FILTERS));
			facetedHeaderNavigationResultBean.setResults(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
					VGCommonConstants.FACETS_KEY_RESULTS, FACETS_VALUE_RESULTS));
			facetedHeaderNavigationResultBean.setNarrowResults(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, VGCommonConstants.FACETS_KEY_NARROW_RESULTS, FACETS_VALUE_NARROW_RESULTS));
			facetedHeaderNavigationResultBean.setViewMore(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, VGCommonConstants.FACETS_KEY_VIEW_MORE, FACETS_VALUE_VIEW_MORE));
			facetedHeaderNavigationResultBean.setViewLess(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, VGCommonConstants.VIEW_LESS, VIEW_LESS_VALUE));
			facetedHeaderNavigationResultBean.setRemoveFilters(CommonUtil.getI18NFromResourceBundle(slingRequest,
					currentPage, VGCommonConstants.FACETS_KEY_REMOVE_FILTERS, FACETS_VALUE_REMOVE_FILTERS));
			facetedHeaderNavigationResultBean.setApply(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
					VGCommonConstants.FACETS_KEY_APPLY, FACETS_VALUE_APPLY));
			facetedHeaderNavigationResultBean.setFilters(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
					VGCommonConstants.FACETS_KEY_FILTERS, FACETS_VALUE_FILTERS));

		} catch (Exception e) {
			LOG.error("Exception in getting I18n values " , e);
		}
	}

	/**
	 * Sets the sort by option list.
	 * @return the list
	 */
	private List<SortByOptionValueBean> setSortByOptionList() {

		List<SortByOptionValueBean> sortList = new ArrayList<>();
		SortByOptionValueBean sortByOptionValueBean1 = new SortByOptionValueBean();
		SortByOptionValueBean sortByOptionValueBean2 = new SortByOptionValueBean();
		sortByOptionValueBean1.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
				VGCommonConstants.FACETS_KEY_ASCENDING_SORT, VGCommonConstants.DISPLAY_ASCENDING_SORT));
		sortByOptionValueBean2.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
				VGCommonConstants.FACETS_KEY_DESCENDING_SORT, VGCommonConstants.DISPLAY_DESCENDING_SORT));
		sortByOptionValueBean1.setValue(VGCommonConstants.ASCENDING_SORT);
		sortByOptionValueBean2.setValue(VGCommonConstants.DESCENDING_SORT);
		sortList.add(sortByOptionValueBean1);
		sortList.add(sortByOptionValueBean2);

		return sortList;
	}

	/**
	 * Gets the show sort for torque.
	 *
	 * @return the show sort for torque
	 */
	public boolean getShowSortForTorque() {
		boolean showSort = true;
		if (StringUtils.equals(VGCommonConstants.CLUTCH_PAGE, selectorToolType)) {
			showSort = false;
		}
		return showSort;
	}

	/**
	 * Gets the base SKU path.
	 * @return the base SKU path
	 */
	public String getBaseSKUPath() {
		return baseSKUPath;
	}

	/**
	 * Gets the selector tool type.
	 * @return the selector tool type
	 */
	public String getSelectorToolType() {
		return selectorToolType;
	}

	/**
	 * Gets the site resource sling model.
	 * @return the site resource sling model
	 */
	public ConfigModel getSiteResourceSlingModel() {
		return siteResourceSlingModel;
	}

	/**
	 * Gets the load more flag.
	 * @return the load more flag
	 */
	public String getLoadMoreFlag() {
		return loadMoreFlag;
	}

	/**
	 * Gets the load more attr json string.
	 * @return the loadMoreAttrJsonString
	 */
	public String getLoadMoreAttrJsonString() {
		return loadMoreAttrJsonString;
	}

	/**
	 * Gets the load more label.
	 * @return the loadMoreLabel
	 */
	public String getLoadMoreLabel() {
		return loadMoreLabel;
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
	     return facetedNavigationHelperV2.getFacetGroupListJson(currentPage,facetGroupList,activeFacetsList,facetURLBean,currentSelectedTab, false);
	}

	public String getFacetGroupOrderingJson() {
		return facetedNavigationHelperV2.getFacetGroupOrderingJson(currentPage,facetGroupList,activeFacetsList,facetURLBean,currentSelectedTab, false);
	}
	
	public String getActiveFacetsListJson() {
		return facetedNavigationHelperV2.getActiveFacetsListJson(activeFacetsList);
	}
	/**
	 * @return
	 */
	public List<String> getSelectedFacetsValueGroupList() {
		return this.selectedFacetsValueGroupList;
	}
	/**
	 * @param internalPath
	 * @return
	 */
	protected String getExternalizedPath(String internalPath) {
		return externalizer != null && internalPath != null
				? externalizer.relativeLink(slingRequest, internalPath)
				: internalPath;
	}
	
	/**
	 * Gets the facet value count for siteconfig.
	 *
	 * @return the facetvaluecount
	 */
	
	public int getFacetValueCount() {
		facetValueCount = siteResourceSlingModel.getFacetValueCount();
		return facetValueCount;
	}
}
