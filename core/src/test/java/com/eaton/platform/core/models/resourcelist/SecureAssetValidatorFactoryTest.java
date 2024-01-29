package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.integration.auth.models.UserProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class SecureAssetValidatorFactoryTest {

    @Mock
    UserProfile userProfile;

    @Test
    @DisplayName("createValidators returns collection of SecureAssetValidator for account, application, product and partner Program and tierlevel")
    void testThatCreateValidatorsReturnsCorrectList() {
        Assertions.assertEquals(Arrays.asList(new AccountSecureAssetValidator(userProfile), new ApplicationSecureAssetValidator(userProfile)
                        , new ProductSecureAssetValidator(userProfile), new PartnerProgramAndTierLevelSecureAssetValidator(userProfile))
        , new SecureAssetValidatorFactory().createValidators(userProfile), "Must be equal");
    }
}