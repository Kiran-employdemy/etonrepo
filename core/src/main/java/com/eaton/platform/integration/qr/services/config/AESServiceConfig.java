package com.eaton.platform.integration.qr.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eaton QR AES Service Config")
public @interface AESServiceConfig {

    @AttributeDefinition( name = "Algorithm Ex: AES")
    String algorithm() default "";

    @AttributeDefinition( name = "key")
    String key() default "";

    @AttributeDefinition( name = "Algorithm with padding Ex: AES/CBC/PKCS5Padding")
    String algo_padding() default "";

    @AttributeDefinition( name = "iv_parameter Ex: 12345678901234567890123456789012")
    String iv_parameter() default "";
}
