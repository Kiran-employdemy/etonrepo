package com.eaton.platform.core.search.service;

import com.eaton.platform.core.search.api.VendorFacet;
import com.eaton.platform.core.search.api.VendorFacetGroup;
import com.eaton.platform.core.search.api.VendorFacetGroups;
import com.eaton.platform.core.search.api.VendorSearchRecord;
import com.eaton.platform.core.search.api.VendorSearchResponse;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Mapping service for the SearchResponse.
 * The specific implementation of this service is responsible for providing the correct implementation of the different contexts to be used
 * when mapping the VendorResponse to SearchResponse
 * @param <R> type of the response to map too
 * @param <T> type of the vendor implementation of response to map from
 */
public interface SearchResponseMappingService<R, T extends VendorSearchResponse<? extends VendorFacetGroups<? extends VendorFacetGroup<? extends VendorFacet>>,? extends VendorSearchRecord>> {
    /**
     * It parses the json it receives to the VendorResponse and uses the request to complete the mapping to SearchResponse.
     * @param json to parse
     * @param request to use
     * @return the mapped SearchResponse
     */
    R parseJsonStringAndMapToSearchResponse(String json, SlingHttpServletRequest request);

    Class<T> getVendorResponseClass();
}
