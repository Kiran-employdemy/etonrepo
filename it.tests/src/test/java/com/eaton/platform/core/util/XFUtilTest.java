package com.eaton.platform.core.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit.AemContext;

public class XFUtilTest {

    @Rule
    public final AemContext aemContext = new AemContext();

    @Mock
    private Page page;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource resource;

    @Mock
    XFUtil xfUtil;

    @Before
    public void setUp() {
        resourceResolver = mock(ResourceResolver.class);
        page = mock(Page.class);

    }

    @Test
    public void testUpdateHTMLLinkInXF() {
        xfUtil = new XFUtil();
        String link = "/content/eaton/language-masters/en-us/samplepage.html";
        String expectedLink = "/content/eaton/hr/hr-hr/samplepage.html";
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(link)).thenReturn(resource);
        Page pageAtLink = mock(Page.class);
        when(resource.adaptTo(Page.class)).thenReturn(pageAtLink);
        Page countryPageAtLink = mock(Page.class);
        when(pageAtLink.getAbsoluteParent(2)).thenReturn(countryPageAtLink);
        when(countryPageAtLink.getName()).thenReturn("language-masters");
        Page localePageAtLink = mock(Page.class);
        when(pageAtLink.getAbsoluteParent(3)).thenReturn(localePageAtLink);
        when(localePageAtLink.getName()).thenReturn("hr");
        String linkPath = XFUtil.transformURL(resourceResolver, "hr-hr", "hr", expectedLink, "en-us",
                "language-masters");
        assertEquals(expectedLink, linkPath);
    }

    @Test
    public void testTransformURL() {
        xfUtil = new XFUtil();
        String countryReplacedLink = "/content/eaton/language-masters/id-id/samplepage.html";
        String expectedLink = "/content/eaton/id/id-id/samplepage.html";
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource(countryReplacedLink)).thenReturn(resource);
        Page pageAtLink = mock(Page.class);
        when(resource.adaptTo(Page.class)).thenReturn(pageAtLink);
        Page localePageAtLink = mock(Page.class);
        when(pageAtLink.getAbsoluteParent(2)).thenReturn(localePageAtLink);
        when(localePageAtLink.getName()).thenReturn("id");
        String linkPath = XFUtil.transformURL(resourceResolver, "id-id", "id", expectedLink, "id",
                "language-masters");
        assertEquals(expectedLink, linkPath);
    }
}
