package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * The Class DynamicMediaUrlServiceConfig.
 */
@ObjectClassDefinition(name = "DynamicMediaUrlServiceImpl")
public @interface DynamicMediaUrlServiceConfig {

    /**
     * The dynamic media url base can be configured using this configuration.
     */
    @AttributeDefinition(name = "Dynamic Media URL Base")
    String dm_url_base() default "";

}
