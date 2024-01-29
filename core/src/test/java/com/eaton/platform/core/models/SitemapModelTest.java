package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.sitemap.Navigation;
import com.eaton.platform.core.bean.sitemap.SitemapBean;
import com.eaton.platform.core.services.sitemap.HtmlSitemapService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SitemapModelTest {
    private AemContext ctx = new AemContext();

    @Mock
    private HtmlSitemapService htmlSitemapService;

    @BeforeEach
    public void setUp() {
        ctx.create().page("/content/eaton/us");
        ctx.create().page("/content/eaton/us/en-us");
        Page sitemap = ctx.create().page("/content/eaton/us/en-us/sitemap",
                "/test",
                Map.of("sitemapRootPath", "/content/eaton/us/en-us",
                        "sitemapLinkLevels", "2")
        );
        ctx.registerService(HtmlSitemapService.class, htmlSitemapService);
        ctx.currentPage(sitemap);
    }

    @Test
    @DisplayName("Test initialization of primary navigation list")
    public void testInitPrimaryNavigationList() {
        Navigation navigation = new Navigation();
        when(htmlSitemapService.getNavigationList(any(), anyInt(), anyInt(), eq(false))).thenReturn(List.of(navigation));
        SitemapModel sitemapModel = ctx.request().adaptTo(SitemapModel.class);

        assertEquals(navigation, sitemapModel.getPrimaryNavList().get(0), "Navigation item should be the same");
    }


    @Test
    @DisplayName("Test initialization of modified navigation list")
    public void testInitModifiedNavigationList() throws RepositoryException {
        Page sitemap = ctx.create().page("/content/eaton/us/en-us/sitemap-full",
                "/test",
                Map.of("sitemapRootPath", "/content/eaton/us/en-us",
                        "sitemapLinkLevels", "2")
        );
        ctx.currentPage(sitemap);
        SitemapBean sitemapBean = new SitemapBean();
        when(htmlSitemapService.getModifiedPages("cq:lastModified",
                "2023-09-21T00:00:00.000",
                "/content/eaton/us/en-us/"
                , true, ctx.resourceResolver())).thenReturn(List.of(sitemapBean));
        ctx.request().addRequestParameter("date", "2023/09/21");
        SitemapModel sitemapModel = ctx.request().adaptTo(SitemapModel.class);

        assertEquals(sitemapBean, sitemapModel.getModifiedNavList().get(0), "SitemapBean should be the same");
    }

    @Test
    @DisplayName("Test ignore date parameter for sitemap.html page")
    public void testIgnoreDateParameterForSitemapHtml() throws RepositoryException {
        Navigation navigation = new Navigation();
        when(htmlSitemapService.getNavigationList(any(), anyInt(), anyInt(), eq(false))).thenReturn(List.of(navigation));

        ctx.request().addRequestParameter("date", "2023/09/21");
        SitemapModel sitemapModel = ctx.request().adaptTo(SitemapModel.class);

        assertEquals(navigation, sitemapModel.getPrimaryNavList().get(0), "Navigation item should be the same");
        assertNull(sitemapModel.getModifiedNavList(), "Modified list should be null");
    }
}