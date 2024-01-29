/**
 * Product Grid Component resides in product Grid package
 */
package com.eaton.platform.core.models.productgrid;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.FacetURLBean;
import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;
import com.eaton.platform.core.bean.FacetedHeaderNavigationResultBean;
import com.eaton.platform.core.bean.ProductCardSubcategoryBean;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.bean.SortByOptionValueBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.helpers.FacetedNavigationHelperV2;
import com.eaton.platform.core.models.EndecaConfigModel;
import com.eaton.platform.core.models.EndecaFacetTag;
import com.eaton.platform.core.models.PageDecorator;
import com.eaton.platform.core.models.PdhConfigModel;
import com.eaton.platform.core.models.ProductGridSlingModel;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.services.EatonConfigService;
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
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.FacetsBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.familymodule.FamilyModuleBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.filters.SecureFacetFilter;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.ProductFamilyBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The Class ProductGrid Model.
 *
 * @author E0362677
 */

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductGridModel {

    /**
     * The facets key results.
     */
    private static final String FACETS_KEY_RESULTS = "results";
    /**
     * The facets key sort.
     */
    private static final String FACETS_KEY_SORT = "ProductGrid.sortBy";
    /**
     * The facets key relevance.
     */
    private static final String FACETS_KEY_RELEVENCE = "relevance";
    /**
     * The facets key ascending sort.
     */
    private static final String FACETS_KEY_ASCENDING_SORT = "ProductGrid.alphaAtoZ";
    /**
     * The facets key descending sort.
     */
    private static final String FACETS_KEY_DESCENDING_SORT = "ProductGrid.alphaZtoA";
    /**
     * The facets key clear filters.
     */
    private static final String FACETS_KEY_CLEAR_FILTERS = "clearFilters";
    /**
     * The facets key narrow results.
     */
    private static final String FACETS_KEY_NARROW_RESULTS = "narrowResults";
    /**
     * The facets key view more.
     */
    private static final String FACETS_KEY_VIEW_MORE = "viewMore";
    /**
     * The facets key new.
     */
    private static final String FACETS_KEY_NEW = "newKeyword";
    /**
     * The facets key bestseller.
     */
    private static final String FACETS_KEY_BESTSELLER = "bestSeller";
    /**
     * The card key go to.
     */
    private static final String CARD_KEY_GO_TO = "goTo";
    /**
     * The card key load more.
     */
    private static final String CARD_KEY_LOAD_MORE = "loadMore";
    /**
     * Compare products pop up
     */
    private static final String COMPARE_PRODUCTS = "compareProducts";
    /**
     * Product comparision table
     */
    private static final String COMPARISION_TABLE = "compareTable";
    /**
     * Clear selection for the selected products for comparision
     */
    private static final String CLEAR_SELECTION = "clearSelection";
    /**
     * Error message heading for maximum products
     */
    private static final String MAX_ERROR_HEADING = "maxErrorHeading";
    /**
     * Error description for maximum products
     */
    private static final String MAX_ERROR_DESCRIPTION = "maxErrorDescription";
    /**
     * Error message heading for minimum products
     */
    private static final String MIN_ERROR_HEADING = "minErrorHeading";
    /**
     * Error message description for minimum products
     */
    private static final String MIN_ERROR_DESCRIPTION = "minErrorDescription";
    /**
     * Closing comparison Table message
     */
    private static final String BACK_TO_SELECTION = "backToSelection";
    private static final String HIGHLIGHT_DIFF = "highlightDifferences";
    private static final String SHOW_DIFFRENCES = "showOnlyDifferences";
    private static final String HIDE_BLANK_FEATURES = "hideBlankFeatures";
    /**
     * The facets key remove filters.
     */
    private static final String FACETS_KEY_REMOVE_FILTERS = "removeFilters";
    /**
     * The facets key apply.
     */
    private static final String FACETS_KEY_APPLY = "apply";
    /**
     * The facets key filters.
     */
    private static final String FACETS_KEY_FILTERS = "filters";
    /**
     * product family facet sorting
     */
    private static final String TAXONOMY_ATTRIBUTE = "taxonomyAttributeValue";
    /**
     * Global Attribute value
     */
    private static final String GLOBAL_ATTRIBUTE = "globalAttributeValue";
    /**
     * Default sort Order
     */
    private static final String DEFAULT_SORT_ORDER = "defaultSortOrder";
    /**
     * Alpha A to Z sorting
     */
    private static final String ALPHA_A_TO_Z = "Alpha: A to Z";
    /**
     * Alpha Z to A sorting
     */
    private static final String ALPHA_Z_TO_A = "Alpha: Z to A";
    /**
     * Radion Button state -Checked
     */
    private static final String CHECKED = "checked";
    /**
     * String false
     */
    private static final String FALSE = "false";
    /**
     * String True
     */
    private static final String TRUE = "true";
    /**
     * Component name Radios
     */
    private static final String RADIOS = "radios";
    /**
     * Facet Search enabled
     */
    public static final String FACET_GROUP_FACET_SEARCH_ENABLED = "facetSearchEnabled";
    /**
     * Facet sortOrder
     */
    public static final String FACET_GROUP_FACET_SORT = "sortOrder";
    /**
     * Single facet enabled
     */
    public static final String SINGLE_FACET_ENABLED = "singleFacetEnabled";
    /**
     * Show as Grid
     */
    private static final String FACET_GROUP_SHOW_AS_GRID = "showAsGrid";
    /**
     * Descending
     */
    private static final String DESC = "desc";
    /**
     * Ascending
     */
    private static final String ASC = "asc";
    /**
     * High to low
     */
    private static final String HIGHTOLOW = " :High To Low";
    /**
     * Low to High
     */
    private static final String LOWTOHIGH = " :Low To High";
    /**
     * Relevance
     */
    private static final String RELEVANCESTR = "Relevance";

    /**
     * Product Grid Log reference
     */
    private static final Logger LOG = LoggerFactory.getLogger(ProductGridModel.class);

    /**
     * The sku list response bean.
     */
    private SKUListResponseBean skuListResponseBean;

    /**
     * The productType list response bean.
     */
    private SKUListResponseBean productTypeListResponseBean;

    /**
     * The page type.
     */
    private String pageType;

    /**
     * The pdh page flag.
     */
    private boolean pdhFlag = Boolean.TRUE;

    /**
     * The faceted header navigation result bean.
     */
    private FacetedHeaderNavigationResultBean facetedHeaderNavigationResultBean;

    private FacetedNavigationHelperV2 facetedNavigationHelperV2;

    /**
     * The pg model.
     */
    private ProductGridSlingModel pgModel;

    private FacetsBean allReturnedFacetsBean;

    /**
     * The product family list.
     */
    private List<ProductFamilyBean> productFamilyList;

    /**
     * The family module bean list.
     */
    private List<FamilyModuleBean> familyModuleBeanList;

    /**
     * The site resource sling model.
     */
    private SiteResourceSlingModel siteResourceSlingModel;

    /**
     * The product card subcategory bean.
     */
    private ProductCardSubcategoryBean productCardSubcategoryBean;

    /**
     * The FaceGroup Of Facet Value Selected
     */
    private List<String> selectedFacetsValueGroupList;

    /**
     * The base SKU path.
     */
    private String baseSKUPath;

    /**
     * The product grid selectors.
     */
    private ProductGridSelectors productGridSelectors;

    /**
     * The product family details.
     */
    private ProductFamilyDetails productFamilyDetails;

    /**
     * The admin service.
     */
    @Inject
    protected AdminService adminService;

    /**
     * The Tag Manager.
     */
    @Inject
    protected TagManager tagManager;

    /**
     * The load more attr json string.
     */
    private String loadMoreAttrJsonString;

    /**
     * The sku page name.
     */
    private String skuPageName;

    /**
     * The country.
     */
    private String country;

    /**
     * Load More Flag.
     */
    private String loadMoreFlag;

    /**
     * subCategory Hide and Show
     */
    private String subCategoryHideShow;

    /**
     * Description Hide and Show
     */
    private String hideProductDescription;

    /**
     * Facet value count from site config
     */
    private int facetValueCount;

    private String defaultSortOption;

    private PdhConfigModel pdhConfigModel;

    private EndecaConfigModel endecaCloudConfig;

    @SlingObject
    private Resource resource;

    @Inject
    private String hideGlobalFacetSearch;

    @Inject
    protected Page currentPage;

    @Inject
    protected ValueMap currentPageProperties;

    /**
     * The SlingHttpServletRequest
     */
    @Inject
    @Source("sling-object")
    private SlingHttpServletRequest slingRequest;

    /**
     * The FacetURLBeanService
     */
    @Inject
    private FacetURLBeanService facetURLBeanService;

    /**
     * The Externalizer
     */
    @Inject
    private Externalizer externalizer;

    /**
     * The EndecaRequestService
     */
    @Inject
    private EndecaRequestService endecaRequestService;

    /**
     * The EndecaService
     */
    @Inject
    private EndecaService endecaService;

    /**
     * The ProductFamilyDetailService
     */
    @Inject
    private ProductFamilyDetailService productFamilyDetailService;

    @Inject
    @Via("resource")
    private Resource res;

    @Inject
    @Via("resource")
    private String zeroFacetResultsMessage;

    protected FacetURLBean facetURLBean;

    protected List<FacetGroupBean> facetGroupList = new ArrayList<>();

    protected List<FacetBean> activeFacetsList;

    protected String currentSelectedTab;

    /**
     * The EatonConfigService
     */
    @Inject
    protected EatonConfigService configService;

    /**
     * The Cloud Config Service
     */
    @Inject
    protected CloudConfigService cloudConfigService;

    /**
     * The Eaton Site Config Service
     */
    @Inject
    protected EatonSiteConfigService eatonSiteConfigService;


    @Inject
    private AuthorizationService authorizationService;

    @Inject
    private AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    protected EndecaConfig endecaConfigService;

    private AuthenticationToken authenticationToken;
    private boolean isAuthenticated;
    private static final String RESOURCE_ENABLED_ON = "on";
    final String FORM_FIELD_CHECKED = "checked";

    private String isSubCategoryView;
    public static final String DIGITALTOOL_COMPARE_TAB = "compare";
    public static final String DIGITALTOOL_SPECIFICATION_TAB = "specification";
    public static final String MODEL_CODE = "modelCode";
    public static final String DIGITALTOOL_RESOURCE_TAB = "resource";
    public static final String DIGITALTOOL_COMPARE_BUTTON = "Compare";
    public static final String DIGITALTOOL_STR_OK = "Ok";
    public static final String DIGITALTOOL_CANCEL = "Cancel";
    public static final String DIGITALTOOL_CONTINUE = "Continue";
    public static final int DEFAULT_STATUS_BADGE_TIME = 90;
    private static final String FACET_DOLLAR = "facets$";

    @PostConstruct
    public void init() {
        LOG.debug("ProductGridModel : This is Entry into init() method");
        if (null != resource) {
            pgModel = resource.adaptTo(ProductGridSlingModel.class);
            if (null != pgModel) {
                subCategoryHideShow = pgModel.getSubCategoryHideShow();
                hideGlobalFacetSearch = pgModel.getHideGlobalFacetSearch();
                hideProductDescription = pgModel.getHideProductDescription();
                isSubCategoryView = StringUtils.equals(pgModel.getView(), CommonConstants.SUBCATEGORY) ? CommonConstants.TRUE : CommonConstants.FALSE;
            }
            if (currentPage != null) {
                currentPageProperties = currentPage.getProperties();
            }
            if (currentPageProperties != null && currentPageProperties.get(CommonConstants.PAGE_TYPE) != null) {
                pageType = currentPageProperties.get(CommonConstants.PAGE_TYPE).toString();
            }
            if (adminService != null) {
                try (ResourceResolver adminResourceResolver = adminService.getReadService()) {

                    if (null != slingRequest) {
                        authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
                    }
                    if (authenticationToken != null && (authenticationToken.getUserProfile() != null || authenticationToken.getBypassAuthorization())) {
                        isAuthenticated = true;
                    } else {
                        isAuthenticated = false;
                    }
                    if (adminResourceResolver != null) {
                        tagManager = adminResourceResolver.adaptTo(TagManager.class);
                    }
                    if (cloudConfigService != null) {
                        pdhConfigModel = cloudConfigService.getPdhCloudConfig(res).isPresent() ? cloudConfigService.getPdhCloudConfig(res).get() : null;
                        endecaCloudConfig = cloudConfigService.getEndecaCloudConfig(res).isPresent() ? cloudConfigService.getEndecaCloudConfig(res).get() : null;
                    }
                    if (eatonSiteConfigService != null) {
                        Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);
                        if (siteConfig.isPresent()) {
                            siteResourceSlingModel = siteConfig.get();
                        } else {
                            siteResourceSlingModel = null;
                            LOG.error("Product grid Model Site config was not authored : siteResourceSlingModel is null");
                        }
                        if (null != slingRequest && null != facetURLBeanService && siteResourceSlingModel != null) {
                            final String contentResourcePath = CommonUtil.getMappedUrl(currentPage.getPath(), externalizer, slingRequest);
                            String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(String.format("ProductGridModel : selectors from slingRequest: %s",
                                        Arrays.toString(selectors)));
                            }
                            List<String> selectorList = Arrays.stream(selectors).collect(Collectors.toList());
                            final EndecaConfigServiceBean configServiceBean = endecaConfigService.getConfigServiceBean();
                            if(selectorList.isEmpty() || Collections.singletonList("nocache").containsAll(selectorList)|| Collections.singletonList("anon").containsAll(selectorList)) {
                                if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
                                    selectorList.add(FACET_DOLLAR+configServiceBean.getProductFamilyActiveFacetID());
                                    selectors = selectorList.toArray(selectors);

                                }
                            }
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(String.format("ProductGridModel : selectors after transformation based on pageType: %s",
                                        Arrays.toString(selectors)));
                            }
                            FacetURLBeanServiceResponse facetURLBeanResponse = facetURLBeanService.getFacetURLBeanResponse(
                                    selectors, siteResourceSlingModel.getPageSize(),
                                    pageType, contentResourcePath);
                            if (facetURLBeanResponse != null) {
                                facetURLBean = facetURLBeanResponse.getFacetURLBean();
                                productGridSelectors = facetURLBeanResponse.getProductGridSelectors();
                            }
                        }
                    }
                    facetedHeaderNavigationResultBean = new FacetedHeaderNavigationResultBean();
                    populateSortByUrl();
                    if (endecaRequestService != null) {
                        final String inventoryId = endecaRequestService.getInventoryId(currentPage, adminResourceResolver);
                        productCardSubcategoryBean = new ProductCardSubcategoryBean();
                        if (null != currentPage && configService != null) {
                            Locale locale = currentPage.getLanguage(true);
                            country = locale.getCountry();
                            if (isProductSubCategoryPage(this.pageType)){
                                fillDetailsOfProductSubCategoryPageType(adminResourceResolver, locale, inventoryId, slingRequest);
                            } else if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
                                final String priceListPath = configService.getConfigServiceBean().getPriceListFolderPath();
                                skuPageName = configService.getConfigServiceBean().getSkupagename();
                                final String jsonfilename = configService.getConfigServiceBean().getJsonFileName();
                                final String rootElementName = configService.getConfigServiceBean().getJsonRootElementName();
                                String extension = slingRequest.getRequestPathInfo().getExtension();
                                if (extension.equalsIgnoreCase(CommonConstants.FILE_TYPE_HTML)) {
                                    filleDetailsOfProductFamilyPage(priceListPath, jsonfilename, rootElementName, adminResourceResolver,
                                            locale, inventoryId);
                                } else if (extension.equalsIgnoreCase(CommonConstants.FILE_TYPE_APPLICATION_XML_VALUE)) {
                                    fillDetailsOfProductFamilyPageForSitemap(inventoryId);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Product Grid Model Admin service catch block: exception", e);
                }
            }

            facetedNavigationHelperV2 = new FacetedNavigationHelperV2();
        }
    }

    protected boolean isProductSubCategoryPage(String pageType){
        return pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
    }
    protected int getStatusBadgeDayLimit(){
        return DEFAULT_STATUS_BADGE_TIME;
    }

    /**
     * To update the secure facet's group label and value label
     *
     * @param updateFacetGroupList
     */
    private void updateSecureFacetValue(List<FacetGroupBean> updateFacetGroupList) {
        if (null != updateFacetGroupList) {
            updateFacetGroupList.stream().filter(faceGroup -> faceGroup.getFacetGroupId().equalsIgnoreCase(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL))
                    .forEach(facetGroup -> {
                        facetGroup.setFacetGroupLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, EndecaConstants.I18N_LABEL_PREFIX + facetGroup.getFacetGroupId(),
                                facetGroup.getFacetGroupLabel()));
                        facetGroup.getFacetValueList().stream().filter(facetValue -> facetValue.getFacetValueLabel().equalsIgnoreCase(EndecaConstants.SECURE_ONLY_FILTER_VALUE))
                                .forEach(facetValue ->
                                        facetValue.setFacetValueLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, EndecaConstants.SECURE_ONLY_FILTER_VALUE,
                                                facetValue.getFacetValueLabel())));
                    });
        }
    }

    /**
     * @param priceListPath
     * @param jsonfilename
     * @param rootElementName
     * @param adminResourceResolver
     * @param locale
     * @param inventoryId
     */
    private void filleDetailsOfProductFamilyPage(String priceListPath, String jsonfilename, String rootElementName,
                                                 ResourceResolver adminResourceResolver, Locale locale, final String inventoryId) {
        LOG.debug("ProductGridModel : This is Entry into filleDetailsOfProductFamilyPage() method");
        productFamilyDetails = productFamilyDetailService.getProductFamilyDetailsBean(currentPage);
        final List<String> activeFacets = (productGridSelectors != null && productGridSelectors.getFacets() != null) ? productGridSelectors
                .getFacets().stream().map(facet -> facet.getFacetID()).collect(Collectors.toList())
                : new ArrayList<>();
        final List<String> inventoryIds = currentPage.adaptTo(PageDecorator.class).getPims().stream()
                .map(pim -> pim.getInventoryId()).filter(Objects::nonNull).collect(Collectors.toList());
        String sortBy = productGridSelectors.getSortyByOption();
        String sortFacet = productGridSelectors.getSortFacet();
        final List<String> returnFacetsFor = new ArrayList<>(
                currentPage.adaptTo(PageDecorator.class).getFacets());
        final List<String> activeFacetsWithProductType = activeFacets != null
                ? new ArrayList<>(activeFacets) : new ArrayList<>();
        if (StringUtils.isEmpty(sortBy)) {
            // no parameter was passed in URL,checking default
            LOG.debug("No sorting parameter was passed. Checking page for default direction then facet");
            sortBy = pgModel.getDefaultSortingDirection();
            if (StringUtils.isEmpty(sortFacet)) {
                sortFacet = pgModel.getDefaultSortingOption();
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Generating Endeca reqest bean for product grid with these pims: %s",
                    inventoryIds.stream().collect(Collectors.joining(", "))));
        }
        final String defaulProductType;
        if (productGridSelectors != null && !StringUtils.isEmpty(productGridSelectors.getExtensionId())) {
            activeFacetsWithProductType.add(productGridSelectors.getExtensionId());
            // If we have a product type id through the getExtensionId() method then we do not want to use the default product type.
            defaulProductType = null;
        } else {
            defaulProductType = productFamilyDetails.getProductType();
        }
        final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
                .getProductFamilyEndecaRequestBean(currentPage, inventoryIds, returnFacetsFor,
                        activeFacetsWithProductType, sortBy, sortFacet, defaulProductType,
                        siteResourceSlingModel.getPageSize(), CommonConstants.DEFAULT_STARTING_RECORD);
        final EndecaServiceRequestBean productTypeRequestBean = endecaRequestService
                .getProductFamilyEndecaRequestBean(currentPage, inventoryIds,
                        Arrays.asList(CommonConstants.PRODUCT_TYPE_FACET_NAME), activeFacets, sortBy, sortFacet, null,
                        siteResourceSlingModel.getPageSize(), CommonConstants.DEFAULT_STARTING_RECORD);
        if (endecaServiceRequestBean.hasValidReturnFacetsFor()) {
            populateBaseSKUPath();
            if ((StringUtils.isNotBlank(inventoryId)) || !(inventoryIds.isEmpty())) {
                if (pgModel != null && pgModel.isTurnOffProductTypes()) {
                    List<FilterBean> listOfFilters = endecaServiceRequestBean.getFilters();
                    FilterBean productFilter = new FilterBean();
                    for (FilterBean filterBean : listOfFilters) {
                        if (filterBean != null && (filterBean.getFilterName() != null && filterBean.getFilterName().equalsIgnoreCase(CommonConstants.PRODUCT_TYPE_FACET_NAME))) {
                            productFilter = filterBean;
                            break;
                        }
                    }
                    listOfFilters.remove(productFilter);
                } else {
                    productTypeListResponseBean = endecaService.getSKUList(productTypeRequestBean, res);
                }
                skuListResponseBean = endecaService.getSKUList(endecaServiceRequestBean, res);
            }
            if (null != productFamilyDetails
                    && StringUtils.isBlank(productFamilyDetails.getPdhRecordPath())) {
                pdhFlag = Boolean.FALSE;
            }
            if (skuListResponseBean != null && skuListResponseBean.getFamilyModuleResponse() != null
                    && skuListResponseBean.getFamilyModuleResponse().getStatus()
                    .equalsIgnoreCase(CommonConstants.ENDECA_STATUS_SUCCESS)
                    && (skuListResponseBean.getFamilyModuleResponse().getTotalCount() != null
                    && skuListResponseBean.getFamilyModuleResponse().getTotalCount() > 0)) {
                int totalResultCnt = skuListResponseBean.getFamilyModuleResponse().getTotalCount();
                allReturnedFacetsBean = skuListResponseBean.getFamilyModuleResponse().getFacets();

                if (allReturnedFacetsBean != null) {
                    setI18NValues();
                    if (pgModel.getTaxonomyAttributes() != null
                            && allReturnedFacetsBean.getFacetGroupList() != null) {
                        if (allReturnedFacetsBean != null) {
                            if (endecaCloudConfig != null) {
                                allReturnedFacetsBean.getFacetGroupList().stream()
                                        .map(facetGroupBean -> new ImmutablePair<>(
                                                endecaCloudConfig.getAdditionalTaxonomyAttributeName(
                                                        facetGroupBean.getFacetGroupId()),
                                                facetGroupBean))
                                        .filter(pair -> pair.getKey().isPresent())
                                        .forEach(pair -> pair.getValue()
                                                .setFacetGroupLabel(CommonUtil.getI18NFromResourceBundle(
                                                        slingRequest, currentPage, pair.getKey().get(),
                                                        pair.getKey().get())));
                            }
                            facetGroupList = orderFacetGroups(allReturnedFacetsBean.getFacetGroupList(),
                                    pgModel.getTaxonomyAttributes(), TAXONOMY_ATTRIBUTE,
                                    (orderedFacetId, facetGroupId) -> orderedFacetId.equals(facetGroupId));
                            ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(endecaServiceRequestBean, slingRequest, res, endecaService, allReturnedFacetsBean);
                            SKUListResponseBean newResponseFromEndeca = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors( facetGroupList, pageType);
                            if (newResponseFromEndeca != null) {
                                skuListResponseBean = newResponseFromEndeca;
							}
                        } else {
                            LOG.warn("Binning set returned null from Endeca");
                        }
                    } else {
                        facetGroupList = allReturnedFacetsBean.getFacetGroupList();
                    }
                } else {
                    facetGroupList = new ArrayList<>();
                }

                initialRadioFacets(adminResourceResolver);
                activeFacetsList = populateActiveFacetsBeanList(productGridSelectors, facetURLBean,
                        facetGroupList);
                updatesFacetGroupWithMoreThanOneList(totalResultCnt);
                facetedHeaderNavigationResultBean
                        .setTotalCount(skuListResponseBean.getFamilyModuleResponse().getTotalCount());
                familyModuleBeanList = skuListResponseBean.getFamilyModuleResponse().getFamilyModule();
                List<FamilyModuleBean> encodedFamilyModuleBeanList = new ArrayList<>();
                for (int i = 0; i < familyModuleBeanList.size(); i++) {
                    FamilyModuleBean tempBean = familyModuleBeanList.get(i);
                    if (tempBean.getCatalogNumber() != null && !(StringUtils.EMPTY).equals(tempBean.getCatalogNumber())) {
                        final String price = CommonUtil.priceItem(priceListPath, adminService,
                                tempBean.getCatalogNumber(), country, rootElementName, jsonfilename);
                        tempBean.setPrice(price);
                        tempBean.setEncodedCatalogNumber(
                                CommonUtil.encodeURLString(tempBean.getCatalogNumber()));
                        for (int k = 0; k < activeFacetsList.size(); k++) {
                            if (activeFacetsList.get(k).getLabel()
                                    .equals(CommonConstants.STATUS_DISCONTINUED)) {
                                tempBean.setStatus(CommonConstants.STATUS_DISCONTINUED);
                            }
                        }
                    }
                    encodedFamilyModuleBeanList.add(tempBean);
                }
                familyModuleBeanList = encodedFamilyModuleBeanList;
                populateProductCardSubcategoryBean();
                if (siteResourceSlingModel != null && facetedHeaderNavigationResultBean
                        .getTotalCount() > siteResourceSlingModel.getPageSize()) {
                    loadMoreFlag = TRUE;
                } else {
                    loadMoreFlag = FALSE;
                }
                populateLoadMoreJson();
            }
            populateDefaultSortOption();
        }
        LOG.debug("ProductGridModel : This is Exit into filleDetailsOfProductFamilyPage() method");
    }


    /**
     * Sets the Secure facet.
     */
    private List<FacetGroupBean> addSecureFacet(List<FacetGroupBean> updatedFacetGroupList) {

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
            secureTypeFacetGroup.setSecure(true);
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
     * Select the checkbox if secure option checked.
     *
     * @param secureFacets
     */
    private void addSecureSelectionFlagIfChecked(FacetValueBean secureFacets) {
        if (productGridSelectors.getFacets() != null) {
            for (FacetBean facetBean : productGridSelectors.getFacets()) {
                if (facetBean.getFacetID().equalsIgnoreCase(SecureConstants.SECURE_FILTER_LABEL)) {
                    // Checks Secure Facet
                    secureFacets.setActiveRadioButton(FORM_FIELD_CHECKED);
                }
            }
        } else {
            // Uncheck Secure Facet
            secureFacets.setActiveRadioButton(StringUtils.EMPTY);
        }
    }

    /**
     * @param inventoryId
     */
    private void fillDetailsOfProductFamilyPageForSitemap(final String inventoryId) {
        LOG.debug("ProductGridModel : This is Entry into fillDetailsOfProductFamilyPageForSitemap() method");
        final List<String> inventoryIds = currentPage.adaptTo(PageDecorator.class).getPims().stream()
                .map(pim -> pim.getInventoryId()).filter(Objects::nonNull).collect(Collectors.toList());
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Generating Endeca reqest bean for product grid with these pims: %s",
                    inventoryIds.stream().collect(Collectors.joining(", "))));
        }

        final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
                .getProductFamilySiteMapEndecaRequestBean(currentPage, inventoryIds,
                        CommonConstants.DEFAULT_STARTING_RECORD);

        populateBaseSKUPath();
        if ((StringUtils.isNotBlank(inventoryId)) || !(inventoryIds.isEmpty())) {
            skuListResponseBean = endecaService.getSKUList(endecaServiceRequestBean, res);
        }
        if (skuListResponseBean != null && skuListResponseBean.getFamilyModuleResponse() != null
                && skuListResponseBean.getFamilyModuleResponse().getStatus()
                .equalsIgnoreCase(CommonConstants.ENDECA_STATUS_SUCCESS)
                && (skuListResponseBean.getFamilyModuleResponse().getTotalCount() != null
                && skuListResponseBean.getFamilyModuleResponse().getTotalCount() > 0)) {

            familyModuleBeanList = skuListResponseBean.getFamilyModuleResponse().getFamilyModule();

        }
        LOG.debug("ProductGridModel : This is Exit into filleDetailsOfProductFamilyPage() method");
    }

    /**
     * @param adminResourceResolver
     * @param locale
     * @param inventoryId
     */
    private void fillDetailsOfProductSubCategoryPageType(ResourceResolver adminResourceResolver, Locale locale,
                                                         final String inventoryId, SlingHttpServletRequest request) {
        LOG.debug("ProductGridModel : This is Entry into fillDetailsOfProductSubCategoryPageType() method");
        final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService.getEndecaRequestBean(
                currentPage, slingRequest.getRequestPathInfo().getSelectors(), inventoryId,
                productGridSelectors, request);
        if (endecaServiceRequestBean.hasValidReturnFacetsFor()) {
            FamilyListResponseBean familyListResponseBean;
            setI18NValues();
            familyListResponseBean = endecaService.getProductFamilyList(endecaServiceRequestBean);
            if (familyListResponseBean != null && familyListResponseBean.getResponse() != null
                    && familyListResponseBean.getResponse().getStatus()
                    .equalsIgnoreCase(CommonConstants.ENDECA_STATUS_SUCCESS)) {
                getDetailsOnSuccessfulResponse(adminResourceResolver, locale, endecaServiceRequestBean,
                        familyListResponseBean);
                facetGroupList = facetGroupList.stream().filter(new SecureFacetFilter(familyListResponseBean)).collect(Collectors.toList());
                updateSecureFacetValue(facetGroupList);
                populateDefaultSortOption();
            }
        }
        LOG.debug("ProductGridModel : This is Exit fillDetailsOfProductSubCategoryPageType() method");
    }

    private void getDetailsOnSuccessfulResponse(ResourceResolver adminResourceResolver, Locale locale,
                                                final EndecaServiceRequestBean endecaServiceRequestBean, FamilyListResponseBean familyListResponseBean) {
        allReturnedFacetsBean = familyListResponseBean.getResponse().getFacets();
        if (allReturnedFacetsBean != null) {
            if (pgModel != null && pgModel.getTagAttributes() != null && pgModel.getTagAttributes().hasChildren()) {
                if (allReturnedFacetsBean.getFacetGroupList() != null && tagManager != null) {
                    facetGroupList = orderFacetGroups(allReturnedFacetsBean.getFacetGroupList(),
                            pgModel.getTagAttributes(), CommonConstants.CQ_TAGS, (orderedTagId, facetGroupId) -> {
                                Tag tag = tagManager.resolve(orderedTagId);
                                return tag != null && tag.adaptTo(EndecaFacetTag.class).getFacetId()
                                        .equals(facetGroupId);
                            });
                    facetGroupList.forEach(facetGroupBean -> {
                        if (pgModel.getSelectedTabularFields() != null && !pgModel.getSelectedTabularFields().isEmpty()) {
                            pgModel.getSelectedTabularFields()
                                    .forEach((facetGrpKey, facetGrpVal) -> sortedFacetValueInGroup(
                                            facetGrpKey, facetGroupBean, facetGrpVal, locale));
                        } else {
                            getSortedList(facetGroupBean.getFacetValueList());
                        }
                    });

                    final List<FacetGroupBean> statusfacetGroupList = populateStatusFacetOption(
                            pageType, locale);
                    final String currentUrl = facetedHeaderNavigationResultBean.getSortByUrl();
                    if (!(currentUrl.contains(EndecaConstants.FACETS_STRING))
                            && null != statusfacetGroupList && !(statusfacetGroupList.isEmpty())) {
                        final String statusFilter = statusfacetGroupList.get(0).getFacetValueList()
                                .get(0).getFacetValueId();
                        endecaServiceRequestBean.getFilters().add(FilterUtil.getFilterBean(EndecaConstants.FILTER_FACETS, statusFilter));
                        familyListResponseBean = endecaService
                                .getProductFamilyList(endecaServiceRequestBean);
                    }
                } else {
                    LOG.warn("Binning set returned null from Endeca");
                }
            } else {
                facetGroupList = allReturnedFacetsBean.getFacetGroupList();
            }
        } else {
            facetGroupList = new ArrayList<>();
        }
        initialRadioFacets(adminResourceResolver);
        activeFacetsList = populateActiveFacetsBeanList(productGridSelectors, facetURLBean,
                facetGroupList);
        updatesFacetGroupWithMoreThanOneList(familyListResponseBean.getResponse().getTotalCount());
        facetedHeaderNavigationResultBean
                .setTotalCount(familyListResponseBean.getResponse().getTotalCount());
        if (familyListResponseBean.getResponse().getProductFamilyBean() != null || !pageType.equals(CommonConstants.PAGE_TYPE_LEARN_PAGE)) {
            productFamilyList = familyListResponseBean.getResponse().getProductFamilyBean();
            populateNewProductBadge(productFamilyList, adminResourceResolver);
        }
        populateProductCardSubcategoryBean();
        if (facetedHeaderNavigationResultBean.getTotalCount() > siteResourceSlingModel
                .getPageSize()) {
            loadMoreFlag = TRUE;
        } else {
            loadMoreFlag = FALSE;
        }
        populateLoadMoreJson();
    }

    private void populateNewProductBadge(List<ProductFamilyBean> productFamilyList, ResourceResolver adminResourceResolver) {
        ValueMap pageProperties = null;
        ProductFamilyBean productFamilyBean = null;
        String publicationDate = "";
        SimpleDateFormat publicationDateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
        int dayLimit=getStatusBadgeDayLimit();
        for (int i = 0; i < productFamilyList.size(); i++) {
            productFamilyBean = productFamilyList.get(i);
            String pagePathLink = CommonUtil.getLinkPagePath(productFamilyBean.getUrl());
            Resource linkPathResource = adminResourceResolver.getResource(pagePathLink);
            if (null != linkPathResource) {
                Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
                if (null != jcrResource) {
                    pageProperties = jcrResource.getValueMap();
                }
                if (pageProperties != null) {
                    publicationDate = CommonUtil.getDateProperty(pageProperties, CommonConstants.PUBLICATION_DATE, publicationDateFormat);

                    if (publicationDate != null && !publicationDate.isEmpty()) {
                        long noOfDays = CommonUtil.getDaysDifference(publicationDate);
                        if (noOfDays >= 0 && noOfDays <= dayLimit)
                            productFamilyBean.setNewBadgeVisible(true);
                        else
                            productFamilyBean.setNewBadgeVisible(false);
                    } else {
                        productFamilyBean.setNewBadgeVisible(false);
                        LOG.debug("populateNewProductBadge: publicationDate is null for Family Page ");
                    }
                }
            }
        }
    }

    /**
     * getSortedList
     *
     * @param facetValueList
     */
    private void getSortedList(List<FacetValueBean> facetValueList) {

        Collections.sort(facetValueList, (FacetValueBean facetValueBeanFirst,
                                          FacetValueBean facetValueBeanSecond) -> {
            int comparisonValue = 0;
            comparisonValue = facetValueBeanFirst.getFacetValueLabel().compareToIgnoreCase(facetValueBeanSecond.getFacetValueLabel());
            return comparisonValue;
        });
    }

    /**
     * populateStatusFacetOption
     *
     * @param pageType
     * @return List<FacetGroupBean>
     */
    private List<FacetGroupBean> populateStatusFacetOption(String pageType, Locale locale) {
        LOG.debug("ProductGridModel : This is Entry into populateStatusFacetOption() method");
        int cnt = 0;
        boolean showStatusFacet = true;
        List<FacetGroupBean> statusFacetList = new ArrayList<>();
        String facetStatus;
        if (isProductSubCategoryPage(pageType)) {
            facetStatus = CommonConstants.CWC_PRODUCT_STATUS;
        } else {
            facetStatus = CommonConstants.PRODUCT_STATUS;
        }

        if (allReturnedFacetsBean != null) {
            final List<FacetGroupBean> facetList = allReturnedFacetsBean.getFacetGroupList();
            if (facetList != null) {
                for (int i = 0; i < facetList.size(); i++) {
                    FacetGroupBean facetGroupBean = facetList.get(i);
                    if (facetGroupBean != null && facetGroupBean.getFacetGroupId().equals(facetStatus)) {
                        final List<FacetValueBean> facetvaluebean = facetGroupBean.getFacetValueList();
                        for (int j = 0; j < facetvaluebean.size(); j++) {
                            if (facetvaluebean != null && facetvaluebean.get(j) != null) {
                                if (facetvaluebean.get(j).getFacetValueLabel().equals(CommonConstants.STATUS_ACTIVE)) {
                                    cnt++;
                                    facetvaluebean.get(j).setActiveRadioButton(CHECKED);
                                    facetvaluebean.get(j).setFacetValueLabel(getLocalizedTagTitle(CommonConstants.ACTIVE_TAG_PATH, locale, facetvaluebean.get(j).getFacetValueLabel()));
                                } else if (facetvaluebean.get(j).getFacetValueLabel()
                                        .equals(CommonConstants.STATUS_DISCONTINUED)) {
                                	facetvaluebean.get(j).setFacetValueLabel(getLocalizedTagTitle(CommonConstants.DISCONTINUED_TAG_PATH, locale, facetvaluebean.get(j).getFacetValueLabel()));
                                    cnt++;
                                }
                            }
                        }
                        if (cnt == 2) {
                            facetGroupBean.setInputType(RADIOS);
                            facetGroupBean.setSingleFacetEnabled(showStatusFacet);
                            statusFacetList.add(0, facetGroupBean);
                        }
                    }
                }
            } else {
                LOG.debug("Facet list null ProductGridModel.populateStatusFacetOption.");
            }
        }
        LOG.debug("ProductGridModel : This is Exit populateStatusFacetOption() method");
        return statusFacetList;
    }

    /**
     * @param facetGroupBean
     * @param oldFacetValueList
     * @param locale
     * @return
     */
    private List<FacetValueBean> sortedFacetValueForCWC(final FacetGroupBean facetGroupBean,
                                                        final String[] oldFacetValueList, Locale locale) {
        LOG.debug("ProductGridModel : This is Entry into sortedFacetValueForCWC() method");
        final List<FacetValueBean> facetValueListSorted = new ArrayList<>();
        final List<FacetValueBean> newFacetValueList = facetGroupBean.getFacetValueList();
        final Locale finalLocal = StringUtils.equals(locale.toString(), CommonConstants.FALLBACK_LOCALE) ? null
                : locale;
        for (final String facetValue : oldFacetValueList) {
            newFacetValueList.forEach(facetValueBean -> {
                final Tag[] tags = getTagFromLocaleSpecificTitle(facetValue, finalLocal);
                String oldFacetTag = null;
                if (Objects.nonNull(tags)) {
                    for (Tag facetTag : tags) {
                        if (null != facetTag && facetTag.getParent().getTagID().equals(EndecaFacetTag.convertId(facetGroupBean.getFacetGroupId().replace(CommonConstants.UNDER_SCORE, CommonConstants.SLASH_STRING)))) {
                            oldFacetTag = facetTag.getTitle(finalLocal);
                        }
                    }
                }
                if (null != oldFacetTag && oldFacetTag.equals(facetValueBean.getFacetValueLabel())) {
                    facetValueListSorted.add(facetValueBean);
                }
            });
        }
        LOG.debug("ProductGridModel : This is Exit sortedFacetValueForCWC() method");
        return facetValueListSorted;
    }

    private Tag[] getTagFromLocaleSpecificTitle(String facetValue, Locale finalLocal) {
        Tag[] tags = tagManager.findTagsByTitle(facetValue, finalLocal);
        if (Objects.isNull(tags) || tags.length == 0) {
            tags = tagManager.findTagsByTitle(facetValue, null);
        }
        return tags;
    }


    private void sortedFacetValueInGroup(final String facetGroupName, final FacetGroupBean facetGroupBean, final String[] facetValueList, Locale locale) {
        LOG.debug("ProductGridModel : This is Entry sortedFacetValueInGroup() method");
        if (facetGroupName != null && (tagManager != null && tagManager.resolve(facetGroupName).getTagID().equals(EndecaFacetTag.convertId(facetGroupBean.getFacetGroupId())))) {
            final List<FacetValueBean> sortedFacetValueForCWC = sortedFacetValueForCWC(facetGroupBean, facetValueList, locale);
            if (!sortedFacetValueForCWC.isEmpty()) {
                facetGroupBean.setFacetValueList(sortedFacetValueForCWC);
            }
            LOG.debug("ProductGridModel : This is Exit sortedFacetValueInGroup() method");
        }
    }

    private void populateDefaultSortOption() {
        LOG.debug("ProductGridModel : This is Entry populateDefaultSortOption() method");
        if (allReturnedFacetsBean != null && allReturnedFacetsBean.getFacetGroupList() != null) {
            Optional<String> selectedDefaultSort = allReturnedFacetsBean.getFacetGroupList().stream()
                    .filter(facetGroup -> facetGroup.getFacetGroupId().equals(pgModel.getDefaultSortingOption()))
                    .map(facetGroup -> facetGroup.getFacetGroupLabel())
                    .findFirst();
            final String defaultSortDirection = StringUtils.equals(pgModel.getDefaultSortingDirection(), DESC) ?
                    CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.FACETS_KEY_HIGH2LOW_SORT, HIGHTOLOW) :
                    CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.FACETS_KEY_LOW2HIGH_SORT, LOWTOHIGH);
            final String alphaAscendingLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_ASCENDING_SORT, ALPHA_A_TO_Z);
            final String alphaDescendingLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_DESCENDING_SORT, ALPHA_Z_TO_A);
            String aZSortFallback = DESC.equals(pgModel.getDefaultSortingDirection()) ? alphaDescendingLabel : alphaAscendingLabel;
            defaultSortOption = selectedDefaultSort.isPresent() ? (selectedDefaultSort.get() + defaultSortDirection) : aZSortFallback;
        } else {
            LOG.warn("Unexpected null binning set during ProductGridModel.populateDefaultSortOption.");
        }
        LOG.debug("ProductGridModel : This is Exit populateDefaultSortOption() method");
    }

    /**
     * prepareSelectedSortByOptionMap
     *
     * @return
     */
    private Map<String, String> prepareSelectedSortByOptionMap() {
        LOG.debug("ProductGridModel : This Entry prepareSelectedSortByOptionMap() method");
        Map<String, String> selectedSortByOptionsMap = new HashMap<>();
        selectedSortByOptionsMap.put(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_ASCENDING_SORT, ALPHA_A_TO_Z), ASC);
        selectedSortByOptionsMap.put(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_DESCENDING_SORT, ALPHA_Z_TO_A), DESC);
        Set<String> selectedSortByOptions = pgModel.getSelectedSortByOptions();
        selectedSortByOptions.forEach(value -> selectedSortByOptionsMap.putAll(populateSelectedSortByOptionsMap(value)));
        LOG.debug("ProductGridModel : This exit prepareSelectedSortByOptionMap() method");
        return selectedSortByOptionsMap;
    }

    /**
     * populateSelectedSortByOptionsMap
     *
     * @param value
     * @return
     */
    private Map<String, String> populateSelectedSortByOptionsMap(String value) {
        LOG.debug("ProductGridModel : This Entry populateSelectedSortByOptionsMap() method");
        Map<String, String> selectedSortByOptionsMap = new HashMap<>();
        Optional<String> label;
        boolean isSubCategoryPage = StringUtils.equals(pgModel.getView(), CommonConstants.SUBCATEGORY);
        if (isSubCategoryPage && tagManager != null) {
            Tag sortTag = tagManager.resolve(value);
            label = Objects.nonNull(sortTag) ? Optional.of(sortTag.getTitle()) : Optional.empty();
        } else {
            label = allReturnedFacetsBean.getFacetGroupList().stream()
                    .filter(facetGroup -> facetGroup.getFacetGroupId().equals(value))
                    .map(facetGroup -> facetGroup.getFacetGroupLabel() != null ? facetGroup.getFacetGroupLabel() : "")
                    .findFirst();
        }
        if (label.isPresent()) {
            String tempValue = isSubCategoryPage ? formatTagValue(value) : value;
            String lowLabel = label.get().concat(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.FACETS_KEY_LOW2HIGH_SORT, LOWTOHIGH));
            final StringBuilder lowValue = new StringBuilder();
            lowValue.append(tempValue).append(CommonConstants.SORT_DIRECTION_DELIMITER).append(ASC);
            selectedSortByOptionsMap.put(lowLabel, lowValue.toString());
            String highLabel = label.get().concat(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.FACETS_KEY_HIGH2LOW_SORT, HIGHTOLOW));
            final StringBuilder highValue = new StringBuilder();
            highValue.append(tempValue).append(CommonConstants.SORT_DIRECTION_DELIMITER).append(DESC);
            selectedSortByOptionsMap.put(highLabel, highValue.toString());
        }
        LOG.debug("ProductGridModel : This Exit populateSelectedSortByOptionsMap() method");
        return selectedSortByOptionsMap;
    }

    /* Formats tagid into sort option value
     * Example: eaton:product-attributes/product-type should be product-attributes_product-type */
    private static String formatTagValue(String value) {
        LOG.debug("ProductGridModel : This Entry formatTagValue() method");
        if (StringUtils.isNotBlank(value)) {
            return StringUtils.substringAfter(StringUtils.replace(value, CommonConstants.SLASH_STRING, CommonConstants.UNDERSCORE), CommonConstants.COLON);
        }
        LOG.debug("ProductGridModel : This Entry formatTagValue() method");
        return value;
    }

    /**
     * updatesFacetGroupWithMoreThanOneList
     *
     * @param totalCnt
     */
    private void updatesFacetGroupWithMoreThanOneList(int totalCnt) {
        LOG.debug("ProductGridModel : This Entry updatesFacetGroupWithMoreThanOneList() method");
        selectedFacetsValueGroupList = new ArrayList<>();
        boolean facetselectedpresent = false;
        List<FacetGroupBean> tempFacetGroupList = new ArrayList<>();
        if (facetGroupList != null) {
            if (activeFacetsList != null && !activeFacetsList.isEmpty()) {
                for (int j = 0; j < facetGroupList.size(); j++) {
                    FacetGroupBean facetGroupBean = facetGroupList.get(j);
                    if (facetGroupBean.getFacetValueList().size() == 1) {
                        FacetValueBean facetValueBean = facetGroupBean.getFacetValueList().get(0);
                        for (int i = 0; i < activeFacetsList.size(); i++) {
                            if (activeFacetsList.get(i).getFacetID().equals(facetValueBean.getFacetValueId())) {
                                facetselectedpresent = true;
                                break;
                            } else {
                                facetselectedpresent = false;
                            }
                        }
                        if (!facetselectedpresent) {
                            if (totalCnt == facetValueBean.getFacetValueDocs()) {
                                tempFacetGroupList.add(facetGroupList.get(j));
                            }
                        } else {
                            selectedFacetsValueGroupList.add(facetGroupList.get(j).getFacetGroupLabel());
                        }
                    }
                }
                facetGroupList.removeAll(tempFacetGroupList);
            } else {
                for (int j = 0; j < facetGroupList.size(); j++) {
                    if (facetGroupList.get(j).getFacetGroupId() != null && !facetGroupList.get(j).getFacetGroupId().equalsIgnoreCase(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL) &&
                            facetGroupList.get(j).getFacetValueList().size() == 1) {
                        FacetGroupBean facetGroupBean = facetGroupList.get(j);
                        FacetValueBean facetValueBean = facetGroupBean.getFacetValueList().get(0);
                        if (totalCnt == facetValueBean.getFacetValueDocs()) {
                            tempFacetGroupList.add(facetGroupList.get(j));
                        }
                    }
                }
                facetGroupList.removeAll(tempFacetGroupList);
            }
        }

        LOG.debug("ProductGridModel : This Exit updatesFacetGroupWithMoreThanOneList() method");
    }

    /**
     * Populate load more json.
     */
    @SuppressWarnings("unchecked")
    private void populateLoadMoreJson() {
        LOG.debug("ProductGridModel : This Entry populateLoadMoreJson() method");
        final String currentLanguage = "currentLanguage";
        final String lanuageMasters = "language-masters";
        final String currentCountry = "currentCountry";
        final String primarySubCategoryTag = "primarySubCategoryTag";
        final String pageSize = "pageSize";
        final String pageTypeString = "pageType";
        final String defaultSortFacet = "defaultSortFacet";
        final String currentLoadMore = "currentLoadMore";
        final String currentSortByOption = "currentSortByOption";
        final String currentSortFacetOption = "currentSortFacetOption";
        final String currentResourcePath = "currentResourcePath";
        final String pimPrimaryImage = "pimPrimaryImage";
        final String inventoryId = "inventoryId";
        final String selectors = "selectors";
        final String requestUri = "requestUri";
        final String skuCardAttrString = "skuCardAttr";
        final String currentPagePath = "currentPagePath";
        final String fallBackImage = "fallBackImage";
        final String londDescCheck = "londDescCheck";
        final String dataAttribute = "dataAttribute";

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
            JSONObject skuCardAttrJson = new JSONObject();
            JSONObject currentPagePathJson = new JSONObject();
            JSONObject fallBackImageJson = new JSONObject();
            JSONObject longDecCheckJson = new JSONObject();


            Locale languageValue = currentPage.getLanguage(false);
            if (languageValue != null && ((languageValue.getLanguage() != null) && (!(StringUtils.EMPTY).equals(languageValue.getLanguage())))) {
                //endecaServiceRequestBean.setLanguage(languageValue.getLanguage());CommentedCode
                if (StringUtils.isNotEmpty(languageValue.getCountry())) {
                    country = languageValue.getCountry();
                    languageJson.put(currentLanguage, CommonUtil.getUpdatedLocaleFromPagePath(currentPage));
                    Page countryPage = currentPage.getAbsoluteParent(2);
                    String countryString = countryPage.getName();
                    if ((!country.equalsIgnoreCase(countryString)) && (!(lanuageMasters).equals(countryString))) {
                        country = countryString.toUpperCase();
                    }
                    countryJson.put(currentCountry, country);
                }
            }
            pageTypeJson.put(pageTypeString, pageType);
            String[] primarySubcategoryTagArray = currentPageProperties.get(CommonConstants.PRIMARY_SUB_CATEGORY_TAG, String[].class);
            String primarySubcategoryTag = StringUtils.EMPTY;
            if (primarySubcategoryTagArray != null) {
                for (int n = 0; n < primarySubcategoryTagArray.length; n++) {
                    if (n == primarySubcategoryTagArray.length - 1) {
                        primarySubcategoryTag = primarySubcategoryTag.concat(primarySubcategoryTagArray[n]);
                    } else {
                        primarySubcategoryTag = primarySubcategoryTag.concat(primarySubcategoryTagArray[n]).concat(CommonConstants.PIPE);
                    }
                }
            }
            primarySubCategoryTagJson.put(primarySubCategoryTag, primarySubcategoryTag);
            pageSizeJson.put(pageSize, siteResourceSlingModel.getPageSize());
            if (isProductSubCategoryPage(pageType)) {
                defaultSortOrderJson.put(DEFAULT_SORT_ORDER, siteResourceSlingModel.getDefaultCategoryWithCardsSortOrder());
                defaultSortFacetJson.put(defaultSortFacet, pgModel.getDefaultSortingOption());
            } else if ((pageType != null) && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
                defaultSortOrderJson.put(DEFAULT_SORT_ORDER, siteResourceSlingModel.getDefaultProductFamilySortOrder());
                defaultSortFacetJson.put(defaultSortFacet, pgModel.getDefaultSortingOption());
            } else if ((pageType != null) && pageType.equals(CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE)) {
                defaultSortOrderJson.put(DEFAULT_SORT_ORDER, siteResourceSlingModel.getDefaultSiteSearchSortOrder());
            }
            currentLoadMoreJson.put(currentLoadMore, productGridSelectors.getLoadMoreOption());
            currentSortByJson.put(currentSortByOption, productGridSelectors.getSortyByOption());
            currentSortFacetJson.put(currentSortFacetOption, productGridSelectors.getSortFacet());
            currentResourcePathJson.put(currentResourcePath, res.getPath());
            if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
                pimPrimaryImageJson.put(pimPrimaryImage, productFamilyDetails.getPrimaryImage());
                inventoryIdJson.put(inventoryId, productFamilyDetails.getInventoryID());
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
            }
            selectorsJson.put(selectors, tempSelectors);
            requestUriJson.put(requestUri, slingRequest.getRequestURI());
            if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {

                JSONObject productTypeJson = getProductTypeJson();
                if (productTypeJson != null) {
                    jsonArray.add(productTypeJson);
                }

                String skuCardAttr = StringUtils.EMPTY;
                // Getting SKUCard Attributes
                if ((productFamilyDetails.getSkuCardAttributeList() != null) && (!productFamilyDetails.getSkuCardAttributeList().isEmpty())) {
                    for (int i = 0; i < productFamilyDetails.getSkuCardAttributeList().size(); i++) {
                        // Break if configured attributes are more than 3
                        if (i == 3) {
                            break;
                        }
                        if (i == 2) {
                            skuCardAttr = skuCardAttr.concat(productFamilyDetails.getSkuCardAttributeList().get(i));
                        } else {
                            skuCardAttr = skuCardAttr.concat(productFamilyDetails.getSkuCardAttributeList().get(i) + CommonConstants.PIPE);
                        }
                    }
                }
                skuCardAttrJson.put(skuCardAttrString, skuCardAttr);
                jsonArray.add(skuCardAttrJson);
            }
            currentPagePathJson.put(currentPagePath, currentPage.getPath());
            fallBackImageJson.put(fallBackImage, siteResourceSlingModel.getSkuFallBackImage());
            if (productFamilyDetails != null && productFamilyDetails.getShowLongDescription() != null && productFamilyDetails.getShowLongDescription()) {
                longDecCheckJson.put(londDescCheck, TRUE);
            } else {
                longDecCheckJson.put(londDescCheck, FALSE);
            }

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

            loadMoreAttrJson.put(dataAttribute, jsonArray);
            loadMoreAttrJsonString = loadMoreAttrJson.toJSONString();
        } catch (Exception e) {
            LOG.error(String.format("Exception in creating parameter for Load More Servlet: %s ", e));
        }
        LOG.debug("ProductGridModel : This Exit populateLoadMoreJson() method");
    }

    /**
     * Populate the product type json
     * @return null - when no product type or "turn off product type is true", JSONObject productTypeJson - when product type exists and "turn off product type" is false
     */
    public JSONObject getProductTypeJson() {
        if (pgModel != null && !pgModel.isTurnOffProductTypes()) {
            String productFamilyProductType = productFamilyDetails.getProductType();
            if (StringUtils.isNotBlank(productFamilyProductType)) {
                JSONObject productTypeJson = new JSONObject();
                productTypeJson.put(CommonConstants.PRODUCTTYPE, productFamilyProductType);
                return productTypeJson;
            }
        }
        return null;
    }

    /**
     * Populate base SKU path.
     */
    private void populateBaseSKUPath() {
        baseSKUPath = CommonUtil.getSKUPagePath(currentPage, skuPageName);
    }

    /**
     * Populate active facets bean list.
     */
    private static List<FacetBean> populateActiveFacetsBeanList(ProductGridSelectors productGridSelectors, FacetURLBean facetURLBean, List<FacetGroupBean> facetGroupList) {
        LOG.debug("ProductGridModel : This Entry populateActiveFacetsBeanList() method");
        ArrayList<FacetBean> activeFacetsList = new ArrayList<>();
        List<FacetBean> activeFacetsReceivedList = productGridSelectors.getFacets();
        if (activeFacetsReceivedList != null) {
            for (FacetBean activeFacetsRecieved : activeFacetsReceivedList) {
                activeFacetsRecieved.setLabel(activeFacetsRecieved.getFacetID());
                activeFacetsRecieved.setFacetDeselectURL(facetURLBean.getContentPath() + activeFacetsRecieved.getFacetDeselectURL() + facetURLBean.getFacetEndURL());
                if (facetGroupList != null) {
                    for (FacetGroupBean facetGroupBean : facetGroupList) {
                        List<FacetValueBean> facetValueList = facetGroupBean.getFacetValueList();
                        for (FacetValueBean facetValueBean : facetValueList) {
                            if (activeFacetsRecieved.getFacetID() != null && facetValueBean.getFacetValueId() != null && activeFacetsRecieved.getFacetID().equals(facetValueBean.getFacetValueId())) {
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
     * Populate sort by url.
     */
    private void populateSortByUrl() {
        String currentUrl = slingRequest.getRequestURI();
        facetedHeaderNavigationResultBean.setSortByUrl(currentUrl);
    }

    /**
     * Initial radio facets.
     *
     * @param adminResourceResolver
     */
    private void initialRadioFacets(ResourceResolver adminResourceResolver) {
        LOG.debug("ProductGridModel : This Entry initialRadioFacets() method");
        final String product_type = "product_type/ValueCQDATA";
        final String productTypes = "productTypes";
        final String prodTypes = "Product Types";
        String initProductType = null;

        if (productTypeListResponseBean != null && productTypeListResponseBean.getFamilyModuleResponse() != null
                && productTypeListResponseBean.getFamilyModuleResponse().getStatus()
                .equalsIgnoreCase(CommonConstants.ENDECA_STATUS_SUCCESS)) {
            List<FacetGroupBean> productTypeFacetGroupList = new ArrayList<>();
            if (productTypeListResponseBean.getFamilyModuleResponse().getFacets() != null) {
                productTypeFacetGroupList = productTypeListResponseBean.getFamilyModuleResponse().getFacets().getFacetGroupList();
            }
            String pdhPath = productFamilyDetails.getPdhRecordPath();
            if (StringUtils.isNotEmpty(pdhPath)) {
                Resource pdhRecordResource = adminResourceResolver.getResource(pdhPath);
                if (pdhRecordResource != null) {
                    initProductType = CommonUtil.getStringProperty(pdhRecordResource.getValueMap(), product_type);
                }
            }
            String groupName = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, productTypes, prodTypes);
            List<FacetValueBean> facetValueBeanList = new ArrayList<>();
            FacetGroupBean facetGroupBean = new FacetGroupBean();
            for (int j = 0; j < productTypeFacetGroupList.size(); j++) {
                FacetGroupBean productTypeFacetGroupBean = productTypeFacetGroupList.get(j);
                if (productTypeFacetGroupBean.getFacetGroupId().equals(CommonConstants.PRODUCT_TYPE_FACET_NAME)) {
                    List<FacetValueBean> facetValueList = productTypeFacetGroupBean.getFacetValueList();
                    for (int k = 0; k < facetValueList.size(); k++) {
                        FacetValueBean facetValueBean = facetValueList.get(k);
                        facetValueBean.setFacetStartURL(facetURLBean.getExtensionIdStartURL());
                        facetValueBean.setFacetEndURL(facetURLBean.getExtensionIdEndURL());
                        if ((productGridSelectors != null) && (productGridSelectors.getExtensionId() != null)) {
                            if (productGridSelectors.getExtensionId().equals(facetValueBean.getFacetValueId())) {
                                facetValueBean.setActiveRadioButton(CHECKED);
                            }
                        } else if ((initProductType != null) && (initProductType.equals(facetValueBean.getFacetValueLabel()))) {
                            facetValueBean.setActiveRadioButton(CHECKED);
                        }
                        facetValueBeanList.add(facetValueBean);
                    }
                    if ((facetValueBeanList != null) && (!facetValueBeanList.isEmpty())) {
                        facetGroupBean.setFacetValueList(facetValueBeanList);
                        facetGroupBean.setInputType(RADIOS);
                        facetGroupBean.setFacetGroupLabel(groupName);
                        facetGroupList.add(0, facetGroupBean);
                    }
                }
            }
        }
        LOG.debug("ProductGridModel : This Entry initialRadioFacets() method");
    }

    /**
     * Populate product card subcategory bean.
     */
    private void populateProductCardSubcategoryBean() {
        LOG.debug("ProductGridModel : This Entry populateProductCardSubcategoryBean() method");
        final String sampleSelector = ".sampleSelector";
        String currentUrl = slingRequest.getRequestURI();
        productCardSubcategoryBean.setAjaxRequestUrl(currentUrl + sampleSelector);
        productCardSubcategoryBean.setAjaxRequestNextPage(StringUtils.EMPTY);
        LOG.debug("ProductGridModel : This Exit populateProductCardSubcategoryBean() method");
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
        final String goTo = "Go To";
        final String loadMore = "Load More";
        final String specification = "Specification";
        final String modelCode = "Model Code";
        final String resourceStr = "Resource";
        final String compareStr = "Compare";
        final String compareProducts = "Compare Products";
        final String comparisionTable = "Comparison Table";
        final String clearSelection = "Clear Selection";
        final String maxErrorHeading = "Maximum reached";
        final String maxErrorDescription = "Four is the maximum number of items in the comparison tool.";
        final String maxErrorOk = "OK";
        final String minErrorHeading = "Two items are required";
        final String minErrorDescription = "A minimum of two items are required to use the comparison tool.";
        final String minErrorCancel = "OK";
        final String backToSelection = "< Back";
        final String highlightDifferences = "Highlight differences";
        final String showOnlyDifferences = "Show only differences";
        final String hideBlankFeatures = "Hide blank features";

        try {
            List<SortByOptionValueBean> sortList = setSortByOptionList();
            facetedHeaderNavigationResultBean.setSortList(sortList);
            facetedHeaderNavigationResultBean.setResults(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_RESULTS, results));
            facetedHeaderNavigationResultBean.setSort(getSelectedSortOption(sortList));
            facetedHeaderNavigationResultBean.setRelevance(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_RELEVENCE, RELEVANCESTR));
            facetedHeaderNavigationResultBean.setAssendingSort(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_ASCENDING_SORT, ALPHA_A_TO_Z));
            facetedHeaderNavigationResultBean.setDescendingSort(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_DESCENDING_SORT, ALPHA_Z_TO_A));
            facetedHeaderNavigationResultBean.setClearFilters(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_CLEAR_FILTERS, clearFilter));
            facetedHeaderNavigationResultBean.setNarrowResults(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_NARROW_RESULTS, narrowResults));
            facetedHeaderNavigationResultBean.setViewMore(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_VIEW_MORE, viewMore));
            facetedHeaderNavigationResultBean.setViewLess(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, VGCommonConstants.VIEW_LESS, viewLess));
            facetedHeaderNavigationResultBean.setNewKeyword(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_NEW, newString));
            facetedHeaderNavigationResultBean.setBestSeller(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_BESTSELLER, bestSeller));
            facetedHeaderNavigationResultBean.setRemoveFilters(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_REMOVE_FILTERS, removeFilters));
            facetedHeaderNavigationResultBean.setApply(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_APPLY, apply));
            facetedHeaderNavigationResultBean.setFilters(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_FILTERS, filters));
            productCardSubcategoryBean.setIsPriceDisclaimerEnabled(!pgModel.getHidePriceDisc());
            productCardSubcategoryBean.setPriceDisclaimer(CommonUtil.getI18NFromResourceBundle(slingRequest,
                    currentPage, CommonConstants.PRICE_DISCLAIMER_I18N_KEY, CommonConstants.PRICE_DISCLAIMER_I18N_KEY));
            productCardSubcategoryBean.setGoTo(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CARD_KEY_GO_TO, goTo));
            productCardSubcategoryBean.setLoadMore(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CARD_KEY_LOAD_MORE, loadMore));
            productCardSubcategoryBean.setSpecificationTabTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, DIGITALTOOL_SPECIFICATION_TAB, specification));
            productCardSubcategoryBean.setModelCodeTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, MODEL_CODE, modelCode));
            productCardSubcategoryBean.setResourceTabTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, DIGITALTOOL_RESOURCE_TAB, resourceStr));
            productCardSubcategoryBean.setCompareProducts(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, COMPARE_PRODUCTS, compareProducts));
            productCardSubcategoryBean.setComparisionTable(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, COMPARISION_TABLE, comparisionTable));
            productCardSubcategoryBean.setClearSelection(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CLEAR_SELECTION, clearSelection));
            productCardSubcategoryBean.setCompareTabTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, DIGITALTOOL_COMPARE_TAB, compareStr));
            productCardSubcategoryBean.setCompareButton(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, DIGITALTOOL_COMPARE_BUTTON, compareStr));
            productCardSubcategoryBean.setMaxErrorHeading(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, MAX_ERROR_HEADING, maxErrorHeading));
            productCardSubcategoryBean.setMaxErrorDescription(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, MAX_ERROR_DESCRIPTION, maxErrorDescription));
            productCardSubcategoryBean.setMaxErrorOk(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, DIGITALTOOL_STR_OK, maxErrorOk));
            productCardSubcategoryBean.setMinErrorHeading(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, MIN_ERROR_HEADING, minErrorHeading));
            productCardSubcategoryBean.setMinErrorDescription(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, MIN_ERROR_DESCRIPTION, minErrorDescription));
            productCardSubcategoryBean.setMinErrorCancel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, DIGITALTOOL_CANCEL, minErrorCancel));
            productCardSubcategoryBean.setBackToSelection(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, BACK_TO_SELECTION, backToSelection));
            productCardSubcategoryBean.setHighlightDifferences(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, HIGHLIGHT_DIFF, highlightDifferences));
            productCardSubcategoryBean.setShowOnlyDifferences(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, SHOW_DIFFRENCES, showOnlyDifferences));
            productCardSubcategoryBean.setHideBlankFeatures(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, HIDE_BLANK_FEATURES, hideBlankFeatures));

        } catch (Exception e) {
            LOG.error(String.format("Exception in getting I18n values: %s ", e));
        }
        LOG.debug("ProductGridModel : This is Exit from setI18NValues() method");
    }

    private String getSelectedSortOption(List<SortByOptionValueBean> sortList) {
        LOG.debug("ProductGridModel : This is Entry into getSelectedSortOption() method");
        final String sortBy = "Sort By";
        String currentSortOption = productGridSelectors.getSortyByOption();
        for (SortByOptionValueBean sortOption : sortList) {
            if (StringUtils.equalsIgnoreCase(currentSortOption, sortOption.getValue())) {
                return sortOption.getLabel();
            }
        }
        LOG.debug("ProductGridModel : This is Exit into getSelectedSortOption() method");
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_SORT, sortBy);
    }

    /**
     * Sets the sort by option list.
     *
     * @return the list
     */
    private List<SortByOptionValueBean> setSortByOptionList() {
        LOG.debug("ProductGridModel : This is Entry into setSortByOptionList() method");
        final String rel = "rel";
        Map<String, String> selectedSortByOptionsMap = prepareSelectedSortByOptionMap();
        List<SortByOptionValueBean> sortList = new ArrayList<>();
        if (selectedSortByOptionsMap.isEmpty()) {
            SortByOptionValueBean sortByOptionValueBean1 = new SortByOptionValueBean();
            SortByOptionValueBean sortByOptionValueBean2 = new SortByOptionValueBean();
            SortByOptionValueBean sortByOptionValueBean3 = new SortByOptionValueBean();
            sortByOptionValueBean1.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_RELEVENCE,
                    RELEVANCESTR));
            sortByOptionValueBean2.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_ASCENDING_SORT,
                    ALPHA_A_TO_Z));
            sortByOptionValueBean3.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_DESCENDING_SORT,
                    ALPHA_Z_TO_A));
            sortByOptionValueBean1.setValue(rel);
            sortByOptionValueBean2.setValue(ASC);
            sortByOptionValueBean3.setValue(DESC);
            sortList.add(sortByOptionValueBean1);
            sortList.add(sortByOptionValueBean2);
            sortList.add(sortByOptionValueBean3);
        } else {
            selectedSortByOptionsMap.entrySet().forEach(entry -> {
                SortByOptionValueBean bean = new SortByOptionValueBean();
                bean.setLabel(entry.getKey());
                bean.setValue(entry.getValue());
                sortList.add(bean);
            });
        }
        sortList.sort(Comparator.comparing(SortByOptionValueBean::getLabel));
        LOG.debug("ProductGridModel : This is Exit setSortByOptionList() method");
        return sortList;
    }

    /**
     * @param facetList               The facets returned by Endeca. Only these facets will be returned as FacetGroupBean's in the returned list.
     * @param facetListResourceParent The parent resource of the facet list resources.
     * @param sortAttribute           The attribute of the facetListResourceParent child resources to sort on.
     * @param ordering                The ordering of the facetList. The provided sortAttribute will be given to this BiPredicate as the first parameter.
     * @return A list of FacetGroupBean's created from the child resources of facetListResourceParent ordered based on the provided
     * ordering, followed by the configured global attributes ordered in the same way.
     */
    private List<FacetGroupBean> orderFacetGroups(final List<FacetGroupBean> facetList,
                                                  final Resource facetListResourceParent, final String sortAttribute,
                                                  final BiPredicate<String, String> ordering) {
        LOG.debug("ProductGridModel : This is Entry orderFacetGroups() method");
        final List<FacetGroupBean> sortedFacetList = new ArrayList<>();

        facetList.stream().filter(faceGroup -> faceGroup.getFacetGroupId().equalsIgnoreCase(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL))
                .forEach(facetGroup -> {
                    sortedFacetList.add(facetGroup);
                });
        if (null != facetListResourceParent && null != currentPage) {
            sortedFacetList.addAll(filterFacetGroupList(facetList, facetListResourceParent, sortAttribute, ordering));
        }
        if (null != pgModel && null != pgModel.getGlobalAttributes() && null != currentPage) {
            sortedFacetList
                    .addAll(filterFacetGroupList(facetList, pgModel.getGlobalAttributes(), GLOBAL_ATTRIBUTE, ordering));
        }
        Collections.sort(sortedFacetList, Comparator.comparing(FacetGroupBean::getSortOrder));
        LOG.debug("ProductGridModel : This is Exit orderFacetGroups() method");
        return sortedFacetList;
    }

    /**
     * ;
     *
     * @param facetList               The facets returned by Endeca. Only these facets will be returned as FacetGroupBean's in the returned list.
     * @param facetListResourceParent The parent resource of the facet list resources.
     * @param sortAttribute           The attribute of the facetListResourceParent child resources to sort on.
     * @param ordering                The ordering of the facetList. The provided sortAttribute will be given to the BiPredicate as the first parameter.
     * @return A list of FacetGroupBean's created from the child resource of the facetListResourceParent and ordered based upon the provided ordering.
     */
    private List<FacetGroupBean> filterFacetGroupList(final List<FacetGroupBean> facetList,
                                                      final Resource facetListResourceParent, final String sortAttribute,
                                                      final BiPredicate<String, String> ordering) {
        LOG.debug("ProductGridModel : This is Entry filterFacetGroupList() method");
        final List<FacetGroupBean> sortedFacetList = new ArrayList<>();

        // Because we are doing an ordering here the second parameter to this
        // method is
        // false so that it does not perform the action in parallel.
        StreamSupport.stream(facetListResourceParent.getChildren().spliterator(), false).map(Resource::getValueMap)
                .filter(valueMap -> valueMap.containsKey(sortAttribute)).forEach(valueMap -> {
                    // For each sort resource add the corresponding facet if it
                    // is present in the
                    // facetList. Add them based upon the provided ordering.
                    final String orderedAttr = valueMap.get(sortAttribute, StringUtils.EMPTY);
                    final Boolean showAsGrid = valueMap.get(FACET_GROUP_SHOW_AS_GRID, false);
                    final Boolean facetSearchEnabled = valueMap.get(FACET_GROUP_FACET_SEARCH_ENABLED, false);
                    final Boolean singleFacetEnabled = currentPage.getProperties().containsKey(
                            CommonConstants.PAGE_PIM_PATHS) ? Boolean.TRUE : valueMap.get(SINGLE_FACET_ENABLED, false);
                    final Integer sortOrder = valueMap.get(FACET_GROUP_FACET_SORT, 0);
                    facetList.stream().filter(facet -> ordering.test(orderedAttr, facet.getFacetGroupId()))
                            .forEach(facet -> {
                                facet.setFacetGroupLabel(slingRequest, currentPage, pdhConfigModel);
                                facet.setGridFacet(showAsGrid);
                                facet.setFacetSearchEnabled(facetSearchEnabled);
                                facet.setSingleFacetEnabled(singleFacetEnabled);
                                facet.setSortOrder(sortOrder);
                                sortedFacetList.add(facet);
                            });
                });
        LOG.debug("ProductGridModel : This is Exit filterFacetGroupList() method");
        return sortedFacetList;
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
     * Gets the site resource sling model.
     *
     * @return the site resource sling model
     */
    public SiteResourceSlingModel getSiteResourceSlingModel() {
        return siteResourceSlingModel;
    }

    /**
     * Gets the product family list.
     *
     * @return the product family list
     */
    public List<ProductFamilyBean> getProductFamilyList() {
        return productFamilyList;
    }

    /**
     * Gets the family module bean list.
     *
     * @return the family module bean list
     */
    public List<FamilyModuleBean> getFamilyModuleBeanList() {
        return familyModuleBeanList;
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
     * Gets the product card subcategory bean.
     *
     * @return the product card subcategory bean
     */
    public ProductCardSubcategoryBean getProductCardSubcategoryBean() {
        return productCardSubcategoryBean;
    }

    public List<String> getSelectedFacetsValueGroupList() {
        return selectedFacetsValueGroupList;
    }

    /**
     * Gets the base SKU path.
     *
     * @return the base SKU path
     */
    public String getBaseSKUPath() {
        return baseSKUPath;
    }

    /**
     * Gets the product family details.
     *
     * @return the product family details
     */
    public ProductFamilyDetails getProductFamilyDetails() {
        return productFamilyDetails;
    }

    /**
     * Gets the load more attr json string.
     *
     * @return the load more attr json string
     */
    public String getLoadMoreAttrJsonString() {
        return loadMoreAttrJsonString;
    }

    public String getZeroFacetResultsMessage() {
        return zeroFacetResultsMessage;
    }

    /**
     * Gets the analytics event DTM.
     *
     * @return the analytics event DTM
     */
    public String getAnalyticsEventDTM() {
        return CommonConstants.DTM_MODEL_FACET;
    }

    /**
     * @return the subCategoryHideShow
     */
    public String getHideProductDescription() {
        return hideProductDescription;
    }

    public String getIsSubCategoryView() {
        return isSubCategoryView;
    }

    public String getSubCategoryHideShow() {
        return subCategoryHideShow;
    }

    public String getLoadMoreFlag() {
        return loadMoreFlag;
    }

    public String getDefaultSortingOption() {
        return defaultSortOption;
    }

    public boolean getPdhFlag() {
        return pdhFlag;
    }

    public List<FacetGroupBean> getFacetGroupList() {
        return facetGroupList;
    }

    public FacetURLBean getFacetURLBean() {
        return facetURLBean;
    }

    public List<FacetBean> getActiveFacetsList() {
        return activeFacetsList;
    }

    public String getHideGlobalFacetSearch() {
        return hideGlobalFacetSearch;
    }

    public String getFacetGroupListJson() {
        return facetedNavigationHelperV2.getFacetGroupListJson(currentPage, facetGroupList, activeFacetsList, facetURLBean, currentSelectedTab, isAuthenticated);
    }

    public String getFacetGroupOrderingJson() {
        return facetedNavigationHelperV2.getFacetGroupOrderingJson(currentPage, facetGroupList, activeFacetsList, facetURLBean, currentSelectedTab, isAuthenticated);
    }

    public String getActiveFacetsListJson() {
        return facetedNavigationHelperV2.getActiveFacetsListJson(activeFacetsList);
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

    protected String getLocalizedTagTitle(String tagPath, Locale locale, String defaultValue) {
    	if (tagManager != null) {
    		Tag tag = tagManager.resolve(tagPath);
        	if (tag != null) {
        		return tag.getTitle(locale);
        	}
    	}

    	return defaultValue;
    }
}
