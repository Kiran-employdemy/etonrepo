package com.eaton.platform.integration.auth.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.config.AuthenticationServiceConfig;

@Component(
        service = AuthenticationServiceConfiguration.class,
        immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "Authentication - OKTA Service Configuration",
                AEMConstants.PROCESS_LABEL + "AuthenticationServiceConfiguration"
        })
@Designate(ocd = AuthenticationServiceConfig.class)
public class AuthenticationServiceConfigurationImpl implements AuthenticationServiceConfiguration {

        private String oktaEndpointRoot;
        private String oktaClientID;
        private String oktaClientSecret;
        private String oktaRegistrationFlag;
        private String oktaRedirectURI;
        private String oktaLoginURI;
		private String oktaWidgetLoginURI;
        private String oktaIssuer;
        private String oktaAudience;
        private int oktaConnectionTimeout;
        private int oktaRetryAttempts;
        private String oktaPasswordLessAuthFlag;
        private String oktaRememberMeFlag;
        private String oktaidpDiscoveryFlag;
		private String oktaidpDiscoveryValue;
        private String oktaKeyURI;
        private int oktaJwtExpirationLeeway;

        private String defaultPostAuthRedirectURL;
        private String defaultPostSignOutRedirectURL;
        private String userLookupUrl;
        private String trackDownloadEndpointUrl;
		private String myeatonOktaSamlURL;
        private String myeatonNsRole;
        private int maxConnections;
        private int maxHostConnections;
        private int maxCacheSize;
        private String userLookupAPIUsername;
        private String userLookupAPIPassword;
        private String tokenUrl;
        private int userProfileCacheTTL;
        private int securityCookieTTL;
        private String securityCookieId;
        private String securityCookieDomain;
        private String ssoCookieDomain;
        private String orderCenterSSOLink;
        private String orderCenterAppRole;
        private String bidManagerSSOLink;
        private String bidManagerAppRole;
        private String myEatonApplicationAttr;
        private String oktaWidgetVersion;
        private String devPortalOktaLoginURI;
        @Activate
        @Modified
        protected  final void activate(final AuthenticationServiceConfig config) {
                if (config != null) {
                        oktaEndpointRoot = config.oktaEndpointRoot();
                        defaultPostSignOutRedirectURL = config.defaultPostSignOutRedirectURI();
                        oktaClientID = config.oktaClientID();
                        oktaClientSecret = config.oktaClientSecret();
                        oktaRegistrationFlag = config.oktaRegistrationFlag();
                        oktaRedirectURI = config.oktaRedirectURI();
                        oktaLoginURI = config.oktaLoginURI();
						oktaWidgetLoginURI = config.oktaWidgetLoginURI();
                        oktaIssuer = config.oktaIssuer();
                        oktaAudience = config.oktaAudience();
                        oktaConnectionTimeout = config.oktaConnectionTimeout();
                        oktaRetryAttempts = config.oktaRetryAttempts();
                        oktaPasswordLessAuthFlag = config.oktaPasswordLessAuthFlag();
                        oktaRememberMeFlag = config.oktaRememberMeFlag();
                        oktaidpDiscoveryFlag = config.oktaidpDiscoveryFlag();
						oktaidpDiscoveryValue = config.oktaidpDiscoveryValue();
                        oktaKeyURI = config.oktaKeyURI();
                        oktaJwtExpirationLeeway = config.oktaJwtExpirationLeeway();
                        userLookupUrl = config.userLookupEndpoint();
                        trackDownloadEndpointUrl = config.trackingLookupEndpoint();
                        maxConnections = config.maxConnections();
                        maxHostConnections = config.maxHostConnections();
                        maxCacheSize = config.maxCacheSize();
                        userLookupAPIUsername = config.userLookupUsername();
                        userLookupAPIPassword = config.userLookupPassword();
                        tokenUrl = config.tokenEndpoint();
                        userProfileCacheTTL = config.userProfileCacheTTL();
                        securityCookieTTL = config.securityCookieTTL();
                        securityCookieId = config.securityCookieId();
                        securityCookieDomain = config.securityCookieDomain();
                        ssoCookieDomain = config.ssoCookieDomain();
                        defaultPostAuthRedirectURL=config.defaultPostAuthRedirectURI();
						myeatonOktaSamlURL = config.myeatonOktaSamlURI();
                        myeatonNsRole = config.myeatonNsRole();
                        orderCenterSSOLink = config.orderCenterSSOLink();
                        bidManagerSSOLink = config.bidManagerSSOLink();
                        orderCenterAppRole = config.orderCenterAppRole();
                        bidManagerAppRole = config.bidManagerAppRole();
                        myEatonApplicationAttr = config.myEatonApplicationAttr();
                        oktaWidgetVersion = config.oktaWidgetVersion();
                        devPortalOktaLoginURI = config.devPortalOktaLoginURI();;
                }
        }

