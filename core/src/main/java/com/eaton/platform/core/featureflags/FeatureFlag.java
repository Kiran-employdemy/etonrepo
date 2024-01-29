package com.eaton.platform.core.featureflags;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a property on a model as feature flagged
 * This triggers the
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@InjectAnnotation
@Source(FeatureFlagRegisteringInjector.FEATURE_FLAG_INJECTOR)
public @interface FeatureFlag {

    /**
     * Resource Type
     * @return the resource type used to map to feature flags
     */
    String resourceType();

    /**
     * Feature Description
     * @return featureFlag description of the feature flag
     */
    String description();

    /**
     * Tool tip text
     * @return tooltip used to populate the text behind I icon
     */
    String tooltipText() default "";

}
