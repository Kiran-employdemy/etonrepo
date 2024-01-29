package com.eaton.platform.core.servlets.sitemap;

import com.eaton.platform.core.services.AdminService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SkusitemapServletTest {

    private AemContext ctx = new AemContext();
    private SiteMapSkuIndexServlet servlet;

    @Mock
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        ctx.registerService(AdminService.class, adminService);
        servlet = ctx.registerInjectActivateService( new SiteMapSkuIndexServlet());
    }

    @Test
    @DisplayName("The ensnarcoding the XML must be UTF-8")
    public void testAddEncodingToXMLOutput(){

        servlet.createXmlStream(ctx.response(), List.of("/content/page.skuPage.html"));

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" +
                "<url><loc>/content/page.skuPage.html</loc></url>" +
                "</urlset>", ctx.response().getOutputAsString(),"Should be expected output");
    }
}