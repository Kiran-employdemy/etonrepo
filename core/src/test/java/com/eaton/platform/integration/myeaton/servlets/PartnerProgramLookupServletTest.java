package com.eaton.platform.integration.myeaton.servlets;

import static com.eaton.platform.core.util.ResourceBundleFixtures.resourceBundlePartnerProgramDeDe;
import static com.eaton.platform.core.util.ResourceBundleFixtures.resourceBundlePartnerProgramEnUs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PartnerProgramLookupServletTest {

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @InjectMocks
    PartnerProgramLookupServlet partnerProgramLookupServlet = new PartnerProgramLookupServlet();

    @Mock
    UserProfile userProfile;

    @Mock
    PrintWriter printWriter;

    @Mock
    AuthenticationToken authenticationToken;

    @Mock
    AuthorizationService authorizationService;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Resource pageResource;

    @Mock
    private Page currentPage;

    private String pagePath = "/us/en-us/secure/profile";
    private String contentPagePath = "/content/eaton" + pagePath;
    private String pageUrl = "https://www-local.eaton.com" + pagePath + CommonConstants.HTML_EXTN;

    @Nested
    class TestDoGet {
        @BeforeEach
        void setup() {

            when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
            when(slingHttpServletRequest.getHeader(CommonConstants.REFERER_URL)).thenReturn(pageUrl);
            when(pageResource.getPath()).thenReturn(contentPagePath);
            when(resourceResolver.resolve(pagePath)).thenReturn(pageResource);

            when(resourceResolver.getResource(contentPagePath)).thenReturn(pageResource);
            when(pageResource.adaptTo(Page.class)).thenReturn(currentPage);

        }

        @Test
        @DisplayName("When servlet is called and authentication token is not found, servlet responds with 401")
        void testDoGetWhenAuthenticationTokenNull() throws IOException {


            when(authorizationService.getTokenFromSlingRequest(slingHttpServletRequest)).thenReturn(null);

            partnerProgramLookupServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

            Mockito.verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization token not found in sling request.");

        }

        @Test
        @DisplayName("When servlet is called and user profile in authentication token is null, servlet responds with 401")
        void testDoGetWhenUserProfileNull() throws IOException {

            when(authorizationService.getTokenFromSlingRequest(slingHttpServletRequest)).thenReturn(authenticationToken);
            when(authenticationToken.getUserProfile()).thenReturn(null);

            partnerProgramLookupServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

            Mockito.verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Cannot find user profile from authentication token.");

        }

        @Test
        @DisplayName("When servlet is called and user doesn't have partner program data, servlet responds with 404")
        void testDoGetWhenUserHasNoPartnerProgramData() throws IOException {

            when(authorizationService.getTokenFromSlingRequest(slingHttpServletRequest)).thenReturn(authenticationToken);
            when(authenticationToken.getUserProfile()).thenReturn(userProfile);
            when(userProfile.getUid()).thenReturn("test@example.com");

            List<String> blankData = new ArrayList<>();

            when(userProfile.getPartnerProgramTypeAndTierLevels()).thenReturn(blankData);

            when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);

            partnerProgramLookupServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

            Mockito.verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "User has no partner program data.");

        }

    }

    @Nested
    class TestGetCurrentPage {

        @Test
        @DisplayName("When calling getCurrentPage with a valid slingRequest, the current page resource is returned.")
        void testGetCurrentPageWhenNotNull() throws IOException {

            when(slingHttpServletRequest.getHeader(CommonConstants.REFERER_URL)).thenReturn(pageUrl);
            when(pageResource.getPath()).thenReturn(contentPagePath);
            when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
            when(resourceResolver.resolve(pagePath)).thenReturn(pageResource);

            when(resourceResolver.getResource(contentPagePath)).thenReturn(pageResource);
            when(pageResource.adaptTo(Page.class)).thenReturn(currentPage);
            when(currentPage.getPath()).thenReturn(contentPagePath);

            Page actualCurrentPage = partnerProgramLookupServlet.getCurrentPage(slingHttpServletRequest, slingHttpServletResponse);
            assertNotNull(actualCurrentPage, "current page is not null");
            assertEquals(contentPagePath, actualCurrentPage.getPath(), "content path of page is correct");

        }

        @Test
        @DisplayName("When calling getCurrentPage and referer url is null, the servlet responds with an error.")
        void testGetCurrentPageWhenRefererUrlNull() throws IOException {

            when(slingHttpServletRequest.getHeader(CommonConstants.REFERER_URL)).thenReturn(null);

            Page currentPage = partnerProgramLookupServlet.getCurrentPage(slingHttpServletRequest, slingHttpServletResponse);
            assertNull(currentPage, "Expect current page to be null");

            Mockito.verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Referer url from sling request is blank.");

        }


    }
    
    @Nested
    class TestTranslateData {

        List<String> testInputData = new ArrayList<>();

        @BeforeEach
        void setUp() {

            testInputData.add("progConsultantProgram|Registered");
            testInputData.add("progElectricalInstaller");
            testInputData.add("progCtrPanelBuilderProgram|Authorized");

        }
        @Test
        @DisplayName("When calling translateData to en-us with valid slingRequest, valid currentPage and partner program formatted data, the partner program data is returned and translated.")
        void testTranslateDataEnUs() {

            JSONArray expectedDataEnUs = new JSONArray();
            expectedDataEnUs.put("Consultant program|Registered");
            expectedDataEnUs.put("Electrical installer program");
            expectedDataEnUs.put("Control panel builder program|Authorized");

            Locale enUs = new Locale("en", "us");
            when(currentPage.getLanguage(true)).thenReturn(enUs);
            when(slingHttpServletRequest.getResourceBundle(enUs)).thenReturn(resourceBundlePartnerProgramEnUs());

            JSONArray actualData = partnerProgramLookupServlet.translateData(slingHttpServletRequest, currentPage, testInputData);

            assertEquals(actualData, expectedDataEnUs, "data has been translated to en-us correctly");

        }

        @Test
        @DisplayName("When calling translateData to de-de with valid slingRequest, valid currentPage and partner program formatted data, the partner program data is returned and translated.")
        void testTranslateDataDeDe() {

            JSONArray expectedDataDeDe = new JSONArray();
            expectedDataDeDe.put("Beraterprogramm|Eingetragen");
            expectedDataDeDe.put("Elektroinstallateurprogramm");
            expectedDataDeDe.put("Programm zum Erstellen von Bedienfeldern|Autorisiert");

            Locale deDe = new Locale("de", "de");
            when(currentPage.getLanguage(true)).thenReturn(deDe);
            when(slingHttpServletRequest.getResourceBundle(deDe)).thenReturn(resourceBundlePartnerProgramDeDe());

            JSONArray actualData = partnerProgramLookupServlet.translateData(slingHttpServletRequest, currentPage, testInputData);

            assertEquals(actualData, expectedDataDeDe, "data has been translated to de-de correctly");

        }
        
    }

}