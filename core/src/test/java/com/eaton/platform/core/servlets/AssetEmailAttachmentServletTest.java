package com.eaton.platform.core.servlets;

import static com.eaton.platform.core.util.ResourceBundleFixtures.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Locale;
import java.util.Objects;

import com.eaton.platform.core.constants.CommonConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import com.eaton.platform.core.servlets.assets.AssetEmailAttachmentServlet;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class AssetEmailAttachmentServletTest {

    @InjectMocks
    AssetEmailAttachmentServlet assetEmailAttachmentServlet = new AssetEmailAttachmentServlet();

    @Mock
    ResourceResolver mockResourceResolver;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    Resource damAssetWithTitleResource;
    Resource damAssetNoTitle;
    Resource parentPageResourceEnUs;
    Resource parentPageResourceDeDe;
    Resource currentPageResourceEnUs;
    Resource currentPageResourceDeDe;

    @BeforeEach
    public void setUp(AemContext aemContext) throws Exception {
        damAssetWithTitleResource = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("damAssetWithTitle.json")),
                "/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/af-gf-receptacles/obc-afgf-dual-purpose-recep-sell-sheet.pdf");
        damAssetNoTitle = aemContext.load().json(Objects.requireNonNull(getClass().getResourceAsStream("damAssetNoTitle.json"))
                , "/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/home-automation-hub/home-automation-hub-faq.pdf");

        parentPageResourceEnUs = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("parentPageEnUs.json")),
                "/content/eaton/us/en-us");
        parentPageResourceDeDe = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("parentPageDeDe.json")),
                "/content/eaton/de/de-de");

        currentPageResourceEnUs = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("currentPage.json")),
                parentPageResourceEnUs, "/library");
        currentPageResourceDeDe = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("currentPageDeDe.json")),
                parentPageResourceDeDe, "/library");

        when(slingHttpServletRequest.getResourceResolver()).thenReturn(mockResourceResolver);

    }

    @Test
    public void testDoPostWithEmptyPayload() throws Exception {
        Mockito.reset(slingHttpServletRequest);

        // Construct empty JSON payload
        JSONObject json = new JSONObject();
        StringReader stringReader = new StringReader(json.toString());
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(stringReader));

        // Execute doPost()
        assetEmailAttachmentServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        // Verify that response status is 500
        verify(slingHttpServletResponse).setStatus(500);
    }

    @Test
    public void testMissingEmailRecipient() throws Exception {
        String requestData = "{\"dataValue\":[{\"documentLinks\":[\"link1\",\"link2\"]}]}";
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestData)));

        assetEmailAttachmentServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);
        verify(slingHttpServletResponse).setStatus(500);
    }

    @Test
    public void testEmptyDocumentLinks() throws Exception {
        String requestData = "{\"dataValue\":[{\"emailTo\":\"test@example.com\",\"documentLinks\":[],\"domain\":\"example.com\"}]}";
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader(requestData)));

        assetEmailAttachmentServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);
        verify(slingHttpServletResponse).setStatus(500);
    }

    @Test
    public void testBuildEmailBodyHtml() throws JSONException {
        when(slingHttpServletRequest.getHeader(CommonConstants.REFERER_URL)).thenReturn("http://www.example.com/content/eaton/us/en-us/library.html");
        when(mockResourceResolver.resolve("/content/eaton/us/en-us/library")).thenReturn(currentPageResourceEnUs);
        when(mockResourceResolver.getResource("/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/af-gf-receptacles/obc-afgf-dual-purpose-recep-sell-sheet.pdf")).thenReturn(damAssetWithTitleResource);

        prepareI18nLocale("en", "us");

        String requestData = "{\"dataValue\":[{\"emailTo\":\"test@example.com\",\"documentLinks\": [\"/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/af-gf-receptacles/obc-afgf-dual-purpose-recep-sell-sheet.pdf\"],\"domain\":\"example.com\"}]}";

        JSONObject json = new JSONObject(requestData);
        JSONArray dataValueArray = json.getJSONArray("dataValue");

        Assertions.assertEquals(AssetEmailAttachmentServletFixtures.emailBodyExpected, assetEmailAttachmentServlet.buildEmailBodyHtml(slingHttpServletRequest, dataValueArray), "Email body should be constructed as expected with available DAM asset titles and i18n phrases");
        Assertions.assertEquals(AssetEmailAttachmentServletFixtures.subjectExpectedI18nEnUs, AssetEmailAttachmentServlet.getI18nItems(slingHttpServletRequest, "subject"), "Subject should be en-us");
        Assertions.assertEquals(AssetEmailAttachmentServletFixtures.noReplyExpectedI18nEnUs, AssetEmailAttachmentServlet.getI18nItems(slingHttpServletRequest, "noReplyMsg"), "NoReply should be en-us");
    }

    @Test
    public void testBuildEmailBodyHtmlNoTitleInDAM() throws JSONException {
        when(slingHttpServletRequest.getHeader(CommonConstants.REFERER_URL)).thenReturn("http://www.example.com/content/eaton/us/en-us/library.html");
        when(mockResourceResolver.resolve("/content/eaton/us/en-us/library")).thenReturn(currentPageResourceEnUs);
        when(mockResourceResolver.getResource("/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/home-automation-hub/home-automation-hub-faq.pdf")).thenReturn(damAssetNoTitle);

        prepareI18nLocale("en", "us");

        String requestData = "{\"dataValue\":[{\"emailTo\":\"test@example.com\",\"documentLinks\": [\"/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/home-automation-hub/home-automation-hub-faq.pdf\"],\"domain\":\"example.com\"}]}";

        JSONObject json = new JSONObject(requestData);
        JSONArray dataValueArray = json.getJSONArray("dataValue");

        Assertions.assertEquals(AssetEmailAttachmentServletFixtures.emailBodyExpectedNoTitle, assetEmailAttachmentServlet.buildEmailBodyHtml(slingHttpServletRequest, dataValueArray), "should fall back to domain/assetpath to label the link, if no title is available in the DAM");
    }

    @Test
    public void testBuildEmailBodyHtmlWithI18n() throws JSONException {
        when(slingHttpServletRequest.getHeader(CommonConstants.REFERER_URL)).thenReturn("http://www.example.com/content/eaton/de/de-de/library.html");
        when(mockResourceResolver.resolve("/content/eaton/de/de-de/library")).thenReturn(currentPageResourceDeDe);
        when(mockResourceResolver.getResource("/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/af-gf-receptacles/obc-afgf-dual-purpose-recep-sell-sheet.pdf")).thenReturn(damAssetWithTitleResource);

        prepareI18nLocale("de", "de");

        String requestData = "{\"dataValue\":[{\"emailTo\":\"test@example.com\",\"documentLinks\": [\"/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/af-gf-receptacles/obc-afgf-dual-purpose-recep-sell-sheet.pdf\"],\"domain\":\"example.com\"}]}";

        JSONObject json = new JSONObject(requestData);
        JSONArray dataValueArray = json.getJSONArray("dataValue");

        Assertions.assertEquals(AssetEmailAttachmentServletFixtures.emailBodyExpectedI18nDeDe, assetEmailAttachmentServlet.buildEmailBodyHtml(slingHttpServletRequest, dataValueArray), "should substitute German i18n labels for /de/de-de locale");
        Assertions.assertEquals(AssetEmailAttachmentServletFixtures.subjectExpectedI18nDeDe, AssetEmailAttachmentServlet.getI18nItems(slingHttpServletRequest, "subject"), "Subject should be de-de");
        Assertions.assertEquals(AssetEmailAttachmentServletFixtures.noReplyExpectedI18nDeDe, AssetEmailAttachmentServlet.getI18nItems(slingHttpServletRequest, "noReplyMsg"), "NoReply should be de-de");
    }

    private void prepareI18nLocale(String localeLang, String localeCountry) {
        Locale locale = new Locale(localeLang, localeCountry);
        if(localeLang.equals("en") && localeCountry.equals("us")) {
            when(slingHttpServletRequest.getResourceBundle(locale)).thenReturn(resourceBundle());
        } else if(localeLang.equals("de") && localeCountry.equals("de")) {
            when(slingHttpServletRequest.getResourceBundle(locale)).thenReturn(resourceBundleDeDe());
        }
    }
}
