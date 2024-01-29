package com.eaton.platform.integration.apigee.services.config;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "ApigeeServiceImpl")
public @interface ApigeeServiceConfig {

    @AttributeDefinition(name = "Base Url", description = "Base Url to apigee")
    String baseUrl() default "https://api.eaton.com";

    @AttributeDefinition(name = "Key", description = "Api Key")
    String key() default "Mz4zUbvcpGelQ3SVyJ3jWi8iTLewMR0w";

    @AttributeDefinition(name = "Free sample Key", description = "Free sample Api Key")
    String freeSampleOrderKey() default "gVcmvpJHscMtTgQAPUMXNE7p3S410LVt";

    @AttributeDefinition(name = "Secret", description = "API Secret")
    String secret() default "{String}{a7d0d57b291d8ff9bca2800925480efc67e9b3d571e96451f43c0d78476b21a5b34404f749335c6af4553f28ef112b49}";

    @AttributeDefinition(name = "Free sample Secret", description = "Free sample API Secret")
    String freeSampleOrderSecret() default "4ROGHVyosIdyJSVj";

    @AttributeDefinition(name = "Token Cache", description = "Token Cache")
    String accessTokenCache() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Token Cache Size", description = "Token Cache Size")
    int accessTokenCacheSize() default 5;

    @AttributeDefinition(name = "Token Cache Duration (minutes)", description = "Access Token Cache Duration (minutes)")
    int accessTokenCacheDuration() default 50;

    @AttributeDefinition(name = "Response Cache", description = "Response Cache")
    String responseCache() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Response Cache Size", description = "Response Cache Size")
    int responseCacheSize() default 1000;

    @AttributeDefinition(name = "Response Cache Duration (hours)", description = "Response Cache Duration (hours)")
    int responseCacheDuration() default 24;

    @AttributeDefinition(name = "Api timeout (seconds)", description = "Api timeout (seconds)")
    int responseTimeout() default 30;

    @AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
    int maxConnections() default 200;

    @AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values only apply to the number of connections from a particular instance of HttpConnectionManager.")
    int maxHostConnections() default 200;

}