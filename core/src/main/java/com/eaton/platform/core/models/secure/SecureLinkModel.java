package com.eaton.platform.core.models.secure;

import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;

import javax.inject.Inject;
import java.util.HashMap;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecureLinkModel {

    HashMap<String, UserProfile> map;

    private boolean isSecureExternal = false;

    @Inject
    private AuthorizationService authorizationService;

    @Inject
    private SlingHttpServletRequest slingHttpServletRequest;

    @Inject
    private Resource resource;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    SlingHttpServletRequest request;

    @Inject
    private AuthenticationServiceConfiguration authenticationServiceConfig;

    /** The cta link. */
    @Inject  @Via("resource")
    private String link;

    /**
     *
     * @return Configured link with Extension
     */
    public String getLink() {
        if(link != null) {
            return CommonUtil.dotHtmlLink(link);
        }
        return link;
    }

    @Inject
    private boolean isAuthorised;

    public boolean isAuthorised() {
        return isAuthorisedLink();
    }

    /**
     * This method checks if  megaMenu is enabled in the parent secureContainer
     * Returns True : If megaMenu prop is set to true in the parent secureContainer resource .
     * Returns False : If megaMenu prop is not available or its equal to false  in the parent secureContainer resource
     * @return
     */
    public boolean isMegaNav() {
        Resource parResource =  resource.getParent();
        if(parResource != null) {
            Resource secureContainer =  parResource.getParent() ;
            if (null != secureContainer && secureContainer.getValueMap().containsKey(SecureConstants.MEGA_MENU)) {
                String megaMenu =  secureContainer.getValueMap().get(SecureConstants.MEGA_MENU, StringUtils.EMPTY);
                if( null != megaMenu && megaMenu.equals(SecureConstants.TRUE)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method checks if the link configured has required privileges or not.
     * Returns True : If all the Secure Attributes Matches with current logged-in user.
     * Returns False : If Matching criteria fails.
     * @return
     */
    private boolean isAuthorisedLink(){
        Resource secureResource = null;
        boolean isAuthorized = false;
        if(checkIfExternal(link)){
            secureResource = resource;
        }
        else if(null != this.link){
            secureResource = resource.getResourceResolver().resolve(link);
        }
        if(null != secureResource) {
            SecureAttributesModel secureAttributesModel = secureResource.adaptTo(SecureAttributesModel.class);
            if(null != secureAttributesModel && checkIfExternal(link)) {
                secureAttributesModel.setSecurePage(SecureConstants.TRUE);
            }
            AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(slingHttpServletRequest);
            if(null != authenticationToken){
                isAuthorized = authorizationService.isAuthorized(authenticationToken, secureResource);
            }
        }
        return isAuthorized;
    }

    /**
     * This method Returns True : If the link is External. False : Not an external Link
     * @param resourceLink
     * @return
     */
    private boolean checkIfExternal(final String resourceLink){
        final boolean isFQDNUrl = (null != resourceLink && CommonUtil.getIsExternal(resourceLink)); // FQDN - Fully Qualified Domain Name
        if (isFQDNUrl && resource.getValueMap().get(SecureConstants.IS_EXTERNAL, StringUtils.EMPTY).equals(SecureConstants.TRUE)) {
            isSecureExternal = true;
        }
        return isSecureExternal;
    }
}