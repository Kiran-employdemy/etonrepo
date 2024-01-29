package com.eaton.platform.core.services.impl;

import com.adobe.acs.commons.util.ModeUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.SiteMapConstants;
import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.core.models.*;
import com.eaton.platform.core.models.productcompatibilitytool.Filter;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.FacetURLBeanService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.FilterUtil;
import com.eaton.platform.core.util.JcrUtils;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.factories.SecureFilterBeanFactory;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.query.SlingQuery;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.eaton.platform.integration.endeca.constants.EndecaConstants.*;
import static org.apache.sling.query.SlingQuery.$;

@SuppressWarnings("deprecation")
@Component(service = EndecaRequestService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "EndecaRequestServiceImpl",
                AEMConstants.PROCESS_LABEL + "EndecaRequestServiceImpl"
        })
public class EndecaRequestServiceImpl implements EndecaRequestService {
    private static final Logger LOG = LoggerFactory.getLogger(EndecaRequestServiceImpl.class);
    private static final String SEPERATOR = "||";
    private static final String NAME = "name";
    private static final String ORDER = "order";
    private static final String COUNTRY_CONSTANT = "Country";
    private static final String MAXRELATIONSSKU = "MaxRelationSKU";
    private static final String ADVANCED_SEARCH_CONTENT_TYPE_FACET_CONSTANT = "advanced_search content_type_facet";
    private static final String SUB_CATEGORY = "sub-category";
    private static final String PRODUCT_FAMILY = "product-family";
    private static final String PRODUCT_STATUS = "Status";
    private static final String FILTERLIST_NAME = "Name: ";
    private static final String FILTERLIST_VALUES = ", Values: ";
    private static final String STARTING_RECORD = "0";
    private static final String NO_OF_RECORDS = "30";

    @Reference
    protected ProductFamilyDetailService productFamilyDetailService;

    @Reference
    private EndecaConfig endecaConfig;

    @Reference
    private AuthorizationService authorizationService;

    @Reference
    private AuthenticationServiceConfiguration authenticationServiceConfig;

    @Reference
    private AuthenticationService authenticationService;

    @Reference
    private EatonSiteConfigService eatonSiteConfigService;

    @Reference
    private AdminService adminService;

    @Reference
    private FacetURLBeanService facetURLBeanService;

    public EndecaServiceRequestBean getEndecaRequestBean(final Page page,
                                                         final String[] selectors,
                                                         final String inventoryId) {
        return getEndecaRequestBean(page, selectors, inventoryId, null, null, null);
    }

    public EndecaServiceRequestBean getEndecaRequestBean(final Page page,
                                                         final String[] selectors,
                                                         final String inventoryId,
                                                         final ProductGridSelectors productGridSelectors) {
        return getEndecaRequestBean(page, selectors, inventoryId, productGridSelectors, null, null);
    }

    public EndecaServiceRequestBean getEndecaRequestBean(final Page page,
                                                         final String[] selectors,
                                                         final String inventoryId,
                                                         final ProductGridSelectors productGridSelectors,
                                                         SlingHttpServletRequest request) {
        final EndecaServiceRequestBean endecaServiceRequestBeanTabsCount = new EndecaServiceRequestBean();
        return getEndecaRequestBean(page, selectors, inventoryId, productGridSelectors, endecaServiceRequestBeanTabsCount, request);
    }

