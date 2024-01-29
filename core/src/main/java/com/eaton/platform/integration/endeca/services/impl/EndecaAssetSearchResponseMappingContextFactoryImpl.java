package com.eaton.platform.integration.endeca.services.impl;

import com.eaton.platform.core.bean.secure.NewAdvancedSearchFacetConfiguration;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.models.secure.AdvancedSearchImageUrlFactoryImpl;
import com.eaton.platform.core.models.secure.AdvancedSearchModel;
import com.eaton.platform.core.search.api.FacetConfiguration;
import com.eaton.platform.core.search.api.AssetSearchResponseMappingContext;
import com.eaton.platform.core.search.api.AssetSearchResponseMappingContextFactory;
import com.eaton.platform.core.search.impl.AssetSearchResponseMappingContextImpl;
import com.eaton.platform.core.search.impl.AdvancedSearchResultMappingContextImpl;
import com.eaton.platform.core.search.impl.FacetGroupMappingContextImpl;
import com.eaton.platform.core.search.impl.TranslationMappingContextImpl;
import com.eaton.platform.integration.endeca.helpers.EndecaFacetValueIdsProviderImpl;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.util.FileSizeToHumanReadableStringConverter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Endeca's Service implementation of the SearchResponseMappingContextFactory for Advanced Search
 */
@Component(service = AssetSearchResponseMappingContextFactory.class, immediate = true,
        property = {
                AEMConstants.SERVICE_NAME + EndecaAssetSearchResponseMappingContextFactoryImpl.SERVICE_NAME,
                AEMConstants.SERVICE_DESCRIPTION + "Advanced Search Context Mapping Factory",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "SearchResponseMappingContextFactory"
        })
public class EndecaAssetSearchResponseMappingContextFactoryImpl implements AssetSearchResponseMappingContextFactory<FacetGroupMappingContextImpl
        , AdvancedSearchResultMappingContextImpl, TranslationMappingContextImpl, EndecaFacetValueIdsProviderImpl> {

    public static final String SERVICE_NAME = "advanced-search-context-factory";
    @Reference
    private EndecaConfig endecaConfig;
    private LocalDate now;
    private ZoneId zoneId;

    public EndecaAssetSearchResponseMappingContextFactoryImpl() {
        //needed for AEM
    }

    /**
     * Constructor taking as arguments:
     * @param endecaConfig to set
     */
    public EndecaAssetSearchResponseMappingContextFactoryImpl(EndecaConfig endecaConfig) {
        this.endecaConfig = endecaConfig;
    }

    @Override
    public AssetSearchResponseMappingContext<FacetGroupMappingContextImpl, AdvancedSearchResultMappingContextImpl, EndecaFacetValueIdsProviderImpl> create(SlingHttpServletRequest request
            , SiteResourceSlingModel siteConfig, Locale locale) {
        FacetGroupMappingContextImpl facetGroupMappingContext = new FacetGroupMappingContextImpl(getFacetConfigurations(request, siteConfig), NewAdvancedSearchFacetConfiguration.defaultConfiguration()
                , new TranslationMappingContextImpl(request, locale));
        AdvancedSearchResultMappingContextImpl advancedSearchResultMappingContext = getAdvancedSearchResultMappingContext(new TranslationMappingContextImpl(request, locale), request, siteConfig);
        return new AssetSearchResponseMappingContextImpl<>(facetGroupMappingContext, advancedSearchResultMappingContext, siteConfig
                , new EndecaFacetValueIdsProviderImpl(endecaConfig.getConfigServiceBean()), new EndecaAdvancedSearchLoadMoreOffsetProvider(request));
    }

    private AdvancedSearchResultMappingContextImpl getAdvancedSearchResultMappingContext(TranslationMappingContextImpl translationHelper
            , SlingHttpServletRequest request, SiteResourceSlingModel siteConfig) {
        return new AdvancedSearchResultMappingContextImpl()
                .now(now)
                .zoneId(zoneId)
                .translationHelper(translationHelper)
                .request(request)
                .siteConfig(siteConfig)
                .defaultImageUrlFactory(new AdvancedSearchImageUrlFactoryImpl())
                .fileSizeToHumanReadableStringConverter(new FileSizeToHumanReadableStringConverter());
    }

    public void setNow(LocalDate now) {
        this.now = now;
    }

    @Override
    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    private static List<FacetConfiguration> getFacetConfigurations(SlingHttpServletRequest request, SiteResourceSlingModel siteConfig) {
        Resource advanceSearchResource = getAdvancedSearchResource(request.getResource());
        if (advanceSearchResource == null) {
            throw new IllegalStateException("This should not happen, this can only be used by a page with an advanced search component");
        }
        AdvancedSearchModel advancedSearchModel = advanceSearchResource.adaptTo(AdvancedSearchModel.class);
        if (advancedSearchModel == null) {
            throw new IllegalStateException("Adapting the advanced search component to the appropriate model should not yield null");
        }
        if (advancedSearchModel.getAdvancedSearchFacetWhiteListBeanList().isEmpty()) {
            return siteConfig.getFacetGroupsList().stream().map(NewAdvancedSearchFacetConfiguration::of).collect(Collectors.toList());
        }
        return advancedSearchModel.getAdvancedSearchFacetWhiteListBeanList();
    }

    private static Resource getAdvancedSearchResource(Resource resource) {
        if (resource.isResourceType(AdvancedSearchModel.RESOURCE_TYPE)) {
            return resource;
        }
        for (Resource childResource : resource.getChildren()) {
            Resource advancedSearchResource = getAdvancedSearchResource(childResource);
            if (advancedSearchResource == null) {
                continue;
            }
            return advancedSearchResource;
        }
        return null;
    }

}
