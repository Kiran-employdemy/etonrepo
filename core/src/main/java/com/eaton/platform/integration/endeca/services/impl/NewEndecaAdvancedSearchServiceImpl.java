package com.eaton.platform.integration.endeca.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.search.pojo.AssetSearchResponse;
import com.eaton.platform.core.search.service.SearchResponseMappingService;
import com.eaton.platform.core.search.service.AssetSearchService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.pojo.asset.EndecaAssetResponse;
import com.eaton.platform.integration.endeca.services.EndecaAdvancedSearchService;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;

/**
 * Endeca's implementation of SearchService for Advanced Search.
 */
@Component(service = AssetSearchService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_NAME + NewEndecaAdvancedSearchServiceImpl.SERVICE_NAME,
                AEMConstants.SERVICE_DESCRIPTION + "Endeca Advanced Search Implementation",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "NewEndecaAdvancedSearchServiceImpl"
        })
public class NewEndecaAdvancedSearchServiceImpl implements AssetSearchService {
    public static final String SERVICE_NAME = "advanced-search-service";
    @Reference
    private EndecaService endecaService;
    @Reference
    private EndecaAdvancedSearchService endecaAdvancedSearchService;
    @Reference(target = "(serviceName=" + EndecaAssetSearchResponseMappingService.SERVICE_NAME + ")")
    private SearchResponseMappingService<AssetSearchResponse, EndecaAssetResponse> endecaAdvancedSearchResponseMappingService;

    @Override
    public AssetSearchResponse search(SlingHttpServletRequest request) throws IOException {
        EndecaServiceRequestBean endecaServiceRequestBean = endecaAdvancedSearchService.constructEndecaRequestBean(request);
        String jsonResult = endecaService.getSearchResultJson(endecaServiceRequestBean);
        return endecaAdvancedSearchResponseMappingService.parseJsonStringAndMapToSearchResponse(jsonResult, request);
    }
}
