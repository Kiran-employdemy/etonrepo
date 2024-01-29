package com.eaton.platform.integration.auth.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.eaton.platform.core.constants.CommonConstants;

/** Configuration for Authentication services */

@ObjectClassDefinition(name = "AuthenticationService")
public @interface AuthenticationServiceConfig {

    int ONE_HUNDRED = 100;
    int ONE_THOUSAND = 1000;
    int SIXTY = 60;

    /**
     * Okta Endpoint Root Config
     * @return Okta Endpoint Root */

    @AttributeDefinition(
            name = "Okta Endpoint URL Root",
            description = "Root endpoint for Okta API Calls")
    String oktaEndpointRoot() default "https://eaton.oktapreview.com";

    @AttributeDefinition(
            name = "Okta Client ID",
            description = "Okta Client ID for Okta API Calls")
    String oktaClientID() default "0oav6hov1qfjQhdpM0h7";

    @AttributeDefinition(
            name = "Okta Client Secret",
            description = "Okta Client Secret for decrypt JWTToken")
    String oktaClientSecret() default "4H1gY7two8XMTO8KKKourCOLL2VKbC9wXyLWC49B";

    @AttributeDefinition(
            name = "Okta Registration Flag",
            description = "Okta Registration Flag for OKTA widget")
    String oktaRegistrationFlag() default CommonConstants.FALSE;

    @AttributeDefinition(
            name = "Okta Redirect URL",
            description = "Okta Redirect URL for OKTA widget")
    String oktaRedirectURI() default "http://localhost:4502/eaton/authorization";

    @AttributeDefinition(
            name = "Okta Login URL",
            description = "Okta Login URL for OKTA widget")
    String oktaLoginURI() default "";

	@AttributeDefinition(
			name = "Okta Widget Login URL",
			description = "Okta Widget Login URL for OKTA widget")
	String oktaWidgetLoginURI() default "";

    @AttributeDefinition(
            name = "Dev Portal Okta Login URL",
            description = "Used to redirect none authenticated user to Okta login if user attempt to access Developer Portal secure page"
    )
    String devPortalOktaLoginURI() default "";

    @AttributeDefinition(
            name = "Okta Issuer URL",
            description =
                    "URL used for JWT verification")
    String oktaIssuer() default "https://id-qa.eaton.com/oauth2/auswm0hll1dz7SaUw0h7";

    @AttributeDefinition(
            name = "Okta Audience",
            description =
                    "Audience for JWT verification")
    String oktaAudience() default "api://default";

    @AttributeDefinition(
            name = "Okta Connection Timeout",
            description =
                    "Connection timeout in seconds")
    int oktaConnectionTimeout() default 30;

    @AttributeDefinition(
            name = "Okta Retry Attempts",
            description =
                    "Number of retries Okta will make")
    int oktaRetryAttempts() default 3;

    @AttributeDefinition(
            name = "Okta passwordlessAuth Flag",
            description = "Okta Password less Auth Flag for OKTA widget")
    String oktaPasswordLessAuthFlag() default CommonConstants.TRUE;

    @AttributeDefinition(
            name = "Okta OktaRememberMeFlag",
            description = "Okta OktaRememberMeFlag for OKTA widget")
    String oktaRememberMeFlag() default "";

    @AttributeDefinition(
            name = "Okta idpDiscovery Flag",
            description = "Okta idp Discovery Flag for OKTA widget")
    String oktaidpDiscoveryFlag() default CommonConstants.TRUE;

	@AttributeDefinition(
            name = "Okta idpDiscovery Value",
            description = "Okta idp Discovery Value for OKTA widget")
    String oktaidpDiscoveryValue() default "";

    @AttributeDefinition(
            name = "Okta Public Key URL",
            description = "Okta Endpoint for retrieving public keys")
    String oktaKeyURI() default "https://eaton.oktapreview.com/oauth2/default/v1/keys";

    @AttributeDefinition(
            name = "Okta JWT Expiration Leeway",
            description = "The length of leeway (in seconds) allowed when validating jwt expiration")
    int oktaJwtExpirationLeeway() default 180;

    @AttributeDefinition(
            name = "Default Post Authentication Redirect URL",
            description = "Default Post Authentication Redirect URL")
    String defaultPostAuthRedirectURI() default "http://localhost:4502/content/eaton/us/en-us/secure/dashboard.html";

    @AttributeDefinition(
            name = "Default Post SignOut Redirect URL",
            description = "Default Post SingOut Redirect URL")
    String defaultPostSignOutRedirectURI() default "http://localhost:4502/content/eaton/us/en-us.html";

	@AttributeDefinition(
            name = "Default MyEaton SAML SSO URL",
            description = "Default MyEaton SAML SSO URL")
    String myeatonOktaSamlURI() default "";

