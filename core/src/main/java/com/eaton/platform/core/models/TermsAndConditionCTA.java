package com.eaton.platform.core.models;

import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
resourceType = TermsAndConditionCTA.RESOURCE_TYPE)
public class TermsAndConditionCTA {

    public static final String RESOURCE_TYPE = "eaton/components/general/core/term-condition-cta/v1/term-condition-cta";

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    private String cancelModalText;

    @ValueMapValue
    @Default(values = "right")
    private String alignment;

    @ValueMapValue
    @Default(booleanValues = false)
    private boolean includedMargin;

    @ValueMapValue
    @Default(values = "Cancel")
    private String cancelText;

    @ValueMapValue
    @Default(values = "Accept")
    private String acceptText;

    @ValueMapValue
    @Default(values = "Continue")
    private String continueText;


    @Inject
    private AuthorizationService authorizationService;

    private boolean shouldRender = false;

    @PostConstruct
    protected void init() {
        AuthenticationToken userToken = authorizationService.getTokenFromSlingRequest(request);
        // meaning user is login
        shouldRender = (userToken != null && userToken.getUserProfile() != null) ? true : false;
    }

    public String getCancelModalText() {
        return cancelModalText;
    }

    public String getAlignment() {
        return alignment;
    }

    public boolean isIncludedMargin() {
        return includedMargin;
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public String getCancelText() {
        return cancelText;
    }

    public String getAcceptText() {
        return acceptText;
    }

    public String getContinueText() {
        return continueText;
    }
}
