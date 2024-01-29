package com.eaton.platform.core.search.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.integration.endeca.pojo.sku.EndecaSkuDocument;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkuSearchRecordMappingContextImplTest {

    @Mock
    AdminService adminService;
    @Mock
    SiteResourceSlingModel siteResourceSlingModel;
    @Mock
    ProductFamilyDetailService productFamilyDetailService;
    @Mock
    Page currentPage;
    @Mock
    SlingHttpServletRequest request;
    SkuSearchRecordMappingContextImpl<EndecaSkuDocument> skuSearchRecordMappingContext;
    SkuSearchRecordMappingContextImpl<EndecaSkuDocument> skuSearchRecordMappingContextWithoutCurrentPage;

    @BeforeEach
    void setUp() {
        skuSearchRecordMappingContext = new SkuSearchRecordMappingContextImpl<>(adminService, siteResourceSlingModel, productFamilyDetailService, currentPage, request);
        skuSearchRecordMappingContextWithoutCurrentPage= new SkuSearchRecordMappingContextImpl<>(adminService, siteResourceSlingModel, productFamilyDetailService, null, request);
    }

    @Test
    void testGetCountryWithoutCurrentPage() {
        assertEquals(Locale.getDefault().getCountry(), skuSearchRecordMappingContextWithoutCurrentPage.getCountry(), "Should be default one");
    }

    @Test
    void testGetCountryLocaleCountryInPath() {
        when(currentPage.getLanguage()).thenReturn(Locale.US);
        when(currentPage.getPath()).thenReturn("/content/eaton/us/en-us/skuPage");
        assertEquals("US", skuSearchRecordMappingContext.getCountry(), "should be US");
    }
    @Test
    void testGetCountryLocaleCountryNotInPath() {
        when(currentPage.getLanguage()).thenReturn(Locale.US);
        when(currentPage.getPath()).thenReturn("/content/eaton/au/en-us/skuPage");
        assertEquals("AU", skuSearchRecordMappingContext.getCountry(), "should be AU");
    }
    @Test
    void testGetCountryLocaleCountryNotInPathNoContentEatonRoot() {
        when(currentPage.getLanguage()).thenReturn(Locale.US);
        when(currentPage.getPath()).thenReturn("/au/en-us/skuPage");
        assertEquals("AU", skuSearchRecordMappingContext.getCountry(), "should be AU");
    }
}