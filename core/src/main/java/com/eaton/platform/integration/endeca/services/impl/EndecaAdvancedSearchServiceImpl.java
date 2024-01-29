package com.eaton.platform.integration.endeca.services.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.core.models.EndecaFacetTag;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.models.secure.AdvancedSearchModel;
import com.eaton.platform.core.models.secure.WhatsNewModel;
import com.eaton.platform.core.search.api.FacetConfiguration;
import com.eaton.platform.core.search.pojo.AssetSearchResponse;
import com.eaton.platform.core.search.service.SearchResponseMappingService;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.FilterUtil;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.factories.SecureFilterBeanFactory;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.pojo.asset.EndecaAssetResponse;
import com.eaton.platform.integration.endeca.services.EndecaAdvancedSearchService;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.integration.endeca.services.MyEatonConfig;
import com.eaton.platform.integration.endeca.util.EndecaUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.mime.MimeTypeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <html> Description: This class is used to get data from Endeca.
 *
 * @author ICF
 * @version 1.0
 * @since 2021
 */

@Component(service = EndecaAdvancedSearchService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Endeca Advanced Search Service",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "EndecaAdvancedSearchServiceImpl"
        })
@Designate(ocd = MyEatonConfig.class)
public class EndecaAdvancedSearchServiceImpl implements EndecaAdvancedSearchService {


    private static final Logger LOG = LoggerFactory.getLogger(EndecaAdvancedSearchServiceImpl.class);
    @Reference
    private AdminService adminService;

    @Reference
    protected EndecaService endecaService;

    @Reference
    private EndecaConfig endecaConfig;

    @Reference
    private transient MimeTypeService mimeTypeService;

    @Reference
    private AuthorizationService authorizationService;


    @Reference
    private EatonSiteConfigService eatonSiteConfigService;

    @Reference
    protected EndecaConfig endecaConfigService;
    @Reference(target = "(serviceName=" + EndecaAssetSearchResponseMappingService.SERVICE_NAME + ")")
    private SearchResponseMappingService<AssetSearchResponse, EndecaAssetResponse> endecaAdvancedSearchResponseMappingService;

    private static final String GLOBAL_STRING = "GLOBAL";

    private static final String GLOBAL_DEFAULT_STRING = "Global";

    private static final String MULTILINGUAL_STRING = "Multilingual";

    private static final String LANGUAGE = "language";

    private static final String OPEN_PARANTHESIS_STRING = "(";

    private static final String JCR_CONSTANT_STRING = "/jcr:content";

    private MyEatonConfig myEatonConfig;

    @Activate
    protected void activate(MyEatonConfig config) {
        myEatonConfig = config;
    }

