package com.eaton.platform.integration.auth.filters;

import com.adobe.acs.commons.util.CookieUtil;
import com.eaton.platform.core.services.TermsAndConditionsService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.CookieFactory;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.engine.EngineConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static com.eaton.platform.core.constants.CommonConstants.*;

@Component(
        immediate = true,
        service = Filter.class,
        property = {
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                EngineConstants.SLING_FILTER_METHODS + "=GET",
                EngineConstants.SLING_FILTER_PATTERN + "=" + SecureConstants.GENERIC_CONTENT_SECURE_REGEX,
                Constants.SERVICE_RANKING + "=" + Integer.MAX_VALUE
        }
)
public class AuthorizationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Reference
    protected AuthenticationService authenticationService;

    @Reference
    protected AuthorizationService authorizationService;

    @Reference
    protected AuthenticationServiceConfiguration authenticationServiceConfig;

    @Reference
    protected SlingSettingsService slingSettingsService;

    @Reference
    protected TermsAndConditionsService termsAndConditionsService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Empty Body
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        LOGGER.debug("******** In doFilter() method of AuthorizationFilter *******");
        AuthorizationFilterActions filterActions = new AuthorizationFilterActions().request(request).response(response).filterChain(chain);
        String rawJWT = filterActions.extractRawJWTFromRequest(authenticationServiceConfig.getSecurityCookieId());
        LOGGER.debug("******** AuthorizationFilter : RAW JWT Token  ******* {}", rawJWT);
        String pathOfResource = filterActions.getPathOfResource();
        LOGGER.debug("******** AuthorizationFilter : Resource Path  ******* {}", pathOfResource);
        if (StringUtils.isNotEmpty(rawJWT)) {
            final AuthenticationToken authenticationToken = authenticationService.parseToken(rawJWT);
            filterActions.authenticationToken(authenticationToken);
            // here we need to check if current user need to be redirected to t&c.
            // guard against anyone from reaching secure page if he/she hasn't accepted terms and conditions
            if (filterActions.isPageOrAsset()) {
                checkToRedirectToTermsAndConditions(filterActions);
            }
            if (authenticationToken != null) {
                if ( ! filterActions.isResourceExisting() ){
                    filterActions.sendNotFound();
                }
                checkIfAuthorized(filterActions);
            } else {
                LOGGER.debug("******** Cookie expired  : Redirecting to Login Page  ******* {}", pathOfResource);
                redirectToLoginPage(filterActions);
            }
        } else {
            LOGGER.debug("******** Not loggedIn  : Redirecting to Login Page  ******* {}", pathOfResource);
            redirectToLoginPage(filterActions);
        }
    }

    private void checkIfAuthorized(AuthorizationFilterActions authorizationFilterActions) throws IOException {
        boolean grantAccess;
        AuthenticationToken authenticationToken = authorizationFilterActions.getAuthenticationToken();
        SlingHttpServletRequest request = authorizationFilterActions.getSlingHttpServletRequest();
        HttpServletResponse response = authorizationFilterActions.getSlingHttpServletResponse();
        LOGGER.debug("******** AuthorizationFilter : LDAP ID  ******* {}", authenticationToken.getUserLDAPId());
        grantAccess = authorizationService.isAuthorized(authenticationToken, authorizationFilterActions.getResource());
        if (grantAccess) {
            authorizationService.setTokenOnSlingRequest(request, authenticationToken);
            // Sets Profile Data in Request Attribute as XML
            authorizationService.setProfileJSONOnSlingRequest(request, authenticationToken);
            authorizationFilterActions.continueChain();
        } else {
            //Access denied, return 403
            LOGGER.debug("******** Access Denied : Redirecting to 403  ******* {}", authorizationFilterActions.getPathOfResource());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void checkToRedirectToTermsAndConditions(AuthorizationFilterActions filterActions) throws IOException {
        if (null != termsAndConditionsService && termsAndConditionsService.shouldRedirect(filterActions.getAuthenticationToken())) {
            String tcPath = termsAndConditionsService.getTermAndConditionPath(filterActions.getSlingHttpServletRequest());

            LOGGER.debug("********* tc path to redirect: {} **********", tcPath);
            String pathOfResource = filterActions.getPathOfResource();
            LOGGER.debug("********* Servlet resource path: {} ************", pathOfResource);
            // we need to make sure we are not re-looping if it is already in tc page
            // meaning user need to be redirected
            if (!tcPath.equals(FilenameUtils.removeExtension(pathOfResource))) {
                String redirectPath = CommonUtil.dotHtmlLink(CommonUtil.dotHtmlLink(tcPath), filterActions.getResourceResolver());
                filterActions.sendRedirect(redirectPath);
            }
        }
    }

    @Override
    public void destroy() {
        // Empty Body
    }

    @Activate
    @Modified
    public void activate(final ComponentContext context) {
        LOGGER.debug("AuthorizationFilter :: activate() :: Start, set");
        Set<String> runModes = slingSettingsService.getRunModes();
        if (!runModes.contains(RUNMODE_PUBLISH) || runModes.contains(EndecaConstants.RUNMODE_ENDECA)) {
            LOGGER.info("AuthorizationFilter deactivating as runmode doesnt include publish");
            if (context != null) {
                final String componentName = (String) context.getProperties()
                        .get(ComponentConstants.COMPONENT_NAME);
                context.disableComponent(componentName);
            }
        }
        LOGGER.debug("NoCacheFilter :: activate() :: End");
    }

    /**
     * Method- Sets login-page-redirect cookie and redirect the user to login Page
     *
     * @param filterActions the accessed resource
     */
    private void redirectToLoginPage(AuthorizationFilterActions filterActions) throws IOException {
        String pathOfResource = filterActions.getPathOfResource();
        if (filterActions.isPageOrAsset()) {
            CookieUtil.addCookie(new CookieFactory().loginRedirectCookie(filterActions.getSlingHttpServletRequest(), pathOfResource), filterActions.getSlingHttpServletResponse());
        } else {
            LOGGER.debug("******** Resource is not of Page Type or Asset (Ignored)  ******* {}", pathOfResource);
        }
        String loginPageURL = new LoginUrlFactory(authenticationServiceConfig).generateRedirectToLoginPage(filterActions);
        if (loginPageURL.isEmpty()) {
            filterActions.sendNotFound();
        } else {
            filterActions.sendRedirect(loginPageURL);
        }
    }


}
