package com.eaton.platform.integration.endeca.bean.factories;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory for creating the different SecureFilters needed for calling endeca to retrieve the gated content
 * for a specific user.
 */
public class SecureFilterBeanFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureFilterBeanFactory.class);
    public static final String ILLEGAL_SATE_LOG_MESSAGE = "In SecureFilterBeanFactory: userProfile was null, this should not happen, please check the code, throwing an IllegalStateException";
    public static final String ILLEGAL_STATE_EXCEPTION_MESSAGE = "At this point you need to check the code, userProfile must not be null.";
    private static final List<SecureFilterBeanCreationStrategy> STRATEGIES;

    static {
        STRATEGIES = new ArrayList<>();
        STRATEGIES.add(new AccountTypeFilterBeanCreationStrategy());
        STRATEGIES.add(new ApplicationAccessFilterBeanCreationStrategy());
        STRATEGIES.add(new ProductCategoryFilterBeanCreationStrategy());
        STRATEGIES.add(new CountryFilterBeanCreationStrategy());
        STRATEGIES.add(new ProgramTypeAndTierLevelFilterBeanCreationStrategy());
    }

    /**
     * Creates the list of filter beans given the user and secure module
     *
     * @param authenticationToken to extract the tags from
     * @param secureModule        specifying the use case for the creation of secured filters
     * @return a list of FilterBean for retrieving the secure content
     */
    public List<FilterBean> createFilterBeans(AuthenticationToken authenticationToken, SecureModule secureModule) {
        List<FilterBean> filterBeans = new ArrayList<>();
        if (authenticationToken == null) {
            return filterBeans;
        }
        if (authenticationToken.getBypassAuthorization()) {
            filterBeans.add(createBypassFilterBean());
            return filterBeans;
        }
        UserProfile userProfile = authenticationToken.getUserProfile();
        if (userProfile == null) {
            LOGGER.error(ILLEGAL_SATE_LOG_MESSAGE);
            throw new IllegalStateException(ILLEGAL_STATE_EXCEPTION_MESSAGE);
        }
		STRATEGIES.forEach(strategy -> {
			final FilterBean filter = strategy.createFromUserProfileAndModule(userProfile, secureModule);

			if (CollectionUtils.isEmpty(filter.getFilterValues())) {
				filter.setFilterValues(Collections.singletonList(EndecaConstants.EMPTY_FILTER_VALUE));
			}
			filterBeans.add(filter);
		});
        return filterBeans;
    }

    private static FilterBean createBypassFilterBean() {
        return new FilterBean(EndecaConstants.SECURE_BY_PASS, Collections.singletonList(CommonConstants.TRUE));
    }
}
