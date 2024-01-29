package com.eaton.platform.integration.salesforce.services.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.integration.salesforce.services.SalesforceKnowledgeSearchService;
import com.eaton.platform.integration.salesforce.services.models.SalesforceKnowledgeSearchModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesforceKnowledgeSearchModelTest {

    private final String pagePath = "/content/eaton/us/en-us/catalog/backup-power-ups-surge-it-power-distribution/eaton-3s-ups";
    private final String productFamilyName = "Eaton 3S UPS";
    private final String[] skuSelector = {"3S750"};
    private final String salesforceSiteUrl = "https://eaton--rollhier.sandbox.my.site.com/EatonKnowledge";
    private final String expectedSalesforceSiteUrl = salesforceSiteUrl;
    private final String salesforceSearchPath = "/s/global-search";
    private final String expectedUrlForSearchWithSearchTerm = salesforceSiteUrl + salesforceSearchPath + CommonConstants.SLASH_STRING + "Eaton+3S+UPS";
    private final String imagePath = "/content/dam/eaton/resources/knowledgeSearchIcon.png";
    private final String[] targetTags = {"eaton/integrations/nanorep/electronic-components-nanorep","eaton/integrations/nanorep/b-line-nanorep", "eaton/integrations/nanorep/filtration-kb-widget","eaton/integrations/nanorep/vehicle-nanorep", "eaton/integrations/nanorep/bussmann-nanorep"};

    private String[] nonMatchingSourceTagsForPim = {"/content/cq:tags/eaton/product-attributes/input-power/single-phase", "/content/cq:tags/eaton/integrations/chat/electrical-avery-creek", "/content/cq:tags/eaton/eaton-brand/industry/PQD-1p-ups"};

    private String[] nonMatchingSourceTagsForPageProperties = {"eaton:product-attributes/input-power/single-phase", "eaton:integrations/chat/electrical-avery-creek", "eaton:eaton-brand/industry/PQD-1p-ups"};

    @InjectMocks
    SalesforceKnowledgeSearchModel salesforceKnowledgeSearchModel = new SalesforceKnowledgeSearchModel();

    @Mock
    SalesforceKnowledgeSearchService salesforceKnowledgeSearchService;
    @Mock
    Page currentPage;
    @Mock
    ProductFamilyDetails productFamilyDetails;
    @Mock
    ProductFamilyDetailService productFamilyDetailService;
    @Mock
    ProductFamilyPIMDetails productFamilyPIMDetails;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    EndecaRequestService endecaRequestService;
    @Mock
    EndecaService endecaService;
    @Mock
    EndecaServiceRequestBean endecaServiceRequestBean;
    @Mock
    SKUDetailsResponseBean skuDetailsResponseBean;
    @Mock
    SKUResponseBean skuResponseBean;
    @Mock
    SKUDetailsBean skuDetailsBean;
    @Mock
    RequestPathInfo requestPathInfo;
    @Mock
    ValueMap valueMap;
    @Mock
    Object object;

    @BeforeEach
    void setup() {

        when(salesforceKnowledgeSearchService.isGlobalEnabled()).thenReturn(true);
        when(currentPage.getPath()).thenReturn(pagePath);
        when(currentPage.getProperties()).thenReturn(valueMap);
        when(currentPage.getProperties().get(CommonConstants.PAGE_TYPE)).thenReturn(object);

    }

    @Nested
    class ProductFamilyTest {
        @BeforeEach
        void setup() {

            when(currentPage.getProperties().get(CommonConstants.PAGE_TYPE).toString()).thenReturn(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE);
            when(productFamilyDetailService.getProductFamilyDetailsBean(currentPage)).thenReturn(productFamilyDetails);

            salesforceKnowledgeSearchModel.init();

        }

        @Test
        @DisplayName("getKnowledgeSearchUrl should return salesforce search url with product family's name as the encoded search term")
        void testGetKnowledgeSearchUrlWhenProductFamilyNonNull() {
            when(salesforceKnowledgeSearchService.getSalesforceSiteUrl()).thenReturn(salesforceSiteUrl);
            when(productFamilyDetails.getProductFamilyName()).thenReturn(productFamilyName);
            when(salesforceKnowledgeSearchService.getSalesforceSearchPath()).thenReturn(salesforceSearchPath);

            Assertions.assertEquals(salesforceKnowledgeSearchModel.getKnowledgeSearchUrl(), expectedUrlForSearchWithSearchTerm, "should be salesforce search url with search term");

        }

        @Test
        @DisplayName("getKnowledgeSearchUrl should return salesforce site home url when product family's name not found")
        void testGetKnowledgeSearchUrlWhenProductFamilyIsNull() {
            when(salesforceKnowledgeSearchService.getSalesforceSiteUrl()).thenReturn(salesforceSiteUrl);
            when(productFamilyDetails.getProductFamilyName()).thenReturn(null);

            Assertions.assertEquals(salesforceKnowledgeSearchModel.getKnowledgeSearchUrl(), expectedSalesforceSiteUrl, "should be salesforce site home url");

        }


        @Test
        @DisplayName("isLocalEnabled should be enabled when 1 or more target tags match any of the cq:tags in product family pim tags")
        void testIsLocalEnabledWhenTargetTagMatchesProductFamilyPimTag() {


            List<String> matchPimTags = new ArrayList<>(Arrays.asList(nonMatchingSourceTagsForPim));
            matchPimTags.add("/content/cq:tags/eaton/integrations/nanorep/bussmann-nanorep");

            List<String> configTargetTags = new ArrayList<>(Arrays.asList(targetTags));

            when(salesforceKnowledgeSearchService.getTargetTags()).thenReturn(configTargetTags);
            when(productFamilyDetails.getPimTags()).thenReturn(matchPimTags);

            Assertions.assertTrue(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be enabled when a target tag matches a tag from product family pim tags for other pages");

        }

        @Test
        @DisplayName("isLocalEnabled should be disabled when no target tags match any of the cq:tags in product family pim tags")
        void testIsLocalEnabledWhenTargetTagDoesNotMatchProductFamilyPimTag() {

            List<String> configTargetTags = new ArrayList<>(Arrays.asList(targetTags));
            List<String> nonMatchingPimTags = new ArrayList<>(Arrays.asList(nonMatchingSourceTagsForPim));

            when(salesforceKnowledgeSearchService.getTargetTags()).thenReturn(configTargetTags);
            when(productFamilyDetails.getPimTags()).thenReturn(nonMatchingPimTags);

            Assertions.assertFalse(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be disabled when a target tag does not match a tag from product family pim tags for other pages");

        }

        @Test
        @DisplayName("isLocalEnabled should be disabled when 1 or more target tags partially match any of the cq:tags in product family pim tags")
        void testIsLocalEnabledWhenTargetTagPartiallyMatchesProductFamilyPimTag() {


            List<String> partialMatchPimTags = new ArrayList<>(Arrays.asList(nonMatchingSourceTagsForPim));
            partialMatchPimTags.add("/content/cq:tags/eaton/integrations");

            List<String> configTargetTags = new ArrayList<>(Arrays.asList(targetTags));

            when(salesforceKnowledgeSearchService.getTargetTags()).thenReturn(configTargetTags);
            when(productFamilyDetails.getPimTags()).thenReturn(partialMatchPimTags);

            Assertions.assertFalse(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be disabled when a target tag partially matches a tag from product family pim tags for other pages");

        }

        @Test
        @DisplayName("isLocalEnabled should be disabled when product family pim tags not found")
        void testIsLocalEnabledWhenProductFamilyPimTagsIsNull() {

            when(productFamilyDetails.getPimTags()).thenReturn(null);

            Assertions.assertFalse(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be false when product family pim tags is null");

        }

    }

    @Nested
    class ProductSkuTest {
        @BeforeEach
        void setup() {

            when(currentPage.getProperties().get(CommonConstants.PAGE_TYPE).toString()).thenReturn(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE);
            
            when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
            when(slingHttpServletRequest.getRequestPathInfo().getSelectors()).thenReturn(skuSelector);
            when(endecaRequestService.getEndecaRequestBean(currentPage, skuSelector, StringUtils.EMPTY)).thenReturn(endecaServiceRequestBean);
            when(endecaService.getSKUDetails(endecaServiceRequestBean)).thenReturn(skuDetailsResponseBean);
            when(skuDetailsResponseBean.getSkuResponse()).thenReturn(skuResponseBean);
            when(skuDetailsResponseBean.getSkuResponse().getSkuDetails()).thenReturn(skuDetailsBean);
            when(productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage)).thenReturn(productFamilyPIMDetails);

            salesforceKnowledgeSearchModel.init();

        }

        @Test
        @DisplayName("getKnowledgeSearchUrl should return salesforce search url with sku's product family as the encoded search term")
        void testGetKnowledgeSearchUrlWhenProductFamilyNonNull() {
            when(salesforceKnowledgeSearchService.getSalesforceSiteUrl()).thenReturn(salesforceSiteUrl);
            when(skuDetailsBean.getFamilyName()).thenReturn(productFamilyName);
            when(salesforceKnowledgeSearchService.getSalesforceSearchPath()).thenReturn(salesforceSearchPath);

            Assertions.assertEquals(salesforceKnowledgeSearchModel.getKnowledgeSearchUrl(), expectedUrlForSearchWithSearchTerm, "should be salesforce search url with search term");

        }

        @Test
        @DisplayName("getKnowledgeSearchUrl should return salesforce site home url when product family not found")
        void testGetKnowledgeSearchUrlWhenProductFamilyIsNull() {
            when(salesforceKnowledgeSearchService.getSalesforceSiteUrl()).thenReturn(salesforceSiteUrl);
            when(skuDetailsBean.getFamilyName()).thenReturn(null);

            Assertions.assertEquals(salesforceKnowledgeSearchModel.getKnowledgeSearchUrl(), expectedSalesforceSiteUrl, "should be salesforce site home url");

        }


        @Test
        @DisplayName("isLocalEnabled should be enabled when 1 or more target tags match any of the cq:tags in product sku pim tags")
        void testIsLocalEnabledWhenTargetTagMatchesProductSkuPimTag() {


            List<String> matchPimTags = new ArrayList<>(Arrays.asList(nonMatchingSourceTagsForPim));
            matchPimTags.add("/content/cq:tags/eaton/integrations/nanorep/bussmann-nanorep");

            List<String> configTargetTags = new ArrayList<>(Arrays.asList(targetTags));

            when(salesforceKnowledgeSearchService.getTargetTags()).thenReturn(configTargetTags);
            when(productFamilyPIMDetails.getPimTags()).thenReturn(matchPimTags);

            Assertions.assertTrue(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be enabled when a target tag matches a tag from product sku pim tags");

        }

        @Test
        @DisplayName("isLocalEnabled should be disabled when no target tags match any of the cq:tags in product sku tags")
        void testIsLocalEnabledWhenTargetTagDoesNotMatchProductSkuPimTag() {

            List<String> configTargetTags = new ArrayList<>(Arrays.asList(targetTags));
            List<String> nonMatchingPimTags = new ArrayList<>(Arrays.asList(nonMatchingSourceTagsForPim));

            when(salesforceKnowledgeSearchService.getTargetTags()).thenReturn(configTargetTags);
            when(productFamilyPIMDetails.getPimTags()).thenReturn(nonMatchingPimTags);

            Assertions.assertFalse(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be disabled when a target tag does not match a tag from product sku pim tags");

        }

        @Test
        @DisplayName("isLocalEnabled should be disabled when 1 or more target tags partially match any of the cq:tags in product sku tags")
        void testIsLocalEnabledWhenTargetTagPartiallyMatchesProductSkuPimTag() {


            List<String> partialMatchPimTags = new ArrayList<>(Arrays.asList(nonMatchingSourceTagsForPim));
            partialMatchPimTags.add("/content/cq:tags/eaton/integrations");

            List<String> configTargetTags = new ArrayList<>(Arrays.asList(targetTags));

            when(salesforceKnowledgeSearchService.getTargetTags()).thenReturn(configTargetTags);
            when(productFamilyPIMDetails.getPimTags()).thenReturn(partialMatchPimTags);

            Assertions.assertFalse(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be disabled when a target tag partially matches a tag from product sku pim tags");

        }

        @Test
        @DisplayName("isLocalEnabled should be disabled when product sku pim tags not found")
        void testIsLocalEnabledWhenProductSkuPimTagsIsNull() {

            when(productFamilyPIMDetails.getPimTags()).thenReturn(null);
            Assertions.assertFalse(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be false when product sku pim tags is null");


        }

    }

    @Nested
    class OtherPageTest {
        

        @BeforeEach
        void setup() {

            when(currentPage.getProperties().get(CommonConstants.PAGE_TYPE).toString()).thenReturn(CommonConstants.PAGE_TYPE_GENERIC_PAGE);
            salesforceKnowledgeSearchModel.init();

        }

        @Test
        @DisplayName("getKnowledgeSearchUrl should return salesforce site url without search term")
        void testGetKnowledgeSearchUrl() {

            when(salesforceKnowledgeSearchService.getSalesforceSiteUrl()).thenReturn(salesforceSiteUrl);

            Assertions.assertEquals(salesforceKnowledgeSearchModel.getKnowledgeSearchUrl(), expectedSalesforceSiteUrl, "should be salesforce search url without search term");

        }

        @Test
        @DisplayName("isLocalEnabled should be enabled when 1 or more target tags match any of the cq:tags in page properties")
        void testIsLocalEnabledWhenTargetTagMatchesPagePropertiesTag() {


            List<String> pagePropertiesTags = new ArrayList<>(Arrays.asList(nonMatchingSourceTagsForPageProperties));
            pagePropertiesTags.add("eaton:integrations/nanorep/bussmann-nanorep");
            String[] matchPagePropertiesTags = pagePropertiesTags.toArray(new String[0]);

            List<String> configTargetTags = new ArrayList<>(Arrays.asList(targetTags));

            when(salesforceKnowledgeSearchService.getTargetTags()).thenReturn(configTargetTags);
            when(currentPage.getProperties().get(CommonConstants.CQ_TAGS, String[].class)).thenReturn(matchPagePropertiesTags);

            Assertions.assertTrue(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be enabled when a target tag matches a tag from page properties for other pages");

        }

        @Test
        @DisplayName("isLocalEnabled should be disabled when no target tags match any of the cq:tags in page properties")
        void testIsLocalEnabledWhenTargetTagDoesNotMatchPagePropertiesTag() {

            List<String> configTargetTags = new ArrayList<>(Arrays.asList(targetTags));

            when(salesforceKnowledgeSearchService.getTargetTags()).thenReturn(configTargetTags);
            when(currentPage.getProperties().get(CommonConstants.CQ_TAGS, String[].class)).thenReturn(nonMatchingSourceTagsForPageProperties);

            Assertions.assertFalse(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be disabled when a target tag does not match a tag from page properties for other pages");

        }

        @Test
        @DisplayName("isLocalEnabled should be disabled when 1 or more target tags partially match any of the cq:tags in page properties")
        void testIsLocalEnabledWhenTargetTagPartiallyMatchesPagePropertiesTag() {


            List<String> pagePropertiesTags = new ArrayList<>(Arrays.asList(nonMatchingSourceTagsForPageProperties));
            pagePropertiesTags.add("eaton:integrations");
            String[] partialMatchPagePropertiesTags = pagePropertiesTags.toArray(new String[0]);

            List<String> configTargetTags = new ArrayList<>(Arrays.asList(targetTags));

            when(salesforceKnowledgeSearchService.getTargetTags()).thenReturn(configTargetTags);
            when(currentPage.getProperties().get(CommonConstants.CQ_TAGS, String[].class)).thenReturn(partialMatchPagePropertiesTags);

            Assertions.assertFalse(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be disabled when a target tag partially matches a tag from page properties for other pages");

        }

        @Test
        @DisplayName("isLocalEnabled should be disabled when page properties tags not found")
        void testIsLocalEnabledWhenPagePropertiesTagsIsNull() {

            when(currentPage.getProperties().get(CommonConstants.CQ_TAGS, String[].class)).thenReturn(null);
            Assertions.assertFalse(salesforceKnowledgeSearchModel.isLocalEnabled(), "should be false when page properties tags is null");

        }

    }
    

}
