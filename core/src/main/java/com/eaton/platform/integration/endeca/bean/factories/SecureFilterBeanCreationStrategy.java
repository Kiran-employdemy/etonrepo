package com.eaton.platform.integration.endeca.bean.factories;

import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.endeca.bean.FilterBean;

/**
 * Strategy for creating the list of FilterBean
 */
public interface SecureFilterBeanCreationStrategy {
    /**
     * Creates the FilterBean using as parameters:
     *
     * @param userProfile to extract the tags from
     * @param module to specify the strategy for which secure module it is
     * @return the created FilterBean
     */
    FilterBean createFromUserProfileAndModule(UserProfile userProfile, SecureModule module);
}
