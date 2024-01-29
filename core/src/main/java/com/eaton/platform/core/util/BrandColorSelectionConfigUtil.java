package com.eaton.platform.core.util;

import com.eaton.platform.core.models.brandingselectortool.BrandColorSelectionConfigModel;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class BrandColorSelectionConfigUtil.
 */
public class BrandColorSelectionConfigUtil {

    /**
     * The Constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BrandColorSelectionConfigUtil.class);

    /**
     * Gets the brand color selection config details.
     *
     * @param brandColorSelectionConfigRes the brand color selection config res
     * @return the brand color selection config details
     */
    public static BrandColorSelectionConfigModel getBrandColorSelectionConfigDetails(Resource brandColorSelectionConfigRes) {
        LOGGER.info("Entered into BrandColorSelectionConfigModel getbrandColorSelectionConfigDetails() method :::");
        BrandColorSelectionConfigModel brandColorSelectionConfigModel = null;
        if (null != brandColorSelectionConfigRes) {
            brandColorSelectionConfigModel = brandColorSelectionConfigRes.adaptTo(BrandColorSelectionConfigModel.class);
        }
        return brandColorSelectionConfigModel;
    }
}
