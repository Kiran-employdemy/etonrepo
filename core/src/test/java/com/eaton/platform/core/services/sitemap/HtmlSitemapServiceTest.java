package com.eaton.platform.core.services.sitemap;

import com.adobe.cq.social.srp.internal.AbstractSchemaMapper;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.sitemap.Navigation;
import com.eaton.platform.core.bean.sitemap.SitemapBean;
import com.eaton.platform.core.services.SiteMapGenerationService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
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
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class HtmlSitemapServiceTest {

    private AemContext ctx = new AemContext(ResourceResolverType.JCR_OAK);

    @Mock
    private SiteMapGenerationService siteMapGenerationService;

    private HtmlSitemapService htmlSitemapService;
    private Page rootPage;

    @BeforeEach
    public void setUp() {
        rootPage = ctx.create().page("/content/eaton/us/en-us");
        ctx.registerService(SiteMapGenerationService.class, siteMapGenerationService);
        htmlSitemapService = ctx.registerInjectActivateService(new HtmlSitemapService());
    }

    @Test
    @DisplayName("Should build navigation list based on the sitemapLinkLevel")
    public void testGetListNavigation() {
        when(siteMapGenerationService.getInternalExcludeProperty()).thenReturn("hideInHtml");

        ctx.create().page("/content/eaton/us/en-us/level1");
        ctx.create().page("/content/eaton/us/en-us/level1/level2");
        ctx.create().page("/content/eaton/us/en-us/level11");

        List<Navigation> navigationList = htmlSitemapService.getNavigationList(rootPage, 1, 2, true);

        assertEquals(2, navigationList.size(), "Size should be as expected");
        SitemapBean nav = navigationList.get(0).getNav();
        assertEquals("level1", nav.getLinkTitle(), "Title should be as expected");
        assertEquals("/content/eaton/us/en-us/level1.html", nav.getLinkPath(), "Link should be as expected");
        assertEquals(1, navigationList.get(0).getInnerNavList().size(), "Size should be as expected");

    }

    @Test
    @DisplayName("Should create empty page bean in the case when the page is secure")
    public void testIgnorePropertiesForBeanForSecurePage() {
        when(siteMapGenerationService.getInternalExcludeProperty()).thenReturn("hideInHtml");

        ctx.create().page("/content/eaton/us/en-us/secure", "/test",
                Map.of("securePage", "true"));
        ctx.create().page("/content/eaton/us/en-us/level1");

        List<Navigation> navigationList = htmlSitemapService.getNavigationList(rootPage, 1, 2, false);

        assertEquals(2, navigationList.size(), "Size should be as expected");
        assertNull(navigationList.get(0).getNav(), "Object should be null");
    }

    @Test
    @DisplayName("Should ignore hidden page")
    public void testIgnoreHiddenPage() {
        when(siteMapGenerationService.getInternalExcludeProperty()).thenReturn("hideInHtml");

        ctx.create().page("/content/eaton/us/en-us/secure", "/test",
                Map.of("hideInHtml", "true"));
        ctx.create().page("/content/eaton/us/en-us/level1");

        List<Navigation> navigationList = htmlSitemapService.getNavigationList(rootPage, 1, 2, true);

        assertEquals(1, navigationList.size(), "Size should be as expected");
        SitemapBean nav = navigationList.get(0).getNav();
        assertEquals("level1", nav.getLinkTitle(), "Title should be as expected");
        assertEquals("/content/eaton/us/en-us/level1.html", nav.getLinkPath(), "Link should be as expected");
    }

    @Test
    @DisplayName("Should ignore sitemap-full page")
    public void testIgnoreSitemapFullPage() {
        when(siteMapGenerationService.getInternalExcludeProperty()).thenReturn("hideInHtml");

        ctx.create().page("/content/eaton/us/en-us/sitemap-full");
        ctx.create().page("/content/eaton/us/en-us/level1");

        List<Navigation> navigationList = htmlSitemapService.getNavigationList(rootPage, 1, 2, true);

        assertEquals(1, navigationList.size(), "Size should be as expected");
        SitemapBean nav = navigationList.get(0).getNav();
        assertEquals("level1", nav.getLinkTitle(), "Title should be as expected");
        assertEquals("/content/eaton/us/en-us/level1.html", nav.getLinkPath(), "Link should be as expected");
    }

    @Test
    @DisplayName("Should include only first level pages")
    public void testIncludeOnlyFirstLevel() {
        when(siteMapGenerationService.getInternalExcludeProperty()).thenReturn("hideInHtml");

        ctx.create().page("/content/eaton/us/en-us/level1");
        ctx.create().page("/content/eaton/us/en-us/level1/level2");

        List<Navigation> navigationList = htmlSitemapService.getNavigationList(rootPage, 1, 1, true);

        assertEquals(1, navigationList.size(), "Size should be as expected");
        assertNull(navigationList.get(0).getInnerNavList(), "Item should be null");
    }

    @Test
    @DisplayName("Should return pages based on cq:lastModified date")
    public void testGetModifiedPages() throws RepositoryException {
        ctx.create().page("/content/eaton/us/en-us/level1", "/test",
                Map.of(AbstractSchemaMapper.CQ_LAST_MODIFIED, "2023-09-23T00:00:00.000"));
        List<SitemapBean> modifiedPages = htmlSitemapService.getModifiedPages(AbstractSchemaMapper.CQ_LAST_MODIFIED,
                "2023-09-20T00:00:00.000",
                "/content/eaton/us/en-us",
                true, ctx.resourceResolver());

        assertEquals(1, modifiedPages.size(), "Size should be as expected");
        assertEquals("level1", modifiedPages.get(0).getLinkTitle(), "Title should be as expected");
        assertEquals("/content/eaton/us/en-us/level1.html", modifiedPages.get(0).getLinkPath(), "Link should be as expected");

    }

    @Test
    @DisplayName("Should ignore pages that were modified before the passed date")
    public void testGetIgnoreOutOfRangeModifiedPage() throws RepositoryException {
        ctx.create().page("/content/eaton/us/en-us/level1", "/test",
                Map.of(AbstractSchemaMapper.CQ_LAST_MODIFIED, "2023-09-19T00:00:00.000"));
        List<SitemapBean> modifiedPages = htmlSitemapService.getModifiedPages(AbstractSchemaMapper.CQ_LAST_MODIFIED,
                "2023-09-20T00:00:00.000",
                "/content/eaton/us/en-us",
                true, ctx.resourceResolver());

        assertEquals(0, modifiedPages.size(), "Size should be as expected");
    }

}