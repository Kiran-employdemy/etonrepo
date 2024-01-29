package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.FacetURLBean;
import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SearchResultsModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.FacetURLBeanService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component(service = FacetURLBeanService.class,immediate = true,
    property = {
            AEMConstants.SERVICE_VENDOR_EATON,
            AEMConstants.SERVICE_DESCRIPTION + "Facet URL Bean Service",
            AEMConstants.PROCESS_LABEL + "Facet URL Bean Service"
    })
public class FacetURLBeanServiceImpl implements FacetURLBeanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacetURLBeanServiceImpl.class);
    private static final String LOADMORE_CONSTANT = ".loadmore";
	private static final String PRODUCTTYPE_CONSTANT=".productType$";
	
    @Reference
    private AdminService adminService;

    @Reference
    private EndecaConfig endecaConfigService;

    public FacetURLBeanServiceResponse getFacetURLBeanResponse(String[] selectors, int configPageSize,
                                                               String pageType, String contentResourcePath) {
        LOGGER.debug("Entry into getFacetURLBeanResponse method");

        ProductGridSelectors productGridSelectors = new ProductGridSelectors();
        FacetURLBean facetURLBean = new FacetURLBean();
        String basecontentPath = contentResourcePath;
        FacetURLBeanServiceResponse facetURLBeanServiceResponse = new FacetURLBeanServiceResponse();

        if (StringUtils.equals(CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE, pageType)) {
            SearchResultsModel searchResultModel = getSearchResultModel(contentResourcePath);
            if (null != searchResultModel && StringUtils.equals(CommonConstants.SEARCH_ADVANCED_VIEW, searchResultModel.getView())) {
                productGridSelectors.setSearchTerm("##IGNORE##");

                EndecaConfigServiceBean endecaConfigBean = null;

                if (endecaConfigService != null) {
                    endecaConfigBean = endecaConfigService.getConfigServiceBean();
                }

                setProductGridData(productGridSelectors, searchResultModel, endecaConfigBean);
            }
        }

        facetURLBean.setContentPath(basecontentPath);
        facetURLBean.setExtensionIdEndURL(CommonConstants.HTML_EXTN);
        facetURLBean.setFacetStartURL(".facets");
        facetURLBean.setFacetEndURL(CommonConstants.HTML_EXTN);
        facetURLBean.setSortStartURL(".sort");
        facetURLBean.setSortEndURL(CommonConstants.HTML_EXTN);
        facetURLBean.setLoadMoreStartURL(LOADMORE_CONSTANT);
        facetURLBean.setLoadMoreEndURL("$11.html");

        if (null != selectors && selectors.length >0) {
            String facetStartURL = StringUtils.EMPTY;
            String sortSelector = StringUtils.EMPTY;

            for (int i = 0; i < selectors.length; i++) {
                String selector = selectors[i];


                if (selector.startsWith("searchTerm")) {

                    String[] searchTerm = selector.split("\\$");
                    if ((searchTerm != null) && (searchTerm.length > 1)) {
                        String decodedSearchTerm = CommonUtil.decodeSearchTermString(searchTerm[1]);
                        productGridSelectors.setSearchTerm(decodedSearchTerm);

                        if (facetURLBean.getContentPath() != null) {
                            try {
                                String encodedSearchTerm = CommonUtil.encodeSearchTermString(searchTerm[1]);
                                facetURLBean.setContentPath(basecontentPath + CommonConstants.PERIOD + searchTerm[0] + "$" + URLEncoder.encode(encodedSearchTerm, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                LOGGER.error("Exception occured:",e);
                            }
                        }
                    }
                }

                if ((selector.startsWith("specifications"))&& (facetURLBean.getContentPath()!= null)) {
                        facetURLBean.setContentPath(basecontentPath + CommonConstants.PERIOD + selector);
                        facetURLBean.setExtensionIdStartURL(facetURLBean.getContentPath() + PRODUCTTYPE_CONSTANT);
                }

                if (selector.startsWith("extensionId")) {
                    if (facetURLBean.getContentPath() != null) {
                        facetURLBean.setContentPath(facetURLBean.getContentPath() + CommonConstants.PERIOD + selector);
                    }

                    String[] searhTerm = selector.split("\\$");
                    if ((searhTerm != null) && (searhTerm.length > 1)) {
                        productGridSelectors.setExtensionId(searhTerm[1]);
                    }
                }

                if (selector.startsWith("tabs")) {
                    if (facetURLBean.getContentPath() != null) {
                        facetURLBean.setTabStartURL(facetURLBean.getContentPath() + ".tabs$");
                        facetURLBean.setTabEndURL(".html");
                        facetURLBean.setContentPath(facetURLBean.getContentPath() + CommonConstants.PERIOD + selector);
                    }

                    String[] searhTerm = selector.split("\\$");
                    if ((searhTerm != null) && (searhTerm.length > 1) && (!searhTerm[1].equalsIgnoreCase(CommonConstants.ALL))) {
                            productGridSelectors.setSelectedTab(searhTerm[1]);
                        }
                }

                if ((selector.startsWith("models"))&&(facetURLBean.getContentPath() != null)) {
                        facetURLBean.setContentPath(basecontentPath + CommonConstants.PERIOD + selector);
                        facetURLBean.setExtensionIdStartURL(facetURLBean.getContentPath() + PRODUCTTYPE_CONSTANT);

                }

                if (selector.startsWith("productType") && (facetURLBean.getContentPath() != null)) {
                    facetURLBean.setContentPath(facetURLBean.getContentPath() + CommonConstants.PERIOD + selector);
                    String[] searhTerm = selector.split("\\$");
                    if ((searhTerm != null) && (searhTerm.length > 1)) {
                        productGridSelectors.setExtensionId(searhTerm[1]);
                    }
                }

                if (selector.startsWith("autocorrect") && (facetURLBean.getContentPath() != null)) {
                    facetURLBean.setContentPath(facetURLBean.getContentPath() + CommonConstants.PERIOD + selector);
                    String[] autoCorrectFlag = selector.split("\\$");
                    if ((autoCorrectFlag != null) && (autoCorrectFlag.length > 1)) {
                        productGridSelectors.setAutoCorrect(autoCorrectFlag[1]);
                    }
                }

                if (selector.startsWith("facets")) {
                    facetStartURL = selector;
                    facetURLBean.setFacetStartURL(CommonConstants.PERIOD + facetStartURL);

                    String sortStartURL = StringUtils.EMPTY;
                    String loadMoreStartURL = StringUtils.EMPTY;
                    String tabsEndURL = StringUtils.EMPTY;

                    if ((facetURLBean.getFacetStartURL() != null) && (!facetURLBean.getFacetStartURL().isEmpty())) {
                        sortStartURL = facetURLBean.getFacetStartURL() + ".sort";
                        loadMoreStartURL = facetURLBean.getFacetStartURL() + LOADMORE_CONSTANT;
                        tabsEndURL = facetURLBean.getFacetStartURL() + CommonConstants.HTML_EXTN;
                    }

                    facetURLBean.setSortStartURL(sortStartURL);
                    facetURLBean.setLoadMoreStartURL(loadMoreStartURL);
                    facetURLBean.setTabEndURL(tabsEndURL);

                    String[] Facets = selectors[i].split("\\$");
                    if ((Facets != null) && (Facets.length > 1)) {
                        List<FacetBean> FacetList = new ArrayList<>();
                        for (int j = 1; j < Facets.length; j++) {

                            FacetBean Facet = new FacetBean();
                            Facet.setFacetID(Facets[j]);
                            String deselectURL = CommonConstants.PERIOD + selector.replace("$" + Facets[j], "");
                            if ((deselectURL != null) && (deselectURL.equals(".facets"))) {
                                Facet.setFacetDeselectURL("");
                            } else {
                                Facet.setFacetDeselectURL(deselectURL);
                            }
                            FacetList.add(Facet);

                        }
                        productGridSelectors.setFacets(FacetList);
                    }
                } else if (selector.startsWith("sort")) {

                    String loadMoreStartURL = StringUtils.EMPTY;
                    String extensionIdEndURL = StringUtils.EMPTY;
                    String tabsEndURL = StringUtils.EMPTY;

                    sortSelector = selector;

                    if ((facetStartURL != null) && (!facetStartURL.isEmpty())) {
                        loadMoreStartURL = CommonConstants.PERIOD + facetStartURL;
                        extensionIdEndURL = CommonConstants.PERIOD + facetStartURL;
                        tabsEndURL = CommonConstants.PERIOD + facetStartURL;
                    }
                    loadMoreStartURL = loadMoreStartURL + CommonConstants.PERIOD + selector + LOADMORE_CONSTANT;
                    extensionIdEndURL = extensionIdEndURL + CommonConstants.PERIOD + selector + CommonConstants.HTML_EXTN;
                    tabsEndURL = tabsEndURL + CommonConstants.PERIOD + selector + CommonConstants.HTML_EXTN;

                    facetURLBean.setLoadMoreStartURL(loadMoreStartURL);
                    String FacetEndURL = CommonConstants.PERIOD + selector + CommonConstants.HTML_EXTN;
                    facetURLBean.setFacetEndURL(FacetEndURL);
                    facetURLBean.setTabEndURL(tabsEndURL);
                    facetURLBean.setExtensionIdEndURL(extensionIdEndURL);

                    String[] SortByOptions = selectors[i].split("\\$");
                    if ((SortByOptions != null) && (SortByOptions.length > 1)) {
                        String[] sortByFacets = SortByOptions[1].split("\\*");
                        if ((sortByFacets != null) && (sortByFacets.length > 1)) {
                            productGridSelectors.setSortyByOption(sortByFacets[1]);
                            productGridSelectors.setSortFacet(sortByFacets[0]);
                        } else{
                            productGridSelectors.setSortyByOption(SortByOptions[1]);
                        }
                    }
                } else if (selector.startsWith("loadmore")) {

                    String FacetEndURL = StringUtils.EMPTY;
                    String extensionIdEndURL = StringUtils.EMPTY;
                    String tabsEndURL = StringUtils.EMPTY;

                    int pageSize = 0;

                    if ((facetStartURL != null) && (!facetStartURL.isEmpty())) {
                        extensionIdEndURL = CommonConstants.PERIOD + facetStartURL;
                        tabsEndURL = CommonConstants.PERIOD + facetStartURL;
                    }

                    if ((sortSelector != null) && (!sortSelector.isEmpty())) {
                        FacetEndURL = CommonConstants.PERIOD + sortSelector;
                        extensionIdEndURL = extensionIdEndURL + CommonConstants.PERIOD + sortSelector;
                        tabsEndURL = tabsEndURL + CommonConstants.PERIOD + sortSelector;
                    }
                    FacetEndURL = FacetEndURL + CommonConstants.PERIOD + selector + CommonConstants.HTML_EXTN;
                    extensionIdEndURL = extensionIdEndURL + CommonConstants.PERIOD + selector + CommonConstants.HTML_EXTN;
                    tabsEndURL = tabsEndURL + CommonConstants.PERIOD + selector + CommonConstants.HTML_EXTN;

                    facetURLBean.setFacetEndURL(FacetEndURL);

                    String sortEndURL = CommonConstants.PERIOD + selector + CommonConstants.HTML_EXTN;
                    facetURLBean.setSortEndURL(sortEndURL);

                    facetURLBean.setTabEndURL(tabsEndURL);
                    facetURLBean.setExtensionIdEndURL(extensionIdEndURL);

                    String[] LoadMoreOptions = selectors[i].split("\\$");
                    if ((LoadMoreOptions != null) && (LoadMoreOptions.length > 1)) {
                        int loadMoreCount = Integer.parseInt(LoadMoreOptions[1]);
                        productGridSelectors.setLoadMoreOption(loadMoreCount);

                        if (configPageSize != 0) {
                            pageSize = configPageSize;
                        }
                        int loadmoreNewCount = loadMoreCount + pageSize;
                        String loadMoreEndUrl = "$" + loadmoreNewCount + CommonConstants.HTML_EXTN;
                        facetURLBean.setLoadMoreEndURL(loadMoreEndUrl);
                    }
                }
            }
        }
        facetURLBeanServiceResponse.setProductGridSelectors(productGridSelectors);
        facetURLBeanServiceResponse.setFacetURLBean(facetURLBean);
        LOGGER.debug("Exit from getFacetURLBeanResponse method");
        return  facetURLBeanServiceResponse;
    }

    private static void setProductGridData(ProductGridSelectors productGridSelectors, SearchResultsModel searchResultModel, EndecaConfigServiceBean endecaConfigBean) {
        if ((endecaConfigBean != null)&&(searchResultModel.getContentTypeOption() != null)) {
                if ("enableProductsTab".equals(searchResultModel.getContentTypeOption())) {
                    productGridSelectors.setSelectedTab(endecaConfigBean.getProductsTabId());
                } else if ("enableNewsTab".equals(searchResultModel.getContentTypeOption())) {
                    productGridSelectors.setSelectedTab(endecaConfigBean.getNewsTabId());
                } else if ("enableResourcesTab".equals(searchResultModel.getContentTypeOption())) {
                    productGridSelectors.setSelectedTab(endecaConfigBean.getResourcesTabId());
                } else if ("enableServicesTab".equals(searchResultModel.getContentTypeOption())) {
                    productGridSelectors.setSelectedTab(endecaConfigBean.getServicesTabId());
                }
        }
    }

    private SearchResultsModel getSearchResultModel(String contentResourcePath) {
        SearchResultsModel searchResultModel = null;
        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                if (adminResourceResolver != null) {
                    if (!contentResourcePath.endsWith(CommonConstants.SEARCH_RESULTS_PATH_FROM_JCR_CONTENT)) {
                        contentResourcePath = contentResourcePath.concat(CommonConstants.SEARCH_RESULTS_PATH_FROM_JCR_CONTENT);
                    }
                    Resource searchResultResource = adminResourceResolver.getResource(contentResourcePath);
                    if (null != searchResultResource) {
                        searchResultModel = searchResultResource.adaptTo(SearchResultsModel.class);
                    } else {
                        searchResultResource = adminResourceResolver.resolve(contentResourcePath);
                        searchResultModel = searchResultResource.adaptTo(SearchResultsModel.class);
                        
                    }
                }
            }
        }
        return searchResultModel;
    }
}
