package com.eaton.platform.core.servlets.sitemap;

import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CountryLangCodeLastmodConfigService;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.services.EndecaService;
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
class SiteMapSkuIndexServletTest {

    private AemContext ctx = new AemContext();
    private SkusitemapServlet servlet;

    @Mock
    public CountryLangCodeLastmodConfigService countryLangCodeLastmodConfigService;


    @Mock
    public EndecaRequestService endecaRequestService;

    @Mock
    public EndecaService endecaService;

    @Mock
    public EatonConfigService configService;

    @Mock
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        ctx.registerService(CountryLangCodeLastmodConfigService.class, countryLangCodeLastmodConfigService);
        ctx.registerService(EndecaRequestService.class, endecaRequestService);
        ctx.registerService(EndecaService.class, endecaService);
        ctx.registerService(EatonConfigService.class, configService);
        ctx.registerService(AdminService.class, adminService);
        servlet = ctx.registerInjectActivateService( new SkusitemapServlet());
    }

    @Test
    @DisplayName("The encoding the XML must be UTF-8")
    public void testAddEncodingToXMLOutput(){

        servlet.createXmlStream(ctx.response(), List.of("/content/page.skuPage.html"));

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" +
                "<url><loc>/content/page.skuPage.html</loc></url>" +
                "</urlset>", ctx.response().getOutputAsString(),"Should be expected output");
    }
}