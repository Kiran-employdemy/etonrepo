package com.eaton.platform.integration.endeca.bean.factories;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.enums.SecureFilter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Strategy for creating the Secure FilterBean for country
 */
class CountryFilterBeanCreationStrategy extends AbstractSecureFilterBeanCreationStrategy {
    private final SecureFilter secureFilter = SecureFilter.COUNTRY;
    @Override
    public FilterBean createFromUserProfileAndModule(UserProfile userProfile, SecureModule module) {
        return new FilterBean(secureFilter.getValue(),
                userProfile.getLocaleTags().stream()
                        .map(tag -> extractTagName(module, tag)).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private String extractTagName(SecureModule module, String tag) {
        String extractedTagName = extractSimpleTagValue(tag, secureFilter.getMappedRootTag());
        if (Arrays.asList(SecureModule.PRODUCTGRID, SecureModule.PRODUCTGRID_FOR_AUTHOR).contains(module)) {
            if (!extractedTagName.contains(CommonConstants.SLASH_STRING)) {
                return null;
            } else {
                return extractLastLevel(tag, secureFilter.getMappedRootTag()).toUpperCase(Locale.US);
            }
        }
        return extractLastLevel(tag, secureFilter.getMappedRootTag());
    }
}
