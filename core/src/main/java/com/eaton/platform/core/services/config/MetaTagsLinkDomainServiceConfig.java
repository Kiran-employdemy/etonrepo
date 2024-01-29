package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Meta Tags Link Domain Service Configuration", description = "Modify domains of canonical and alternate links in the page's metadata")
public @interface MetaTagsLinkDomainServiceConfig {

    @AttributeDefinition(name = "Country Code Domain Key Mapping", description = "A mapping of excluded countries with their domain key (from ExternalizerImpl config). format: country:domain key, example: cn:eaton-meta-cn")
    String[] excludedCountryDomainKeyList() default  {};

    @AttributeDefinition(name = "Fallback domain key", description = "Domain key to use when a country's domain key is not found in ExternalizerImpl config. Domain key must exist in ExternalizerImpl config.")
    String fallbackDomainKey() default "eaton";

}
