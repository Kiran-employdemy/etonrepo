package com.eaton.platform.core.filters;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.eaton.platform.core.constants.CommonConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

import static com.day.cq.wcm.api.WCMMode.REQUEST_ATTRIBUTE_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RedirectFallbackFilterTest {

    private AemContext ctx = new AemContext();

    @InjectMocks
    private RedirectFallbackFilter redirectFallbackFilter;

    @Mock
    private FilterChain filterChain;
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    @BeforeEach
    private void beforeEach() {
        request = ctx.request();
        response = ctx.response();
    }

    @Test
    void testDoNothingWhenModeIsNotDisabled() throws ServletException, IOException {
        ctx.request().setAttribute(REQUEST_ATTRIBUTE_NAME, WCMMode.EDIT);
        Page page = ctx.create().page("/test");
        ctx.currentPage(page);

        redirectFallbackFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }


    @Test
    void testDoNothingWhenResourceIsNotPage() throws ServletException, IOException {
        ctx.request().setAttribute(REQUEST_ATTRIBUTE_NAME, WCMMode.DISABLED);
        Resource resource = ctx.create().resource("/test");
        ctx.currentResource(resource);

        redirectFallbackFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoNothingWhenTemplateIsCorrectAndRedirectTargetIsDefined() throws ServletException, IOException {
        ctx.request().setAttribute(REQUEST_ATTRIBUTE_NAME, WCMMode.DISABLED);
        Page homePage = ctx.create().page("/content/eaton/ca/en-gb");
        Page redirectPage = ctx.create().page("/content/eaton/ca/en-gb/catalog",
                "/apps/eaton/templates/eaton-redirect-template",
                Map.of("cq:redirectTarget", "/test"));
        ctx.currentResource(redirectPage.getPath());

        redirectFallbackFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testRedirectWhenTemplateIsCorrectAndRedirectTargetIsNotDefined() throws ServletException, IOException {
        ctx.request().setAttribute(REQUEST_ATTRIBUTE_NAME, WCMMode.DISABLED);
        Page homePage = ctx.create().page("/content/eaton/ca/en-gb");
        Page redirectPage = ctx.create().page("/content/eaton/ca/en-gb/catalog", "/apps/eaton/templates/eaton-redirect-template");
        ctx.currentResource(redirectPage.getPath());

        redirectFallbackFilter.doFilter(request, response, filterChain);

        assertEquals("Should return 301 redirect code", 301, response.getStatus());
        assertEquals("Should redirect to home page", "/content/eaton/ca/en-gb.html", response.getHeader(CommonConstants.LOCATION));
    }
}