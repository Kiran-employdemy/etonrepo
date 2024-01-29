package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.integration.auth.models.UserProfile;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecureAssetsFilterTest {

    @Mock
    SecureAssetValidator secureTagValidator1;
    @Mock
    SecureAssetValidator secureTagValidator2;
    @Mock
    SecureAssetValidator secureTagValidator3;
    @Mock
    SecureAssetValidatorFactory secureTagValidatorFactory;
    @Mock
    UserProfile userProfile;
    @Mock
    Resource resource;
    @Mock
    SecureAssetModel secureAssetModel;
    private SecureAssetFilter secureTagsFilterSecured;
    private SecureAssetFilter secureTagsFilterNotSecured;

    @BeforeEach
    void  setUp() {
        secureTagsFilterSecured = SecureAssetFilter.secured(secureTagValidatorFactory, userProfile);
        secureTagsFilterNotSecured = SecureAssetFilter.nonSecured();
        when(resource.adaptTo(SecureAssetModel.class)).thenReturn(secureAssetModel);
        when(secureTagValidatorFactory.createValidators(userProfile)).thenReturn(Arrays.asList(secureTagValidator1, secureTagValidator2, secureTagValidator3));
    }

    @Test
    @DisplayName("test method yields true if the asset being validated is not secure")
    void testThatTestYieldsTrueWhenAssetIsNotSecured() {
        reset(secureTagValidatorFactory);
        when(secureAssetModel.isSecured()).thenReturn(false);
        assertTrue(secureTagsFilterSecured.test(resource), "should be true");
    }

    @Test
    @DisplayName("test method yields false if the asset being validated is secure for non-secured use")
    void testThatTestYieldsFalseWhenAssetIsSecuredButNotSecuredUse() {
        reset(secureTagValidatorFactory);
        when(secureAssetModel.isSecured()).thenReturn(true);
        assertFalse(secureTagsFilterNotSecured.test(resource), "should be false");
    }

    @Test
    @DisplayName("test method yields false if one validator returns false")
    void testThatTestYieldsFalseOne() {
        when(secureAssetModel.isSecured()).thenReturn(true);
        when(secureTagValidator1.validate(secureAssetModel)).thenReturn(false);
        assertFalse(secureTagsFilterSecured.test(resource), "should be false");
    }

    @Test
    @DisplayName("test method yields false if second validator returns false")
    void testThatTestYieldsFalseTwo() {
        when(secureAssetModel.isSecured()).thenReturn(true);
        when(secureTagValidator1.validate(secureAssetModel)).thenReturn(true);
        when(secureTagValidator2.validate(secureAssetModel)).thenReturn(false);
        assertFalse(secureTagsFilterSecured.test(resource), "should be false");
    }

    @Test
    @DisplayName("test method yields false if third validator returns false")
    void testThatTestYieldsFalseThree() {
        when(secureAssetModel.isSecured()).thenReturn(true);
        when(secureTagValidator1.validate(secureAssetModel)).thenReturn(true);
        when(secureTagValidator2.validate(secureAssetModel)).thenReturn(true);
        when(secureTagValidator3.validate(secureAssetModel)).thenReturn(false);
        assertFalse(secureTagsFilterSecured.test(resource), "should be false");
    }

    @Test
    @DisplayName("test method yields ture if all validators return true")
    void testThatTestYieldsFalse() {
        when(secureAssetModel.isSecured()).thenReturn(true);
        when(secureTagValidator1.validate(secureAssetModel)).thenReturn(true);
        when(secureTagValidator2.validate(secureAssetModel)).thenReturn(true);
        when(secureTagValidator3.validate(secureAssetModel)).thenReturn(true);
        assertTrue(secureTagsFilterSecured.test(resource), "should be true");
    }

}