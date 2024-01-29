package com.eaton.platform.core.featureflags;

import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the FeatureFlagRegistryService interface
 */
@Component(service = FeatureFlagRegistryService.class,immediate = true)
public class FeatureFlagRegistryServiceImpl implements FeatureFlagRegistryService {

    private static final String FIELD_DESCRIPTION = "fieldDescription";

    private final Map<String, Set<FeatureFlagPojo>> featureFlagsMap = new HashMap<>();

    @Override
    public List<Resource> getExperimentalFeaturesFor(String resourceType, SlingHttpServletRequest request) {
        if(!featureFlagsMap.containsKey(resourceType)){
            return Collections.emptyList();
        }
        return featureFlagsMap.get(resourceType).stream()
                .map(featureFlagPojo ->
                        createValueMapResource(request, "./" + featureFlagPojo.getPropertyName(), featureFlagPojo.getPropertyDescription(), featureFlagPojo.getTooltipText()))
                .collect(Collectors.toList());
    }

    @Override
    public void registerFeatureFlag(String name, FeatureFlag featureFlag) {
        String resourceType = featureFlag.resourceType();
        FeatureFlagPojo featureFlagPojo = new FeatureFlagPojo(name, featureFlag.description());
        if(!StringUtils.EMPTY.equals(featureFlag.tooltipText())){
            featureFlagPojo = featureFlagPojo.withTooltipText(featureFlag.tooltipText());
        }
        if (!featureFlagsMap.containsKey(resourceType)) {
            featureFlagsMap.put(resourceType, new HashSet<>());
        }
        featureFlagsMap.get(resourceType).add(featureFlagPojo);
    }

    private static ValueMapResource createValueMapResource(SlingHttpServletRequest request, String propertyName, String propertyDescription, String tooltipText) {
        ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
        valueMap.put(CommonConstants.PROPERTY_NAME, propertyName);
        valueMap.put(CommonConstants.VALUE, true);
        valueMap.put(CommonConstants.TEXT, propertyDescription);
        if (tooltipText != null) {
            valueMap.put(FIELD_DESCRIPTION, tooltipText);
        }
        return new ValueMapResource(request.getResourceResolver(), new ResourceMetadata(), "nt:unstructured", valueMap);
    }
}
