package com.eaton.platform.integration.endeca.services;

import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONObject;

/**
 * The Interface EndecaAdvancedSearchService.
 */
public interface EndecaAdvancedSearchService {

    /**
     * Gets the advanced search Result
     *
     * @param endecaServiceRequestBean request bean
     * @param request request object
     * @return the search result as JSONObject
     */
    JSONObject getAdvanceSearchResults(EndecaServiceRequestBean endecaServiceRequestBean,SlingHttpServletRequest request );

    /**
     * Constructs the Endeca request object
     *
     * @param request SlingHttpServletRequest
     * @return EndecaServiceRequestBean
     */
    EndecaServiceRequestBean constructEndecaRequestBean(SlingHttpServletRequest request);

    /**
     * Returns What's New Endece Request Bean
     * @param request SlingHttpServletRequest
     * @param startDate startDate
     * @param endDate  endDate
     * @return return EndecaServiceRequestBean
     */
    EndecaServiceRequestBean constructWhatsNewEndecaRequestBean(SlingHttpServletRequest request, Long startDate, Long endDate);

}
