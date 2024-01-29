package com.eaton.platform.core.models;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.*;
import com.eaton.platform.core.bean.sitesearch.SecondaryLink;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.helpers.FacetedNavigationHelperV2;
import com.eaton.platform.core.models.producttab.ProductTabsVersionChecker;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.FacetURLBeanService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.FilterUtil;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.filters.FacetGroupBeanFilter;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResultsBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.eaton.platform.core.constants.CommonConstants.HTML_EXTN;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 * @updated as sling model July 2020
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchResultsRenderModel {
	public static final String FACET_SITE_SEARCH_FACET_GROUPS = "siteSearchFacetGroups";
	public static final String TAB_2_HASH = "#tab-2";
	public static final String TAB_3_HASH = "#tab-3";
	/** The facets key searchInsteadFor. */
	private static String FACETS_KEY_SEARCHINSTEADFOR = "searchInsteadFor";
	private static String FACETS_KEY_SEARCHINSTEADFOR_DEFAULT = "Search Instead For";
	/** The facets key showingResultsFor. */
	private static String FACETS_KEY_SHOWINGRESULTSFOR = "showingResultsFor";
	private static String FACETS_KEY_SHOWINGRESULTSFOR_DEFAULT = "Showing Results For";
	/** The facets key results. */
	private static String FACETS_KEY_RESULTS = "results";
	private static String FACETS_KEY_RESULTS_DEFAULT = "Results";
	/** The facets key sort. */
	private static String FACETS_KEY_SORT = "sort";
	private static String FACETS_KEY_SORT_DEFAULT = "Sort";
	/** The facets key relevence. */
	private static String FACETS_KEY_RELEVENCE = "relevance";
    private static String FACETS_KEY_RELEVENCE_DEFAULT = "Relevance";
	/** The facets key ascending sort. */
	private static String FACETS_KEY_ASCENDING_SORT= "ascendingSort";
	private static String FACETS_KEY_ASCENDING_SORT_DEFAULT= "A-Z";
	/** The facets key descending sort. */
	private static String FACETS_KEY_DESCENDING_SORT = "descendingSort";
	private static String FACETS_KEY_DESCENDING_SORT_DEFAULT="Z-A";
	/** The facets key ascending sort. */
	private static String FACETS_KEY_DATE_ASCENDING_SORT= "dateAscendingSort";
	/** The facets key ascending sort. */
	private static String FACETS_KEY_DATE_DESCENDING_SORT= "dateDescendingSort";
	/** The facets key clear filters. */
	private static String FACETS_KEY_CLEAR_FILTERS = "clearFilters";
	private static String FACETS_KEY_CLEAR_FILTERS_DEFAULT = "Clear Filters";
	/** The facets key narrow results. */
	private static String FACETS_KEY_NARROW_RESULTS = "narrowResults";
	private static String FACETS_KEY_NARROW_RESULTS_DEFAULT = "Narrow Results";
	/** The facets key view more. */
	private static String FACETS_KEY_VIEW_MORE = "viewMore";
	private static String FACETS_KEY_VIEW_MORE_DEFAULT = "View More";
	private static String FACETS_KEY_VIEW_LESS_DEFAULT = "View Less";
	/** The facets key new. */
	private static String FACETS_KEY_NEW = "newKeyword";
	private static String FACETS_KEY_NEW_DEFAULT = "New";
	/** The facets key bestseller. */
	private static String FACETS_KEY_BESTSELLER = "bestSeller";
	private static String FACETS_KEY_BESTSELLER_DEFAULT = "Best Seller";
	/** The card key go to. */
	private static String CARD_KEY_GO_TO = "goTo";
	private static String CARD_KEY_GO_TO_DEFAULT = "Go To";
	/** The products key go to. */
	private static String PRODUCTS_KEY = "products";
	private static String PRODUCT_KEY = "product";
	private static String PRODUCTS_KEY_DEFAULT = "Products";
	/** The products key go to. */
	private static String CONTENT_KEY = "contentType";
	private static String CONTENT_KEY_DEFAULT = "Content Type";
	private static String CONTENT_TYPE_KEYID = "content-type";
	/** The resource key go to. */
	private static String RESOURCE_KEY = "resources";
	private static String RESOURCE_KEY_DEFAULT = "Resources";
	private static String RESOURCE_KEY_URL_SELECTOR=".resources.html";
	/** The resource key go to. */
	private static String SPECIFICATION_KEY_DEFAULT = "Specifications";
	private static String SPECIFICATION_KEY_URL_SELECTOR=".specifications.html";

	private static String SPECIFICATION_HASH_URL=".html#Specifications";
	private static String RESOURCE_HASH_URL=".html#Resources";
	/** The resource key go to. */
	private static String SERVICE_KEY = "services";
	private static String SERVICE_KEY_DEFAULT = "Services";
	/** The newsAndSights key go to. */
	private static String NEWS_AND_SIGHTS_KEY = "newsAndSights";
	private static String NEWS_AND_SIGHTS_KEY_DEFAULT = "News & insights";
	/** The facets key remove filters. */
	private static String FACETS_KEY_REMOVE_FILTERS = "removeFilters";
	private static String FACETS_KEY_REMOVE_FILTERS_DEFAULT = "Remove Filters";
	/** The facets key apply. */
	private static String FACETS_KEY_APPLY ="apply";
	private static String FACETS_KEY_APPLY_DEFAULT ="Apply";
	/** The facets key filters. */
	private static String FACETS_KEY_FILTERS ="filters";
	private static String FACETS_KEY_FILTERS_DEFAULT ="Filters";
	private static final String MODELS = "Models";
	private static final String CONTENT_TYPE_FAMILY = "family";
	/** .sampleSelector String. */
	private static final String SAMPLE_SELECTOR= ".sampleSelector";
	public static final String FACET_PROD_STATUS_INPUT_TYPE= "radios";
	private static final String FACETS_KEY_ASCENDING_SORT_DEFAULT_LABEL="A-Z";
	private static final String FACETS_KEY_ASCENDING_SORT_DEFAULT_VALUE="asc";
	private static final String FACETS_KEY_DESCENDING_SORT_DEFAULT_LABEL="Z-A";
	private static final String FACETS_KEY_DESCENDING_SORT_DEFAULT_VALUE="desc";
	private static final String RESOURCE_ENABLED_ON = "on";

	private static final String LEARN_PAGE_TYPE="learn-page";

	final String FORM_FIELD_CHECKED = "checked";

	@Inject
	private AuthorizationService authorizationService;

	@Inject
	private AuthenticationServiceConfiguration authenticationServiceConfig;

	@Inject
	private AuthenticationService authenticationService;

	@Inject
	private ProductFamilyDetailService productFamilyDetailService;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SearchResultsRenderModel.class);
	/** The page type. */
	private String pageType;
	/** The faceted header navigation result bean. */
	private FacetedHeaderNavigationResultBean facetedHeaderNavigationResultBean;
	/** The site search results bean. */
	private List<SiteSearchResultsBean> siteSearchResultsBeanList;
	/** The product grid selectors. */
	private ProductGridSelectors productGridSelectors;
	/** The site search response. */
	private SiteSearchResponse siteSearchResponse;
	/** The country. */
	private String country;
	/** The site resource sling model. */
	private SiteResourceSlingModel siteResourceSlingModel;
	/** The tab list. */
	private List<Tabs> tabList;
	/** The ajax url. */
	private String ajaxUrl;
	/** The auto correct. */
	private AutoCorrect autoCorrect;
	/** The total results. */
	private int totalResults;
	/** The all results URL. */
	private String allResultsURL;
	/** The load more attr json string. */
	private String loadMoreAttrJsonString;
	/** The search term. */
	private String searchTerm;
	/** Load More Flag. */
	private String loadMoreFlag;
	/** The Mobile SK U image. */
	private String Mobile_SKU_Image = "/content/dam/eaton/resources/default-sku-image-220.jpg";
	/** The zero results. */
	private String zeroResults ;
	private String viewType = CommonConstants.SEARCH_DEFAULT_VIEW;
	/**Facet value count from site config*/
	private int facetValueCount;
    private String hideGlobalFacetSearch;
    @Inject
    private List<FacetGroupBean> facetGroupBeanList;
	@Inject
    protected AdminService adminService;
	@Inject
	protected Page currentPage;
	@Inject @Source("sling-object")
	private SlingHttpServletRequest slingRequest;
	@Inject
	protected EatonSiteConfigService eatonSiteConfigService;
	@Inject
	protected Externalizer externalizer;
	@Inject
	protected FacetURLBeanService facetURLBeanService;
	@Inject
	protected EndecaRequestService endecaRequestService;
	@Inject
	protected EndecaService endecaService;
	@Inject
	protected TagManager tagManager;
	private SearchResultsModel searchResultsModel;
	private FacetURLBean facetURLBean;
	private List<FacetGroupBean> facetGroupList;
	protected List<FacetBean> activeFacetsList;
	protected String currentSelectedTab;
	private FacetedNavigationHelperV2 facetedNavigationHelperV2 = new FacetedNavigationHelperV2();
	/** searchInsteadfor key */
	private String searchInsteadFor;
	/** showingResultsFor key */
	private String showingResultsFor;
	private String defaultSortingOption;

	private AuthenticationToken authenticationToken;
	private boolean isAuthenticated;

	@PostConstruct
    private void init(){
		LOG.debug("SearchResultsRenderModel :: init() start");
		try(ResourceResolver adminResourceResolver = adminService.getReadService()) {
			tagManager = adminResourceResolver.adaptTo(TagManager.class);
			searchResultsModel = slingRequest.getResource().adaptTo(SearchResultsModel.class);
			authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest); // EAT-4643
			defaultSortingOption = StringUtils.EMPTY;
			if(searchResultsModel!=null){
		  	viewType = searchResultsModel.getView();
		  	hideGlobalFacetSearch = searchResultsModel.getHideGlobalFacetSearch();
			}
			if (currentPage!=null && currentPage.getProperties().get(CommonConstants.PAGE_TYPE) != null) {
				pageType = currentPage.getProperties().get(CommonConstants.PAGE_TYPE).toString();
			}
			LOG.debug("Page Type :", pageType);
			if (null != pageType && (StringUtils.equals(CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE, pageType))) {
				country = CommonUtil.getCountryFromPagePath(currentPage);
				// ICF re factored service invocation. Update code to utilize same. Reference class provided ProductGridHelper.java
				Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);
				if (siteConfig.isPresent()) {
					siteResourceSlingModel = siteConfig.get();
				} else {
					siteResourceSlingModel = null;
					LOG.error("SearchResultsRenderModel Site config was not authored : siteResourceSlingModel is null");
				}
				if (null != slingRequest && null != facetURLBeanService
						&& siteResourceSlingModel != null) {
					String contentResourcePath = CommonUtil.getMappedUrl(
							currentPage.getPath(), externalizer, slingRequest);
					FacetURLBeanServiceResponse facetURLBeanResponse = facetURLBeanService
							.getFacetURLBeanResponse(slingRequest
									.getRequestPathInfo().getSelectors(),
									siteResourceSlingModel.getPageSize(),
									pageType, contentResourcePath);
					if (null != facetURLBeanResponse) {
						facetURLBean = facetURLBeanResponse.getFacetURLBean();
						productGridSelectors = facetURLBeanResponse
								.getProductGridSelectors();
					}
				}
				boolean isUnitedStatesDateFormat = false;
				if(null != siteResourceSlingModel) {
					isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.getUnitedStatesDateFormat());
				}
				final String inventoryId = endecaRequestService.getInventoryId(currentPage, adminResourceResolver);
				final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
						.getEndecaRequestBean(currentPage, slingRequest.getRequestPathInfo().getSelectors(), inventoryId, productGridSelectors, slingRequest);
				siteSearchResponse = endecaService.getSearchResults(endecaServiceRequestBean, slingRequest, isUnitedStatesDateFormat);
				facetedHeaderNavigationResultBean = new FacetedHeaderNavigationResultBean();
				// set the i18N values
				setI18NValues();
				searchInsteadFor= CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_SEARCHINSTEADFOR,FACETS_KEY_SEARCHINSTEADFOR_DEFAULT);
				showingResultsFor=CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_SHOWINGRESULTSFOR,FACETS_KEY_SHOWINGRESULTSFOR_DEFAULT);
				zeroResults = CommonConstants.FALSE;
				if ((productGridSelectors != null) && (productGridSelectors.getSearchTerm() != null)) {
					searchTerm = productGridSelectors.getSearchTerm();
				}
				if ((null != siteSearchResponse) && (siteSearchResponse.getPageResponse() != null)) {
					if (siteSearchResponse.getPageResponse().getTotalCount() != null && siteSearchResponse.getPageResponse().getTotalCount()>0 ) {
						facetedHeaderNavigationResultBean.setTotalCount(siteSearchResponse.getPageResponse().getTotalCount());
					} else {
						facetedHeaderNavigationResultBean.setTotalCount(0);
					}
					if ((siteSearchResponse.getPageResponse().getTotalCount() != null) && (siteSearchResponse.getPageResponse().getTotalCount() != 0)) {
						if ((siteSearchResponse.getPageResponse().getFacets() != null) && (siteSearchResponse.getPageResponse().getFacets().getFacetGroupList() != null)) {
							facetGroupList = siteSearchResponse.getPageResponse().getFacets().getFacetGroupList();
						}
						if (siteSearchResponse.getPageResponse().getSiteSearchResults() != null) {
							siteSearchResultsBeanList = siteSearchResponse.getPageResponse().getSiteSearchResults();
						}
						if ((productGridSelectors != null) && (productGridSelectors.getSearchTerm() != null)) {
							searchTerm = productGridSelectors.getSearchTerm();
						}
						// EAT-4643 Secure Search
						authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
						if(authenticationToken != null && authenticationToken.getUserProfile() != null) {
							isAuthenticated = true;

						}
						if (StringUtils.equals(CommonConstants.SEARCH_DEFAULT_VIEW, viewType)) {
							populateAutoCorrectBean();
							setContentTypeFacet();
						} else if (StringUtils.equals(CommonConstants.SEARCH_ADVANCED_VIEW, viewType)) {
							List<SortByOptionValueBean> sortList = setAdvancedViewSortByOptionList();
							facetedHeaderNavigationResultBean.setSortList(sortList);
							for (SortByOptionValueBean sortByOptionValueBean : sortList) {
								if(sortByOptionValueBean.getValue().equalsIgnoreCase(searchResultsModel.getDefaultSort())) {
									defaultSortingOption = sortByOptionValueBean.getLabel();
									break;
								}
							}
							List<FacetGroupBean> updatedFacetGroupList = new ArrayList<>();
							extractFacetGroups(updatedFacetGroupList);
						}
						populateActiveFacetsBeanList();
						populateContentType();
						facetGroupList = filterOutTheFacetGroupsWithOnlyOneValueWhenNotOneOfTheActiveOnes();

						if (null != facetURLBean) {
							String currentUrl = slingRequest.getRequestURI();
							ajaxUrl = currentUrl + SAMPLE_SELECTOR;
						}
						final List<FacetGroupBean> statusfacetGroupList = populateStatusFacetOption();
						if (facetGroupListNotNullAndNotEmpty(statusfacetGroupList)) {
							final String statusFilter = statusfacetGroupList.get(0).getFacetValueList().get(0).getFacetValueId();
							endecaServiceRequestBean.getFilters().add(FilterUtil.getFilterBean(EndecaConstants.FILTER_FACETS, statusFilter));
							siteSearchResponse = endecaService.getSearchResults(endecaServiceRequestBean, slingRequest, isUnitedStatesDateFormat);
						}
						if (facetedHeaderNavigationResultBean.getTotalCount() > siteResourceSlingModel.getPageSize()) {
							loadMoreFlag = CommonConstants.TRUE;
						} else {
							loadMoreFlag = CommonConstants.FALSE;
						}
						populateLoadMoreJson();
					} else {
						zeroResults = CommonConstants.TRUE;
					}
				} else {
					zeroResults = CommonConstants.TRUE;
				}
			} else {
				LOG.error("Page Type is null");
			}
		} catch (Exception e){
			LOG.debug(String.format("SearchResultsRenderModel exception %s", e.getMessage()));
		}
		LOG.debug("SearchResultsRenderModel :: init() complete");
	}

	private List<FacetGroupBean> filterOutTheFacetGroupsWithOnlyOneValueWhenNotOneOfTheActiveOnes() {
		if (facetGroupList == null) {
			return null;
		}
		return facetGroupList.stream().filter(new FacetGroupBeanFilter(siteSearchResponse.getPageResponse().getTotalCount(), activeFacetsList)).collect(Collectors.toList());
	}

	public List<FacetGroupBean> getFacetGroupBeanList() {
		return facetGroupBeanList;
	}

	public void setFacetGroupBeanList(List<FacetGroupBean> facetGroupBeanList) {
		this.facetGroupBeanList = facetGroupBeanList;
	}

	private static boolean facetGroupListNotNullAndNotEmpty(final List<FacetGroupBean> statusfacetGroupList) {
		return (null != statusfacetGroupList && !(statusfacetGroupList.isEmpty()))&& statusfacetGroupList.get(0).getFacetValueList()!=null && !statusfacetGroupList.get(0).getFacetValueList().isEmpty();
	}

	/**
	 * This method is to populate status Facet option
	 * populateStatusFacetOption
	 * @return List<FacetGroupBean>
	 */
	private  List<FacetGroupBean> populateStatusFacetOption() {
		int cnt =0;
		boolean showStatusFacet = true;
		List<FacetGroupBean> statusFacetList = new ArrayList<>();
		if(siteSearchResponse!=null && siteSearchResponse.getPageResponse()!=null && siteSearchResponse.getPageResponse().getFacets()!=null){
			final List<FacetGroupBean> facetList = siteSearchResponse.getPageResponse().getFacets().getFacetGroupList();
			if (facetList != null) {
				for (int facetListIndex = 0; facetListIndex < facetList.size(); facetListIndex++){
					FacetGroupBean facetGroupBean = facetList.get(facetListIndex);
					if(facetGroupBean.getFacetGroupId().equals(EndecaConstants.SEARCH_PRODUCT_STATUS)){
						final List<FacetValueBean> facetvaluebean = facetGroupBean.getFacetValueList();
						for(int facetValueBeanIndex=0;facetValueBeanIndex<facetvaluebean.size();facetValueBeanIndex++){
							if(facetValueLabelNotNull(facetvaluebean, facetValueBeanIndex) && facetvaluebean.get(facetValueBeanIndex).getFacetValueLabel().equals(CommonConstants.STATUS_ACTIVE)){
								cnt++;
							}else if(facetValueLabelNotNull(facetvaluebean, facetValueBeanIndex)&& facetvaluebean.get(facetValueBeanIndex).getFacetValueLabel().equals(CommonConstants.STATUS_DISCONTINUED)){
								cnt++;
							}
						}
						if(cnt==2){
							facetGroupBean.setInputType(FACET_PROD_STATUS_INPUT_TYPE);
							facetGroupBean.setSingleFacetEnabled(showStatusFacet);
							facetGroupBean.setFacetGroupLabel(CommonConstants.STATUS_LABEL);
							statusFacetList.add(0, facetGroupBean);
						}
					}
				}
			} else {
				LOG.debug("Facet list null ProductGridHelper.populateStatusFacetOption.");
			}
		} else {
			LOG.debug("siteSearchResponse OR siteSearchResponse.getPageResponse OR siteSearchResponse.getPageResponse.getFacet is Null");
		}
		return statusFacetList;
	}

	private static boolean facetValueLabelNotNull(final List<FacetValueBean> facetvaluebean, int facetValueBeanIndex) {
		return facetvaluebean.get(facetValueBeanIndex)!=null &&facetvaluebean.get(facetValueBeanIndex).getFacetValueLabel()!=null;
	}

	private void extractFacetGroups(List<FacetGroupBean> updatedFacetGroupList) {
		if(facetGroupList!=null){
			for(int facetGrp = 0 ; facetGrp < facetGroupList.size(); facetGrp++){
				FacetGroupBean facetGroupBean = facetGroupList.get(facetGrp);
				String facetGroupID = facetGroupBean.getFacetGroupId();
				if(!facetGroupID.equals(EndecaConstants.CONTENT_TYPE_STRING)){
					updatedFacetGroupList.add(facetGroupBean);
				}
			}
			facetGroupList = updateSearchFacetGroupsBySiteConfigs(updatedFacetGroupList);
			updateFacetValueLabels(facetGroupList);
			LOG.info("Updated facetGroupList "+facetGroupList.toString());
		}
	}

	/**
	 * Updates the facet groups with labels updated based upon tag titles if a corresponding tag was found. Otherwise
	 * the provided label remains the same. Both scenarios are expected. We attempt to convert the label if possible.
	 * @param facetGroupBeans The facet group beans for which you want to update the facet values labels.
	 */
	private void updateFacetValueLabels(List<FacetGroupBean> facetGroupBeans) {
		facetGroupBeans.stream()
				.filter(facetGroupBean -> facetGroupBean.getFacetGroupId() != null)
				.forEach(facetGroupBean ->
						facetGroupBean.getFacetValueList().stream()
								.filter(facetValueBean -> facetValueBean.getFacetValueLabel() != null)
								.forEach(facetValueBean ->
										updateFacetValueLabel(facetGroupBean.getFacetGroupId(), facetValueBean)));
	}

	/**
	 * Updates the facet value label based upon the facet group tag id and the current label of the facet value bean. If
	 * these two combine to provide an Endeca facet tag id then this is used to find the corresponding tag and the localized
	 * title of that tag is used. Otherwise the facet label is used as is. Both scenarios are valid.
	 * @param facetGroupTagId The facet group to search within in the format defined by EndecaFacetTag.getFacetId
	 * @param facetValueBean The facet value bean to update.
	 */
	private void updateFacetValueLabel(String facetGroupTagId, FacetValueBean facetValueBean) {
		Locale locale = currentPage.getLanguage(false);
		final String tagId = facetGroupTagId.equalsIgnoreCase(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL) ? null :
				EndecaFacetTag.convertId(facetGroupTagId + EndecaFacetTag.ID_SEPARATOR + facetValueBean.getFacetValueLabel());
		if (tagId != null && tagManager != null && locale != null) {
			Tag facetValueTag = tagManager.resolve(tagId);
			if (facetValueTag != null) {
				facetValueBean.setFacetValueLabel(facetValueTag.getTitle(locale));
			}
		}else if(facetGroupTagId.equalsIgnoreCase(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL)){
			facetValueBean.setFacetValueLabel(CommonUtil.getI18NFromLocale(slingRequest, EndecaConstants.SECURE_ONLY_FILTER_VALUE, locale));
		}
	}

	private void populateLoadMoreJson(){
		LOG.debug("Entry into populateLoadMoreJson method");
		final String DEFAULT_SORT_ORDER = "defaultSortOrder";
		final String CURRENT_LANGUAGE = "currentLanguage";
		final String CURRENT_COUNTRY = "currentCountry";
		final String CURRENT_LOAD_MORE = "currentLoadMore";
		final String CURRENT_SORT_BY_OPTION = "currentSortByOption";
		final String CURRENT_RESOURCE_PATH = "currentResourcePath";
		final String PRIMARY_SUB_CATEGORY_TAG = "primarySubCategoryTag";
		final String PAGE_SIZE = "pageSize";
		final String UNITED_STATES_DATE_FORMAT = "unitedStatesDateFormat";
		final String URL_SELECTORS = "selectors";
		final String REQUEST_URI = "requestUri";
		final String SKU_PAGE_URL = "skuPageUrl";
		final String SKU_FALL_BACK_IMAGE = "skuFallBackImage";
		final String RETURN_FACETS_FOR = "returnFacetsFor";
		final String DATA_ATTRIBUTE = "dataAttribute";
		final String FILTER_PAGE_TYPE = "pageType";
		try(ResourceResolver adminResourceResolver = adminService.getReadService()){
			JSONObject loadMoreAttrJson = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONObject languageJson = new JSONObject();
			JSONObject countryJson = new JSONObject();
			JSONObject pageTypeJson = new JSONObject();
			JSONObject primarySubCategoryTagJson = new JSONObject();
			JSONObject pageSizeJson = new JSONObject();
			JSONObject unitedStatesDateFormatJson = new JSONObject();
			JSONObject defaultSortOrderJson = new JSONObject();
			JSONObject activeFacetsJson = new JSONObject();
			JSONObject currentLoadMoreJson = new JSONObject();
			JSONObject currentSortByJson = new JSONObject();
			JSONObject currentResourcePathJson = new JSONObject();
			JSONObject pimPrimaryImageJson = new JSONObject();
			JSONObject extensionIdJson = new JSONObject();
			JSONObject selectorsJson = new JSONObject();
			JSONObject requestUriJson = new JSONObject();
			JSONObject skuPageUrlJson = new JSONObject();
			JSONObject siteResourceSlingModelJson = new JSONObject();
			JSONObject returnFacetsForJson = new JSONObject();
			zeroResults = "false";
			Locale languageValue =  CommonUtil.getLocaleFromPagePath(currentPage);
			if (languageValue != null && ((languageValue.getLanguage() != null) && (!languageValue.getLanguage().equals(StringUtils.EMPTY)))) {
				//endecaServiceRequestBean.setLanguage(languageValue.getLanguage());CommentedCode
				String lang = languageValue.getLanguage();
				if ((languageValue.getCountry() != null) && (!languageValue.getCountry().equals(StringUtils.EMPTY))) {
					country = languageValue.getCountry();
					languageJson.put(CURRENT_LANGUAGE, lang+"_"+country);
					Page countryPage = currentPage.getAbsoluteParent(2);
					String countryString = countryPage.getName();
					if((!country.equalsIgnoreCase(countryString)) && (!countryString.equals(CommonConstants.LANGUAGE_MASTERS_NODE_NAME))){
						country = countryString.toUpperCase();
					}
					countryJson.put(CURRENT_COUNTRY,country);
				}
			}
			pageTypeJson.put(FILTER_PAGE_TYPE, pageType);
			String primarySubcategoryTag = currentPage.getProperties().get(CommonConstants.PRIMARY_SUB_CATEGORY_TAG, String.class);
			primarySubCategoryTagJson.put(PRIMARY_SUB_CATEGORY_TAG, primarySubcategoryTag);
			pageSizeJson.put(PAGE_SIZE, siteResourceSlingModel.getPageSize());
			unitedStatesDateFormatJson.put(UNITED_STATES_DATE_FORMAT, siteResourceSlingModel.getUnitedStatesDateFormat());
			if ((pageType != null) && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE)) {
				defaultSortOrderJson.put(DEFAULT_SORT_ORDER, siteResourceSlingModel.getDefaultCategoryWithCardsSortOrder());
			} else if ((pageType != null) && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
				defaultSortOrderJson.put(DEFAULT_SORT_ORDER, siteResourceSlingModel.getDefaultProductFamilySortOrder());
			} else if ((pageType != null) && (StringUtils.equals(CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE, pageType))) {
				String sortByOption = StringUtils.EMPTY ;
				if ((null != searchResultsModel) && (StringUtils.equals(CommonConstants.SEARCH_ADVANCED_VIEW, searchResultsModel.getView()))) {
					sortByOption = searchResultsModel.getDefaultSort();
				}
				if ((siteResourceSlingModel.getDefaultSiteSearchSortOrder() != null) && (!siteResourceSlingModel.getDefaultSiteSearchSortOrder().equals(StringUtils.EMPTY))
					&& (null != searchResultsModel) && (StringUtils.equals(CommonConstants.SEARCH_DEFAULT_VIEW, searchResultsModel.getView()))) {
					sortByOption = siteResourceSlingModel.getDefaultSiteSearchSortOrder().trim();
				}
				defaultSortOrderJson.put(DEFAULT_SORT_ORDER, sortByOption);
			}
			currentLoadMoreJson.put(CURRENT_LOAD_MORE, productGridSelectors.getLoadMoreOption());
			currentSortByJson.put(CURRENT_SORT_BY_OPTION, productGridSelectors.getSortyByOption());
			currentResourcePathJson.put(CURRENT_RESOURCE_PATH, slingRequest.getResource().getPath());
			String[] tempSelectorsArray = null;
			String tempSelectors = StringUtils.EMPTY;
			if(slingRequest.getRequestPathInfo().getSelectors() != null){
				tempSelectorsArray = slingRequest.getRequestPathInfo().getSelectors();
			}
			if(tempSelectorsArray != null && tempSelectorsArray.length > 0){
				for(int k=0;k<tempSelectorsArray.length;k++){
					if(k == tempSelectorsArray.length -1){
						tempSelectors = tempSelectors.concat(tempSelectorsArray[k]);
					} else {
						tempSelectors = tempSelectors.concat(tempSelectorsArray[k]+"|");
					}
				}
			}
			selectorsJson.put(URL_SELECTORS,tempSelectors );
			requestUriJson.put(REQUEST_URI, slingRequest.getRequestURI());
			String skuPageUrl = StringUtils.EMPTY ;
			if(siteResourceSlingModel.getSkuPageURL()!=null){
				skuPageUrl = siteResourceSlingModel.getSkuPageURL();
				skuPageUrlJson.put(SKU_PAGE_URL, skuPageUrl);
			}
			String skuFallBackImage = StringUtils.EMPTY ;
			if ((siteResourceSlingModel.getSkuFallBackImage()!=null) && (!siteResourceSlingModel.getSkuFallBackImage().equals(StringUtils.EMPTY))) {
				skuFallBackImage = siteResourceSlingModel.getSkuFallBackImage();
				siteResourceSlingModelJson.put(SKU_FALL_BACK_IMAGE, skuFallBackImage);
			}
			List<String> facetGroups = new ArrayList<String>();
			List<String> filterValues = new ArrayList<>();
			if (StringUtils.equals(CommonConstants.SEARCH_ADVANCED_VIEW,searchResultsModel.getView())) {
				// if the view in Search Results is advanced, get the facet tags from Facet Settings tab dialog
				getAuthoredFacetTags(adminResourceResolver, filterValues);
				facetGroups = filterValues;
			}
			if((siteResourceSlingModel.getSiteSearchFacetGroups()!=null) && (!(siteResourceSlingModel.getSiteSearchFacetGroups().isEmpty())) && (StringUtils.equals(CommonConstants.SEARCH_DEFAULT_VIEW, searchResultsModel.getView())) ){
				facetGroups = siteResourceSlingModel.getSiteSearchFacetGroups();
			}
			if(!facetGroups.isEmpty()){
				String returnFacetsValue = StringUtils.EMPTY;
				for(int i=0;i<facetGroups.size();i++){
					if(i == facetGroups.size() -1){
						returnFacetsValue = returnFacetsValue.concat(facetGroups.get(i));
					}else{
						returnFacetsValue = returnFacetsValue.concat(facetGroups.get(i).concat("{[()]}"));
					}
				}
				returnFacetsForJson.put(RETURN_FACETS_FOR, returnFacetsValue);
			} else {
				returnFacetsForJson.put(RETURN_FACETS_FOR, StringUtils.EMPTY);
			}
			jsonArray.add(languageJson);
			jsonArray.add(countryJson);
			jsonArray.add(pageTypeJson);
			jsonArray.add(primarySubCategoryTagJson);
			jsonArray.add(pageSizeJson);
			jsonArray.add(unitedStatesDateFormatJson);
			jsonArray.add(defaultSortOrderJson);
			jsonArray.add(activeFacetsJson);
			jsonArray.add(currentLoadMoreJson);
			jsonArray.add(currentSortByJson);
			jsonArray.add(currentResourcePathJson);
			jsonArray.add(pimPrimaryImageJson);
			jsonArray.add(extensionIdJson);
			jsonArray.add(selectorsJson);
			jsonArray.add(requestUriJson);
			jsonArray.add(skuPageUrlJson);
			jsonArray.add(siteResourceSlingModelJson);
			jsonArray.add(returnFacetsForJson);
			loadMoreAttrJson.put(DATA_ATTRIBUTE, jsonArray);
			loadMoreAttrJsonString = loadMoreAttrJson.toJSONString();
			LOG.debug("Load More JSON"+ loadMoreAttrJsonString);
		}catch(Exception e){
			LOG.error(String.format("Exception in creating parameter for Load More Servlet %s", e.getMessage()));
		}
		LOG.debug("Exit from populateLoadMoreJson method");
	}

	/**
	 * Populate content type.
	 */
	private void populateContentType() throws MalformedURLException {
		LOG.debug("Entry into populateContentType method");
		final String CONTENT_TYPE_OTHERS = "others";
		final String CONTENT_TYPE_RESOURCES = "resources";
		final String CONTENT_TYPE_SKU = "sku";
		final String CONTENT_TYPE_ARTICLE= "article";
		final String CONTENT_TYPE_NEWS_AND_INSIGHT= "news-and-insights";
		final String SET_LINK_TYPE_INTERNAL = "internal";
		final String SET_LINK_TYPE_EXTERNAL = "external";
		final String URL_CATALOG_KEY= "/catalog";

		if(siteSearchResultsBeanList!=null){
			for (int i =0; i< siteSearchResultsBeanList.size(); i++){
				SiteSearchResultsBean siteSearchResultsBean = siteSearchResultsBeanList.get(i);
				LOG.info("Content type :"+ siteSearchResultsBean.getContentType());
				if((siteSearchResultsBean.getContentType()!=null) && ((siteSearchResultsBean.getContentType().equals(PRODUCT_KEY)) || (siteSearchResultsBean.getContentType().equals(PRODUCTS_KEY)))){
					if(!Strings.isNullOrEmpty(siteSearchResultsBean.getUrl())&& (siteSearchResultsBean.getUrl().indexOf(URL_CATALOG_KEY)!=-1)){
						handleProductFamilyPage(siteSearchResultsBean);
					} else if((siteSearchResultsBean.getUrl()!=null) && (!siteSearchResultsBean.getUrl().equals(StringUtils.EMPTY))){
						siteSearchResultsBean.setContentType(CONTENT_TYPE_OTHERS);
						siteSearchResultsBean.setUrlTarget(CommonConstants.TARGET_SELF);
						if((siteSearchResultsBean.getUrl()!=null) && (!siteSearchResultsBean.getUrl().equals(StringUtils.EMPTY))){
							if(CommonUtil.isExternalURL(siteSearchResultsBean.getUrl())){
								siteSearchResultsBean.setLinkType(SET_LINK_TYPE_EXTERNAL);
								siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());
							} else {
								siteSearchResultsBean.setLinkType(SET_LINK_TYPE_INTERNAL);
								if(siteSearchResultsBean.getUrl()!=null){
									siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(),slingRequest.getResourceResolver()));
								}
								if(siteSearchResultsBean.getUrl()!=null){
									siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(),slingRequest.getResourceResolver()));
								}
							}
							siteSearchResultsBeanList.set(i, siteSearchResultsBean);
						}
					} else {
						siteSearchResultsBean.setContentType(CONTENT_TYPE_SKU);
						siteSearchResultsBean.setUrlTarget(CommonConstants.TARGET_SELF);
						List<SecondaryLink> secondaryLinkList = new ArrayList<SecondaryLink>();
						String skuPageUrl = StringUtils.EMPTY ;
						if(siteResourceSlingModel.getSkuPageURL()!=null){
							skuPageUrl = siteResourceSlingModel.getSkuPageURL();
						}
						skuPageUrl = CommonUtil.dotHtmlLinkSKU(skuPageUrl,slingRequest.getResourceResolver());
						String contextURL = skuPageUrl+"."+  CommonUtil.encodeURLString(siteSearchResultsBean.getTitle());
						if(contextURL!=null){
							contextURL = CommonUtil.dotHtmlLink(contextURL,slingRequest.getResourceResolver());
						}
						if(contextURL!=null){
							siteSearchResultsBean.setUrl(contextURL+ HTML_EXTN);
							siteSearchResultsBean.setCompleteUrl(contextURL+ HTML_EXTN);
						}
						SecondaryLink specificationsLink = new SecondaryLink();
						specificationsLink.setText(SPECIFICATION_KEY_DEFAULT);
						specificationsLink.setUrl(contextURL+SPECIFICATION_HASH_URL);
						SecondaryLink resourceLink = new SecondaryLink();
						resourceLink.setText(RESOURCE_KEY_DEFAULT);
						resourceLink.setUrl(contextURL+RESOURCE_HASH_URL);
						secondaryLinkList.add(specificationsLink);
						secondaryLinkList.add(resourceLink);
						siteSearchResultsBean.setSecondaryLinkList(secondaryLinkList);
						if((siteSearchResultsBean.getDsktopRendition()==null) || (siteSearchResultsBean.getDsktopRendition().equals(StringUtils.EMPTY))) {
							siteSearchResultsBean.setDsktopRendition(Mobile_SKU_Image);
						}
						if(((siteSearchResultsBean.getMobileRendition()==null) || (siteSearchResultsBean.getMobileRendition().equals(StringUtils.EMPTY))) && (slingRequest.getAttribute(CommonConstants.SITE_CONFIGURATION_OBJECT) != null)) {
							SiteResourceSlingModel siteConfiguration = (SiteResourceSlingModel) slingRequest.getAttribute(CommonConstants.SITE_CONFIGURATION_OBJECT);
							if ((siteConfiguration.getSkuFallBackImage()!=null) && (!siteConfiguration.getSkuFallBackImage().equals(StringUtils.EMPTY))) {
								siteSearchResultsBean.setMobileRendition(siteConfiguration.getSkuFallBackImage());
							}
						}
						siteSearchResultsBeanList.set(i, siteSearchResultsBean);
					}
				} else if((null != siteSearchResultsBean.getContentType()) && ((StringUtils.equals(CONTENT_TYPE_ARTICLE, siteSearchResultsBean.getContentType())) || (StringUtils.equals(CONTENT_TYPE_NEWS_AND_INSIGHT, siteSearchResultsBean.getContentType())))){
					siteSearchResultsBean.setUrlTarget(CommonConstants.TARGET_SELF);
					if((null != siteSearchResultsBean.getUrl()) && (StringUtils.isNotBlank(siteSearchResultsBean.getUrl()))){
						if(CommonUtil.isExternalURL(siteSearchResultsBean.getUrl())){
							siteSearchResultsBean.setLinkType(SET_LINK_TYPE_EXTERNAL);
							siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());
						} else {
							siteSearchResultsBean.setLinkType(SET_LINK_TYPE_INTERNAL);
							if(null != siteSearchResultsBean.getUrl()) {
								siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(),slingRequest.getResourceResolver()));
							}
							// Added on 2/21 by Punit to keep url label & url both same
							if(siteSearchResultsBean.getUrl()!=null){
								siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(),slingRequest.getResourceResolver()));
							}
						}
						siteSearchResultsBeanList.set(i, siteSearchResultsBean);
					}
				} else if((siteSearchResultsBean.getContentType()!=null) && (siteSearchResultsBean.getContentType().equals(RESOURCE_KEY))){
					if((siteSearchResultsBean.getUrl()!=null) && (!siteSearchResultsBean.getUrl().equals(StringUtils.EMPTY))){
						siteSearchResultsBean.setContentType(CONTENT_TYPE_RESOURCES);
						siteSearchResultsBean.setUrlTarget(CommonConstants.TARGET_BLANK);
						if(CommonUtil.isExternalURL(siteSearchResultsBean.getUrl())){
							siteSearchResultsBean.setLinkType(SET_LINK_TYPE_EXTERNAL);
							siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());
						} else {
							siteSearchResultsBean.setLinkType(SET_LINK_TYPE_INTERNAL);
							if(siteSearchResultsBean.getUrl()!=null){
								siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(),slingRequest.getResourceResolver()));
							}
							// Added on 2/21 by Punit to keep url label & url both same
							if(siteSearchResultsBean.getUrl()!=null){
								siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(),slingRequest.getResourceResolver()));
							}
						}
						String assetSize = StringUtils.EMPTY;
						String assetType = StringUtils.EMPTY;
						if((siteSearchResultsBean.getFileSize()!=null) && (!siteSearchResultsBean.getFileSize().equals(StringUtils.EMPTY))){
							// get the asset size in Bytes unit
							long fileSize = Long.parseLong(siteSearchResultsBean.getFileSize());
							long sizeOfAsset = (long) (fileSize / Math.pow(10, 6));
							String spaceUnit;
							// set the asset size unit
							if (sizeOfAsset < 1) {
								sizeOfAsset = (long) (fileSize / Math.pow(10, 3));
								spaceUnit = CommonConstants.KB;
								if (sizeOfAsset < 1) {
									sizeOfAsset = (long)(fileSize);
									spaceUnit = CommonConstants.B;
								}
							} else {
								spaceUnit = CommonConstants.MB;
							}
							assetSize = sizeOfAsset+ spaceUnit;
						}
						if((siteSearchResultsBean.getFileType()!=null) && (!siteSearchResultsBean.getFileType().equals(StringUtils.EMPTY))){
							if(siteSearchResultsBean.getFileType().equals(CommonConstants.FILE_TYPE_IMAGE_JPEG)){
								siteSearchResultsBean.setFileType(CommonConstants.FILE_TYPE_IMAGE_JPEG_VALUE);
							}else if(siteSearchResultsBean.getFileType().equals(CommonConstants.FILE_TYPE_APPLICATION_JAVASCRIPTS)){
								siteSearchResultsBean.setFileType(CommonConstants.FILE_TYPE_APPLICATION_JAVASCRIPTS_VALUE);
							}else if(siteSearchResultsBean.getFileType().equals(CommonConstants.FILE_TYPE_TEXT_HTML)){
								siteSearchResultsBean.setFileType(CommonConstants.FILE_TYPE_TEXT_HTML_VALUE);
								siteSearchResultsBean.setUrlTarget(CommonConstants.TARGET_SELF);
							}else if(siteSearchResultsBean.getFileType().equals(CommonConstants.FILE_TYPE_IMAGE_GIF)){
								siteSearchResultsBean.setFileType(CommonConstants.FILE_TYPE_IMAGE_GIF_VALUE);
							}else if(siteSearchResultsBean.getFileType().equals(CommonConstants.FILE_TYPE_APPLICATION_XML)){
								siteSearchResultsBean.setFileType(CommonConstants.FILE_TYPE_APPLICATION_XML_VALUE);
							}else if(siteSearchResultsBean.getFileType().equals(CommonConstants.FILE_TYPE_APPLICATION_MS_POWERPOINT)) {
								siteSearchResultsBean.setFileType(CommonConstants.FILE_TYPE_APPLICATION_MS_POWERPOINT_VALUE);
							}else if(siteSearchResultsBean.getFileType().equals(CommonConstants.FILE_TYPE_APPLICATION_MS_EXCEL)){
								siteSearchResultsBean.setFileType(CommonConstants.FILE_TYPE_APPLICATION_MS_EXCEL_VALUE);
							}else if(siteSearchResultsBean.getFileType().equals(CommonConstants.FILE_TYPE_APPLICATION_PDF)){
								siteSearchResultsBean.setFileType(CommonConstants.FILE_TYPE_APPLICATION_PDF_VALUE);
							}
							if(!assetSize.equals(StringUtils.EMPTY)){
								assetType = siteSearchResultsBean.getFileType()+CommonConstants.BLANK_SPACE;
							}else{
								assetType = siteSearchResultsBean.getFileType();
							}
						}
						siteSearchResultsBean.setFileSize(assetSize);
						siteSearchResultsBean.setFileType(assetType);
						siteSearchResultsBeanList.set(i, siteSearchResultsBean);
					}
				}else if((siteSearchResultsBean.getContentType()!=null) && (siteSearchResultsBean.getContentType().equals(SERVICE_KEY))){
					siteSearchResultsBean.setContentType(CONTENT_TYPE_OTHERS);
					siteSearchResultsBean.setUrlTarget(CommonConstants.TARGET_SELF);
					if((siteSearchResultsBean.getUrl()!=null) && (!siteSearchResultsBean.getUrl().equals(StringUtils.EMPTY))){
						if(CommonUtil.isExternalURL(siteSearchResultsBean.getUrl())){
							siteSearchResultsBean.setLinkType(SET_LINK_TYPE_EXTERNAL);
							siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());
						} else {
							siteSearchResultsBean.setLinkType(SET_LINK_TYPE_INTERNAL);
							if(siteSearchResultsBean.getUrl() != null){
								siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), slingRequest.getResourceResolver()));
							}
							if(siteSearchResultsBean.getUrl() != null){
								siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), slingRequest.getResourceResolver()));
							}
						}
						siteSearchResultsBeanList.set(i, siteSearchResultsBean);
					}
				}else{
					siteSearchResultsBean.setContentType(CONTENT_TYPE_OTHERS);
					siteSearchResultsBean.setUrlTarget(CommonConstants.TARGET_SELF);
					if((siteSearchResultsBean.getUrl()!=null) && (!siteSearchResultsBean.getUrl().equals(StringUtils.EMPTY))){
						if(CommonUtil.isExternalURL(siteSearchResultsBean.getUrl())){
							siteSearchResultsBean.setLinkType(SET_LINK_TYPE_EXTERNAL);
							siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());
						} else {
							siteSearchResultsBean.setLinkType(SET_LINK_TYPE_INTERNAL);
							if(siteSearchResultsBean.getUrl()!=null){
								siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(),slingRequest.getResourceResolver()));
							}
							// Added on 2/21 by Punit to keep url label & url both same
							if(siteSearchResultsBean.getUrl()!=null){
								siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(),slingRequest.getResourceResolver()));
							}
						}
						siteSearchResultsBeanList.set(i, siteSearchResultsBean);
					}
					setImageForEcommerceLearnPageType(siteSearchResultsBean);
				}
				if(siteSearchResultsBean.getContentType().equals(CONTENT_TYPE_OTHERS) &&
						(StringUtils.isBlank(siteSearchResultsBean.getDsktopRendition()) && StringUtils.isBlank(siteSearchResultsBean.getMobileRendition()))){
					// now you check if the image has already been set
					setDesktopAndMobileImageIntoBean(siteSearchResultsBean);
				}
			}
		}
		LOG.debug("Exit from populateContentType method");
	}

	private void handleProductFamilyPage(SiteSearchResultsBean siteSearchResultsBean) throws MalformedURLException {
		ResourceResolver resourceResolver = slingRequest.getResourceResolver();
		siteSearchResultsBean.setContentType(CONTENT_TYPE_FAMILY);
		siteSearchResultsBean.setUrlTarget(CommonConstants.TARGET_SELF);
		String urlFromSiteSearch = siteSearchResultsBean.getUrl();
		if(urlFromSiteSearch !=null){
			urlFromSiteSearch = CommonUtil.dotHtmlLink(urlFromSiteSearch, resourceResolver);
			populateNewProductBadge(siteSearchResultsBean, resourceResolver, urlFromSiteSearch);
			int index = urlFromSiteSearch.indexOf(HTML_EXTN);
			String contextURL = urlFromSiteSearch.substring(0,index);
			siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(urlFromSiteSearch,slingRequest.getResourceResolver()));
			siteSearchResultsBean.setUrl(urlFromSiteSearch);
			siteSearchResultsBean.setDsktopRendition(siteSearchResultsBean.getImage());
			siteSearchResultsBean.setMobileRendition(siteSearchResultsBean.getImage());
			List<SecondaryLink> secondaryLinkList = new ArrayList<>();
			SecondaryLink specificationsLink = new SecondaryLink();
			specificationsLink.setUrl(urlFromSiteSearch);
			if((siteSearchResultsBean.getMiddleTabName()!=null) && (!siteSearchResultsBean.getMiddleTabName().equals(StringUtils.EMPTY))){
				specificationsLink.setText(siteSearchResultsBean.getMiddleTabName());
			}else{
				specificationsLink.setText(MODELS);
			}
			specificationsLink.setUrl(contextURL+"."+(specificationsLink.getText()).toLowerCase()+ HTML_EXTN);

			LOG.debug("SRH::SpecificationLink page URL::"+specificationsLink.getUrl());
			SecondaryLink resourceLink = new SecondaryLink();
			resourceLink.setText(RESOURCE_KEY_DEFAULT);
			resourceLink.setUrl(contextURL+RESOURCE_HASH_URL);
			Resource resource = resourceResolver.getResource(("/content/eaton" + (new URL(contextURL.toLowerCase()).getPath())).replace(".html","").replace("_","-"));
			if(resource != null){
				Page page = resource.adaptTo(Page.class);
				if (new ProductTabsVersionChecker().isVersion2(Objects.requireNonNull(page))) {
					specificationsLink.setUrl(contextURL + HTML_EXTN + TAB_2_HASH);
					resourceLink.setUrl(contextURL + HTML_EXTN + TAB_3_HASH);
				}
			}
			secondaryLinkList.add(specificationsLink);
			secondaryLinkList.add(resourceLink);
			siteSearchResultsBean.setSecondaryLinkList(secondaryLinkList);
		}
	}

	private void setDesktopAndMobileImageIntoBean(SiteSearchResultsBean siteSearchResultsBean){
			siteSearchResultsBean.setDsktopRendition(siteSearchResultsBean.getImage());
			siteSearchResultsBean.setMobileRendition(siteSearchResultsBean.getImage());
	}
	private void setImageForEcommerceLearnPageType(SiteSearchResultsBean siteSearchResultsBean) {
		if(StringUtils.isNotBlank(siteSearchResultsBean.getEcommPageType()) && LEARN_PAGE_TYPE.equals(siteSearchResultsBean.getEcommPageType())){
			// this mean it's the eCommerce learn page
			if(StringUtils.isNotBlank(siteSearchResultsBean.getImage())){
				// we set the images for different rendition
				siteSearchResultsBean.setMobileRendition(siteSearchResultsBean.getImage());
				siteSearchResultsBean.setDsktopRendition(siteSearchResultsBean.getImage());
			} else {
				siteSearchResultsBean.setMobileRendition(Mobile_SKU_Image);
				siteSearchResultsBean.setDsktopRendition(Mobile_SKU_Image);
			}
		}
	}

	private void populateNewProductBadge(SiteSearchResultsBean siteSearchResultsBean,ResourceResolver adminResourceResolver,String link) {
		ValueMap pageProperties = null;
		String publicationDate = "";
		SimpleDateFormat publicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
		String pagePathLink = CommonUtil.getLinkPagePath(link);
		Resource linkPathResource = adminResourceResolver.getResource(pagePathLink);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				pageProperties = jcrResource.getValueMap();
			}
			if(pageProperties != null)
			{
				publicationDate = CommonUtil.getDateProperty(pageProperties, CommonConstants.PUBLICATION_DATE, publicationDateFormat);

				if(publicationDate != null && !publicationDate.isEmpty()) {
					long noOfDays = CommonUtil.getDaysDifference(publicationDate);
					if(noOfDays>=0 && noOfDays<=90)
						siteSearchResultsBean.setnewBadgeVisible(true);
		            else
		            	siteSearchResultsBean.setnewBadgeVisible(false);
					}else {
						siteSearchResultsBean.setnewBadgeVisible(false);
		            	LOG.debug("populateNewProductBadge:publicationDate is null for Family Page ");
				}
			}
		}
	}

	/**
	 * Populate auto correct bean.
	 */
	private void populateAutoCorrectBean(){
		LOG.debug("Entry into populateAutoCorrectBean method ");
		autoCorrect = new AutoCorrect();
		if((siteSearchResponse.getPageResponse().getDidYouMean()!=null) && (!(siteSearchResponse.getPageResponse().getDidYouMean().isEmpty()))){
			List<String> didYouMeanList = siteSearchResponse.getPageResponse().getDidYouMean();
			if((didYouMeanList.get(0)!=null) && (!didYouMeanList.get(0).equals(StringUtils.EMPTY))){
				autoCorrect.setSearchTerm(didYouMeanList.get(0));
			}
			if((didYouMeanList.get(1)!=null) && (!didYouMeanList.get(1).equals(StringUtils.EMPTY))){
				autoCorrect.setCorrectedTerm(didYouMeanList.get(1));
			}
		}
		String currentUrl = slingRequest.getRequestURI();
		if((currentUrl!=null) && (autoCorrect.getSearchTerm()!=null)){
			String[] contentPathList = currentUrl.split("\\.");
			if((contentPathList!=null) && (contentPathList.length>0)){
				autoCorrect.setUrl(contentPathList[0]+".searchTerm$"+autoCorrect.getSearchTerm()+".autocorrect$false.html");
			}
		}
		if((productGridSelectors!=null) && (productGridSelectors.getAutoCorrect()!=null) && (productGridSelectors.getAutoCorrect().equals(CommonConstants.FALSE))){
			autoCorrect.setEnabled(false);
		}else if((autoCorrect.getCorrectedTerm()!=null) && (autoCorrect.getSearchTerm()!=null) && (!autoCorrect.getSearchTerm().equals(StringUtils.EMPTY)) && (!autoCorrect.getCorrectedTerm().equals(StringUtils.EMPTY))){
			autoCorrect.setEnabled(true);
		}else {
			autoCorrect.setEnabled(false);
		}
		LOG.debug("Exit from populateAutoCorrectBean method ");
	}

	/**
	 * Populate active facets bean list.
	 */
	private void populateActiveFacetsBeanList(){
		LOG.debug("Entry into populateActiveFacetsBeanList method ");
		activeFacetsList = new ArrayList<FacetBean>();
		String deselectURL = "";
		List <FacetBean> activeFacetDetailList = productGridSelectors.getFacets();
		if(activeFacetDetailList != null){
			for(int i=0;i<activeFacetDetailList.size();i++){
				FacetBean activeFacets = activeFacetDetailList.get(i);
				activeFacets.setLabel(activeFacets.getFacetID());
				activeFacets.setFacetDeselectURL(facetURLBean.getContentPath()+activeFacets.getFacetDeselectURL()+facetURLBean.getFacetEndURL());
				if(facetGroupList != null){
					for(int j=0;j<facetGroupList.size();j++){
						FacetGroupBean facetGroupBean = facetGroupList.get(j);
						List<FacetValueBean>  facetValueList = facetGroupBean.getFacetValueList();
						for(int k=0;k<facetValueList.size();k++){
							FacetValueBean facetValueBean = facetValueList.get(k);
							if((activeFacets.getFacetID() != null) && (facetValueBean.getFacetValueId() != null) && (activeFacets.getFacetID().equals(facetValueBean.getFacetValueId())) ||
									facetValueBean.getFacetValueId().contains(activeFacets.getFacetID()) ? Boolean.TRUE : Boolean.FALSE){
										if(facetGroupBean.getFacetGroupId().contains(CONTENT_TYPE_KEYID)) {
												deselectURL = facetURLBean.getContentPath()+facetURLBean.getFacetEndURL();
											    activeFacets.setFacetDeselectURL(deselectURL);
										}
										activeFacets.setLabel(facetValueBean.getFacetValueLabel());
							}
						}
					}
				}
				activeFacetsList.add(activeFacets);
			}
		}
		LOG.debug("Exit from populateActiveFacetsBeanList method ");
		// Remove Duplication facets from search filter
		getRemoveDuplicateFacets(activeFacetsList);
	}

	private void getRemoveDuplicateFacets(List<FacetBean> activeFacetsList){
		try {
			final Map<String, Integer> duplicateFacetList = new HashMap<String, Integer>();
			if (null != activeFacetsList){
				for (int activeFacetsListIndex = 0; activeFacetsListIndex < this.activeFacetsList.size(); activeFacetsListIndex++) {
					if (null != duplicateFacetList && duplicateFacetList.containsKey(this.activeFacetsList.get(activeFacetsListIndex).getLabel())) {
						final String replaceUrl = this.activeFacetsList.get(activeFacetsListIndex).getFacetDeselectURL().replace(EndecaConstants.DOLLAR_SYMBOL + this.activeFacetsList.get(duplicateFacetList.get(this.activeFacetsList.get(activeFacetsListIndex).getLabel())).getFacetID(),StringUtils.EMPTY);
						this.activeFacetsList.get(activeFacetsListIndex).setFacetDeselectURL(replaceUrl);
						final int countNumber = duplicateFacetList.get(this.activeFacetsList.get(activeFacetsListIndex).getLabel());
						this.activeFacetsList.remove(countNumber);
						if (activeFacetsListIndex != 0) {
							activeFacetsListIndex = activeFacetsListIndex - 1;
						}
					} else {
						duplicateFacetList.put(this.activeFacetsList.get(activeFacetsListIndex).getLabel(), activeFacetsListIndex);
					}
				}
			}
		}catch (Exception e){
			LOG.error(String.format("Exception in getRemoveDuplicateFacets %s", e.getMessage()));
		}
	}

	/**
	 * Sets the I 18 N values.
	 */
	private void setI18NValues() {
		LOG.debug("Entry into setI18NValues method");
		try {
			List<SortByOptionValueBean> sortList = setSortByOptionList();
			facetedHeaderNavigationResultBean.setSortList(sortList);
			facetedHeaderNavigationResultBean.setResults(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_RESULTS,FACETS_KEY_RESULTS_DEFAULT));
			facetedHeaderNavigationResultBean.setSort(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_SORT,FACETS_KEY_SORT_DEFAULT));
			facetedHeaderNavigationResultBean.setRelevance(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_RELEVENCE,FACETS_KEY_RELEVENCE_DEFAULT));
			facetedHeaderNavigationResultBean.setAssendingSort(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_ASCENDING_SORT,FACETS_KEY_ASCENDING_SORT_DEFAULT));
			facetedHeaderNavigationResultBean.setDescendingSort(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_DESCENDING_SORT,FACETS_KEY_DESCENDING_SORT_DEFAULT));
			facetedHeaderNavigationResultBean.setClearFilters(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_CLEAR_FILTERS,FACETS_KEY_CLEAR_FILTERS_DEFAULT));
			facetedHeaderNavigationResultBean.setNarrowResults(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_NARROW_RESULTS,FACETS_KEY_NARROW_RESULTS_DEFAULT));
			facetedHeaderNavigationResultBean.setViewMore(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_VIEW_MORE,FACETS_KEY_VIEW_MORE_DEFAULT));
			facetedHeaderNavigationResultBean.setViewLess(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage, VGCommonConstants.VIEW_LESS,FACETS_KEY_VIEW_LESS_DEFAULT));
			facetedHeaderNavigationResultBean.setNewKeyword(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_NEW,FACETS_KEY_NEW_DEFAULT));
			facetedHeaderNavigationResultBean.setBestSeller(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_BESTSELLER,FACETS_KEY_BESTSELLER_DEFAULT));
			facetedHeaderNavigationResultBean.setRemoveFilters(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_REMOVE_FILTERS,FACETS_KEY_REMOVE_FILTERS_DEFAULT));
			facetedHeaderNavigationResultBean.setApply(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_APPLY,FACETS_KEY_APPLY_DEFAULT));
			facetedHeaderNavigationResultBean.setFilters(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_FILTERS,FACETS_KEY_FILTERS_DEFAULT));
			facetedHeaderNavigationResultBean.setGoTo(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,CARD_KEY_GO_TO,CARD_KEY_GO_TO_DEFAULT));
			LOG.debug("facetedHeaderNavigationResultBean" +facetedHeaderNavigationResultBean);
		} catch(Exception e){
			LOG.error(String.format("Exception in getting I18n values %s",e));
		}
		LOG.debug("Exit from setI18NValues method");
	}

	/**
	 * Sets the sort by option list.
	 *
	 * @return the list
	 */
	private List<SortByOptionValueBean> setSortByOptionList() {
		final String FACETS_KEY_RELEVENCE_DEFAULT_LABEL="Relevance";
		final String FACETS_KEY_RELEVENCE_DEFAULT_VALUE="relevance";
		defaultSortingOption = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_RELEVENCE,FACETS_KEY_RELEVENCE_DEFAULT_LABEL);
		List<SortByOptionValueBean> sortList = new ArrayList<SortByOptionValueBean>();
		SortByOptionValueBean bean1 = new SortByOptionValueBean();
		SortByOptionValueBean bean2 = new SortByOptionValueBean();
		SortByOptionValueBean bean3 = new SortByOptionValueBean();
		bean1.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_RELEVENCE,FACETS_KEY_RELEVENCE_DEFAULT_LABEL));
		bean2.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_ASCENDING_SORT,FACETS_KEY_ASCENDING_SORT_DEFAULT_LABEL));
		bean3.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_DESCENDING_SORT,FACETS_KEY_DESCENDING_SORT_DEFAULT_LABEL));
		bean1.setValue(FACETS_KEY_RELEVENCE_DEFAULT_VALUE);
		bean2.setValue(FACETS_KEY_ASCENDING_SORT_DEFAULT_VALUE);
		bean3.setValue(FACETS_KEY_DESCENDING_SORT_DEFAULT_VALUE);
		sortList.add(bean1);
		sortList.add(bean2);
		sortList.add(bean3);
		return sortList;
	}

	private List<SortByOptionValueBean> setAdvancedViewSortByOptionList() {
		List<SortByOptionValueBean> sortList = new ArrayList<SortByOptionValueBean>();
		SortByOptionValueBean bean1 = new SortByOptionValueBean();
		SortByOptionValueBean bean2 = new SortByOptionValueBean();
		SortByOptionValueBean bean3 = new SortByOptionValueBean();
		SortByOptionValueBean bean4 = new SortByOptionValueBean();
		final String FACETS_KEY_DATE_DESCENDING_SORT_DEFAULT_LABEL="Date Desc";
		final String FACETS_KEY_DATE_DESCENDING_SORT_VALUE="pub_date_desc";
		final String FACETS_KEY_DATE_ASCENDING_SORT_DEFAULT_LABEL="Date Asc";
		final String FACETS_KEY_DATE_ASCENDING_SORT_VALUE="pub_date_asc";
		bean1.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_ASCENDING_SORT,FACETS_KEY_ASCENDING_SORT_DEFAULT_LABEL));
		bean2.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_DESCENDING_SORT,FACETS_KEY_DESCENDING_SORT_DEFAULT_LABEL));
		bean3.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_DATE_DESCENDING_SORT,FACETS_KEY_DATE_DESCENDING_SORT_DEFAULT_LABEL));
		bean4.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,FACETS_KEY_DATE_ASCENDING_SORT,FACETS_KEY_DATE_ASCENDING_SORT_DEFAULT_LABEL));
		bean1.setValue(FACETS_KEY_ASCENDING_SORT_DEFAULT_VALUE);
		bean2.setValue(FACETS_KEY_DESCENDING_SORT_DEFAULT_VALUE);
		bean3.setValue(FACETS_KEY_DATE_DESCENDING_SORT_VALUE);
		bean4.setValue(FACETS_KEY_DATE_ASCENDING_SORT_VALUE);
		sortList.add(bean1);
		sortList.add(bean2);
		sortList.add(bean3);
		sortList.add(bean4);
		return sortList;
	}

	/**
	 * Sets the content type facet.
	 */
	private void setContentTypeFacet(){
		LOG.debug("Entry into setContentTypeFacet method ");

		final String FORM_FIELD_CHECKED = "checked";
		final String CONTENT_TYPE_NEWS_AND_INSIGHT= "news-and-insights";

		List<FacetGroupBean> updatedFacetGroupList = new ArrayList<FacetGroupBean>();
		FacetGroupBean contentTypeFacetGroup = new FacetGroupBean();
		contentTypeFacetGroup.setFacetGroupLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,CONTENT_KEY,CONTENT_KEY_DEFAULT));
		contentTypeFacetGroup.setFacetGroupId(CONTENT_TYPE_KEYID);
		contentTypeFacetGroup.setInputType(FACET_PROD_STATUS_INPUT_TYPE);
		List<FacetValueBean> contentTypeFacetGroupList = new ArrayList<FacetValueBean>();
		FacetValueBean productsFacets = new FacetValueBean();
		FacetValueBean newsFacets= new FacetValueBean();
		FacetValueBean resourcesFacets= new FacetValueBean();
		FacetValueBean servicesFacets= new FacetValueBean();
		productsFacets.setFacetValueLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,PRODUCTS_KEY,PRODUCTS_KEY_DEFAULT));
		newsFacets.setFacetValueLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,NEWS_AND_SIGHTS_KEY,NEWS_AND_SIGHTS_KEY_DEFAULT));
		resourcesFacets.setFacetValueLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,RESOURCE_KEY,RESOURCE_KEY_DEFAULT));
		servicesFacets.setFacetValueLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,SERVICE_KEY,SERVICE_KEY_DEFAULT));

		if((siteSearchResponse!=null) && (siteSearchResponse.getPageResponse()!=null)){

			final List<FacetGroupBean> facetList = siteSearchResponse.getPageResponse().getFacets().getFacetGroupList();
			if (facetList != null) {
				for (int facetListIndex = 0; facetListIndex < facetList.size(); facetListIndex++){
					FacetGroupBean facetGroupBean = facetList.get(facetListIndex);
					if(facetGroupBean.getFacetGroupId().equals(EndecaConstants.CONTENT_TYPE_FACET)){
						final List<FacetValueBean> facetvaluebean = facetGroupBean.getFacetValueList();
						for(int facetValueBeanIndex=0;facetValueBeanIndex<facetvaluebean.size();facetValueBeanIndex++){
								if(facetvaluebean.get(facetValueBeanIndex).getActiveRadioButton() !=null && facetvaluebean.get(facetValueBeanIndex).getActiveRadioButton().equalsIgnoreCase("true")){
									if(facetvaluebean.get(facetValueBeanIndex).getFacetValueLabel().equals(PRODUCT_KEY))
										productsFacets.setActiveRadioButton(FORM_FIELD_CHECKED);
									else if(facetvaluebean.get(facetValueBeanIndex).getFacetValueLabel().equals(RESOURCE_KEY))
										resourcesFacets.setActiveRadioButton(FORM_FIELD_CHECKED);
									else if(facetvaluebean.get(facetValueBeanIndex).getFacetValueLabel().equals(SERVICE_KEY))
										servicesFacets.setActiveRadioButton(FORM_FIELD_CHECKED);
									else if(facetvaluebean.get(facetValueBeanIndex).getFacetValueLabel().equals(CONTENT_TYPE_NEWS_AND_INSIGHT))
										newsFacets.setActiveRadioButton(FORM_FIELD_CHECKED);
								}
						}
					}
				}
			}
			if((siteResourceSlingModel!=null) && (siteResourceSlingModel.getEnableProductsTab()!=null) && (siteResourceSlingModel.getEnableProductsTab().equals(RESOURCE_ENABLED_ON))
			  && (siteSearchResponse.getPageResponse().getProductsCount()!=null)){
				if(siteSearchResponse.getPageResponse().getProductsId()!=null){
					productsFacets.setFacetValueId(siteSearchResponse.getPageResponse().getProductsId());

					}

				contentTypeFacetGroupList.add(productsFacets);
			}
			if((siteResourceSlingModel!=null) && (siteResourceSlingModel.getEnableNewsTab()!=null) && (siteResourceSlingModel.getEnableNewsTab().equals(RESOURCE_ENABLED_ON))
				&& (siteSearchResponse.getPageResponse().getNewsCount()!=null)){
				if(siteSearchResponse.getPageResponse().getNewsId()!=null){
					newsFacets.setFacetValueId(siteSearchResponse.getPageResponse().getNewsId());

				}

				contentTypeFacetGroupList.add(newsFacets);
			}
			if((siteResourceSlingModel!=null) && (siteResourceSlingModel.getEnableResourcesTab()!=null) && (siteResourceSlingModel.getEnableResourcesTab().equals(RESOURCE_ENABLED_ON))
				 && (siteSearchResponse.getPageResponse().getResourcesCount()!=null)){
				if(siteSearchResponse.getPageResponse().getResourcesId()!=null){
					resourcesFacets.setFacetValueId(siteSearchResponse.getPageResponse().getResourcesId());

				}

				contentTypeFacetGroupList.add(resourcesFacets);
			}
			if((siteResourceSlingModel!=null) && (siteResourceSlingModel.getEnableServicesTab()!=null) && (siteResourceSlingModel.getEnableServicesTab().equals(RESOURCE_ENABLED_ON))
			 && (siteSearchResponse.getPageResponse().getServicesCount()!=null)){
				if(siteSearchResponse.getPageResponse().getServicesId()!=null){
					servicesFacets.setFacetValueId(siteSearchResponse.getPageResponse().getServicesId());

				}

				contentTypeFacetGroupList.add(servicesFacets);
			}
			if(!(contentTypeFacetGroupList.isEmpty())){
				contentTypeFacetGroup.setFacetValueList(contentTypeFacetGroupList);
				updatedFacetGroupList.add(contentTypeFacetGroup);
			}
		}
		extractFacetGroups(updatedFacetGroupList);
		LOG.debug("Entry into setContentTypeFacet method ");
	}

	private List<FacetGroupBean> updateSearchFacetGroupsBySiteConfigs(final List<FacetGroupBean> updatedFacetGroupList) {
		List<FacetGroupBean> configuredFacetGroupsList = null;
		if(StringUtils.equalsIgnoreCase(viewType,CommonConstants.SEARCH_ADVANCED_VIEW)){
			configuredFacetGroupsList = searchResultsModel.getFacetGroupBeanList();
		}else{
			configuredFacetGroupsList = siteResourceSlingModel.getFacetGroupsList();
		}
		List<FacetGroupBean> localFacetGroupBeanList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(updatedFacetGroupList) && CollectionUtils.isNotEmpty(configuredFacetGroupsList)){
			for (FacetGroupBean configFacetGroupBean  : configuredFacetGroupsList ) {
				for (FacetGroupBean  facetGroupBean : updatedFacetGroupList) {
					if (StringUtils.equalsIgnoreCase(facetGroupBean.getFacetGroupId(),configFacetGroupBean.getFacetGroupId() )) {
						facetGroupBean.setGridFacet(configFacetGroupBean.isGridFacet());
						final boolean isSingleFacetEnabled = facetGroupBean.getFacetGroupId().equals(EndecaConstants.CONTENT_TYPE_FACET) ?
								Boolean.TRUE : configFacetGroupBean.isSingleFacetEnabled();
						facetGroupBean.setFacetSearchEnabled(configFacetGroupBean.isFacetSearchEnabled());
						facetGroupBean.setSingleFacetEnabled(isSingleFacetEnabled);
						localFacetGroupBeanList.add(facetGroupBean);
						break;
					}
				}

			}
		}
		if (CollectionUtils.isEmpty(localFacetGroupBeanList)){
			localFacetGroupBeanList = updatedFacetGroupList;
		}
		return  localFacetGroupBeanList;
	}

	/**
	 * Sets the Secure facet.
	 */
	private List<FacetGroupBean> addSecureFacet(List<FacetGroupBean> updatedFacetGroupList){

		LOG.debug("Entry into addSecureFacet method ");
		FacetGroupBean secureTypeFacetGroup = new FacetGroupBean();
		secureTypeFacetGroup.setFacetGroupLabel(SecureConstants.SECURE_FILTER_GROUP_LABEL);
		secureTypeFacetGroup.setFacetGroupId(SecureConstants.SECURE_FACET_ID);
		List<FacetValueBean> secureFacetGroupList = new ArrayList<>();
		FacetValueBean secureFacets = new FacetValueBean();
		if (facetURLBean.getTabStartURL() != null) {
			secureFacets.setFacetStartURL(facetURLBean.getTabStartURL());
		}
		if (facetURLBean.getTabEndURL() != null) {
			secureFacets.setFacetEndURL(facetURLBean.getTabEndURL());
		}
		addSecureSelectionFlagIfChecked(secureFacets);
		secureFacets.setFacetValueLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, SecureConstants.SECURE_FILTER_LABEL, SecureConstants.SECURE_FILTER_LABEL));
		if ((siteResourceSlingModel != null) && (siteResourceSlingModel.getEnableProductsTab() != null) && (siteResourceSlingModel.getEnableProductsTab().equals(RESOURCE_ENABLED_ON))) {
			secureFacets.setFacetValueId(SecureConstants.SECURE_FILTER_LABEL);
			secureFacetGroupList.add(secureFacets);
		}
		if (!(secureFacetGroupList.isEmpty())) {
			secureTypeFacetGroup.setFacetValueList(secureFacetGroupList);
			updatedFacetGroupList.add(0, secureTypeFacetGroup);
		}
		LOG.debug("End Of addSecureFacet method ");

		return updatedFacetGroupList;

	}

	/**
	 *  Select the checkbox if secure option checked.
	 * @param secureFacets
	 */
	private void addSecureSelectionFlagIfChecked(FacetValueBean secureFacets){
		if(productGridSelectors.getFacets()!=null) {
			for (FacetBean facetBean : productGridSelectors.getFacets()) {
				if (facetBean.getFacetID().equalsIgnoreCase(SecureConstants.SECURE_FILTER_LABEL)) {
					secureFacets.setActiveRadioButton(FORM_FIELD_CHECKED); // Checks Secure Facet
				}
			}
		}else{
			secureFacets.setActiveRadioButton(StringUtils.EMPTY); // Uncheck Secure Facet
		}
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
	 * Gets the page type.
	 *
	 * @return the page type
	 */
	public String getPageType() {
		return pageType;
	}

	/**
	 * Gets the tab list.
	 *
	 * @return the tab list
	 */
	public List<Tabs> getTabList() {
		return tabList;
	}

	/**
	 * Sets the tab list.
	 *
	 * @param tabList the new tab list
	 */
	public void setTabList(List<Tabs> tabList) {
		this.tabList = tabList;
	}

	/**
	 * Gets the site search results bean list.
	 *
	 * @return the site search results bean list
	 */
	public List<SiteSearchResultsBean> getSiteSearchResultsBeanList() {
		return siteSearchResultsBeanList;
	}

	/**
	 * Sets the site search results bean list.
	 *
	 * @param siteSearchResultsBeanList the new site search results bean list
	 */
	public void setSiteSearchResultsBeanList(List<SiteSearchResultsBean> siteSearchResultsBeanList) {
		this.siteSearchResultsBeanList = siteSearchResultsBeanList;
	}

	/**
	 * Gets the faceted header navigation result bean.
	 *
	 * @return the faceted header navigation result bean
	 */
	public FacetedHeaderNavigationResultBean getFacetedHeaderNavigationResultBean() {
		return facetedHeaderNavigationResultBean;
	}

	/**
	 * Sets the faceted header navigation result bean.
	 *
	 * @param facetedHeaderNavigationResultBean the new faceted header navigation result bean
	 */
	public void setFacetedHeaderNavigationResultBean(FacetedHeaderNavigationResultBean facetedHeaderNavigationResultBean) {
		this.facetedHeaderNavigationResultBean = facetedHeaderNavigationResultBean;
	}

	/**
	 * Sets the facet URL bean.
	 *
	 * @param facetURLBean the new facet URL bean
	 */
	public void setFacetURLBean(FacetURLBean facetURLBean) {
		this.facetURLBean = facetURLBean;
	}

	/**
	 * Gets the ajax url.
	 *
	 * @return the ajax url
	 */
	public String getAjaxUrl() {
		return ajaxUrl;
	}

	/**
	 * Sets the ajax url.
	 *
	 * @param ajaxUrl the new ajax url
	 */
	public void setAjaxUrl(String ajaxUrl) {
		this.ajaxUrl = ajaxUrl;
	}

	/**
	 * Gets the auto correct.
	 *
	 * @return the auto correct
	 */
	public AutoCorrect getAutoCorrect() {
		return autoCorrect;
	}

	/**
	 * Sets the auto correct.
	 *
	 * @param autoCorrect the new auto correct
	 */
	public void setAutoCorrect(AutoCorrect autoCorrect) {
		this.autoCorrect = autoCorrect;
	}

	/**
	 * Gets the load more attr json string.
	 *
	 * @return the load more attr json string
	 */
	public String getLoadMoreAttrJsonString() {
		return loadMoreAttrJsonString;
	}

	/**
	 * Gets the current selected tab.
	 *
	 * @return the current selected tab
	 */
	public String getCurrentSelectedTab() {
		return currentSelectedTab;
	}

	/**
	 * Sets the current selected tab.
	 *
	 * @param currentSelectedTab the new current selected tab
	 */
	public void setCurrentSelectedTab(String currentSelectedTab) {
		this.currentSelectedTab = currentSelectedTab;
	}

	/**
	 * Gets the total results.
	 *
	 * @return the total results
	 */
	public int getTotalResults() {
		int resultCount = 0;
		if(StringUtils.equals(CommonConstants.SEARCH_DEFAULT_VIEW,searchResultsModel.getView())) {
			resultCount = totalResults;
		} else if(StringUtils.equals(CommonConstants.SEARCH_SIMPLE_VIEW,searchResultsModel.getView())) {
			if(StringUtils.equals(CommonConstants.SEARCH_CONTENT_TYPE_PRODUCT,searchResultsModel.getContentTypeOption())) {
				resultCount = siteSearchResponse.getPageResponse().getProductsCount();
			} else if(StringUtils.equals(CommonConstants.SEARCH_CONTENT_TYPE_NEWS,searchResultsModel.getContentTypeOption())) {
				resultCount = siteSearchResponse.getPageResponse().getNewsCount();
			} else if(StringUtils.equals(CommonConstants.SEARCH_CONTENT_TYPE_RESOURCES,searchResultsModel.getContentTypeOption())) {
				resultCount = siteSearchResponse.getPageResponse().getResourcesCount();
			} else if(StringUtils.equals(CommonConstants.SEARCH_CONTENT_TYPE_SERVICES,searchResultsModel.getContentTypeOption())) {
				resultCount = siteSearchResponse.getPageResponse().getServicesCount();
			}
		}
		return resultCount;
	}

	private void getAuthoredFacetTags(ResourceResolver adminResourceResolver,
									  List<String> filterValues) {
		//  get the facet tags from the dialog
		if(adminResourceResolver!=null){
			String productGridResourcePath = slingRequest.getResource().getPath();
			Resource productGridResource = adminResourceResolver.getResource(productGridResourcePath);
			if(productGridResource!=null){
				ValueMap productGridResourceProperties = productGridResource.getValueMap();
				String[] authoredFacetTags = (String[])productGridResourceProperties.get("tags");
				if(authoredFacetTags!=null){
					for(int j=0;j<authoredFacetTags.length;j++){
						Tag facetTag = null;
						if(tagManager!=null){
							facetTag=tagManager.resolve(authoredFacetTags[j]);
							if(facetTag != null){
								String facetTagName = facetTag.getName();
								if((facetTagName!=null) && (!facetTagName.equals(StringUtils.EMPTY))){
									Tag parentTag = facetTag.getParent();
									if(parentTag!=null){
										String parentFacetTagName = parentTag.getName();
										if((parentFacetTagName!=null) && (!parentFacetTagName.equals(StringUtils.EMPTY))){
											String filterValue = parentFacetTagName+"_"+facetTagName;
											if(filterValue.contains(CommonConstants.AMPERSAND)){
												filterValue = filterValue.replace(CommonConstants.AMPERSAND, CommonConstants.ARRAY_EMPTY);
											}
											filterValues.add(filterValue);
										}
									}
								}
							}
						}
					}
					filterValues.add(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL);
				}
			}
		}
	}

	/**
	 * Sets the total results.
	 *
	 * @param totalResults the new total results
	 */
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	/**
	 * Gets the all results URL.
	 *
	 * @return the all results URL
	 */
	public String getAllResultsURL() {
		return allResultsURL;
	}

	/**
	 * Sets the all results URL.
	 *
	 * @param allResultsURL the new all results URL
	 */
	public void setAllResultsURL(String allResultsURL) {
		this.allResultsURL = allResultsURL;
	}

	/**
	 * Gets the analytics event DTM.
	 *
	 * @return the analytics event DTM
	 */
	public String getAnalyticsEventDTM() {
		return CommonConstants.DTM_SEARCH_FACET;
	}


	/**
	 * Gets the search term.
	 *
	 * @return the search term
	 */
	public String getSearchTerm() {
		return searchTerm;
	}


	/**
	 * Sets the search term.
	 *
	 * @param searchTerm the new search term
	 */
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	/**
	 * Gets the load more flag.
	 *
	 * @return the load more flag
	 */
	public String getLoadMoreFlag() {
		return loadMoreFlag;
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


	/**
	 * Gets the zero results.
	 *
	 * @return the zero results
	 */
	public String getZeroResults() {
		return zeroResults;
	}

	/**
	 * Gets the search results model.
	 *
	 * @return the search results model
	 */
	public SearchResultsModel getSearchResultsModel() {
		return searchResultsModel;
	}


	/**
	 * Sets the zero results.
	 *
	 * @param zeroResults the new zero results
	 */
	public void setZeroResults(String zeroResults) {
		this.zeroResults = zeroResults;
	}

	public String getFacetGroupListJson() {
		return facetedNavigationHelperV2.getFacetGroupListJson(currentPage,facetGroupList, activeFacetsList, facetURLBean, currentSelectedTab, isAuthenticated);
	}

	public String getFacetGroupOrderingJson() {
		return facetedNavigationHelperV2.getFacetGroupOrderingJson(currentPage,facetGroupList, activeFacetsList, facetURLBean, currentSelectedTab, isAuthenticated);
	}

	public String getHideGlobalFacetSearch() {
		return hideGlobalFacetSearch;
	}
	public String getActiveFacetsListJson() {
		return facetedNavigationHelperV2.getActiveFacetsListJson(activeFacetsList);
	}
	public FacetURLBean getFacetURLBean() {
		return facetURLBean;
     }

	public List<FacetBean> getActiveFacetsList() {
		return activeFacetsList;
	}

	public String getSearchInsteadFor() {
		return searchInsteadFor;
	}

	public String getShowingResultsFor() {
		return showingResultsFor;
	}

	public String getDefaultSortingOption() {
		return defaultSortingOption;
	}


}
