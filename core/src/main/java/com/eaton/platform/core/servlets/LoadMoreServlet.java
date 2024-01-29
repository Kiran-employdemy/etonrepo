package com.eaton.platform.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.Servlet;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.FacetURLBean;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.bean.loadmore.SKUListLoadMore;
import com.eaton.platform.core.bean.loadmore.SubCategoryLoadMore;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ProductGridSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.models.productgrid.DigitalAttributeOperator;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.factories.SecureFilterBeanFactory;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityListResponseBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityModuleBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.eaton.platform.core.bean.loadmore.Image;
import com.eaton.platform.core.bean.loadmore.Link;
import com.eaton.platform.core.bean.loadmore.ProductAttribute;
import com.eaton.platform.core.bean.loadmore.ProductLinks;
import com.eaton.platform.core.bean.loadmore.SKUListContentItem;
import com.eaton.platform.core.bean.loadmore.SKUListResultsList;
import com.eaton.platform.core.bean.loadmore.SubCategoryContentItem;
import com.eaton.platform.core.bean.loadmore.SubCategoryResults;
import com.eaton.platform.integration.endeca.bean.familymodule.FamilyModuleBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUCardParameterBean;
import com.eaton.platform.integration.endeca.bean.subcategory.ProductFamilyBean;

import com.eaton.platform.core.models.productcompatibilitytool.Attribute;

import static com.eaton.platform.core.constants.CommonConstants.PRODUCT_TYPE_FACET_NAME;

import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import com.eaton.platform.integration.auth.models.AuthenticationToken;


/**
 * LoadMoreServlet
 *
 */
