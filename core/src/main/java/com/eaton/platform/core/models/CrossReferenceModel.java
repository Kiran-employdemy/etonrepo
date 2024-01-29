package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.bean.SortByOptionValueBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.FacetURLBeanService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.crossreference.XRefResult;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaService;
import java.util.Optional;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Model(adaptables = {
        Resource.class,
        SlingHttpServletRequest.class
}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CrossReferenceModel {
    private static final Logger LOG = LoggerFactory.getLogger(CrossReferenceModel.class);
    private static final String SINGLE_FACET_ENABLED = "singleFacetEnabled";
    private static final String SHOW_AS_GRID = "showAsGrid";
    private static final String FACET_SEARCH_ENABLED = "facetSearchEnabled";
    private static final String SORT_BY_OPTIONS_VALUE = "sortByOptionsValue";
    private static final String TAXONOMY_ATTRIBUTE_VALUE = "taxonomyAttributeValue";

    @OSGiService
    private EndecaRequestService endecaRequestService;

    @OSGiService
    private EatonSiteConfigService eatonSiteConfigService;

    @OSGiService
    private EndecaService endecaService;

    @OSGiService
    private EatonConfigService configService;

    @OSGiService
    private FacetURLBeanService facetURLBeanService;

    @Inject
    private Resource resource;

    @Inject
    private Page currentPage;

    @Inject
    private SlingHttpServletRequest slingRequest;

    @Inject
    @Via("resource")
    private boolean hideGlobalFacetSearch;

    @Inject
    @Via("resource")
    private Resource taxonomyAttributes;

    @Inject
    @Via("resource")
    private String defaultSortingOption;

    @Inject
    @Via("resource")
    private Resource sortByOptions;
	
	/**Facet value count from site config*/
	private int facetValueCount;

    private final ArrayList<String> returnFacet = new ArrayList<>();
    private final JsonArray activeFacetListJson = new JsonArray();
    private final JsonArray facetGroupOrderingJson = new JsonArray();
    private JsonArray facetGroupListJson = new JsonArray();
    private List<XRefResult> bestMatches;
    private List<XRefResult> partialMatches;
    private String baseSKUPath;
    private SiteResourceSlingModel siteResourceSlingModel;
    private String specificationLabel;
    private String resourceLabel;
    private JsonObject crossReferenceResponse;
    private ProductGridSelectors productGridSelectors;
    private List<SortByOptionValueBean> sortByOptionsValue;
    private String selectedSortBy;
    private String searchTerm;

    @PostConstruct
    public void init() {
        if (null != taxonomyAttributes && taxonomyAttributes.hasChildren()) {
            taxonomyAttributes.getChildren().forEach(child -> {
                facetGroupOrderingJson.add(getOrderingConfig(child));
                returnFacet.add(child.getValueMap().get(TAXONOMY_ATTRIBUTE_VALUE, StringUtils.EMPTY));
            });
        }

        if (null != sortByOptions && sortByOptions.hasChildren()) {
            sortByOptionsValue = StreamSupport.stream(sortByOptions.getChildren()
                    .spliterator(), false)
                    .filter(Objects::nonNull)
                    .map(sortByOptionsResource -> sortByOptionsResource.getValueMap())
                    .map(valueMap -> valueMap.get(SORT_BY_OPTIONS_VALUE, StringUtils.EMPTY))
                    .map(option -> {
                        SortByOptionValueBean sortByOptionValueBean = new SortByOptionValueBean();
                        sortByOptionValueBean.setLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,option,option));
                        sortByOptionValueBean.setValue(option);
                        return sortByOptionValueBean;
                    })
                    .collect(Collectors.toList());

        }

        selectedSortBy = defaultSortingOption;

        if (configService != null && endecaService != null && eatonSiteConfigService != null && endecaRequestService != null) {
            final String skuPageName = configService.getConfigServiceBean().getSkupagename();
            final Optional < SiteResourceSlingModel > siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);

            if (siteConfig.isPresent()) {
                siteResourceSlingModel = siteConfig.get();
                final String pageType = currentPage.getProperties().get(CommonConstants.PAGE_TYPE).toString();
                final FacetURLBeanServiceResponse facetURLBeanResponse = facetURLBeanService.getFacetURLBeanResponse(slingRequest.getRequestPathInfo().getSelectors(), siteResourceSlingModel.getPageSize(), pageType, resource.getPath());
                if (null != facetURLBeanResponse) {
                    productGridSelectors = facetURLBeanResponse.getProductGridSelectors();
                } else {
                    productGridSelectors = null;
                }
            } else {
                LOG.error("Site config was not authored: " + currentPage.getPath());
            }

            if (null != productGridSelectors && StringUtils.isNotBlank(productGridSelectors.getSearchTerm())) {
                final List < String > activeFacets = productGridSelectors.getFacets() != null ?
                        productGridSelectors.getFacets().stream().map(facet -> facet.getFacetID()).collect(Collectors.toList()) :
                        new ArrayList< >();
                selectedSortBy = StringUtils.isNotBlank(productGridSelectors.getSortyByOption()) ? productGridSelectors.getSortyByOption() : defaultSortingOption;
                searchTerm = StringUtils.isNotBlank(productGridSelectors.getSearchTerm()) ? productGridSelectors.getSearchTerm() : StringUtils.EMPTY;
                final EndecaServiceRequestBean endecaServiceRequest = endecaRequestService

                        .getCrossReferenceEndecaRequestBean(currentPage, activeFacets, productGridSelectors.getSearchTerm(), "0",
                            String.valueOf(siteResourceSlingModel.getPageSize()), selectedSortBy, returnFacet);
                crossReferenceResponse = endecaService.getCrossReferenceResponse(endecaServiceRequest, slingRequest, currentPage);
                bestMatches = endecaService.getCrossReferenceBestMatches(endecaServiceRequest);
                partialMatches = endecaService.getCrossReferencePartialMatches(endecaServiceRequest);
                if (null != crossReferenceResponse) {
                    try {
                        facetGroupListJson = crossReferenceResponse.get(EndecaConstants.FACETS_STRING).getAsJsonArray();

                        for (int i = 0; i < facetGroupListJson.size(); i++) {
                            final JsonArray group = facetGroupListJson.get(i).getAsJsonObject().get(EndecaConstants.VALUES).getAsJsonArray();
                            for (int j = 0; j < group.size(); j++) {
                                final JsonObject facet = group.get(j).getAsJsonObject();
                                if (facet.has(EndecaConstants.ACTIVE_STRING) && CommonConstants.TRUE.equals(facet.get(EndecaConstants.ACTIVE_STRING).getAsString())) {
                                    final JsonObject activeFacet = new JsonObject();
                                    activeFacet.add(EndecaConstants.NAME_STRING, new Gson().toJsonTree(facet.get(EndecaConstants.NAME_STRING).getAsString()));
                                    activeFacet.add(EndecaConstants.TITLE, new Gson().toJsonTree(facet.get(EndecaConstants.TITLE).getAsString()));
                                    activeFacet.add(EndecaConstants.ID_STRING, new Gson().toJsonTree(facet.get(EndecaConstants.ID_STRING).getAsString()));
                                    activeFacet.add(CommonConstants.VALUE, new Gson().toJsonTree(facet.get(CommonConstants.VALUE).getAsString()));
                                    activeFacetListJson.add(activeFacet);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("Malformed cross reference response JSON", e);
                    }
                }

                baseSKUPath = CommonUtil.getSKUPagePath(currentPage, skuPageName);
                specificationLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.I18N_SPECIFICATION_TAB, "Specification");
                resourceLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.I18N_RESOURCE_TAB, "Resource");
            }
        } else {
            if (configService == null) LOG.error("RecommendedProductsModel: configService was null");
            if (endecaService == null) LOG.error("RecommendedProductsModel: endecaService was null");
            if (endecaRequestService == null) LOG.error("RecommendedProductsModel: endecaRequestService was null");
            if (eatonSiteConfigService == null) LOG.error("RecommendedProductsModel: eatonSiteConfigService was null");
        }
    }

    private JsonObject getOrderingConfig(final Resource resource) {
        final JsonObject filterConfig = new JsonObject();

        try {
            filterConfig.add(EndecaConstants.NAME_STRING, new Gson().toJsonTree(resource.getValueMap().get(TAXONOMY_ATTRIBUTE_VALUE, StringUtils.EMPTY)));
            filterConfig.add(SINGLE_FACET_ENABLED, new Gson().toJsonTree(CommonConstants.TRUE.equals(resource.getValueMap().get(SINGLE_FACET_ENABLED, CommonConstants.FALSE))));
            filterConfig.add(FACET_SEARCH_ENABLED, new Gson().toJsonTree(CommonConstants.TRUE.equals(resource.getValueMap().get(FACET_SEARCH_ENABLED, CommonConstants.FALSE))));
            filterConfig.add(SHOW_AS_GRID, new Gson().toJsonTree(CommonConstants.TRUE.equals(resource.getValueMap().get(SHOW_AS_GRID, CommonConstants.FALSE))));
        } catch (Exception e) {
            LOG.error("JSON Exception while forming service JSON object", e);
        }

        return filterConfig;
    }

    public String getBaseSKUPath() {
        return baseSKUPath;
    }

    public SiteResourceSlingModel getSiteResourceSlingModel() {
        return siteResourceSlingModel;
    }

    public String getSpecificationLabel() {
        return specificationLabel;
    }

    public String getResourceLabel() {
        return resourceLabel;
    }

    public boolean isHideGlobalFacetSearch() {
        return hideGlobalFacetSearch;
    }

    public String getServletUrl() {
        return resource.getPath() + ".crossreference.json";
    }

    public Boolean getShowLoadMore() {
        return Integer.parseInt(getResultsCount()) > siteResourceSlingModel.getPageSize();
    }

    public List<XRefResult> getBestMatches() {
        return bestMatches;
    }

    public List<XRefResult> getPartialMatches() {
        return partialMatches;
    }

    public String getFacetGroupListJson() {
        try {
            return URIUtil.encodeAll(facetGroupListJson.toString());
        } catch (URIException e) {
            LOG.error("Error while URI encoding facet group json", e);

            return "[]";
        }
    }

    public String getFacetGroupOrderingJson() {
        try {
            return URIUtil.encodeAll(facetGroupOrderingJson.toString());
        } catch (URIException e) {
            LOG.error("Error while URI encoding facet ordering json", e);
            return "[]";
        }
    }

    public String getActiveFacetJson() {
        try {
            return URIUtil.encodeAll(activeFacetListJson.toString());
        } catch (URIException e) {
            LOG.error("Error while URI encoding active facet json", e);

            return "[]";
        }
    }

    public int getActiveFacetCount() {
        return activeFacetListJson.size();
    }

    public Boolean getHasSearchTerm() {
        return StringUtils.isNotBlank(productGridSelectors.getSearchTerm());
    }

    public Boolean getHasResults() {
        boolean hasResult = Boolean.FALSE;
        try {
            if (null != crossReferenceResponse) {
                hasResult = StringUtils.isNotBlank(crossReferenceResponse.get(EndecaConstants.TOTAL_COUNT_STRING).getAsString()) && !crossReferenceResponse.get(EndecaConstants.TOTAL_COUNT_STRING).getAsString().equals("0");
            }
        } catch (Exception e) {
            hasResult =  false;
        }
        return hasResult;
    }

    public ArrayList<String> getReturnFacet() {
        return returnFacet;
    }

    public String getResultsCount() {
        String totalCount = StringUtils.EMPTY;
        if (null != crossReferenceResponse) {
            try {
                totalCount = crossReferenceResponse.get(EndecaConstants.TOTAL_COUNT_STRING).getAsString();
            } catch (Exception e) {
                totalCount = StringUtils.EMPTY;
            }
        }
        return totalCount;
    }

    public String getDefaultSortingOption() {
        return defaultSortingOption;
    }

    public List<SortByOptionValueBean> getSortByOptionsValue() {
        return sortByOptionsValue;
    }

    public String getSelectedSortBy() {
        return selectedSortBy;
    }

    public String getSearchTerm() {
        return searchTerm;
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