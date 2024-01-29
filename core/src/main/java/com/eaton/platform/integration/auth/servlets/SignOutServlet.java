package com.eaton.platform.integration.auth.servlets;

import com.adobe.acs.commons.util.CookieUtil;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.EatonVirtualAssistantConfiguration;
import com.eaton.platform.integration.auth.services.UserProfileService;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

import static com.eaton.platform.integration.auth.constants.AuthConstants.ETN_CART_COUNT_COOKIE;

@Component(
        immediate = true,
        service = {Servlet.class},
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton SignOut Servlet",
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/signout",
                ServletConstants.SLING_SERVLET_METHODS_GET
        }
)
public class SignOutServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SignOutServlet.class);
    private static final String REDIRECT_PARAM = "redirect";
    private static final long serialVersionUID = 142621045900875629L;

    @Reference
    private UserProfileService userProfileService;

    @Reference
    private AuthenticationService authenticationService;

    @Reference
    private AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Reference
    private transient EatonVirtualAssistantConfiguration eatonVirtualAssistantConfiguration;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        final String redirect = request.getParameter(REDIRECT_PARAM);
        final String redirectUrl = redirect != null ? redirect :
                authenticationServiceConfiguration.getDefaultPostSignOutRedirectURL();

        LOG.debug("Request to Sign Out Servlet initiated.");

        String rawJWT = AuthCookieUtil.getJWTFromAuthCookie(request, authenticationServiceConfiguration);

        if(rawJWT != null && StringUtils.isNotEmpty(rawJWT)) {
            AuthenticationToken token = authenticationService.parseToken(rawJWT);
            if(token != null){
                LOG.debug(String.format("Signing out user: %s",token.getUserLDAPId()));

                AuthCookieUtil.deleteAuthCookie(request,response,authenticationServiceConfiguration);

                AuthCookieUtil.deleteSsoUtilsCookie(request,response,authenticationServiceConfiguration);

                AuthCookieUtil.deletePublishServerNameCookie(request,response);

                AuthCookieUtil.deleteCookie(request,
                        response,
                        eatonVirtualAssistantConfiguration.getUserIdCookieName(),
                        eatonVirtualAssistantConfiguration.getUserIdCookieDomain());

                AuthCookieUtil.deleteCookie(request,
                        response,
                        eatonVirtualAssistantConfiguration.getVistaIdCookieName(),
                        eatonVirtualAssistantConfiguration.getVistaIdCookieDomain());

                AuthCookieUtil.deleteCookie(request,
                        response,
                        eatonVirtualAssistantConfiguration.getHistoryCookieName(),
                        eatonVirtualAssistantConfiguration.getHistoryCookieDomain());

                AuthCookieUtil.deleteCookie(request,
                        response,
                        eatonVirtualAssistantConfiguration.getIvSecurityCookieName(),
                        eatonVirtualAssistantConfiguration.getIvSecurityCookieDomain());

                AuthCookieUtil.deleteCookie(request,
                        response,
                        ETN_CART_COUNT_COOKIE,
                        retrieveHostFromHeader(request));

                CookieUtil.dropCookies(request,response, CommonConstants.SLASH_STRING,
                        CommonConstants.LOGIN_PAGE_REDIRECT_COOKIE_ID);

                userProfileService.removeUserCacheEntry(token.getUserLDAPId(),token.getExpiration());

                LOG.debug(String.format("Successfully signed out user: %s Redirecting user to %s",
                        token.getUserLDAPId(),
                        redirectUrl));

                response.sendRedirect(redirectUrl);
                return;
            }
        }

        LOG.info(String.format("Sign out request made for user not signed in.  Redirecting user to: %s",
                redirectUrl));

        response.sendRedirect(redirectUrl);
    }

    private String retrieveHostFromHeader(SlingHttpServletRequest request){
        String domain = StringUtils.EMPTY;
        if(null != request){
            domain = request.getHeader(HttpHeaders.HOST);
        }
        // need to sanitize before logging into log
        LOG.debug("Host value from request header: {}",StringUtils.replaceAll(domain,"[\r\n]",""));
        return domain;
    }
}
