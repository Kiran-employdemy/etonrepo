package com.eaton.platform.integration.contentfragmentapi.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author E0527858
 *
 */
@ObjectClassDefinition(name = "ContentFragmentService")
public @interface ContentFragmentServiceConfig {
	

    @AttributeDefinition(
    		name = "Config Delimiter",
    		description="Delimiter used for the config values"
    	)
    String config_delimiter() default  "=";
	
    @AttributeDefinition(
            name = "Content Fragment Service Configuration",
            description = "Content Fragment DAM and Content Service Model properties",
            type = AttributeType.STRING
        )
    String[] contentfragment_service_config() default {"cfdam-golfpride-navigation=/content/dam/golfpride/", "cfmodel-golfpride-navigation=/conf/golfpride/settings/dam/cfm/models/navigation-api"};
}