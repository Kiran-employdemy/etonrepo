package com.eaton.platform.integration.auth.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.secure.SecureAttributesModel;
import com.eaton.platform.core.services.CountryLangCodeConfigService;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.models.impl.SimpleAuthenticationTokenImpl;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.eaton.platform.core.constants.CommonConstants.RUNMODE_AUTHOR;

@Component(service = AuthorizationService.class,immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "Authorization Service for Secure Implementation",
                AEMConstants.PROCESS_LABEL + "AuthorizationService"
})
public class AuthorizationServiceImpl implements AuthorizationService {
    private static final String AUTHENTICATION_TOKEN_PARAM = "authenticationToken";
    private static final String AEM_FORMS_DATA = "data";
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServiceImpl.class);
    private boolean bypassAuthorization = false;
    private boolean isexcludecountryChecked;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    protected CountryLangCodeConfigService countryLangCodeConfigService;

    @Override
    public void setTokenOnSlingRequest(SlingHttpServletRequest slingRequest, AuthenticationToken authenticationToken) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entered setTokenOnSlingRequest");
        }

        slingRequest.setAttribute(AUTHENTICATION_TOKEN_PARAM, authenticationToken);

        if (LOG.isDebugEnabled()) {
            if (authenticationToken == null) {
                LOG.debug("SetTokenOnSlingRequest - AUTHENTICATION TOKEN is NULL!!");
            } else {
                LOG.debug(String.format("SetTokenOnSlingRequest for User LDAP ID: %s, User Subject: %s",
                        authenticationToken.getUserLDAPId(), authenticationToken.getSubject()));
            }
        }
    }

    @Override
    public void setProfileJSONOnSlingRequest(SlingHttpServletRequest slingRequest, AuthenticationToken authenticationToken) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entered setProfileXMLOnSlingRequest");
        }


        slingRequest.setAttribute(AEM_FORMS_DATA, SecureUtil.constructPrefillBoundJSON(authenticationToken,slingRequest.getResourceResolver()));

        if (LOG.isDebugEnabled()) {
            if (authenticationToken == null) {
                LOG.debug("setProfileXMLOnSlingRequest - AUTHENTICATION TOKEN is NULL!!");
            } else {
                LOG.debug(String.format("setProfileXMLOnSlingRequest for User LDAP ID: %s, User Subject: %s",
                        authenticationToken.getUserLDAPId(), authenticationToken.getSubject()));
            }
        }
    }

    @Override
    public boolean isAuthenticated(SlingHttpServletRequest slingRequest) {

        AuthenticationToken authenticationToken = getTokenFromSlingRequest(slingRequest);
        return authenticationToken != null && authenticationToken.getUserProfile() != null;
    }

    @Override
    public AuthenticationToken getTokenFromSlingRequest(SlingHttpServletRequest slingRequest){
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entered getTokenFromSlingRequest");
        }
        if(bypassAuthorization){
            LOG.debug("getTokenFromSlingRequest() : ByPass Authorization is true");
            return new SimpleAuthenticationTokenImpl(true);
        }
        return (AuthenticationToken)slingRequest.getAttribute(AUTHENTICATION_TOKEN_PARAM);
    }

    @Override
    public String getProfileJSONFromSlingRequest(SlingHttpServletRequest slingRequest){
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entered getTokenFromSlingRequest");
        }
        if(bypassAuthorization){
            LOG.debug("getProfileXMLOnSlingRequest() : ByPass Authorization is true");
            return StringUtils.EMPTY;
        }
        return (String)slingRequest.getAttribute(AEM_FORMS_DATA);
    }

    @Override
    public boolean isAuthorized(SlingHttpServletRequest slingRequest,String resourcePath){

        if(bypassAuthorization){
            LOG.debug("isAuthorized(slingRequest, resourcePath) : ByPass Authorization is true");
            return true;
        }

        AuthenticationToken authenticationToken = getTokenFromSlingRequest(slingRequest);

        if(authenticationToken != null){
            return processAuthorization(authenticationToken.getUserProfile(),slingRequest.getResourceResolver().resolve(resourcePath).adaptTo(Resource.class));
        }

        return false;
    }

    @Override
    public boolean isAuthorized(AuthenticationToken authenticationToken, Resource resource){

        if(bypassAuthorization){
            LOG.debug("isAuthorized(authenticationToken,resource) : ByPass Authorization is true");
            return true;
        }
        UserProfile userProfile =  null != authenticationToken ? authenticationToken.getUserProfile(): null;
        return processAuthorization(userProfile,resource);
    }

    /**
     * processAuthorization
     * @param userProfile - Profile of the user to be authorized
     * @param resource - Resource to be authorized
     * @return
     */
    private boolean processAuthorization(UserProfile userProfile, Resource resource){
        if(resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            LOG.debug("AuthorizationServiceImpl :: processAuthorization :: {}",resource.getPath());
            SecureAttributesModel secureModel = SecureUtil.getSecureModelByResource(resource);
            isexcludecountryChecked = (secureModel.getExcludeCountries() != null && secureModel.getExcludeCountries().equals(SecureConstants.TRUE));
            final boolean isSecure = (secureModel.getSecurePage() != null && secureModel.getSecurePage().equals(SecureConstants.TRUE)) ||
                    (secureModel.getSecureAsset() != null && secureModel.getSecureAsset().equals(SecureConstants.YES));
            final boolean isAuthenticated = (null != userProfile);
            LOG.debug("Resource :: {} is secure? {}",resource.getPath(),isSecure);
            if ((isAuthenticated && !isSecure) || (!isAuthenticated && !isSecure)) {
                LOG.debug("Identified Non Secure Asset/Page -> Granted Access --> {}", resource.getPath());
                return true;
            } else if ((!isAuthenticated)) {
                LOG.debug("Identified  Secure Asset/Page & User Not Authenticated -> Denied Access --> {}", resource.getPath());
                return false;
            } else {
                return this.authorizeResource(secureModel, userProfile);
            }
        }else{
            // Return false if the resource is non-existed
            return false;
        }
    }

    /**
     * This method checks/compares current user attributes with page/asset attributes.
     *  Returns true if anyone of the attribute within each group else false.
     * @param secureModel - Current resource Attribute Model
     * @param userAuthProfile - Authenticated Profile
     * @return  True - If the User has right privileges to access the resource, False - Not Having Rights to Access
     */
    private boolean authorizeResource(SecureAttributesModel secureModel, UserProfile userAuthProfile){
        LOG.debug(":::: Entered Into authorizeResource() method ::::: ");
        if(checkAccessForExcludeCountry(countryLangCodeConfigService.getExcludeCountryList(), userAuthProfile.getCountryCode())){
          if(secureModel.isPresent()) {
            LOG.debug("********** One or More Secure Attributes Found ********");
            LOG.debug("Okta Group From Secure Tab: {} : auth profile Okta Group: {}", secureModel.getOktaGroups(),userAuthProfile.getUserOktaGroups());
            return Stream.of(checkAccessEligibilityForAttribute(secureModel.getAccountType(), userAuthProfile.getAccountTypeTags()),
                    checkAccessEligibilityForAttribute(secureModel.getProductCategories(), userAuthProfile.getProductCategoriesTags()),
                    checkAccessEligibilityForAttribute(secureModel.getApplicationAccess(), userAuthProfile.getAppAccessTags()),
                    checkAccessEligibilityForAttribute(secureModel.getCountries(), userAuthProfile.getLocaleTags()),
                    checkAccessEligibilityForAttribute(secureModel.getOktaGroups(),userAuthProfile.getUserOktaGroups()),
                    checkAccessEligibilityForAttribute(secureModel.getPartnerProgramAndTierLevel(), userAuthProfile.getPartnerProgramAndTierLevelTags())).allMatch(item -> item == true);
           }
        }
        else{
        	// Return false if user is from ExcludeCountry list
        	return false;
        }
        // Return true if all attributes are empty
        return  !secureModel.isPresent();
    }

    /**
     *
     * @param resourceSecureTags
     * @param userProfileSecureTags
     * @return
     */
    private boolean checkAccessEligibilityForAttribute(String[] resourceSecureTags, Iterable<String> userProfileSecureTags){
        boolean allowAccess = false;
        if( resourceSecureTags != null && resourceSecureTags.length > 0){
            List<String> secureAttributesMatched =  Arrays.stream(resourceSecureTags).filter(e ->
                    (ImmutableList.copyOf(userProfileSecureTags).stream().filter(d -> d.equals(e)).count()) >= 1).collect(Collectors.toList());
            LOG.debug("******* Secure Matched Attributes :  --> {}", secureAttributesMatched);
            if(!secureAttributesMatched.isEmpty()){
                allowAccess = true;
            }
        }else{
            allowAccess = true;
        }

        return allowAccess;
    }

    /**
    *
    * @param excludeCountryArray
    * @param userCountry
    * @return
    */
   private boolean checkAccessForExcludeCountry(String[] excludeCountryArray, String userCountry){
       boolean allowAccess = true;
       if(isexcludecountryChecked && excludeCountryArray != null && excludeCountryArray.length > 0 ){
    	   List<String> excludeCountryList = Arrays.asList(excludeCountryArray);
           if(excludeCountryList.stream().map(String::toLowerCase).collect(Collectors.toList()).contains(userCountry.toLowerCase())){
               allowAccess = false;
           }
       }
       LOG.debug("checkAccessForExcludeCountry: {} userCountry: {}" ,allowAccess,userCountry);
       return allowAccess;
   }

    @Activate
    @Modified
    protected final void activate() {
        LOG.debug("AuthorizationService :: activate() :: Start, set");
        Set<String> runModes = slingSettingsService.getRunModes();

        if (runModes.contains(RUNMODE_AUTHOR) || runModes.contains(EndecaConstants.RUNMODE_ENDECA)) {
            LOG.debug("Run modes contains 'author' or 'endeca' - setting bypass to true");
            bypassAuthorization = true;
        }
        LOG.debug("AuthorizationService :: activate() :: End");
    }
}
