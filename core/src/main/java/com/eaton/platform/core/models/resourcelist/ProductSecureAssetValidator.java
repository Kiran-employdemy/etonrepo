package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.integration.auth.models.UserProfile;

import java.util.Objects;

public class ProductSecureAssetValidator implements SecureAssetValidator {
    private final UserProfile userProfile;

    public ProductSecureAssetValidator(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public boolean validate(SecureAssetModel secureAssetModel) {
        return secureAssetModel.validateProductCategoriesTags(userProfile.getProductCategoriesTags());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductSecureAssetValidator that = (ProductSecureAssetValidator) o;
        return Objects.equals(userProfile, that.userProfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfile);
    }
}
