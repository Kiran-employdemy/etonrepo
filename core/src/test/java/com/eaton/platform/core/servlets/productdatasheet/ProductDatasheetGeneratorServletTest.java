package com.eaton.platform.core.servlets.productdatasheet;

import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.search.service.SkuSearchService;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDatasheetGeneratorServletTest {

    @InjectMocks
    ProductDatasheetGeneratorServlet productDatasheetGeneratorServlet = new ProductDatasheetGeneratorServlet();
    @Mock
    SkuSearchService skuSearchService;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    SlingHttpServletResponse slingHttpServletResponse;
    @Mock
    PrintWriter printWriter;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    Externalizer externalizer;
    Map<String, String> br120AndInventoryId;
    Map<String, String> br120WithSpecialCharsAndInventoryId;
    Map<String, String> br120AndCh120WithInventoryIds;
    SKUDetailsResponseBean skuDetailsResponseBeanFail;


    @BeforeEach
    void setUp() {
        br120AndInventoryId = new HashMap<>();
        br120AndInventoryId.put("BR120", "blah");
        br120WithSpecialCharsAndInventoryId = new HashMap<>();
        br120WithSpecialCharsAndInventoryId.put("BR 120./MODEL X", "blah");
        br120AndCh120WithInventoryIds = new HashMap<>();
        br120AndCh120WithInventoryIds.put("BR120", "blah");
        br120AndCh120WithInventoryIds.put("CH120", "blahblah");
    }

    @Test
    @DisplayName("doPost writes json with for the locale 1 skuId with urls")
    void testDoPostSingleSkuIDSingleLocale() throws ServletException, IOException {
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{\"skuIDs\": [\"br120\"] ,\"locales\":[\"en-us\"]}")));
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(externalizer.externalLink(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON, CommonConstants.COUNTRY)).thenReturn("https://www-local.eaton.com/country");
        when(skuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("BR120"))).thenReturn(br120AndInventoryId);
        when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);

        productDatasheetGeneratorServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(printWriter).write("{\"datasheetGen\":[{\"skuId\":\"BR120\",\"skuDeepLinksList\":[{\"locale\":\"English\",\"skuLink\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\",\"downloadPDF\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf\",\"downloadXls\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\"}]}]}");

    }
    @Test
    @DisplayName("doPost writes json with urls html escaped when special chars")
    void testDoPostSingleSkuIDSingleLocaleSpecialChars() throws ServletException, IOException {
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{\"skuIDs\": [\"br 120./model x\"] ,\"locales\":[\"en-us\"]}")));
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(externalizer.externalLink(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON, CommonConstants.COUNTRY)).thenReturn("https://www-local.eaton.com/country");
        when(skuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("BR 120./MODEL X"))).thenReturn(br120WithSpecialCharsAndInventoryId);
        when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);

        productDatasheetGeneratorServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(printWriter).write("{\"datasheetGen\":[{\"skuId\":\"BR 120./MODEL X\",\"skuDeepLinksList\":[{\"locale\":\"English\",\"skuLink\":\"https://www-local.eaton.com/us/en-us/skuPage.BR%2520120%252E%252FMODEL%2520X.html\",\"downloadPDF\":\"https://www-local.eaton.com/us/en-us/skuPage.BR%2520120%252E%252FMODEL%2520X.pdf\",\"downloadXls\":\"https://www-local.eaton.com/us/en-us/skuPage.BR%2520120%252E%252FMODEL%2520X.html\"}]}]}");

    }
    @Test
    @DisplayName("doPost writes json with for the locale 2 skuIds with urls")
    void testDoPostMultipleSkuIDSingleLocale() throws ServletException, IOException {
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{\"skuIDs\": [\"br120\",\"ch120\"] ,\"locales\":[\"en-us\"]}")));
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(externalizer.externalLink(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON, CommonConstants.COUNTRY)).thenReturn("https://www-local.eaton.com/country");
        when(skuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("BR120","CH120"))).thenReturn(br120AndCh120WithInventoryIds);
        when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);

        productDatasheetGeneratorServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(printWriter).write("{\"datasheetGen\":[{\"skuId\":\"BR120\",\"skuDeepLinksList\":[{\"locale\":\"English\",\"skuLink\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\",\"downloadPDF\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf\",\"downloadXls\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\"}]},{\"skuId\":\"CH120\",\"skuDeepLinksList\":[{\"locale\":\"English\",\"skuLink\":\"https://www-local.eaton.com/us/en-us/skuPage.CH120.html\",\"downloadPDF\":\"https://www-local.eaton.com/us/en-us/skuPage.CH120.pdf\",\"downloadXls\":\"https://www-local.eaton.com/us/en-us/skuPage.CH120.html\"}]}]}");

    }

    @Test
    @DisplayName("doPost writes json with only the successful locale filled in completely")
    void testDoPostSingleSkuIDMultipleLocalesSecondFail() throws ServletException, IOException {
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{\"skuIDs\": [\"br120\"] ,\"locales\":[\"en-us\",\"de-de\"]}")));
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(externalizer.externalLink(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON, CommonConstants.COUNTRY)).thenReturn("https://www-local.eaton.com/country");
        when(skuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("BR120"))).thenReturn(br120AndCh120WithInventoryIds);
        when(skuSearchService.searchInventoryIdsPerSkuIdsForLocale("de-de", List.of("BR120"))).thenReturn(new HashMap<>());
        when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);

        productDatasheetGeneratorServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(printWriter).write("{\"datasheetGen\":[{\"skuId\":\"BR120\",\"skuDeepLinksList\":[{\"locale\":\"English\",\"skuLink\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\",\"downloadPDF\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf\",\"downloadXls\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\"},{\"locale\":\"German\",\"skuLink\":\"\",\"downloadPDF\":\"\",\"downloadXls\":\"\"}]}]}");

    }

    @Test
    @DisplayName("doPost writes json for both locales when both yield result")
    void testDoPostSingleSkuIDMultipleLocalesAllOK() throws ServletException, IOException {
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{\"skuIDs\": [\"br120\"] ,\"locales\":[\"en-us\",\"de-de\"]}")));
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(externalizer.externalLink(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON, CommonConstants.COUNTRY)).thenReturn("https://www-local.eaton.com/country");
        when(skuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("BR120"))).thenReturn(br120AndInventoryId);
        when(skuSearchService.searchInventoryIdsPerSkuIdsForLocale("de-de", List.of("BR120"))).thenReturn(br120AndInventoryId);
        when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);

        productDatasheetGeneratorServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(printWriter).write("{\"datasheetGen\":[{\"skuId\":\"BR120\",\"skuDeepLinksList\":[{\"locale\":\"English\",\"skuLink\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\",\"downloadPDF\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf\",\"downloadXls\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\"},{\"locale\":\"German\",\"skuLink\":\"https://www-local.eaton.com/de/de-de/skuPage.BR120.html\",\"downloadPDF\":\"https://www-local.eaton.com/de/de-de/skuPage.BR120.pdf\",\"downloadXls\":\"https://www-local.eaton.com/de/de-de/skuPage.BR120.html\"}]}]}");

    }

    @Test
    @DisplayName("doPost writes json multiple locale and multiple skuIds, second local fails on second skuId")
    void testDoPostMultipleSkuIDMultipleLocalesSecondFail() throws ServletException, IOException {
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{\"skuIDs\": [\"br120\",\"ch120\"] ,\"locales\":[\"en-us\",\"de-de\"]}")));
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(externalizer.externalLink(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON, CommonConstants.COUNTRY)).thenReturn("https://www-local.eaton.com/country");
        when(skuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("BR120","CH120"))).thenReturn(br120AndCh120WithInventoryIds);
        when(skuSearchService.searchInventoryIdsPerSkuIdsForLocale("de-de", List.of("BR120","CH120"))).thenReturn(br120AndInventoryId);
        when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);

        productDatasheetGeneratorServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(printWriter).write("{\"datasheetGen\":[{\"skuId\":\"BR120\",\"skuDeepLinksList\":[{\"locale\":\"English\",\"skuLink\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\",\"downloadPDF\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf\",\"downloadXls\":\"https://www-local.eaton.com/us/en-us/skuPage.BR120.html\"},{\"locale\":\"German\",\"skuLink\":\"https://www-local.eaton.com/de/de-de/skuPage.BR120.html\",\"downloadPDF\":\"https://www-local.eaton.com/de/de-de/skuPage.BR120.pdf\",\"downloadXls\":\"https://www-local.eaton.com/de/de-de/skuPage.BR120.html\"}]},{\"skuId\":\"CH120\",\"skuDeepLinksList\":[{\"locale\":\"English\",\"skuLink\":\"https://www-local.eaton.com/us/en-us/skuPage.CH120.html\",\"downloadPDF\":\"https://www-local.eaton.com/us/en-us/skuPage.CH120.pdf\",\"downloadXls\":\"https://www-local.eaton.com/us/en-us/skuPage.CH120.html\"},{\"locale\":\"German\",\"skuLink\":\"\",\"downloadPDF\":\"\",\"downloadXls\":\"\"}]}]}");

    }
}