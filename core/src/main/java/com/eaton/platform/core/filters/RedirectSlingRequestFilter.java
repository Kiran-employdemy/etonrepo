package com.eaton.platform.core.filters;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.services.EndecaSecretKeyConfiguration;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.engine.EngineConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.WCMMode;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import org.osgi.service.component.ComponentContext;
import java.util.Set;

import static com.eaton.platform.core.constants.CommonConstants.RUNMODE_AUTHOR;
import static com.eaton.platform.core.constants.CommonConstants.RUNMODE_PUBLISH;
import static com.eaton.platform.integration.endeca.constants.EndecaConstants.RUNMODE_ENDECA;

/*
 * This class is for restricting any Endeca or service call in case of "redirectTarget" property
 * available on the page. As of now restring this call only for /content/eaton/.* cq:pages. It is only active when
 * the 'endeca' runmode is applied to the AEM instance.
 * */
@Component(service = Filter.class,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton Redirect Implementation filter",
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                Constants.SERVICE_RANKING + ":Integer=-700",
                "sling.filter.pattern=" + SecureConstants.SECURE_ENDECA_REDIRECT_REQUEST_FILTER_REGEX

        })
public class RedirectSlingRequestFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(RedirectSlingRequestFilter.class);

    @Reference
    private AdminService adminService;

    @Reference
    private EndecaConfig endecaConfigService;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private EndecaSecretKeyConfiguration endecaSecretKeyConfiguration;

    private Set<String> runModes;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.debug("RedirectSlingRequestFilter :: init() :: Start");

        LOG.debug("RedirectSlingRequestFilter :: init() :: End");
    }
    
    @Activate
    public void activate(final ComponentContext context){
        LOG.debug("RedirectSlingRequestFilter :: Activate() :: Start");

        runModes = slingSettingsService.getRunModes();

        if (! ((runModes.contains(RUNMODE_PUBLISH) && runModes.contains(RUNMODE_ENDECA)) || (runModes.contains(RUNMODE_AUTHOR)))) {
            LOG.info("RedirectSlingRequestFilter deactivating");
            if(context !=null){
                final String componentName = (String) context.getProperties().get(ComponentConstants.COMPONENT_NAME);
                context.disableComponent(componentName);
                LOG.debug("Disabling Redirect filter on this instance");
            }
        }

        LOG.debug("RedirectSlingRequestFilter :: Activate() :: End");
    }
    
    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        WCMMode mode = WCMMode.fromRequest(request);
        LOG.debug("RedirectSlingRequestFilter :: doFilter() :: Start");
        try (ResourceResolver resourceResolver = adminService.getReadService()) {
            final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
            final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
            final Resource resource = resourceResolver.getResource(slingRequest.getResource().getPath());

            if(resource != null && ! runModes.contains(RUNMODE_AUTHOR)
                    &&  resource.getPath().contains(SecureConstants.SECURE_FOLDER) &&
                    ! SecureUtil.isValidEndecaRequest(slingRequest,endecaSecretKeyConfiguration,endecaConfigService)) {
                LOG.debug("Access Denied for the resource -- > {} ", resource.getPath());
                ((SlingHttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            if (adminService != null && (mode == WCMMode.DISABLED || mode == WCMMode.PREVIEW)) {
                //To make sure this is a cq:page request
                if (null != resource && resource.isResourceType(CommonConstants.NODE_TYPE)) {
                    final StringBuilder queryStringBuilder = new StringBuilder();
                    final String resourcePath = queryStringBuilder.append(resource.getPath()).append(CommonConstants.SLASH_STRING).append(CommonConstants.JCR_CONTENT_STR).toString();
                    final Resource res = resourceResolver.resolve(resourcePath);
                    if (null != res) {
                        final ValueMap pageProperties = res.adaptTo(ValueMap.class);
                        //logic to check if the request is from endeca, then bypass the redirect starts
                        boolean requestFromEndeca = false;
                        String endecaUserAgent = StringUtils.EMPTY;
                        final String useragent = slingRequest.getHeader(CommonConstants.USER_AGENT);
                        LOG.debug("User Agent Value :: " + useragent);
                        final EndecaConfigServiceBean endecaConfigBean = endecaConfigService.getConfigServiceBean();

                        if (null != endecaConfigBean) {
                            endecaUserAgent = endecaConfigBean.getEndecaUserAgentValue();
                        }

                        LOG.debug("Configured User Agent Value :: " + endecaUserAgent);
                        if ((null != useragent) && (null != endecaUserAgent) && (useragent.contains(endecaUserAgent))) {
                            requestFromEndeca = true;
                        }

                        LOG.debug("Is request from Endeca  :: " + requestFromEndeca);
                        //logic to check if the request is from endeca, then bypass the redirect ends

                        if (!requestFromEndeca && (null != pageProperties) && (pageProperties.containsKey(CommonConstants.CQ_REDIRECT_URL) || pageProperties.containsKey(CommonConstants.REDIRECT_URL))) {
                            final String redirectProp = pageProperties.get(CommonConstants.CQ_REDIRECT_URL,
                                    pageProperties.get(CommonConstants.REDIRECT_URL, "")).trim();
                            if (!StringUtils.isEmpty(redirectProp)) {
                                String redirectPath;
                                redirectPath = CommonUtil.dotHtmlLink(CommonUtil.dotHtmlLink(redirectProp), resourceResolver);
                                slingResponse.setStatus(SlingHttpServletResponse.SC_MOVED_PERMANENTLY);
                                slingResponse.setHeader(CommonConstants.LOCATION, redirectPath);
                                slingResponse.setHeader(CommonConstants.CONNECTION, CommonConstants.CLOSETEXT);
                                slingResponse.sendRedirect(redirectPath);
                                return;
                            }
                        }

                    }
                }
            }
            LOG.debug("RedirectSlingRequestFilter :: doFilter() :: End");
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
        LOG.debug("RedirectSlingRequestFilter: Exit from destroy() method");
    }
}