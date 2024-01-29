package com.eaton.platform.core.filters;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static com.eaton.platform.core.constants.CommonConstants.CQ_TEMPLATE_PROPERTY;

/**
 * Redirect to the home page of the site in the case when page created
 * from eaton-redirect-template but cq:redirectTarget or redirectTarget property was not specified
 */
@Component(service = Filter.class,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton Redirect Fallback filter",
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                "sling.filter.pattern=" + "^/content/eaton/.*"

        })
public class RedirectFallbackFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(RedirectFallbackFilter.class);
    public static final String REDIRECT_TEMPLATE = "/apps/eaton/templates/eaton-redirect-template";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.debug("RedirectFallbackFilter :: init() :: Start");
    }

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        WCMMode mode = WCMMode.fromRequest(request);
        LOG.debug("RedirectFallbackFilter :: doFilter() :: Start");
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        ResourceResolver resourceResolver = slingRequest.getResourceResolver();
        final Resource resource = resourceResolver.getResource(slingRequest.getResource().getPath());
        if (mode == WCMMode.DISABLED && null != resource) {
            Page page = resource.adaptTo(Page.class);
            if (page != null && page.getContentResource() != null) {
                final ValueMap pageProperties = page.getContentResource().adaptTo(ValueMap.class);
                if (pageProperties != null
                        && REDIRECT_TEMPLATE.equals(pageProperties.get(CQ_TEMPLATE_PROPERTY, StringUtils.EMPTY))
                        && !pageProperties.containsKey(CommonConstants.CQ_REDIRECT_URL)
                        && !pageProperties.containsKey(CommonConstants.REDIRECT_URL)) {
                    String homePagePath = CommonUtil.getHomePagePath(page);
                    if (StringUtils.isNotEmpty(homePagePath)) {
                        String redirectPath = CommonUtil.dotHtmlLink(CommonUtil.dotHtmlLink(homePagePath), resourceResolver);
                        slingResponse.setStatus(SlingHttpServletResponse.SC_MOVED_PERMANENTLY);
                        slingResponse.setHeader(CommonConstants.LOCATION, redirectPath);;
                        return;
                    }
                }
            }
        }
        LOG.debug("RedirectFallbackFilter :: doFilter() :: End");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOG.debug("RedirectFallbackFilter: Exit from destroy() method");
    }
}
