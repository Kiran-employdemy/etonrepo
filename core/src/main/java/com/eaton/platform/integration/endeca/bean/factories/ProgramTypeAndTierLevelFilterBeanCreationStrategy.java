package com.eaton.platform.integration.endeca.bean.factories;

import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.enums.SecureFilter;

import java.util.stream.Collectors;

/**
 * Strategy for creating the Secure FilterBean for partner program type and tier levels
 */
class ProgramTypeAndTierLevelFilterBeanCreationStrategy extends AbstractSecureFilterBeanCreationStrategy {
    private final SecureFilter secureFilter = SecureFilter.PARTNER_PROGRAMME_TYPE_AND_TIER_LEVEL;
    @Override
    public FilterBean createFromUserProfileAndModule(UserProfile userProfile, SecureModule module) {
        return new FilterBean(secureFilter.getValue(), userProfile.getPartnerProgramAndTierLevelTags().stream().map(
                tag-> extractReplacingUnderscoreBetweenLevels(tag, secureFilter.getMappedRootTag())
        ).collect(Collectors.toList()));
    }
}
