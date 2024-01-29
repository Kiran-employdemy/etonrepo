package com.eaton.platform.integration.endeca.services.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.search.pojo.AssetSearchResponse;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.util.ResourceBundleFixtures;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.pojo.asset.EndecaAssetResponse;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.google.gson.Gson;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class EndecaAssetSearchResponseMappingServiceTest {

    public static final int NUMBER_OF_DAYS = 30;
    public static final int FACET_VALUE_COUNT = 7;
    public static final int NUMBER_OF_RESULTS_PER_PAGE = 60;
    @Mock
    EndecaConfigServiceBean endecaConfigServiceBean;
    @Mock
    EndecaConfig endecaConfig;
    @Mock
    EatonSiteConfigService eatonSiteConfigService;
    @Mock
    SiteResourceSlingModel siteConfigModel;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    Clock clock;
    @Mock
    RequestPathInfo requestPathInfo;
    Resource libraryResourceWithFacetConfig;
    Resource libraryResourceWithoutFacetConfig;
    private EndecaAssetSearchResponseMappingService endecaAdvancedSearchResponseMappingService;

    @BeforeEach
    void setUp(AemContext aemContext) {
        when(endecaConfig.getConfigServiceBean()).thenReturn(endecaConfigServiceBean);
        when(endecaConfigServiceBean.getServicesTabId()).thenReturn("850411755");
        when(endecaConfigServiceBean.getProductsTabId()).thenReturn("1316679294");
        when(endecaConfigServiceBean.getResourcesTabId()).thenReturn("968989543");
        when(endecaConfigServiceBean.getNewsTabId()).thenReturn("842808312");
        when(eatonSiteConfigService.getSiteConfig(any(Page.class))).thenReturn(Optional.of(siteConfigModel));
        when(siteConfigModel.getFacetValueCount()).thenReturn(FACET_VALUE_COUNT);
        when(slingHttpServletRequest.getResourceBundle(new Locale("en", "us"))).thenReturn(ResourceBundleFixtures.resourceBundle());
        aemContext.registerService(AdminService.class);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("eaton.tags.json")), "/content/cq:tags/eaton");
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("eaton.resources.tags.json")), "/content/cq:tags/eaton/resources");
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("eaton.search-tabs.tags.json")), "/content/cq:tags/eaton/search-tabs");
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("eaton.language.tags.json")), "/content/cq:tags/eaton/language");
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("instructions-me-replacement-contact-kit-il15-800-13.pdf.json")), "/content/dam/eaton/products/industrialcontrols-drives-automation-sensors/mill-type-dc-contactors/instructions-me-replacement-contact-kit-il15-800-13.pdf");
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("eaton.en-us.json")), "/content/eaton/us/en-us");
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("eaton.library-with-config.json")), "/content/eaton/us/en-us/products/wiring-devices-connectivity/library-with-facet-config");
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("eaton.library-without-config.json")), "/content/eaton/us/en-us/products/wiring-devices-connectivity/library-without-facet-config");
        libraryResourceWithFacetConfig = aemContext.resourceResolver().getResource("/content/eaton/us/en-us/products/wiring-devices-connectivity/library-with-facet-config/jcr:content/root/responsivegrid/advanced_search");
        libraryResourceWithoutFacetConfig = aemContext.resourceResolver().getResource("/content/eaton/us/en-us/products/wiring-devices-connectivity/library-without-facet-config/jcr:content/root/responsivegrid/advanced_search");
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(aemContext.resourceResolver());
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"searchTerm$","SortBy$", "pub_date_desc", "Facets$", "startDate$", "endDate$", "nocache"});
        EndecaAssetSearchResponseMappingContextFactoryImpl advancedSearchResponseMappingContextFactory = new EndecaAssetSearchResponseMappingContextFactoryImpl(endecaConfig);
        endecaAdvancedSearchResponseMappingService = new EndecaAssetSearchResponseMappingService(eatonSiteConfigService, advancedSearchResponseMappingContextFactory,LocalDate.of(2023, 8, 28), ZoneId.of("America/New_York"));
    }

    @Test
    @DisplayName("EndecaResponse should map correctly to json from endeca")
    void testThatEndecaResponseGsonIsMappedCorrectly() {
        resetMocks();
        EndecaAssetResponse endecaResponse = new Gson().fromJson(getInputStreamReader("endeca-response.json"), EndecaAssetResponse.class);
        Assertions.assertNotNull(endecaResponse, "endeca response should map correctly to json");
    }

    @Test
    @DisplayName("SearchResponse should map correctly to json used for advanced search")
    void testThatSearchGsonIsMappedCorrectly() {
        resetMocks();
        AssetSearchResponse searchResponse = new Gson().fromJson(getInputStreamReader("advanced-search-expected-response.json"), AssetSearchResponse.class);
        Assertions.assertNotNull(searchResponse, "search response should map correctly to json");
    }

    private void resetMocks() {
        Mockito.reset(clock, slingHttpServletRequest, requestPathInfo, endecaConfig, eatonSiteConfigService, endecaConfigServiceBean, siteConfigModel);
    }

    @Test
    void testThatMappingFromEndecaResponseToSearchResponseIsHappeningCorrectlyNoSecure(){
        when(siteConfigModel.getPageSize()).thenReturn(NUMBER_OF_RESULTS_PER_PAGE);
        when(siteConfigModel.getNoOfDays()).thenReturn(NUMBER_OF_DAYS);
        when(siteConfigModel.getUnitedStatesDateFormat()).thenReturn("true");
        when(siteConfigModel.getFacetGroupsList()).thenReturn(getFacetsConfiguration());
        when(slingHttpServletRequest.getResource()).thenReturn(libraryResourceWithoutFacetConfig);
        EndecaAssetResponse endecaResponse = new Gson().fromJson(getInputStreamReader("endeca-response-no-secure.json"), EndecaAssetResponse.class);
        AssetSearchResponse expectedSearchResponse = new Gson().fromJson(getInputStreamReader("advanced-search-expected-response-no-secure.json"), AssetSearchResponse.class);
        AssetSearchResponse actualSearchResponse = this.endecaAdvancedSearchResponseMappingService.parseJsonStringAndMapToSearchResponse(new Gson().toJson(endecaResponse), slingHttpServletRequest);
        Assertions.assertEquals(expectedSearchResponse, actualSearchResponse, "should be equal");
    }

    private InputStreamReader getInputStreamReader(String fileName) {
        return new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream(fileName)), Charset.defaultCharset());
    }

    @Test
    void testThatMappingFromEndecaResponseToSearchResponseIsHappeningCorrectlyWhenNoResults(){
        when(siteConfigModel.getFacetGroupsList()).thenReturn(getFacetsConfiguration());
        when(slingHttpServletRequest.getResource()).thenReturn(libraryResourceWithoutFacetConfig);
        EndecaAssetResponse endecaResponse = new Gson().fromJson(getInputStreamReader("endeca-response-no-results.json"), EndecaAssetResponse.class);
        AssetSearchResponse expectedSearchResponse = new Gson().fromJson(getInputStreamReader("advanced-search-expected-response-no-results.json"), AssetSearchResponse.class);
        AssetSearchResponse actualSearchResponse = this.endecaAdvancedSearchResponseMappingService.parseJsonStringAndMapToSearchResponse(new Gson().toJson(endecaResponse), slingHttpServletRequest);
        Assertions.assertEquals(expectedSearchResponse, actualSearchResponse, "should be equal");
    }
    @Test
    void testThatMappingFromEndecaResponseToSearchResponseIsHappeningCorrectlyWhenOtherSortOrderFromEndeca(){
        when(siteConfigModel.getPageSize()).thenReturn(NUMBER_OF_RESULTS_PER_PAGE);
        when(siteConfigModel.getNoOfDays()).thenReturn(NUMBER_OF_DAYS);
        when(siteConfigModel.getUnitedStatesDateFormat()).thenReturn("true");
        when(siteConfigModel.getFacetGroupsList()).thenReturn(getFacetsConfiguration());
        when(slingHttpServletRequest.getResource()).thenReturn(libraryResourceWithoutFacetConfig);
        EndecaAssetResponse endecaResponse = new Gson().fromJson(getInputStreamReader("endeca-response-no-secure-content-last.json"), EndecaAssetResponse.class);
        AssetSearchResponse expectedSearchResponse = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("advanced-search-expected-response-no-secure.json")), Charset.defaultCharset()), AssetSearchResponse.class);
        AssetSearchResponse actualSearchResponse = this.endecaAdvancedSearchResponseMappingService.parseJsonStringAndMapToSearchResponse(new Gson().toJson(endecaResponse), slingHttpServletRequest);
        Assertions.assertEquals(expectedSearchResponse, actualSearchResponse, "should be equal");
    }
    @Test
    void testThatMappingFromEndecaResponseToSearchResponseIsHappeningCorrectlySecure(){
        when(siteConfigModel.getPageSize()).thenReturn(NUMBER_OF_RESULTS_PER_PAGE);
        when(siteConfigModel.getNoOfDays()).thenReturn(NUMBER_OF_DAYS);
        when(siteConfigModel.getUnitedStatesDateFormat()).thenReturn("true");
        when(slingHttpServletRequest.getResource()).thenReturn(libraryResourceWithFacetConfig);
        when(slingHttpServletRequest.getResourceBundle(new Locale("en", "us"))).thenReturn(ResourceBundleFixtures.resourceBundle());
        EndecaAssetResponse endecaResponse = new Gson().fromJson(getInputStreamReader("endeca-response.json"), EndecaAssetResponse.class);
        AssetSearchResponse expectedSearchResponse = new Gson().fromJson(getInputStreamReader("advanced-search-expected-response.json"), AssetSearchResponse.class);
        AssetSearchResponse actualSearchResponse = this.endecaAdvancedSearchResponseMappingService.parseJsonStringAndMapToSearchResponse(new Gson().toJson(endecaResponse), slingHttpServletRequest);
        Assertions.assertEquals(expectedSearchResponse, actualSearchResponse, "should be equal");
    }

    @Test
    void testThatMappingFromEndecaResponseWithNumberOfDocumentsLessThanLoadMoreOffset(){
        when(siteConfigModel.getPageSize()).thenReturn(NUMBER_OF_RESULTS_PER_PAGE);
        when(siteConfigModel.getNoOfDays()).thenReturn(NUMBER_OF_DAYS);
        when(siteConfigModel.getUnitedStatesDateFormat()).thenReturn("true");
        when(slingHttpServletRequest.getResource()).thenReturn(libraryResourceWithFacetConfig);
        when(slingHttpServletRequest.getResourceBundle(new Locale("en", "us"))).thenReturn(ResourceBundleFixtures.resourceBundle());
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"searchTerm$","SortBy$", "pub_date_desc", "Facets$", "startDate$", "loadMoreOffset$60", "endDate$", "nocache"});
        EndecaAssetResponse endecaResponse = new Gson().fromJson(getInputStreamReader("endeca-response-less-than-loadmore.json"), EndecaAssetResponse.class);
        AssetSearchResponse expectedSearchResponse = new Gson().fromJson(getInputStreamReader("advanced-search-expected-response-less-than-loadmore.json"), AssetSearchResponse.class);
        AssetSearchResponse actualSearchResponse = this.endecaAdvancedSearchResponseMappingService.parseJsonStringAndMapToSearchResponse(new Gson().toJson(endecaResponse), slingHttpServletRequest);
        Assertions.assertEquals(expectedSearchResponse, actualSearchResponse, "should be equal");
    }

    @Test
    void testThatMappingFromEndecaResponseWithLoadMoreOffsetZeroAndOneMorePageNeeded(){
        when(siteConfigModel.getPageSize()).thenReturn(NUMBER_OF_RESULTS_PER_PAGE);
        when(siteConfigModel.getNoOfDays()).thenReturn(NUMBER_OF_DAYS);
        when(siteConfigModel.getUnitedStatesDateFormat()).thenReturn("true");
        when(slingHttpServletRequest.getResource()).thenReturn(libraryResourceWithFacetConfig);
        when(slingHttpServletRequest.getResourceBundle(new Locale("en", "us"))).thenReturn(ResourceBundleFixtures.resourceBundle());
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{"searchTerm$","SortBy$", "pub_date_desc", "Facets$", "startDate$", "loadMoreOffset$", "endDate$", "nocache"});
        EndecaAssetResponse endecaResponse = new Gson().fromJson(getInputStreamReader("endeca-response-less-than-loadmore.json"), EndecaAssetResponse.class);
        AssetSearchResponse expectedSearchResponse = new Gson().fromJson(getInputStreamReader("advanced-search-expected-response-one-page-extra.json"), AssetSearchResponse.class);
        AssetSearchResponse actualSearchResponse = this.endecaAdvancedSearchResponseMappingService.parseJsonStringAndMapToSearchResponse(new Gson().toJson(endecaResponse), slingHttpServletRequest);
        Assertions.assertEquals(expectedSearchResponse, actualSearchResponse, "should be equal");
    }

    private static List<FacetGroupBean> getFacetsConfiguration() {
        return Arrays.asList(facetConfiguration("content-type")
                ,facetConfiguration("eaton-secure_attributes")
                ,facetConfiguration("resources_marketing-resources")
                ,facetConfiguration("resources_technical-resources"));
    }

    private static FacetGroupBean facetConfiguration(String facet) {
        FacetGroupBean advancedSearchFacetWhiteListBean = new FacetGroupBean();
        advancedSearchFacetWhiteListBean.setFacetGroupId(facet);
        advancedSearchFacetWhiteListBean.setGridFacet(false);
        advancedSearchFacetWhiteListBean.setFacetSearchEnabled(false);
        advancedSearchFacetWhiteListBean.setSingleFacetEnabled(false);
        return advancedSearchFacetWhiteListBean;
    }
}
