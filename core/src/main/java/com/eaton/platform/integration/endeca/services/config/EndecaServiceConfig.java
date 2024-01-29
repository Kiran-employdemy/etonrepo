package com.eaton.platform.integration.endeca.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EndecaServiceImpl")
public @interface EndecaServiceConfig {

    @AttributeDefinition(name = "Max Cache Size", description = "The maximum amount of Endeca responses to cache before invalidating the oldest entries.")
    int MAX_CACHE_SIZE() default 1000;

    @AttributeDefinition(name = "Max Cache Duration", description = "Seconds that a cached Endeca response will stay valid.")
    int MAX_CACHE_DURATION() default 60;

    @AttributeDefinition(name = "Connection Timeout", description = "Milliseconds until the Endeca connection will timeout.")
    int CONNECTION_TIMEOUT() default 60000;

    @AttributeDefinition(name = "Socket Timeout", description = "Socket timeout for the Endeca connection. This is the number of milliseconds before which data must be returned from Endeca.")
    int SOCKET_TIMEOUT() default 120000;

    @AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
    int MAX_CONNECTIONS() default 1000;

    @AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values only apply to the number of connections from a particular instance of HttpConnectionManager.")
    int MAX_HOST_CONNECTIONS() default 1000;



}