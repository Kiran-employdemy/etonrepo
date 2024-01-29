package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.integration.auth.models.UserProfile;

import java.util.Objects;

public class AccountSecureAssetValidator implements SecureAssetValidator {
    private final UserProfile userProfile;

    public AccountSecureAssetValidator(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public boolean validate(SecureAssetModel secureAssetModel) {
        return secureAssetModel.validateAccountTypeTags(userProfile.getAccountTypeTags());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountSecureAssetValidator that = (AccountSecureAssetValidator) o;
        return Objects.equals(userProfile, that.userProfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfile);
    }
}
