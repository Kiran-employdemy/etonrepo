package com.eaton.platform.core.services;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.models.SearchResultsModel;
import com.eaton.platform.core.models.productcompatibilitytool.Filter;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.ArrayList;
import java.util.List;

public interface EndecaRequestService {

    EndecaServiceRequestBean getEndecaRequestBean(final Page page,
                                                  final String[] selectors,
                                                  final String inventoryId);
    
    EndecaServiceRequestBean getProductCompareEndecaRequestBean(final Page page, final List<String> catalogNo);

    EndecaServiceRequestBean getEndecaRequestBean(final Page page,
                                                  final String[] selectors,
                                                  final String inventoryId,
                                                  final ProductGridSelectors productGridSelectors);

    EndecaServiceRequestBean getEndecaRequestBean(final Page page,
                                                  final String[] selectors,
                                                  final String inventoryId,
                                                  final ProductGridSelectors productGridSelectors,
                                                  final EndecaServiceRequestBean endecaServiceRequestBeanTabsCount, SlingHttpServletRequest request);

    EndecaServiceRequestBean getEndecaRequestBean(final Page page,
                                                  final String[] selectors,
                                                  final String inventoryId,
                                                  final ProductGridSelectors productGridSelectors,
                                                  SlingHttpServletRequest request);

    /**
     * overload for getSubmitBuilderEndecaRequestBean
     * @param page
     * @param resourceResolver
     * @param subCatergoryTags
     * @param facetValue
     * @param sortOrder
     * @param activeFacets
     * @param pageSize
     * @param startingRecord
     * @return
     */
    EndecaServiceRequestBean getSubmitBuilderEndecaRequestBean(final Page page, final ResourceResolver resourceResolver,
                                                               final String subCatergoryTags,
                                                               final String facetValue,
                                                               final String sortOrder,
                                                               final String activeFacets,
                                                               final int pageSize,
                                                               final int startingRecord);

    String getInventoryId(final Page page, final ResourceResolver resourceResolver);

    SearchResultsModel getSearchResultModel(final Resource contentResource);
    EndecaServiceRequestBean getProductCompatibilityEndecaRequestBean(final Page page,final String searchTerm, List<Filter> filterLists, final List<String> activeFacets);

    

    EndecaServiceRequestBean getProductFamilyEndecaRequestBean(final Page page,
                                                               final List<String> inventoryIds,
                                                               final List<String> returnFacetsFor,
                                                               final List<String> activeFacets,
                                                               final String sortBy,
                                                               final String sortFacet,
                                                               final String productType,
                                                               final int pageSize,
                                                               final int startingRecord);

    EndecaServiceRequestBean getProductFamilyEndecaRequestBean(final Page page,
                                                               final List<String> activeFacets,
                                                               final String sortBy,
                                                               final String sortFacet);

    EndecaServiceRequestBean getProductFamilyEndecaRequestBean(final Page page,
                                                               final List<String> activeFacets,
                                                               final String sortBy,
                                                               final String sortFacet,
                                                               final String productType);

    /**
     * Overload for getProductFamilySiteMapEndecaRequestBean
     * @param page
     * @param inventoryIds
     * @param startingRecord
     * @return
     */
    EndecaServiceRequestBean getProductFamilySiteMapEndecaRequestBean(final Page page,
																final List<String> inventoryIds,
																final int startingRecord);
    
    EndecaServiceRequestBean getOrphanSkuSiteMapEndecaRequestBean(final Page page, final int startingRecord);

    EndecaServiceRequestBean getAltProductFamilyEndecaRequestBean(final Page page,
                                                                  final List<String> activeFacets,
                                                                  final String sortBy,
                                                                  final String sortFacet,
                                                                  final String productType,
                                                                  final int pageSize);

	EndecaServiceRequestBean getCrossReferenceEndecaRequestBean(final Page page, final List<String> activeFacets, final String searchTerm, final String startingRecord, final String pageSize, final String sortBy, ArrayList<String> returnFacetFor);

    EndecaServiceRequestBean getDataSheetEndecaRequestBean(String locale, List<String> catalogNo);
}
