package com.eaton.platform.integration.auth.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EndecaSecretKeyConfiguration")
public @interface EndecaSecretKeyConfig {

    /**
     * AEM Secret Key
     * @return Secret Key */

    @AttributeDefinition(
            name = "AEM Secret Key",
            description = "Enter Base64 encoded Secret Key."
            )
    String secret_key() default "";
}
