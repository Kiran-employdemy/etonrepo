package com.eaton.platform.core.models.secure;

import com.eaton.platform.TestConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.impl.UserProfileImpl;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.services.impl.AuthorizationServiceImpl;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*","javax.security.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class SecureLinkModelTest {

    @Rule
    public final AemContext aemContext = new AemContext();

    @Mock
    Resource resource;

    @Mock
    ResourceResolver resourceResolver;

    @InjectMocks
    SecureLinkModel secureLinkModel;

    @Mock
    AuthorizationService authorizationService;

    @Mock
    AuthenticationToken authenticationToken;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    private Resource pageResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        aemContext.load().json(TestConstants.SECURE_LINK_PAGE_MOCK, TestConstants.SECURE_TEST_PAGE_PATH);
        FieldSetter.setField(secureLinkModel, secureLinkModel.getClass().getDeclaredField("link"), TestConstants.SECURE_TEST_PAGE_PATH);
        Mockito.when(resource.getResourceResolver()).thenReturn(resourceResolver);
        pageResource = aemContext.resourceResolver().getResource(TestConstants.SECURE_TEST_PAGE_PATH+"/jcr:content");
        Mockito.when(resource.getResourceResolver().resolve(TestConstants.SECURE_TEST_PAGE_PATH)).thenReturn(pageResource);
        Mockito.when(authorizationService.getTokenFromSlingRequest(slingHttpServletRequest)).thenReturn(authenticationToken);
        UserProfileImpl userProfileImpl = new UserProfileImpl();
        userProfileImpl.setAccountTypeTags(Collections.singletonList(TestConstants.ACCOUNTTYPE));
        userProfileImpl.setProductCategoriesTags(Collections.singletonList(TestConstants.PRODCAT));
        userProfileImpl.setAppAccessTags(Collections.singletonList(TestConstants.APPACC));
        userProfileImpl.setPartnerProgramTypeAndTierLevelTags(Collections.singletonList(TestConstants.PROGRAM_TYPE));
        Mockito.when(authenticationToken.getUserProfile()).thenReturn(userProfileImpl);
        aemContext.addModelsForClasses(SecureAttributesModel.class);
        Mockito.when(authorizationService.isAuthorized(authenticationToken, resource)).thenReturn(true);
    }

    @Test
    public void testIsAuthorisedLink(){
        AuthorizationServiceImpl authorizationServiceImpl = new AuthorizationServiceImpl();
        authorizationServiceImpl.setTokenOnSlingRequest(slingHttpServletRequest, authenticationToken);
        Mockito.when(authorizationService.isAuthorized(authenticationToken, pageResource)).thenReturn(true);
        secureLinkModel.isAuthorised();
        Assertions.assertTrue(secureLinkModel.isAuthorised(), "should be true");
    }
}