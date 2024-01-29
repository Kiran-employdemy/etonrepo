package com.eaton.platform.core.models.secure;

import com.eaton.platform.integration.auth.models.*;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecureXFModel extends SecureAttributesModel {

    private static final Logger LOG = LoggerFactory.getLogger(SecureXFModel.class);


    @Self
    private SlingHttpServletRequest slingHttpServletRequest;
    @Inject
    private AuthorizationService authorizationService;

    private boolean authorized;

    @PostConstruct
    protected void initModel(){
        if(null != slingHttpServletRequest){
            Resource resource = slingHttpServletRequest.getResource();
            LOG.debug("SecureXFModel :: initModel :: {}",resource.getPath());
            AuthenticationToken userToken = authorizationService.getTokenFromSlingRequest(slingHttpServletRequest);
            authorized = authorizationService.isAuthorized(userToken,slingHttpServletRequest.getResource());
        }
    }

    public boolean isAuthorized() {
        return authorized;
    }
}
