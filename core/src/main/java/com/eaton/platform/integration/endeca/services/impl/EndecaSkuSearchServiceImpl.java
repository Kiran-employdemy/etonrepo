package com.eaton.platform.integration.endeca.services.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.search.pojo.SkuSearchResponse;
import com.eaton.platform.core.search.service.SearchResponseMappingService;
import com.eaton.platform.core.search.service.SkuSearchService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.pojo.sku.EndecaSkuDocument;
import com.eaton.platform.integration.endeca.pojo.sku.EndecaSkuResponse;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.integration.endeca.util.EndecaUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * Endeca's implementation of SkuSearchService.
 */
@Component(service = SkuSearchService.class, immediate = true, name = EndecaSkuSearchServiceImpl.SERVICE_NAME,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Endeca Sku Search Implementation",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "EndecaSkuSearchServiceImpl"
        })
public class EndecaSkuSearchServiceImpl implements SkuSearchService {
    public static final String SERVICE_NAME = "sku-search-service";
    @Reference
    private EndecaRequestService endecaRequestService;
    @Reference
    private EndecaService endecaService;
    @Reference(target = "(serviceName=" + EndecaSkuResponseMappingService.SERVICE_NAME + ")")
    private SearchResponseMappingService<SkuSearchResponse, EndecaSkuResponse> endecaSkuResponseMappingService;

    @Override
    public Map<String, String> searchInventoryIdsPerSkuIdsForLocale(String locale, List<String> skuIds) throws IOException {
        Map<String, String> inventoryIdPerSkuId = new HashMap<>();
        EndecaServiceRequestBean endecaRequestBean = endecaRequestService
                .getDataSheetEndecaRequestBean(locale, skuIds);
        String skuDetailsResponse = endecaService.getSearchResultJson(endecaRequestBean);
        EndecaSkuResponse endecaResponse = new Gson().fromJson(skuDetailsResponse, EndecaSkuResponse.class);
        if (endecaResponse != null && endecaResponse.getDocuments() != null) {
            endecaResponse.getDocuments().forEach((EndecaSkuDocument document) -> skuIds.forEach((String skuId) -> {
                if (document != null) {
                    skuId = skuId.toUpperCase(Locale.getDefault());
                    String catalogNumber = document.getCatalogNumber();
                    if (skuId.equals(catalogNumber)) {
                        inventoryIdPerSkuId.put(skuId, document.getInventoryId());
                    }
                }
            }));
        }
        return inventoryIdPerSkuId;
    }

    @Override
    public SkuSearchResponse search(SlingHttpServletRequest request) throws IOException {
        String path = request.getResource().getPath();
        Page currentPage = request.getResourceResolver().getResource(path.substring(0, path.indexOf("/jcr:content"))).adaptTo(Page.class);
        EndecaServiceRequestBean endecaRequestBean = endecaRequestService
                .getEndecaRequestBean(currentPage, request.getRequestPathInfo().getSelectors(), StringUtils.EMPTY);
        EndecaUtil.resetLanguageAndCountryFilters(endecaRequestBean, currentPage);
        String skuDetailsResponse = endecaService.getSearchResultJson(endecaRequestBean);
        return endecaSkuResponseMappingService.parseJsonStringAndMapToSearchResponse(skuDetailsResponse, request);
    }
}
