package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * The Class DynamicMediaUrlServiceConfig.
 */
@ObjectClassDefinition(name = "FunctionalityEnablementConfiguration")
public @interface FeatureEnablementConfiguration {

    /**
     * The dynamic media url base can be configured using this configuration.
     */
    @AttributeDefinition(name = "free sample enable")
    boolean isFreeSampleButtonEnabled() default false;

}
