package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.integration.auth.models.UserProfile;

import java.util.Objects;

public class ApplicationSecureAssetValidator implements SecureAssetValidator {
    private final UserProfile userProfile;

    public ApplicationSecureAssetValidator(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public boolean validate(SecureAssetModel secureAssetModel) {
        return secureAssetModel.validateApplicationAccessTags(userProfile.getAppAccessTags());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApplicationSecureAssetValidator that = (ApplicationSecureAssetValidator) o;
        return Objects.equals(userProfile, that.userProfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfile);
    }
}