@Component(service = Servlet.class, immediate = true, property = {
		ServletConstants.SLING_SERVLET_METHODS_GET,
		ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/loadmore",
})
public class LoadMoreServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(LoadMoreServlet.class);

	private static final String SEARCH = "search";
	private static final String COUNTRY_CONSTANT = "Country";
	private static final String HTML_CONSTANT = ".html";
	private static final String LOADMORE_CONSTANT = ".loadmore";
	private static final String DEFAULT_FALLBACK_SORT = "asc";

	private static final String CURRENT_LANGUAGE = "currentLanguage";
	private static final String CURRENT_COUNTRY = "currentCountry";
	private static final String CURRENT_PAGE_PATH = "currentPagePath";
	private static final String CURRENT_RESOURCE_PATH = "currentResourcePath";
	private static final String CURRENT_SORTBY_OPTION = "currentSortByOption";
	private static final String DEFAULT_SORT_FACET = "defaultSortFacet";
	private static final String DEFAULT_SORT_ORDER = "defaultSortOrder";
	private static final String FALL_BACK_IMAGE = "fallBackImage";
	private static final String INVENTORY_ID = "inventoryId";
	private static final String LOND_DESC_CHECK = "londDescCheck";
	private static final String PAGE_TYPE = "pageType";
	private static final String PRIMARY_SUB_CATEGORY_TAG = "primarySubCategoryTag";
	private static final String PAGE_SIZE = "pageSize";
	private static final String PRODUCT_TYPE = "productType";
	private static final String REQUEST_URI = "requestUri";
	private static final String SELECTORS_KEY = "selectors";
	private static final String SKU_CARD_ATTR = "skuCardAttr";
	private static final String FACET  = "Facets";
	private static final String RETURNFACETSFOR = "ReturnFacetsFor";
	private static final String ATTRIBUTELABEL = "attributeLabel";
	private static final String ATTRIBUTEVALUE = "attributeVaue";
	private static final String COMPONENT = "component";
	private static final String COMPONENTVIEW = "componentView";
	private String subCategoryHideShow;

	/** The endeca service. */
	@Reference
	private transient EndecaService endecaService;
	/** The endeca config service. */
	@Reference
	private transient EndecaConfig endecaConfigService;

	@Reference
	private transient ResourceResolverFactory resolverFactory;

	@Reference
	private transient AdminService adminService;

	@Reference
	private transient EatonConfigService configService;
	
	@Reference
    private transient AuthenticationService authenticationService; 

    @Reference
    private transient AuthenticationServiceConfiguration authenticationServiceConfig;

	private String jsonResponse;
	private transient ProductGridSelectors productGridSelectors;

	private transient Page currentPage;
	private EndecaConfigServiceBean endecaConfigBean;

	/** The attributeList from multifield. */
	private transient List<Attribute> attributeLists;

	/** The attributeValueList */
	private List<String> attributeValueList;

	/** The attributeDynamicLists from multifield. */
	private Map<String, String> mapObj = new HashMap<>();

	private String lampManufacturerLabel;

	private String lampModelNumber;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		LOG.info("******** LoadMore Servlet execution started ***********");
		PageManager pagemanager;
		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			String jsonfilename;
			String PRICE_LIST_PATH;
			String SKU_PAGE_NAME;
			String rootElementName;
			String language = null;
			String country = null;
			String componentView = null;
			String component = null;
			String pageType = null;
			String primarySubCategoryTag = null;
			String pageSize = null;
			String defaultSortOrder = null;
			String defaultSortFacet = null;
			String currentSortBy = null;
			String currentResourcePath = null;
			String inventoryId = null;
			String selectors = null;
			String requestUri = null;
			String skuCardAttr = null;
			String currentPagePath = null;
			String fallBackImage = null;
			String londDescCheck = null;
			String productType = null;
			int totalCount = 0;
			String countString;

			String loadMoreJsonString = request.getParameter("url");
			countString = request.getParameter("count");
			JSONParser parser = new JSONParser();
			JSONObject paramJson = new JSONObject();
			JSONArray valuesJSONarray = new JSONArray();

			PRICE_LIST_PATH = configService.getConfigServiceBean().getPriceListFolderPath();
			SKU_PAGE_NAME = configService.getConfigServiceBean().getSkupagename();
			jsonfilename = configService.getConfigServiceBean().getJsonFileName();
			rootElementName = configService.getConfigServiceBean().getJsonRootElementName();
			pagemanager = adminResourceResolver.adaptTo(PageManager.class);
			paramJson = (JSONObject) parser.parse(loadMoreJsonString);

			if (paramJson.containsKey("dataAttribute")) {
				valuesJSONarray = (JSONArray) paramJson.get("dataAttribute");

				for (int i = 0; i < valuesJSONarray.size(); i++) {
					JSONObject innerJson = (JSONObject) valuesJSONarray.get(i);
					if (innerJson.containsKey(CURRENT_LANGUAGE) && innerJson.get(CURRENT_LANGUAGE) != null) {
						language = innerJson.get(CURRENT_LANGUAGE).toString();
					} else if (innerJson.containsKey(CURRENT_COUNTRY) && innerJson.get(CURRENT_COUNTRY) != null) {
						country = innerJson.get(CURRENT_COUNTRY).toString();
					} else if (innerJson.containsKey(PAGE_TYPE) && innerJson.get(PAGE_TYPE) != null) {
						pageType = innerJson.get(PAGE_TYPE).toString();
					} else if (innerJson.containsKey(PRIMARY_SUB_CATEGORY_TAG)
							&& innerJson.get(PRIMARY_SUB_CATEGORY_TAG) != null) {
						primarySubCategoryTag = innerJson.get(PRIMARY_SUB_CATEGORY_TAG).toString();
					} else if (innerJson.containsKey(PAGE_SIZE) && innerJson.get(PAGE_SIZE) != null) {
						pageSize = innerJson.get(PAGE_SIZE).toString();
					} else if (innerJson.containsKey(DEFAULT_SORT_ORDER) && innerJson.get(DEFAULT_SORT_ORDER) != null) {
						defaultSortOrder = innerJson.get(DEFAULT_SORT_ORDER).toString();
					} else if (innerJson.containsKey(DEFAULT_SORT_FACET)) {
						defaultSortFacet = innerJson.get(DEFAULT_SORT_FACET) != null
								? innerJson.get(DEFAULT_SORT_FACET).toString()
								: "";
					} else if (innerJson.containsKey(CURRENT_SORTBY_OPTION)
							&& innerJson.get(CURRENT_SORTBY_OPTION) != null) {
						currentSortBy = innerJson.get(CURRENT_SORTBY_OPTION).toString();
					} else if (innerJson.containsKey(CURRENT_RESOURCE_PATH)
							&& innerJson.get(CURRENT_RESOURCE_PATH) != null) {
						currentResourcePath = innerJson.get(CURRENT_RESOURCE_PATH).toString();
					} else if (innerJson.containsKey(INVENTORY_ID) && innerJson.get(INVENTORY_ID) != null) {
						inventoryId = innerJson.get(INVENTORY_ID).toString();
					} else if (innerJson.containsKey(SELECTORS_KEY) && innerJson.get(SELECTORS_KEY) != null) {
						selectors = innerJson.get(SELECTORS_KEY).toString();
					} else if (innerJson.containsKey(REQUEST_URI) && innerJson.get(REQUEST_URI) != null) {
						requestUri = innerJson.get(REQUEST_URI).toString();
					} else if (innerJson.containsKey(SKU_CARD_ATTR) && innerJson.get(SKU_CARD_ATTR) != null) {
						skuCardAttr = innerJson.get(SKU_CARD_ATTR).toString();
					} else if (innerJson.containsKey(CURRENT_PAGE_PATH) && innerJson.get(CURRENT_PAGE_PATH) != null) {
						currentPagePath = innerJson.get(CURRENT_PAGE_PATH).toString();
					} else if (innerJson.containsKey(FALL_BACK_IMAGE) && innerJson.get(FALL_BACK_IMAGE) != null) {
						fallBackImage = innerJson.get(FALL_BACK_IMAGE).toString();
					} else if (innerJson.containsKey(LOND_DESC_CHECK) && innerJson.get(LOND_DESC_CHECK) != null) {
						londDescCheck = innerJson.get(LOND_DESC_CHECK).toString();
					} else if (innerJson.containsKey(PRODUCT_TYPE) && innerJson.get(PRODUCT_TYPE) != null) {
						productType = innerJson.get(PRODUCT_TYPE).toString();
					} else if (innerJson.containsKey(COMPONENTVIEW) && innerJson.get(COMPONENTVIEW) != null) {
						componentView = innerJson.get(COMPONENTVIEW).toString();
					} else if (innerJson.containsKey(COMPONENT) && innerJson.get(COMPONENT) != null) {
						component = innerJson.get(COMPONENT).toString();
					}
				}
			}

			if (currentPagePath != null && pagemanager != null) {
				currentPage = pagemanager.getPage(currentPagePath);
			}
			if (StringUtils.isBlank(currentSortBy) && StringUtils.isBlank(selectors)
					&& StringUtils.isNotBlank(defaultSortOrder)) {
				selectors = "sort$" + defaultSortOrder;
			}

			parseURL(selectors, requestUri, pageSize);

			if (componentView != null && component.equals("compatible-product-tool")) {
				EndecaServiceRequestBean endecaServiceRequestBean = populateCompatibleProductToolEndecaServiceRequestBean(
						language,
						pageSize, country, countString,
						currentResourcePath);
				List<String> compatibilityExcelTable = new ArrayList<>();
				final ProductCompatibilityListResponseBean skuDetailsResponse = endecaService
						.getProductCompatibilitySkuList(endecaServiceRequestBean, compatibilityExcelTable);
				totalCount = skuDetailsResponse.getFamilyModuleResponse().getTotalCount();
				if (componentView.equals("dimmerView")) {
					SKUListLoadMore skuListLoadMore = setProductCompatibilityLoadMoreValuesDimmerView(
							skuDetailsResponse.getFamilyModuleResponse().getFamilyModule(),
							SKU_PAGE_NAME, currentPage, fallBackImage,
							request.getResourceResolver());
					jsonResponse = convertSubCategoryBeanTOJSON(skuListLoadMore);
				} else if (componentView.equals("ledView")) {
					populateAttributeDataFromDialog(currentResourcePath);
					SKUListLoadMore skuListLoadMore = setProductCompatibilityLoadMoreValuesLEDView(
							skuDetailsResponse.getFamilyModuleResponse().getFamilyModule());
					jsonResponse = convertSubCategoryBeanTOJSON(skuListLoadMore);
				}

			} else {
				EndecaServiceRequestBean endecaServiceRequestBean = populateEndecaServiceRequestBean(language, pageType,
						primarySubCategoryTag, pageSize, defaultSortOrder, currentSortBy, country, countString,
						defaultSortFacet, currentResourcePath, inventoryId, skuCardAttr, productType, request);
				if ((pageType != null) && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE) || CommonConstants.PAGE_TYPE_LEARN_PAGE.equalsIgnoreCase(pageType)) {
					FamilyListResponseBean familyList = endecaService.getProductFamilyList(endecaServiceRequestBean);
					totalCount = familyList.getResponse().getTotalCount();
					SubCategoryLoadMore subCategoryLoadMore = setSubcategoryLoadMoreValues(
							familyList.getResponse().getProductFamilyBean(),pageType,request);

					jsonResponse = convertSubCategoryBeanTOJSON(subCategoryLoadMore);
				} else if ((pageType != null) && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {

					SKUListResponseBean skuList = endecaService.getSKUList(endecaServiceRequestBean);
					totalCount = skuList.getFamilyModuleResponse().getTotalCount();

					SKUListLoadMore skuListLoadMore = setskuListLoadMoreValues(
							skuList.getFamilyModuleResponse().getFamilyModule(), country, PRICE_LIST_PATH, adminService,
							SKU_PAGE_NAME, currentPage, fallBackImage, jsonfilename, rootElementName,
							londDescCheck, request.getResourceResolver());

					jsonResponse = convertSubCategoryBeanTOJSON(skuListLoadMore);

				}
			}

			int countInt = Integer.parseInt(countString);
			int pageSizeInt = Integer.parseInt(pageSize);
			countInt = countInt + pageSizeInt;
			int indexLast = jsonResponse.lastIndexOf('}');
			String subJson = jsonResponse.substring(0, indexLast);
			String buttonStatus = "active";
			if (countInt + pageSizeInt >= totalCount) {
				buttonStatus = "inActive";
			}
			jsonResponse = subJson + ",\"loadmoreButtonCount\":\"" + countInt + "\",\"buttonStatus\":\"" + buttonStatus
					+ "\"}";

			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().println(jsonResponse);
		} catch (Exception e) {
			LOG.error(String.format("Exception raised at doGet() of LoadMoreServlet: %s", e));
		}

	}

	/**
	 * @param language
	 * @param pageSize
	 * @param country
	 * @param countString
	 * @param currentResourcePath
	 * @return
	 */
	public EndecaServiceRequestBean populateCompatibleProductToolEndecaServiceRequestBean(String language,
			String pageSize, String country, String countString, String currentResourcePath) {
		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();

			if (endecaConfigService != null) {
				
				endecaConfigBean = endecaConfigService.getConfigServiceBean();
			}
			// Setting common attributes of Endeca Request Bean
			if (endecaConfigBean != null) {
				// TBU
				endecaServiceRequestBean.setSearchApplication(endecaConfigService.getConfigServiceBean().getCompatibilityAppName());
				endecaServiceRequestBean.setSearchApplicationKey(endecaConfigService.getConfigServiceBean().getEspAppKey());

			}

			endecaServiceRequestBean.setFunction(SEARCH);
			endecaServiceRequestBean.setLanguage(language);

			if (productGridSelectors != null && (productGridSelectors.getSearchTerm() != null)
					&& (!productGridSelectors.getSearchTerm().isEmpty())) {
				endecaServiceRequestBean.setSearchTerms(productGridSelectors.getSearchTerm());
			}
			// Setting PageSize from Site Configuration
			if (pageSize != null) {
				endecaServiceRequestBean.setNumberOfRecordsToReturn(pageSize);
			}
			List<FilterBean> filters = new ArrayList<>();
			FilterBean filterBean = new FilterBean();
			List<String> filterValues = new ArrayList<>();
			filterBean.setFilterName(COUNTRY_CONSTANT);
			if (StringUtils.isNotEmpty(country)) {
				// TBU
				filterValues.add(country);
			}
			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			filterBean = new FilterBean();
			filterBean.setFilterName(FACET);
			filterValues = new ArrayList<>();

			if (productGridSelectors != null && (productGridSelectors.getFacets() != null)
					&& (!productGridSelectors.getFacets().isEmpty())) {
				List<FacetBean> FacetList = productGridSelectors.getFacets();
				for (int i = 0; i < FacetList.size(); i++) {
					FacetBean facetBean = FacetList.get(i);
					filterValues.add(facetBean.getFacetID());
				}
			}

			if (filterValues.isEmpty()) {
				filterValues.add("");
			}

			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);
			// Setting startingRecordNumber - TBD

			// Setting startingRecordNumber
			if (countString != null && pageSize != null) {
				int countInt = Integer.parseInt(countString);
				int pageSizeInt = Integer.parseInt(pageSize);
				int startNum = countInt + pageSizeInt;

				String startNumString = Integer.toString(startNum);
				endecaServiceRequestBean.setStartingRecordNumber(startNumString);
			} else {
				endecaServiceRequestBean.setStartingRecordNumber("0");
			}

			filterBean = new FilterBean();
			filterBean.setFilterName(RETURNFACETSFOR);
			filterValues = new ArrayList<>();

			if (adminResourceResolver != null) {
				Resource componentResource = adminResourceResolver.getResource(currentResourcePath);
				ValueMap compProperties = componentResource.getValueMap();
				if(compProperties!=null){
					if(compProperties.get("lampManufacturerLabel",String.class)!=null){
						lampManufacturerLabel =compProperties.get("lampManufacturerLabel",String.class);
					}
					if(compProperties.get("lampModelNumber",String.class)!=null){
						lampModelNumber = compProperties.get("lampModelNumber",String.class);
					}
				}
				if (componentResource != null) {
					Resource childResource = componentResource.getChild("filterList");
					if (childResource != null) {
						Iterator<Resource> itemResourceIterator = childResource.listChildren();
						while (itemResourceIterator.hasNext()) {
							Resource itemResource = itemResourceIterator.next();
							ValueMap proeprties = itemResource.getValueMap();
							if (proeprties != null && proeprties.get("filterAttributeVaue", String.class) != null) {

								String filterAttributeValue = proeprties.get("filterAttributeVaue", String.class);
								filterValues.add(filterAttributeValue);

							}

						}
					}
				}
			}

			if (filterValues.isEmpty()) {
				filterValues.add("");
			}

			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			endecaServiceRequestBean.setFilters(filters);

			return endecaServiceRequestBean;
		}

	}

	/**
	 * @param language
	 * @param pageType
	 * @param primarySubCategoryTag
	 * @param pageSize
	 * @param defaultSortOrder
	 * @param currentSortBy
	 * @param country
	 * @param countString
	 * @param defaultSortFacet
	 * @param currentResourcePath
	 * @param inventoryId
	 * @param skuCardAttr
	 * @param productType
	 * @return
	 */
	public EndecaServiceRequestBean populateEndecaServiceRequestBean(String language, String pageType,
			String primarySubCategoryTag, String pageSize, String defaultSortOrder, String currentSortBy,
			String country, String countString, String defaultSortFacet, String currentResourcePath, String inventoryId,
			String skuCardAttr, String productType, SlingHttpServletRequest request) {

		EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();

		if (endecaConfigService != null) {
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
		}
		// Setting common attributes of Endeca Request Bean
		if (endecaConfigBean != null) {
			// TBU
			endecaServiceRequestBean.setSearchApplicationKey(endecaConfigBean.getEspAppKey());
		}

		endecaServiceRequestBean.setFunction(SEARCH);
		endecaServiceRequestBean.setLanguage(language);

		// Populated the EndecaRequestBean for sub-category page and Learn Page
		if ((pageType != null) && (pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE)|| pageType.equals(CommonConstants.PAGE_TYPE_LEARN_PAGE))) {
			populateSubCategoryPageEndecaRequestBean(endecaServiceRequestBean, primarySubCategoryTag, pageSize,
					defaultSortOrder, currentSortBy, country, countString, defaultSortFacet, currentResourcePath);
		} else if ((pageType != null) && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
			populateProductFamilyPageEndecaRequestBean(endecaServiceRequestBean, pageSize, defaultSortOrder,
					defaultSortFacet, countString, inventoryId, skuCardAttr, productType, currentResourcePath, country);
		}
		
		// Populated the EndecaRequestBean with Secure attribute.
		LOG.debug("Adding Secure attribute for Endeca request on Load More");
		String rawJWT = AuthCookieUtil.getJWTFromAuthCookie(request, authenticationServiceConfig);
		if (StringUtils.isNotEmpty(rawJWT)) {
			final AuthenticationToken authenticationToken = authenticationService.parseToken(rawJWT);
			endecaServiceRequestBean.getFilters().addAll(new SecureFilterBeanFactory().createFilterBeans(authenticationToken, SecureModule.PRODUCTGRID));
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("endecaServiceRequestBean :" + endecaServiceRequestBean.toString());
        }
		LOG.debug("Adding Secure attribute for Endeca request on Load More");

		return endecaServiceRequestBean;
	}

	/**
	 * @param endecaServiceRequestBean
	 * @param currentResourcePath
	 * @param defaultSortFacet
	 * @param countString
	 * @param country
	 * @param currentSortBy
	 * @param defaultSortOrder
	 * @param pageSize
	 * @param primarySubCategoryTag
	 */
	public void populateSubCategoryPageEndecaRequestBean(EndecaServiceRequestBean endecaServiceRequestBean,
			String primarySubCategoryTag, String pageSize, String defaultSortOrder, String currentSortBy,
			String country, String countString, String defaultSortFacet, String currentResourcePath) {

		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			if (null != endecaConfigBean) {
				endecaServiceRequestBean.setSearchApplication(endecaConfigBean.getSubcategoryAppName());
			}
			String sortByOption;

			String[] primarySubCategoryTags = null;
			if (primarySubCategoryTag != null) {
				primarySubCategoryTags = primarySubCategoryTag.split("\\|");
			}
			if ((primarySubCategoryTags != null) && (primarySubCategoryTags.length > 0)) {
				TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);
				Tag subCategoryTag = null;
				if (tagManager != null) {
					subCategoryTag = tagManager.resolve(primarySubCategoryTags[0]);
				}
				if (subCategoryTag != null) {
					// TBU
					endecaServiceRequestBean.setSearchTerms(subCategoryTag.getName());
				}
			}
			// Setting PageSize from Site Configuration
			if (pageSize != null) {

				endecaServiceRequestBean.setNumberOfRecordsToReturn(pageSize);
			}
			if (StringUtils.isNotEmpty(defaultSortOrder)) {
				sortByOption = defaultSortOrder;
			} else {
				sortByOption = DEFAULT_FALLBACK_SORT;
			}

			List<FilterBean> filters = new ArrayList<>();
			FilterBean filterBean = new FilterBean();
			List<String> filterValues = new ArrayList<>();
			filters = new ArrayList<>();

			final String applicationId = CommonUtil.getApplicationId(currentPage.getPath());
			if (!applicationId.equals(CommonConstants.APPLICATION_ID_MISSING)) {
				filters.add(createFilterValue(EndecaConstants.APPLICATION_ID, applicationId));
			}

			filterBean = new FilterBean();
			filterBean.setFilterName(COUNTRY_CONSTANT);
			filterValues = new ArrayList<>();
			if (StringUtils.isNotEmpty(country)) {
				// TBU
				filterValues.add(country);
			}
			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			filterBean = new FilterBean();
			filterBean.setFilterName(FACET);
			filterValues = new ArrayList<>();

			if (productGridSelectors != null && (productGridSelectors.getFacets() != null)
					&& (!productGridSelectors.getFacets().isEmpty())) {
				List<FacetBean> FacetList = productGridSelectors.getFacets();
				for (int i = 0; i < FacetList.size(); i++) {
					FacetBean facetBean = FacetList.get(i);
					filterValues.add(facetBean.getFacetID());
				}
			}

			if (filterValues.isEmpty()) {
				filterValues.add("");
			}

			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);
			// Setting startingRecordNumber - TBD

			// Setting startingRecordNumber
			if (countString != null && pageSize != null) {
				int countInt = Integer.parseInt(countString);
				int pageSizeInt = Integer.parseInt(pageSize);
				int startNum = countInt + pageSizeInt;

				String startNumString = Integer.toString(startNum);
				endecaServiceRequestBean.setStartingRecordNumber(startNumString);
			} else {
				endecaServiceRequestBean.setStartingRecordNumber("0");
			}
			// Setting SortBy - TBD
			filterBean = new FilterBean();
			filterBean.setFilterName("SortBy");
			filterValues = new ArrayList<>();
			if (StringUtils.isNotBlank(currentSortBy)) {
				filterValues.add(currentSortBy);
			} else {
				// TBU
				filterValues.add(sortByOption);
			}
			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			// Adding PageType for Filters which is Exclusive for Sub-Category Page /
			// ProductGridComponent
			filterBean = new FilterBean();
			filterBean.setFilterName(CommonConstants.FILTER_PAGE_TYPE);
			filterValues = new ArrayList<>();
			if (primarySubCategoryTags.length > 0) {
				final TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);
				if (null != tagManager) {
					for (int index = 0; index < primarySubCategoryTags.length; index++) {
						final Tag subCategoryTag = tagManager.resolve(primarySubCategoryTags[index]);
						if (null != subCategoryTag && subCategoryTag.getPath().contains(CommonConstants.PAGE_TYPE)) {
							filterValues.add(subCategoryTag.getName());
						}
					}
				}
			} else {
				// Defaulter if the Tags are not Configured
				filterValues.add(StringUtils.EMPTY);
			}
			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			/* Adding SortFacet filter */
			filterBean = new FilterBean();
			filterBean.setFilterName("SortFacet");
			filterValues = new ArrayList<>();
			if (null != productGridSelectors) {
				filterValues.add(StringUtils.isNotBlank(productGridSelectors.getSortFacet())
						? productGridSelectors.getSortFacet()
						: defaultSortFacet);
			}
			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);
			filterBean = new FilterBean();
			filterBean.setFilterName(RETURNFACETSFOR);
			filterValues = new ArrayList<>();

			if (adminResourceResolver != null) {
				Resource productGridResource = adminResourceResolver.getResource(currentResourcePath);
				if (productGridResource != null) {
					ProductGridSlingModel productGrid = productGridResource.adaptTo(ProductGridSlingModel.class);

					if (productGrid != null) {
						filterValues.addAll(productGrid.getFacetTags());
						subCategoryHideShow = productGrid.getSubCategoryHideShow();
					}
				}
			}

			if (filterValues.isEmpty()) {
				filterValues.add("");
			}

			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			endecaServiceRequestBean.setFilters(filters);
		}
	}

	/**
	 * @param endecaServiceRequestBean
	 * @param country
	 * @param currentResourcePath
	 * @param productType
	 * @param skuCardAttr
	 * @param inventoryId
	 * @param countString
	 * @param defaultSortFacet
	 * @param defaultSortOrder
	 * @param pageSize
	 */
	public void populateProductFamilyPageEndecaRequestBean(EndecaServiceRequestBean endecaServiceRequestBean,
			String pageSize, String defaultSortOrder, String defaultSortFacet, String countString, String inventoryId,
			String skuCardAttr, String productType, String currentResourcePath, String country) {

		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			String sortByOption;
			if (null != endecaConfigBean) {
				endecaServiceRequestBean.setSearchApplication(endecaConfigBean.getProductfamilyAppName());
			}
			// Setting PageSize from Site Configuration
			if (pageSize != null) {
				endecaServiceRequestBean.setNumberOfRecordsToReturn(pageSize);
			}
			if (StringUtils.isNotEmpty(defaultSortOrder)) {
				sortByOption = defaultSortOrder;
			} else {
				sortByOption = DEFAULT_FALLBACK_SORT;
			}
			List<FilterBean> filters = new ArrayList<>();
			FilterBean filterBean = new FilterBean();
			List<String> filterValues = new ArrayList<>();
			filters = new ArrayList<>();

			final String applicationId = CommonUtil.getApplicationId(currentPage.getPath());
			if (!applicationId.equals(CommonConstants.APPLICATION_ID_MISSING)) {
				filters.add(createFilterValue(EndecaConstants.APPLICATION_ID, applicationId));
			}

			// Setting SortBy
			filterBean = new FilterBean();
			filterBean.setFilterName("SortBy");
			filterValues = new ArrayList<>();
			if (productGridSelectors != null) {
				if (StringUtils.isNotBlank(productGridSelectors.getSortyByOption())) {
					filterValues.add(productGridSelectors.getSortyByOption());
				} else {
					filterValues.add(sortByOption);
				}
			} else {
				// TBU
				filterValues.add(sortByOption);
			}

			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			/* Adding SortFacet filter */
			filterBean = new FilterBean();
			filterBean.setFilterName("SortFacet");
			filterValues = new ArrayList<>();
			filterValues.add(
					StringUtils.isNotBlank(productGridSelectors.getSortFacet()) ? productGridSelectors.getSortFacet()
							: defaultSortFacet);
			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			// Setting Facets
			filterBean = new FilterBean();
			filterBean.setFilterName(FACET);
			filterValues = new ArrayList<>();
			if (productGridSelectors != null && (productGridSelectors.getFacets() != null)
					&& (!productGridSelectors.getFacets().isEmpty())) {
				List<FacetBean> FacetList = productGridSelectors.getFacets();
				for (int i = 0; i < FacetList.size(); i++) {
					FacetBean facetBean = FacetList.get(i);
					filterValues.add(facetBean.getFacetID());
				}
			}

			if (productGridSelectors != null && productGridSelectors.getExtensionId() != null) {
				filterValues.add(productGridSelectors.getExtensionId());
			}

			if (filterValues.isEmpty()) {
				filterValues.add("");
			}

			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			// Setting startingRecordNumber
			if (countString != null && pageSize != null) {
				int countInt = Integer.parseInt(countString);
				int pageSizeInt = Integer.parseInt(pageSize);
				int startNum = countInt + pageSizeInt;
				String startNumString = Integer.toString(startNum);
				endecaServiceRequestBean.setStartingRecordNumber(startNumString);
			} else {
				endecaServiceRequestBean.setStartingRecordNumber("0");
			}

			filterBean = new FilterBean();
			filterBean.setFilterName("SKUCardParameters");
			filterValues = new ArrayList<>();

			// Setting extension-id from PIM node.
			if (StringUtils.isNotEmpty(inventoryId)) {
				endecaServiceRequestBean.setSearchTerms(inventoryId);
			}

			// Getting SKUCard Attributes
			String[] skuCardAttrArray = null;
			if (skuCardAttr != null) {
				skuCardAttrArray = skuCardAttr.split("\\|");
			}
			if ((skuCardAttrArray != null) && (skuCardAttrArray.length != 0)) {
				for (int i = 0; i < skuCardAttrArray.length; i++) {
					// Break if configured attributes are more than 3
					if (i == 3) {
						break;
					}
					filterValues.add(skuCardAttrArray[i]);
				}
			}

			if (filterValues.isEmpty()) {
				filterValues.add("");
			}

			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			if (productGridSelectors != null && productGridSelectors.getExtensionId() == null && productType != null) {
				// set product-type
				filterBean = new FilterBean();
				filterBean.setFilterName(PRODUCT_TYPE_FACET_NAME);
				filterValues = new ArrayList<>();

				filterValues.add(productType);

				filterBean.setFilterValues(filterValues);
				filters.add(filterBean);
			}

			filterBean = new FilterBean();
			filterBean.setFilterName(RETURNFACETSFOR);
			filterValues = new ArrayList<>();

			if (adminResourceResolver != null) {
				String productGridResourcePath = currentResourcePath;
				Resource productGridResource = adminResourceResolver.getResource(productGridResourcePath);
				ProductGridSlingModel productGridSlingModel;
				Resource taxonomyAttributeResource = null;
				Resource globalAttributeResource = null;

				if (productGridResource != null) {
					productGridSlingModel = productGridResource.adaptTo(ProductGridSlingModel.class);
					if (productGridSlingModel != null) {
						taxonomyAttributeResource = productGridSlingModel.getTaxonomyAttributes();
						globalAttributeResource = productGridSlingModel.getGlobalAttributes();
					}

					if (null != taxonomyAttributeResource) {
						Iterator<Resource> items = taxonomyAttributeResource.listChildren();
						while (items.hasNext()) {
							Resource item = items.next();
							ValueMap properties = item.getValueMap();
							if (!StringUtils.equals(StringUtils.EMPTY,
									CommonUtil.getStringProperty(properties, "taxonomyAttributeValue"))) {
								filterValues.add(CommonUtil.getStringProperty(properties, "taxonomyAttributeValue"));
							}
						}
					}

					if (null != globalAttributeResource) {
						Iterator<Resource> items = globalAttributeResource.listChildren();
						while (items.hasNext()) {
							Resource item = items.next();
							ValueMap properties = item.getValueMap();
							if (!StringUtils.equals(StringUtils.EMPTY,
									CommonUtil.getStringProperty(properties, "globalAttributeValue"))) {
								filterValues.add(CommonUtil.getStringProperty(properties, "globalAttributeValue"));
							}
						}
					}
				}
			}

			if (filterValues.isEmpty()) {
				filterValues.add("");
			}

			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			// Setting Country
			filterBean = new FilterBean();
			filterBean.setFilterName(COUNTRY_CONSTANT);
			filterValues = new ArrayList<>();
			if (StringUtils.isNotEmpty(country)) {
				// TBU
				filterValues.add(country);
			}
			filterBean.setFilterValues(filterValues);
			filters.add(filterBean);

			// Setting Facet List
			endecaServiceRequestBean.setFilters(filters);
		}
	}

	/**
	 * parseURL method.
	 * 
	 * @param pageSize
	 * @param requestUri
	 * @param selectors
	 */
	public void parseURL(String selectors, String requestUri, String pageSize) {

		try {
			String[] selectorsArray = null;
			if (selectors != null) {
				selectorsArray = selectors.split("\\|");
			}
			FacetURLBean facetURLBean;
			String contentPath = requestUri;
			String[] contentPathList = contentPath.split("\\.");
			productGridSelectors = new ProductGridSelectors();
			facetURLBean = new FacetURLBean();
			String basecontentPath = "";

			if ((contentPathList != null) && (contentPathList.length > 0)) {
				basecontentPath = contentPathList[0];
				facetURLBean.setContentPath(basecontentPath);

				if (selectorsArray != null && selectorsArray.length > 0 && selectorsArray[0] != null
						&& selectorsArray[0].startsWith("specifications")) {
					facetURLBean.setContentPath(contentPathList[0] + "." + selectorsArray[0]);
					facetURLBean.setExtensionIdStartURL(contentPathList[0] + "." + selectorsArray[0] + ".productType");
				}
			}

			facetURLBean.setExtensionIdEndURL(HTML_CONSTANT);
			facetURLBean.setFacetStartURL(".facets");
			facetURLBean.setFacetEndURL(HTML_CONSTANT);
			facetURLBean.setSortStartURL(".sort");
			facetURLBean.setSortEndURL(HTML_CONSTANT);
			facetURLBean.setLoadMoreStartURL(LOADMORE_CONSTANT);
			facetURLBean.setLoadMoreEndURL("$11.html");

			if (selectorsArray != null) {
				String facetStartURL = "";
				String sortSelector = "";

				for (int i = 0; i < selectorsArray.length; i++) {
					String selector = selectorsArray[i];

					if (selector.startsWith("tabs")) {
						if (facetURLBean.getContentPath() != null) {
							facetURLBean.setContentPath(basecontentPath + "." + selector);
						}

						String[] searhTerm = selector.split("\\$");
						if ((searhTerm != null) && (searhTerm.length > 1)) {
							productGridSelectors.setSelectedTab(searhTerm[1]);
						}
					}

					if (selector.startsWith("searchTerm")) {
						if (facetURLBean.getContentPath() != null) {
							facetURLBean.setContentPath(facetURLBean.getContentPath() + "." + selector);
						}

						String[] searhTerm = selector.split("\\$");
						if ((searhTerm != null) && (searhTerm.length > 1)) {
							productGridSelectors.setSearchTerm(searhTerm[1]);
						}
					}

					if (selector.startsWith("specifications") && facetURLBean.getContentPath() != null) {
						facetURLBean.setContentPath(basecontentPath + "." + selector);
						facetURLBean.setExtensionIdStartURL(facetURLBean.getContentPath() + ".extensionId");
					}

					if (selector.startsWith(PRODUCT_TYPE)) {
						if (facetURLBean.getContentPath() != null) {
							facetURLBean.setContentPath(facetURLBean.getContentPath() + "." + selector);
						}

						String[] searhTerm = selector.split("\\$");
						if ((searhTerm != null) && (searhTerm.length > 1)) {
							productGridSelectors.setExtensionId(searhTerm[1]);
						}
					}

					if (selector.startsWith("facets")) {
						facetStartURL = selector;
						facetURLBean.setFacetStartURL("." + facetStartURL);

						String sortStartURL = "";
						String loadMoreStartURL = "";
						String extensionIdEndURL = "";
						if ((facetURLBean.getFacetStartURL() != null)
								&& (!("").equals(facetURLBean.getFacetStartURL()))) {
							sortStartURL = facetURLBean.getFacetStartURL() + ".sort";
							loadMoreStartURL = facetURLBean.getFacetStartURL() + LOADMORE_CONSTANT;
							extensionIdEndURL = facetURLBean.getFacetStartURL() + HTML_CONSTANT;
						}
						facetURLBean.setSortStartURL(sortStartURL);
						facetURLBean.setLoadMoreStartURL(loadMoreStartURL);
						facetURLBean.setExtensionIdEndURL(extensionIdEndURL);

						String[] Facets = selectorsArray[i].split("\\$");
						if ((Facets != null) && (Facets.length > 1)) {
							List<FacetBean> FacetList = new ArrayList<>();
							for (int j = 1; j < Facets.length; j++) {
								FacetBean Facet = new FacetBean();
								Facet.setFacetID(Facets[j]);
								String deselectURL = "." + selector.replace("$" + Facets[j], "");
								Facet.setFacetDeselectURL(deselectURL);
								FacetList.add(Facet);
							}
							productGridSelectors.setFacets(FacetList);
						}
					} else if (selector.startsWith("sort")) {

						String loadMoreStartURL = "";
						String extensionIdEndURL = "";
						sortSelector = selector;

						if (StringUtils.isNotEmpty(facetStartURL)) {
							loadMoreStartURL = "." + facetStartURL;
							extensionIdEndURL = "." + facetStartURL;
						}
						loadMoreStartURL = loadMoreStartURL + "." + selector + LOADMORE_CONSTANT;
						extensionIdEndURL = extensionIdEndURL + "." + selector + HTML_CONSTANT;
						facetURLBean.setLoadMoreStartURL(loadMoreStartURL);

						String FacetEndURL = "." + selector + HTML_CONSTANT;
						facetURLBean.setFacetEndURL(FacetEndURL);

						facetURLBean.setExtensionIdEndURL(extensionIdEndURL);

						String[] SortByOptions = selectorsArray[i].split("\\$");
						if ((SortByOptions != null) && (SortByOptions.length > 1)) {
							String[] sortFacetAndOption = StringUtils.contains(SortByOptions[1],
									CommonConstants.SORT_DIRECTION_DELIMITER)
											? StringUtils.splitByWholeSeparatorPreserveAllTokens(SortByOptions[1],
													CommonConstants.SORT_DIRECTION_DELIMITER)
											: new String[] { StringUtils.EMPTY, SortByOptions[1] };
							productGridSelectors.setSortyByOption(sortFacetAndOption[1]);
							productGridSelectors.setSortFacet(sortFacetAndOption[0]);
						}
					} else if (selector.startsWith("loadmore")) {

						String FacetEndURL = "";
						String extensionIdEndURL = "";

						int tempPageSize = 0;

						if (StringUtils.isNotEmpty(facetStartURL)) {
							extensionIdEndURL = "." + facetStartURL;
						}

						if (StringUtils.isNotEmpty(sortSelector)) {
							FacetEndURL = "." + sortSelector;
							extensionIdEndURL = extensionIdEndURL + "." + sortSelector;
						}
						FacetEndURL = FacetEndURL + "." + selector + HTML_CONSTANT;
						extensionIdEndURL = extensionIdEndURL + "." + selector + HTML_CONSTANT;
						facetURLBean.setFacetEndURL(FacetEndURL);

						String sortEndURL = "." + selector + HTML_CONSTANT;
						facetURLBean.setSortEndURL(sortEndURL);

						facetURLBean.setExtensionIdEndURL(extensionIdEndURL);

						String[] LoadMoreOptions = selectorsArray[i].split("\\$");
						if ((LoadMoreOptions != null) && (LoadMoreOptions.length > 1)) {
							int loadMoreCount = Integer.parseInt(LoadMoreOptions[1]);
							productGridSelectors.setLoadMoreOption(loadMoreCount);

							// Setting PageSize from Site Configuration
							tempPageSize = Integer.parseInt(pageSize);
							int loadmoreNewCount = loadMoreCount + tempPageSize;
							String loadMoreEndUrl = "$" + loadmoreNewCount + HTML_CONSTANT;
							facetURLBean.setLoadMoreEndURL(loadMoreEndUrl);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error(String.format("exception occured on parseUrl method of Load More Servlet: %s", e));
		}
	}

	private static FilterBean createFilterValue(final String name, final List<String> values) {
		final FilterBean filterBean = new FilterBean();
		filterBean.setFilterName(name);
		filterBean.setFilterValues(values);
		return filterBean;
	}

	private static FilterBean createFilterValue(final String name, final String value) {
		final List<String> valueList = new ArrayList<>();
		valueList.add(value);
		return createFilterValue(name, valueList);
	}

	/**
	 * @param subCategoryLoadMore
	 * @return
	 * @throws IOException
	 */
	public String convertSubCategoryBeanTOJSON(SubCategoryLoadMore subCategoryLoadMore) throws IOException {
		LOG.info("Entry into convertBeanTOJSON method");
		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest;

		try {

			// Object to JSON in String using Jackson mapper
			jsonRequest = mapper.writeValueAsString(subCategoryLoadMore);

		} catch (JsonGenerationException e) {
			throw new JsonGenerationException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch (JsonMappingException e) {
			throw new JsonMappingException(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("JSON request :%s", jsonRequest));
		}
		LOG.info("Exit from convertBeanTOJSON method");

		return jsonRequest;
	}

	/**
	 * @param skuListLoadMore
	 * @return
	 * @throws IOException
	 */
	public String convertSubCategoryBeanTOJSON(SKUListLoadMore skuListLoadMore) throws IOException {
		LOG.info("Entry into convertBeanTOJSON method");
		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest;

		try {

			// Object to JSON in String using Jackson mapper
			jsonRequest = mapper.writeValueAsString(skuListLoadMore);

		} catch (JsonGenerationException e) {
			throw new JsonGenerationException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch (JsonMappingException e) {
			throw new JsonMappingException(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("JSON request :%s", jsonRequest));
		}
		LOG.info("Exit from convertBeanTOJSON method");

		return jsonRequest;
	}

	   public SubCategoryLoadMore setSubcategoryLoadMoreValues(List<ProductFamilyBean> productFamilyList,String pageType,SlingHttpServletRequest request) {
		SubCategoryLoadMore subCategoryLoadMore = new SubCategoryLoadMore();
		LOG.debug("Inside set sub category");
		List<SubCategoryResults> subCategoryResultsLists = new ArrayList<>();
		for (ProductFamilyBean productFamilyBean : productFamilyList) {

			SubCategoryResults subCategoryResults = new SubCategoryResults();
			SubCategoryContentItem contentItem = new SubCategoryContentItem();

			contentItem.setName(productFamilyBean.getProductName());
			contentItem.setProductGridDescription(productFamilyBean.getProductGridDescription());
			// Attempting to populate for learn page only
			if(CommonConstants.PAGE_TYPE_LEARN_PAGE.equalsIgnoreCase(pageType)) {
				DigitalAttributeOperator digitalAttributeOperator = new DigitalAttributeOperator(request,adminService);
		        digitalAttributeOperator.addNewBadge(productFamilyBean);
				contentItem.setCompanyName(productFamilyBean.getCompanyName());
				contentItem.setNewBadgeVisible(productFamilyBean.getNewBadgeVisible());
				contentItem.setPartnerBadgeVisible(productFamilyBean.getPartnerBadgeVisible());
				contentItem.setProductType(productFamilyBean.getProductType());
				contentItem.setProductGridDescription(productFamilyBean.getProductGridDescription());
			}
			if (CommonConstants.TRUE.equalsIgnoreCase(subCategoryHideShow)) {
				contentItem.setSubcategory(productFamilyBean.getSubCategoryName());
			}

			Image image = new Image();
			image.setMobile(productFamilyBean.getProductImage());
			image.setAltText(productFamilyBean.getProductImage());
			image.setTablet(productFamilyBean.getProductImage());
			image.setDesktop(productFamilyBean.getProductImage());
			contentItem.setImage(image);

			Link link = new Link();
			link.setUrl(productFamilyBean.getUrl());
			contentItem.setLink(link);
			contentItem.setSecure(productFamilyBean.isSecure());

			subCategoryResults.setContentItem(contentItem);
			subCategoryResultsLists.add(subCategoryResults);
		}

		subCategoryLoadMore.setResultsList(subCategoryResultsLists);

		return subCategoryLoadMore;
	}

	/**
	 * @param SKU_PAGE_NAME
	 * @param currentPage
	 * @param fallBackImage
	 * @param resourceResolver
	 * @return
	 */
	public SKUListLoadMore setProductCompatibilityLoadMoreValuesDimmerView(List<ProductCompatibilityModuleBean> list,
			String SKU_PAGE_NAME,
			Page currentPage, String fallBackImage,
			ResourceResolver resourceResolver) {

		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		homePagePath = CommonUtil.dotHtmlLinkSKU(homePagePath, resourceResolver);
		String dummyDesc = "<p style='visibility: hidden;'>dummy data</p>";
		SKUListLoadMore skuListLoadMore = new SKUListLoadMore();
		try {
			List<SKUListResultsList> skuListResultsLists = new ArrayList<>();
			for (ProductCompatibilityModuleBean familyBean : list) {

				SKUListResultsList skuListResults = new SKUListResultsList();
				SKUListContentItem contentItem = new SKUListContentItem();

				contentItem.setName(familyBean.getDimmerCatalogNumber());
				contentItem.setDescription(familyBean.getLongDesc() != null ? familyBean.getLongDesc() : dummyDesc);

				Image image = new Image();
				if (familyBean.getDsktopRendition() != null && !("").equals(familyBean.getDsktopRendition())) {
					image.setMobile(familyBean.getMobileRendition());
					image.setTablet(familyBean.getDsktopRendition());
					image.setDesktop(familyBean.getDsktopRendition());
				} else {
					image.setMobile(fallBackImage);
					image.setTablet(fallBackImage);
					image.setDesktop(fallBackImage);
				}
				contentItem.setImage(image);

				Link link = new Link();
				link.setUrl(homePagePath
						+ SKU_PAGE_NAME.concat("." + CommonUtil.encodeURLString(familyBean.getDimmerCatalogNumber()))
								.concat(CommonConstants.HTML_EXTN));
				contentItem.setLink(link);

				ProductLinks productLinks = new ProductLinks();
				if (familyBean.getDimmerCatalogNumber() != null) {
					productLinks.setResourcesURL(homePagePath + SKU_PAGE_NAME
							.concat("." + CommonUtil.encodeURLString(familyBean.getDimmerCatalogNumber()))
							.concat("." + CommonConstants.RESOURCES_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN));
					productLinks.setSpecificationsURL(homePagePath
							+ SKU_PAGE_NAME
									.concat("." + CommonUtil.encodeURLString(familyBean.getDimmerCatalogNumber()))
									.concat("." + CommonConstants.SPECIFICATIONS_TAB_SELECTOR)
									.concat(CommonConstants.HTML_EXTN));
				}
				contentItem.setProductLinks(productLinks);

				skuListResults.setContentItem(contentItem);

				skuListResultsLists.add(skuListResults);
			}

			skuListLoadMore.setResultsList(skuListResultsLists);
		} catch (Exception e) {
			LOG.error(String.format("Exception occured in setskuListLoadMoreValues(): %s", e));
		}

		return skuListLoadMore;
	}

	/**
	 * Load more LED-related values
	 *
	 * @param list
	 * @return SKUListLoadMore
	 */
	public SKUListLoadMore setProductCompatibilityLoadMoreValuesLEDView(List<ProductCompatibilityModuleBean> list) {

		String dummyDesc = "<p style='visibility: hidden;'>dummy data</p>";
		SKUListLoadMore skuListLoadMore = new SKUListLoadMore();
		try {
			List<SKUListResultsList> skuListResultsLists = new ArrayList<>();
			for (ProductCompatibilityModuleBean familyBean : list) {

				SKUListResultsList skuListResults = new SKUListResultsList();
				SKUListContentItem contentItem = new SKUListContentItem();

				contentItem.setName(familyBean.getDimmerCatalogNumber());
				contentItem.setDescription(familyBean.getLongDesc() != null ? familyBean.getLongDesc() : dummyDesc);
				contentItem.setLampManufacturer(familyBean.getExtLampManufacturer());
				contentItem.setLampModelNumber(familyBean.getExtModelNumber());
				contentItem.setLampManufacturerlabel(this.lampManufacturerLabel);
				contentItem.setLampModelNumberlabel(this.lampModelNumber);

				Image image = new Image();

				//for LED display, use TST_EXT_Image_URL instead of renditions. If no TST_EXT_Image_URL, use fallback image.
				familyBean.setLEDImageURL(familyBean.getExtImageURL());
				image.setMobile(familyBean.getLEDImageURL());
				image.setTablet(familyBean.getLEDImageURL());
				image.setDesktop(familyBean.getLEDImageURL());

				contentItem.setImage(image);

				List<ProductAttribute> productAttributesList = new ArrayList<>();
				if (attributeValueList.contains(COUNTRY_CONSTANT)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(COUNTRY_CONSTANT));
					productAttribute.setProductAttributeValue(familyBean.getCountry());
					productAttributesList.add(productAttribute);
				}
				if (attributeValueList.contains(CommonConstants.TST_EXT_LAMP_MODEL_NUMBER)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_EXT_LAMP_MODEL_NUMBER));
					productAttribute.setProductAttributeValue(familyBean.getExtLampModelNumber());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_EXT_TYPE)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_EXT_TYPE));
					productAttribute.setProductAttributeValue(familyBean.getExtType());
					productAttributesList.add(productAttribute);
				}
				if (attributeValueList.contains(CommonConstants.TST_CONTROL_FAMILY)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_CONTROL_FAMILY));
					productAttribute.setProductAttributeValue(familyBean.getControlFamily());
					productAttributesList.add(productAttribute);
				}
				if (attributeValueList.contains(CommonConstants.TST_FAMILY_ID)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_FAMILY_ID));
					productAttribute.setProductAttributeValue(familyBean.getFamilyID());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_LOWES_ITEMS_NUMBER)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_LOWES_ITEMS_NUMBER));
					productAttribute.setProductAttributeValue(familyBean.getLowesItemNumber());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_EXT_LAMP_MODEL_NUMBER)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_EXT_LAMP_MODEL_NUMBER));
					productAttribute.setProductAttributeValue(familyBean.getLowesModelNumber());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_LOW_END_TRIM)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_LOW_END_TRIM));
					productAttribute.setProductAttributeValue(familyBean.getLowEndTrim());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_MAX_OF_LOADS)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_MAX_OF_LOADS));
					productAttribute.setProductAttributeValue(familyBean.getMaxofloads());
					productAttributesList.add(productAttribute);
				}
				if (attributeValueList.contains(CommonConstants.TST_EXT_LAMP_VOLTAGE)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_EXT_LAMP_VOLTAGE));
					productAttribute.setProductAttributeValue(familyBean.getMaxofloads());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_EXT_COLOR_TEMPERATURE)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_EXT_COLOR_TEMPERATURE));
					productAttribute.setProductAttributeValue(familyBean.getExtColorTemperature());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_LED_COMPATIBLE_SCORE)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_LED_COMPATIBLE_SCORE));
					productAttribute.setProductAttributeValue(familyBean.getLedCompatibleScore());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_EXT_BASE)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_EXT_BASE));
					productAttribute.setProductAttributeValue(familyBean.getExtBase());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_EXT_DATE_CODE)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_EXT_DATE_CODE));
					productAttribute.setProductAttributeValue(familyBean.getExtDateCode());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_EXT_LAMP_LUMEN_OUTPUT)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_EXT_LAMP_LUMEN_OUTPUT));
					productAttribute.setProductAttributeValue(familyBean.getExtLampLumenOutput());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_FLICKER)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_FLICKER));
					productAttribute.setProductAttributeValue(familyBean.getFlicker());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_LED_RATING)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_LED_RATING));
					productAttribute.setProductAttributeValue(familyBean.getLedRating());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_LOW_END)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_LOW_END));
					productAttribute.setProductAttributeValue(familyBean.getLowEnd());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_RELATIVE_LOW_END)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_RELATIVE_LOW_END));
					productAttribute.setProductAttributeValue(familyBean.getRelativeLowEnd());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_PERCEIVED_DIMMING_RANGE)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_PERCEIVED_DIMMING_RANGE));
					productAttribute.setProductAttributeValue(familyBean.getPerceivedDimmingrange());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_PERCEIVED_LOW_END)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_PERCEIVED_LOW_END));
					productAttribute.setProductAttributeValue(familyBean.getPerceivedLowend());
					productAttributesList.add(productAttribute);

				}
				if (attributeValueList.contains(CommonConstants.TST_TEST_NOTES)) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(mapObj.get(CommonConstants.TST_TEST_NOTES));
					productAttribute.setProductAttributeValue(familyBean.getTestNotes());
					productAttributesList.add(productAttribute);
				}

				contentItem.setProductAttributes(productAttributesList);

				skuListResults.setContentItem(contentItem);

				skuListResultsLists.add(skuListResults);
			}

			skuListLoadMore.setResultsList(skuListResultsLists);
		} catch (Exception e) {
			LOG.error(String.format("Exception occured in setskuListLoadMoreValues(): %s", e));
		}

		return skuListLoadMore;
	}

	/**
	 * @param familyModuleList
	 * @param country
	 * @param PRICE_LIST_PATH
	 * @param adminService
	 * @param pimPrimaryImage
	 * @param SKU_PAGE_NAME
	 * @param currentPage
	 * @param fallBackImage
	 * @param jsonfilename
	 * @param rootElementName
	 * @param londDescCheck
	 * @param resourceResolver
	 * @return
	 */
	public SKUListLoadMore setskuListLoadMoreValues(List<FamilyModuleBean> familyModuleList, String country,
			String PRICE_LIST_PATH, AdminService adminService, String SKU_PAGE_NAME,
			Page currentPage, String fallBackImage, String jsonfilename, String rootElementName, String londDescCheck,
			ResourceResolver resourceResolver) {

		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		homePagePath = CommonUtil.dotHtmlLinkSKU(homePagePath, resourceResolver);

		SKUListLoadMore skuListLoadMore = new SKUListLoadMore();
		try {
			List<SKUListResultsList> skuListResultsLists = new ArrayList<>();
			for (FamilyModuleBean familyBean : familyModuleList) {

				SKUListResultsList skuListResults = new SKUListResultsList();
				SKUListContentItem contentItem = new SKUListContentItem();

				contentItem.setName(familyBean.getCatalogNumber());
				contentItem.setModelCode(familyBean.getModelCode());

				if (CommonConstants.TRUE.equalsIgnoreCase(londDescCheck)) {
					contentItem.setDescription(familyBean.getLongDesc());
				} else {
					contentItem.setDescription("");
				}

				Image image = new Image();
				if (familyBean.getDsktopRendition() != null && !("").equals(familyBean.getDsktopRendition())) {
					image.setMobile(familyBean.getMobileRendition());
					image.setTablet(familyBean.getDsktopRendition());
					image.setDesktop(familyBean.getDsktopRendition());
				} else {
					image.setMobile(fallBackImage);
					image.setTablet(fallBackImage);
					image.setDesktop(fallBackImage);
				}
				contentItem.setImage(image);

				Link link = new Link();
				link.setUrl(homePagePath
						+ SKU_PAGE_NAME.concat("." + CommonUtil.encodeURLString(familyBean.getCatalogNumber()))
								.concat(CommonConstants.HTML_EXTN));
				contentItem.setLink(link);

				ProductLinks productLinks = new ProductLinks();
				if (familyBean.getCatalogNumber() != null) {
					productLinks.setResourcesURL(homePagePath + SKU_PAGE_NAME
							.concat("." + CommonUtil.encodeURLString(familyBean.getCatalogNumber()))
							.concat("." + CommonConstants.RESOURCES_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN));
					productLinks.setSpecificationsURL(homePagePath
							+ SKU_PAGE_NAME.concat("." + CommonUtil.encodeURLString(familyBean.getCatalogNumber()))
									.concat("." + CommonConstants.SPECIFICATIONS_TAB_SELECTOR)
									.concat(CommonConstants.HTML_EXTN));
				}
				contentItem.setProductLinks(productLinks);
				List<ProductAttribute> productAttributesList = new ArrayList<>();
				for (SKUCardParameterBean skuCardParameter : familyBean.getSkuCardParameters()) {
					ProductAttribute productAttribute = new ProductAttribute();
					productAttribute.setProductAttributeLabel(skuCardParameter.getLabel());
					productAttribute.setProductAttributeValue(skuCardParameter.getSkuCardValues());
					productAttributesList.add(productAttribute);
				}

				contentItem.setProductAttributes(productAttributesList);

				String price = CommonUtil.priceItem(PRICE_LIST_PATH, adminService, familyBean.getCatalogNumber(),
						country, rootElementName, jsonfilename);
				if (StringUtils.isNotEmpty(price)) {
					contentItem.setProductPrice(price);
				} else {
					contentItem.setProductPrice("");
				}
				skuListResults.setContentItem(contentItem);
				skuListResultsLists.add(skuListResults);
			}

			skuListLoadMore.setResultsList(skuListResultsLists);
		} catch (Exception e) {
			LOG.error(String.format("Exception occured in setskuListLoadMoreValues(): %s", e));
		}

		return skuListLoadMore;
	}

	/**
	 * Populate populateAttributeDataFromDialog.
	 * This methods reads all multifield values authored for attribute and builds
	 * alist
	 * This
	 * 
	 * @param currentResourcePath currentResourcePath
	 * @return List
	 */
	public List<Attribute> populateAttributeDataFromDialog(String currentResourcePath) {
		if (currentResourcePath != null) {
			attributeLists = new ArrayList<>();
			attributeValueList = new ArrayList<>();

			Resource currentResource = adminService.getReadService().getResource(currentResourcePath);
			if (currentResource != null) {
				Resource attribResource = currentResource.getChild(CommonConstants.ATTRIBUTELIST);
				Iterator<Resource> childResources = attribResource.listChildren();
				while (childResources.hasNext()) {
					Resource childResource = childResources.next();
					if (childResource != null) {

						Attribute attribute = new Attribute();
						ValueMap properties = childResource.getValueMap();
						if (properties.get(ATTRIBUTELABEL, String.class) != null) {
							attribute.setAttributeLabel(properties.get(ATTRIBUTELABEL, String.class));
						}
						if (properties.get(ATTRIBUTEVALUE, String.class) != null) {

							attribute.setAttributeVaue(properties.get(ATTRIBUTEVALUE, String.class));
							attributeValueList.add(properties.get(ATTRIBUTEVALUE, String.class));
							
						}
						attributeLists.add(attribute);
						mapObj.put(properties.get(ATTRIBUTEVALUE, String.class),
								properties.get(ATTRIBUTELABEL, String.class));

					}
				}
			}

		}
		return this.attributeLists;
	}

	
	public String getLampManufacturerLabel() {
		return lampManufacturerLabel;
	}

	

	public String getLampModelNumber() {
		return lampModelNumber;
	}

	

}