        @Override
        public String getOktaEndpointRoot() {
                return oktaEndpointRoot;
        }
        @Override
        public String getOktaIssuer() {
                return oktaIssuer;
        }
        @Override
        public String getOktaAudience() {
                return oktaAudience;
        }
        @Override
        public int getOktaConnectionTimeout() {
                return oktaConnectionTimeout;
        }

        @Override
        public int getOktaRetryAttempts() {
                return oktaRetryAttempts;
        }

        @Override
        public String getOktaClientID() { return oktaClientID; }

        @Override
        public String getOktaClientSecret() { return oktaClientSecret; }

        @Override
        public String getOktaRegistrationFlag() { return oktaRegistrationFlag; }

        @Override
        public String getOktaRedirectURI() { return oktaRedirectURI; }

        @Override
        public String getOktaLoginURI() { return oktaLoginURI; }

		@Override
		public String getOktaWidgetLoginURI() { return oktaWidgetLoginURI; }

        @Override
        public String getOktaPasswordLessAuthFlag() { return oktaPasswordLessAuthFlag; }

        @Override
        public String getOktaRememberMeFlag() { return oktaRememberMeFlag; }

        @Override
        public String getOktaidpDiscoveryFlag() { return oktaidpDiscoveryFlag; }

		@Override
        public String getOktaidpDiscoveryValue() { return oktaidpDiscoveryValue; }

        @Override
        public String getOktaKeyURI() { return oktaKeyURI; }

        @Override
        public int getOktaJwtExpirationLeeway() {
                return oktaJwtExpirationLeeway;
        }

        @Override
        public int getMaxConnections() {
                return maxConnections;
        }

        @Override
        public int getMaxHostConnections() {
                return maxHostConnections;
        }

        @Override
        public int getMaxCacheSize() {
                return maxCacheSize;
        }

        @Override
        public String getUserLookupUrl() {
                return userLookupUrl;
        }

        @Override
        public String getTrackDownloadEndpointUrl() {
                return trackDownloadEndpointUrl;
        }

        @Override
        public String getUserLookupAPIUsername() {
                return userLookupAPIUsername;
        }

        @Override
        public String getUserLookupAPIPassword() {
                return userLookupAPIPassword;
        }

        @Override
        public String getTokenUrl() {
                return tokenUrl;
        }

        @Override
        public int getUserProfileCacheTTL() { return userProfileCacheTTL; }

        @Override
        public int getSecurityCookieTTL() { return securityCookieTTL; }

        @Override
        public String getSecurityCookieId() { return securityCookieId; }

        @Override
        public String getSecurityCookieDomain() { return securityCookieDomain; }

        @Override
		public String getSsoCookieDomain() {return ssoCookieDomain;	}

        @Override
		public String getDefaultPostAuthRedirectURI() {	return defaultPostAuthRedirectURL;	}

		@Override
		public String getMyeatonOktaSamlURI() {	return myeatonOktaSamlURL;	}

        @Override
		public String getMyeatonNsRole() {	return myeatonNsRole;	}

        @Override
        public String getOrderCenterSSOLink() { return orderCenterSSOLink; }

        @Override
        public String getBidManagerSSOLink() { return bidManagerSSOLink; }

        @Override
        public String getBidManagerAppRole() { return bidManagerAppRole; }

        @Override
        public String getOrderCenterAppRole() { return orderCenterAppRole; }

        @Override
        public String getDefaultPostSignOutRedirectURL() { return defaultPostSignOutRedirectURL; }

        @Override
        public String getMyEatonApplicationAttr() { return myEatonApplicationAttr; }

        @Override
        public String getOktaWidgetVersion() {
                return oktaWidgetVersion;
        }

        @Override
        public String devPortalOktaLoginURI() {
                return devPortalOktaLoginURI;
        }

}
