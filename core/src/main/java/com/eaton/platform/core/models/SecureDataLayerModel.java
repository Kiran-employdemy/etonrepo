package com.eaton.platform.core.models;

import com.eaton.platform.core.bean.SecureDataLayerBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = { Resource.class,
        SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecureDataLayerModel {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** The login Token */
    private AuthenticationToken authenticationToken;


    private String dataLayerJson;

    /** The SlingHttpServletRequest */
    @Inject
    @Source("sling-object")
    private SlingHttpServletRequest slingRequest;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(DataLayerModel.class);

    @Inject
    private AuthorizationService authorizationService;


    @PostConstruct
    public void init() {

        LOG.debug("SecureDataLayerModel : This is Entry into init() method");
        SecureDataLayerBean secureDataLayerBean = new SecureDataLayerBean();
        // Set Login state as Unauthenticated as defualt
        secureDataLayerBean.setLoginState(CommonConstants.UNAUTHENTICATED);
        if(null!= slingRequest){
            authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);

        }
        if(authenticationToken != null && authenticationToken.getUserProfile() != null) {
            LOG.debug("SecureDataLayerModel :  User Authenticated {}", authenticationToken.getUserLDAPId());
            UserProfile userProfile = authenticationToken.getUserProfile();
            if(userProfile.getApplicationAccessTags() != null) {
                secureDataLayerBean.setVisitorApplicationAuth(String.join(CommonConstants.COMMA, userProfile.getApplicationAccessTags()));
            }
            if(userProfile.getProductCategories() != null) {
                secureDataLayerBean.setVisitorProductAuth(String.join(CommonConstants.COMMA, userProfile.getProductCategories()));
            }
            if(userProfile.getAccountTypes() != null) {
                secureDataLayerBean.setVisitorAccountType(String.join(CommonConstants.COMMA, userProfile.getAccountTypes()));
            }
            if(userProfile.getCountryCode() != null) {
                secureDataLayerBean.setVisitorCountryAuth(userProfile.getCountryCode());
            }
            if(userProfile.getPartnerProgramTypeAndTierLevels() != null) {
                secureDataLayerBean.setVisitorPartnerProgramTypeAndTier(String.join(CommonConstants.COMMA, userProfile.getPartnerProgramTypeAndTierLevels()));
            }
            secureDataLayerBean.setLoginId(userProfile.getNsUniqueId());
            secureDataLayerBean.setLoginState(CommonConstants.AUTHENTICATED);
        }

        dataLayerJson=this.gson.toJson(secureDataLayerBean);
        LOG.debug("SecureDataLayerModel : END of init() method");
    }

    /**
     *
     * @return dataLayerJson wrapped with user profile attributes
     */
    public String getDataLayerJson(){
        return dataLayerJson;
    }

    /**
     *
     * @param dataLayerJson
     */
    public void setDataLayerJson(String dataLayerJson) {
        this.dataLayerJson = dataLayerJson;
    }


}
