package com.eaton.platform.core.enums.secure;

import com.eaton.platform.integration.endeca.constants.EndecaConstants;

/**
 * Enum to provide a list of SecureModules and a mapping with the corresponding endeca appId
 */
public enum SecureModule {
    PRODUCTGRID(EndecaConstants.PRODUCT_GRID_APP_ID),
    SECURESEARCH(EndecaConstants.SECURE_EATON_SEARCH),
    SECURETYPEAHEAD(EndecaConstants.SECURE_APP_ID),
    SECUREASSETSEARCH(EndecaConstants.SECURE_ASSET_EATON_SEARCH),
    PRODUCTGRID_FOR_AUTHOR(EndecaConstants.PRODUCT_GRID_AUTH_APP_ID);

    private final String appId;

    SecureModule(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }

}
