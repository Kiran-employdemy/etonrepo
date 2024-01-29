package com.eaton.platform.integration.endeca.services;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.crossreference.XRefResult;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityListResponseBean;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.bean.typeahead.TypeAheadSiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueSelectorResponse;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.io.IOException;
import java.util.List;

/**
 * The Interface EndecaService.
 */
public interface EndecaService {

	/**
     * Gets the product family list.
     *
     * @return the product family list
     */
    FamilyListResponseBean getProductFamilyList(EndecaServiceRequestBean endecaServiceRequestBean);

    /**
     * @param endecaServiceRequestBean The request to send to Endeca in order to retrieve a list of SKU's.
     * @param resource The resource to use as a basis for retrieve a Endeca cloud config.
     * @return The sku list with facet groups names converted into AEM appropriate names based upon the Endeca cloud config.
     */
    SKUListResponseBean getSKUList(EndecaServiceRequestBean endecaServiceRequestBean, Resource resource);

    /**
     * Gets the SKU list.
     *
     * @return the SKU list
     */
    SKUListResponseBean getSKUList(EndecaServiceRequestBean endecaServiceRequestBean);

    /**
     * Gets the SKU details.
     *
     * @return the SKU details
     */
    SKUDetailsResponseBean getSKUDetails(EndecaServiceRequestBean endecaServiceRequestBean);

    /**
     * Gets the SKU details.
     *
     * @return the SKU details
     */
    SKUDetailsResponseBean getProductCompareSKUList(EndecaServiceRequestBean endecaServiceRequestBean);
       /**
     * Gets the SKU details.
     *
     * @return the SKU details
     */
    ProductCompatibilityListResponseBean getProductCompatibilitySkuList(EndecaServiceRequestBean endecaServiceRequestBean,List<String> compatibilityExcelTable);


    /**
     * Gets the search keywords
     *
     * @return the search Keywords
     */
    TypeAheadSiteSearchResponse getSearchKeywords(EndecaServiceRequestBean endecaServiceRequestBean);


    /**
     * Gets the search keywords
     *
     * @return the search Keywords
     */
    SiteSearchResponse
    getSearchResults(EndecaServiceRequestBean endecaServiceRequestBean, SlingHttpServletRequest slingRequest, boolean isUnitedStatesDateFormat);
    String getSearchResultJson(EndecaServiceRequestBean endecaServiceRequestBean) throws IOException;

    /**
     * Gets the Clutch search results
     *
     * @return the Clutch options and clutch results
     */
    ClutchSelectorResponse getClutchToolDetails(EndecaServiceRequestBean endecaServiceRequestBean);

    /**
     * Gets the Torque search results
     *
     * @return the Torque options and clutch results
     */
    TorqueSelectorResponse getTorqueToolDetails(EndecaServiceRequestBean endecaServiceRequestBean);

    JsonObject getSubmittalResponse(EndecaServiceRequestBean endecaServiceRequestBean) throws Exception;

    List<XRefResult> getCrossReferenceBestMatches(EndecaServiceRequestBean endecaServiceRequestBean);
    List<XRefResult> getCrossReferencePartialMatches(EndecaServiceRequestBean endecaServiceRequestBean);
    JsonObject getCrossReferenceResponse(EndecaServiceRequestBean endecaServiceRequestBean, SlingHttpServletRequest slingRequest, Page currentPage);
}
