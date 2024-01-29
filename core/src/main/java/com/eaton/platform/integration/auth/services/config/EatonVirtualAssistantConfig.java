package com.eaton.platform.integration.auth.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EatonVirtualAssistantConfiguration")
public @interface EatonVirtualAssistantConfig {

    @AttributeDefinition(
            name = "Name of user id cookie",
            description = "Name for user id cookie"
    )
    String userIdCookieName() default "eatonUserIDFinal";


    @AttributeDefinition(
            name = "Domain of user id cookie",
            description = "Domain for user id cookie"
    )
    String userIdCookieDomain() default "";

    @AttributeDefinition(
            name = "Time to live of user id cookie (seconds)",
            description = "Time to live of user id cookie (seconds)"
    )
    int userIdCookieTTL() default 86400;

    @AttributeDefinition(
            name = "Name of vista id cookie",
            description = "Name for vista id cookie"
    )
    String vistaIdCookieName() default "eatonICCVistaUid";

    @AttributeDefinition(
            name = "Domain of vista id cookie",
            description = "Domain for vista id cookie"
    )
    String vistaIdCookieDomain() default "";

    @AttributeDefinition(
            name = "Time to live of vista id cookie (seconds)",
            description = "Time to live of vista id cookie (seconds)"
    )
    int vistaIdCookieTTL() default 86400;

    @AttributeDefinition(
            name = "Json Name of vista id value",
            description = "Json name of vista id value (this is within the vista id cookie json string)"
    )
    String vistaIdJsonName() default "eatonICCVistaUid";

    @AttributeDefinition(
            name = "Json Name of drc id value",
            description = "Json name of drc id value (this is within the vista id cookie json string)"
    )
    String drcIdJsonName() default "eatonDrcId";

    @AttributeDefinition(
            name = "Virtual assistant history cookie name",
            description = "Cookie name of the virtual assistant history"
    )
    String historyCookieName() default "bftoken";

    @AttributeDefinition(
            name = "Virtual assistant history domain name",
            description = "Domain name of the virtual assistant history"
    )
    String historyCookieDomain() default "";

    @AttributeDefinition(
            name = "Virtual assistant iv security cookie",
            description = "Should the iv security cookie be set for Eaton Virtual Assistant"
    )
    boolean isIvSecurityCookieSet() default true;

    @AttributeDefinition(
            name = "Virtual assistant iv security cookie cookie name",
            description = "Cookie name of the virtual assistant iv security cookie"
    )
    String ivSecurityCookieName() default "eatonCRVS";

    @AttributeDefinition(
            name = "Virtual assistant iv security cookie domain name",
            description = "Domain name of the virtual assistant iv security cookie"
    )
    String ivSecurityCookieDomain() default "";

    @AttributeDefinition(
            name = "Time to live of iv security cookie (seconds)",
            description = "Time to live of iv security cookie (seconds)"
    )
    int ivSecurityCookieTTL() default 86400;

    @AttributeDefinition(
            name = "Virtual assistant iv security cookie value",
            description = "Value of the virtual assistant iv security cookie"
    )
    String ivSecurityCookieValue() default "true";

    @AttributeDefinition(
            name = "Encryption Type",
            description = "Type of encryption for user id cookies"
    )
    String encryptionAlgorithm() default "DES";

    @AttributeDefinition(
            name = "Encryption Mode",
            description = "Mode of encryption for user id cookies"
    )
    String encryptionMode() default "DES/CBC/PKCS5Padding";

    @AttributeDefinition(
            name = "Encryption Key",
            description = "Private key to encrypt virtual assistant cookies"
    )
    String encryptionKey() default "ezf38swvhj6ui9on7lk5";
}
