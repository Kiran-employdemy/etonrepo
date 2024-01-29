package com.eaton.platform.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junitx.util.PrivateAccessor;

@ExtendWith(value = { MockitoExtension.class })
public class SiteConfigModelTest {
    @InjectMocks
    SiteConfigModel siteConfigModel;

    String globalAttributeGroupName = "globalAttributeGroupName";
    String title = "title";
    String[] resourceGroup;
    String disableDatasheet = "false";
    String overridePDHData = "overridePDHData";
    String enableProductsTab = "enableProductsTab";
    String enableNewsTab = "enableNewsTab";
    String enableResourcesTab = "enableResourcesTab";
    String enableServicesTab = "enableServicesTab";
    String unitedStatesDateFormat = "false";
    int countCorouselItem = 12;
    int facetValueCount;
    int pageSize;
    int noOfDays;
    int expandedFacetCount;
    int facetCount;
    int countUpSell;
    String defaultSortOrder;
    Resource globalAttributeList;
    Resource suppCountryList;
    Resource documentGroup;
    String skuFallBackImage;
    String defaultLinkCTA;
    String defaultLinkHTB;
    String pfOverviewTitle;
    String pfOverviewDesc;
    String pfModelsTitle;
    String pfResourcesTitle;
    String skuOverviewTitle;
    String skuOverviewDesc;
    String skuSpecificationsTitle;
    String skuResourcesTitle;
    String skuPageURL;
    String cartUrl;
    String searchResultsURL;
    String crossRefSearchResultsURL;
    Resource siteSearchFacetGroup;
    String dirType;
    String contenthubDefaultIcon = "";
    String faviconIcon = "/apps/eaton/settings/wcm/designs/favicon.ico";
    String  bulkDownloadPackageSize = "1048576056467899";
    int bulkDownloadCacheDuration = 120;
    String companyName = "Eaton";
    String seoScriptPath = "";
    String[] siteVerificationCodeList;
    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void testTitle() throws NoSuchFieldException {
        PrivateAccessor.setField(siteConfigModel, "title", title);
        assertEquals(siteConfigModel.getTitle(), title);
    }

