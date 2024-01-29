package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.HowToBuyBean;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.eaton.platform.core.util.ResourceBundleFixtures.resourceBundle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductFamilyCardModelTest {


    public static final String POWER_UPS = "backup power (ups)";
    public static final String POWER_UPS_PATH = "/content/eaton/us/en-us/products/backup-power-ups-surge-it-power-distribution/backup-power-ups";
    public static final String POWER_UPS_PATH_WITH_HTML = "/content/eaton/us/en-us/products/backup-power-ups-surge-it-power-distribution/backup-power-ups.html";
    public static final String HOW_TO_BUY_PATH = "/content/eaton/us/en-us/products/backup-power-ups-surge-it-power-distribution/backup-power-ups/how-to-buy";
    private String defaultHowtoBuyCTALink = StringUtils.EMPTY;
    @InjectMocks
    ProductFamilyCardModel productFamilyCardModel = new ProductFamilyCardModel();
    @Mock
    Page currentPage;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    ProductFamilyDetails productFamilyDetails;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    Resource resource;
    @Mock
    Page page;

    @Test
    @DisplayName("GetEyebrowLink should return null when the ProductFamilyDetails is null")
    void testGetEyebrowLinkWhenFamilyBeanNull() {
        Assertions.assertNull(new ProductFamilyCardModel().getEyebrowLink(), "Should be null");
    }
    @Test
    @DisplayName("GetEyebrowTitle should return null when the ProductFamilyDetails is null")
    void testGetEyebrowTitleWhenFamilyBeanNull() {
        Assertions.assertNull(new ProductFamilyCardModel().getEyebrowTitle(), "Should be null");
    }
    @Test
    @DisplayName("GetHowToBuyOptions should return empty list when the ProductFamilyDetails is null")
    void testGetHowToBuyOptionsWhenFamilyBeanNull() {
        Assertions.assertEquals(new ArrayList<>(), new ProductFamilyCardModel().getHowToBuyOptions(), "Should be and empty list");
    }

    @Test
    @DisplayName("GetHowToBuyOptions should return defaultHowtoBuyCTA when Additional CTA is none")
    void testGetHowToBuyOptionsWhenFamilyBeanNotNullAndNoAdditionalCTA() {
        List<HowToBuyBean> howToBuyList = new ArrayList<>();
        howToBuyList.add(new HowToBuyBean());
        when(productFamilyDetails.getHowToBuyList()).thenReturn(howToBuyList);
        Assertions.assertEquals(defaultHowtoBuyCTALink, productFamilyCardModel.getHowToBuyOptions().get(0).getLink(), "Should be defaultHowtoBuyCTALink");
    }

    @Test
    @DisplayName("GetHowToBuyOptions should return empty list when HowToBuyList is empty")
    void testGetHowToBuyOptionsWhenFamilyBeanNotNullAndNotEmptyHowToBuyList() {
        List<HowToBuyBean> howToBuyList = new ArrayList<>();
        when(productFamilyDetails.getHowToBuyList()).thenReturn(howToBuyList);
        Assertions.assertEquals(new ArrayList<>(), productFamilyCardModel.getHowToBuyOptions(), "Should be an empty list");
    }

    @Test
    @DisplayName("GetHowToBuyOptions should return empty list when HowToBuyList is null")
    void testGetHowToBuyOptionsWhenFamilyBeanNotNullAndNullHowToBuyList() {
        when(productFamilyDetails.getHowToBuyList()).thenReturn(null);
        Assertions.assertEquals(new ArrayList<>(), productFamilyCardModel.getHowToBuyOptions(), "Should be an empty list");
    }

    @Test
    @DisplayName("GetEyebrowLink should return null when the ProductFamilyDetails is null")
    void testGetHowToBuyLinkWhenFamilyBeanNull() {
        Assertions.assertEquals(defaultHowtoBuyCTALink, new ProductFamilyCardModel().getHowToBuyLink(), "Should be default");
    }
    @Test
    @DisplayName("GetEyebrowLink should return the primary subCategory with html if not yet from the ProductFamilyDetails when not null")
    void testGetEyebrowLinkWithouthHtmlWhenFamilyBeanNotNull() {
        when(productFamilyDetails.getPrimarySubCategory()).thenReturn(POWER_UPS_PATH);
        Assertions.assertEquals(POWER_UPS_PATH_WITH_HTML, productFamilyCardModel.getEyebrowLink(), "Should be path to backup power (ups)");
    }
    @Test
    @DisplayName("GetEyebrowLink should return the primary subCategory with html if already from the ProductFamilyDetails when not null")
    void testGetEyebrowLinkWithHtmlWhenFamilyBeanNotNull() {
        when(productFamilyDetails.getPrimarySubCategory()).thenReturn(POWER_UPS_PATH_WITH_HTML);
        Assertions.assertEquals(POWER_UPS_PATH_WITH_HTML, productFamilyCardModel.getEyebrowLink(), "Should be path to backup power (ups)");
    }
    @Test
    @DisplayName("GetEyebrowTitle: return null if subcategory page is not existent")
    void testGetEyebrowTitleWhenFamilyBeanNotNullCase1() {
        when(productFamilyDetails.getPrimarySubCategory()).thenReturn(POWER_UPS_PATH);
        when(resourceResolver.getResource(POWER_UPS_PATH)).thenReturn(new NonExistingResource(resourceResolver, POWER_UPS_PATH));
        Assertions.assertNull(productFamilyCardModel.getEyebrowTitle(), "Should be null");
    }

    @Test
    @DisplayName("GetEyebrowTitle: return null if subcategory page is not existent")
    void testGetEyebrowTitleWhenFamilyBeanNotNullCase2() {
        when(productFamilyDetails.getPrimarySubCategory()).thenReturn(POWER_UPS_PATH);
        when(resourceResolver.getResource(POWER_UPS_PATH)).thenReturn(null);
        Assertions.assertNull(productFamilyCardModel.getEyebrowTitle(), "Should be null");
    }
    @Test
    @DisplayName("GetEyebrowTitle: return navigation title of subcategory page if present, if already initialized, does not fetch resource again.")
    void testGetEyebrowTitleWhenFamilyBeanNotNullCase3() {
        when(productFamilyDetails.getPrimarySubCategory()).thenReturn(POWER_UPS_PATH);
        when(resourceResolver.getResource(POWER_UPS_PATH)).thenReturn(resource);
        when(resource.adaptTo(Page.class)).thenReturn(page);
        when(page.getNavigationTitle()).thenReturn(POWER_UPS);
        Assertions.assertEquals(POWER_UPS, productFamilyCardModel.getEyebrowTitle(), "Should be backup power (ups)");
        Mockito.reset(productFamilyDetails, resourceResolver, resource, page);
        Assertions.assertEquals(POWER_UPS, productFamilyCardModel.getEyebrowTitle(), "Should be backup power (ups)");
        verifyNoMoreInteractions(productFamilyDetails, resourceResolver, resource, page);
    }
    @Test
    @DisplayName("GetEyebrowTitle: if navigation title from subcategory page not present return page title, if already initialized, does not fetch resource again.")
    void testGetEyebrowTitleWhenFamilyBeanNotNullCase4() {
        when(productFamilyDetails.getPrimarySubCategory()).thenReturn(POWER_UPS_PATH);
        when(resourceResolver.getResource(POWER_UPS_PATH)).thenReturn(resource);
        when(resource.adaptTo(Page.class)).thenReturn(page);
        when(page.getNavigationTitle()).thenReturn(null);
        when(page.getPageTitle()).thenReturn(POWER_UPS);
        Assertions.assertEquals(POWER_UPS, productFamilyCardModel.getEyebrowTitle(), "Should be backup power (ups)");
        Mockito.reset(productFamilyDetails, resourceResolver, resource, page);
        Assertions.assertEquals(POWER_UPS, productFamilyCardModel.getEyebrowTitle(), "Should be backup power (ups)");
        verifyNoMoreInteractions(productFamilyDetails, resourceResolver, resource, page);
    }
    @Test
    @DisplayName("GetEyebrowTitle: if page title from subcategory page not present return title, if already initialized, does not fetch resource again.")
    void testGetEyebrowTitleWhenFamilyBeanNotNullCase5() {
        when(productFamilyDetails.getPrimarySubCategory()).thenReturn(POWER_UPS_PATH);
        when(resourceResolver.getResource(POWER_UPS_PATH)).thenReturn(resource);
        when(resource.adaptTo(Page.class)).thenReturn(page);
        when(page.getNavigationTitle()).thenReturn(null);
        when(page.getPageTitle()).thenReturn(null);
        when(page.getTitle()).thenReturn(POWER_UPS);
        Assertions.assertEquals(POWER_UPS, productFamilyCardModel.getEyebrowTitle(), "Should be backup power (ups)");
        Mockito.reset(productFamilyDetails, resourceResolver, resource, page);
        Assertions.assertEquals(POWER_UPS, productFamilyCardModel.getEyebrowTitle(), "Should be backup power (ups)");
        verifyNoMoreInteractions(productFamilyDetails, resourceResolver, resource, page);
    }
    @Test
    @DisplayName("GetHowToBuyOptions should return the list from ProductFamilyDetails when not null")
    void testGetHowToBuyOptionsWhenFamilyBeanNotNull() {
        List<HowToBuyBean> howToBuyBeans = Arrays.asList(new HowToBuyBean(), new HowToBuyBean());
        when(productFamilyDetails.getHowToBuyList()).thenReturn(howToBuyBeans);
        Assertions.assertEquals(howToBuyBeans, productFamilyCardModel.getHowToBuyOptions(), "Should be and the list from details");
    }
    @Test
    @DisplayName("GetHowToBuyLink should return link from the ProductFamilyDetailsBean when not null")
    void testGetHowToBuyLinkWhenFamilyBeanNotNull() {
        when(productFamilyDetails.getHowToBuyLink()).thenReturn(HOW_TO_BUY_PATH);
        Assertions.assertEquals(HOW_TO_BUY_PATH, productFamilyCardModel.getHowToBuyLink(), "Should be the link from details");
    }
    @Test
    @DisplayName("GetHowToBuyLink should return the default link when the howToBuyLink from the ProductFamilyDetailsBean is null")
    void testGetHowToBuyLinkWhenFamilyBeanNotNullButHowToBuyLinkNull() {
        when(productFamilyDetails.getHowToBuyLink()).thenReturn(null);
        Assertions.assertEquals(defaultHowtoBuyCTALink, productFamilyCardModel.getHowToBuyLink(), "Should be the default");
    }

    @Test
    @DisplayName("GetHowToBuyLabel should return the value from i18n when not already initialized")
    void testGetHowToBuyLabelLazyLoad() {
        Locale enUs = new Locale("en", "us");
        when(currentPage.getLanguage(true)).thenReturn(enUs);
        when(slingHttpServletRequest.getResourceBundle(enUs)).thenReturn(resourceBundle());
        assertEquals("How to buy", productFamilyCardModel.getHowToBuyLabel()
                , "How to buy should be returned");
        Mockito.reset(currentPage, slingHttpServletRequest);
        // next call will fail if the lazy load is not working
        assertEquals("How to buy", productFamilyCardModel.getHowToBuyLabel()
                , "How to buy should be returned");
        verifyNoMoreInteractions(currentPage, slingHttpServletRequest);

    }
}