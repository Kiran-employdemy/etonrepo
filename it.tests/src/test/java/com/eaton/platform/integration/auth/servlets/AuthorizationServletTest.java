package com.eaton.platform.integration.auth.servlets;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.TestConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.models.impl.UserProfileImpl;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;

import io.wcm.testing.mock.aem.junit.AemContext;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class AuthorizationServletTest {

    @Rule
    public final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    private Cookie loginCookie;

	private Cookie ssoCookie;
	
    static final Logger LOG = LoggerFactory.getLogger(AuthorizationServletTest.class);

    @Mock
    AuthenticationService authenticationService;

    @Mock
    AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @InjectMocks
    AuthorizationServlet authorizationServlet;

    @Mock
    AuthenticationToken authenticationToken;

	@Mock
	UserProfile userProfile;
	
    private URI uri;

    private URI homePageUri;

    private URI referredPageUri;

	@Before
	public void setUp() throws Exception {
		uri = new URI(TestConstants.SECURE_URI1);
		homePageUri = new URI(TestConstants.SECURE_URI2);
		referredPageUri = new URI(TestConstants.SECURE_URI_PAGE);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("code", "test");
		aemContext.request().setParameterMap(parameters);
		MockitoAnnotations.initMocks(this);
		userProfile = new UserProfileImpl();
		userProfile.setEatonPersonType("employee");
		userProfile.setGivenName("ranjit");
		userProfile.setLastName("nethi");
		userProfile.setEmail("ranjitnethi@eaton.com");
		loginCookie = new Cookie(TestConstants.SECURE_LOGIN_ID, TestConstants.SECURE_LOGIN_TOKEN);
		aemContext.request().addCookie(loginCookie);
		ssoCookie = new Cookie(TestConstants.SECURE_SSOCOOKIE_ID, TestConstants.SECURE_SSOCOOKIE_TOKEN);
		aemContext.request().addCookie(ssoCookie);
		Mockito.when(authenticationServiceConfiguration.getOktaLoginURI()).thenReturn(uri.getPath());
		Mockito.when(authenticationServiceConfiguration.getDefaultPostAuthRedirectURI())
				.thenReturn(homePageUri.getPath());
		Mockito.when(authenticationToken.getUserProfile()).thenReturn(userProfile);
		Mockito.when(authenticationServiceConfiguration.getSecurityCookieId())
				.thenReturn(TestConstants.SECURE_LOGIN_ID);
		Mockito.when(authenticationServiceConfiguration.getSecurityCookieDomain()).thenReturn(homePageUri.getHost());
		Mockito.when(authenticationService.requestToken("code")).thenReturn(loginCookie.getValue());
		Mockito.when(authenticationToken.getUserLDAPId()).thenReturn("C3052834");
		Mockito.when(authenticationService.parseToken(loginCookie.getValue())).thenReturn(authenticationToken);
	}


    @Test
    public void testAuthorizationDoGetLoginPageRedirect() throws IOException, ServletException {
        authorizationServlet.doGet(aemContext.request(), aemContext.response());
        assertTrue(uri.getPath().contains(aemContext.response().getHeader("Location")));
    }

	/*
	 * @Test public void testAuthorizationDoGetHomePageRedirect() throws
	 * IOException, ServletException {
	 * Mockito.when(authenticationService.requestToken("test")).thenReturn(
	 * loginCookie.getValue()); Cookie redirectCookie = new
	 * Cookie(CommonConstants.LOGIN_PAGE_REDIRECT_COOKIE_ID, "");
	 * aemContext.request().addCookie(redirectCookie);
	 * authorizationServlet.doGet(aemContext.request(), aemContext.response());
	 * assertTrue(homePageUri.getPath().contains(aemContext.response().getHeader(
	 * "Location"))); }
	 * 
	 * @Test public void testAuthorizationDoGetReferredPageRedirect() throws
	 * IOException, ServletException {
	 * Mockito.when(authenticationService.requestToken("test")).thenReturn(
	 * loginCookie.getValue()); Cookie redirectCookie = new
	 * Cookie(CommonConstants.LOGIN_PAGE_REDIRECT_COOKIE_ID,
	 * TestConstants.SECURE_URI_PAGE);
	 * aemContext.request().addCookie(redirectCookie);
	 * authorizationServlet.doGet(aemContext.request(), aemContext.response());
	 * assertTrue(referredPageUri.getPath().contains(aemContext.response().getHeader
	 * ("Location"))); }
	 */

}