    @Test
     void testGlobalAttributeGroupName() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "globalAttributeGroupName", globalAttributeGroupName);
        assertEquals(siteConfigModel.getGlobalAttributeGroupName(),globalAttributeGroupName);
    }

    @Test
    void testResourceGroup() throws NoSuchFieldException {
        PrivateAccessor.setField(siteConfigModel, "resourceGroup", resourceGroup);
        assertEquals(siteConfigModel.getResourceGroup(),resourceGroup);
    }

    @Test
    void testDisableDatasheet() throws NoSuchFieldException {
        PrivateAccessor.setField(siteConfigModel, "disableDatasheet", disableDatasheet);
        assertEquals(siteConfigModel.getDisableDatasheet(),disableDatasheet);
    }

    @Test
    void testOverridePDHData() throws NoSuchFieldException {
        PrivateAccessor.setField(siteConfigModel, "overridePDHData", overridePDHData);
        assertEquals(siteConfigModel.getOverridePDHData(),overridePDHData);
    }

    @Test
    void testEnableProductsTab() throws NoSuchFieldException {
        PrivateAccessor.setField(siteConfigModel, "enableProductsTab", enableProductsTab);
        assertEquals(siteConfigModel.getEnableProductsTab(),enableProductsTab);
    }
    @Test
    void testEnableNewsTab() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "enableNewsTab", enableNewsTab);
        assertEquals(siteConfigModel.getEnableNewsTab(),enableNewsTab);
    }

    @Test
    void testEnableResourcesTab() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "enableResourcesTab", enableResourcesTab);
        assertEquals(siteConfigModel.getEnableResourcesTab(),enableResourcesTab);
    }

    @Test
    void testEnableServicesTab() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "enableServicesTab", enableServicesTab);
        assertEquals(siteConfigModel.getEnableServicesTab(),enableServicesTab);
    }

    @Test
    void testUnitedStatesDateFormat() throws NoSuchFieldException {
        PrivateAccessor.setField(siteConfigModel, "unitedStatesDateFormat", unitedStatesDateFormat);
        assertEquals(siteConfigModel.getUnitedStatesDateFormat(),unitedStatesDateFormat);
    }

    @Test
    void testCountCorouselItem() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "countCorouselItem", countCorouselItem);
        assertEquals(siteConfigModel.getCountCorouselItem(),countCorouselItem);
    }

    @Test
    void testFacetValueCount() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "facetValueCount", facetValueCount);
        assertEquals(siteConfigModel.getFacetValueCount(),facetValueCount);
    }

    @Test
    void testPageSize() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "pageSize", pageSize);
        assertEquals(siteConfigModel.getPageSize(),pageSize);
    }

    @Test
    void testNoOfDays() throws NoSuchFieldException {
        PrivateAccessor.setField(siteConfigModel, "noOfDays", noOfDays);
        assertEquals(siteConfigModel.getNoOfDays(),noOfDays);
    }

    @Test
    void testExpandedFacetCount() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "expandedFacetCount", expandedFacetCount);
        assertEquals(siteConfigModel.getExpandedFacetCount(),expandedFacetCount);
    }

    @Test 
    void testFacetCount() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "facetCount", facetCount);
        assertEquals(siteConfigModel.getFacetCount(),facetCount);
    }

    @Test 
    void testCountUpSell() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "countUpSell", countUpSell);
        assertEquals(siteConfigModel.getCountUpSell(),countUpSell);
    }

    @Test 
    void testDefaultSortOrder() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "defaultSortOrder", defaultSortOrder);
        assertEquals(siteConfigModel.getDefaultSortOrder(),defaultSortOrder);
    }

    @Test 
    void testGlobalAttributeList() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "globalAttributeList", globalAttributeList);
        assertEquals(siteConfigModel.getGlobalAttributeList(),globalAttributeList);
    }

    @Test 
    void testSuppCountryList() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "suppCountryList", suppCountryList);
        assertEquals(siteConfigModel.getSuppCountryList(),suppCountryList);
    }

    @Test 
    void testDocumentGroup() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "documentGroup", documentGroup);
        assertEquals(siteConfigModel.getDocumentGroup(),documentGroup);
    }

    @Test 
    void testSkuFallBackImage() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "skuFallBackImage", skuFallBackImage);
        assertEquals(siteConfigModel.getSkuFallBackImage(),skuFallBackImage);
    }

    @Test 
    void testDefaultLinkCTA() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "defaultLinkCTA", defaultLinkCTA);
        assertEquals(siteConfigModel.getDefaultLinkCTA(),defaultLinkCTA);
    }

    @Test 
    void testDefaultLinkHTB() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "defaultLinkHTB", defaultLinkHTB);
        assertEquals(siteConfigModel.getDefaultLinkHTB(),defaultLinkHTB);
    }

    @Test 
    void testPfOverviewTitle() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "pfOverviewTitle", pfOverviewTitle);
        assertEquals(siteConfigModel.getPfOverviewTitle(),pfOverviewTitle);
    }
    @Test 
    void testPfOverviewDesc() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "pfOverviewDesc", pfOverviewDesc);
        assertEquals(siteConfigModel.getPfOverviewDesc(),pfOverviewDesc);
    }

    @Test 
    void testPfModelsTitle() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "pfModelsTitle", pfModelsTitle);
        assertEquals(siteConfigModel.getPfModelsTitle(),pfModelsTitle);
    }

    @Test 
    void testPfResourcesTitle() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "pfResourcesTitle", pfResourcesTitle);
        assertEquals(siteConfigModel.getPfResourcesTitle(),pfResourcesTitle);
    }

    @Test 
    void testSkuOverviewTitle() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "skuOverviewTitle", skuOverviewTitle);
        assertEquals(siteConfigModel.getSkuOverviewTitle(),skuOverviewTitle);
    }

    @Test 
    void testSkuOverviewDesc() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "skuOverviewDesc", skuOverviewDesc);
        assertEquals(siteConfigModel.getSkuOverviewDesc(),skuOverviewDesc);
    }

    @Test 
    void testSkuSpecificationsTitle() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "skuSpecificationsTitle", skuSpecificationsTitle);
        assertEquals(siteConfigModel.getSkuSpecificationsTitle(),skuSpecificationsTitle);
    }

    @Test 
    void testSkuResourcesTitle() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "skuResourcesTitle", skuResourcesTitle);
        assertEquals(siteConfigModel.getSkuResourcesTitle(),skuResourcesTitle);
    }

    @Test 
    void testSkuPageURL() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "skuPageURL", skuPageURL);
        assertEquals(siteConfigModel.getSkuPageURL(),skuPageURL);
    }

    @Test 
    void testCartUrl() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "cartUrl", cartUrl);
        assertEquals(siteConfigModel.getCartUrl(),cartUrl);
    }

    @Test 
    void testSearchResultsURL() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "searchResultsURL", searchResultsURL);
        assertEquals(siteConfigModel.getSearchResultsURL(),searchResultsURL);
    }

    @Test 
    void testCrossRefSearchResultsURL() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "crossRefSearchResultsURL", crossRefSearchResultsURL);
        assertEquals(siteConfigModel.getCrossRefSearchResultsURL(),crossRefSearchResultsURL);
    }

    @Test 
    void testSiteSearchFacetGroup() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "siteSearchFacetGroup", siteSearchFacetGroup);
        assertEquals(siteConfigModel.getSiteSearchFacetGroup(),siteSearchFacetGroup);
    }

    @Test 
    void testDirType() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "dirType", dirType);
        assertEquals(siteConfigModel.getDirType(),dirType);
    }

    @Test 
    void testContenthubDefaultIcon() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "contenthubDefaultIcon", contenthubDefaultIcon);
        assertEquals(siteConfigModel.getContenthubDefaultIcon(),contenthubDefaultIcon);
    }

    @Test 
    void testFaviconIcon() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "faviconIcon", faviconIcon);
        assertEquals(siteConfigModel.getFavionIcon(),faviconIcon);
    }

    @Test 
    void testBulkDownloadPackageSizeIfNotNull() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "bulkDownloadPackageSize", bulkDownloadPackageSize);
        assertEquals(siteConfigModel.getBulkDownloadPackageSize(),Long.parseLong(bulkDownloadPackageSize));
    }

    @Test 
    void testBulkDownloadCacheDuration() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "bulkDownloadCacheDuration", bulkDownloadCacheDuration);
        assertEquals(siteConfigModel.getBulkDownloadCacheDuration(),bulkDownloadCacheDuration);
    }

    @Test 
    void testCompanyName() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "companyName", companyName);
        assertEquals(siteConfigModel.getCompanyName(),companyName);
    }

    @Test 
    void testSeoScriptPath() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "seoScriptPath", seoScriptPath);
        assertEquals(siteConfigModel.getSeoScriptPath(),seoScriptPath);
    }

    @Test 
    void testSiteVerificationCodeList() throws NoSuchFieldException{
        PrivateAccessor.setField(siteConfigModel, "siteVerificationCodeList", siteVerificationCodeList);
        assertEquals(siteConfigModel.getSiteVerificationCodeList(),siteVerificationCodeList);
    }
    
}
