package com.eaton.platform.integration.akamai.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eaton Akamai Service")
public @interface AkamaiConfig {

    @AttributeDefinition( name = "Base URL to send to Akamai")
    String base_url() default "https://www.eaton.com";

    @AttributeDefinition( name = "Akamai Host Url")
    String akamai_host() default "akab-z2mvxb46gck35oic-rqldw3ivl32ff4jk.purge.akamaiapis.net";

    @AttributeDefinition( name = "Akamai Access Token")
    String access_token() default "akab-6xyajldthlukfru7-nqnpornelvonijew";

    @AttributeDefinition( name = "Akamai Client Token")
    String client_token() default "akab-ue3z7mcjpxh2xfq6-ollme4xqg3vq3433";

    @AttributeDefinition( name = "Akamai Client Secret")
    String client_secret() default "3WNnFNcXToxEDAfrq14cLBH7RqjEvvSZGGuPN0NTLGw=";

    @AttributeDefinition( name = "Akamai Transport Handler Delay (Seconds)",
            description = "This is a delay in seconds after the request is received before the request is sent to Akamai in order to ensure it happens AFTER dispatcher and varnish requests.")
    int transport_delay() default 2;

}
