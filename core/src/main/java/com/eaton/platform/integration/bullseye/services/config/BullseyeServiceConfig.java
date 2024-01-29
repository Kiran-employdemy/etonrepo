package com.eaton.platform.integration.bullseye.services.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "BullseyeServiceImpl")
public @interface BullseyeServiceConfig {

    @AttributeDefinition( name = "Do Search 2 URL",description="Service URL for Do search API" )
    String doSearchURL() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Do Search Max Cache Size", description = "The maximum amount of responses to cache before invalidating the oldest entries.")
    int doSearchCacheSize() default 1000;

    @AttributeDefinition(name = "Do Search Max Cache Duration", description = "Seconds that a cached response will stay valid.")
    int doSearchCacheDuration() default 60;

    @AttributeDefinition( name = "Category Group URL",description="Service URL for category group API" )
    String categoryGroupURL() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Category Group Max Cache Size", description = "The maximum amount of responses to cache before invalidating the oldest entries.")
    int categoryGroupCacheSize() default 1000;

    @AttributeDefinition(name = "Category Group Max Cache Duration", description = "Seconds that a cached response will stay valid.")
    int categoryGroupCacheDuration() default 60;

    @AttributeDefinition( name = "Category URL",description="Service URL for category API" )
    String categoryURL() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Category Max Cache Size", description = "The maximum amount of responses to cache before invalidating the oldest entries.")
    int categoryCacheSize() default 1000;

    @AttributeDefinition(name = "Category Max Cache Duration", description = "Seconds that a cached response will stay valid.")
    int categoryCacheDuration() default 60;

    @AttributeDefinition( name = "Country List URL",description="Service URL for country list API" )
    String countryListURL() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Country List Max Cache Size", description = "The maximum amount of responses to cache before invalidating the oldest entries.")
    int countryListCacheSize() default 1000;

    @AttributeDefinition(name = "Country List Max Cache Duration", description = "Seconds that a cached response will stay valid.")
    int countryListCacheDuration() default 60;

    @AttributeDefinition( name = "Google Place URL",description="Service URL for google place API" )
    String googlePlaceURL() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Google Place Max Cache Size", description = "The maximum amount of responses to cache before invalidating the oldest entries.")
    int googlePlaceCacheSize() default 1000;

    @AttributeDefinition(name = "Google Place Max Cache Duration", description = "Seconds that a cached response will stay valid.")
    int googlePlaceCacheDuration() default 60;

    @AttributeDefinition( name = "MapBox API URL",description="Service URL for MapBox API" )
    String mapBoxAPIURL() default StringUtils.EMPTY;

    @AttributeDefinition(name = "MapBox API Max Cache Size", description = "The maximum amount of responses to cache before invalidating the oldest entries.")
    int mapBoxAPICacheSize() default 1000;

    @AttributeDefinition(name = "MapBox API Max Cache Duration", description = "Seconds that a cached response will stay valid.")
    int mapBoxAPICacheDuration() default 60;

    @AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
    int maxConnections() default 100;

    @AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values only apply to the number of connections from a particular instance of HttpConnectionManager.")
    int maxHostConnections() default 100;

}