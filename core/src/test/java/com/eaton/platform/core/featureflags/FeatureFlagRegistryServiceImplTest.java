package com.eaton.platform.core.featureflags;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureFlagRegistryServiceImplTest {

    public static final String ADVANCED_SEARCH = "/eaton/components/secure/advanced-search";
    public static final String SEARCH = "/eaton/components/search/search-results";
    public static final String ADVANCED_SARCH_DESCRIPTION = "Enable Description";
    public static final String ADVANCED_SARCH_DESCRIPTION_TOOL_TIP = "Checked will display the description, default one as long Endeca part is not implemented.";
    public static final String OTHER_FEATURE_DESCRIPTION = "Other Feature";
    public static final String ENABLE_DESCRIPTION = "enableDescription";
    public static final String OTHER_FEATURE = "otherFeature";
    public static final String SHOULD_BE_CORRECT = "should be correct";
    @Mock
    private FeatureFlag enableDescription;
    @Mock
    private FeatureFlag otherFeature;
    @Mock
    private FeatureFlag searchFeature;
    @Mock
    private SlingHttpServletRequest slingHttpServletRequest;

    private FeatureFlagRegistryServiceImpl featureFlagRegistryService = new FeatureFlagRegistryServiceImpl();
    @BeforeEach
    void setUp() {
        when(enableDescription.resourceType()).thenReturn(ADVANCED_SEARCH);
        when(enableDescription.description()).thenReturn(ADVANCED_SARCH_DESCRIPTION);
        when(enableDescription.tooltipText()).thenReturn(ADVANCED_SARCH_DESCRIPTION_TOOL_TIP);
        when(otherFeature.resourceType()).thenReturn(ADVANCED_SEARCH);
        when(otherFeature.description()).thenReturn(OTHER_FEATURE_DESCRIPTION);
        when(otherFeature.tooltipText()).thenReturn("");
        when(searchFeature.resourceType()).thenReturn(SEARCH);
        when(searchFeature.description()).thenReturn(OTHER_FEATURE_DESCRIPTION);
        when(searchFeature.tooltipText()).thenReturn("");
    }

    @Test
    @DisplayName("registerFeatureFlag should be able to register for a given name the FeatureFlag annotation")
    void testGetExperimentalFeaturesForWrongKey() {
        Mockito.reset(otherFeature,enableDescription,searchFeature);
        List<Resource> experimentalFeatures = featureFlagRegistryService.getExperimentalFeaturesFor("not existing", slingHttpServletRequest);
        Assertions.assertNotNull(experimentalFeatures, "should not be null");
        Assertions.assertTrue(experimentalFeatures.isEmpty(), "should be empty list");
    }

    @Test
    @DisplayName("registerFeatureFlag should be able to register for a given name the FeatureFlag annotation")
    void testRegisterFeatureFlag() {
        Mockito.reset(otherFeature,searchFeature);
        featureFlagRegistryService.registerFeatureFlag(ENABLE_DESCRIPTION, enableDescription);
        List<Resource> experimentalFeatures = featureFlagRegistryService.getExperimentalFeaturesFor(ADVANCED_SEARCH, slingHttpServletRequest);
        Assertions.assertNotNull(experimentalFeatures, "should not be null");
        Assertions.assertEquals(1, experimentalFeatures.size(), "should be 1");
        assertValueMap(experimentalFeatures.get(0).getValueMap(), ENABLE_DESCRIPTION, ADVANCED_SARCH_DESCRIPTION, ADVANCED_SARCH_DESCRIPTION_TOOL_TIP);
    }

    @Test
    @DisplayName("registerFeatureFlag should not add already registered feature flag")
    void testRegisterFeatureFlagAgain() {
        Mockito.reset(otherFeature,searchFeature);
        featureFlagRegistryService.registerFeatureFlag(ENABLE_DESCRIPTION, enableDescription);
        featureFlagRegistryService.registerFeatureFlag(ENABLE_DESCRIPTION, enableDescription);
        List<Resource> experimentalFeatures = featureFlagRegistryService.getExperimentalFeaturesFor(ADVANCED_SEARCH, slingHttpServletRequest);
        Assertions.assertNotNull(experimentalFeatures, "should not be null");
        Assertions.assertEquals(1, experimentalFeatures.size(), "should be 1");
        assertValueMap(experimentalFeatures.get(0).getValueMap(), ENABLE_DESCRIPTION, ADVANCED_SARCH_DESCRIPTION, ADVANCED_SARCH_DESCRIPTION_TOOL_TIP);
    }

    @Test
    @DisplayName("registerFeatureFlag should be able to register for a given name the FeatureFlag annotation")
    void testRegister2FeatureFlagsForSameResourceType() {
        Mockito.reset(searchFeature);
        featureFlagRegistryService.registerFeatureFlag(ENABLE_DESCRIPTION, enableDescription);
        featureFlagRegistryService.registerFeatureFlag(OTHER_FEATURE, otherFeature);
        List<Resource> experimentalFeatures = featureFlagRegistryService.getExperimentalFeaturesFor(ADVANCED_SEARCH, slingHttpServletRequest);
        Assertions.assertNotNull(experimentalFeatures, "should not be null");
        Assertions.assertEquals(2, experimentalFeatures.size(), "should be 2");
        assertValueMap(experimentalFeatures.get(0).getValueMap(), OTHER_FEATURE, OTHER_FEATURE_DESCRIPTION, null);
        assertValueMap(experimentalFeatures.get(1).getValueMap(), ENABLE_DESCRIPTION, ADVANCED_SARCH_DESCRIPTION, ADVANCED_SARCH_DESCRIPTION_TOOL_TIP);
    }

    @Test
    @DisplayName("registerFeatureFlag should be able to register for a given name the FeatureFlag annotation")
    void testRegisterFeatureFlagsForDifferentResourceTypes() {
        featureFlagRegistryService.registerFeatureFlag(ENABLE_DESCRIPTION, enableDescription);
        featureFlagRegistryService.registerFeatureFlag(OTHER_FEATURE, otherFeature);
        featureFlagRegistryService.registerFeatureFlag(OTHER_FEATURE, searchFeature);
        List<Resource> experimentalFeaturesAdvancedSearch = featureFlagRegistryService.getExperimentalFeaturesFor(ADVANCED_SEARCH, slingHttpServletRequest);
        List<Resource> experimentalFeaturesSearch = featureFlagRegistryService.getExperimentalFeaturesFor(SEARCH, slingHttpServletRequest);
        Assertions.assertNotNull(experimentalFeaturesAdvancedSearch, "should not be null");
        Assertions.assertEquals(2, experimentalFeaturesAdvancedSearch.size(), "should be 2");
        assertValueMap(experimentalFeaturesAdvancedSearch.get(0).getValueMap(), OTHER_FEATURE, OTHER_FEATURE_DESCRIPTION, null);
        assertValueMap(experimentalFeaturesAdvancedSearch.get(1).getValueMap(), ENABLE_DESCRIPTION, ADVANCED_SARCH_DESCRIPTION, ADVANCED_SARCH_DESCRIPTION_TOOL_TIP);
        Assertions.assertNotNull(experimentalFeaturesSearch, "should not be null");
        Assertions.assertEquals(1, experimentalFeaturesSearch.size(), "should be 1");
        assertValueMap(experimentalFeaturesSearch.get(0).getValueMap(), OTHER_FEATURE, OTHER_FEATURE_DESCRIPTION, null);
    }

    private void assertValueMap(ValueMap valueMap, String name, String text, String toolTip) {
        Assertions.assertAll(()->{
            Assertions.assertEquals("./" + name, valueMap.get("name"), SHOULD_BE_CORRECT);
            Assertions.assertTrue((Boolean) valueMap.get("value"), SHOULD_BE_CORRECT);
            Assertions.assertEquals(text, valueMap.get("text"), SHOULD_BE_CORRECT);
            if (toolTip != null) {
                Assertions.assertEquals(toolTip, valueMap.get("fieldDescription"), SHOULD_BE_CORRECT);
            }
        });
    }
}