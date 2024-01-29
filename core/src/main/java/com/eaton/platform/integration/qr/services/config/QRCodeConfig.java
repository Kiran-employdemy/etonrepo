package com.eaton.platform.integration.qr.services.config;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eaton QR code Service")
public @interface QRCodeConfig {

    @AttributeDefinition( name = "Config google api for Geo location")
    String google_api_key() default "";

    @AttributeDefinition( name = "QR validation URL(validateSerial)")
    String qr_validation_url() default "";
    
    @AttributeDefinition( name = "QR validation URL(validateAuthCode)")
    String qr_authCode_validation_url() default "";
	@AttributeDefinition( name = "QR report issue URL(validateSerial)")
    String qr_reportissue_url() default "";

    @AttributeDefinition( name = "QR oauth accessToken URL")
    String qr_oauth_accessToken_url() default "";

    @AttributeDefinition( name = "QR validation api key")
    String qr_validation_apikey() default "";

    @AttributeDefinition( name = "QR validation api secret")
    String qr_validation_apisecret() default "";

    @AttributeDefinition( name="Default Serial number Authenticated message.")
    String serial_num_auth_msg() default "";

    @AttributeDefinition( name="Default Catalog Number")
    String catalog_number() default "";

    @AttributeDefinition( name = "Default Authentication ToolType")
    String authentication_tool_type();

    @AttributeDefinition( name = "Akamai Geolocation Header")
    String geolocation_akamai_header();

    @AttributeDefinition(name = "Attribute Names From Request Header", description = "Enter value in following format : Country = country_code")
    String [] request_header_attributes();

    @AttributeDefinition( name = "Akamai True Client IP Header")
    String true_client_ip_header();

    @AttributeDefinition( name = "Country Code to Country Name Mapping Json File Path")
    String country_code_mapping();

    @AttributeDefinition( name="Default Scan Image")
    String scan_image() default "";

    @AttributeDefinition( name="Default Registered User")
    String registered_user() default "";

    @AttributeDefinition( name="Default UserId")
    String user_id() default "";

    @AttributeDefinition( name="Default Language")
    String language() default "";

    @AttributeDefinition(name = "Token Cache", description = "Token Cache")
    String accessTokenCache() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Token Cache Size", description = "Token Cache Size")
    int accessTokenCacheSize() default 5;

    @AttributeDefinition(name = "Token Cache Duration (minutes)", description = "Access Token Cache Duration (minutes)")
    int accessTokenCacheDuration() default 50;

    @AttributeDefinition(name = "Response Cache", description = "Response Cache")
    String responseCache() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Response Cache Size", description = "Response Cache Size")
    int responseCacheSize() default 1000;

    @AttributeDefinition(name = "Response Cache Duration (hours)", description = "Response Cache Duration (hours)")
    int responseCacheDuration() default 24;

    @AttributeDefinition(name = "Api timeout (seconds)", description = "Api timeout (seconds)")
    int responseTimeout() default 30;

    @AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
    int maxConnections() default 200;

    @AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values only apply to the number of connections from a particular instance of HttpConnectionManager.")
    int maxHostConnections() default 200;

}

