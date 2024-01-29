package com.eaton.platform.integration.auth.servlets;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.TestConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.mime.MimeTypeService;
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

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class AuthCheckerServletTest {

    @Rule
    public final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    private Cookie loginCookie;

    static final Logger LOG = LoggerFactory.getLogger(AuthCheckerServletTest.class);

    @Mock
    AuthenticationService authenticationService;

    @Mock
    AuthorizationService authorizationService;

    @Mock
    AuthenticationServiceConfiguration authenticationServiceConfig;

    @InjectMocks
    AuthCheckerServlet authCheckerServlet;

    @Mock
    AuthenticationToken authenticationToken;

    @Mock
    MimeTypeService mimeTypeService;

    @Mock
    Resource resource;

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        aemContext.load().json(TestConstants.SECURE_PAGE_MOCK, TestConstants.EATON_SECURE_CONTENT_PATH);
        resource = aemContext.request().getResourceResolver().getResource(TestConstants.EATON_SECURE_CONTENT_PATH+"/en-us/securePage/jcr:content");
        aemContext.request().setResource(resource);
        Page page = aemContext.create().page(TestConstants.EATON_SECURE_CONTENT_PATH+"/en-us/securePage");
        aemContext.currentPage(page);
        URI uri = new URI(TestConstants.SECURE_URI_PAGE);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("uri", uri.getPath());
        loginCookie = new Cookie(TestConstants.SECURE_LOGIN_ID, TestConstants.SECURE_LOGIN_TOKEN);
        aemContext.request().setParameterMap(parameters);
        aemContext.request().addCookie(loginCookie);
        Mockito.when(authenticationServiceConfig.getSecurityCookieId()).thenReturn(TestConstants.SECURE_LOGIN_ID);
        Mockito.when(authenticationService.parseToken(loginCookie.getValue())).thenReturn(authenticationToken);
        Mockito.when(authorizationService.isAuthorized(authenticationToken, resource)).thenReturn(true);
        Mockito.when(mimeTypeService.getMimeType(CommonConstants.FILE_TYPE_HTML)).thenReturn(CommonConstants.FILE_TYPE_TEXT_HTML);
    }

    @Test
    public void testDoGet() throws IOException {
        authCheckerServlet.doGet(aemContext.request(), aemContext.response());
        assertEquals(403, aemContext.response().getStatus());
    }
}