package com.eaton.platform.integration.endeca.services.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.models.secure.AdvancedSearchModel;
import com.eaton.platform.core.search.api.AssetSearchResponseMappingContextFactory;
import com.eaton.platform.core.search.impl.AdvancedSearchResultMappingContextImpl;
import com.eaton.platform.core.search.impl.FacetGroupMappingContextImpl;
import com.eaton.platform.core.search.impl.TranslationMappingContextImpl;
import com.eaton.platform.core.search.pojo.AssetSearchResponse;
import com.eaton.platform.core.search.service.SearchResponseMappingService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.integration.endeca.helpers.EndecaFacetValueIdsProviderImpl;
import com.eaton.platform.integration.endeca.pojo.asset.EndecaAssetResponse;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Endeca's implementation of the SearchResponseMappingService for Advanced Search
 */
@Component(service = SearchResponseMappingService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_NAME + EndecaAssetSearchResponseMappingService.SERVICE_NAME,
                AEMConstants.SERVICE_DESCRIPTION + "Endeca Advanced Search Json Response Mapping Service",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "EndecaResponseMappingService"
        })
public class EndecaAssetSearchResponseMappingService implements SearchResponseMappingService<AssetSearchResponse, EndecaAssetResponse> {

    public static final String SERVICE_NAME = "endeca-asset-search-mapping-service";
    @Reference
    private EatonSiteConfigService eatonSiteConfigService;
    @Reference(target = "(serviceName=" + EndecaAssetSearchResponseMappingContextFactoryImpl.SERVICE_NAME + ")")
    private AssetSearchResponseMappingContextFactory<FacetGroupMappingContextImpl, AdvancedSearchResultMappingContextImpl, TranslationMappingContextImpl
                , EndecaFacetValueIdsProviderImpl> searchResponseMappingContextFactory;
    private LocalDate localDate;
    private ZoneId zoneId;

    public EndecaAssetSearchResponseMappingService() {
        //needed for AEM
    }

    /**
     * Constructor taking as arguments:
     * @param eatonSiteConfigService to set
     * @param searchResponseMappingContextFactory to set
     * @param now to set
     */
    public EndecaAssetSearchResponseMappingService(EatonSiteConfigService eatonSiteConfigService, AssetSearchResponseMappingContextFactory<FacetGroupMappingContextImpl
                , AdvancedSearchResultMappingContextImpl, TranslationMappingContextImpl, EndecaFacetValueIdsProviderImpl> searchResponseMappingContextFactory, LocalDate now, ZoneId zoneId) {
        this.eatonSiteConfigService = eatonSiteConfigService;
        this.searchResponseMappingContextFactory = searchResponseMappingContextFactory;
        this.localDate = now;
        this.zoneId = zoneId;
    }

    @Override
    public AssetSearchResponse parseJsonStringAndMapToSearchResponse(String json, SlingHttpServletRequest request) {
        EndecaAssetResponse endecaResponse = new Gson().fromJson(json, getVendorResponseClass());
        Resource resource = request.getResource();
        AdvancedSearchModel advancedSearchModel = resource.adaptTo(AdvancedSearchModel.class);
        Page currentPage = Objects.requireNonNull(advancedSearchModel).getCurrentPage();
        Locale language = currentPage.getLanguage();
        SiteResourceSlingModel siteResourceSlingModel = retrieveSiteConfig(currentPage);
        searchResponseMappingContextFactory.setNow(getNow());
        searchResponseMappingContextFactory.setZoneId(getZoneId());
        return AssetSearchResponse.of(endecaResponse, searchResponseMappingContextFactory.create(request, siteResourceSlingModel, language));
    }

    private SiteResourceSlingModel retrieveSiteConfig(Page page) {
        Optional<SiteResourceSlingModel> siteConfigOptional = eatonSiteConfigService.getSiteConfig(page);
        return siteConfigOptional.orElse(null);
    }

    public LocalDate getNow() {
        if (localDate == null) {
            localDate = LocalDate.now();
        }
        return localDate;
    }

    public ZoneId getZoneId() {
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault();
        }
        return zoneId;
    }

    @Override
    public Class<EndecaAssetResponse> getVendorResponseClass() {
        return EndecaAssetResponse.class;
    }
}
