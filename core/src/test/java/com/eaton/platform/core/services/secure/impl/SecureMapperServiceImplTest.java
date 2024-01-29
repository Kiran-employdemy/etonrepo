package com.eaton.platform.core.services.secure.impl;

import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.auth.models.UserProfile;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SecureMapperServiceImplTest {

    public static final String ROLE_MAPPING_JSON_PATH = "/content/dam/eaton/resources/secure/role-mapping.json";
    @InjectMocks
    SecureMapperServiceImpl secureMapperService = new SecureMapperServiceImpl();
    @InjectMocks
    SecureMapperServiceImpl secureMapperServiceEmptyRoleMapping = new SecureMapperServiceImpl();
    @InjectMocks
    SecureMapperServiceImpl secureMapperServiceMallFormattedRoleMapping = new SecureMapperServiceImpl();
    @Mock
    AdminService adminService;

    AemContext aemContext = new AemContext();
    AemContext aemContextWithEmptyRoleMapping = new AemContext();
    AemContext aemContextWithMallFormattedRoleMapping = new AemContext();

    @BeforeEach
    void setUp() {
        aemContext.create().asset(ROLE_MAPPING_JSON_PATH
                , Objects.requireNonNull(this.getClass().getResourceAsStream("role-mapping.json"))
                , "application/json");
        aemContextWithEmptyRoleMapping.create().asset(ROLE_MAPPING_JSON_PATH
                , Objects.requireNonNull(this.getClass().getResourceAsStream("role-mapping-empty.json"))
                , "application/json");
        aemContextWithMallFormattedRoleMapping.create().asset(ROLE_MAPPING_JSON_PATH
                , Objects.requireNonNull(this.getClass().getResourceAsStream("role-mapping-mall-formatted.json"))
                , "application/json");
        loadCountryTags(aemContext);
        loadCountryTags(aemContextWithEmptyRoleMapping);
    }

    private void prepareActivateMethod(SecureMapperServiceImpl secureMapperService, ResourceResolver resourceResolver) {
        Mockito.when(adminService.getReadService()).thenReturn(resourceResolver);
        secureMapperService.activate();
    }

    private void loadCountryTags(AemContext aemContextToLoad) {
        aemContextToLoad.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("cq-tags.json"))
                , "/content/cq:tags");
        aemContextToLoad.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("eaton-root-tag.json"))
                , "/content/cq:tags/eaton-root-tag");
        aemContextToLoad.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("country-tags.json"))
                , "/content/cq:tags/eaton/country");
    }

    @Test
    @DisplayName("mapSecureTags yields correct mapped AccountType Tags")
    void testMapSecureTagsAccountTypeTags() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.pptestAtCompany());
        assertEquals(Arrays.asList("eaton-secure:accounttype/contractor","eaton-secure:accounttype/end-customer"), mappedUserProfile.getAccountTypeTags(), "should contain correct values");
    }

    @Test
    @DisplayName("mapSecureTags for other user yields correct mapped AccountType Tags")
    void testMapSecureTagsAccountTypeTagsOtherUser() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.testewpremukatcompanycom());
        assertEquals(Collections.emptyList(), mappedUserProfile.getAccountTypeTags(), "should be empty");
    }

    @Test
    @DisplayName("mapSecureTags yields correct mapped Product Category Tags")
    void testMapSecureTagsProductCategoryTags() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.pptestAtCompany());
        assertEquals(Arrays.asList("eaton-secure:product-category/aerospace/engine-build-up-solutions", "eaton-secure:product-category/aerospace/engine-oil-system"), mappedUserProfile.getProductCategoriesTags(), "should contain correct values");
    }

    @Test
    @DisplayName("mapSecureTags for other user yields correct mapped Product Category Tags")
    void testMapSecureTagsProductCategoryTagsOtherUser() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.testewpremukatcompanycom());
        assertEquals(Collections.emptyList(), mappedUserProfile.getProductCategoriesTags(), "should be empty");
    }

    @Test
    @DisplayName("mapSecureTags yields correct mapped Application Access Tags")
    void testMapSecureTagsApplicationTags() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.pptestAtCompany());
        assertEquals(Arrays.asList("eaton-secure:application-access/eaton-university", "eaton-secure:application-access/new-myeaton"), mappedUserProfile.getAppAccessTags(), "should contain correct values");
    }

    @Test
    @DisplayName("mapSecureTags with other user yields correct mapped Application Access Tags")
    void testMapSecureTagsApplicationTagsOtherUser() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.testewpremukatcompanycom());
        assertEquals(Arrays.asList("eaton-secure:application-access/eaton-university"), mappedUserProfile.getAppAccessTags(), "should contain correct values");
    }
    @Test
    @DisplayName("mapSecureTags with other user yields correct mapped Okta Group Tags")
    void testMapSecureTagsOktaTagsOtherUser() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.oktaGroupUser());
        assertEquals(Arrays.asList("eaton-secure:okta-group/revenera_pq_product-manager"), mappedUserProfile.getUserOktaGroups(), "should contain correct values");
    }


    @Test
    @DisplayName("mapSecureTags yields correct mapped Locale Tags")
    void testMapSecureTagsLocaleTags() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.pptestAtCompany());
        assertEquals(Arrays.asList("eaton:country/europe/gb", "eaton:country/europe"), mappedUserProfile.getLocaleTags(), "should contain correct values");
    }

    @Test
    @DisplayName("mapSecureTags with other user yields correct mapped Locale Tags")
    void testMapSecureTagsLocaleTagsOtherUser() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.testewpremukatcompanycom());
        assertEquals(Arrays.asList("eaton:country/europe/gb", "eaton:country/europe"), mappedUserProfile.getLocaleTags(), "should contain correct values");
    }

    @Test
    @DisplayName("mapSecureTags yields correct mapped ProgramType and Tier Tags")
    void testMapSecureTagsProgramTypeAndTierTags() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.pptestAtCompany());
        assertEquals(Arrays.asList("eaton-secure:partner-programme-type/electrical-installer-programme/premium", "eaton-secure:partner-programme-type/drives-solution-program"), mappedUserProfile.getPartnerProgramAndTierLevelTags(), "should contain correct values");
    }

    @Test
    @DisplayName("mapSecureTags with other user yields correct mapped ProgramType and Tier Tags")
    void testMapSecureTagsProgramTypeAndTierTagsOtherUser() throws IOException {
        prepareActivateMethod(secureMapperService, aemContext.resourceResolver());
        UserProfile mappedUserProfile = secureMapperService.mapSecureTags(UserProfileFixtures.testewpremukatcompanycom());
        assertEquals(Collections.emptyList(), mappedUserProfile.getPartnerProgramAndTierLevelTags(), "should contain correct values");
    }

    @Test
    @DisplayName("When role mapping json is empty, the accountType tags will be empty")
    void testMapSecureTagsEmptyJsonAccountTypeTags() throws IOException {
        prepareActivateMethod(secureMapperServiceEmptyRoleMapping, aemContextWithEmptyRoleMapping.resourceResolver());
        UserProfile mappedUserProfile = secureMapperServiceEmptyRoleMapping.mapSecureTags(UserProfileFixtures.pptestAtCompany());
        assertTrue(mappedUserProfile.getAccountTypeTags().isEmpty(), "should be empty");
    }

    @Test
    @DisplayName("When role mapping json is empty, the productCategory tags will be empty")
    void testMapSecureTagsEmptyJsonProductCategoryTags() throws IOException {
        prepareActivateMethod(secureMapperServiceEmptyRoleMapping, aemContextWithEmptyRoleMapping.resourceResolver());
        UserProfile mappedUserProfile = secureMapperServiceEmptyRoleMapping.mapSecureTags(UserProfileFixtures.pptestAtCompany());
        assertTrue(mappedUserProfile.getProductCategoriesTags().isEmpty(), "should be empty");
    }

    @Test
    @DisplayName("When role mapping json is empty, the programTypeAndTier tags will be empty")
    void testMapSecureTagsEmptyJsonProgramTypeAndTierTags() throws IOException {
        prepareActivateMethod(secureMapperServiceEmptyRoleMapping, aemContextWithEmptyRoleMapping.resourceResolver());
        UserProfile mappedUserProfile = secureMapperServiceEmptyRoleMapping.mapSecureTags(UserProfileFixtures.pptestAtCompany());
        assertTrue(mappedUserProfile.getPartnerProgramAndTierLevelTags().isEmpty(), "should be empty");
    }

    @Test
    @DisplayName("When role mapping json is mall-formatted, the programTypeAndTier tags will be empty")
    void testMapSecureTagsMallFormattedJson() throws IOException {
        prepareActivateMethod(secureMapperServiceMallFormattedRoleMapping, aemContextWithMallFormattedRoleMapping.resourceResolver());
        UserProfile mappedUserProfile = secureMapperServiceMallFormattedRoleMapping.mapSecureTags(UserProfileFixtures.pptestAtCompany());
        assertTrue(mappedUserProfile.getPartnerProgramAndTierLevelTags().isEmpty(), "should be empty");
    }
}