package com.eaton.platform.core.models.secure;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.FacetedHeaderNavigationResultBean;
import com.eaton.platform.core.bean.SortByOptionValueBean;
import com.eaton.platform.core.bean.secure.NewAdvancedSearchFacetConfiguration;
import com.eaton.platform.core.featureflags.FeatureFlag;
import com.eaton.platform.core.models.EndecaFacetTag;
import com.eaton.platform.core.models.downloadable.AbstractBulkDownloadableImpl;
import com.eaton.platform.core.search.api.FacetConfiguration;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.eaton.platform.core.constants.CommonConstants.DOWNLOAD_DEFAULT_VALUE;
import static com.eaton.platform.core.constants.CommonConstants.DOWNLOAD_KEY;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author ICF
 * @version 1.0
 * @since 2021
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AdvancedSearchModel extends AbstractBulkDownloadableImpl {
    public static final String RESOURCE_TYPE = "eaton/components/secure/advanced-search";

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AdvancedSearchModel.class);
    /**
     * The facets key results.
     */
    private static final String FACETS_KEY_RESULTS = "results";
    private static final String FACETS_KEY_RESULTS_DEFAULT = "Results";

    private static final String VIEW_KEY_GRID = "gridview";
    private static final String VIEW_KEY_GRID_DEFAULT = "Grid View";

    private static final String VIEW_KEY_LIST = "listview";
    private static final String VIEW_KEY_LIST_DEFAULT = "List View";

    private static final String FROM_KEY = "from";
    private static final String FROM_DEFAULT_VALUE = "From";

    private static final String TO_KEY = "to";
    private static final String TO_DEFAULT_VALUE = "To";

    private static final String DATE_RANGE_KEY = "daterange";
    private static final String DATE_RANGE_DEFAULT_VALUE = "Date Range";

    private static final String DATE_KEY = "date";
    private static final String DATE_DEFAULT_VALUE = "Date";

    private static final String SIZE_KEY = "size";
    private static final String SIZE_DEFAULT_VALUE = "Size";

    private static final String LANGUAGES_KEY = "languages";
    private static final String LANGUAGES_DEFAULT_VALUE = "Language(s)";


    /**
     * The facets key ascending sort.
     */
    private static final String FACETS_KEY_ASCENDING_SORT = "ascendingSort";
    private static final String FACETS_KEY_ASCENDING_SORT_DEFAULT = "A-Z";
    /**
     * The facets key descending sort.
     */
    private static final String FACETS_KEY_DESCENDING_SORT = "descendingSort";
    private static final String FACETS_KEY_DESCENDING_SORT_DEFAULT = "Z-A";

    /**
     * The facets key relevence.
     */
    private static final String FACETS_KEY_RELEVENCE = "relevance";
    private static final String FACETS_KEY_RELEVENCE_DEFAULT = "Relevance";

    private static final String FACETS_KEY_NEWEST_TO_OLDEST_SORT = "newestToOldest";
    private static final String FACETS_KEY_NEWEST_TO_OLDEST_SORT_DEFAULT_LABEL = "Newest to Oldest";

    /**
     * The facets key apply.
     */
    private static final String FACETS_KEY_APPLY = "apply";
    private static final String FACETS_KEY_APPLY_DEFAULT = "Apply";
    /**
     * The facets key filters.
     */
    private static final String FACETS_KEY_FILTERS = "filters";
    private static final String FACETS_KEY_FILTERS_DEFAULT = "Filters";


    /**
     * The facets key sort.
     */
    private static String FACETS_KEY_SORT = "sort";
    private static String FACETS_KEY_SORT_DEFAULT = "Sort";

    /**
     * The facets key clear filters.
     */
    private static final String FACETS_KEY_CLEAR_FILTERS = "clearFilters";
    private static final String FACETS_KEY_CLEAR_FILTERS_DEFAULT = "Clear Filters";
    /**
     * The facets key narrow results.
     */
    private static String FACETS_KEY_NARROW_RESULTS = "narrowResults";
    private static String FACETS_KEY_NARROW_RESULTS_DEFAULT = "Narrow Results";
    /**
     * The facets key view more.
     */
    private static String FACETS_KEY_VIEW_MORE = "viewMore";
    private static String FACETS_KEY_VIEW_MORE_DEFAULT = "View More";
    private static String FACETS_KEY_VIEW_LESS_DEFAULT = "View Less";
    /**
     * The facets key new.
     */
    private static String FACETS_KEY_NEW = "newKeyword";
    private static String FACETS_KEY_NEW_DEFAULT = "New";

    /**
     * The card key go to.
     */
    private static String CARD_KEY_GO_TO = "goTo";
    private static String CARD_KEY_GO_TO_DEFAULT = "Go To";

    /**
     * The facets key remove filters.
     */
    private static String FACETS_KEY_REMOVE_FILTERS = "removeFilters";
    private static String FACETS_KEY_REMOVE_FILTERS_DEFAULT = "Remove Filters";

    /**
     * sorting values
     */
    private static final String FACETS_KEY_ASCENDING_SORT_DEFAULT_VALUE = "asc";
    private static final String FACETS_KEY_NEWEST_TO_OLDEST_SORT_VALUE = "pub_date_desc";
    private static final String FACETS_KEY_DESCENDING_SORT_DEFAULT_VALUE = "desc";

    /**
     * View Type Constants
     */
    private static final String VIEW_TYPE_GRID = "grid";

    @Self
    private Resource resource;
    @Self
    private SlingHttpServletRequest request;

    @Inject
    private AuthorizationService authorizationService;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    @Via("resource")
    private String enableBulkDownload;

    @Inject
    @Via("resource")
    private String enableGridListView;

    @Inject
    @Via("resource")
    private String enableDateRange;

    @Inject
    @Via("resource")
    private String closeMobileAccordions;

    @Inject
    @Via("resource")
    private String defaultSort;

    @Inject
    @Via("resource")
    private int numberOfRecordsToReturn;

    @Inject
    @Via("resource")
    private String viewType;

    /**
     * The zero search results main message.
     */
    @Inject
    @Via("resource")
    private String zeroSearchResultsMainMessage;

    @Inject
    @ChildResource(name = "whitelistFacets")
    private Resource whitelistFacets;

    private List<String> whitelistFacetsGroups;

    protected List<FacetBean> activeFacetsList;

    private List<FacetConfiguration> advancedSearchFacetWhiteListBeanList;

    /**
     * The faceted header navigation result bean.
     */
    private FacetedHeaderNavigationResultBean facetedHeaderNavigationResultBean;

    /**
     * The country.
     */
    private String country;

    /**
     * The country.
     */
    private String language;

    @Inject
    protected AdminService adminService;
    @Inject
    protected Page currentPage;

    @Inject
    @Source("sling-object")
    private SlingHttpServletRequest slingRequest;

    /**
     * EatonConfig
     */
    @Inject
    protected EndecaConfig endecaConfig;

    @FeatureFlag(resourceType = RESOURCE_TYPE, description = "Description Enabled"
            , tooltipText = "Checked will display the description, default one as long Endeca part is not implemented.")
    private boolean descriptionEnabled;

    private String defaultSortingOption;

    private Boolean isAuthenticated;

    private Long maxAllowedDownloadSize;

    private Long downloadCacheDuration;

    Map<String, String> sortOptionsMap = new HashMap<>();

    {
        sortOptionsMap.put(FACETS_KEY_RELEVENCE, FACETS_KEY_RELEVENCE_DEFAULT);
        sortOptionsMap.put(FACETS_KEY_ASCENDING_SORT_DEFAULT_VALUE, FACETS_KEY_ASCENDING_SORT_DEFAULT);
        sortOptionsMap.put(FACETS_KEY_DESCENDING_SORT_DEFAULT_VALUE, FACETS_KEY_DESCENDING_SORT_DEFAULT);
        sortOptionsMap.put(FACETS_KEY_NEWEST_TO_OLDEST_SORT_VALUE, FACETS_KEY_NEWEST_TO_OLDEST_SORT_DEFAULT_LABEL);
    }

    List<SortByOptionValueBean> sortOptions;

    /**
     * if the resource is null and the request is not null, the resource is set to the resource from the request
     */
    @PostConstruct
    public void postConstruct() {
        if (resource == null && request != null) {
            resource = request.getResource();
        }
    }

    /**
     * Sets the I 18 N values.
     */
    private FacetedHeaderNavigationResultBean withI18nValuesOnNavigationResult(FacetedHeaderNavigationResultBean facetedHeaderNavigationResultBean) {
        LOG.debug("Entry into setI18NValues method");
        facetedHeaderNavigationResultBean.setSortList(getSortByOptionList());
        facetedHeaderNavigationResultBean.setResults(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_RESULTS, FACETS_KEY_RESULTS_DEFAULT));
        facetedHeaderNavigationResultBean.setSort(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_SORT, FACETS_KEY_SORT_DEFAULT));
        facetedHeaderNavigationResultBean.setRelevance(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_RELEVENCE, FACETS_KEY_RELEVENCE_DEFAULT));
        facetedHeaderNavigationResultBean.setAssendingSort(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_ASCENDING_SORT, FACETS_KEY_ASCENDING_SORT_DEFAULT));
        facetedHeaderNavigationResultBean.setDescendingSort(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_DESCENDING_SORT, FACETS_KEY_DESCENDING_SORT_DEFAULT));
        facetedHeaderNavigationResultBean.setClearFilters(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_CLEAR_FILTERS, FACETS_KEY_CLEAR_FILTERS_DEFAULT));
        facetedHeaderNavigationResultBean.setNarrowResults(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_NARROW_RESULTS, FACETS_KEY_NARROW_RESULTS_DEFAULT));
        facetedHeaderNavigationResultBean.setViewMore(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_VIEW_MORE, FACETS_KEY_VIEW_MORE_DEFAULT));
        facetedHeaderNavigationResultBean.setViewLess(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, VGCommonConstants.VIEW_LESS, FACETS_KEY_VIEW_LESS_DEFAULT));
        facetedHeaderNavigationResultBean.setNewKeyword(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_NEW, FACETS_KEY_NEW_DEFAULT));
        facetedHeaderNavigationResultBean.setRemoveFilters(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_REMOVE_FILTERS, FACETS_KEY_REMOVE_FILTERS_DEFAULT));
        facetedHeaderNavigationResultBean.setApply(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_APPLY, FACETS_KEY_APPLY_DEFAULT));
        facetedHeaderNavigationResultBean.setFilters(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FACETS_KEY_FILTERS, FACETS_KEY_FILTERS_DEFAULT));
        facetedHeaderNavigationResultBean.setGoTo(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CARD_KEY_GO_TO, CARD_KEY_GO_TO_DEFAULT));
        return facetedHeaderNavigationResultBean;
    }

    /**
     * Sets the sort by option list.
     *
     * @return the list
     */
    private List<SortByOptionValueBean> getSortByOptionList() {
        if (sortOptions == null) {
            sortOptions = new ArrayList<>();
            for (Map.Entry<String, String> sortOption : sortOptionsMap.entrySet()) {
                sortOptions.add(new SortByOptionValueBean(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, sortOption.getKey(), sortOption.getValue()), sortOption.getKey()));
            }
        }

        return sortOptions;
    }


    /**
     * Gets the faceted header navigation result bean.
     *
     * @return the faceted header navigation result bean
     */
    public FacetedHeaderNavigationResultBean getFacetedHeaderNavigationResultBean() {
        if (facetedHeaderNavigationResultBean == null) {
            facetedHeaderNavigationResultBean = withI18nValuesOnNavigationResult(new FacetedHeaderNavigationResultBean());
        }
        return facetedHeaderNavigationResultBean;
    }

    public String getDefaultSortingOption() {
        if (defaultSortingOption == null) {
            Optional<SortByOptionValueBean> defaultSortOptional = sortOptions.stream().filter(sortOption -> Objects.equals(defaultSort, sortOption.getValue())).findFirst();
            defaultSortOptional.ifPresent(sortByOptionValueBean -> defaultSortingOption = sortByOptionValueBean.getLabel());
        }
        return defaultSortingOption;
    }

    public String getEnableBulkDownload() {
        return enableBulkDownload;
    }

    public Boolean isBulkDownloadEnabled() {
        if (enableBulkDownload == null) {
            if (resource != null) {
                String enableBulkDownload = resource.getValueMap().get("enableBulkDownload", "false");
                return Boolean.parseBoolean(enableBulkDownload);
            }
            return Boolean.FALSE;
        }
        return Boolean.parseBoolean(enableBulkDownload);
    }

    public String getFromText() {
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, FROM_KEY, FROM_DEFAULT_VALUE);
    }

    public String getToText() {
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, TO_KEY, TO_DEFAULT_VALUE);
    }

    public String getDateRangeText() {
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, DATE_RANGE_KEY, DATE_RANGE_DEFAULT_VALUE);
    }

    public String getDateText() {
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, DATE_KEY, DATE_DEFAULT_VALUE);
    }

    public String getSizeText() {
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, SIZE_KEY, SIZE_DEFAULT_VALUE);
    }

    public String getLanguagesText() {
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, LANGUAGES_KEY, LANGUAGES_DEFAULT_VALUE);
    }

    public String getGridViewText() {
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, VIEW_KEY_GRID, VIEW_KEY_GRID_DEFAULT);
    }

    public String getListViewText() {
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, VIEW_KEY_LIST, VIEW_KEY_LIST_DEFAULT);
    }

    public String getDownloadText() {
        return CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, DOWNLOAD_KEY, DOWNLOAD_DEFAULT_VALUE);
    }


    public String getEnableGridListView() {
        return enableGridListView;
    }

    public String getEnableDateRange() {
        return enableDateRange;
    }

    public String getCloseMobileAccordions() {
        return closeMobileAccordions;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        if (language == null) {
            Locale languageValue = getCurrentPage().getLanguage();
            if (languageValue != null) {
                language = languageValue.getLanguage();
            }
        }
        return language;
    }

    @Override
    protected SlingHttpServletRequest getSlingHttpServletRequest() {
        return slingRequest;
    }

    public Page getCurrentPage() {
        if (currentPage == null) {
            ResourceResolver resourceResolver = Objects.requireNonNull(resource).getResourceResolver();
            Resource containingPageRsource = resourceResolver.getResource(resource.getPath().substring(0, resource.getPath().indexOf("/jcr:content")));
            currentPage = Objects.requireNonNull(containingPageRsource).adaptTo(Page.class);
        }
        return currentPage;
    }

    public boolean isAuthenticated() {
        if (isAuthenticated == null) {
            AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
            if (authenticationToken != null && (authenticationToken.getUserProfile() != null || authenticationToken.getBypassAuthorization())) {
                isAuthenticated = true;
            }
        }
        return isAuthenticated;
    }

    public int getNumberOfRecordsToReturn() {
        return numberOfRecordsToReturn;
    }

    public String getViewType() {
        if (viewType != null) {
            return viewType;
        }
        return VIEW_TYPE_GRID;
    }

    public List<FacetConfiguration> getAdvancedSearchFacetWhiteListBeanList() {
        if (whitelistFacets == null) {
            return Collections.emptyList();
        }
        if (advancedSearchFacetWhiteListBeanList == null) {
            advancedSearchFacetWhiteListBeanList = new ArrayList<>();
            whitelistFacets.getChildren().forEach(child -> {
                advancedSearchFacetWhiteListBeanList.add(child.adaptTo(NewAdvancedSearchFacetConfiguration.class));
            });
        }
        return advancedSearchFacetWhiteListBeanList;
    }

    /**
     * Gets the zero search results main message.
     *
     * @return the zero search results main message
     */
    public String getZeroSearchResultsMainMessage() {
        return zeroSearchResultsMainMessage;
    }

    public String getDefaultSort() {
        return defaultSort;
    }

    public List<String> getWhitelistFacetGroups() {
        if (whitelistFacetsGroups == null) {
            whitelistFacetsGroups = createFacetTags();
        }
        return whitelistFacetsGroups;
    }

    private List<String> createFacetTags() {
        final List<String> facetTags = new ArrayList<>();
        List<String> authoredFacetTags = new ArrayList<>();

        if (whitelistFacets != null) {
            for (Resource tagAttrResource : whitelistFacets.getChildren()) {
                String facetTagName = tagAttrResource.getValueMap().get("facet", "");
                if (!facetTagName.isEmpty()) {
                    authoredFacetTags.add(facetTagName);
                }
            }
        }

        if (authoredFacetTags != null) {
            facetTags.add(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL);
            for (String authoredFacetTag : authoredFacetTags) {
                Tag facetTag = resource.getResourceResolver().adaptTo(TagManager.class).resolve(authoredFacetTag);
                if (facetTag != null) {
                    EndecaFacetTag endecaFacetTag = facetTag.adaptTo(EndecaFacetTag.class);
                    if (endecaFacetTag != null && endecaFacetTag.hasFacetId()) {
                        facetTags.add(endecaFacetTag.getFacetId());
                    }
                }
            }
        }

        return facetTags;
    }

}
