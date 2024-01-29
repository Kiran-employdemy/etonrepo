package com.eaton.platform.core.models.downloadable;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static com.eaton.platform.core.models.downloadable.BulkDownloadableFixture.FILE_NAME_PREFIX;
import static com.eaton.platform.core.util.ResourceBundleFixtures.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractBulkDownloadableImplTest {
    @InjectMocks
    BulkDownloadableFixture abstractBulkDownloadableWithConfig = new BulkDownloadableFixture();
    @Mock
    SiteConfigModel siteConfigModel;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    Page currentPage;
    @Mock
    private EatonSiteConfigModel eatonSiteConfigModel;

    @Test
    @DisplayName("When calling setSiteConfigModel with null slingRequest, an IllegalStateException is thrown, null not allowed.")
    void testGetSiteConfigModelSlingRequestNull() {
        BulkDownloadableFixture bulkDownloadableFixture = new BulkDownloadableFixture(null, null);
        assertThrows(IllegalStateException.class, bulkDownloadableFixture::getSiteConfigModel
                , "should throw IllegalStateException");
    }

    @Test
    @DisplayName("When slingRequest can't be adapted to EatonSiteConfigModel, an IllegalStateException, should not happen.")
    void testGetSiteConfigModelSlingRequestNotAdaptableToEatonSiteConfigModel() {
        BulkDownloadableFixture bulkDownloadableFixture = new BulkDownloadableFixture(slingHttpServletRequest, null);
        when(slingHttpServletRequest.adaptTo(EatonSiteConfigModel.class)).thenReturn(null);
        assertThrows(IllegalStateException.class, bulkDownloadableFixture::getSiteConfigModel
                , "should throw IllegalStateException");
    }

    @Test
    @DisplayName("When calling getSiteConfigModel with correct slingRequest, the siteConfigModel is set to value extracted from request and returned")
    void testGetSiteConfigModelWithSlingRequest() {
        BulkDownloadableFixture bulkDownloadableFixture = new BulkDownloadableFixture(slingHttpServletRequest, null);
        when(slingHttpServletRequest.adaptTo(EatonSiteConfigModel.class)).thenReturn(eatonSiteConfigModel);
        when(eatonSiteConfigModel.getSiteConfig()).thenReturn(siteConfigModel);
        assertEquals(siteConfigModel, bulkDownloadableFixture.getSiteConfigModel()
                , "Extracted siteConfigModel should be returned");
    }

    @Test
    @DisplayName("When calling getMaxAllowedDownloadSize and config set, the BulkDownloadPackageSize on config is returned")
    void testGetMaxAllowedDownloadSizeWithConfigSet() {
        when(siteConfigModel.getBulkDownloadPackageSize()).thenReturn((long) 300);
        assertEquals(300, abstractBulkDownloadableWithConfig.getMaxAllowedDownloadSize()
                , "300 should be returned");
    }

    @Test
    @DisplayName("When calling getDownloadCacheDuration and config set, the BulkDownloadCacheDuration on config is returned")
    void testGetDownloadCacheDurationWithConfigSet() {
        when(siteConfigModel.getBulkDownloadCacheDuration()).thenReturn(20);
        assertEquals(20, abstractBulkDownloadableWithConfig.getDownloadCacheDuration()
                , "20 should be returned");
    }

    @Test
    @DisplayName("When calling getFileNamePrefix and prefix already set, the whole logic to fetch the i18n value is not called")
    void testGetFileNamePrefixPrefixNotSetYet() {
        Locale enUs = new Locale("en", "us");
        assertEquals(FILE_NAME_PREFIX, new BulkDownloadableFixture(slingHttpServletRequest, currentPage).getFileNamePrefix()
                , "prefix should be returned");
        verify(slingHttpServletRequest, never()).getResourceBundle(any(Locale.class));
        verify(currentPage, never()).getLanguage(true);
    }

    @Test
    @DisplayName("When calling getFileNamePrefix and config set, the whole logic to fetch the i18n value is called")
    void testGetFileNamePrefixWithConfigSet() {
        Locale enUs = new Locale("en", "us");
        when(currentPage.getLanguage(true)).thenReturn(enUs);
        when(slingHttpServletRequest.getResourceBundle(enUs)).thenReturn(resourceBundle());
        when(siteConfigModel.getCompanyName()).thenReturn("eaton");
        assertEquals("eaton_Download_", abstractBulkDownloadableWithConfig.getFileNamePrefix()
                , "eaton_Download_ should be returned");
    }

    @Test
    @DisplayName("When there is already a value set for bulkDownloadToolTip, the whole logic to fetch the i18n value is not called")
    void testGetBulkDownloadToolTipAlreadyPresent() {
        String bulkDownloadToolTip = new BulkDownloadableFixture(slingHttpServletRequest, currentPage).getBulkDownloadToolTipText();
        Assertions.assertEquals(BULK_DOWNLOAD_TOOL_TIP, bulkDownloadToolTip, "must be the one we set in the setup");
        verify(slingHttpServletRequest, never()).getResourceBundle(any(Locale.class));
        verify(currentPage, never()).getLanguage(true);
    }

    @Test
    @DisplayName("When there is no value set for bulkDownloadToolTip, the whole logic to fetch the i18n value is called")
    void testGetBulkDownloadToolTipNotPresent() {
        Locale enUs = new Locale("en", "us");
        when(currentPage.getLanguage(true)).thenReturn(enUs);
        when(slingHttpServletRequest.getResourceBundle(enUs)).thenReturn(resourceBundle());
        String bulkDownloadToolTip = abstractBulkDownloadableWithConfig.getBulkDownloadToolTipText();
        Assertions.assertEquals(BULK_DOWNLOAD_TOOL_TIP, bulkDownloadToolTip, "must be the one we get from i18n");
    }

    @Test
    @DisplayName("When there is already a value set for bulkDownloadText, the whole logic to fetch the i18n value is not called")
    void testGetBulkDownloadTextAlreadyPresent() {
        String bulkDownloadToolText = new BulkDownloadableFixture(slingHttpServletRequest, currentPage).getBulkDownloadText();
        Assertions.assertEquals(BULK_DOWNLOAD_VALUE, bulkDownloadToolText, "must be the one we set in the setup");
        verify(slingHttpServletRequest, never()).getResourceBundle(any(Locale.class));
        verify(currentPage, never()).getLanguage(true);
    }

    @Test
    @DisplayName("When there is no value set for bulkDownloadText, the whole logic to fetch the i18n value is called")
    void testGetBulkDownloadTextNotPresent() {
        Locale enUs = new Locale("en", "us");
        when(currentPage.getLanguage(true)).thenReturn(enUs);
        when(slingHttpServletRequest.getResourceBundle(enUs)).thenReturn(resourceBundle());
        String bulkDownloadToolText = abstractBulkDownloadableWithConfig.getBulkDownloadText();
        Assertions.assertEquals(BULK_DOWNLOAD_VALUE, bulkDownloadToolText, "must be the one we get from i18n");
    }

    @Test
    @DisplayName("When there is already a value set for bulkDownloadText, the whole logic to fetch the i18n value is not called")
    void testGetDownloadLimitExceededTextAlreadyPresent() {
        String downloadLimitExceededText = new BulkDownloadableFixture(slingHttpServletRequest, currentPage).getDownloadLimitExceededText();
        Assertions.assertEquals(DOWNLOAD_EXCEEDED_TEXT, downloadLimitExceededText, "must be the one we set in the setup");
        verify(slingHttpServletRequest, never()).getResourceBundle(any(Locale.class));
        verify(currentPage, never()).getLanguage(true);
    }

    @Test
    @DisplayName("When there is no value set for bulkDownloadToolTip, the whole logic to fetch the i18n value is called")
    void testGetDownloadLimitExceededText() {
        Locale enUs = new Locale("en", "us");
        when(currentPage.getLanguage(true)).thenReturn(enUs);
        when(slingHttpServletRequest.getResourceBundle(enUs)).thenReturn(resourceBundle());
        String downloadLimitExceededText = abstractBulkDownloadableWithConfig.getDownloadLimitExceededText();
        Assertions.assertEquals(DOWNLOAD_EXCEEDED_TEXT, downloadLimitExceededText, "must be the one we get from i18n");
    }

}