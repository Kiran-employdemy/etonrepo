package com.eaton.platform.integration.cad.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * This is a Javadoc comment
 * CadenasUrlServiceConfig interface
 */
@ObjectClassDefinition(name = "CadenasUrlServiceImpl")
public @interface CadenasUrlServiceConfig {
    @AttributeDefinition(
            name = "Cad Qualifier Url",
            description = "Cadenas Qualifier Url")
    String cadQualifierUrl() default "https://www.partcommunity.com/cadqualifier.asp?firm=eaton_cad";
    @AttributeDefinition(
            name = "Part Community Url",
            description = "Cadenas Part Community Url")
    String partCommunityUrl() default "https://webapi.partcommunity.com/cgi-bin/cgi2pview.exe";

}
