package com.eaton.platform.core.models.secure;


import com.eaton.platform.core.services.CountryLangCodeConfigService;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.models.impl.UserProfileImpl;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.services.impl.AuthorizationServiceImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextCallback;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Objects;

import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SecureXFModelTest {
    private final AemContext aemContext = new AemContextBuilder().afterSetUp(
            new AemContextCallback() {
                @Override
                public void execute(AemContext context) {
                    // custom project initialization code for every unit test
                    context.addModelsForClasses(SecureXFModel.class);
                }
            }
    ).build();
    @InjectMocks
    private AuthorizationService mockAuthorizationService = new AuthorizationServiceImpl();
    @Mock
    private CountryLangCodeConfigService countryLangCodeConfigService;
    @Mock
    private AuthenticationToken mockToken;
    private SlingHttpServletRequest slingHttpServletRequest;

    private static final String RESOURCE_PATH = "/content/eaton/us/us-en/company/jcr:content/securexf";

    @BeforeEach
    void setup(){
        Resource resource= aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("secure-xf-model.json")), RESOURCE_PATH);
        aemContext.request().setResource(resource);
        aemContext.registerService(AuthorizationService.class,mockAuthorizationService);
        aemContext.registerService(CountryLangCodeConfigService.class,countryLangCodeConfigService);
        slingHttpServletRequest = aemContext.request();
    }

    @Test
    @DisplayName("Test to ensure request is adaptable")
    void testSecureModelAdaptable(){
        aemContext.request().setAttribute("authenticationToken",mockToken);
        SecureXFModel model = slingHttpServletRequest.adaptTo(SecureXFModel.class);
        Assertions.assertNotNull(model);
        Assertions.assertFalse(model.isAuthorized());
        verify(mockToken,times(1)).getUserProfile();
    }

    @Test
    @DisplayName("Test ensure with correct account type should return is authorized ")
    void testEnsureWithCorrectAccountTypeShouldReturnIsAuthorized(){
        UserProfile userProfile = new UserProfileImpl();
        userProfile.setAccountTypeTags(Collections.singletonList("eaton-secure:accounttype/contractor"));
        when(mockToken.getUserProfile()).thenReturn(userProfile);
        when(mockToken.getUserProfile()).thenReturn(userProfile);
        aemContext.request().setAttribute("authenticationToken",mockToken);
        SecureXFModel model = slingHttpServletRequest.adaptTo(SecureXFModel.class);
        Assertions.assertNotNull(model);
        Assertions.assertTrue(model.isAuthorized());
        verify(mockToken,times(1)).getUserProfile();
    }

    @Test
    @DisplayName("Test ensure with correct account type should return not authorized")
    void testEnsureWithCorrectAccountTypeShouldReturnIsNotAuthorized(){
        String[] excludeCountries = {};
        when(countryLangCodeConfigService.getExcludeCountryList()).thenReturn(excludeCountries);
        UserProfile userProfile = new UserProfileImpl();
        userProfile.setAccountTypeTags(Collections.singletonList(""));
        when(mockToken.getUserProfile()).thenReturn(userProfile);
        when(mockToken.getUserProfile()).thenReturn(userProfile);
        aemContext.request().setAttribute("authenticationToken",mockToken);
        SecureXFModel model = slingHttpServletRequest.adaptTo(SecureXFModel.class);
        Assertions.assertNotNull(model);
        Assertions.assertFalse(model.isAuthorized());
        verify(mockToken,times(1)).getUserProfile();
    }
}
