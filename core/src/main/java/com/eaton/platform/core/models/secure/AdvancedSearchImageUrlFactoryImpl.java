package com.eaton.platform.core.models.secure;

import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.core.search.api.DefaultImageUrlFactory;

/**
 * The Default Image Url Factory for advanced search
 */
public class AdvancedSearchImageUrlFactoryImpl implements DefaultImageUrlFactory {

    public static final String DEFAULT_IMAGE_URL_TEMPLATE = "/content/dam/eaton/resources/advanced-search/%s-thumbnail.png";

    @Override
    public boolean needsDefaultImageUrl(String imageUrl) {
        return imageUrl.endsWith(EndecaConstants.JCRCONTENT_RENDITIONS_ORIGINAL);
    }

    @Override
    public String createImageUrl(String identifier) {
        return String.format(DEFAULT_IMAGE_URL_TEMPLATE, identifier);
    }
}