    /**
     * This method will return the search results
     *
     * @param endecaServiceRequestBean
     * @return Search results
     */
    @Override
    public JSONObject getAdvanceSearchResults(EndecaServiceRequestBean endecaServiceRequestBean, SlingHttpServletRequest request) {

        JSONObject responseObject = null;
        SiteSearchResponse siteSearchResponse = null;
        final AdvancedSearchModel oldAdvancedSearchModel = request.adaptTo(AdvancedSearchModel.class);

        try (ResourceResolver resourceResolver = adminService.getReadService()) {
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            boolean isUnitedStatesDateFormat = false;
            if (null != oldAdvancedSearchModel) {
                SiteResourceSlingModel siteResourceSlingModel = getSiteConfig(oldAdvancedSearchModel.getCurrentPage());
                if (null != siteResourceSlingModel) {
                    isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.getUnitedStatesDateFormat());
                }
                siteSearchResponse = endecaService.getSearchResults(endecaServiceRequestBean, request, isUnitedStatesDateFormat);
                if (null != siteSearchResponse && null != siteSearchResponse.getPageResponse()) {
                    extractFacetGroups(siteSearchResponse, oldAdvancedSearchModel.getCurrentPage(), tagManager, request, oldAdvancedSearchModel.getAdvancedSearchFacetWhiteListBeanList());
                    endecaServiceRequestBean.setLanguage(CommonUtil.getUpdatedLocaleFromPagePath(oldAdvancedSearchModel.getCurrentPage()));
                    responseObject = new JSONObject(siteSearchResponse.getPageResponse());
                    updateSearchResultResponse(responseObject, endecaServiceRequestBean, oldAdvancedSearchModel, isUnitedStatesDateFormat, request);
                    updateSearchFacets(responseObject, oldAdvancedSearchModel.getAdvancedSearchFacetWhiteListBeanList(), oldAdvancedSearchModel.getCurrentPage());
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class getAdvanceSearchResults method {} ", e.getMessage());
        } finally {
            // In case there is any exception the bean would be returned with
            // fail status
            if (responseObject == null) {
                responseObject = new JSONObject();
                try {
                    responseObject.put("status", EndecaConstants.FAIL_STRING);
                } catch (JSONException e) {
                    LOG.error("JSONException in EndecaAdvancedSearchServiceImpl class getAdvanceSearchResults method {} ", e.getMessage());
                }

            }
        }
        LOG.debug("Exit from getAdvanceSearchResults method ");
        return responseObject;
    }

    /**
     * Method to construct Endeca Request Object
     *
     * @param
     * @param request
     * @return
     */
    @Override
    public EndecaServiceRequestBean constructEndecaRequestBean(SlingHttpServletRequest request) {
        final EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
        final String[] selectors = request.getRequestPathInfo().getSelectors();
        final Map<String, List<String>> requestParameterMap = getParameterMapFromSelectors(selectors);
        final AdvancedSearchModel oldAdvancedSearchModel = request.adaptTo(AdvancedSearchModel.class);

        if (null != endecaConfig) {
            final EndecaConfigServiceBean configServiceBean = endecaConfig.getConfigServiceBean();
            endecaServiceRequestBean.setSearchApplicationKey(configServiceBean.getEspAppKey());
            endecaServiceRequestBean.setSearchApplication(EndecaConstants.SECURE_ASSET_EATON_SEARCH);
            endecaServiceRequestBean.setFunction(EndecaConstants.SEARCH_STRING);
        }
        if (null != oldAdvancedSearchModel) {
            Page currentPage = oldAdvancedSearchModel.getCurrentPage();
            String country = oldAdvancedSearchModel.getCountry();
            List<String> facetGroups = new ArrayList<>();
            Integer numberOfRecordsToReturn = 0;
            endecaServiceRequestBean.setLanguage(EndecaConstants.SEARCH_TERM_IGNORE);
            SiteResourceSlingModel siteConfiguration = getSiteConfig(currentPage);

            List<String> whitelistFacets = oldAdvancedSearchModel.getWhitelistFacetGroups();

            if (!whitelistFacets.isEmpty()) {
                facetGroups = whitelistFacets;
            }
            if (whitelistFacets.isEmpty() && (siteConfiguration != null && (siteConfiguration.getSiteSearchFacetGroups() != null)
                    && (!siteConfiguration.getSiteSearchFacetGroups().isEmpty()))) {
                facetGroups = siteConfiguration.getSiteSearchFacetGroups();
            } else {
                // Do Nothing
            }
            //set number of records to return
            if (siteConfiguration != null && (siteConfiguration.getPageSize() != 0)) {
                numberOfRecordsToReturn = siteConfiguration.getPageSize();
            }
            endecaServiceRequestBean.setNumberOfRecordsToReturn(numberOfRecordsToReturn.toString());

            // set filters
            endecaServiceRequestBean.setSearchApplication(SecureModule.SECUREASSETSEARCH.getAppId());
            setEndecaRequestFilters(request, endecaServiceRequestBean, requestParameterMap, facetGroups, country, SecureModule.SECUREASSETSEARCH, currentPage);

            //set search term
            if (requestParameterMap.containsKey(EndecaConstants.SEARCH_TERM) && !requestParameterMap.get(EndecaConstants.SEARCH_TERM).get(0).isEmpty()) {
                String decodedSearchTerm = CommonUtil.decodeSearchTermString(requestParameterMap.get(EndecaConstants.SEARCH_TERM).get(0));
                endecaServiceRequestBean.setSearchTerms(decodedSearchTerm);
            } else {
                endecaServiceRequestBean.setSearchTerms(EndecaConstants.SEARCH_TERM_IGNORE);
            }

            //set starting record number
            if (requestParameterMap.containsKey(EndecaConstants.LOAD_MORE_OFFSET) && !requestParameterMap.get(EndecaConstants.LOAD_MORE_OFFSET).get(0).isEmpty()) {
                endecaServiceRequestBean.setStartingRecordNumber(requestParameterMap.get(EndecaConstants.LOAD_MORE_OFFSET).get(0));
            } else {
                endecaServiceRequestBean.setStartingRecordNumber("0");
            }
        }
        return endecaServiceRequestBean;
    }

    /**
     * Method to construct Endeca Request Object
     *
     * @param
     * @param request
     * @return
     */
    @Override
    public EndecaServiceRequestBean constructWhatsNewEndecaRequestBean(SlingHttpServletRequest request, Long startDate, Long endDate) {
        final EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
        final Map<String, List<String>> requestParameterMap = getWhatsNewEndecaFilterOptions(startDate, endDate, EndecaConstants.PUBLICATION_DATE_DESCENDING);
        final WhatsNewModel whatsNewModel = request.adaptTo(WhatsNewModel.class);

        if (null != endecaConfig && whatsNewModel != null) {
            final EndecaConfigServiceBean configServiceBean = endecaConfig.getConfigServiceBean();
            endecaServiceRequestBean.setSearchApplicationKey(configServiceBean.getEspAppKey());
            endecaServiceRequestBean.setSearchApplication(EndecaConstants.SECURE_ASSET_EATON_SEARCH);
            endecaServiceRequestBean.setFunction(EndecaConstants.SEARCH_STRING);
            String country = whatsNewModel.getCountry();
            List<String> facetGroups = new ArrayList<>();
            endecaServiceRequestBean.setLanguage(CommonUtil.getUpdatedLocaleFromPagePath(whatsNewModel.getCurrentPage()));
            SiteResourceSlingModel siteConfiguration = getSiteConfig(whatsNewModel.getCurrentPage());
            if (siteConfiguration != null && (siteConfiguration.getSiteSearchFacetGroups() != null)
                    && (!siteConfiguration.getSiteSearchFacetGroups().isEmpty())) {
                facetGroups = siteConfiguration.getSiteSearchFacetGroups();
            }
            //set number of records to return
            endecaServiceRequestBean.setStartingRecordNumber(EndecaConstants.ZERO);
            endecaServiceRequestBean.setNumberOfRecordsToReturn(EndecaConstants.LIMIT_COUNT_TWO_THOUSAND);
            // set filters
            setEndecaRequestFilters(request, endecaServiceRequestBean, requestParameterMap, facetGroups, country, SecureModule.SECUREASSETSEARCH,
                    whatsNewModel.getCurrentPage());
            endecaServiceRequestBean.getFilters().add(FilterUtil.getFilterBeanList(EndecaConstants.FACETS_STRING_WITH_F, Arrays.asList(StringUtils.EMPTY)));
            if (requestParameterMap.containsKey(EndecaConstants.PUBLICATION_DATE)) {
                endecaServiceRequestBean.getFilters().add(FilterUtil.getFilterBeanList(EndecaConstants.PUBLICATION_DATE, requestParameterMap.get(EndecaConstants.PUBLICATION_DATE)));
            }
            //set search term
            endecaServiceRequestBean.setSearchTerms(EndecaConstants.SEARCH_TERM_IGNORE);
        }
        return endecaServiceRequestBean;
    }

    /**
     * Method to get SiteConfig object based on the current page
     *
     * @param currentPage
     * @return siteConfig Object
     */
    private SiteResourceSlingModel getSiteConfig(Page currentPage) {
        if (null != currentPage) {
            Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);
            if (siteConfig.isPresent()) {
                return siteConfig.get();
            }
        }
        return null;
    }

