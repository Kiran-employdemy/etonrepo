package com.eaton.platform.core.search.service;

import com.eaton.platform.core.search.pojo.SkuSearchResponse;
import org.apache.sling.api.SlingHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SkuSearchService {
    Map<String, String> searchInventoryIdsPerSkuIdsForLocale(String locale, List<String> skuIds) throws IOException;

    SkuSearchResponse search(SlingHttpServletRequest request) throws IOException;
}
