package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * The Class SmartCropServiceConfig.
 */
@ObjectClassDefinition(name = "SmartCropServiceConfigImpl")
public @interface SmartCropServiceConfig {

    /**
     * The smart crop title can be configured using this configuration.
     */
    @AttributeDefinition(name = "Smart Crop Title")
    String smart_crop_title() default "";

    /**
     * The smart crop url suffix can be configured using this configuration.
     */
    @AttributeDefinition(name = "Smart Crop Url Suffix")
    String smart_crop_url_suffix() default "";

}
