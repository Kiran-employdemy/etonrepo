package com.eaton.platform.core.services.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EatonEloquaSandBoxPathConfig")
public @interface EloquaSandBoxPathConfig {

    @AttributeDefinition( name = "Enter cloud service Eloqua SandBox Path" )
    String eloqua_sandbox_cloud_service_path() default StringUtils.EMPTY;

}