    public EndecaServiceRequestBean getEndecaRequestBean(final Page page,
                                                         final String[] selectors,
                                                         final String inventoryId,
                                                         final ProductGridSelectors productGridSelectors,
                                                         final EndecaServiceRequestBean endecaServiceRequestBeanTabsCount, SlingHttpServletRequest request) {
        LOG.debug("Start of getEndecaRequestBean");

        EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
        if (null != endecaConfig && (null != page && null != adminService)) {
            try (ResourceResolver adminServiceReadService = adminService.getReadService()) {
                final ValueMap valueMap = page.getContentResource().getValueMap();
                final String pageType = valueMap.get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
                final String country = CommonUtil.getCountryFromPagePath(page);
                final String lang = CommonUtil.getUpdatedLocaleFromPagePath(page);
                final EndecaConfigServiceBean configServiceBean = endecaConfig.getConfigServiceBean();
                endecaServiceRequestBean.setSearchApplicationKey(configServiceBean.getEspAppKey());
                endecaServiceRequestBean.setFunction(SiteMapConstants.SEARCH);
                endecaServiceRequestBean.setLanguage(lang);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Endeca pageType: %s", pageType));
                }
                if (PRODUCT_FAMILY.equalsIgnoreCase(pageType)) {
                    endecaServiceRequestBean.setSearchApplication(configServiceBean.getProductfamilyAppName());
                } else {
                    if (SUB_CATEGORY.equalsIgnoreCase(pageType) || CommonConstants.PAGE_TYPE_LEARN_PAGE.equalsIgnoreCase(pageType)) {
                        endecaServiceRequestBean.setSearchApplication(configServiceBean.getSubcategoryAppName());
                    }
                }

                final String[] subCatergoryTags = valueMap.get(CommonConstants.PRIMARY_SUB_CATEGORY_TAG, new String[]{});
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Endeca subCatergoryTags from page valuemap: %s",
                            Arrays.toString(subCatergoryTags)));
                }

                endecaServiceRequestBean = setSearchTerm(subCatergoryTags, endecaServiceRequestBean,
                        adminServiceReadService);
                if (CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE.equals(pageType)) {
                    LOG.debug("setFacetFilters : PAGE_TYPE_SITE_SEARCH_PAGE");
                    endecaServiceRequestBean = setFacetFilters(endecaServiceRequestBean, page,
                            country, null, null, subCatergoryTags, adminServiceReadService, pageType, selectors, productGridSelectors, configServiceBean, request);
                } else {
                    endecaServiceRequestBean = setFilters(endecaServiceRequestBean, page,
                            country, inventoryId, null, null, null, subCatergoryTags, adminServiceReadService, pageType);
                }
                if (CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE.equals(pageType) || CommonConstants.PAGE_TYPE_GLOBAL_PRODUCT_SKU_PAGE.equals(pageType)) {
                    populateSKUPageEndecaRequestBean(selectors, endecaServiceRequestBean, country, configServiceBean, page);
                }
                if (CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE.equals(pageType) && null != endecaServiceRequestBeanTabsCount) {
                    endecaServiceRequestBeanTabsCount.setSearchApplicationKey(configServiceBean.getEspAppKey());
                    endecaServiceRequestBeanTabsCount.setFunction(SiteMapConstants.SEARCH);
                    endecaServiceRequestBeanTabsCount.setLanguage(lang);
                    populateSiteSearchPageEndecaRequestBean(endecaServiceRequestBean, endecaServiceRequestBeanTabsCount, configServiceBean, selectors, page, request);

                }
                if (CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE.equals(pageType)) {
                    setPageSize(endecaServiceRequestBean, page);
                    endecaServiceRequestBean = getProductFamilyEndecaRequestBean(page,
                            null, null, null);
                }
                if (!CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE.equals(pageType)) {
                    addFacetFilter(productGridSelectors, endecaServiceRequestBean);
                }
                if (selectors.length == 0 && CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE.equalsIgnoreCase(pageType)) {
                    addSortFacet(page, endecaServiceRequestBean);
                }
                if (CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE.equalsIgnoreCase(pageType) || CommonConstants.PAGE_TYPE_LEARN_PAGE.equalsIgnoreCase(pageType)) {
                    boolean secureOnly = isSecureFacet(productGridSelectors);
                    if (authorizationService != null && request != null) {
                        SecureFilterBeanFactory secureFilterBeanFactory = new SecureFilterBeanFactory();
                        SecureModule module = getGridModule();
                        if (CommonConstants.PAGE_TYPE_LEARN_PAGE.equalsIgnoreCase(pageType)){
                            endecaServiceRequestBean.setSearchApplication(configServiceBean.getSubcategoryAppName());
                        }
                        else{
                            endecaServiceRequestBean.setSearchApplication(module.getAppId());
                        }
                        AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(request);
                        endecaServiceRequestBean.getFilters()
                                .addAll(secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.PRODUCTGRID));
                        if (null != endecaServiceRequestBeanTabsCount) {
                            endecaServiceRequestBeanTabsCount.setSearchApplication(SecureModule.PRODUCTGRID.getAppId());
                            endecaServiceRequestBeanTabsCount.getFilters()
                                    .addAll(secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.PRODUCTGRID));
                        }
                    }
                }
                JcrUtils.closeResourceResolver(adminServiceReadService);
            }
        }

        LOG.debug("End of getEndecaRequestBean");

        return endecaServiceRequestBean;
    }

    private static SecureModule getGridModule() {
        if (ModeUtil.isAuthor()) {
            return SecureModule.PRODUCTGRID_FOR_AUTHOR;
        }
        return SecureModule.PRODUCTGRID;
    }

    private static boolean isSecureFacet(ProductGridSelectors productGridSelectors) {
        if ((productGridSelectors != null) && (productGridSelectors.getFacets() != null) && (!productGridSelectors.getFacets().isEmpty())) {
            List<FacetBean> facetList = productGridSelectors.getFacets();
            for (int i = 0; i < facetList.size(); i++) {
                FacetBean facetBean = facetList.get(i);
                if (facetBean.getFacetID().equalsIgnoreCase(SecureConstants.SECURE_FILTER_LABEL)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<String> removeSecureFacet(ProductGridSelectors productGridSelectors) {
        List<String> filterValues = new ArrayList<>();
        if ((productGridSelectors != null) && (productGridSelectors.getFacets() != null) && (!productGridSelectors.getFacets().isEmpty())) {
            List<FacetBean> facetList = productGridSelectors.getFacets();
            for (int i = 0; i < facetList.size(); i++) {
                // Ignore Adding Secure Filter as Facet to Endeca
                if (facetList.get(i) != null && !facetList.get(i).getFacetID().equalsIgnoreCase(SecureConstants.SECURE_FILTER_LABEL)) {
                    FacetBean facetBean = facetList.get(i);
                    filterValues.add(facetBean.getFacetID());
                }
            }
        }
        return filterValues;
    }

    private EndecaServiceRequestBean setFacetFilters(
            final EndecaServiceRequestBean endecaServiceRequestBean,
            final Page page, final String country, final JSONArray facetArray,
            JSONArray activeFacets, final String[] subCatergoryTags, final ResourceResolver resourceResolver, String pageType,
            String[] selectors, ProductGridSelectors productGridSelectors, final EndecaConfigServiceBean serviceRequestBean, SlingHttpServletRequest request) {
        LOG.debug("EndecaRequestServiceImpl : This is Entry into setFACETFilters() method");
        endecaServiceRequestBean.setStartingRecordNumber(SiteMapConstants.RECORD_START);
        Optional<SiteResourceSlingModel> siteConfigResource = eatonSiteConfigService.getSiteConfig(page);
        if (siteConfigResource.isPresent() && siteConfigResource.get().getPageSize() != 0 && !MetaTagsModel.isIsSubmittalBuilder()) {
            endecaServiceRequestBean.setNumberOfRecordsToReturn(StringUtils.EMPTY + siteConfigResource.get().getPageSize());
        } else {
            endecaServiceRequestBean.setNumberOfRecordsToReturn(SiteMapConstants.TOTAL_RECORD);
        }
        List<FilterBean> filters = new ArrayList<>();
        final FilterBean countryBean = getCountryFilterBean(country);
        filters.add(countryBean);
        filters.add(FilterUtil.getFilterBean(CommonConstants.DISCARD_APPLICATION_ID, CommonConstants.GREEN_SWITCHING));
        if (null != facetArray) {
            filters = getSubmitBuilderReturnFacetsFilterBean(facetArray, filters);
            endecaServiceRequestBean.setNumberOfRecordsToReturn("10");
        } else {
            filters = getReturnFacetsFilterBean(page, filters);
        }

        FilterBean filterBean = new FilterBean();
        filterBean.setFilterName(EndecaConstants.FACETS_STRING_WITH_F);
        List<String> filterValues = new ArrayList<>();
        if ((productGridSelectors != null) && (productGridSelectors.getFacets() != null) && (!productGridSelectors.getFacets().isEmpty())) {
            List<FacetBean> facetList = productGridSelectors.getFacets();
            for (int i = 0; i < facetList.size(); i++) {
                // Ignore Adding Secure Filter as Facet to Endeca
                if (facetList.get(i) != null && !facetList.get(i).getFacetID().equalsIgnoreCase(SecureConstants.SECURE_FILTER_LABEL)) {
                    FacetBean facetBean = facetList.get(i);
                    filterValues.add(facetBean.getFacetID());
                }
            }
        }
        String currentTab = StringUtils.EMPTY;
        if ((productGridSelectors != null) && (productGridSelectors.getSelectedTab() != null)) {
            currentTab = productGridSelectors.getSelectedTab();
            filterValues.add(currentTab);
        }
        if (filterValues.isEmpty()) {
            filterValues.add(StringUtils.EMPTY);
        }
        filterBean.setFilterValues(filterValues);
        filters.add(filterBean);

        filterBean = new FilterBean();
        filterValues = new ArrayList<>();
        endecaServiceRequestBean.setFilters(filters);
        String sortByOption = StringUtils.EMPTY;
        List<String> facetGroups = new ArrayList<>();
        SearchResultsModel searchResultModel = null;

        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                if (null != endecaConfig) {
                    endecaServiceRequestBean.setSearchApplication(serviceRequestBean.getSitesearchAppName());
                }
                if (siteConfigResource.isPresent()) {
                    SiteResourceSlingModel siteConfiguration = siteConfigResource.get();
                    searchResultModel = getSearchResultModel(page.getContentResource());
                    if (siteConfiguration.getPageSize() != 0) {
                        Integer pageSize = (Integer) siteConfiguration.getPageSize();
                        String contentResourcePath = StringUtils.EMPTY;
                        Resource contentResource = page.getContentResource();
                        if (contentResource != null) {
                            contentResourcePath = contentResource.getPath();
                            pageType = contentResource.getValueMap().get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
                        }
                        FacetURLBeanServiceResponse facetURLBeanResponse = facetURLBeanService.getFacetURLBeanResponse(selectors, pageSize, pageType, contentResourcePath);
                        productGridSelectors = facetURLBeanResponse.getProductGridSelectors();
                        if ((null != productGridSelectors) && (null != productGridSelectors.getSearchTerm())) {
                            endecaServiceRequestBean.setSearchTerms(productGridSelectors.getSearchTerm());
                        }
                        endecaServiceRequestBean.setNumberOfRecordsToReturn(pageSize.toString());
                    }
                    if ((null != searchResultModel) && (StringUtils.equals(CommonConstants.SEARCH_ADVANCED_VIEW, searchResultModel.getView()))) {
                        sortByOption = searchResultModel.getDefaultSort();
                    }
                    if (siteConfiguration.getDefaultSiteSearchSortOrder() != null
                            && !siteConfiguration.getDefaultSiteSearchSortOrder().equals(StringUtils.EMPTY)
                            && null != searchResultModel
                            && StringUtils.equals(CommonConstants.SEARCH_DEFAULT_VIEW, searchResultModel.getView())) {
                        sortByOption = siteConfiguration.getDefaultSiteSearchSortOrder().trim();
                    }
                    if ((null != searchResultModel) && (StringUtils.equals(CommonConstants.SEARCH_ADVANCED_VIEW, searchResultModel.getView()))) {
                        // if the view in Search Results is advanced, get the facet tags from Facet Settings tab dialog.
                        final String SEARCH_RESULT_PATH = CommonConstants.SEARCH_RESULT_PATH;
                        getAuthoredFacetTags(adminResourceResolver, filterValues, SEARCH_RESULT_PATH, page.getContentResource());
                        facetGroups = filterValues;
                    }
                    if ((siteConfiguration.getSiteSearchFacetGroups() != null)
                            && (!siteConfiguration.getSiteSearchFacetGroups().isEmpty()) && null != searchResultModel
                            && (StringUtils.equals(CommonConstants.SEARCH_DEFAULT_VIEW, searchResultModel.getView()))) {
                        facetGroups = siteConfiguration.getSiteSearchFacetGroups();
                    }
                    filterBean = new FilterBean();
                    filterBean.setFilterName(EndecaConstants.RETURN_FACETS_FOR);
                    filterValues = new ArrayList<>();
                    for (int i = 0; i < facetGroups.size(); i++) {
                        if ((facetGroups.get(i) != null) && (!facetGroups.get(i).equals(StringUtils.EMPTY))) {
                            filterValues.add(facetGroups.get(i));
                        }
                    }
                    if (filterValues.isEmpty()) {
                        filterValues.add(StringUtils.EMPTY);
                    }
                    filterBean.setFilterValues(filterValues);
                    filters.add(filterBean);

                    filterValues = new ArrayList<>();

                    if ((productGridSelectors != null) && (productGridSelectors.getAutoCorrect() != null) && (!productGridSelectors.getAutoCorrect().equals(StringUtils.EMPTY))) {
                        filterValues.add(productGridSelectors.getAutoCorrect());
                    } else {
                        filterValues.add(CommonConstants.TRUE);
                    }
                    filterBean = new FilterBean();
                    filterBean.setFilterName(EndecaConstants.SORT_BY);
                    filterValues = new ArrayList<>();
                    if ((productGridSelectors != null) && (productGridSelectors.getSortyByOption() != null) && (!productGridSelectors.getSortyByOption().equals(StringUtils.EMPTY))) {
                        filterValues.add(productGridSelectors.getSortyByOption());
                    } else {
                        filterValues.add(sortByOption);
                    }
                    filterBean.setFilterValues(filterValues);
                    filters.add(filterBean);

                    filterBean = new FilterBean();
                    filterBean.setFilterName(EndecaConstants.AUTOCORRECT);
                    filterValues = new ArrayList<>();
                    if ((productGridSelectors != null) && (productGridSelectors.getAutoCorrect() != null) && (!productGridSelectors.getAutoCorrect().equals(StringUtils.EMPTY))) {
                        filterValues.add(productGridSelectors.getAutoCorrect());
                    } else {
                        filterValues.add(CommonConstants.TRUE);
                    }
                    filterBean.setFilterValues(filterValues);
                    filters.add(filterBean);
                }
                JcrUtils.closeResourceResolver(adminResourceResolver);
            } catch (Exception e) {
                LOG.error("Exception occured in set facets filter flow: {}", e.getMessage());
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is Exit from setFACETFilters() method");
        return endecaServiceRequestBean;
    }

    /**
     * @param page
     * @param endecaServiceRequestBean
     * @return Set the SortFacet in the Endeca request bean and returns EndecaServiceRequestBean.
     */
    private EndecaServiceRequestBean addSortFacet(final Page page,
                                                  final EndecaServiceRequestBean endecaServiceRequestBean) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into addSortFacet() method");
        List<FilterBean> filters = endecaServiceRequestBean.getFilters();
        final List<Resource> productGrid = $(page.getContentResource())
                .find(SiteMapConstants.EATON_COMPONENTS_PRODUCT_PRODUCT_GRID).asList();
        if (!productGrid.isEmpty()) {
            final java.util.Optional<Resource> componentResourceOptional = productGrid.stream().findFirst();
            if (componentResourceOptional.isPresent()) {
                final Resource resource = componentResourceOptional.get();
                final ProductGridSlingModel productGridSlingModel = resource.adaptTo(ProductGridSlingModel.class);
                if (null != productGridSlingModel) {
                    String sortoption = productGridSlingModel.getDefaultSortingDirection();
                    if (null != sortoption) {
                        filters.add(FilterUtil.getFilterBean(EndecaConstants.SORT_BY, sortoption));
                    } else {
                        filters.add(FilterUtil.getFilterBean(EndecaConstants.SORT_BY, StringUtils.EMPTY));
                    }
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Endeca filters existing on bean post to addSortFacet: %s", filterDump(filters)));
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from addSortFacet() method");
        return endecaServiceRequestBean;
    }

    /**
     * @param productGridSelectors
     * @param endecaServiceRequestBean
     * @return Set the Facets in the Endeca request bean and returns EndecaServiceRequestBean.
     */
    private EndecaServiceRequestBean addFacetFilter(final ProductGridSelectors productGridSelectors,
                                                    final EndecaServiceRequestBean endecaServiceRequestBean) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into addFacetFilter() method");
        if (null != productGridSelectors) {
            List<FilterBean> filters = endecaServiceRequestBean.getFilters();
            List<String> facets = new ArrayList<>();
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Endeca filters existing on bean prior to addFacetFilter: %s",
                        filterDump(filters)));
            }
            if (null != productGridSelectors.getFacets() && !productGridSelectors.getFacets().isEmpty()) {
                for (FacetBean facetBean : productGridSelectors.getFacets()) {
                    if (!facetBean.getFacetID().equalsIgnoreCase(SecureConstants.SECURE_FILTER_LABEL)) {
                        facets.add(facetBean.getFacetID());
                    }
                }
                filters.add(getFilterBean(FILTER_FACETS, facets));

            } else if (filters.stream().noneMatch(fb -> fb.getFilterName().equalsIgnoreCase(FILTER_FACETS))
                    || filters.stream().filter(filterBean -> filterBean.getFilterName().equalsIgnoreCase(FILTER_FACETS)).findFirst().get().getFilterValues().get(0).isEmpty()) {
                filters.add(FilterUtil.getFilterBean(FILTER_FACETS, StringUtils.EMPTY));
            }
            if (null != productGridSelectors.getSortyByOption()) {
                filters.add(FilterUtil.getFilterBean(SORT_BY, productGridSelectors.getSortyByOption()));
            } else {
                if (filters.stream().noneMatch(o -> o.getFilterName().equalsIgnoreCase(SORT_BY))) {
                    filters.add(FilterUtil.getFilterBean(SORT_BY, CommonConstants.ASCENDING_SORT));
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Endeca filters existing on bean post to addFacetFilter: %s",
                        filterDump(filters)));
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from addFacetFilter() method");
        return endecaServiceRequestBean;
    }

    private String filterDump(List<FilterBean> filters) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into filterDump() method");
        StringBuilder filterList = new StringBuilder();
        for (FilterBean bean : filters) {
            filterList.append(FILTERLIST_NAME + bean.getFilterName() + FILTERLIST_VALUES + Arrays.toString(bean.getFilterValues().toArray()) + "\n");
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from filterDump() method");
        return filterList.toString();
    }

    private void populateSKUPageEndecaRequestBean(final String[] selectors,
                                                  final EndecaServiceRequestBean endecaServiceRequestBean,
                                                  final String country,
                                                  final EndecaConfigServiceBean configServiceBean, Page page) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into populateSKUPageEndecaRequestBean() method");
        endecaServiceRequestBean.setSearchApplication(configServiceBean.getSkuDetailsAppName());
        if ((selectors != null) && (selectors.length > 0)) {
            String catalogNumber = selectors[0];
            if ((catalogNumber != null) && (!(StringUtils.EMPTY.equals(catalogNumber)))) {
                catalogNumber = CommonUtil.decodeCatalogNumber(catalogNumber);
                endecaServiceRequestBean.setSearchTerms(catalogNumber);
                String maxReleationsSKU = setPageSize(endecaServiceRequestBean, page);
                List<FilterBean> filters = new ArrayList<>();
                FilterBean filterBean = new FilterBean();
                filterBean.setFilterName(COUNTRY_CONSTANT);
                List<String> filterValues = new ArrayList<>();

                if ((country != null) && (!country.equals(StringUtils.EMPTY))) {
                    filterValues.add(country);
                }

                filterBean.setFilterValues(filterValues);
                filters.add(filterBean);

                filterBean = new FilterBean();
                filterBean.setFilterName(MAXRELATIONSSKU);
                filterValues = new ArrayList<>();

                if (!maxReleationsSKU.equals(StringUtils.EMPTY)) {
                    filterValues.add(maxReleationsSKU);
                }

                filterBean.setFilterValues(filterValues);
                filters.add(filterBean);

                endecaServiceRequestBean.setFilters(filters);
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from populateSKUPageEndecaRequestBean() method");
    }

    /**
     * @param endecaServiceRequestBean
     * @param page
     * @return Sets the starting record number and number of records to return. It returns max relations SKU.
     */
    private String setPageSize(EndecaServiceRequestBean endecaServiceRequestBean, Page page) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into setPageSize() method");
        endecaServiceRequestBean.setStartingRecordNumber("0");
        String maxRelationsSKU = StringUtils.EMPTY;
        Optional<SiteResourceSlingModel> siteConfigResource = eatonSiteConfigService.getSiteConfig(page);
        // Setting PageSize from Site Configuration
        if (siteConfigResource.isPresent()) {
            SiteResourceSlingModel siteConfiguration = siteConfigResource.get();
            if (siteConfiguration.getPageSize() != 0) {
                endecaServiceRequestBean.setNumberOfRecordsToReturn(StringUtils.EMPTY + siteConfiguration.getPageSize());
            }
            if (siteConfiguration.getCountUpSell() != 0) {
                maxRelationsSKU = StringUtils.EMPTY + siteConfiguration.getCountUpSell();
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from setPageSize() method");
        return maxRelationsSKU;
    }

    public void populateSiteSearchPageEndecaRequestBean(final EndecaServiceRequestBean endecaServiceRequestBean,
                                                        final EndecaServiceRequestBean endecaServiceRequestBeanTabsCount,
                                                        final EndecaConfigServiceBean serviceRequestBean,
                                                        final String[] selectors, final Page page, SlingHttpServletRequest request) {
        LOG.debug("Entry into populateSiteSearchPageEndecaRequestBean method");

        String sortByOption = StringUtils.EMPTY;
        ProductGridSelectors productGridSelectors = null;
        List<String> facetGroups = new ArrayList<>();
        List<String> filterValues = new ArrayList<>();
        SearchResultsModel searchResultModel = null;
        String country = CommonUtil.getCountryFromPagePath(page);


        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                if (null != endecaConfig) {
                    endecaServiceRequestBean.setSearchApplication(serviceRequestBean.getSitesearchAppName());
                    endecaServiceRequestBeanTabsCount.setSearchApplication(serviceRequestBean.getSitesearchAppName());
                }
                Optional<SiteResourceSlingModel> siteConfigResource = eatonSiteConfigService.getSiteConfig(page);
                if (siteConfigResource.isPresent()) {
                    SiteResourceSlingModel siteConfiguration = siteConfigResource.get();
                    searchResultModel = getSearchResultModel(page.getContentResource());

                    if (siteConfiguration.getPageSize() != 0) {
                        Integer pageSize = (Integer) siteConfiguration.getPageSize();
                        String contentResourcePath = StringUtils.EMPTY;
                        String pageType = StringUtils.EMPTY;
                        Resource contentResource = page.getContentResource();
                        if (contentResource != null) {
                            contentResourcePath = contentResource.getPath();
                            pageType = contentResource.getValueMap().get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
                        }
                        FacetURLBeanServiceResponse facetURLBeanResponse = facetURLBeanService.getFacetURLBeanResponse(selectors, pageSize, pageType, contentResourcePath);
                        productGridSelectors = facetURLBeanResponse.getProductGridSelectors();
                        if ((null != productGridSelectors) && (null != productGridSelectors.getSearchTerm())) {
                            endecaServiceRequestBean.setSearchTerms(productGridSelectors.getSearchTerm());
                            endecaServiceRequestBeanTabsCount.setSearchTerms(productGridSelectors.getSearchTerm());
                        }
                        endecaServiceRequestBean.setNumberOfRecordsToReturn(pageSize.toString());
                        endecaServiceRequestBeanTabsCount.setNumberOfRecordsToReturn(pageSize.toString());
                    }

                    if ((null != searchResultModel) && (StringUtils.equals(CommonConstants.SEARCH_ADVANCED_VIEW, searchResultModel.getView()))) {
                        sortByOption = searchResultModel.getDefaultSort();
                    }

                    if (siteConfiguration.getDefaultSiteSearchSortOrder() != null
                            && !siteConfiguration.getDefaultSiteSearchSortOrder().equals(StringUtils.EMPTY)
                            && null != searchResultModel
                            && StringUtils.equals(CommonConstants.SEARCH_DEFAULT_VIEW, searchResultModel.getView())) {
                        sortByOption = siteConfiguration.getDefaultSiteSearchSortOrder().trim();
                    }
                    if ((null != searchResultModel) && (StringUtils.equals(CommonConstants.SEARCH_ADVANCED_VIEW, searchResultModel.getView()))) {
                        // if the view in Search Results is advanced, get the facet tags from Facet Settings tab dialog.
                        final String SEARCH_RESULT_PATH = CommonConstants.SEARCH_RESULT_PATH;
                        getAuthoredFacetTags(adminResourceResolver, filterValues, SEARCH_RESULT_PATH, page.getContentResource());
                        facetGroups = filterValues;
                    }

                    if ((siteConfiguration.getSiteSearchFacetGroups() != null)
                            && (!siteConfiguration.getSiteSearchFacetGroups().isEmpty()) && null != searchResultModel
                            && (StringUtils.equals(CommonConstants.SEARCH_DEFAULT_VIEW, searchResultModel.getView()))) {
                        facetGroups = siteConfiguration.getSiteSearchFacetGroups();
                    }
                }

                List<FilterBean> filters = new ArrayList<>();
                List<FilterBean> filtersTabsCount = new ArrayList<>();

                FilterBean filterBean = new FilterBean();
                filterValues = new ArrayList<>();
                filters = new ArrayList<>();
                filtersTabsCount = new ArrayList<>();

                filterBean = new FilterBean();
                filterBean.setFilterName(COUNTRY_CONSTANT);
                filterValues = new ArrayList<>();
                if ((country != null) && (!country.equals(StringUtils.EMPTY))) {
                    filterValues.add(country);
                }
                filterBean.setFilterValues(filterValues);
                filters.add(filterBean);
                filtersTabsCount.add(filterBean);

                filters.add(FilterUtil.getFilterBean(CommonConstants.DISCARD_APPLICATION_ID, CommonConstants.GREEN_SWITCHING));
                filtersTabsCount.add(FilterUtil.getFilterBean(CommonConstants.DISCARD_APPLICATION_ID, CommonConstants.GREEN_SWITCHING));

                boolean secureOnly = false;
                if ((productGridSelectors != null) && (productGridSelectors.getFacets() != null) && (!productGridSelectors.getFacets().isEmpty())) {
                    List<FacetBean> facetList = productGridSelectors.getFacets();
                    for (int i = 0; i < facetList.size(); i++) {
                        FacetBean facetBean = facetList.get(i);
                        if (facetBean.getFacetID().equalsIgnoreCase(SecureConstants.SECURE_FILTER_LABEL)) {
                            secureOnly = true;
                        }
                    }
                }
                if (request != null && authorizationService != null) {
                    // EAT-4643 Secure Search - Secure Search - Results
                    AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(request);
                    if (authenticationToken != null) {
                        endecaServiceRequestBean.setSearchApplication(SecureModule.SECURESEARCH.getAppId());
                        endecaServiceRequestBeanTabsCount.setSearchApplication(SecureModule.SECURESEARCH.getAppId());
                        SecureFilterBeanFactory secureFilterBeanFactory = new SecureFilterBeanFactory();
                        filters.addAll(secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.SECURESEARCH));
                        filtersTabsCount.addAll(secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.SECURESEARCH));
                    }
                }
                filterBean = new FilterBean();
                FilterBean filterBeanTabsCount = new FilterBean();

                filterBean.setFilterName(EndecaConstants.FACETS_STRING_WITH_F);
                filterBeanTabsCount.setFilterName(EndecaConstants.FACETS_STRING_WITH_F);

                filterValues = new ArrayList<>();
                List<String> filterValuesTabsCount = new ArrayList<>();

                if ((productGridSelectors != null) && (productGridSelectors.getFacets() != null) && (!productGridSelectors.getFacets().isEmpty())) {
                    List<FacetBean> facetList = productGridSelectors.getFacets();
                    for (int i = 0; i < facetList.size(); i++) {
                        FacetBean facetBean = facetList.get(i);
                        // Ignore adding Secure Filter as Facet
                        if (!facetBean.getFacetID().equalsIgnoreCase(SecureConstants.SECURE_FILTER_LABEL)) {
                            filterValues.add(facetBean.getFacetID());
                            filterValuesTabsCount.add(facetBean.getFacetID());
                        }
                    }
                }

                String currentTab = StringUtils.EMPTY;
                if ((productGridSelectors != null) && (productGridSelectors.getSelectedTab() != null)) {
                    currentTab = productGridSelectors.getSelectedTab();
                    filterValues.add(currentTab);
                }

                if (filterValues.isEmpty()) {
                    filterValues.add(StringUtils.EMPTY);
                }

                if (filterValuesTabsCount.isEmpty()) {
                    filterValuesTabsCount.add(StringUtils.EMPTY);
                }

                filterBean.setFilterValues(filterValues);
                filters.add(filterBean);

                filterBeanTabsCount.setFilterValues(filterValuesTabsCount);
                filtersTabsCount.add(filterBeanTabsCount);

                // Setting value for Selected Facets for Advanced Search
                if ((null != searchResultModel) && (StringUtils.equals(CommonConstants.SEARCH_ADVANCED_VIEW, searchResultModel.getView()))) {
                    filterBean = new FilterBean();
                    filterValues = new ArrayList<>();

                    filterBean.setFilterName(ADVANCED_SEARCH_CONTENT_TYPE_FACET_CONSTANT);
                    filterValues = new ArrayList<>();

                    if ((productGridSelectors != null) && (productGridSelectors.getSelectedTab() != null)) {
                        currentTab = productGridSelectors.getSelectedTab();
                        filterValues.add(currentTab);
                    }

                    if (filterValues.isEmpty()) {
                        filterValues.add(StringUtils.EMPTY);
                    }

                    filterBean.setFilterValues(filterValues);
                    filters.add(filterBean);
                }

                // Setting startingRecordNumber.
                if ((productGridSelectors != null) && (productGridSelectors.getLoadMoreOption() > 0)) {
                    endecaServiceRequestBean.setStartingRecordNumber(StringUtils.EMPTY + productGridSelectors.getLoadMoreOption());
                    endecaServiceRequestBeanTabsCount.setStartingRecordNumber(StringUtils.EMPTY + productGridSelectors.getLoadMoreOption());
                } else {
                    endecaServiceRequestBean.setStartingRecordNumber("0");
                    endecaServiceRequestBeanTabsCount.setStartingRecordNumber("0");
                }

                filterBean = new FilterBean();
                filterBean.setFilterName(EndecaConstants.RETURN_FACETS_FOR);
                filterValues = new ArrayList<>();

                for (int i = 0; i < facetGroups.size(); i++) {
                    if ((facetGroups.get(i) != null) && (!facetGroups.get(i).equals(StringUtils.EMPTY))) {
                        filterValues.add(facetGroups.get(i));
                    }
                }

                if (filterValues.isEmpty()) {
                    filterValues.add(StringUtils.EMPTY);
                }

                filterBean.setFilterValues(filterValues);
                filters.add(filterBean);
                filtersTabsCount.add(filterBean);

                filterBean = new FilterBean();
                filterBean.setFilterName(EndecaConstants.SORT_BY);
                filterValues = new ArrayList<>();

                if ((productGridSelectors != null) && (productGridSelectors.getSortyByOption() != null) && (!productGridSelectors.getSortyByOption().equals(StringUtils.EMPTY))) {
                    filterValues.add(productGridSelectors.getSortyByOption());
                } else {
                    filterValues.add(sortByOption);
                }
                filterBean.setFilterValues(filterValues);
                filters.add(filterBean);
                filtersTabsCount.add(filterBean);

                filterBean = new FilterBean();
                filterBean.setFilterName(EndecaConstants.AUTOCORRECT);

                filterValues = new ArrayList<>();

                if ((productGridSelectors != null) && (productGridSelectors.getAutoCorrect() != null) && (!productGridSelectors.getAutoCorrect().equals(StringUtils.EMPTY))) {
                    filterValues.add(productGridSelectors.getAutoCorrect());
                } else {
                    filterValues.add(CommonConstants.TRUE);
                }

                filterBean.setFilterValues(filterValues);
                filters.add(filterBean);
                filtersTabsCount.add(filterBean);

                final String applicationId = CommonUtil.getApplicationId(page.getPath());
                if (applicationId.equals(CommonConstants.APPLICATION_ID_ECJV) || applicationId.equals(CommonConstants.APPLICATION_ID_PHOENIXTEC)) {
                    filters.add(FilterUtil.getFilterBean(EndecaConstants.APPLICATION_ID, applicationId));
                    filtersTabsCount.add(FilterUtil.getFilterBean(EndecaConstants.APPLICATION_ID, applicationId));
                }

                endecaServiceRequestBean.setFilters(filters);
                endecaServiceRequestBeanTabsCount.setFilters(filtersTabsCount);
            }
        }

        LOG.debug("Exit from populateSiteSearchPageEndecaRequestBean method");
    }

    private FilterBean getFilterBean(final String key, final List<String> value) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getFilterBean() method");
        final FilterBean filterBean = new FilterBean();
        filterBean.setFilterName(key);
        filterBean.setFilterValues(value);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getFilterBean() method");
        return filterBean;
    }

    //populating Facet Value for Learn Page 
    private FilterBean getFacetFilterforLearnPage(EndecaConfigServiceBean configServiceBean) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getFacetFilterBean() method");
        final List<String> facets = new ArrayList<>();
        if (null != configServiceBean.getFacetLearnPage()) {
            facets.add(configServiceBean.getFacetLearnPage());
        } else {
            facets.add(StringUtils.EMPTY);
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getFacetFilterBean() method");
        return getFilterBean(SiteMapConstants.FACETS, facets);
    }

    private FilterBean getFacetFilterBean(JSONArray activeFacets) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getFacetFilterBean() method");
        final List<String> facets = new ArrayList<>();
        if (null != activeFacets && activeFacets.length() > 0) {
            for (int i = 0; i < activeFacets.length(); i++) {
                try {
                    facets.add(activeFacets.getString(i));
                } catch (JSONException e) {
                    LOG.error("Malformed JSON String provided the the Endeca Request Service", e);
                }
            }
        } else {
            facets.add(StringUtils.EMPTY);
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getFacetFilterBean() method");
        return getFilterBean(SiteMapConstants.FACETS, facets);
    }

    private FilterBean getSortByFilterBean() {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getSortByFilterBean() method");
        final List<String> sortBy = new ArrayList<>();
        sortBy.add(SiteMapConstants.ASCENDING);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getSortByFilterBean() method");
        return getFilterBean(SiteMapConstants.SORT_BY, sortBy);
    }

    private FilterBean getSubmitBuilderSortByFilterBean(final JSONArray sortOrder) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getSubmitBuilderSortByFilterBean() method");
        final List<String> sortBy = new ArrayList<>();
        final String PIPE_ZERO = "|0";
        final String PIPE_ONE = "|1";
        try {
            if (null != sortOrder) {
                final StringBuilder sortOrderBuilder = new StringBuilder();
                for (int sortIndex = 0; sortIndex < sortOrder.length(); sortIndex++) {
                    final JSONObject sortObject = sortOrder.getJSONObject(sortIndex);
                    if (null != sortObject) {
                        if (sortObject.has(NAME) && sortObject.has(ORDER)) {
                            sortOrderBuilder.append(sortObject.get(NAME));
                            if (sortObject.get(ORDER).equals(EndecaConstants.ASCENDING_ASC)) {
                                sortOrderBuilder.append(PIPE_ZERO);
                            } else {
                                sortOrderBuilder.append(PIPE_ONE);
                            }
                        }
                        if (sortIndex != sortOrder.length() - 1) {
                            sortOrderBuilder.append(SEPERATOR);
                        }
                    }
                }
                if (StringUtils.isNotBlank(sortOrderBuilder.toString())) {
                    sortBy.add(sortOrderBuilder.toString());
                }
            } else {
                LOG.debug("\n##### Looks like provided sortOrder array is empty ######\n");
            }
        } catch (JSONException jsonExc) {
            LOG.error("Error returning sort order", jsonExc.getMessage());
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getSubmitBuilderSortByFilterBean() method");
        return getFilterBean(SiteMapConstants.SORT_BY, sortBy);
    }

    private FilterBean getSkuCardFilterBean(final Page page, final ResourceResolver resourceResolver) {
        String pimPath = StringUtils.EMPTY;
        LOG.debug("EndecaRequestServiceImpl : This is entry into getSkuCardFilterBean() method");
        final FilterBean skuCardFilterBean = new FilterBean();
        final List<String> filterValues = new ArrayList<>();
        skuCardFilterBean.setFilterName(SiteMapConstants.SKUCARD_FILTER);

        final ValueMap valueMap = page.getContentResource().getValueMap();
        if (valueMap.containsKey(CommonConstants.PAGE_PIM_PATH)) {
            pimPath = valueMap.get(CommonConstants.PAGE_PIM_PATH, StringUtils.EMPTY);
        } else if (valueMap.containsKey(CommonConstants.PAGE_PIM_PATHS)) {
            pimPath = valueMap.get(CommonConstants.PAGE_PIM_PATHS, StringUtils.EMPTY);
        }
        if (StringUtils.isNotEmpty(pimPath)) {
            final Resource pimResourcePath = resourceResolver.resolve(pimPath);
            final PIMResourceSlingModel pimResourceSlingModel = pimResourcePath
                    .adaptTo(PIMResourceSlingModel.class);
            if (pimResourceSlingModel != null) {
                final Resource skuResourcePath = pimResourceSlingModel.getSkuCardAttributes();
                if (skuResourcePath != null) {
                    final Iterator<Resource> items = skuResourcePath.listChildren();
                    while (items.hasNext()) {
                        final Resource item = items.next();
                        ValueMap properties = item.getValueMap();
                        if (!CommonUtil.getStringProperty(properties, CommonConstants.SKU_CARD_ATTRIBUTE_NAME).equals(StringUtils.EMPTY)) {
                            filterValues.add(CommonUtil.getStringProperty(properties, CommonConstants.SKU_CARD_ATTRIBUTE_NAME));
                        }
                    }
                }
            }
        }

        skuCardFilterBean.setFilterValues(filterValues);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getSkuCardFilterBean() method");
        JcrUtils.closeResourceResolver(resourceResolver);
        return skuCardFilterBean;
    }

    private FilterBean getCountryFilterBean(final String country) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getCountryFilterBean() method");
        final ArrayList<String> filtersValue = new ArrayList<>();
        filtersValue.add(country);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getCountryFilterBean() method");
        return getFilterBean(SiteMapConstants.COUNTRY, filtersValue);
    }

    private List<FilterBean> getReturnFacetsFilterBean(final Page page, final List<FilterBean> filters) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getReturnFacetsFilterBean() method");
        final ValueMap valueMap = page.getContentResource().getValueMap();
        final String pageType = valueMap.get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
        final List<Resource> productGrid = CommonConstants.PAGE_TYPE_LEARN_PAGE.equalsIgnoreCase(pageType) ? (getDigitalCatalogResourceList(page)): 
        	($(page.getContentResource()).find(SiteMapConstants.EATON_COMPONENTS_PRODUCT_PRODUCT_GRID).asList());
        if (!productGrid.isEmpty()) {
            final java.util.Optional<Resource> componentResourceOptional = productGrid.stream().findFirst();
            if (componentResourceOptional.isPresent()) {
                final Resource resource = componentResourceOptional.get();
                final ProductGridSlingModel productGridSlingModel = resource.adaptTo(ProductGridSlingModel.class);
                if (null != productGridSlingModel) {
                    final List<String> facetTags = productGridSlingModel.getFacetTags();
                    if (facetTags.isEmpty()) {
                        if (CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE.equals(pageType) || CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE.equals(pageType) || CommonConstants.PAGE_TYPE_LEARN_PAGE.equalsIgnoreCase(pageType)) {
                            facetTags.add(StringUtils.EMPTY);
                        } else {
                            facetTags.add(SiteMapConstants.PRODUCT_TYPE);
                        }
                        facetTags.add(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL);
                    }
                    final FilterBean returnFacetsFor = getFilterBean(SiteMapConstants.RETURN_FACETS_FOR, facetTags);
                    filters.add(returnFacetsFor);
                }
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getReturnFacetsFilterBean() method");
        return filters;
    }
    
    private List<Resource> getDigitalCatalogResourceList(Page page) {
    	List<Resource> resourceListV2 = $(page.getContentResource()).find(SiteMapConstants.EATON_COMPONENTS_DIGITAL_PRODUCT_GRID_V2).asList();
    	if(!resourceListV2.isEmpty()) {
    		return resourceListV2;
    	}else {
    		return $(page.getContentResource()).find(SiteMapConstants.EATON_COMPONENTS_DIGITAL_PRODUCT_GRID).asList();
    	}
    }

    private EndecaServiceRequestBean setSearchTerm(final String[] subCatergoryTags,
                                                   final EndecaServiceRequestBean endecaServiceRequestBean,
                                                   final ResourceResolver resourceResolver) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into setSearchTerm() method");
        if (subCatergoryTags.length > 0) {
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            if (tagManager != null) {
                final Tag subCategoryTag = tagManager.resolve(subCatergoryTags[0]);
                if (null != subCategoryTag) {
                    endecaServiceRequestBean.setSearchTerms(subCategoryTag.getName());
                }
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from setSearchTerm() method");
        return endecaServiceRequestBean;

    }

    private EndecaServiceRequestBean setFilters(final EndecaServiceRequestBean endecaServiceRequestBean,
                                                final Page page, final String country, final String inventoryId,
                                                final JSONArray facetArray, final JSONArray sortOrder, JSONArray activeFacets, final String[] subCatergoryTags, final ResourceResolver resourceResolver, final String pageType) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into setFilters() method");
        endecaServiceRequestBean.setStartingRecordNumber(SiteMapConstants.RECORD_START);
        Optional<SiteResourceSlingModel> siteConfigResource = eatonSiteConfigService.getSiteConfig(page);
        final EndecaConfigServiceBean configServiceBean = endecaConfig.getConfigServiceBean();                
        if (siteConfigResource.isPresent() && siteConfigResource.get().getPageSize() != 0 && !MetaTagsModel.isIsSubmittalBuilder()) {
            endecaServiceRequestBean.setNumberOfRecordsToReturn(StringUtils.EMPTY + siteConfigResource.get().getPageSize());
        } else {
            endecaServiceRequestBean.setNumberOfRecordsToReturn(SiteMapConstants.TOTAL_RECORD);
        }
        List<FilterBean> filters = new ArrayList<>();
        final FilterBean countryBean = getCountryFilterBean(country);
        filters.add(countryBean);
        if (null != facetArray) {
            filters = getSubmitBuilderReturnFacetsFilterBean(facetArray, filters);
            endecaServiceRequestBean.setNumberOfRecordsToReturn("10");
            final FilterBean sortBean = getSubmitBuilderSortByFilterBean(sortOrder);
            filters.add(sortBean);
        } else {
            filters = getReturnFacetsFilterBean(page, filters);
            final FilterBean sortBean = getSortByFilterBean();
            filters.add(sortBean);
        }
        //Adding Facet Filter for the Learn Page from config File
        if(CommonConstants.PAGE_TYPE_LEARN_PAGE.equalsIgnoreCase(pageType)){   
            final FilterBean facetBean = getFacetFilterforLearnPage(configServiceBean);
            filters.add(facetBean);
       }         
       else{
           final FilterBean facetBean = getFacetFilterBean(activeFacets);
            filters.add(facetBean);
       }

        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            final FilterBean skuCardFilterBean = getSkuCardFilterBean(page, adminResourceResolver);
            filters.add(skuCardFilterBean);
        }
        if ((inventoryId != null) && (!inventoryId.equals(StringUtils.EMPTY))) {
            endecaServiceRequestBean.setSearchTerms(inventoryId);
        }

        if (null != subCatergoryTags && CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE.equalsIgnoreCase(pageType) || CommonConstants.PAGE_TYPE_LEARN_PAGE.equalsIgnoreCase(pageType)) {
            final FilterBean pageTypeFilterBean = getPageTypeFilterBean(subCatergoryTags, resourceResolver);
            filters.add(pageTypeFilterBean);
        }

        final String applicationId = CommonUtil.getApplicationId(page.getPath());
        if (!applicationId.equals(CommonConstants.APPLICATION_ID_MISSING)) {
            filters.add(FilterUtil.getFilterBean(EndecaConstants.APPLICATION_ID, applicationId));
        }

        //Find facets filter and remove "Secure only" value
        for (FilterBean temp : filters) {
            if (temp.getFilterName().equalsIgnoreCase(EndecaConstants.FACETS_STRING) && temp.getFilterValues().contains(SecureConstants.SECURE_FILTER_LABEL)) {
                List<String> temp2 = temp.getFilterValues();
                temp2.remove(SecureConstants.SECURE_FILTER_LABEL);
                temp.setFilterValues(temp2);
            }
            if (temp.getFilterName().equalsIgnoreCase(EndecaConstants.RETURN_FACETS_FOR)) {
            	if (temp.getFilterValues().contains(CommonConstants.TAG_NEWS_RELEASES_TOPIC)) {
            		temp.getFilterValues().set(temp.getFilterValues().indexOf(CommonConstants.TAG_NEWS_RELEASES_TOPIC), CommonConstants.TAG_NEWS_RELEASES_TOPIC.toLowerCase());
            	}
            }
        }
        endecaServiceRequestBean.setFilters(filters);
        LOG.debug("EndecaRequestServiceImpl : This is exit from setFilters() method");
        return endecaServiceRequestBean;
    }

    public EndecaServiceRequestBean getSubmitBuilderEndecaRequestBean(final Page page,
                                                                      final ResourceResolver resourceResolver,
                                                                      final String subCatergoryTags,
                                                                      final String facetValue,
                                                                      final String sortOrder,
                                                                      final String activeFacets,
                                                                      final int pageSize,
                                                                      final int startingRecord) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getSubmitBuilderEndecaRequestBean() method");
        EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
        final String country = CommonUtil.getCountryFromPagePath(page);
        final String lang = CommonUtil.getUpdatedLocaleFromPagePath(page);
        final EndecaConfigServiceBean configServiceBean = endecaConfig.getConfigServiceBean();
        endecaServiceRequestBean.setSearchApplicationKey(configServiceBean.getEspAppKey());
        endecaServiceRequestBean.setFunction(SiteMapConstants.SEARCH);
        endecaServiceRequestBean.setLanguage(lang);
        endecaServiceRequestBean.setSearchApplication(configServiceBean.getSubmittalBuilderAppName());
        try {
            if (StringUtils.isNotBlank(subCatergoryTags) && StringUtils.isNotBlank(facetValue)
                    && StringUtils.isNotBlank(sortOrder) && StringUtils.isNotBlank(activeFacets)) {
                final JSONArray tagsArray = new JSONArray(subCatergoryTags);
                final JSONArray facetArray = new JSONArray(facetValue);
                final JSONArray sortArray = new JSONArray(sortOrder);
                final JSONArray prefixedSortArray = new JSONArray();
                for (int i = 0; i < sortArray.length(); i++) {
                    final JSONObject sortProp = sortArray.getJSONObject(i);
                    final JSONObject prefixedSortProp = new JSONObject();
                    if (!sortProp.getString(EndecaConstants.NAME_STRING).isEmpty()) {
                        prefixedSortProp.put(EndecaConstants.NAME_STRING, "p_" + sortProp.getString(EndecaConstants.NAME_STRING));
                    } else {
                        prefixedSortProp.put(EndecaConstants.NAME_STRING, sortProp.getString(EndecaConstants.NAME_STRING));
                    }
                    prefixedSortProp.put(ORDER, sortProp.getString(ORDER));
                    prefixedSortArray.put(prefixedSortProp);
                }
                final JSONArray activeFacetsArray = new JSONArray(activeFacets);
                endecaServiceRequestBean = setSubmitbuilderSearchTerm(tagsArray, endecaServiceRequestBean,
                        resourceResolver);
                endecaServiceRequestBean = setFilters(endecaServiceRequestBean, page,
                        country, StringUtils.EMPTY, facetArray, prefixedSortArray, activeFacetsArray, null, null, null);

            }
        } catch (JSONException jsonExc) {
            LOG.error("Error returning endecaServiceRequestBean", jsonExc);
        }

        endecaServiceRequestBean.setNumberOfRecordsToReturn(Integer.toString(pageSize));
        endecaServiceRequestBean.setStartingRecordNumber(Integer.toString(startingRecord));
        LOG.debug("EndecaRequestServiceImpl : This is exit from getSubmitBuilderEndecaRequestBean() method");
        return endecaServiceRequestBean;
    }

    private EndecaServiceRequestBean setSubmitbuilderSearchTerm(final JSONArray submitBuilderTags,
                                                                final EndecaServiceRequestBean endecaServiceRequestBean,
                                                                final ResourceResolver resourceResolver) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into setSubmitbuilderSearchTerm() method");
        try {
            if (submitBuilderTags.length() > 0) {
                final StringBuilder searchTerm = new StringBuilder();
                for (int tagIndex = 0; tagIndex < submitBuilderTags.length(); tagIndex++) {
                    {
                        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                        if (tagManager != null && StringUtils.isNotEmpty((submitBuilderTags.getString(tagIndex)))) {
                            final EndecaFacetTag submitBuilderTag = tagManager
                                    .resolve(submitBuilderTags.getString(tagIndex))
                                    .adaptTo(EndecaFacetTag.class);
                            if (null != submitBuilderTag) {
                                searchTerm.append(submitBuilderTag.getFacetId());
                                if (tagIndex != submitBuilderTags.length() - 1) {
                                    searchTerm.append(SEPERATOR);
                                }
                            }
                        }
                    }
                }
                endecaServiceRequestBean.setSearchTerms(searchTerm.toString());
            }
        } catch (JSONException jsonExc) {
            LOG.error("Error returning search term", jsonExc.getMessage());
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from setSubmitbuilderSearchTerm() method");
        return endecaServiceRequestBean;
    }

    private List<FilterBean> getSubmitBuilderReturnFacetsFilterBean(final JSONArray facetsArray, final List<FilterBean> filters) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getSubmitBuilderReturnFacetsFilterBean() method");
        final List<String> facetTags = new ArrayList<>();
        try {
            if (facetsArray.length() > 0) {
                for (int facetIndex = 0; facetIndex < facetsArray.length(); facetIndex++) {
                    if (facetsArray.get(facetIndex) != null) {
                        facetTags.add(facetsArray.get(facetIndex).toString());
                    }
                }
            } else {
                facetTags.add(SiteMapConstants.PRODUCT_TYPE);
            }
            final FilterBean returnFacetsFor = getFilterBean(SiteMapConstants.RETURN_FACETS_FOR, facetTags);
            filters.add(returnFacetsFor);
        } catch (JSONException e) {
            LOG.error("Error returning facets filter", e.getMessage());
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getSubmitBuilderReturnFacetsFilterBean() method");
        return filters;
    }

    public String getInventoryId(final Page page, final ResourceResolver resourceResolver) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getInventoryId() method");
        String inventoryId = StringUtils.EMPTY;
        final ValueMap currentPageProperties = page.getProperties();
        final Resource pimResource = getPimResource(currentPageProperties, resourceResolver);
        if (null != pimResource) {
            final PIMResourceSlingModel pimResourceSlingModel =
                    pimResource.adaptTo(PIMResourceSlingModel.class);
            if (null != pimResourceSlingModel) {
                final String pdhRecordPath = pimResourceSlingModel.getPdhRecordPath();
                final Resource pdhRecordResource = resourceResolver.getResource(pdhRecordPath);
                if (pdhRecordResource != null) {
                    final Resource inventoryIdResource = pdhRecordResource.getParent();
                    inventoryId = inventoryIdResource.getName();
                }
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getInventoryId() method");
        return inventoryId;
    }

    private Resource getPimResource(final ValueMap currentPageProperties, final ResourceResolver resourceResolver) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getPimResource() method");
        Resource pimResource = null;
        if (currentPageProperties.get(CommonConstants.PAGE_PIM_PATH) != null) {
            final String pimPagePath = currentPageProperties.get(SiteMapConstants.PIM_PAGE_PATH).toString();
            if (StringUtils.isNotBlank(pimPagePath)) {
                pimResource = resourceResolver.getResource(pimPagePath);
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getPimResource() method");
        return pimResource;
    }

    public SearchResultsModel getSearchResultModel(final Resource contentResource) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getSearchResultModel() method");
        SearchResultsModel searchResultModel = null;
        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                if (adminResourceResolver != null) {
                    final SlingQuery searchResults = $(contentResource).find("eaton/components/search/search-results");
                    final List<Resource> resources = searchResults.asList();
                    final Iterator<Resource> iterator = resources.iterator();
                    while (iterator.hasNext()) {
                        Resource searchResultResource = iterator.next();
                        searchResultModel = searchResultResource.adaptTo(SearchResultsModel.class);
                    }
                }
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getSearchResultModel() method");
        return searchResultModel;
    }


    private void getAuthoredFacetTags(final ResourceResolver adminResourceResolver,
                                      final List<String> filterValues, final String componentPath,
                                      final Resource contentResource) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getAuthoredFacetTags() method");
        // Get the facet tags from the dialog.
        final TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);
        final String productGridResourcePath = contentResource.getPath() + componentPath;
        final Resource productGridResource = adminResourceResolver.getResource(productGridResourcePath);

        if (null != productGridResource) {
            final ValueMap productGridResourceProperties = productGridResource.getValueMap();
            final String[] authoredFacetTags = (String[]) productGridResourceProperties.get("tags");
            if (authoredFacetTags != null) {
                for (int j = 0; j < authoredFacetTags.length; j++) {
                    Tag facetTag = null;
                    if (tagManager != null) {
                        facetTag = tagManager.resolve(authoredFacetTags[j]);
                        if (facetTag != null) {
                            EndecaFacetTag endecaFacetTag = facetTag.adaptTo(EndecaFacetTag.class);
                            if (endecaFacetTag != null && endecaFacetTag.hasFacetId()) {
                                filterValues.add(endecaFacetTag.getFacetId());
                            }
                        }
                    }
                }
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getAuthoredFacetTags() method");
    }

    /**
     * Generates and EndecaServiceRequestBean given a local, list of inventoryIds, list of facets to return, a list of facets to apply, and a sort order.
     *
     * @param inventoryIds    The list of PDH inventory ids to search for.
     * @param returnFacetsFor The facets to return from Endeca.
     * @param activeFacets    The list of facets to apply.
     * @param sortBy          The sort by to use in the Endeca request.
     * @param productType     The human readable productType, not the ID. Examples include "UPS" and "Accessory".
     * @param pageSize        The number of records to return from Endeca.
     * @param startingRecord  The first record in the list of returned records.
     * @return The Endeca request to use to find a SKU list for use in a product family page.
     */
    public EndecaServiceRequestBean getProductFamilyEndecaRequestBean(
            final Page page,
            final List<String> inventoryIds,
            final List<String> returnFacetsFor,
            final List<String> activeFacets,
            final String sortBy,
            final String sortFacet,
            final String productType,
            final int pageSize,
            final int startingRecord) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getProductFamilyEndecaRequestBean() method");
        final EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
        final String lang = CommonUtil.getUpdatedLocaleFromPagePath(page);
        final List<String> activeFacetValues = activeFacets != null ? new ArrayList<>(activeFacets) : new ArrayList<>();
        final List<FilterBean> filters = new ArrayList<>();

        filters.add(getCountryFilterBean(CommonUtil.getCountryFromPagePath(page)));
        filters.add(getFilterBean(RETURN_FACETS_FOR, returnFacetsFor));

        if (StringUtils.isNotEmpty(sortBy)) {
            filters.add(FilterUtil.getFilterBean(SORT_BY, sortBy));
        } else {
            filters.add(FilterUtil.getFilterBean(SORT_BY, CommonConstants.ASCENDING_SORT));
        }

        if (StringUtils.isNotEmpty(sortFacet)) {
            filters.add(FilterUtil.getFilterBean(EndecaConstants.SORT_FACET, sortFacet));
        }

        if (!activeFacetValues.isEmpty()) {
            filters.add(getFilterBean(FILTER_FACETS, activeFacetValues));
        } else {
            filters.add(FilterUtil.getFilterBean(FILTER_FACETS, StringUtils.EMPTY));
        }

        if (!StringUtils.isEmpty(productType)) {
            final FilterBean filterBean = new FilterBean();
            final ArrayList<String> filterValues = new ArrayList<>();
            filterBean.setFilterName(CommonConstants.PRODUCT_TYPE_FACET_NAME);
            filterValues.add(StringEscapeUtils.escapeXml(productType));
            filterBean.setFilterValues(filterValues);
            filters.add(filterBean);
            returnFacetsFor.add(PRODUCT_STATUS);
        }

        if (inventoryIds.size() > 1) {
            final FilterBean familyIdsFilter = new FilterBean();
            familyIdsFilter.setFilterName(FILTER_FAMILY_IDS);
            familyIdsFilter.setFilterValues(inventoryIds);
            filters.add(familyIdsFilter);
        }

        if (!inventoryIds.isEmpty()) {
            endecaServiceRequestBean.setSearchTerms(inventoryIds.get(0));
        }
        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            final FilterBean skuCardFilterBean = getSkuCardFilterBean(page, adminResourceResolver);
            filters.add(skuCardFilterBean);
        }


        endecaServiceRequestBean.setNumberOfRecordsToReturn(Integer.toString(pageSize));
        endecaServiceRequestBean.setStartingRecordNumber(Integer.toString(startingRecord));
        endecaServiceRequestBean.setSearchApplicationKey(endecaConfig.getConfigServiceBean().getEspAppKey());
        endecaServiceRequestBean.setFunction(SiteMapConstants.SEARCH);
        endecaServiceRequestBean.setLanguage(lang);
        endecaServiceRequestBean.setSearchApplication(endecaConfig.getConfigServiceBean().getProductfamilyAppName());
        endecaServiceRequestBean.setFilters(filters);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getProductFamilyEndecaRequestBean() method");
        return endecaServiceRequestBean;
    }

    /**
     * @param page
     * @param inventoryIds
     * @param startingRecord
     * @return
     */
    public EndecaServiceRequestBean getProductFamilySiteMapEndecaRequestBean(
            final Page page,
            final List<String> inventoryIds,
            final int startingRecord) {
        LOG.debug("EndecaRequestServiceImpl : This is Entry into getProductFamilySiteMapEndecaRequestBean() method");
        final EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
        final String lang = CommonUtil.getUpdatedLocaleFromPagePath(page);
        final List<FilterBean> filters = new ArrayList<>();

        filters.add(getCountryFilterBean(CommonUtil.getCountryFromPagePath(page)));
        if (!inventoryIds.isEmpty()) {
            endecaServiceRequestBean.setSearchTerms(inventoryIds.get(0));
        }
        endecaServiceRequestBean.setNumberOfRecordsToReturn(endecaConfig.getConfigServiceBean().getPfSitemapNumberOfRecordsToReturn());
        endecaServiceRequestBean.setStartingRecordNumber(Integer.toString(startingRecord));
        endecaServiceRequestBean.setSearchApplicationKey(endecaConfig.getConfigServiceBean().getEspAppKey());
        endecaServiceRequestBean.setFunction(SiteMapConstants.SEARCH);
        endecaServiceRequestBean.setLanguage(lang);
        endecaServiceRequestBean.setSearchApplication(endecaConfig.getConfigServiceBean().getProductfamilySitemapAppName());
        endecaServiceRequestBean.setFilters(filters);
        LOG.debug("EndecaRequestServiceImpl : This is Exit from getProductFamilySiteMapEndecaRequestBean() method");
        return endecaServiceRequestBean;
    }

    /**
     * @param page
     * @param startingRecord
     * @return
     */
    public EndecaServiceRequestBean getOrphanSkuSiteMapEndecaRequestBean(
            final Page page,
            final int startingRecord) {
        LOG.debug("EndecaRequestServiceImpl : This is Entry into getProductFamilySiteMapEndecaRequestBean() method");
        final EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
        final String lang = CommonUtil.getUpdatedLocaleFromPagePath(page);
        final List<FilterBean> filters = new ArrayList<>();

        filters.add(getCountryFilterBean(CommonUtil.getCountryFromPagePath(page)));
        endecaServiceRequestBean.setSearchTerms(EndecaConstants.IGNORE);
        endecaServiceRequestBean.setNumberOfRecordsToReturn(endecaConfig.getConfigServiceBean().getSkuSitemapNumberOfRecordsToReturn());
        endecaServiceRequestBean.setStartingRecordNumber(Integer.toString(startingRecord));
        endecaServiceRequestBean.setSearchApplicationKey(endecaConfig.getConfigServiceBean().getEspAppKey());
        endecaServiceRequestBean.setFunction(SiteMapConstants.SEARCH);
        endecaServiceRequestBean.setLanguage(lang);
        endecaServiceRequestBean.setSearchApplication(endecaConfig.getConfigServiceBean().getSkuSitemapAppName());
        endecaServiceRequestBean.setFilters(filters);
        LOG.debug("EndecaRequestServiceImpl : This is Exit from getProductFamilySiteMapEndecaRequestBean() method");
        return endecaServiceRequestBean;
    }

    /**
     * Generates and EndecaServiceRequestBean given a local, list of inventoryIds, list of facets to return, a list of facets to apply, and a sort order.
     *
     * @param page         The page to generate a product family Endeca request for.
     * @param activeFacets The list of facets to apply.
     * @param sortBy       The sort by to use in the Endeca request.
     * @param pageSize     The number of records to return from Endeca.
     * @return The Endeca request to use to find a SKU list for use in a product family page.
     */
    public EndecaServiceRequestBean getAltProductFamilyEndecaRequestBean(
            final Page page,
            final List<String> activeFacets,
            final String sortBy,
            final String sortFacet,
            final String productType,
            final int pageSize) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getAltProductFamilyEndecaRequestBean() method");
        final EndecaServiceRequestBean endecaServiceRequest;
        FilterBean filterBean = new FilterBean();
        filterBean.setFilterName(EndecaConstants.GET_ALT_SKU);
        ArrayList<String> filterValues = new ArrayList<>();
        filterValues.add(String.valueOf(pageSize));
        filterBean.setFilterValues(filterValues);
        endecaServiceRequest = getProductFamilyEndecaRequestBean(page, activeFacets, sortBy, sortFacet, productType);
        endecaServiceRequest.getFilters().add(filterBean);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getAltProductFamilyEndecaRequestBean() method");
        return endecaServiceRequest;
    }

    /**
     * Given a page, a list of active facets, and the sort by parameter this method will return a Endeca request that
     * can be used for any product family page. This method takes into consideration the product grid, product selector form,
     * single PIM page property, and multi PIM page properties in order to formulate the appropriate request for the given page.
     *
     * @param page         The page to generate a product family Endeca request for.
     * @param activeFacets The list of facets to apply.
     * @param sortBy       The sort by to use in the Endeca request.
     * @return The Endeca request to use to find a SKU list for use in a product family page.
     */
    public EndecaServiceRequestBean getProductFamilyEndecaRequestBean(final Page page, final List<String> activeFacets, final String sortBy, final String sortFacet, final String productType) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getProductFamilyEndecaRequestBean() method");
        final List<String> returnFacetsFor = new ArrayList<>(page.adaptTo(PageDecorator.class).getFacets());
        final Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(page);
        final List<String> inventoryIds = page.adaptTo(PageDecorator.class).getPims().stream()
                .map(PIMResourceSlingModel::getInventoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Generating Endeca reqest bean from Endeca request service with these pims: %s",
                    inventoryIds.stream().collect(Collectors.joining(", "))));
        }

        final int pageSize;
        if (siteConfig.isPresent() && siteConfig.get().getPageSize() != 0) {
            pageSize = siteConfig.get().getPageSize();
        } else {
            pageSize = CommonConstants.DEFAULT_PAGE_SIZE;
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("No site config found for page: %s", page.getPath()));
            }
        }
        LOG.debug("EndecaRequestServiceImpl : This is exit from getProductFamilyEndecaRequestBean() method");
        return getProductFamilyEndecaRequestBean(page, inventoryIds, returnFacetsFor, activeFacets, sortBy, sortFacet, productType, pageSize, CommonConstants.DEFAULT_STARTING_RECORD);
    }

    public EndecaServiceRequestBean getProductFamilyEndecaRequestBean(final Page page, final List<String> activeFacets, final String sortBy, final String sortFacet) {
        final ProductFamilyDetails productFamilyDetails = productFamilyDetailService.getProductFamilyDetailsBean(page);
        return getProductFamilyEndecaRequestBean(page, activeFacets, sortBy, sortFacet, productFamilyDetails != null ? productFamilyDetails.getProductType() : StringUtils.EMPTY);
    }

    /*
     * Method to get PageType filter from Resources Tag field. PageType filter is required to fetch only product-family cards in product-grid   component.
     * Return empty filter value if appropriate page-type tag not associated.
     */
    private FilterBean getPageTypeFilterBean(final String[] subCatergoryTags, final ResourceResolver resourceResolver) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getPageTypeFilterBean() method");
        final FilterBean filterBean = new FilterBean();
        final List<String> resourceTags = new ArrayList<>();
        if (subCatergoryTags.length > 0) {
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            if (null != tagManager) {
                Arrays.stream(subCatergoryTags)
                        .forEach(resourceTag -> {
                            final Tag subCategoryTag = tagManager.resolve(resourceTag);
                            if (null != subCategoryTag && subCategoryTag.getPath().contains(CommonConstants.PAGE_TYPE)) {
                                resourceTags.add(subCategoryTag.getName());
                            }
                        });
            }
        }
        if (resourceTags.isEmpty()) {
            resourceTags.add(StringUtils.EMPTY);
        }
        filterBean.setFilterName(CommonConstants.FILTER_PAGE_TYPE);
        filterBean.setFilterValues(resourceTags);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getPageTypeFilterBean() method");
        return filterBean;
    }

    public EndecaServiceRequestBean getCrossReferenceEndecaRequestBean(final Page page,
                                                                       final List<String> activeFacets,
                                                                       final String searchTerm,
                                                                       final String startingRecord,
                                                                       final String pageSize,
                                                                       final String sortBy, ArrayList<String> returnFacetFor) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getCrossReferenceEndecaRequestBean() method");
        final EndecaServiceRequestBean request = new EndecaServiceRequestBean();
        final List<FilterBean> filters = new ArrayList<>();

        final FilterBean facetsFilter = new FilterBean();
        facetsFilter.setFilterName(EndecaConstants.FILTER_FACETS);
        facetsFilter.setFilterValues(activeFacets.isEmpty() ? Lists.newArrayList(StringUtils.EMPTY) : activeFacets);
        filters.add(facetsFilter);

        if (null != returnFacetFor) {
            final FilterBean returnFacetsForFilter = new FilterBean();
            returnFacetsForFilter.setFilterName(EndecaConstants.RETURN_FACETS_FOR);
            returnFacetsForFilter.setFilterValues(returnFacetFor);
            filters.add(returnFacetsForFilter);
        }

        final FilterBean sortByFilter = new FilterBean();
        sortByFilter.setFilterName(EndecaConstants.SORT_BY);
        sortByFilter.setFilterValues(Lists.newArrayList(sortBy));
        filters.add(sortByFilter);

        filters.add(getCountryFilterBean(page.getLanguage().getCountry()));

        request.setFilters(filters);
        request.setSearchApplication(endecaConfig.getConfigServiceBean().getCrossReferenceAppName());
        request.setSearchApplicationKey(endecaConfig.getConfigServiceBean().getEspAppKey());
        request.setSearchTerms(CommonConstants.SORT_DIRECTION_DELIMITER + searchTerm + CommonConstants.SORT_DIRECTION_DELIMITER);
        request.setFunction(EndecaConstants.SEARCH_STRING);
        request.setLanguage(CommonUtil.getUpdatedLocaleFromPagePath(page));
        request.setStartingRecordNumber(startingRecord);
        request.setNumberOfRecordsToReturn(pageSize);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getCrossReferenceEndecaRequestBean() method");
        return request;
    }

    public EndecaServiceRequestBean getProductCompareEndecaRequestBean(final Page page, final List<String> catalogNo) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getProductCompareEndecaRequestBean() method");
        final EndecaServiceRequestBean request = new EndecaServiceRequestBean();
        final List<FilterBean> filters = new ArrayList<>();
        String pageCountry = CommonUtil.getCountryFromPagePath(page);
        final String lang = CommonUtil.getUpdatedLocaleFromPagePath(page);
        filters.add(getCountryFilterBean(pageCountry));

        final FilterBean catalogFilter = new FilterBean();
        catalogFilter.setFilterName(EndecaConstants.CATALOG_NO);

        catalogFilter.setFilterValues(catalogNo);
        filters.add(catalogFilter);

        request.setFilters(filters);
        request.setSearchApplication(endecaConfig.getConfigServiceBean().getComparisonAppName());
        request.setSearchApplicationKey(endecaConfig.getConfigServiceBean().getEspAppKey());
        request.setFunction(EndecaConstants.SEARCH_STRING);
        request.setLanguage(lang);
        request.setStartingRecordNumber(STARTING_RECORD);
        request.setNumberOfRecordsToReturn(NO_OF_RECORDS);
        request.setSearchTerms(EndecaConstants.IGNORE);
        return request;
    }

    public EndecaServiceRequestBean getProductCompatibilityEndecaRequestBean(final Page page, final String searchTerm, List<Filter> filterLists, final List<String> activeFacets) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getProductCompareEndecaRequestBean() method");
        final EndecaServiceRequestBean request = new EndecaServiceRequestBean();
        final List<FilterBean> filters = new ArrayList<>();
        final List<String> activeFacetValues = activeFacets != null ? new ArrayList<>(activeFacets) : new ArrayList<>();
        filters.add(getCountryFilterBean(page.getLanguage().getCountry()));
        final Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(page);
        final int pageSize;
        if (siteConfig.isPresent() && siteConfig.get().getPageSize() != 0) {
            pageSize = siteConfig.get().getPageSize();
        } else {
            pageSize = CommonConstants.DEFAULT_PAGE_SIZE;
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("No site config found for page: %s", page.getPath()));
            }
        }
        final ArrayList<String> filtersValue = new ArrayList<>();
        for (int i = 0; i < filterLists.size(); i++) {
            filtersValue.add(filterLists.get(i).getFilterAttributeVaue());
        }
        final FilterBean returnFacetsForFilter = new FilterBean();
        returnFacetsForFilter.setFilterName(EndecaConstants.RETURN_FACETS_FOR);
        returnFacetsForFilter.setFilterValues(filtersValue);
        filters.add(returnFacetsForFilter);
        LOG.debug("EndecaRequestServiceImpl : This is entry into getCountryFilterBean() method");

        if (!activeFacetValues.isEmpty()) {
            filters.add(getFilterBean(FILTER_FACETS, activeFacetValues));
        } else {
            filters.add(FilterUtil.getFilterBean(FILTER_FACETS, StringUtils.EMPTY));
        }

        request.setFilters(filters);

        request.setSearchApplication(endecaConfig.getConfigServiceBean().getCompatibilityAppName());
        request.setSearchApplicationKey(endecaConfig.getConfigServiceBean().getEspAppKey());
        request.setFunction(EndecaConstants.SEARCH_STRING);
        request.setLanguage(page.getLanguage().getLanguage() + CommonConstants.UNDERSCORE + page.getLanguage().getCountry());
        request.setStartingRecordNumber(STARTING_RECORD);
        request.setNumberOfRecordsToReturn(Integer.toString(pageSize));
        request.setSearchTerms(searchTerm);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getProductCompareEndecaRequestBean() method");
        return request;
    }

    public EndecaServiceRequestBean getDataSheetEndecaRequestBean(String locale, List<String> catalogNo) {
        LOG.debug("EndecaRequestServiceImpl : This is entry into getDataSheetEndecaRequestBean() method");
        String replaceString = locale.replace(CommonConstants.HYPHEN, CommonConstants.UNDERSCORE);
        String countryCode = replaceString.substring(replaceString.lastIndexOf(CommonConstants.UNDERSCORE) + 1);
        countryCode = countryCode.toUpperCase();
        String langCode = replaceString.substring(0, 0 + 2);
        final EndecaServiceRequestBean request = new EndecaServiceRequestBean();
        final List<FilterBean> filters = new ArrayList<>();

        filters.add(getCountryFilterBean(countryCode));

        final FilterBean catalogFilter = new FilterBean();
        catalogFilter.setFilterName(EndecaConstants.CATALOG_NO);

        catalogFilter.setFilterValues(catalogNo);
        filters.add(catalogFilter);

        request.setFilters(filters);
        request.setSearchApplication(endecaConfig.getConfigServiceBean().getComparisonAppName());
        request.setSearchApplicationKey(endecaConfig.getConfigServiceBean().getEspAppKey());
        request.setFunction(EndecaConstants.SEARCH_STRING);
        request.setLanguage(langCode + CommonConstants.UNDERSCORE + countryCode);
        request.setStartingRecordNumber(STARTING_RECORD);
        request.setNumberOfRecordsToReturn(NO_OF_RECORDS);
        request.setSearchTerms(EndecaConstants.IGNORE);
        LOG.debug("EndecaRequestServiceImpl : This is exit from getDataSheetEndecaRequestBean() method");
        return request;
    }
}
