package com.eaton.platform.core.services.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EatonRecaptchaServiceConfigImpl")
public @interface EatonRecaptchaServiceConfig {

    @AttributeDefinition( name = "Google Recaptcha Site Key" )
    String recaptcha_site_key() default StringUtils.EMPTY;
}