    @AttributeDefinition(
            name = "MyEaton NsRole for SAML SSO ",
            description = "MyEaton NsRole for SAML SSO")
    String myeatonNsRole() default "";

    /**
     * trackProfile Lookup Endpoint Config
     * @return trackProfile Lookup Endpoint */
    @AttributeDefinition(
            name = "Track Download URL Part",
            description = "Service URL part for user lookup")
    String trackingLookupEndpoint() default "http://portalapps-dev2.tcc.etn.com:8888/portal-access-api/audit/v1/download/tracking";

    /**
     * User Lookup Endpoint Config
     * @return User Lookup Endpoint */
    @AttributeDefinition(
            name = "User Lookup URL Part",
            description = "Service URL part for user lookup")
    String userLookupEndpoint() default "http://portalapps-dev2.tcc.etn.com:8888/portal-access-api/user-management/v1/users";

    /**
     * User Lookup API Username Config
     * @return Service Username */
    @AttributeDefinition(
            name = "User Lookup API Username",
            description = "Username for User Lookup API")
    String userLookupUsername() default "PAA_D3V#128!";

    /**
     * User Lookup API  Password Config
     * @return Service Password */
    @AttributeDefinition(
            name = "User Lookup API  Password",
            description =
                    "Password for User Lookup API. Should be encrypted with crypto service",
            type = AttributeType.PASSWORD)
    String userLookupPassword() default
            "devRESTap1s836@portal";

    /**
     * Max Connections Config
     * @return Max Connections */
    @AttributeDefinition(
            name = "Max Connections",
            description =
                    "Defines the maximum number of connections allowed overall. This value only applies to"
                            + " the number of connections from a particular instance of HttpConnectionManager.")
    int maxConnections() default ONE_HUNDRED;

    /**
     * Max Host Connections Config
     * @return Max Host Connections */
    @AttributeDefinition(
            name = "Max Host Connections",
            description =
                    "Defines the maximum number of connections allowed per host configuration. These values"
                            + " only apply to the number of connections from a particular instance of"
                            + " HttpConnectionManager.")
    int maxHostConnections() default ONE_HUNDRED;

    /**
     * Max Cache Size Config
     * @return Max Cache Size */
    @AttributeDefinition(
            name = "Max Cache Size",
            description = "Defines the maximum of the cached response before invalidating the oldest.")
    int maxCacheSize() default ONE_THOUSAND;

    /**
     * Get Token Endpoint Config
     * @return User Lookup Endpoint */
    @AttributeDefinition(
            name = "Get Token URL Part",
            description = "Service URL part for Get Token")
    String tokenEndpoint() default "https://id-qa.eaton.com/oauth2/auswm0hll1dz7SaUw0h7/v1/token";

    @AttributeDefinition(
            name = "User Profile Cache TTL",
            description =
                    "The length of time the user profile cache should exist (Seconds)")
    int userProfileCacheTTL() default 1800;

    @AttributeDefinition(
            name = "Security Cookie TTL",
            description =
                    "The length of time the security cookie should exist (Seconds)")
    int securityCookieTTL() default 1800;

    @AttributeDefinition(
            name = "SecurityCookie ID",
            description = "The ID of the security cookie")
    String securityCookieId() default "etn-login";

    @AttributeDefinition(
            name = "SecurityCookie Domain",
            description = "The Domain of the security cookie")
    String securityCookieDomain() default "";

    @AttributeDefinition(
            name = "SSO Cookie Domain",
            description = "The Domain of the legacy SSO cookie")
    String ssoCookieDomain() default "etn.com";

    @AttributeDefinition(
            name = "Order Center Okta SSO",
            description = "Order Center Okta SSO Link")
    String orderCenterSSOLink() default "";

    @AttributeDefinition(
            name = "Order Center  LDAP App role",
            description = "Order Center  LDAP App role")
    String orderCenterAppRole() default "";

    @AttributeDefinition(
            name = "Bid Manager Okta SSO",
            description = "Bid Manager Okta SSO Link")
    String bidManagerSSOLink() default "";

    @AttributeDefinition(
            name = "Bid Manager LDAP App role",
            description = "Bid Manager LDAP App role")
    String bidManagerAppRole() default "";

    @AttributeDefinition(
            name = "Eaton SSO Application Attribute",
            description = "Eaton SSO Application Attribute value to determine if from my.eaton.com"
    )
    String myEatonApplicationAttr() default "";

    @AttributeDefinition(
            name = "Eaton Okta Widget Version",
            description = "Eaton Okta Widget version, default to 5.1.5"
    )
    String oktaWidgetVersion() default "5.1.5";
}
