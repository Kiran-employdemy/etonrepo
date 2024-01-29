package com.eaton.platform.integration.auth.services.impl;

import com.eaton.platform.core.models.secure.SecureAttributesModelTest;
import com.eaton.platform.core.services.CountryLangCodeConfigService;
import com.eaton.platform.core.services.secure.impl.UserProfileFixtures;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Objects;

import static com.eaton.platform.core.models.secure.SecureAttributesModelTest.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AuthorizationServiceImplTest {

    @InjectMocks
    AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl();
    @Mock
    CountryLangCodeConfigService countryLangCodeConfigService;
    @Mock
    SlingSettingsService slingSettingsService;
    @Mock
    AuthenticationToken authenticationToken;

    Resource securedPage;
    Resource securedAsset;
    Resource nonSecuredPage;

    Resource emeaWholesalerPage;
    Resource testProgram;
    private AemContext aemContext;
    static final String REVENERA_PRODUCT_MANAGER_TAG = "eaton-secure:okta-group/revenera_pq_product-manager";
    static final String REVENERA_SYSTEM_ADMIN_TAG ="eaton-secure:okta-group/revenera_pq_system-administrator";

    @BeforeEach
    void setUp(AemContext aemContext) {
        aemContext.load().json(Objects.requireNonNull(SecureAttributesModelTest.class.getResourceAsStream("secure-program.json")), SECURE_PROGRAM_PATH);
        aemContext.load().json(Objects.requireNonNull(SecureAttributesModelTest.class.getResourceAsStream("secure-asset.json")), SECURE_ASSET_PATH);
        aemContext.load().json(Objects.requireNonNull(SecureAttributesModelTest.class.getResourceAsStream("not-secure-page.json")), HYDRAULIC_CYLINDER_REPAIR);
        securedPage = aemContext.resourceResolver().getResource(POWER_OF_AUTHENTICITY_PAGE_PATH);
        securedAsset = aemContext.resourceResolver().getResource(SECURE_ASSET_PATH);
        nonSecuredPage = aemContext.resourceResolver().getResource(HYDRAULIC_CYLINDER_REPAIR);
        emeaWholesalerPage = aemContext.resourceResolver().getResource(EMEA_WHOLESALER_PAGE_PATH);
        testProgram = aemContext.resourceResolver().getResource(TEST_PROGRAM_PAGE_PATH);
        aemContext.create().tag(REVENERA_PRODUCT_MANAGER_TAG);
        aemContext.create().tag(REVENERA_SYSTEM_ADMIN_TAG);
    }

    @Test
    @DisplayName("If the runmode is Author every authorization call yields true")
    void testActivateAuthorRunmode() {
        when(slingSettingsService.getRunModes()).thenReturn(Sets.newSet("author"));
        authorizationService.activate();
        assertTrue(authorizationService.isAuthorized(authenticationToken, securedPage), "Should be authorized");
    }

    @Test
    @DisplayName("If the Runmode is endeca every authorization call yields true")
    void testActivateEndecaRunmode() {
        when(slingSettingsService.getRunModes()).thenReturn(Sets.newSet("author"));
        authorizationService.activate();
        assertTrue(authorizationService.isAuthorized(authenticationToken, securedPage), "Should be authorized");
    }

    @Test
    @DisplayName("When all mapped tags from the user profile match the tags from the secured page isAuthorized yields true")
    void testIsAuthorizedTrue1() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(UserProfileFixtures.pptestAtCompanyWithMappedTags());
        assertTrue(authorizationService.isAuthorized(authenticationToken, securedPage), "Should be authorized");
    }

    @Test
    @DisplayName("When Okta group matches the secure Okta group tag then isAuthorized should yields true")
    void testIsAuthorizedYieldsTrueWhenOktaGroupMatches() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(UserProfileFixtures.oktaGroupWithMappedTags());
        assertTrue(authorizationService.isAuthorized(authenticationToken,testProgram),"Should be authorized");
    }

    @Test
    @DisplayName("When Okta Group does not matches the secure Okta group tag isAuthorized should yields false")
    void testIsAuthorizedYieldsFalseWhenOktaGroupDoesNotMatches() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(UserProfileFixtures.oktaGroupWithMappedTags());
        assertFalse(authorizationService.isAuthorized(authenticationToken,emeaWholesalerPage),"Okta group not match should not be authorized");
    }

    @Test
    @DisplayName("When page is not secured isAuthorized yields true")
    void testIsAuthorizedTrue2() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(UserProfileFixtures.pptestAtCompanyWithMappedTags());
        assertTrue(authorizationService.isAuthorized(authenticationToken, nonSecuredPage), "Should be authorized");
    }

    @Test
    @DisplayName("When some or no tags from the user profile match the tags from the secured page isAuthorized yields false")
    void testIsAuthorizedFalse() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(UserProfileFixtures.pptestAtCompanyWithMappedTags());
        assertFalse(authorizationService.isAuthorized(authenticationToken, securedAsset), "Should not be authorized");
    }

    @Test
    void testIsAuthorizedCountryForbidden1() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(UserProfileFixtures.pptestAtCompanyWithMappedTags());
        when(countryLangCodeConfigService.getExcludeCountryList()).thenReturn(new String[]{"GB"});
        assertFalse(authorizationService.isAuthorized(authenticationToken, securedPage), "Should not be authorized");
    }

    @Test
    void testIsAuthorizedCountryForbidden2() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(UserProfileFixtures.pptestAtCompanyWithMappedTags());
        when(countryLangCodeConfigService.getExcludeCountryList()).thenReturn(new String[]{"gb"});
        assertFalse(authorizationService.isAuthorized(authenticationToken, securedPage), "Should not be authorized");
    }
}