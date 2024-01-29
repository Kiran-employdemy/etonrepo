package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EatonEmailServiceImpl")
public @interface EatonEmailServiceConfig {

    @AttributeDefinition(
            name = "from Address",
            description = "Sender Email Address")
    String fromAddress() default "noreply-EATONcatalog@eaton.com";

    @AttributeDefinition(
            name = "sender Name",
            description = "Sender Name ")
    String senderPreName() default "Eaton.com";
}