package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Country Language Code Configuraton ", description = "Country Language Code Configuraton")
public @interface CountryLangCodeConfigServiceConfig {

    @AttributeDefinition(name = "Max Cache Size", description = "The maximum amount of responses to cache before invalidating the oldest entries.")
    int MAX_CACHE_SIZE() default 1000;

    @AttributeDefinition(name = "Max Cache Duration", description = "Seconds that a cached response will stay valid.")
    int MAX_CACHE_DURATION() default 60;

    @AttributeDefinition(name = "X-default hrefLangCode config for different domains", description = "Enter x-default hrefLangCode with respective repository path (no spaces) : Repository path | HrefLangCode")
    String [] X_DEFAULT_HREF_LANG_CODE() default { "/content/eaton|us/en-us", "/content/login|us/en-us", "/content/eaton-cummins|us/en-us", "/content/greenswitching|gb/en-gb", "/content/phoenixtec|us/en-us"};
    
    @AttributeDefinition(name = "Exclude secure country list", description = "Enter country code which should be excluded for secure pages/assets")
    String [] exclude_country_code_list() default { "IR", "SY", "CU","SD","KP"};

}
