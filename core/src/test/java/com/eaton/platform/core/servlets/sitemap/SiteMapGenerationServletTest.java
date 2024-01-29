package com.eaton.platform.core.servlets.sitemap;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.services.SiteMapGenerationService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SiteMapGenerationServletTest {

    private AemContext ctx = new AemContext();
    private SiteMapGenerationServlet servlet;
    @Mock
    private Externalizer externalizer;
    @Mock
    private SiteMapGenerationService service;

    @BeforeEach
    public void setUp() {
        ctx.registerService(Externalizer.class, externalizer);
        ctx.registerService(SiteMapGenerationService.class, service);
        servlet = ctx.registerInjectActivateService(new SiteMapGenerationServlet());
        Page page = ctx.create().page("/content/page");
        ctx.currentPage(page);

        when(service.getExternalExcludeProperty()).thenReturn("false");
        when(service.getChangefreqProperties()).thenReturn(new String[]{});
        when(service.getPriorityProperties()).thenReturn(new String[]{});
        when(externalizer.externalLink(any(), any(), any())).thenReturn("/page");
    }

    @Test
    @DisplayName("The encoding and content type should be correct in the response")
    public void testSetCorrectEncodingAndContentTypeToResponse() throws ServletException, IOException {

        servlet.doGet(ctx.request(), ctx.response());

        assertEquals( "UTF-8", ctx.response().getCharacterEncoding(),"UTF-8 is expected encoding");
        assertEquals("application/xml;charset=UTF-8", ctx.response().getContentType(), "application/xml is expected content type");
    }

    @Test
    @DisplayName("The encoding the XML must be UTF-8")
    public void testReturnOutputWithCorrectEncoding() throws ServletException, IOException {

        servlet.doGet(ctx.request(), ctx.response());

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" +
                "<url><loc>/page.html</loc></url>" +
                "</urlset>", ctx.response().getOutputAsString(), "Should be expected output");
    }
}