package com.eaton.platform.core.services;

import java.util.List;

/**
 * This service is used for Site map Generation
 */

public interface SiteMapGenerationService {

        String[] getResourceTypes();

        boolean getIncludeInheritValue();

        boolean getIncludeLastModified();

        String [] getChangefreqProperties();

        String [] getPriorityProperties();

        String getDamAssetProperty();

        List<String> getDamAssetTypes();

        String getExternalExcludeProperty();
        
        String getInternalExcludeProperty();

        String getCharacterEncoding();

        boolean getExtensionlessUrls();

        boolean getRemoveTrailingSlash();
}
