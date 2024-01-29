package com.eaton.platform.integration.auth.filters;

import com.eaton.platform.TestConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.*;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import io.wcm.testing.mock.aem.junit.AemContext;
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class NoCacheFilterTest {

    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    private Cookie loginCookie;

    static final Logger LOG = LoggerFactory.getLogger(NoCacheFilterTest.class);

    @Mock
    FilterChain filterChain;

    @Mock
    AuthenticationService authenticationService;

    @Mock
    AuthorizationService authorizationService;

    @Mock
    AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @InjectMocks
    NoCacheFilter noCacheFilter;

    @Mock
    AuthCookieUtil authCookieUtil;

    @Mock
    AuthenticationToken authenticationToken;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        loginCookie = new Cookie(TestConstants.SECURE_LOGIN_ID, TestConstants.SECURE_LOGIN_TOKEN);
        context.request().addCookie(loginCookie);
        Mockito.when(authenticationServiceConfiguration.getSecurityCookieId()).thenReturn(TestConstants.SECURE_LOGIN_ID);
        Mockito.when(authenticationService.parseToken(TestConstants.SECURE_LOGIN_TOKEN)).thenReturn(authenticationToken);
    }

    @Test
    public void testDoFilter() throws IOException, ServletException {
        noCacheFilter.doFilter(context.request(), context.response(), filterChain);
        assertEquals(200, context.response().getStatus());
    }

}
