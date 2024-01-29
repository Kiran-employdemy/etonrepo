package com.eaton.platform.integration.endeca.bean.factories;

import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.enums.SecureFilter;

import java.util.stream.Collectors;

/**
 * Strategy for creating the Secure FilterBean for product categories
 */
class ProductCategoryFilterBeanCreationStrategy extends AbstractSecureFilterBeanCreationStrategy {
    private final SecureFilter secureFilter = SecureFilter.PRODUCT_CATEGORIES;
    @Override
    public FilterBean createFromUserProfileAndModule(UserProfile userProfile, SecureModule module) {
        return new FilterBean(secureFilter.getValue(), userProfile.getProductCategoriesTags().stream().map(
                tag-> extractLastLevel(tag, secureFilter.getMappedRootTag())
        ).collect(Collectors.toList()));
    }
}