    /**
     * Method to extract Facet groups based on the SiteConfig
     *
     * @param siteSearchResponse
     * @param currentPage
     * @param tagManager
     */
    private void extractFacetGroups( SiteSearchResponse siteSearchResponse, Page currentPage,TagManager tagManager, final SlingHttpServletRequest slingRequest,List<FacetConfiguration> advancedSearchModelList) {
        List<FacetGroupBean> facetGroupList = siteSearchResponse.getPageResponse().getFacets().getFacetGroupList();
        List<FacetGroupBean> updatedFacetGroupList = new ArrayList<>();
        if (facetGroupList != null) {
            for (int facetGrp = 0; facetGrp < facetGroupList.size(); facetGrp++) {
                FacetGroupBean facetGroupBean = facetGroupList.get(facetGrp);
                String facetGroupID = facetGroupBean.getFacetGroupId();
                facetGroupBean.setFacetTempGroupId(facetGroupID);
                /*Truncates FacetGroupId value with anything which has ':' - Example : eaton:language_EN_US
                 Same id's is been used in the FE to avoid errors, escpaing the before text(eaton:)*/
                facetGroupBean.setFacetGroupId(facetGroupID.substring(facetGroupID.indexOf(EndecaConstants.COLON) + 1));
                if (!facetGroupID.equals(EndecaConstants.CONTENT_TYPE_STRING)) {
                    updatedFacetGroupList.add(facetGroupBean);
                }
            }
            // '0' means facets not whitelisted and extract from site config
            if (advancedSearchModelList.isEmpty()) {
                facetGroupList = updateSearchFacetGroupsBySiteConfigs(updatedFacetGroupList, currentPage);
            } else {
                facetGroupList = updateSearchFacetGroupsByComponentConfigs(updatedFacetGroupList, advancedSearchModelList);
            }
            updateFacetValueLabels(facetGroupList, currentPage, tagManager, slingRequest);

            LOG.info("Updated facetGroupList : {}", facetGroupList);
            siteSearchResponse.getPageResponse().getFacets().setFacetGroupList(facetGroupList);
        }
    }

