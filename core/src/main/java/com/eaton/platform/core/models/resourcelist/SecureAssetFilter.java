package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.integration.auth.models.UserProfile;
import com.google.common.base.Joiner;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class SecureAssetFilter implements Predicate<Resource> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureAssetFilter.class);

    private SecureAssetValidatorFactory secureAssetValidatorFactory;
    private UserProfile userProfile;
    private boolean doNotIncludeSecured;

    public static SecureAssetFilter secured(SecureAssetValidatorFactory secureAssetValidatorFactory, UserProfile userProfile) {
        SecureAssetFilter filter = new SecureAssetFilter();
        filter.secureAssetValidatorFactory = secureAssetValidatorFactory;
        filter.userProfile = userProfile;
        filter.doNotIncludeSecured = false;
        return filter;
    }

    public static SecureAssetFilter nonSecured() {
        SecureAssetFilter filter = new SecureAssetFilter();
        filter.doNotIncludeSecured = true;
        return filter;
    }

    @Override
    public boolean test(Resource resource) {
        SecureAssetModel secureAsset = resource.adaptTo(SecureAssetModel.class);
        if (Objects.requireNonNull(secureAsset).isSecured() && doNotIncludeSecured) {
            return false;
        }
        if (!Objects.requireNonNull(secureAsset).isSecured()){
            return true;
        }
        Map<Class<? extends SecureAssetValidator>, Boolean> validationMap = new HashMap<>();
        boolean allMatch = secureAssetValidatorFactory.createValidators(userProfile).stream().allMatch((SecureAssetValidator secureAssetValidator) -> {
            boolean validate = secureAssetValidator.validate(secureAsset);
            validationMap.put(secureAssetValidator.getClass(), validate);
            return secureAssetValidator.validate(secureAsset);
        });
        logValidationMap(resource, validationMap);
        return allMatch;
    }

    private static void logValidationMap(Resource resource, Map<Class<? extends SecureAssetValidator>, Boolean> validationMap) {
        String joined = Joiner.on("\n").join(validationMap.entrySet());
        LOGGER.debug("Resource on path {} is valid because {}", resource.getPath(), joined);
    }
}
