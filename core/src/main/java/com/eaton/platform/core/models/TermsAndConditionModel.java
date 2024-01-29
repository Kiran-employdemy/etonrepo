package com.eaton.platform.core.models;

import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.Calendar;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        resourceType = TermsAndConditionModel.RESOURCE_TYPE)
public class TermsAndConditionModel {

    public static final String RESOURCE_TYPE = "eaton/components/general/core/term-condition/v1/term-condition";

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    protected String type;

    @ValueMapValue
    protected Calendar tcDate;

    @ValueMapValue
    protected String tcVersion;

    @ValueMapValue
    protected String[] appName;

    @Inject
    private AuthorizationService authorizationService;

    private boolean shouldRender = true;

    //TODO: Business decided we do not need to check on application name for now. We can uncomment this if business decided we need to check application tag against nsrole

    public Calendar getTcDate() {
        return tcDate;
    }

    public String getTcVersion() {
        return tcVersion;
    }

    public String[] getAppName() {
        return appName;
    }

    public boolean shouldRender() {
        return shouldRender;
    }

    public String getType() {
        return type;
    }
}
