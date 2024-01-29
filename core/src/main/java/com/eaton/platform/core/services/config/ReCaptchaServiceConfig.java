package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "ReCaptchaServiceImpl")
public @interface ReCaptchaServiceConfig {

    @AttributeDefinition( name = "Google ReCAPTCHA Validation URL", description = "The URL that validates Google ReCAPTCHA." )
    String validationUrl() default "https://www.google.com/recaptcha/api/siteverify";

    @AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
    int maxConnections() default 100;

    @AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values only apply to the number of connections from a particular instance of HttpConnectionManager.")
    int maxHostConnections() default 100;

}