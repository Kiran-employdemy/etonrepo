package com.eaton.platform.integration.auth.servlets;

import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.TermsAndConditionsService;
import com.eaton.platform.core.services.vanity.LocaleMappingService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.CookieFactory;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.EatonVirtualAssistantConfiguration;
import com.eaton.platform.integration.auth.services.UserProfileService;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


@Component(
        immediate = true,
        service = {Servlet.class},
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton Authorization Servlet",
                ServletConstants.SLING_SERVLET_PATHS + AuthorizationServlet.SERVLET_PATH,
                ServletConstants.SLING_SERVLET_METHODS_GET
        }
)
public class AuthorizationServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServlet.class);
    private static final long serialVersionUID = 142621054900875629L;
    public static final String SERVLET_PATH = "/eaton/authorization";
    public static final  String PUBLISH_SERVER_NAME = "publishServerName";

    @Reference
    protected transient AuthenticationService authenticationService;

    @Reference
    protected transient AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Reference
    protected transient EatonVirtualAssistantConfiguration eatonVirtualAssistantConfiguration;

    @Reference
    protected LocaleMappingService localeMappingService;

    @Reference
    protected UserProfileService profileService;

    @Reference
    protected transient TermsAndConditionsService termsAndConditionsService;

    @Reference
    protected Externalizer externalizer;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.debug("Start with doGet method :: AuthorizationServlet");

        boolean isAuthenticationSuccessful = false;
		boolean isNewMyEatonUser = false;
        final String loginPageURL = authenticationServiceConfiguration.getOktaLoginURI();
        final String defaultPostAuthRedirectURL = authenticationServiceConfiguration.getDefaultPostAuthRedirectURI();
		final String myeatonOktaSamlURL = authenticationServiceConfiguration.getMyeatonOktaSamlURI();
        final String oktaAuthorizationCode = request.getParameter(AuthConstants.AUTHORIZATION_CODE_PARAM);
		final String oktaIssuer = request.getParameter(AuthConstants.ISSUER_PARAM);
        final String stateConst = request.getParameter(AuthConstants.REDIRECT_URL_PARAM);
        LOG.debug("Authorization Code received in request :: {}", oktaAuthorizationCode);
		LOG.debug("Issuer received in request :: {}", oktaIssuer);
        String idpRedirectUrl ="";
        String stateStr="";
		Iterable<String> nsRolesList;
        AuthenticationToken authenticationToken = null;

        try {
				if(null != oktaIssuer && StringUtils.isNotEmpty(oktaIssuer)) {
					if(stateConst != null)
						stateStr = AuthConstants.AMP_CONST.concat(AuthConstants.REDIRECT_URL_PARAM).concat(stateConst);
					else
						stateStr = AuthConstants.AMP_CONST.concat(AuthConstants.REDIRECT_URL_PARAM).concat(AuthConstants.EQUAL_CONST).concat(AuthConstants.DEFAULT_STATE_CONST);
					 LOG.debug("Value of stateStr :: {}", stateStr);
					idpRedirectUrl = authenticationServiceConfiguration.getOktaIssuer().concat(AuthConstants.AUTH_CLIENTID_CONST).concat(authenticationServiceConfiguration.getOktaClientID()).concat(AuthConstants.AMP_CONST).concat(AuthConstants.REDIRECT_URI_KEY).concat(AuthConstants.EQUAL_CONST).concat(authenticationServiceConfiguration.getOktaRedirectURI()).concat(AuthConstants.RESP_CODE_CONST).concat(AuthConstants.SCOPE_CONST).concat(stateStr);
					if(StringUtils.isNotEmpty(idpRedirectUrl)) {
						LOG.debug("Redirecting to idpRedirectURL :: {}", idpRedirectUrl);
						response.sendRedirect(idpRedirectUrl);
					}else{
						LOG.error("Unable to redirect idpRedirectUrl.");
					}

				} else if (StringUtils.isNotEmpty(oktaAuthorizationCode)) {
					//Exchange the authorization token for JWT
					String rawJWT = authenticationService.requestToken(oktaAuthorizationCode);

					//Verify the JWT
                    authenticationToken = authenticationService.parseToken(rawJWT);
					if((rawJWT!=null) && (authenticationToken!=null) && (StringUtils.isNotEmpty(authenticationToken.getUserLDAPId()))) {
						//Encapsulate the JWT in eaton security cookie - Base 64 Encode?
						AuthCookieUtil.setJWTonAuthCookie(rawJWT, request, response, authenticationServiceConfiguration);
						AuthCookieUtil.setSSOUtilsCookie(authenticationToken, response, authenticationServiceConfiguration);
                        AuthCookieUtil.setEatonVirtualAssistantCookies(authenticationToken, response, eatonVirtualAssistantConfiguration);
                        if (externalizer != null) {
                        AuthCookieUtil.setCookie(PUBLISH_SERVER_NAME,externalizer.publishLink(request.getResourceResolver(),StringUtils.EMPTY),response);
                        }

                        LOG.debug("JWT encapsulated in Eaton Security Cookie");

						UserProfile userProfile = profileService.getUserProfile(authenticationToken.getUserLDAPId(), authenticationToken.getExpiration());
						if(userProfile != null){
							profileService.updateLastLogin(userProfile);
							nsRolesList = userProfile.getApplicationAccessTags();
							for(String nsRole: nsRolesList){
								LOG.debug("Nsrole Value for logged in user{}",nsRole);
								if(authenticationServiceConfiguration.getMyeatonNsRole().equalsIgnoreCase(nsRole))
								{
									isNewMyEatonUser = true;
									break;
								}

							}
						}else{
							LOG.error("UserProfile retrieved from API call is null");
						}
						isAuthenticationSuccessful = true;
					}else{
						LOG.error("Invalid Token received.");
					}
				}else{
					LOG.error("Invalid AuthorizationCode received.");

				}
			} catch (Exception e) {
				LOG.error("Exception occurred while authentication {}", e.getMessage(),e);
			}

        if(!isAuthenticationSuccessful){
            //Redirect to Login Page
            LOG.debug("Authentication failed.");
            if(StringUtils.isNotEmpty(loginPageURL)) {
                LOG.debug("Redirecting to Login Page :: {}", loginPageURL);
                response.sendRedirect(loginPageURL);
            }else{
                LOG.error("Unable to redirect to Login page as loginPageURL is null.");
            }
        }else{
            //Redirect to Home Page
            LOG.debug("******* do not need to redirect user to tc page: {} ********");
            LOG.debug("Authentication successful.");
            String postAuthRedirectURL = computePostAuthRedirectUrl(request, response);
            LOG.debug("RedirectingURL after getting from login-page-redirect Cookie: {}", postAuthRedirectURL);
            if(StringUtils.isNotEmpty(postAuthRedirectURL)) {
                postAuthRedirectURL = CommonUtil.dotHtmlLink(postAuthRedirectURL,request.getResourceResolver());
                LOG.debug("Redirecting to referred Page :: {}", postAuthRedirectURL);
                if(isNewMyEatonUser) {
                    if(null != termsAndConditionsService && termsAndConditionsService.shouldRedirect(authenticationToken)) {
                        LOG.debug("*** handling tc redirect from if ***");
                        handleTermsAndConditionsRedirect(request,response);
                    }else {
                        if(isFromMyEaton(postAuthRedirectURL)) {
                            // it's from my.eaton.com, so redirect user to secure dashboard
                            LOG.debug("Is from my.eaton.com and new user role so redirecting to secure dashboard");
                            response.sendRedirect(getLocaleSpecificDashboardUrl(defaultPostAuthRedirectURL,request));
                        } else {
                            response.sendRedirect(postAuthRedirectURL);
                        }
                    }
                } else {
                    response.sendRedirect(postAuthRedirectURL);
                }
            } else if(StringUtils.isNotEmpty(defaultPostAuthRedirectURL)) {
                LOG.debug("Redirecting to Home Page :: {}", defaultPostAuthRedirectURL);
                final String redirectUrl = getLocaleSpecificDashboardUrl(defaultPostAuthRedirectURL, request);
                if(!isNewMyEatonUser) {
                    response.sendRedirect(myeatonOktaSamlURL);
                }else {
                    if(null != termsAndConditionsService && termsAndConditionsService.shouldRedirect(authenticationToken)) {
                        LOG.debug("*** handling tc redirect from else if ***");
                        handleTermsAndConditionsRedirect(request,response);
                    }else {
                        response.sendRedirect(redirectUrl);
                    }
                }
            }else{
                LOG.error("Unable to redirect to Home page as redirectURL is null.");
            }

        }

        LOG.debug("End with doGet method :: AuthorizationServlet");
    }

    private static String computePostAuthRedirectUrl(SlingHttpServletRequest request, SlingHttpServletResponse response) throws MalformedURLException {
        Cookie loginPageRedirectCookie = request.getCookie(CookieFactory.LOGIN_PAGE_REDIRECT_COOKIE_NAME);
        CookieFactory cookieFactory = new CookieFactory();
        if (loginPageRedirectCookie != null) {
            response.addCookie(cookieFactory.loginRedirectCookieForDeletion(request, loginPageRedirectCookie.getValue()));
            return ifPresentReplaceWithEatonRedirectCookieValue(request, loginPageRedirectCookie.getValue());
        }
        Cookie deepLinkRedirectCookie = request.getCookie(CookieFactory.DEEP_LINK_REDIRECT_COOKIE_NAME);
        if (deepLinkRedirectCookie != null) {
            return ifPresentReplaceWithEatonRedirectCookieValue(request, deepLinkRedirectCookie.getValue());
        }
        return null;
    }

    private static String ifPresentReplaceWithEatonRedirectCookieValue(SlingHttpServletRequest request, String redirectUrl) {
        Cookie etnRedirectCookie = request.getCookie("etn_redirect_cookie");
        if (etnRedirectCookie != null && StringUtils.isNotEmpty(redirectUrl)) {
            return redirectUrl.replaceAll("(/[a-z]{2}/[a-z]{2}-[a-z]{2})"
                    , URLDecoder.decode(Objects.requireNonNull(etnRedirectCookie.getValue()), StandardCharsets.UTF_8));
        }
        return redirectUrl;
    }

    private void handleTermsAndConditionsRedirect(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Resource resource = request.getResource();
        LOG.debug("Resource path: {} ****** resource type: {}",resource.getPath(),resource.getResourceType());
        String tcPath = termsAndConditionsService.getTermAndConditionPath(request);
        String redirectPath = CommonUtil.dotHtmlLink(CommonUtil.dotHtmlLink(tcPath),request.getResourceResolver());
        LOG.debug("******* tc path: {} ********",tcPath);
        LOG.debug("******* redirect path: {} ********",redirectPath);

        // meaning user need to be redirect
        response.sendRedirect(redirectPath);
    }

    private boolean isFromMyEaton(String url) {
        String myEatonAppAttr = authenticationServiceConfiguration.getMyEatonApplicationAttr();
        if(StringUtils.isNotBlank(url) && StringUtils.isNotBlank(myEatonAppAttr)) {
            // check to see if it contains the sso attribute from the fromUri
           return url.contains(myEatonAppAttr);
        } else {
            LOG.debug("AuthorizationServlet :: isFromMyEaton :: either redirect url is empty/null {} or myEatonAppAttr is empty/null: {}",url,myEatonAppAttr);
        }
        return false;
    }


    /**
     * Method to get cookie based locale specific dashboard page url
     * Returns defaultPostAuthRedirectURL passed to the method if locale specific resource doesnot exist
     * @param defaultPostAuthRedirectURL
     *   the defaultPostAuthRedirectURL  configured in osgi
     * @param request request
     * @return locale specific page url
     */
    private String getLocaleSpecificDashboardUrl(String defaultPostAuthRedirectURL,SlingHttpServletRequest request) throws IOException {

        String redirectLocale = localeMappingService.getLanguageSiteRootPagePath(request, true);
        if(StringUtils.isNotBlank(redirectLocale)) {
            return CommonUtil.constructLocaleSpecificUrl(redirectLocale, defaultPostAuthRedirectURL, request.getResourceResolver(), CommonConstants.CONTENT_ROOT_FOLDER);
        }
        Cookie eatonRedirectCookie = request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE);
        String redirectCookie =  eatonRedirectCookie != null ? eatonRedirectCookie.getValue() : "";
        LOG.debug("Redirect Cookie Found from Login Domain --> {}", redirectCookie);
        if(StringUtils.isNotEmpty(redirectCookie) ){
            String eatonCookies = AuthCookieUtil.getCookie(request, AuthConstants.EATON_COOKIES);
            if (StringUtils.isNotBlank(eatonCookies) && eatonCookies.equals(CommonConstants.TRUE) && null != localeMappingService) {
                return CommonUtil.constructLocaleSpecificUrlByRedirectCookie(redirectCookie, defaultPostAuthRedirectURL, request.getResourceResolver());
            }
        }
        return defaultPostAuthRedirectURL;
    }

}
