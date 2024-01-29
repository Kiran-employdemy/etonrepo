package com.eaton.platform.integration.eloqua.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EloquaServiceImpl")
public @interface EloquaServiceConfig {

    @AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
    int maxConnections() default 100;

    @AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values only apply to the number of connections from a particular instance of HttpConnectionManager.")
    int maxHostConnections() default 100;

    @AttributeDefinition(name = "Do Proxy", description = "If checked this will put a proxy between the form submission and Eloqua that will validate Google reCAPTCHA.")
    boolean doProxy() default true;

    @AttributeDefinition(name = "Default Redirect", description = "The fallback redirect URL if there is no redirect set in Eloqua, AEM, or the header region.")
    String defaultRedirect() default "https://eaton.com";

    @AttributeDefinition(name = "Total GUID Timeout (seconds)", description = "Total seconds to look for GUID before timeout. Half this amount will be for first party guid, the other half will be for third party guid (if first party not found).")
    int guidTimeout() default 20;

}