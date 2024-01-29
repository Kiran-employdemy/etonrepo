package com.eaton.platform.core.servlets.secure;

import com.eaton.platform.TestConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.secure.SecureAttributesModel;
import com.eaton.platform.core.services.AdminService;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlethelpers.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.MockSling;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
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

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*","javax.security.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class SecureAttributesServletTest {

    @Rule
    public final AemContext aemContext = new AemContext();

    @Mock
    AdminService adminService;

    @Mock
    ResourceResolver resourceResolver;

    @InjectMocks
    SecureAttributesServlet secureAttributesServlet;

    private MockSlingHttpServletRequest request;

    private MockSlingHttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        URI uri = new URI(TestConstants.SECURE_ATTRIB_PATH);
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("configuredUrl", uri.getPath());
        aemContext.load().json(TestConstants.SECURE_ATTRIB_ASSET, TestConstants.SECURE_ATTRIB_PATH);
        resourceResolver = aemContext.resourceResolver().resolve(TestConstants.SECURE_ATTRIB_PATH).getResourceResolver();
        Mockito.when(adminService.getReadService()).thenReturn(resourceResolver);
        ResourceResolver resourceResolver = MockSling.newResourceResolver(aemContext.bundleContext());
        request = new MockSlingHttpServletRequest(resourceResolver, aemContext.bundleContext());
        response = new MockSlingHttpServletResponse();
        request.setParameterMap(parameter);
        request.setPathInfo(TestConstants.SECURE_ATTRIB_PATH);
        request.setResource(resourceResolver.getResource(TestConstants.SECURE_ATTRIB_PATH+"/jcr:content"));
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo)request.getRequestPathInfo();
        requestPathInfo.setSelectorString("asset");
        aemContext.addModelsForClasses(SecureAttributesModel.class);
    }

    @Test
    public void testSecureAttributesServlet() throws IOException, ServletException {
        secureAttributesServlet.doGet(request, response);
        assertEquals(CommonConstants.APPLICATION_JSON, response.getContentType());
        assertEquals(200, response.getStatus());
    }
}