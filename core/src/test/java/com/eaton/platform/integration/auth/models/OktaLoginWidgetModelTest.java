package com.eaton.platform.integration.auth.models;

import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import junitx.util.PrivateAccessor;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(value = { MockitoExtension.class })
public class OktaLoginWidgetModelTest {

    private static final String OKTA_WIDGET_VERSION = "7.10.1";
    public static final String OKTA_WIDGET_JS = "https://global.oktacdn.com/okta-signin-widget/7.10.1/js/okta-sign-in.min.js";
    public static final String OKTA_WIDGET_CSS = "https://global.oktacdn.com/okta-signin-widget/7.10.1/css/okta-sign-in.min.css";
    @InjectMocks
    OktaLoginWidgetModel oktaLoginWidgetModel;

    @Mock
	ResourceResolver resourceResolver;

    @Mock
    protected AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Mock
    private SlingHttpServletRequest slingRequest;

    MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resourceResolver);

    @Mock
    private SlingHttpServletResponse slingResponse;

    Logger logger = LoggerFactory.getLogger(getClass());

    public static final String OKTA_ENDPOINT_ROOT = "https://eaton.oktapreview.com";
    public static final String OKTA_CLIENTID = "0oav6hov1qfjQhdpM0h7";
    public static final String OKTA_REDIRECT_URI = "http://www-local.eaton.com/eaton/authorization";
    public static final String OKTA_ISSUER = "https://id-qa.eaton.com/oauth2/auswm0hll1dz7SaUw0h7";
    public static final String FLAG_FALSE = "false";
    public static final String FLAG_TRUE = "true";
    public static final String OKTA_PASSWORDLESS_AUTHFLAG = "unmodified";
    public static final String OKTAID_DISCOVERY_VALUE = "/home/oidc_client/0oaw0sqojgvnnSXGI0h7/aln5z7uhkbM6y7bMy0g7";
    public static final String SSO_COOKIE_DOMAIN = "eaton.com";

    String PRIMARYAUTH_TITLE ="title";
    String PRIMARYAUTH_SUBMIT = "submit";
    String OFORM_ERRORBANNER_TITLE = "We found some errors. Please review the form and make corrections..";
    String PRIMARYAUTH_USERNAME_PLACEHOLDER = "Email";
    String MFA_CHALLENGER_PASSWORD_PLACEHOLDER = "Password";
    String DEFAULTLANGUAGE = "en";
    String ERROR_LOGIN_LOCKED_MULTI_WRONG_PASSWORD_ATTEMPT = "Your account is locked due to too many authentication attempts. You can try to log in again after 15 minutes.";
    String ERROR_PASSWORD_REQUIRED = "Please enter a password";
    String ERROR_USERNAME_REQUIRED = "Please enter a username";
    String FACTOR_PASSWORD = "Factor Password";
    String REMEMBER_ME = "Remember Me";
    String OFORM_NEXT = "Next";
    String MFA_CHALLENGE_VERIFY = "Verify";
    String FORGOT_PASSWORD_EMAIL_TOOLTIP = " Forgot Email password";
    String FORGOT_PASSWORD_EMAIL_PLACEHOLDER = "yourname@yourdomain.com";

    @Test
    void testInitmetod() {

        when(authenticationServiceConfiguration.getOktaEndpointRoot()).thenReturn(OKTA_ENDPOINT_ROOT);
        when(authenticationServiceConfiguration.getOktaClientID()).thenReturn(OKTA_CLIENTID);
        when(authenticationServiceConfiguration.getOktaRedirectURI()).thenReturn(OKTA_REDIRECT_URI);
        when(authenticationServiceConfiguration.getOktaIssuer()).thenReturn(OKTA_ISSUER);
        when(authenticationServiceConfiguration.getOktaRegistrationFlag()).thenReturn(FLAG_FALSE);
        when(authenticationServiceConfiguration.getOktaRememberMeFlag()).thenReturn(FLAG_TRUE);
        when(authenticationServiceConfiguration.getOktaPasswordLessAuthFlag()).thenReturn(OKTA_PASSWORDLESS_AUTHFLAG);
        when(authenticationServiceConfiguration.getOktaidpDiscoveryFlag()).thenReturn(FLAG_TRUE);
        when(authenticationServiceConfiguration.getOktaidpDiscoveryValue()).thenReturn(OKTAID_DISCOVERY_VALUE);
        when(authenticationServiceConfiguration.getSsoCookieDomain()).thenReturn(SSO_COOKIE_DOMAIN);
        when(authenticationServiceConfiguration.getOktaWidgetVersion()).thenReturn(OKTA_WIDGET_VERSION);

        oktaLoginWidgetModel.init();

        assertEquals(OKTA_ENDPOINT_ROOT,oktaLoginWidgetModel.getOktaEndpointRoot());
        assertEquals(OKTA_CLIENTID,oktaLoginWidgetModel.getOktaClientID());
        assertEquals(OKTA_REDIRECT_URI,oktaLoginWidgetModel.getOktaRedirectURI());
        assertEquals(OKTA_ISSUER,oktaLoginWidgetModel.getOktaIssuer());
        assertEquals(FLAG_FALSE,oktaLoginWidgetModel.getOktaRegistrationFlag());
        assertEquals(FLAG_TRUE,oktaLoginWidgetModel.getOktaRememberMeFlag());
        assertEquals(FLAG_FALSE,oktaLoginWidgetModel.getOktaPasswordLessAuthFlag());
        assertEquals(FLAG_TRUE,oktaLoginWidgetModel.getOktaidpDiscoveryFlag());
        assertEquals(OKTAID_DISCOVERY_VALUE,oktaLoginWidgetModel.getOktaidpDiscoveryValue());
        assertEquals(SSO_COOKIE_DOMAIN,oktaLoginWidgetModel.getSsoCookieDomain());
        assertEquals(OKTA_WIDGET_JS,oktaLoginWidgetModel.getOktaWidgetJavaScript());
        assertEquals(OKTA_WIDGET_CSS,oktaLoginWidgetModel.getOktaWidgetStyleSheet());

    }
    @Test
    void testSetCookie() throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fromURI", "submitted");
        request.setParameterMap(parameters);
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "slingRequest", request);
            when(slingRequest.getParameter("fromURI")).thenReturn("submitted");
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting SetCookie: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(slingRequest.getParameter("fromURI"),"submitted");
    }

    @Test
    void testPrimaryauthTitle() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "primaryauth_title", PRIMARYAUTH_TITLE);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting primaryauth_title field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getPrimaryauth_title(),PRIMARYAUTH_TITLE);
    }

    @Test
    void testPrimaryauthSubmit() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "primaryauth_submit", PRIMARYAUTH_SUBMIT);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Primaryauth_submit field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getPrimaryauth_submit(),PRIMARYAUTH_SUBMIT);
    }

    @Test
    void testOformErrorbannerTitle() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "oform_errorbanner_title", OFORM_ERRORBANNER_TITLE);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Oform_errorbanner_title field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getOform_errorbanner_title(),OFORM_ERRORBANNER_TITLE);
    }

    @Test
    void testPrimaryauth_username_placeholder() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "primaryauth_username_placeholder", PRIMARYAUTH_USERNAME_PLACEHOLDER);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Primaryauth_username_placeholder field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getPrimaryauth_username_placeholder(),PRIMARYAUTH_USERNAME_PLACEHOLDER);
    }

    @Test
    void testMfa_challenger_password_placeholder() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "mfa_challenger_password_placeholder", MFA_CHALLENGER_PASSWORD_PLACEHOLDER);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Mfa_challenger_password_placeholder field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getMfa_challenger_password_placeholder(),MFA_CHALLENGER_PASSWORD_PLACEHOLDER);
    }

    @Test
    void testDefaultLanguage() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "defaultLanguage", DEFAULTLANGUAGE);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting DefaultLanguage field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getDefaultLanguage(),DEFAULTLANGUAGE);
    }

    @Test
    void testError_login_locked_multi_wrong_password_attempt() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "error_login_locked_multi_wrong_password_attempt", ERROR_LOGIN_LOCKED_MULTI_WRONG_PASSWORD_ATTEMPT);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Error_login_locked_multi_wrong_password_attempt field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getError_login_locked_multi_wrong_password_attempt(),ERROR_LOGIN_LOCKED_MULTI_WRONG_PASSWORD_ATTEMPT);
    }

    @Test
    void testError_password_required() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "error_password_required", ERROR_PASSWORD_REQUIRED);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Error_password_required field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getError_password_required(),ERROR_PASSWORD_REQUIRED);
    }

    @Test
    void testError_username_required() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "error_username_required", ERROR_USERNAME_REQUIRED);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Error_username_required field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getError_username_required(),ERROR_USERNAME_REQUIRED);
    }

    @Test
    void testFactor_password() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "factor_password", FACTOR_PASSWORD);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Factor_password field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getFactor_password(),FACTOR_PASSWORD);
    }

    @Test
    void testRemember_me() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "remember_me", REMEMBER_ME);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Remember_me field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getRemember_me(),REMEMBER_ME);
    }

    @Test
    void testOform_next() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "oform_next", OFORM_NEXT);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Oform_next field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getOform_next(),OFORM_NEXT);
    }

    @Test
    void testMfa_challenge_verify() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "mfa_challenge_verify", MFA_CHALLENGE_VERIFY);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Mfa_challenge_verify field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getMfa_challenge_verify(),MFA_CHALLENGE_VERIFY);
    }

    @Test
    void testForgot_password_email_tooltip() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "forgot_password_email_tooltip", FORGOT_PASSWORD_EMAIL_TOOLTIP);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Forgot_password_email_tooltip field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getForgot_password_email_tooltip(),FORGOT_PASSWORD_EMAIL_TOOLTIP);
    }

    @Test
    void testForgot_password_email_placeholder() throws URISyntaxException {
        try {
            PrivateAccessor.setField(oktaLoginWidgetModel, "forgot_password_email_placeholder", FORGOT_PASSWORD_EMAIL_PLACEHOLDER);
        } catch (NoSuchFieldException e) {
            logger.error("An error occurred while setting Forgot_password_email_placeholder field: {}", e.getMessage());
            e.printStackTrace();
        }
        assertEquals(oktaLoginWidgetModel.getForgot_password_email_placeholder(),FORGOT_PASSWORD_EMAIL_PLACEHOLDER);
    }

}


