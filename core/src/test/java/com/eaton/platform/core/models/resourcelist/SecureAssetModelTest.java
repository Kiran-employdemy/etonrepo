package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.core.constants.CommonConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class SecureAssetModelTest {

    SecureAssetModel secureAssetModel;
    SecureAssetModel notSecureAssetModel;

    @BeforeEach
    void setUp(AemContext aemContext) {
        aemContext.addModelsForClasses(SecureAssetModel.class);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("secure-asset.json"))
                , "/content/dam/eaton/secure/secure-asset.pdf");
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("not-secure-asset.json"))
                , "/content/dam/eaton/secure/not-secure-asset.pdf");
        Resource secureAssetResource = aemContext.resourceResolver().getResource("/content/dam/eaton/secure/secure-asset.pdf");
        Resource notSecureAssetResource = aemContext.resourceResolver().getResource("/content/dam/eaton/secure/not-secure-asset.pdf");
        secureAssetModel = Objects.requireNonNull(Objects.requireNonNull(secureAssetResource).getChild(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(SecureAssetModel.class);
        notSecureAssetModel = Objects.requireNonNull(Objects.requireNonNull(notSecureAssetResource).getChild(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(SecureAssetModel.class);
    }

    @Test
    @DisplayName("Model must be adaptable from resource")
    void testThatModelIsAdaptableFromResource() {
        assertNotNull(secureAssetModel, "must not be null");
        assertNotNull(notSecureAssetModel, "must not be null");
    }
    @Test
    @DisplayName("validateAccountTypeTags must return true when the asset doesn't have account types")
    void testThatWhenNoAccountTypesInAssetYieldsTrue() {
        assertTrue(new SecureAssetModel().validateAccountTypeTags(new ArrayList<>()), "must be true");
    }

    @Test
    @DisplayName("isSecured yields false on a not secure asset")
    void testThatIsSecureYieldsFalseOnNotSecuredAsset() {
        assertFalse(notSecureAssetModel.isSecured(), "should be false");
    }
    @Test
    @DisplayName("isSecured yields true on a secure asset")
    void testThatIsSecureYieldsTrueOnSecuredAsset() {
        assertTrue(secureAssetModel.isSecured(), "should be true");
    }

    @Test
    @DisplayName("validateAccountTypeTags must return false when the asset has account types, but input is an empty list")
    void testThatWhenAccountTypesInAssetEmptyListYieldsFalse() {
        assertFalse(secureAssetModel.validateAccountTypeTags(new ArrayList<>()), "must be false");
    }

    @Test
    @DisplayName("validateAccountTypeTags must return true when the asset has at least one of the account types of the list")
    void testThatWhenAccountTypesInAssetHasAtLeastOneOfListYieldsTrue() {
        assertTrue(secureAssetModel.validateAccountTypeTags(List.of("eaton-secure:accounttype/consultant")), "must be true");
    }
    @Test
    @DisplayName("validateAccountTypeTags must return true when the asset has all of the account types of the list")
    void testThatWhenAccountTypesInAssetHasAAllOfListYieldsTrue() {
        assertTrue(secureAssetModel.validateAccountTypeTags(Arrays.asList("eaton-secure:accounttype/consultant",
                "eaton-secure:accounttype/contractor")), "must be true");
    }

    @Test
    @DisplayName("validateAccountTypeTags must return true when the asset doesn't have account types")
    void testThatWhenNoApplicationAccessTagsInAssetYieldsTrue() {
        assertTrue(new SecureAssetModel().validateApplicationAccessTags(new ArrayList<>()), "must be true");
    }
    @Test
    @DisplayName("validateAccountTypeTags must return false when the asset has account types, but input is an empty list")
    void testThatWhenApplicationAccessTagsInAssetEmptyListYieldsFalse() {
        assertFalse(secureAssetModel.validateApplicationAccessTags(new ArrayList<>()), "must be false");
    }

    @Test
    @DisplayName("validateAccountTypeTags must return true when the asset has at least one of the account types of the list")
    void testThatWhenApplicationAccessTagsInAssetHasAtLeastOneOfListYieldsTrue() {
        assertTrue(secureAssetModel.validateApplicationAccessTags(List.of("eaton-secure:application-access/foreseer")), "must be true");
    }
    @Test
    @DisplayName("validateAccountTypeTags must return true when the asset has all of the account types of the list")
    void testThatWhenApplicationAccessTagsInAssetHasOneOfListYieldsTrue() {
        assertTrue(secureAssetModel.validateApplicationAccessTags(Arrays.asList("eaton-secure:application=access/blah",
                "eaton-secure:application-access/foreseer")), "must be true");
    }
    @Test
    @DisplayName("validateProductCategoriesTags must return true when the asset doesn't have account types")
    void testThatWhenNoProductCategoriesTagsInAssetYieldsTrue() {
        assertTrue(new SecureAssetModel().validateProductCategoriesTags(new ArrayList<>()), "must be true");
    }
    @Test
    @DisplayName("validateProductCategoriesTags must return false when the asset has account types, but input is an empty list")
    void testThatWhenProductCategoriesTagsInAssetEmptyListYieldsFalse() {
        assertFalse(secureAssetModel.validateProductCategoriesTags(new ArrayList<>()), "must be false");
    }

    @Test
    @DisplayName("validateProductCategoriesTags must return true when the asset has at least one of the account types of the list")
    void testThatWhenProductCategoriesTagsInAssetHasAtLeastOneOfListYieldsTrue() {
        assertTrue(secureAssetModel.validateProductCategoriesTags(List.of("eaton-secure:product-category/vehicle")), "must be true");
    }
    @Test
    @DisplayName("validateProductCategoriesTags must return true when the asset has all of the account types of the list")
    void testThatWhenProductCategoriesTagsInAssetHasOneOfListYieldsTrue() {
        assertTrue(secureAssetModel.validateProductCategoriesTags(Arrays.asList("eaton-secure:product-category/blah",
                "eaton-secure:product-category/vehicle")), "must be true");
    }
    @Test
    @DisplayName("validatePartnerProgramAndTierLevelTags must return true when the asset doesn't have account types")
    void testThatWhenNoPartnerProgramAndTierLevelTagsInAssetYieldsTrue() {
        assertTrue(new SecureAssetModel().validatePartnerProgramAndTierLevelTags(new ArrayList<>()), "must be true");
    }
    @Test
    @DisplayName("validatePartnerProgramAndTierLevelTags must return false when the asset has account types, but input is an empty list")
    void testThatWhenPartnerProgramAndTierLevelTagsInAssetEmptyListYieldsFalse() {
        assertFalse(secureAssetModel.validatePartnerProgramAndTierLevelTags(new ArrayList<>()), "must be false");
    }

    @Test
    @DisplayName("validatePartnerProgramAndTierLevelTags must return true when the asset has at least one of the account types of the list")
    void testThatWhenPartnerProgramAndTierLevelTagsInAssetHasAtLeastOneOfListYieldsTrue() {
        assertTrue(secureAssetModel.validatePartnerProgramAndTierLevelTags(List.of("eaton-secure:partner-programme-type/electrical-installer-programme/authorized")), "must be true");
    }
    @Test
    @DisplayName("validatePartnerProgramAndTierLevelTags must return true when the asset has all of the account types of the list")
    void testThatWhenPartnerProgramAndTierLevelTagsInAssetHasOneOfListYieldsTrue() {
        assertTrue(secureAssetModel.validatePartnerProgramAndTierLevelTags(Arrays.asList("eaton-secure:partner-programme-type/electrical-installer-programme/authorized",
                "eaton-secure:partner-programme-type/blah/authorized")), "must be true");
    }

}