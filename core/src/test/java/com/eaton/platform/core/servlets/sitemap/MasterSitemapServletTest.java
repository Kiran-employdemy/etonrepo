package com.eaton.platform.core.servlets.sitemap;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CountryLangCodeLastmodConfigService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class MasterSitemapServletTest {
    private AemContext ctx = new AemContext();
    private MasterSitemapServlet servlet;

    @Mock
    public CountryLangCodeLastmodConfigService countryLangCodeLastmodConfigService;

    @Mock
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        ctx.registerService(AdminService.class, adminService);
        ctx.registerService(CountryLangCodeLastmodConfigService.class, countryLangCodeLastmodConfigService);
        servlet = ctx.registerInjectActivateService( new MasterSitemapServlet());
        Page page = ctx.create().page("/content/page");
        ctx.currentPage(page);
    }

    @Test
    @DisplayName("The encoding the XML must be UTF-8")
    public void testAddEncodingToXMLOutput(){

        servlet.createXmlStream(ctx.response(), Map.of("/content/page.sitemap.html", "2022-07-27T01:36:49.449-04:00"));

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" +
                "<sitemap><loc>/content/page.sitemap.html</loc><lastmod>2022-07-27T01:36:49.449-04:00</lastmod></sitemap>" +
                "<sitemap><loc>/content/page.skusitemap.html</loc><lastmod>2022-07-27T01:36:49.449-04:00</lastmod>" +
                "</sitemap></sitemapindex>", ctx.response().getOutputAsString(),"Should be expected output");
    }
}