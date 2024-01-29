package com.eaton.platform.integration.auth.services;

public interface AuthenticationServiceConfiguration {

    String getOktaEndpointRoot();

    String getOktaClientID();

    String getOktaClientSecret();

    String getOktaRegistrationFlag();

    String getOktaRedirectURI();

    String getOktaLoginURI();

	String getOktaWidgetLoginURI();

    String getOktaIssuer();

    String getOktaAudience();

    int getOktaConnectionTimeout();

    int getOktaRetryAttempts();

    String getOktaPasswordLessAuthFlag();

    String getOktaidpDiscoveryFlag();

	String getOktaidpDiscoveryValue();

    String getOktaRememberMeFlag();

    String getOktaKeyURI();

    int getOktaJwtExpirationLeeway();

    String getDefaultPostAuthRedirectURI();

	String getMyeatonOktaSamlURI();

    String getMyeatonNsRole();

    String getUserLookupUrl();

    int getMaxConnections();

    int getMaxHostConnections();

    int getMaxCacheSize();

    String getUserLookupAPIUsername();

    String getUserLookupAPIPassword();

    String getTokenUrl();

    int getUserProfileCacheTTL();

    int getSecurityCookieTTL();

    String getSecurityCookieId();

    String getSecurityCookieDomain();

    String getSsoCookieDomain();

    String getOrderCenterSSOLink();

    String getBidManagerSSOLink();

    String getOrderCenterAppRole();

    String getBidManagerAppRole();

    String getDefaultPostSignOutRedirectURL();

    String getTrackDownloadEndpointUrl();

    String getMyEatonApplicationAttr();

    String getOktaWidgetVersion();
    String devPortalOktaLoginURI();
}
