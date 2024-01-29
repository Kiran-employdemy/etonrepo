package com.eaton.platform.core.services.impl;

import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.TermsAndConditionsService;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class TermsAndConditionsServiceImplTest {

    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    AdminService mockAdminService;

    @Mock
    AuthenticationToken mockToken;

    @Mock
    UserProfile mockUserProfile;

    @Mock
    ResourceResolver mockResourceResolver;

    @Mock
    PageManager mockPageManager;

    TermsAndConditionsService fixture;

    final String defaultPath = "/content/eaton/us/en-us/default/term-and-condition";
    final String termsAndconditionsPath = "/content/eaton/us/en-us/locate/terms-and-condition";
    final String resourcePath = defaultPath+"/content/eaton/us/en-us/locate/term-and-condition/jcr:content/root/responsivegrid/term_condition";

    @Before
    public void setup() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("defaultPath",defaultPath);
        properties.put("languagePaths",new String[]{"en-US|"+termsAndconditionsPath});
        properties.put("enable",true);
        context.registerService(AdminService.class,mockAdminService);
        fixture = context.registerInjectActivateService(new TermsAndConditionsServiceImpl(), properties);
        context.create().page(defaultPath);
        Resource resource = context.create().resource(resourcePath);
    }

    @Test
    public void testServiceIsActivated() {
        assertNotNull(fixture);
        assertEquals(fixture.getDefaultPath(),defaultPath);
    }

    @Test
    public void testEnsureGetTermsAndConditionsHasPath() {
        assertNotNull(fixture);
        when(mockAdminService.getReadService()).thenReturn(mockResourceResolver);
        when(mockResourceResolver.adaptTo(PageManager.class)).thenReturn(mockPageManager);
        when(mockPageManager.getContainingPage(Mockito.any(Resource.class))).thenReturn(context.currentPage(defaultPath));
        when(mockToken.getUserProfile()).thenReturn(mockUserProfile);
        when(mockUserProfile.getEatonEshopEulaAcceptDate()).thenReturn(null);
        when(mockUserProfile.getEatonEulaAcceptDate()).thenReturn(null);
        String tcPath = fixture.getTermAndConditionPath(context.currentResource(resourcePath));
        assertNotNull(tcPath);
        assertEquals(tcPath,termsAndconditionsPath);
    }

    @Test
    public void testEnsureCheckRedirectShouldBeTrue() {
        assertNotNull(fixture);
        when(mockAdminService.getReadService()).thenReturn(mockResourceResolver);
        when(mockResourceResolver.adaptTo(PageManager.class)).thenReturn(mockPageManager);
        when(mockPageManager.getContainingPage(Mockito.any(Resource.class))).thenReturn(context.currentPage(defaultPath));
        when(mockToken.getUserProfile()).thenReturn(mockUserProfile);
        when(mockUserProfile.getEatonEshopEulaAcceptDate()).thenReturn(null);
        when(mockUserProfile.getEatonEulaAcceptDate()).thenReturn(null);
        boolean shouldRedirect = fixture.shouldRedirect(mockToken);
        assertTrue(shouldRedirect);
    }
}
