package com.eaton.platform.integration.endeca.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EndecaQRServiceImpl")
public @interface EndecaQRServiceConfig {

    @AttributeDefinition(name = "Max Cache Size",
            description = "The maximum amount of Endeca responses to cache before invalidating the oldest entries.")
    int max_cache_size() default 1000;

    @AttributeDefinition(name = "Max Cache Duration",
            description = "Seconds that a cached Endeca response will stay valid.")
    int max_cache_duration() default 60;

    @AttributeDefinition(name = "Max Connections",
            description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
    int max_connections() default 1000;

    @AttributeDefinition(name = "Max Host Connections",
            description = "Defines maximum number of connections allowed per host configuration. (Values only apply to the number of connections from a particular instance of HttpConnectionManager.)")
    int max_host_connections() default 1000;

    @AttributeDefinition(name = "Global SKU Page Path",
            description = "Defines the global SKU page path. Redirect here if product is not sellable in current cookie language, or if language cookie not available and product is sold globally.")
    String global_sku_page_path() default "/globalSku";

}