    /**
     * Method to update response facets group by site configs
     *
     * @param updatedFacetGroupList
     * @param currentPage
     * @return
     */
    private List<FacetGroupBean> updateSearchFacetGroupsBySiteConfigs(final List<FacetGroupBean> updatedFacetGroupList, Page currentPage) {
        List<FacetGroupBean> configuredFacetGroupsList = null;
        SiteResourceSlingModel siteResourceSlingModel = getSiteConfig(currentPage);
        if (null != siteResourceSlingModel) {
            configuredFacetGroupsList = siteResourceSlingModel.getFacetGroupsList();
        }
        List<FacetGroupBean> localFacetGroupBeanList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(updatedFacetGroupList) && CollectionUtils.isNotEmpty(configuredFacetGroupsList)) {
            for (FacetGroupBean configFacetGroupBean : configuredFacetGroupsList) {
                for (FacetGroupBean facetGroupBean : updatedFacetGroupList) {
                    if (StringUtils.equalsIgnoreCase(facetGroupBean.getFacetGroupId(), configFacetGroupBean.getFacetGroupId())) {
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
        if (CollectionUtils.isEmpty(localFacetGroupBeanList)) {
            localFacetGroupBeanList = updatedFacetGroupList;
        }
        return localFacetGroupBeanList;
    }

    /**
     * Method to update response facets group by Component Whitelisted configs
     *
     * @param updatedFacetGroupList
     * @param advancedSearchFacetWhiteListBeans
     * @return FacetGroupBean
     */
    private static List<FacetGroupBean> updateSearchFacetGroupsByComponentConfigs(final List<FacetGroupBean> updatedFacetGroupList,
                                                                                  List<FacetConfiguration> advancedSearchFacetWhiteListBeans) {
        List<FacetGroupBean> localFacetGroupBeanList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(updatedFacetGroupList)){
            for (FacetConfiguration configFacetGroupBean  : advancedSearchFacetWhiteListBeans ) {
                for (FacetGroupBean  facetGroupBean : updatedFacetGroupList) {
					if (StringUtils.equalsIgnoreCase(facetGroupBean.getFacetGroupId(), configFacetGroupBean.getFacet())) {
						localFacetGroupBeanList.add(facetGroupBean);
						break;
					}
                }
            }
        }
        if (CollectionUtils.isEmpty(localFacetGroupBeanList)) {
            localFacetGroupBeanList = updatedFacetGroupList;
        }
        return localFacetGroupBeanList;
    }

    /**
     * Updates the facet groups with labels updated based upon tag titles if a corresponding tag was found. Otherwise
     * the provided label remains the same. Both scenarios are expected. We attempt to convert the label if possible.
     *
     * @param facetGroupBeans The facet group beans for which you want to update the facet values labels.
     */
    private static void updateFacetValueLabels(List<FacetGroupBean> facetGroupBeans, Page currentPage, TagManager tagManager, final SlingHttpServletRequest slingRequest) {
        Iterator<FacetGroupBean> itrFacetGroupBean = facetGroupBeans.iterator();
        if (itrFacetGroupBean != null) {
            while (itrFacetGroupBean.hasNext()) {
                FacetGroupBean facetGroupBean = itrFacetGroupBean.next();
                if (facetGroupBean.getFacetGroupId() != null && facetGroupBean.getFacetTempGroupId() != null) {
                    final Tag tag = tagManager.resolve(EndecaFacetTag.convertToTagId(facetGroupBean.getFacetTempGroupId().toLowerCase()));
                    if (tag != null) {
                        final EndecaFacetTag endecaFacetTag = tag.adaptTo(EndecaFacetTag.class);
                        if (endecaFacetTag != null) {
                            facetGroupBean.setFacetGroupLabel(endecaFacetTag.getLocalizedFacetLabel(currentPage.getLanguage(false)));
                        }
                    } else if (facetGroupBean.getFacetGroupId().contains(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL)) {
                        facetGroupBean.setFacetGroupLabel(CommonUtil.getI18NFromLocale(slingRequest, EndecaConstants.I18N_LABEL_PREFIX + facetGroupBean.getFacetGroupLabel(), currentPage.getLanguage(false)));
                    } else {
                        LOG.debug("Non-Secure Facet Found : {}", facetGroupBean.getFacetGroupId());
                    }
                    // Remove the facetGroup item from the list, if the size of facetValueslables is 0.
                    if (tag != null && checkIfFacetsAreValid(facetGroupBean, tagManager, currentPage, slingRequest, tag)) {
                        LOG.debug("Empty Facet Group Found, removed from the list : {}", facetGroupBean.getFacetGroupLabel());
                        itrFacetGroupBean.remove();
                    }
                }
            }
        }
    }

    /**
     * @param facetGroupBean
     * @param tagManager
     * @param currentPage
     * @param slingRequest
     * @return Check if facets are valid, means exist in AEM as Tag. It removes the item from
     * the list when tagManager returns null. Return true if overall size > 0 else false.
     */
    private static boolean checkIfFacetsAreValid(
            FacetGroupBean facetGroupBean,
            TagManager tagManager,
            Page currentPage,
            SlingHttpServletRequest slingRequest,
            Tag tag) {
        LOG.debug("Entered into the method checkIfFacetsAreValid() -- Start");
        if (facetGroupBean.getFacetGroupId().contains(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY)) {
            List<FacetValueBean> facetList = new ArrayList<>();
            List<FacetValueBean> responseValueList = facetGroupBean.getFacetValueList();
            Iterator<Tag> it = tag.listChildren();
            while (it.hasNext()) {
                Tag childTag = it.next();
                if (hasDocuments(responseValueList, childTag.getName())) {
                    Locale locale = currentPage.getLanguage(false);
                    FacetValueBean facetValueBean = getFacetValueBean(facetGroupBean, childTag, locale);
                    if (facetValueBean != null) {
                        facetList.add(facetValueBean);
                    }
                }
            }
            facetGroupBean.setFacetValueList(facetList);
        } else {
            Iterator<FacetValueBean> itrFacetValueBean = facetGroupBean.getFacetValueList().iterator();
            if (null != itrFacetValueBean) {
                while (itrFacetValueBean.hasNext()) {
                    FacetValueBean facetValueBean = itrFacetValueBean.next();
                    String facetGroupId;
                    // Lower the facetGroupId if the type of the facet is Language to match with AEM TagId
                    if (facetGroupBean.getFacetTempGroupId().contains(EndecaConstants.LANGUAGE_STRING)) {
                        facetGroupId = facetGroupBean.getFacetTempGroupId().toLowerCase();
                    } else {
                        facetGroupId = facetGroupBean.getFacetTempGroupId();
                    }
                    String tagName = facetGroupId.concat(EndecaFacetTag.ID_SEPARATOR).concat(facetValueBean.getFacetValueLabel());
                    String tagId = EndecaFacetTag.convertToTagId(tagName);
                    boolean isSecureFacet = facetGroupBean.getFacetGroupId().equalsIgnoreCase(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL);
                    if (CommonUtil.isValidAEMTag(tagManager, tagId) || isSecureFacet) {
                        updateFacetValueLabel(facetGroupId, facetValueBean, currentPage, tagManager, slingRequest);
                    } else {
                        // Removes item from the list if the facet tag is invalid
                        LOG.debug("Invalid Tag Found : {}", facetValueBean.getFacetValueLabel());
                        LOG.debug("Group ID: {}", facetGroupBean.getFacetTempGroupId());
                        itrFacetValueBean.remove();
                    }
                }
            }
        }
        LOG.debug("Entered into the method checkIfFacetsAreValid() -- END");
        // 0 means - none of the facets are valid under a facetgroup
        return facetGroupBean.getFacetValueList().isEmpty();
    }

    private static boolean hasDocuments(List<FacetValueBean> responseValueList, String sublevel) {
        for (FacetValueBean subFacet : responseValueList) {
            if (subFacet.getFacetValueLabel().equals(sublevel)) {
                return true;
            }
        }
        return false;
    }

    private static FacetValueBean getFacetValueBean(FacetGroupBean facetGroupBean, Tag childTag, Locale locale) {
        FacetValueBean facetValueBean = null;
        Iterator<FacetValueBean> itrFacetValueBean = facetGroupBean.getFacetValueList().iterator();
        if (null != itrFacetValueBean) {
            while (itrFacetValueBean.hasNext()) {
                FacetValueBean facet = itrFacetValueBean.next();
                if (facet.getFacetValueLabel().equals(childTag.getName())) {
                    facetValueBean = facet;
                    facetValueBean.setFacetValueLabel(childTag.getTitle(locale));
                }
            }
        }
        return facetValueBean;
    }

    /**
     * Updates the facet value label based upon the facet group tag id and the current label of the facet value bean. If
     * these two combine to provide an Endeca facet tag id then this is used to find the corresponding tag and the localized
     * title of that tag is used. Otherwise the facet label is used as is. Both scenarios are valid.
     *
     * @param facetGroupTagId The facet group to search within in the format defined by EndecaFacetTag.getFacetId
     * @param facetValueBean  The facet value bean to update.
     */
    private static void updateFacetValueLabel(String facetGroupTagId, FacetValueBean facetValueBean, Page currentPage, TagManager tagManager, final SlingHttpServletRequest slingRequest) {
        if (null != currentPage) {
            final Locale locale = currentPage.getLanguage(false);
            final String tagId = facetGroupTagId.equalsIgnoreCase(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL) ? null :
                    EndecaFacetTag.convertToTagId(facetGroupTagId.toLowerCase() + EndecaFacetTag.ID_SEPARATOR + facetValueBean.getFacetValueLabel());
            if (tagId != null && tagManager != null && locale != null) {
                final Tag facetValueTag = tagManager.resolve(tagId);
                if (facetValueTag != null) {
                    facetValueBean.setFacetValueLabel(facetValueTag.getTitle(locale));
                }
            }
            if (facetGroupTagId.contains(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL)) {
                facetValueBean.setFacetValueLabel(CommonUtil.getI18NFromLocale(slingRequest, EndecaConstants.SECURE_ONLY_FILTER_VALUE, locale));
            } else {
                // Do Nothing
            }
        }
    }

    /**
     * Method to add language,loadmoreOffset,facetsViewMoreOffset,filtTypeAndSize,DefaultImage and downloadflag to the response Object
     *
     * @param responseObject
     * @param endecaServiceRequestBean
     */
    private void updateSearchResultResponse(JSONObject responseObject, EndecaServiceRequestBean endecaServiceRequestBean, AdvancedSearchModel oldAdvancedSearchModel, boolean isUnitedStatesDateFormat, SlingHttpServletRequest request) {
        try {

            setLoadMoreOffset(responseObject, endecaServiceRequestBean);
            setFacetsViewMoreOffset(responseObject, oldAdvancedSearchModel);
            final JSONArray searchResult = responseObject.has("siteSearchResults") ? responseObject.getJSONArray("siteSearchResults") : new JSONArray();
            for (int i = 0; i < searchResult.length(); i++) {
                final JSONObject resultJSONObject = searchResult.getJSONObject(i);
                setFileTypeAndSize(resultJSONObject);
                setDefaultImage(resultJSONObject);
                setDownloadFlag(resultJSONObject, oldAdvancedSearchModel);
                setId(resultJSONObject);
                setEpochPublicationDate(resultJSONObject, isUnitedStatesDateFormat);
                setLanguage(resultJSONObject, request);
            }
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class updateSearchResultResponse method {} ", e.getMessage());
        }
    }

    /**
     * This method iterates facetsGroup JSON Array object from the response and checks if the facetValueDocs value is '0'.
     * If '0' - Add new attribute 'facetSelected' to 'checked'.
     * This helps FE to handle to check and uncheck for dynamic facets.
     *
     * @param responseObject
     */
    private void  updateSearchFacets(JSONObject responseObject, List<FacetConfiguration> advancedSearchFacetWhiteListBeans, Page currentPage){
        try {
            final JSONObject facetsJSONObj = responseObject.has(EndecaConstants.FACETS_STRING) && responseObject.getJSONObject(EndecaConstants.FACETS_STRING).length() > 0
                    ? responseObject.getJSONObject(EndecaConstants.FACETS_STRING) : new JSONObject();
            JSONArray facetGroupJSONArray = facetsJSONObj.has(EndecaConstants.FACET_GROUP_LIST) && facetsJSONObj.getJSONArray(EndecaConstants.FACET_GROUP_LIST).length() > 0
                    ? facetsJSONObj.getJSONArray(EndecaConstants.FACET_GROUP_LIST) : new JSONArray();
            if (facetGroupJSONArray.length() > 0) {
                for (int i = 0; i < facetGroupJSONArray.length(); i++) {
                    JSONObject facetGroupItem = facetGroupJSONArray.getJSONObject(i);
                    if (facetGroupItem != null && facetGroupItem.has(EndecaConstants.FACET_GROUP_VALUE_LIST)) {
                        JSONArray facetValueJSONArray = facetGroupItem.getJSONArray(EndecaConstants.FACET_GROUP_VALUE_LIST);
                        updateFacetValue(facetValueJSONArray);
                    }
                    updateFacetFieldTypes(facetGroupItem,advancedSearchFacetWhiteListBeans,currentPage);
                }
            }
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class updateSearchFacets method {} ", e.getMessage());
        }
    }


    /**
     * @param facetGroupItem
     * @param advancedSearchFacetWhiteListBeans
     * @param currentPage
     */
    private void updateFacetFieldTypes(JSONObject facetGroupItem,List<FacetConfiguration> advancedSearchFacetWhiteListBeans, Page currentPage ){
        if(!advancedSearchFacetWhiteListBeans.isEmpty()) {
            EndecaUtil.updateFacetFieldTypeFromComponentConfig(facetGroupItem, advancedSearchFacetWhiteListBeans);
        }else{
            SiteResourceSlingModel siteConfiguration = getSiteConfig(currentPage);
            if (siteConfiguration != null) {
                EndecaUtil.updateFacetFieldTypeFromSiteConfig(facetGroupItem, siteConfiguration.getFacetGroupsList());
            }
        }
    }

    /**
     * @param facetValueJSONArray
     */
    private static void updateFacetValue(JSONArray facetValueJSONArray) {
        for (int i = 0; i < facetValueJSONArray.length(); i++) {
            try {
                JSONObject facetValue = facetValueJSONArray.getJSONObject(i);
                if (facetValue != null && facetValue.length() > 0) {
                    int docsCount = facetValue.getInt(EndecaConstants.FACET_VALUE_DOCS);
                    if (docsCount == 0) {
                        facetValue.put(EndecaConstants.FACET_SELECTED, EndecaConstants.FACET_VALUE_CHECKED);
                    }
                }
            } catch (JSONException e) {
                LOG.error("Exception in EndecaAdvancedSearchServiceImpl class updateFacetValue method {} ", e.getMessage());
            }
        }

    }

    /**
     * Method to convert epoch date format to dd/MM/yyyy
     *
     * @param resultJSONObject
     */
    private static void setEpochPublicationDate(JSONObject resultJSONObject, boolean isUnitedStatesDateFormat) {
        try {
            if (resultJSONObject.has(EndecaConstants.EPOCH_PUBLICATION_DATE) && !resultJSONObject.getString(EndecaConstants.EPOCH_PUBLICATION_DATE).isEmpty()) {
                final String epocDate = resultJSONObject.getString(EndecaConstants.EPOCH_PUBLICATION_DATE);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
                if (isUnitedStatesDateFormat) {
                    simpleDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
                }
                final Date date = new Date(Long.parseLong(epocDate));
                resultJSONObject.put("publishDate", simpleDateFormat.format(date));
            }
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class setDefaultImage method {} ", e.getMessage());
        }
    }

    /**
     * Method to add id in the response each records
     * creates id based on the url
     *
     * @param resultJSONObject
     */
    private static void setId(JSONObject resultJSONObject) {
        try {
            if (resultJSONObject.has(EndecaConstants.URL) && !resultJSONObject.getString(EndecaConstants.URL).isEmpty()) {
                final String id = CommonUtil.generateHashValue(resultJSONObject.getString(EndecaConstants.URL));
                resultJSONObject.put("id", id);
            }
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class setLoadMoreOffset method {} ", e.getMessage());
        }
    }

    /**
     * Method to add default number of facets count to show on load
     *
     * @param responseObject
     * @param oldAdvancedSearchModel
     */
    private void setFacetsViewMoreOffset(JSONObject responseObject, AdvancedSearchModel oldAdvancedSearchModel) {
        SiteResourceSlingModel siteResourceSlingModel = getSiteConfig(oldAdvancedSearchModel.getCurrentPage());
        try {
            if (null != siteResourceSlingModel) {
                int facetValueCount = siteResourceSlingModel.getFacetValueCount();
                responseObject.put("facetsViewMoreOffset", facetValueCount);
            }
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class setFacetsViewMoreOffset method {} ", e.getMessage());
        }
    }

    /**
     * Method to setDownloadflag -
     * Set to true - If bulkdownload is enabled by the author and filetype is not equal to text/html
     * set to false - If bulkdownload is not enabled by the author or filetype is equal to text/html
     *
     * @param resultJSONObject
     * @param oldAdvancedSearchModel
     */
    private static void setDownloadFlag(JSONObject resultJSONObject, AdvancedSearchModel oldAdvancedSearchModel) {
        boolean downloadEnabled = false;
        final String enableBulkDownload = oldAdvancedSearchModel.getEnableBulkDownload();
        try {
            if (null != enableBulkDownload && CommonConstants.TRUE.equals(enableBulkDownload)){
                downloadEnabled = true;
                if (resultJSONObject.has(EndecaConstants.FILE_TYPE) && resultJSONObject.getString(EndecaConstants.FILE_TYPE).equals(CommonConstants.FILE_TYPE_TEXT_HTML)) {
                    downloadEnabled = false;
                }
            }
            resultJSONObject.put(EndecaConstants.DOWNLOAD_ENABLED, downloadEnabled);
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class setFileTypeAndSize method {} ", e.getMessage());
        }
    }

    /**
     * Method to set loadMoreOffset in the response object
     *
     * @param responseObject
     * @param endecaServiceRequestBean
     */
    private static void setLoadMoreOffset(JSONObject responseObject, EndecaServiceRequestBean endecaServiceRequestBean) {
        try {
            if (responseObject.has(EndecaConstants.TOTAL_COUNT_STRING) && responseObject.getInt(EndecaConstants.TOTAL_COUNT_STRING) > 0) {
                final int resultCount = responseObject.getInt(EndecaConstants.TOTAL_COUNT_STRING);
                final int startingRecordNumber = Integer.parseInt(endecaServiceRequestBean.getStartingRecordNumber());
                final int numberOfRecordsToReturn = Integer.parseInt(endecaServiceRequestBean.getNumberOfRecordsToReturn());
                if (resultCount > (startingRecordNumber + numberOfRecordsToReturn) && null != responseObject) {
                    int loadmoreNewCount = startingRecordNumber + numberOfRecordsToReturn;
                    responseObject.put(EndecaConstants.LOAD_MORE_OFFSET, loadmoreNewCount);
                }
            }
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class setLoadMoreOffset method {} ", e.getMessage());
        }
    }

    /**
     * Method to get Parameter Map from  selectors array
     *
     * @param selectors
     * @return requestParameterMap Map<String, List<String>>
     */
    private static Map<String, List<String>> getParameterMapFromSelectors(String[] selectors) {
        final Map<String, List<String>> requestParameterMap = new HashMap<>();
        if (null != selectors && selectors.length > 0) {
            Arrays.asList(selectors).stream().forEach(selector -> {
                final String[] selectorArray = selector.split("\\$");
                List<String> values = Arrays.asList(selectorArray).stream()
                        .skip(1)
                        .collect(Collectors.toList());
                if (values.isEmpty()) {
                    values.add(StringUtils.EMPTY);
                }
                values = dotConversion(values, selectorArray);
                requestParameterMap.put(selectorArray[0], values);
            });
        }
        return requestParameterMap;
    }

    /**
     * @param values
     * @param selectorArray
     * @return SearchTerm keyword has its periods('.') replaced with '@' symbols.
     */
    private static List<String> dotConversion(List<String> values, String[] selectorArray) {
        String[] newValue = values.stream().toArray(String[]::new);
        if ((newValue[0].contains(CommonConstants.ATRATE_CHAR)) && (selectorArray[0].contains(CommonConstants.SEARCH_TERM))) {
            for (int i = 0; i < newValue.length; i++) {
                if (newValue[i].contains(CommonConstants.ATRATE_CHAR)) {
                    newValue[i] = newValue[i].replace(CommonConstants.ATRATE_CHAR, CommonConstants.PERIOD);
                    values = Arrays.asList(newValue);
                }
            }
        }
        return values;
    }

    /**
     * @param startDate
     * @param endDate
     * @param sortBy
     * @return return updated parameter map with publishDate + SortBy
     */
    private static Map<String, List<String>> getWhatsNewEndecaFilterOptions(Long startDate, Long endDate, String sortBy) {
        final Map<String, List<String>> requestParameterMap = new HashMap<>();
        if (startDate != null && endDate != null) {
            List<String> values = new ArrayList<>();
            values.add(Long.toString(startDate));
            values.add(Long.toString(endDate));
            requestParameterMap.put(EndecaConstants.PUBLICATION_DATE, values);
        }
        if (sortBy != null) {
            List<String> values = new ArrayList<>();
            values.add(sortBy);
            requestParameterMap.put(EndecaConstants.SORT_BY, values);
        }
        return requestParameterMap;
    }

    /**
     * Method to set Publication Date filter
     * Start date: Taken from the requestParameterMap and converted to epoch and set to the filter
     * End date: If endDate is not available in the requestParameterMap then current data is considered as endDate
     *
     * @param filters
     * @param requestParameterMap
     */
    private static void setPublicationDateFilter(List<FilterBean> filters, Map<String, List<String>> requestParameterMap) {
        final List<String> dateRange = new ArrayList<>();
        final SimpleDateFormat formatOfDate = new SimpleDateFormat(EndecaConstants.DATE_FORMAT);
        final GregorianCalendar date = new GregorianCalendar();
        try {
            //Adds Start Date to dateRange list
            date.setTime(formatOfDate.parse(requestParameterMap.get(EndecaConstants.START_DATE).get(0)));
            dateRange.add(CommonUtil.getEpocTimeFromGregorianForAssets(date));
            //Adds End Date to dateRange list
            if (requestParameterMap.containsKey(EndecaConstants.END_DATE) && !requestParameterMap.get(EndecaConstants.END_DATE).get(0).isEmpty()) {
                date.setTime(formatOfDate.parse(requestParameterMap.get(EndecaConstants.END_DATE).get(0)));
                dateRange.add(CommonUtil.getEpocTimeFromGregorianForAssets(date));
            } else {
                date.setTime(new Date());
                dateRange.add(CommonUtil.getEpocTimeFromGregorianForAssets(date));
            }
            filters.add(FilterUtil.getFilterBeanList(EndecaConstants.PUBLICATION_DATE, dateRange));
        } catch (ParseException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class setPublicationDateFilter method {} ", e.getMessage());
        }
    }

    /**
     * Method to set all required  filters in the endecaServiceRequestBean
     *
     * @param request
     * @param endecaServiceRequestBean
     * @param requestParameterMap
     * @param facetGroups
     * @param country
     */
    private void setEndecaRequestFilters(SlingHttpServletRequest request, EndecaServiceRequestBean endecaServiceRequestBean,
                                         Map<String, List<String>> requestParameterMap, List<String> facetGroups, String country, SecureModule module, Page currentPage) {
        List<FilterBean> filters = new ArrayList<>();
        final EndecaConfigServiceBean configServiceBean = endecaConfig.getConfigServiceBean();
        filters.add(FilterUtil.getFilterBean(EndecaConstants.COUNTRY_CONSTANT, EndecaConstants.SEARCH_TERM_IGNORE));
        filters.add(FilterUtil.getFilterBean(EndecaConstants.AUTOCORRECT, CommonConstants.TRUE));
        filters.add(FilterUtil.getFilterBeanList(EndecaConstants.RETURN_FACETS_FOR, facetGroups));
        filters.add(FilterUtil.getFilterBeanList(EndecaConstants.FILE_TYPES, Arrays.asList(configServiceBean.getFileTypes())));
        List<String> categoryTagsFilter = getCategoryTagsFromResources(currentPage);
        if (!categoryTagsFilter.isEmpty()) {
            filters.add(FilterUtil.getFilterBeanList(EndecaConstants.CATEGORY_TAGS_FILTER, categoryTagsFilter));
        }
        if (requestParameterMap.containsKey(EndecaConstants.SORT_BY)) {
            filters.add(FilterUtil.getFilterBeanList(EndecaConstants.SORT_BY, requestParameterMap.get(EndecaConstants.SORT_BY)));
        }
        if (requestParameterMap.containsKey(EndecaConstants.START_DATE) && !requestParameterMap.get(EndecaConstants.START_DATE).get(0).isEmpty()) {
            setPublicationDateFilter(filters, requestParameterMap);
        }
        if (requestParameterMap.containsKey(EndecaConstants.FACETS_STRING_WITH_F)) {
            filters.add(FilterUtil.getFilterBeanList(EndecaConstants.FACETS_STRING_WITH_F, requestParameterMap.get(EndecaConstants.FACETS_STRING_WITH_F)));
        }

        filters.addAll(new SecureFilterBeanFactory().createFilterBeans(authorizationService.getTokenFromSlingRequest(request), module));

        endecaServiceRequestBean.setFilters(filters);
    }

    /**
     * Method to set FileType and File Size in Bytes to the response Object
     * Ex: PDF 250KB
     *
     * @param responseObject
     */
    private void setFileTypeAndSize(JSONObject responseObject) {
        try {
            if (responseObject.has(EndecaConstants.FILE_SIZE) && responseObject.has(EndecaConstants.FILE_TYPE)) {
                final String mimeType = responseObject.getString(EndecaConstants.FILE_TYPE);
                final String fileSize = getFileSizeInByte(responseObject.get(EndecaConstants.FILE_SIZE).toString());
                String fileType = getFileTypeFromMimeType(mimeType);
                fileType = fileType != null ? fileType.toUpperCase(Locale.ENGLISH) : mimeType;
                if (null != fileType && !fileType.equals(StringUtils.EMPTY)) {
                    fileType = fileType + CommonConstants.BLANK_SPACE;
                }
                responseObject.put(EndecaConstants.FILE_TYPE_AND_SIZE, fileType + fileSize);
            }
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class setFileTypeAndSize method {} ", e.getMessage());
        }
    }

    /**
     * Method to set default image in the response based on the file type
     * Default image is set only if endeca response  image attribute value is empty
     * Gets the default image from the location- /content/dam/eaton/resources/advanced-search/
     *
     * @param responseObject
     */
    private void setDefaultImage(JSONObject responseObject) {
        try {
            if (responseObject.has(EndecaConstants.FILE_TYPE) && responseObject.has(EndecaConstants.IMAGE_STRING)
                    && (responseObject.getString(EndecaConstants.IMAGE_STRING).isEmpty() ||
                    responseObject.getString(EndecaConstants.IMAGE_STRING).endsWith(EndecaConstants.JCRCONTENT_RENDITIONS_ORIGINAL))) {
                final String fileType = getFileTypeFromMimeType(responseObject.getString(EndecaConstants.FILE_TYPE));
                if (null != fileType && !fileType.isEmpty()) {
                    final String defaultImage = new StringBuilder()
                            .append(EndecaConstants.DEFAULT_IMAGE_DAM_PATH_ROOT)
                            .append(fileType)
                            .append(CommonConstants.HYPHEN)
                            .append(CommonConstants.DAM_THUMBNAIL_KEY)
                            .append(".png").toString();

                    responseObject.put(EndecaConstants.IMAGE_STRING, defaultImage);
                }
                //remove this else block(added just to display the images in local)
            } else {
                responseObject.put(EndecaConstants.IMAGE_STRING, responseObject.getString(EndecaConstants.IMAGE_STRING));
            }
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class setDefaultImage method {} ", e.getMessage());
        }
    }

    /**
     * Method to get fileType by passing MimeType
     *
     * @param mimeType
     * @return
     */
    private String getFileTypeFromMimeType(String mimeType) {
        String fileType = mimeType;
        if ((mimeType != null) && (!mimeType.equals(StringUtils.EMPTY)) && null != mimeTypeService) {
            if (mimeType.equals(CommonConstants.FILE_TYPE_APPLICATION_POSTSCRIPT) ||
                    mimeType.endsWith(CommonConstants.FILE_TYPE_EPS) || mimeType.endsWith(CommonConstants.FILE_TYPE_X_EPS)) {
                return CommonConstants.FILE_TYPE_APPLICATION_EPS_VALUE;
            }
            String[] finalValue = mimeType.split(CommonConstants.SLASH_STRING);
            fileType = finalValue[1];
        }
        return fileType;
    }

    /**
     * Method to set Display language on the response object
     *
     * @param responseObject
     */
    private static void setLanguage(JSONObject responseObject, SlingHttpServletRequest request) {
        try {
            if (responseObject.has(LANGUAGE)) {
                String languageName = responseObject.get(LANGUAGE).toString();
                String requestPath = request.getRequestPathInfo().getResourcePath();
                String pagePath = requestPath.split(JCR_CONSTANT_STRING)[0];
                ResourceResolver resolver = request.getResourceResolver();
                Resource currentResource = resolver.getResource(pagePath);
                Page currentPage = currentResource.adaptTo(Page.class);
                if (languageName.equals(GLOBAL_STRING)) {
                    responseObject.put(EndecaConstants.LANGUAGE_STRING,
                            CommonUtil.getI18NFromResourceBundle(request, currentPage, GLOBAL_STRING,
                                    GLOBAL_DEFAULT_STRING));
                } else if (languageName.equals(MULTILINGUAL_STRING)) {
                    responseObject.put(EndecaConstants.LANGUAGE_STRING,
                            CommonUtil.getI18NFromResourceBundle(request, currentPage, MULTILINGUAL_STRING,
                                    MULTILINGUAL_STRING));
                } else if (languageName.isEmpty()) {
                    responseObject.put(EndecaConstants.LANGUAGE_STRING, "");
                } else {
                    String tag = languageName.toLowerCase().replace(CommonConstants.UNDERSCORE, CommonConstants.HYPHEN);
                    Resource currentTagResource = resolver.getResource(CommonConstants.TAG_PATH + CommonConstants.SLASH_STRING + tag);
                    Tag currentTag = currentTagResource.adaptTo(Tag.class);
                    if (currentTag != null) {
                        String tagTitle = currentTag.getTitle();
                        if (tagTitle.indexOf(OPEN_PARANTHESIS_STRING) == -1) {
                            responseObject.put(EndecaConstants.LANGUAGE_STRING, tagTitle);
                        } else {
                            Integer index = tagTitle.indexOf(OPEN_PARANTHESIS_STRING);
                            String title = tagTitle.substring(0, index);
                            responseObject.put(EndecaConstants.LANGUAGE_STRING, title);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            LOG.error("Exception in EndecaAdvancedSearchServiceImpl class setLanguage method {} ", e.getMessage());
        }
    }

    /**
     * Method to get file size in Bytes
     *
     * @param size
     * @return
     */
    private static String getFileSizeInByte(String size) {
        if ((size != null) && (!size.equals(StringUtils.EMPTY))) {
            // get the file size in Bytes unit
            long fileSize = Long.parseLong(size);
            long sizeOfFile = (long) (fileSize / Math.pow(EndecaConstants.TEN, EndecaConstants.SIX));
            String spaceUnit;
            // set the file size unit
            if (sizeOfFile < 1) {
                sizeOfFile = (long) (fileSize / Math.pow(EndecaConstants.TEN, EndecaConstants.THREE));
                spaceUnit = CommonConstants.KB;
                if (sizeOfFile < 1) {
                    sizeOfFile = (long) (fileSize);
                    spaceUnit = CommonConstants.B;
                }
            } else {
                spaceUnit = CommonConstants.MB;
            }
            size = sizeOfFile + spaceUnit;
        }
        return size;
    }

    /**
     * Method to get the Tags from Resource Tag which is configured in Teaser Tab and pass the tag names as values to category tags filter
     *
     * @param currentPage
     */
    private List<String> getCategoryTagsFromResources(Page currentPage) {
        List<String> categoryTags = new ArrayList<>();
        try (ResourceResolver resourceResolver = adminService.getReadService()) {
            ValueMap pageProperties = null;
            String[] subCatergoryTags = new String[0];
            final Resource pageContentResource = currentPage.getContentResource();
            if (null != pageContentResource) {
                pageProperties = pageContentResource.getValueMap();
            }
            if (null != pageProperties) {
                subCatergoryTags = pageProperties.get(CommonConstants.PRIMARY_SUB_CATEGORY_TAG, new String[]{});
            }
            if (subCatergoryTags.length > 0) {
                for (String tag : subCatergoryTags) {
                    categoryTags.add(tag.replaceAll(EndecaFacetTag.AMPERSAND, EndecaFacetTag.AMPERSAND_ENCODE));
                }
            }
            LOG.debug("Exit from getCategoryTagsFromResources(): Category Tags filter value is updated");

        } catch (Exception e) {
            LOG.error("Exception in getCategoryTagsFromResources() {} ", e.getMessage());
        }
        return categoryTags;
    }
}



