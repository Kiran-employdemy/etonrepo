package com.eaton.platform.integration.endeca.services.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.search.impl.SkuSearchRecordMappingContextImpl;
import com.eaton.platform.core.search.pojo.SkuSearchResponse;
import com.eaton.platform.core.search.service.SearchResponseMappingService;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.integration.endeca.pojo.sku.EndecaSkuResponse;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Objects;

@Component(service = SearchResponseMappingService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_NAME + EndecaSkuResponseMappingService.SERVICE_NAME,
                AEMConstants.SERVICE_DESCRIPTION + "Endeca Sku Json Response Mapping Service",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "EndecaSkuResponseMappingService"
        })
public class EndecaSkuResponseMappingService implements SearchResponseMappingService<SkuSearchResponse, EndecaSkuResponse> {
    public static final String SERVICE_NAME = "endeca-sku-mapping-service";
    @Reference
    private AdminService adminService;
    @Reference
    private EatonSiteConfigService eatonSiteConfigService;
    @Reference
    private ProductFamilyDetailService productFamilyDetailService;

    @Override
    public SkuSearchResponse parseJsonStringAndMapToSearchResponse(String json, SlingHttpServletRequest request) {
        EndecaSkuResponse endecaSkuResponse = new Gson().fromJson(json, getVendorResponseClass());
        String path = request.getResource().getPath();
        Page currentPage = getCurrentPage(request, path);
        SiteResourceSlingModel siteResourceSlingModel = eatonSiteConfigService.getSiteConfig(currentPage).orElse(null);
        return SkuSearchResponse.of(endecaSkuResponse, new SkuSearchRecordMappingContextImpl<>(adminService, siteResourceSlingModel, productFamilyDetailService, currentPage, request));
    }

    private static Page getCurrentPage(SlingHttpServletRequest request, String path) {
        Page currentPage;
        if (path.contains("/jcr:content")) {
            Resource containingResource = request.getResourceResolver().getResource(path.substring(0, path.indexOf("/jcr:content")));
            currentPage = Objects.requireNonNull(containingResource).adaptTo(Page.class);
        } else {
            currentPage = request.getResource().adaptTo(Page.class);
        }
        return currentPage;
    }

    @Override
    public Class<EndecaSkuResponse> getVendorResponseClass() {
        return EndecaSkuResponse.class;
    }
}
