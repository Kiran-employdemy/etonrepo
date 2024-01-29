package com.eaton.platform.core.models.secure;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SecureAttributesModelTest {


    public static final String SECURE_PROGRAM_PATH = "/content/eaton/us/en-us/secure/program";
    public static final String EATON_DISTRIBUTOR_ADV_PROGR_PAGE_PATH = SECURE_PROGRAM_PATH + "/eaton-distributor-advantage-program--edap-";
    public static final String POWER_OF_AUTHENTICITY_PAGE_PATH = SECURE_PROGRAM_PATH + "/power-of-authenticity";

    public static final String EMEA_WHOLESALER_PAGE_PATH = SECURE_PROGRAM_PATH + "/emea-wholesaler";
    public static final String TEST_PROGRAM_PAGE_PATH = SECURE_PROGRAM_PATH + "/test_programs";
    public static final String SECURE_ASSET_PATH = "/content/dam/eaton/secure/communications/Foreseer Test File.pdf";
    public static final String HYDRAULIC_CYLINDER_REPAIR = "/content/eaton/us/en-us/services/hydraulic-cylinder-repair";
    ResourceResolver resourceResolver;

    SecureAttributesModel secureAttributesModelDistributorPage;
    SecureAttributesModel secureAttributesModelPowerPage;
    SecureAttributesModel secureAttributesModelAsset;
    SecureAttributesModel nonSecureAttributesModelPage;

    @BeforeEach
    void setUp(AemContext aemContext) {
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("secure-program.json")), SECURE_PROGRAM_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("not-secure-page.json")), HYDRAULIC_CYLINDER_REPAIR);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("secure-asset.json")), SECURE_ASSET_PATH);
        aemContext.addModelsForPackage("com.eaton.platform.core.models");
        resourceResolver = aemContext.resourceResolver();
        Resource resource = resourceResolver.getResource(EATON_DISTRIBUTOR_ADV_PROGR_PAGE_PATH + "/jcr:content");
        secureAttributesModelDistributorPage = Objects.requireNonNull(resource).adaptTo(SecureAttributesModel.class);
        resource = resourceResolver.getResource(POWER_OF_AUTHENTICITY_PAGE_PATH + "/jcr:content");
        secureAttributesModelPowerPage = Objects.requireNonNull(resource).adaptTo(SecureAttributesModel.class);
        resource = resourceResolver.getResource(HYDRAULIC_CYLINDER_REPAIR + "/jcr:content");
        nonSecureAttributesModelPage = Objects.requireNonNull(resource).adaptTo(SecureAttributesModel.class);
        resource = resourceResolver.getResource(SECURE_ASSET_PATH + "/jcr:content/metadata");
        secureAttributesModelAsset = Objects.requireNonNull(resource).adaptTo(SecureAttributesModel.class);
    }

    @Test
    @DisplayName("On a page the securePage property is set to true on a page, on an asset it is null")
    void testSecurePage() {
        assertEquals("true", secureAttributesModelDistributorPage.getSecurePage(), "should be true");
        assertNull(secureAttributesModelAsset.getSecurePage(), "should be null");
    }
    @Test
    @DisplayName("On an asset the secureAsset property is set to YES on an asset, on a page it is null")
    void testSecureAsset() {
        assertEquals("YES", secureAttributesModelAsset.getSecureAsset(), "should be YES");
        assertNull(secureAttributesModelDistributorPage.getSecureAsset(), "should be null");
    }
    @Test
    @DisplayName("Both on an asset and a page isPresent is true if one of the secure properties is not null")
    void testIsPresentTrue() {
        assertTrue(secureAttributesModelDistributorPage.isPresent(), "should be true");
        assertTrue(secureAttributesModelAsset.isPresent(), "should be true");
    }

    @Test
    @DisplayName("When none of the secure properties are filled in isPresent is false")
    void testIsPresentFalse() {
        Resource resource = resourceResolver.getResource(HYDRAULIC_CYLINDER_REPAIR);
        SecureAttributesModel secureAttributesModel = Objects.requireNonNull(resource).adaptTo(SecureAttributesModel.class);
        assertFalse(Objects.requireNonNull(secureAttributesModel).isPresent(), "should be false");
    }

    @Test
    @DisplayName("GetAccountType yields the array of tag paths set in accountType")
    void testGetAccountType() {
        assertArrayEquals(new String[]{"eaton-secure:accounttype/distributor","eaton-secure:accounttype/eaton-employee"}, secureAttributesModelDistributorPage.getAccountType(), "should contain correct tags");
        assertArrayEquals(new String[]{"eaton-secure:accounttype/consultant","eaton-secure:accounttype/contractor"}, secureAttributesModelAsset.getAccountType(), "should contain correct tags");
    }

    @Test
    @DisplayName("GetApplicationAccess yields the array of tag paths set in applicationAccess")
    void testGetApplicationAccess() {
        assertArrayEquals(new String[]{"eaton-secure:application-access/electrical-installer-premium",
                "eaton-secure:application-access/electrical-installer-registered",
                "eaton-secure:application-access/eas-demand-response"}, secureAttributesModelDistributorPage.getApplicationAccess(), "should contain correct tags");
        assertArrayEquals(new String[]{"eaton-secure:application-access/foreseer"}, secureAttributesModelAsset.getApplicationAccess(), "should contain correct tags");
    }

    @Test
    @DisplayName("GetCountries yields the array of tag paths set in country")
    void testGetCountries() {
        assertArrayEquals(new String[]{"eaton:country/north-america/us",
                "eaton:country/north-america/ca"}, secureAttributesModelDistributorPage.getCountries(), "should contain correct tags");
        assertArrayEquals(new String[]{"eaton:country/europe/fr",
                "eaton:country/europe/it"}, secureAttributesModelAsset.getCountries(), "should contain correct tags");
    }

    @Test
    @DisplayName("GetProductCategories yields the array of tag paths set in productCategories")
    void testGetProductCategories() {
        assertArrayEquals(new String[]{"eaton-secure:product-category/aerospace/engine-build-up-solutions",
                "eaton-secure:product-category/aerospace/engine-oil-system"}, secureAttributesModelPowerPage.getProductCategories(), "should contain correct tags");
        assertArrayEquals(new String[]{"eaton-secure:product-category/vehicle"}, secureAttributesModelAsset.getProductCategories(), "should contain correct tags");
    }

    @Test
    @DisplayName("GetPartnerProgramAndTierLevel yields the array of tag paths set in partnerProgramAndTierLevel")
    void testGetPartnerProgramAndTierLevel() {
        assertArrayEquals(new String[]{"eaton-secure:partner-programme-type/electrical-installer-programme/authorized"}, secureAttributesModelDistributorPage.getPartnerProgramAndTierLevel(), "should contain correct tags");
        assertArrayEquals(new String[]{"eaton-secure:partner-programme-type/electrical-installer-programme/authorized"}, secureAttributesModelAsset.getPartnerProgramAndTierLevel(), "should contain correct tags");
    }

}