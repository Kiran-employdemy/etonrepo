package com.eaton.platform.integration.endeca.services.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.search.pojo.SkuSearchResponse;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.integration.endeca.pojo.sku.EndecaSkuResponse;
import com.google.gson.Gson;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class EndecaSkuResponseMappingServiceTest {
    @InjectMocks
    EndecaSkuResponseMappingService endecaSkuResponseMappingService = new EndecaSkuResponseMappingService();
    @Mock
    AdminService adminService;
    @Mock
    EatonSiteConfigService siteConfigService;
    @Mock
    ProductFamilyDetailService productFamilyDetailService;
    Page currentPage;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    Resource componentResource;
    private SiteResourceSlingModel siteResourceSlingModel;
    private SiteResourceSlingModel siteResourceSlingModelWithoutGlobalAttributes;
    private EndecaSkuResponse skuDetailsResponseBean;
    private EndecaSkuResponse skuDetailsResponseBeanWithCID;
    private EndecaSkuResponse skuDetailsResponseBeanMultipleCountriesNoValue;
    private EndecaSkuResponse skuDetailsResponseBeanMultipleCountriesTwoValues;
    private EndecaSkuResponse skuDetailsResponseBeanMultipleCountriesOneGlobal;
    private ProductFamilyPIMDetails productFamilyPIMDetails;
    private ProductFamilyPIMDetails productFamilyPIMDetailsWithoutTaxonomy;
    private ProductFamilyPIMDetails productFamilyPIMDetailsWithCID;
    private ResourceResolver resourceResolver;

    @BeforeEach
    void setUp(AemContext aemContext) {
        aemContext.addModelsForClasses(SiteResourceSlingModel.class);
        Resource skuPageResource = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("sku-page-en-us.json")), "/content/eaton/us/en-us/skuPage");
        Resource resource = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("siteconfig-en-us.json")), "/etc/cloudservices/siteconfig/en_us");
        aemContext.assetManager().createAsset("/content/dam/eaton/resources/endeca-stub-response/endecaSKUResponseImg.xsd", Objects.requireNonNull(this.getClass().getResourceAsStream("CID.xsd")), "application/octet-stream", true);
        aemContext.assetManager().createAsset("/content/dam/eaton/resources/endeca-stub-response/CID.xsd", Objects.requireNonNull(this.getClass().getResourceAsStream("product-sku-pdh.xsd")), "application/octet-stream", true);
        siteResourceSlingModel = Objects.requireNonNull(resource.getChild("jcr:content")).adaptTo(SiteResourceSlingModel.class);
        siteResourceSlingModelWithoutGlobalAttributes = Objects.requireNonNull(resource.getChild("jcr:content")).adaptTo(SiteResourceSlingModel.class);
        Objects.requireNonNull(siteResourceSlingModelWithoutGlobalAttributes).setGlobalAttributeList(Collections.emptyList());
        resourceResolver = aemContext.resourceResolver();
        skuDetailsResponseBeanWithCID = new Gson().fromJson(getInputStreamReader("sku-response-from-endeca-with-CID.json"), EndecaSkuResponse.class);
        skuDetailsResponseBean = new Gson().fromJson(getInputStreamReader("sku-response-from-endeca.json"), EndecaSkuResponse.class);
        skuDetailsResponseBeanMultipleCountriesNoValue = new Gson().fromJson(getInputStreamReader("sku-endeca-response-no-value-for-warranty.json"), EndecaSkuResponse.class);
        skuDetailsResponseBeanMultipleCountriesTwoValues = new Gson().fromJson(getInputStreamReader("sku-endeca-response-two-possible-value-for-warranty.json"), EndecaSkuResponse.class);
        skuDetailsResponseBeanMultipleCountriesOneGlobal = new Gson().fromJson(getInputStreamReader("sku-endeca-response-global-value-for-warranty.json"), EndecaSkuResponse.class);
        productFamilyPIMDetails = new Gson().fromJson(getInputStreamReader("product-family-pim-page-details.json"), ProductFamilyPIMDetails.class);
        productFamilyPIMDetailsWithoutTaxonomy = new Gson().fromJson(getInputStreamReader("product-family-pim-page-details-no-taxonomy.json"), ProductFamilyPIMDetails.class);
        productFamilyPIMDetailsWithCID = new Gson().fromJson(getInputStreamReader("product-family-pim-page-details-with-CID.json"), ProductFamilyPIMDetails.class);
        when(adminService.getReadService()).thenReturn(resourceResolver);
        when(request.getResource()).thenReturn(skuPageResource);
        currentPage = skuPageResource.adaptTo(Page.class);
    }

    private InputStreamReader getInputStreamReader(String name) {
        return new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream(name)), StandardCharsets.UTF_8);
    }

    @Test
    void testParseJsonStringAndMapToSearchResponseWhenSiteConfigIsPresent()  {
        when(siteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.ofNullable(siteResourceSlingModel));
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean("38100536","3517494", currentPage)).thenReturn(productFamilyPIMDetails);
        SkuSearchResponse skuSearchResponse = endecaSkuResponseMappingService.parseJsonStringAndMapToSearchResponse(skuDetailsResponseBean.toString(), request);
        String expected = new Gson().fromJson(getInputStreamReader("expected-product-specifications-model.json"), SkuSearchResponse.class).toString();
        assertEquals(expected, skuSearchResponse.toString(), "should be equal to expected string");
    }

    @Test
    void testParseJsonStringAndMapToSearchResponseWhenSiteConfigIsPresentFromComponentPath()  {
        when(request.getResource()).thenReturn(componentResource);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(componentResource.getPath()).thenReturn("/content/eaton/us/en-us/skuPage/jcr:content/responsivegrid/blah/blah");
        when(siteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.ofNullable(siteResourceSlingModel));
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean("38100536","3517494", currentPage)).thenReturn(productFamilyPIMDetails);
        SkuSearchResponse skuSearchResponse = endecaSkuResponseMappingService.parseJsonStringAndMapToSearchResponse(skuDetailsResponseBean.toString(), request);
        String expected = new Gson().fromJson(getInputStreamReader("expected-product-specifications-model.json"), SkuSearchResponse.class).toString();
        assertEquals(expected, skuSearchResponse.toString(), "should be equal to expected string");
    }
    @Test
    void testParseJsonStringAndMapToSearchResponseWhenSiteConfigIsAbsent() {
        when(siteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.ofNullable(siteResourceSlingModelWithoutGlobalAttributes));
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean("38100536","3517494", currentPage)).thenReturn(productFamilyPIMDetails);
        SkuSearchResponse skuSearchResponse = endecaSkuResponseMappingService.parseJsonStringAndMapToSearchResponse(skuDetailsResponseBean.toString(), request);
        String expected = new Gson().fromJson(getInputStreamReader("expected-product-specifications-model-all-global-attributes.json"), SkuSearchResponse.class).toString();
        assertEquals(expected, skuSearchResponse.toString(), "should be equal to expected string");
    }
    @Test
    void testParseJsonStringAndMapToSearchResponseWhenNoPimNode() {
        when(siteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.ofNullable(siteResourceSlingModel));
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean("38100536","3517494", currentPage)).thenReturn(productFamilyPIMDetailsWithoutTaxonomy);
        SkuSearchResponse skuSearchResponse = endecaSkuResponseMappingService.parseJsonStringAndMapToSearchResponse(skuDetailsResponseBean.toString(), request);
        String expected = new Gson().fromJson(getInputStreamReader("expected-product-specifications-model-default-taxonomy.json"), SkuSearchResponse.class).toString();
        assertEquals(expected, skuSearchResponse.toString(), "should be equal to expected string");
    }
    @Test
    void testParseJsonStringAndMapToSearchResponseWhenSiteConfigIsPresentAndCIDData() {
        when(siteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.ofNullable(siteResourceSlingModel));
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean("29291314", "3394626", currentPage)).thenReturn(productFamilyPIMDetailsWithCID);
        SkuSearchResponse skuSearchResponse = endecaSkuResponseMappingService.parseJsonStringAndMapToSearchResponse(skuDetailsResponseBeanWithCID.toString(), request);
        String expected = new Gson().fromJson(getInputStreamReader("expected-product-specifications-model-CID.json"), SkuSearchResponse.class).toString();
        assertEquals(expected, skuSearchResponse.toString(), "should be equal to expected string");
    }

    @Test
    void testParseJsonStringAndMapToSearchResponseWhenSiteConfigIsPresentAndValuesWithCountryNoValues() {
        when(siteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.ofNullable(siteResourceSlingModel));
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean("29774088", "3394640", currentPage)).thenReturn(productFamilyPIMDetailsWithCID);
        SkuSearchResponse skuSearchResponse = endecaSkuResponseMappingService.parseJsonStringAndMapToSearchResponse(skuDetailsResponseBeanMultipleCountriesNoValue.toString(), request);
        String expected = new Gson().fromJson(getInputStreamReader("expected-product-specifications-model-no-possible-values.json"), SkuSearchResponse.class).toString();
        assertEquals(expected, skuSearchResponse.toString(), "should be equal to expected string");
    }

    @Test
    void testParseJsonStringAndMapToSearchResponseWhenSiteConfigIsPresentAndValuesWithCountryTwoValues() {
        when(siteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.ofNullable(siteResourceSlingModel));
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean("29774088", "3394640", currentPage)).thenReturn(productFamilyPIMDetailsWithCID);
        SkuSearchResponse skuSearchResponse = endecaSkuResponseMappingService.parseJsonStringAndMapToSearchResponse(skuDetailsResponseBeanMultipleCountriesTwoValues.toString(), request);
        String expected = new Gson().fromJson(getInputStreamReader("expected-product-specifications-model-2-possible-values.json"), SkuSearchResponse.class).toString();
        assertEquals(expected, skuSearchResponse.toString(), "should be equal to expected string");
    }

    @Test
    void testParseJsonStringAndMapToSearchResponseWhenSiteConfigIsPresentAndValuesWithCountryOneGlobal() {
        when(siteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.ofNullable(siteResourceSlingModel));
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean("29774088", "3394640", currentPage)).thenReturn(productFamilyPIMDetailsWithCID);
        SkuSearchResponse skuSearchResponse = endecaSkuResponseMappingService.parseJsonStringAndMapToSearchResponse(skuDetailsResponseBeanMultipleCountriesOneGlobal.toString(), request);
        String expected = new Gson().fromJson(getInputStreamReader("expected-product-specifications-model-global-value.json"), SkuSearchResponse.class).toString();
        assertEquals(expected, skuSearchResponse.toString(), "should be equal to expected string");
    }

}