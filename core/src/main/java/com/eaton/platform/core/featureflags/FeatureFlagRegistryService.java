package com.eaton.platform.core.featureflags;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.List;

/**
 * Service for mapping of FeatureFlaggedModel
 * To be used
 */

interface FeatureFlagRegistryService {

    /**
     * Returns the experimental features for a given resourceType
     * @param resourceType to find the feature flags for
     * @param request is used to create the Resource list
     * @return the list of resources used to construct the added checkboxes to enable/disable features
     */
    List<Resource> getExperimentalFeaturesFor(String resourceType, SlingHttpServletRequest request);

    /**
     * Registers for a given name the FeatureFlag annotation
     * @param name is the property name used for storing the feature flag in CRX
     * @param featureFlag annotation having the needed information for mapping and creating the checkboxes
     */
    void registerFeatureFlag(String name, FeatureFlag featureFlag);
}
