package com.eaton.platform.integration.auth.filters;

import com.eaton.platform.core.services.TermsAndConditionsService;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Objects;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class AuthorizationFilterTest {

    public static final String SECURITY_COOKIE_ID = "etn_id";
    public static final String JWT_TOKEN = "jwt-token";
    public static final String FRENCH_SECURE_DASHBOARD_PATH = "/content/eaton/fr/fr-fr/secure/dashboard";
    public static final String ARAB_EMIRATES_SECURE_DASHBOARD_PATH = "/content/eaton/ae/en-gb/secure/dashboard";
    public static final String FRENCH_LANGUAGE_PATH = "/content/eaton/fr/fr-fr";
    public static final String ARAB_EMIRATES_LANGUAGE_PATH = "/content/eaton/ae/en-gb";
    public static final String SECURE_ASSET_PATH = "/content/dam/eaton/secure/secure-asset.zip";
    public static final String EXPECTED_COOKIE_NAME = "login-page-redirect";
    public static final String NOT_EXISTING_RESOURCE = "/content/eaton/not/existing-resource";
    public static final String PATH_TO_TC = "/content/path/to/tc";
    @InjectMocks
    AuthorizationFilter authorizationFilter;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    SlingHttpServletResponse response;
    @Mock
    Cookie cookie;
    @Mock
    AuthenticationServiceConfiguration authenticationServiceConfiguration;
    @Mock
    AuthenticationService authenticationService;
    @Mock
    AuthenticationToken authenticationToken;
    @Mock
    AuthorizationService authorizationService;
    @Mock
    TermsAndConditionsService termsAndConditionsService;
    @Mock
    FilterChain filterChain;
    @Mock
    ComponentContext componentContext;
    @Mock
    SlingSettingsService slingSettingsService;
    Resource frenchResource;
    Resource arabEmiratesResource;
    Resource assetResource;
    Resource notExisting;

    @BeforeEach
    void setUp(AemContext aemContext) {
        when(authenticationServiceConfiguration.getSecurityCookieId()).thenReturn(SECURITY_COOKIE_ID);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("fr-fr-language-page.json")), FRENCH_LANGUAGE_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("ae-en-gb-language-page.json")), ARAB_EMIRATES_LANGUAGE_PATH);
        assetResource = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("secure-asset.json")), SECURE_ASSET_PATH);
        frenchResource = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("fr-fr-dashboard.json")), FRENCH_SECURE_DASHBOARD_PATH);
        arabEmiratesResource = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("fr-fr-dashboard.json")), ARAB_EMIRATES_SECURE_DASHBOARD_PATH);
        notExisting = aemContext.resourceResolver().resolve(NOT_EXISTING_RESOURCE);
    }

    @Test
    @DisplayName("activate disables this filter for author")
    void testActivateAuthor() {
        reset(authenticationServiceConfiguration, request);
        when(slingSettingsService.getRunModes()).thenReturn(Sets.newSet("local", "author"));
        Dictionary<String, Object> dictionary = mock(Dictionary.class);
        when(componentContext.getProperties()).thenReturn(dictionary);
        when(dictionary.get(ComponentConstants.COMPONENT_NAME)).thenReturn("component");
        authorizationFilter.activate(componentContext);
        verify(componentContext).disableComponent("component");
    }
    @Test
    @DisplayName("activate disables this filter for endeca")
    void testActivateEndeca() {
        reset(authenticationServiceConfiguration, request);
        when(slingSettingsService.getRunModes()).thenReturn(Sets.newSet("prod", "endeca"));
        Dictionary<String, Object> dictionary = mock(Dictionary.class);
        when(componentContext.getProperties()).thenReturn(dictionary);
        when(dictionary.get(ComponentConstants.COMPONENT_NAME)).thenReturn("component");
        authorizationFilter.activate(componentContext);
        verify(componentContext).disableComponent("component");
    }
    @Test
    @DisplayName("activate does not disable this filter for publish")
    void testActivatePublish() {
        reset(authenticationServiceConfiguration, request);
        when(slingSettingsService.getRunModes()).thenReturn(Sets.newSet("prod", "publish"));
        authorizationFilter.activate(componentContext);
        verify(componentContext, times(0)).disableComponent("component");
    }

    @Test
    @DisplayName("doFilter if everything is ok, continues the filter chain")
    void testDoFilterRequestAuthenticatedAndAuthorized() throws ServletException, IOException {
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(cookie);
        when(request.getResource()).thenReturn(frenchResource);
        when(cookie.getValue()).thenReturn(JWT_TOKEN);
        when(authenticationService.parseToken(JWT_TOKEN)).thenReturn(authenticationToken);
        when(authorizationService.isAuthorized(authenticationToken, frenchResource)).thenReturn(true);

        authorizationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

    }

    @Test
    @DisplayName("doFilter authenticated resource not found sends a 404")
    void testDoFilterAuthenticatedResourceNotFond() throws ServletException, IOException {
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(cookie);
        when(cookie.getValue()).thenReturn(JWT_TOKEN);
        when(authenticationService.parseToken(JWT_TOKEN)).thenReturn(authenticationToken);
        when(request.getResource()).thenReturn(notExisting);

        authorizationFilter.doFilter(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);

    }

    @Test
    @DisplayName("doFilter authenticated but need to redirect to t & c")
    void testDoFilterAuthenticatedRedirectToTAndC() throws ServletException, IOException {
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(cookie);
        when(cookie.getValue()).thenReturn(JWT_TOKEN);
        when(authenticationService.parseToken(JWT_TOKEN)).thenReturn(authenticationToken);
        when(termsAndConditionsService.shouldRedirect(authenticationToken)).thenReturn(true);
        when(request.getResource()).thenReturn(frenchResource);
        when(termsAndConditionsService.getTermAndConditionPath(request)).thenReturn(PATH_TO_TC);

        authorizationFilter.doFilter(request, response, filterChain);

        verify(response).sendRedirect(PATH_TO_TC);

    }
    @Test
    @DisplayName("doFilter not authenticated resource not found sends a 404")
    void testDoFilterNotAuthenticatedResourceNotFond() throws ServletException, IOException {
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(null);
        when(request.getResource()).thenReturn(notExisting);

        authorizationFilter.doFilter(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);

    }

    @Test
    @DisplayName("doFilter adds eaton-login-redirect cookie to current resource path and redirects to localized url set in configuration when security id token cookie not set")
    void testDoFilterNoSecurityCookiePresent() throws IOException, ServletException {
        when(request.getResource()).thenReturn(frenchResource);
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://www.eaton.com/fr/fr-fr/secure/dashboard.html"));
        when(authenticationServiceConfiguration.getOktaLoginURI()).thenReturn("https://login.eaton.com/login.html");

        authorizationFilter.doFilter(request, response, filterChain);

        verify(response).addCookie(ArgumentMatchers.argThat(cookie -> {
            Assertions.assertEquals(EXPECTED_COOKIE_NAME, cookie.getName(), "Cookie name should be login-page-redirect");
            Assertions.assertEquals(FRENCH_SECURE_DASHBOARD_PATH, cookie.getValue(), "Cookie value should be path of requested resource");
            return EXPECTED_COOKIE_NAME.equals(cookie.getName())
                            && FRENCH_SECURE_DASHBOARD_PATH.equals(cookie.getValue());
                }
        ));
        verify(response).sendRedirect("https://login.eaton.com/fr/fr-fr/login.html");

    }
    @Test
    @DisplayName("doFilter adds eaton-login-redirect cookie to current resource path and redirects to localized url set in configuration when country different from locale")
    void testDoFilterNoSecurityCookiePresentCountryDifferentFromLocal() throws IOException, ServletException {
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(null);
        when(request.getResource()).thenReturn(arabEmiratesResource);
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://www.eaton.com/ae/en-gb/secure/dashboard.html"));
        when(authenticationServiceConfiguration.getOktaLoginURI()).thenReturn("https://login.eaton.com/login.html");

        authorizationFilter.doFilter(request, response, filterChain);

        verify(response).addCookie(ArgumentMatchers.argThat(cookie -> {
            Assertions.assertEquals(EXPECTED_COOKIE_NAME, cookie.getName(), "Cookie name should be login-page-redirect");
            Assertions.assertEquals(ARAB_EMIRATES_SECURE_DASHBOARD_PATH, cookie.getValue(), "Cookie value should be path of requested resource");
            return EXPECTED_COOKIE_NAME.equals(cookie.getName())
                            && ARAB_EMIRATES_SECURE_DASHBOARD_PATH.equals(cookie.getValue());
                }
        ));
        verify(response).sendRedirect("https://login.eaton.com/ae/en-gb/login.html");

    }
    @Test
    @DisplayName("doFilter adds eaton-login-redirect cookie to current resource path and redirects to localized url set in configuration when security id token cookie is not valid")
    void testDoFilterSecurityCookiePresentNoValid() throws IOException, ServletException {
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(cookie);
        when(request.getResource()).thenReturn(frenchResource);
        when(cookie.getValue()).thenReturn(JWT_TOKEN);
        when(authenticationService.parseToken(JWT_TOKEN)).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://www.eaton.com/fr/fr-fr/secure/dashboard.html"));
        when(authenticationServiceConfiguration.getOktaLoginURI()).thenReturn("https://www-qa-login.eaton.com/content/us/en-us/login.html");

        authorizationFilter.doFilter(request, response, filterChain);

        verify(response).addCookie(ArgumentMatchers.argThat(cookie -> {
            Assertions.assertEquals(EXPECTED_COOKIE_NAME, cookie.getName(), "Cookie name should be login-page-redirect");
            Assertions.assertEquals(FRENCH_SECURE_DASHBOARD_PATH, cookie.getValue(), "Cookie value should be path of requested resource");
            return EXPECTED_COOKIE_NAME.equals(cookie.getName())
                            && FRENCH_SECURE_DASHBOARD_PATH.equals(cookie.getValue());
                }
        ));
        verify(response).sendRedirect("https://www-qa-login.eaton.com/fr/fr-fr/login.html");

    }
    @Test
    @DisplayName("doFilter sends a 403 when security id token cookie is present ad valid, but not authorized")
    void testDoFilterSecurityCookiePresentNotAuthorized() throws IOException, ServletException {
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(cookie);
        when(request.getResource()).thenReturn(frenchResource);
        when(cookie.getValue()).thenReturn(JWT_TOKEN);
        when(authenticationService.parseToken(JWT_TOKEN)).thenReturn(authenticationToken);
        when(authorizationService.isAuthorized(authenticationToken, frenchResource)).thenReturn(false);

        authorizationFilter.doFilter(request, response, filterChain);

        verify(response).sendError(403);

    }
    @Test
    @DisplayName("doFilter, when asset, no etn_reidrect_cookie set, redirects to url inn configuration when security id token cookie not set")
    void testDoFilterNoSecurityCookiePresentAsset() throws IOException, ServletException {
        when(request.getResource()).thenReturn(assetResource);
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://www.eaton.com/content/dam/eaton/secure/secure-asset.zip"));
        when(authenticationServiceConfiguration.getOktaLoginURI()).thenReturn("https://login.eaton.com/us/en-us/login.html");

        authorizationFilter.doFilter(request, response, filterChain);

        verify(response).addCookie(ArgumentMatchers.argThat(cookie -> {
                    Assertions.assertEquals(EXPECTED_COOKIE_NAME, cookie.getName(), "Cookie name should be login-page-redirect");
                    Assertions.assertEquals(SECURE_ASSET_PATH, cookie.getValue(), "Cookie value should be path of requested resource");
                    return EXPECTED_COOKIE_NAME.equals(cookie.getName())
                            && SECURE_ASSET_PATH.equals(cookie.getValue());
                }
        ));
        verify(response).sendRedirect("https://login.eaton.com/us/en-us/login.html");

    }
    @Test
    @DisplayName("doFilter, when asset, etn_reidrect_cookie set, redirects to localized url in configuration when security id token cookie not set")
    void testDoFilterNoSecurityCookiePresentAssetAndRedirectCookie() throws IOException, ServletException {
        when(request.getResource()).thenReturn(assetResource);
        when(request.getCookie(SECURITY_COOKIE_ID)).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://www.eaton.com/content/dam/eaton/secure/secure-asset.zip"));
        when(authenticationServiceConfiguration.getOktaLoginURI()).thenReturn("https://login.eaton.com/us/en-us/login.html");
        when(request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE)).thenReturn(cookie);
        when(cookie.getValue()).thenReturn("%2Ffr%2Ffr-fr");

        authorizationFilter.doFilter(request, response, filterChain);

        verify(response).addCookie(ArgumentMatchers.argThat(cookie -> {
                    Assertions.assertEquals(EXPECTED_COOKIE_NAME, cookie.getName(), "Cookie name should be login-page-redirect");
                    Assertions.assertEquals(SECURE_ASSET_PATH, cookie.getValue(), "Cookie value should be path of requested resource");
                    return EXPECTED_COOKIE_NAME.equals(cookie.getName())
                            && SECURE_ASSET_PATH.equals(cookie.getValue());
                }
        ));
        verify(response).sendRedirect("https://login.eaton.com/fr/fr-fr/login.html");

    }
}