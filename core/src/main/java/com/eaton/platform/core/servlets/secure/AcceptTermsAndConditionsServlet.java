package com.eaton.platform.core.servlets.secure;

import com.adobe.acs.commons.util.CookieUtil;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.vanity.LocaleMappingService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.services.UserProfileService;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import com.eaton.platform.integration.myeaton.dto.UserProfilePayload;
import com.eaton.platform.integration.myeaton.services.UpdateUserService;
import com.eaton.platform.integration.myeaton.services.exception.UpdateUserInformationException;
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
import javax.servlet.ServletException;
import java.io.IOException;

import static org.apache.http.HttpStatus.*;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/general/term-condition-cta",
                ServletConstants.SLING_SERVLET_EXTENSIONS + "servlet"
        })
public class AcceptTermsAndConditionsServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AcceptTermsAndConditionsServlet.class);

    @Reference
    private transient UpdateUserService updateUserService;

    @Reference
    private transient UserProfileService userProfileService;

    @Reference
    private transient AuthorizationService authorizationService;

    @Reference
    private transient AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Reference
    private transient LocaleMappingService localeMappingService;


    private UserProfilePayload createUserProfilePayload(UserProfile userProfile) {
        return new UserProfilePayload()
                .setCurrentEmailAddress(userProfile.getUid())
                .setEmailAddress(userProfile.getUid())
                .setUpdatorFirstName(userProfile.getGivenName())
                .setUpdatorLastName(userProfile.getLastName())
                .setUpdatorIdentifier(userProfile.getUid())
                .setEatonPersonType(userProfile.getEatonPersonType())
                .setUpdateMyEatonTermsConditionsTimestamp(true)
                .setUpdateOrderCenterTermsConditionsTimestamp(true);

        //TODO: Since we are accepting all terms and conditions we no longer need to check

    }
    //TODO: if business decide we need checking if user need to accept certain T and C we can uncomment this and uncomment the top portion


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Resource resource = request.getResource();
        LOG.error("Resource path: {}",resource.getPath());
        AuthenticationToken authToken = authorizationService.getTokenFromSlingRequest(request);
        UserProfile userProfile;
        if(null != authToken && null != (userProfile = authToken.getUserProfile())) {
            final String defaultRedirectPath = authenticationServiceConfiguration.getDefaultPostAuthRedirectURI();
            LOG.error("Default post defaultRedirectPath: {}",defaultRedirectPath);
            // ensure user is log in
            UserProfilePayload userProfilePayload = createUserProfilePayload(userProfile);
            try {
                boolean updated = updateUserService.updateUserInformation(userProfilePayload);
                if(updated) {
                    // since the user profile is cache we will need to immediately set the date if update is successful
                    boolean updateTimestamp = userProfileService.updateUserTermsAndConditionsTimestamp(authToken.getUserLDAPId(),authToken.getExpiration());
                    if(!updateTimestamp) {
                        LOG.error("**** AcceptTermsAndConditionsServlet :: doGet :: Update timestamp failed ****");
                    }
                    // response now we need to figure out where to redirect user
                    String postAuthRedirectURL = AuthCookieUtil.getCookie(request, CommonConstants.LOGIN_PAGE_REDIRECT_COOKIE_ID);
                    CookieUtil.dropCookies(request,response, CommonConstants.SLASH_STRING,
                            CommonConstants.LOGIN_PAGE_REDIRECT_COOKIE_ID);
                    LOG.debug("RedirectingURL after getting from login-page-redirect Cookie: {}", postAuthRedirectURL);
                    if(StringUtils.isNotEmpty(postAuthRedirectURL)) {
                        postAuthRedirectURL = CommonUtil.dotHtmlLink(postAuthRedirectURL,request.getResourceResolver());
                        LOG.error("Redirecting to referred Page :: {}", postAuthRedirectURL);
                        response.sendRedirect(postAuthRedirectURL);
                    } else if(StringUtils.isNotEmpty(defaultRedirectPath)) {
                        LOG.error("Redirecting to Home Page :: {}", defaultRedirectPath);
                        final String redirectUrl = getLocaleSpecificDashboardUrl(defaultRedirectPath, request);
                        response.sendRedirect(redirectUrl);
                    }else{
                        LOG.error("Unable to redirect to Home page as redirectURL is null.");
                    }
                } else {
                    response.sendError(SC_BAD_REQUEST);
                }

            } catch (UpdateUserInformationException e) {
                LOG.error("AcceptTermsAndConditionsServlet :: UpdateUserInformationException:: {}",e.getMessage(),e);
                response.sendError(SC_SERVICE_UNAVAILABLE);
            }
        } else {
            response.sendError(SC_FORBIDDEN);
        }
    }

    /**
     * Method to get cookie based locale specific dashboard page url
     * Returns defaultPostAuthRedirectURL passed to the method if locale specific resource doesnot exist
     * @param defaultPostAuthRedirectURL
     *   the defaultPostAuthRedirectURL  configured in osgi
     * @param request request
     * @return locale specific page url
     */
    private String getLocaleSpecificDashboardUrl(String defaultPostAuthRedirectURL,SlingHttpServletRequest request)  {

        String redirectLocale = localeMappingService.getLanguageSiteRootPagePath(request, true);
        if(StringUtils.isNotBlank(redirectLocale)) {
            return CommonUtil.constructLocaleSpecificUrl(redirectLocale, defaultPostAuthRedirectURL, request.getResourceResolver(), CommonConstants.CONTENT_ROOT_FOLDER);
        }
        String redirectCookie = AuthCookieUtil.getCookie(request, AuthConstants.ETN_REDIRECT_COOKIE);
        LOG.debug("Redirect Cookie Found from Login Domain --> {}", redirectCookie);
        if(StringUtils.isNotEmpty(redirectCookie) ){
            String eatonCookies = AuthCookieUtil.getCookie(request, AuthConstants.EATON_COOKIES);
            if (StringUtils.isNotBlank(eatonCookies) && eatonCookies.equals(CommonConstants.TRUE) ) {
                return CommonUtil.constructLocaleSpecificUrlByRedirectCookie(redirectCookie, defaultPostAuthRedirectURL, request.getResourceResolver());
            }
        }
        return defaultPostAuthRedirectURL;
    }

}
