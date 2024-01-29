package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EatonCacheServiceImpl")
public @interface EatonCacheServiceConfig {

    @AttributeDefinition( name = "Max Cache Size" )
    int CONFIGURABLE_MAX_CACHE_SIZE() default 1000;

    @AttributeDefinition(name = "Cache Duration")
    int CONFIGURABLE_DURATION() default 60;

}