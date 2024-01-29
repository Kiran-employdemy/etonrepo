package com.eaton.platform.core.featureflags;

import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Custom injector to handle the FeatureFlag annotation.
 * Registers the annotated FeatureFlag property using the FeatureFlagRegistryService
 */
@Component(service = {Injector.class},immediate = true,
        property = {
                Constants.SERVICE_RANKING+"=" + Integer.MAX_VALUE
        })
public class FeatureFlagRegisteringInjector implements Injector {
    public static final String FEATURE_FLAG_INJECTOR = "featureFlagged";

    @Reference
    private FeatureFlagRegistryService featureFlagRegistryService;

    @Override
    public String getName() {
        return FEATURE_FLAG_INJECTOR;
    }

    @Override
    public Object getValue(Object adaptable, String name, Type type, AnnotatedElement annotatedElement, DisposalCallbackRegistry disposalCallbackRegistry) {
        if(annotatedElement.getDeclaredAnnotation(FeatureFlag.class) == null) {
            return null;
        }
        featureFlagRegistryService.registerFeatureFlag(name, annotatedElement.getAnnotation(FeatureFlag.class));
        return adaptable;
    }
}
