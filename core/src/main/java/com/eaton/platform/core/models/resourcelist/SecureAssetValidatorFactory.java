package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.integration.auth.models.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class SecureAssetValidatorFactory {
    public List<SecureAssetValidator> createValidators(UserProfile userProfile) {
        ArrayList<SecureAssetValidator> secureAssetValidators = new ArrayList<>();
        secureAssetValidators.add(new AccountSecureAssetValidator(userProfile));
        secureAssetValidators.add(new ApplicationSecureAssetValidator(userProfile));
        secureAssetValidators.add(new ProductSecureAssetValidator(userProfile));
        secureAssetValidators.add(new PartnerProgramAndTierLevelSecureAssetValidator(userProfile));
        return secureAssetValidators;
    }
}
