package com.eaton.platform.integration.endeca.servlets;

import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.endeca.services.EndecaQRService;
import com.eaton.platform.integration.endeca.services.impl.EndecaServiceResponseBeanFixtures;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class EndecaQRProcessingServletTest  {

    private static final String CATALOG_NUMBER = "BR120";

    @InjectMocks
    EndecaQRProcessingServlet endecaQRProcessingServlet;

    @Mock
    EndecaQRService endecaQRService;

    @Mock
    SlingHttpServletRequest request;

    @Mock
    SlingHttpServletResponse response;

    @Mock
    RequestPathInfo requestPathInfo;

    @Mock
    RequestParameterMap requestParameterMap;

    @Mock
    RequestParameter serialNumber;

    @Mock
    RequestParameter eventId;

    @Mock
    ServletOutputStream responseOutputStream;

    @Mock
    Resource resource;

    @Mock
    ResourceResolver resourceResolver;

    @BeforeEach
    void setUp() {
        Mockito.when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        Mockito.when(request.getResourceResolver()).thenReturn(resourceResolver);
        Mockito.when(request.getRequestParameterMap()).thenReturn(requestParameterMap);
    }

    @Test
    @DisplayName("valid catalogNumber selector and matching language cookie")
    void testHappyPath() throws ServletException, IOException {
        Mockito.when(requestPathInfo.getSelectors()).thenReturn(new String[]{CATALOG_NUMBER});
        Mockito.when(endecaQRService.createPdhEndecaRequestBean(CATALOG_NUMBER)).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean());
        Mockito.when(endecaQRService.getEatonpdhlstcountries(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean())).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaPdhResponse());
        Mockito.when(request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE)).thenReturn(new Cookie(AuthConstants.ETN_REDIRECT_COOKIE, "%2Fmx%2Fen-us"));
        Mockito.when(resourceResolver.getResource("/content/eaton/mx/en-us")).thenReturn(resource);
        Mockito.when(resourceResolver.map("/content/eaton/mx/en-us/skuPage.BR120.html")).thenReturn("https://www.eaton.com/mx/en-us/skuPage.BR120.html");
        Mockito.when(response.getOutputStream()).thenReturn(responseOutputStream);
        endecaQRProcessingServlet.doGet(request, response);
        Mockito.verify(response).sendRedirect("https://www.eaton.com/mx/en-us/skuPage.BR120.html");
    }

    @Test
    @DisplayName("test servlet without catalogNumber selector - should redirect to 404 page")
    void testNoCatalogNumberSelectorRedirect404() throws ServletException, IOException {
        Mockito.when(requestPathInfo.getSelectors()).thenReturn(new String[]{});
        Mockito.when(resourceResolver.getResource("/content/eaton/us/en-us")).thenReturn(resource);
        Mockito.when(resourceResolver.map("/content/eaton/us/en-us/404.html")).thenReturn("https://www.eaton.com/us/en-us/404.html");
        endecaQRProcessingServlet.doGet(request, response);
        Mockito.verify(response).sendRedirect("https://www.eaton.com/us/en-us/404.html");
    }

    @Test
    @DisplayName("test servlet with catalogNumber selector, without languageCookie - should redirect to Global SKU Page")
    void testNoLanguageCookie() throws ServletException, IOException {
        Mockito.when(requestPathInfo.getSelectors()).thenReturn(new String[]{CATALOG_NUMBER});
        Mockito.when(endecaQRService.createPdhEndecaRequestBean(CATALOG_NUMBER)).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean());
        Mockito.when(endecaQRService.getEatonpdhlstcountries(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean())).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaPdhResponse());
        Mockito.when(request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE)).thenReturn(null);
        Mockito.when(endecaQRService.getGlobalSkuPagePath()).thenReturn("/globalSku");
        Mockito.when(resourceResolver.getResource("/content/eaton/globalSku")).thenReturn(resource);
        Mockito.when(resourceResolver.map("/content/eaton/globalSku.BR120.html")).thenReturn("https://www.eaton.com/globalSku.BR120.html");
        Mockito.when(response.getOutputStream()).thenReturn(responseOutputStream);
        endecaQRProcessingServlet.doGet(request, response);
        Mockito.verify(response).sendRedirect("https://www.eaton.com/globalSku.BR120.html");
    }

    @Test
    @DisplayName("test servlet with catalogNumber selector, with languageCookie but cookie does not match - should redirect to Global SKU Page")
    void testDoGetNoCatalogNumber() throws ServletException, IOException {
        Mockito.when(requestPathInfo.getSelectors()).thenReturn(new String[]{CATALOG_NUMBER});
        Mockito.when(endecaQRService.createPdhEndecaRequestBean(CATALOG_NUMBER)).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean());
        Mockito.when(endecaQRService.getEatonpdhlstcountries(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean())).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaPdhResponse());
        Mockito.when(request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE)).thenReturn(new Cookie(AuthConstants.ETN_REDIRECT_COOKIE, "%2Flv%2Flv-lv"));
        Mockito.when(endecaQRService.getGlobalSkuPagePath()).thenReturn("/globalSku");
        Mockito.when(resourceResolver.getResource("/content/eaton/globalSku")).thenReturn(resource);
        Mockito.when(resourceResolver.map("/content/eaton/globalSku.BR120.html")).thenReturn("https://www.eaton.com/globalSku.BR120.html");
        Mockito.when(response.getOutputStream()).thenReturn(responseOutputStream);
        endecaQRProcessingServlet.doGet(request, response);
        Mockito.verify(response).sendRedirect("https://www.eaton.com/globalSku.BR120.html");
    }

    @Test
    @DisplayName("test happy path except the locale doesn't exist in CRXDE, global does exist - should redirect to Global SKU Page")
    void testDoGetLocaleNotPresentInCRXDE() throws ServletException, IOException {
        Mockito.when(requestPathInfo.getSelectors()).thenReturn(new String[]{CATALOG_NUMBER});
        Mockito.when(endecaQRService.createPdhEndecaRequestBean(CATALOG_NUMBER)).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean());
        Mockito.when(endecaQRService.getEatonpdhlstcountries(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean())).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaPdhResponse());
        Mockito.when(request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE)).thenReturn(new Cookie(AuthConstants.ETN_REDIRECT_COOKIE, "%2Fmx%2Fen-us"));
        Mockito.when(endecaQRService.getGlobalSkuPagePath()).thenReturn("/globalSku");
        Mockito.when(resourceResolver.getResource("/content/eaton/mx/en-us")).thenReturn(null);
        Mockito.when(resourceResolver.getResource("/content/eaton/globalSku")).thenReturn(resource);
        Mockito.when(resourceResolver.map("/content/eaton/globalSku.BR120.html")).thenReturn("https://www.eaton.com/globalSku.BR120.html");
        Mockito.when(response.getOutputStream()).thenReturn(responseOutputStream);
        endecaQRProcessingServlet.doGet(request, response);
        Mockito.verify(response).sendRedirect("https://www.eaton.com/globalSku.BR120.html");
    }

    @Test
    @DisplayName("test happy path with both parameters valid - should be added to the redirect")
    void testParamsBothPresent() throws ServletException, IOException {
        Mockito.when(requestPathInfo.getSelectors()).thenReturn(new String[]{CATALOG_NUMBER});
        Mockito.when(endecaQRService.createPdhEndecaRequestBean(CATALOG_NUMBER)).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean());
        Mockito.when(endecaQRService.getEatonpdhlstcountries(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean())).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaPdhResponse());
        Mockito.when(request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE)).thenReturn(new Cookie(AuthConstants.ETN_REDIRECT_COOKIE, "%2Fmx%2Fen-us"));
        Mockito.when(requestParameterMap.getValue("S")).thenReturn(serialNumber);
        Mockito.when(requestParameterMap.getValue("E")).thenReturn(eventId);
        Mockito.when(serialNumber.getString()).thenReturn("serialNumber");
        Mockito.when(eventId.getString()).thenReturn("eventId");
        Mockito.when(resourceResolver.getResource("/content/eaton/mx/en-us")).thenReturn(resource);
        Mockito.when(resourceResolver.map("/content/eaton/mx/en-us/skuPage.BR120.html?S=serialNumber&E=eventId")).thenReturn("https://www.eaton.com/mx/en-us/skuPage.BR120.html?S=serialNumber&E=eventId");
        Mockito.when(response.getOutputStream()).thenReturn(responseOutputStream);
        endecaQRProcessingServlet.doGet(request, response);
        Mockito.verify(response).sendRedirect("https://www.eaton.com/mx/en-us/skuPage.BR120.html?S=serialNumber&E=eventId");
    }

    @Test
    @DisplayName("test happy path with S parameter valid - should be added to the redirect")
    void testParamsFirstPresent() throws ServletException, IOException {
        Mockito.when(requestPathInfo.getSelectors()).thenReturn(new String[]{CATALOG_NUMBER});
        Mockito.when(endecaQRService.createPdhEndecaRequestBean(CATALOG_NUMBER)).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean());
        Mockito.when(endecaQRService.getEatonpdhlstcountries(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean())).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaPdhResponse());
        Mockito.when(request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE)).thenReturn(new Cookie(AuthConstants.ETN_REDIRECT_COOKIE, "%2Fmx%2Fen-us"));
        Mockito.when(requestParameterMap.getValue("S")).thenReturn(serialNumber);
        Mockito.when(requestParameterMap.getValue("E")).thenReturn(null);
        Mockito.when(serialNumber.getString()).thenReturn("serialNumber");
        Mockito.when(resourceResolver.getResource("/content/eaton/mx/en-us")).thenReturn(resource);
        Mockito.when(resourceResolver.map("/content/eaton/mx/en-us/skuPage.BR120.html?S=serialNumber")).thenReturn("https://www.eaton.com/mx/en-us/skuPage.BR120.html?S=serialNumber");
        Mockito.when(response.getOutputStream()).thenReturn(responseOutputStream);
        endecaQRProcessingServlet.doGet(request, response);
        Mockito.verify(response).sendRedirect("https://www.eaton.com/mx/en-us/skuPage.BR120.html?S=serialNumber");
    }

    @Test
    @DisplayName("test happy path with E parameter valid - should be added to the redirect")
    void testParamsSecondPresent() throws ServletException, IOException {
        Mockito.when(requestPathInfo.getSelectors()).thenReturn(new String[]{CATALOG_NUMBER});
        Mockito.when(endecaQRService.createPdhEndecaRequestBean(CATALOG_NUMBER)).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean());
        Mockito.when(endecaQRService.getEatonpdhlstcountries(EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean())).thenReturn(EndecaServiceResponseBeanFixtures.createEndecaPdhResponse());
        Mockito.when(request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE)).thenReturn(new Cookie(AuthConstants.ETN_REDIRECT_COOKIE, "%2Fmx%2Fen-us"));
        Mockito.when(requestParameterMap.getValue("S")).thenReturn(null);
        Mockito.when(requestParameterMap.getValue("E")).thenReturn(eventId);
        Mockito.when(eventId.getString()).thenReturn("eventId");
        Mockito.when(resourceResolver.getResource("/content/eaton/mx/en-us")).thenReturn(resource);
        Mockito.when(resourceResolver.map("/content/eaton/mx/en-us/skuPage.BR120.html?E=eventId")).thenReturn("https://www.eaton.com/mx/en-us/skuPage.BR120.html?E=eventId");
        Mockito.when(response.getOutputStream()).thenReturn(responseOutputStream);
        endecaQRProcessingServlet.doGet(request, response);
        Mockito.verify(response).sendRedirect("https://www.eaton.com/mx/en-us/skuPage.BR120.html?E=eventId");
    }

}