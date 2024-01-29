package com.eaton.platform.integration.bullseye.services.config;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Bullseye Cache", description = "Cache Object for Bullseye")
public @interface BullseyeCacheConfig{

        @AttributeDefinition(name = "TTL (Time to live)", description = "Define in second how long should this cache be persist in the system. (default to 1 hour)", required = true)
        int ttl() default 3600;

        @AttributeDefinition(name = "Cache Url", description = "Provide endpoint to build this cache", required = true)
        String cacheUrl() default StringUtils.EMPTY;

        @AttributeDefinition(name = "Max Cache Size", description = "Provide max number of cache size. Default to 1000")
        int cacheSize() default 1000;

        @AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
        int maxConnections() default 250;

        @AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values only apply to the number of connections from a particular instance of HttpConnectionManager.")
        int maxHostConnections() default 250;

}
