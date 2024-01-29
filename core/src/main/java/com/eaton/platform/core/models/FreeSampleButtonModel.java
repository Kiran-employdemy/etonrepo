package com.eaton.platform.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.osgi.resource.Resource;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;

@Model(adaptables = { Resource.class,SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FreeSampleButtonModel {
    

	private static final String UNAUTHENTICATED_LABEL = "UnAuthenticatedCTALabel";
	private static final String AUTHENTICATED_LABEL = "AuthenticatedCTALabel";
	private static final String UNAUTHENTICATED_LABEL_MESSAGE = "Add a free sample to the cart";
	private static final String AUTHENTICATED_LABEL_MESSAGE = "Add a free sample to the cart";
    private String freeSampleCTALabel;
	private String freeSampleCTALink;
	 

    @Inject
	@Source("sling-object")
	private SlingHttpServletRequest slingRequest;

    @Inject
	@ScriptVariable
	private Page currentPage;

    @Inject
	private AuthenticationServiceConfiguration authenticationServiceConfiguration;

	@Inject
	private AuthorizationService authorizationService;

	
 
    @PostConstruct
	protected void init() {
	
    boolean isAuthenticated = authorizationService.isAuthenticated(slingRequest);
			if(isAuthenticated) {
				setFreeSampleCTALink(null);
				setFreeSampleCTALabel(CommonUtil.getI18NFromResourceBundle(slingRequest,
						currentPage, AUTHENTICATED_LABEL, AUTHENTICATED_LABEL_MESSAGE));
			} else {
				String loginPageURL = authenticationServiceConfiguration.getOktaLoginURI();
				setFreeSampleCTALink(loginPageURL);
				setFreeSampleCTALabel(CommonUtil.getI18NFromResourceBundle(slingRequest,
						currentPage, UNAUTHENTICATED_LABEL, UNAUTHENTICATED_LABEL_MESSAGE));

			}
		}

    public String getFreeSampleCTALabel() {
        return freeSampleCTALabel;
    }

    public String getFreeSampleCTALink() {
        return freeSampleCTALink;
    }
    
	public void setFreeSampleCTALink(String freeSampleCTALink) {
        this.freeSampleCTALink = freeSampleCTALink;
    }

	public void setFreeSampleCTALabel(String freeSampleCTALabel) {
		this.freeSampleCTALabel = freeSampleCTALabel;
	}


	
}
