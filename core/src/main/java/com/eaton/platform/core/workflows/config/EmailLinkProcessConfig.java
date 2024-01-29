package com.eaton.platform.core.workflows.config;

import com.eaton.platform.core.constants.CommonConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


@ObjectClassDefinition(name = "Eaton asset download email link process workflow service")
public  @interface EmailLinkProcessConfig{
    @AttributeDefinition(name = "Externalizer Domain",description = "Must correspond to a configuration of the Externalizer component.")
    String externalizer_domain() default CommonConstants.DEFAULT_EXTERNALIZER_DOMAIN;


}