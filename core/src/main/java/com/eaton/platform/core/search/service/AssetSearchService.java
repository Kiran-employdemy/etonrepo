package com.eaton.platform.core.search.service;

import com.eaton.platform.core.search.pojo.AssetSearchResponse;
import org.apache.sling.api.SlingHttpServletRequest;

import java.io.IOException;

/**
 * Eaton Search Service
 * Gateway for all search related methods.
 * For now, it will call the legacy search service
 */
public interface AssetSearchService {
    /**
     * Performs the search, whether it is on AEM, external search engine, etc...
     *
     * @param request to use for extracting the properties that define the search query
     * @return the result of the search
     * @throws IOException when calling the legacy search service
     */
    AssetSearchResponse search(SlingHttpServletRequest request) throws IOException;
}
