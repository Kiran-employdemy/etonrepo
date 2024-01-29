package com.eaton.platform.integration.google.services.config;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "GoogleServiceImpl")
public @interface GoogleServiceConfig {

    @AttributeDefinition(name = "Base Url", description = "Base Url")
    String baseUrl() default "https://maps.googleapis.com";
    
    @AttributeDefinition(name = "Key", description = "Api Key")
    String key() default "AIzaSyBE9jgy97Zhmn-s5vyOzwAj2jaCslurhlQ";

    @AttributeDefinition(name = "Response Cache", description = "Response Cache")
    String responseCache() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Response Cache Size", description = "Response Cache Size")
    int responseCacheSize() default 1000;

    @AttributeDefinition(name = "Response Cache Duration (hours)", description = "Response Cache Duration (hours)")
    int responseCacheDuration() default 24;

    @AttributeDefinition(name = "Response Timeout (seconds)", description = "Response Timeout (seconds)")
    int responseTimeout() default 15;

    @AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
    int maxConnections() default 100;

    @AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values only apply to the number of connections from a particular instance of HttpConnectionManager.")
    int maxHostConnections() default 100;

}