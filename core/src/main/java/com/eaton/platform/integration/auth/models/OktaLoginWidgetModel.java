package com.eaton.platform.integration.auth.models;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.URISyntaxException;


@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class OktaLoginWidgetModel {


    public static final String JAVA_SCRIPT_FILE_TEMPLATE = "https://global.oktacdn.com/okta-signin-widget/%s/js/okta-sign-in.min.js";
    public static final String CSS_FILE_TEMPLATE = "https://global.oktacdn.com/okta-signin-widget/%s/css/okta-sign-in.min.css";
    /**
     * The Default language fallback.
     **/
    @Inject
    @Via("resource")
    @Default(values = "en")
    private String defaultLanguage;

    /**
     * The Primary auth title.
     **/
    @Inject
    @Via("resource")
    @Default(values = " ")
    private String primaryauth_title;

    /**
     * The Primary auth submit title.
     **/
    @Inject
    @Via("resource")
    private String primaryauth_submit;

    /**
     * Username Placeholder.
     **/
    @Inject
    @Via("resource")
    @Default(values = "Email")
    private String primaryauth_username_placeholder;

    /**
     * Password Placeholder.
     **/
    @Inject
    @Via("resource")
    @Default(values = "Password")
    private String mfa_challenger_password_placeholder;

    /**
     * Password Tooltip.
     **/
    @Inject
    @Via("resource")
    @Default(values = "Password")
    private String factor_password;

    /**
     * Remember Me.
     **/
    @Inject
    @Via("resource")
    @Default(values = "Remember Me")
    private String remember_me;

    /**
     * Form Next Button.
     **/
    @Inject
    @Via("resource")
    @Default(values = "Next")
    private String oform_next;

    /**
     * Form Verify Button.
     **/
    @Inject
    @Via("resource")
    @Default(values = "Verify")
    private String mfa_challenge_verify;

    /**
     * Form Error Banner Title.
     **/
    @Inject
    @Via("resource")
    @Default(values = "We found some errors. Please review the form and make corrections..")
    private String oform_errorbanner_title;

    /**
     * Form Error Username required
     **/
    @Inject
    @Via("resource")
    @Default(values = "Please enter a username")
    private String error_username_required;

    /**
     * Form Error password required
     **/
    @Inject
    @Via("resource")
    @Default(values = "Please enter a password")
    private String error_password_required;

    /**
     * Form Error password required
     **/
    @Inject
    @Via("resource")
    @Default(values = "Email")
    private String forgot_password_email_tooltip;

    /**
     * Form Error password required
     **/
    @Inject
    @Via("resource")
    @Default(values = "yourname@yourdomain.com")
    private String forgot_password_email_placeholder;

    /**
     * Form Error login locked
     **/
    @Inject
    @Via("resource")
    @Default(values = "Your account is locked due to too many authentication attempts. You can try to log in again after 15 minutes.")
    private String error_login_locked_multi_wrong_password_attempt;

    /**
     * The Authentication Configuration service.
     */
    @Inject
    protected AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Inject
    @Source("sling-object")
    private SlingHttpServletRequest slingRequest;

    @Inject
    @Source("sling-object")
    private SlingHttpServletResponse slingResponse;

    private String oktaEndpointRoot;

    private String oktaIssuer;

    private String oktaClientID;

    private String oktaRegistrationFlag;

    private String oktaRememberMeFlag;

    private String oktaRedirectURI;

    private String oktaPasswordLessAuthFlag;

    private String oktaidpDiscoveryFlag;

    private String oktaidpDiscoveryValue;

    private String ssoCookieDomain;

    private String oktaWidgetVersion;

    private static final Logger LOGGER = LoggerFactory.getLogger(OktaLoginWidgetModel.class);

    /**
     * Inits the.
     */
    @PostConstruct
    protected void init() {
        LOGGER.debug("OktaLoginWidgetModel :: init() :: Started");
        if (null != authenticationServiceConfiguration) {
            try {
                // check to see if we should set redirect cookie for login success redirect
                setRedirectCookie();
                oktaEndpointRoot = authenticationServiceConfiguration.getOktaEndpointRoot();
                oktaClientID = authenticationServiceConfiguration.getOktaClientID();
                oktaRedirectURI = authenticationServiceConfiguration.getOktaRedirectURI();
                oktaIssuer = authenticationServiceConfiguration.getOktaIssuer();
                oktaRegistrationFlag = authenticationServiceConfiguration.getOktaRegistrationFlag();
                oktaRememberMeFlag = authenticationServiceConfiguration.getOktaRememberMeFlag();
                oktaPasswordLessAuthFlag = authenticationServiceConfiguration.getOktaPasswordLessAuthFlag();
                oktaidpDiscoveryFlag = authenticationServiceConfiguration.getOktaidpDiscoveryFlag();
                oktaidpDiscoveryValue = authenticationServiceConfiguration.getOktaidpDiscoveryValue();
                ssoCookieDomain = authenticationServiceConfiguration.getSsoCookieDomain();
                oktaWidgetVersion = authenticationServiceConfiguration.getOktaWidgetVersion();

            } catch (URISyntaxException e) {
                LOGGER.error("URISyntaxException :: OktaLoginWidgetModel Authenticationservice config catch block: URISyntaxException: {}", e.getMessage(), e);

            } catch (Exception e) {
                LOGGER.error("OktaLoginWidgetModel Authenticationservice config catch block: exception", e.getMessage());
            }
        }
        LOGGER.debug(" OktaLoginWidgetModel :: int() :: Exit");
    }

    private void setRedirectCookie() throws URISyntaxException {
        String fromURI = slingRequest.getParameter("fromURI");
        LOGGER.debug("setRedirectCookie :: From URI :: {}", fromURI);
        if (StringUtils.isNotBlank(fromURI)) {
            AuthCookieUtil.setCookie(CommonConstants.LOGIN_PAGE_REDIRECT_COOKIE_ID, fromURI, authenticationServiceConfiguration.getSsoCookieDomain(), slingResponse);
        }
    }

    /**
     * Gets the OktaEndpoint.
     *
     * @return the Okta Endpoint URL
     */
    public String getOktaEndpointRoot() {
        return oktaEndpointRoot;
    }

    /**
     * Gets the oktaClientID.
     *
     * @return the Okta ClientID
     */
    public String getOktaClientID() {
        return oktaClientID;
    }

    /**
     * Gets the oktaRedirectURI.
     *
     * @return the Okta RedirectURI
     */
    public String getOktaRedirectURI() {
        return oktaRedirectURI;
    }

    /**
     * Gets the oktaIssuer.
     *
     * @return the Okta Issuer
     */
    public String getOktaIssuer() {
        return oktaIssuer;
    }

    /**
     * Gets the oktaRegistrationFlag.
     *
     * @return the Okta Registration Flag
     */
    public String getOktaRegistrationFlag() {
        return oktaRegistrationFlag;
    }

    /**
     * Gets the oktaRememberMeFlag.
     *
     * @return the Okta Remember Me Flag
     */
    public String getOktaRememberMeFlag() {
        return oktaRememberMeFlag;
    }

    /**
     * Gets the Okta Widget - Primary Auth title.
     *
     * @return the Okta Widget - Primary Auth title
     */
    public String getPrimaryauth_title() {
        return primaryauth_title;
    }

    /**
     * Gets the Okta Widget - Primary Submit button text.
     *
     * @return the Okta Widget - Primary Submit button text
     */
    public String getPrimaryauth_submit() {
        return primaryauth_submit;
    }

    /**
     * Gets the Okta Widget - Form Error Banner Title.
     *
     * @return the Okta Widget - Form Error Banner Title.
     */
    public String getOform_errorbanner_title() {
        return oform_errorbanner_title;
    }

    /**
     * Gets the Okta Widget - Form Error Username required..
     *
     * @return the Okta Widget -Form Error Username required..
     */
    public String getError_username_required() {
        return error_username_required;
    }

    /**
     * Gets the Okta Widget - Form Error password required..
     *
     * @return the Okta Widget - Form Error password required..
     */
    public String getError_password_required() {
        return error_password_required;
    }

    /**
     * Gets the Okta Widget - Username Placeholder..
     *
     * @return the Okta Widget - Username Placeholder..
     */
    public String getPrimaryauth_username_placeholder() {
        return primaryauth_username_placeholder;
    }

    /**
     * Gets the Okta Widget - Password Placeholder..
     *
     * @return the Okta Widget - Password Placeholder.
     */
    public String getMfa_challenger_password_placeholder() {
        return mfa_challenger_password_placeholder;
    }

    /**
     * Gets the oktaPasswordLessAuthFlag.
     *
     * @return the Okta Password Less Auth Flag
     */
    public String getOktaPasswordLessAuthFlag() {
        if (isOktaWidgetNew()) {
            return "false";
        }
        return oktaPasswordLessAuthFlag;
    }

    /**
     * Gets the oktaidpDiscoveryFlag.
     *
     * @return the Okta idp discovery Flag
     */
    public String getOktaidpDiscoveryFlag() {
        return oktaidpDiscoveryFlag;
    }

    /**
     * Gets the oktaidpDiscoveryValue.
     *
     * @return the Okta idp discovery Value
     */
    public String getOktaidpDiscoveryValue() {
        return oktaidpDiscoveryValue;
    }

    /**
     * Gets the Okta Widget - Password Tooltip.
     *
     * @return the Okta Widget - Password Tooltip.
     */
    public String getFactor_password() {
        return factor_password;
    }

    /**
     * Gets the Okta Widget - Remember Me.
     *
     * @return the Okta Widget - Remember Me.
     */
    public String getRemember_me() {
        return remember_me;
    }

    /**
     * Gets the Okta Widget - Next.
     *
     * @return the Okta Widget - Next.
     */
    public String getOform_next() {
        return oform_next;
    }

    /**
     * Gets the Okta Widget - Verify.
     *
     * @return the Okta Widget - Verify.
     */
    public String getMfa_challenge_verify() {
        return mfa_challenge_verify;
    }

    /**
     * Gets the default Langauge for the string fields
     *
     * @return
     */
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * @return current cookie domain
     */
    public String getSsoCookieDomain() {
        return ssoCookieDomain;
    }

    public String getForgot_password_email_tooltip() {
        return forgot_password_email_tooltip;
    }

    public String getForgot_password_email_placeholder() {
        return forgot_password_email_placeholder;
    }

    /**
     * Gets the Okta Widget - Form Error login locked..
     *
     * @return the Okta Widget -Form Error login locked..
     */
    public String getError_login_locked_multi_wrong_password_attempt() {
        return error_login_locked_multi_wrong_password_attempt;
    }

    public String getOktaWidgetJavaScript() {
        return String.format(JAVA_SCRIPT_FILE_TEMPLATE, oktaWidgetVersion);
    }

    public String getOktaWidgetStyleSheet() {
        return String.format(CSS_FILE_TEMPLATE, oktaWidgetVersion);
    }

    public boolean isOktaWidgetNew() {
        return !oktaWidgetVersion.equals("5.1.5");
    }
}
