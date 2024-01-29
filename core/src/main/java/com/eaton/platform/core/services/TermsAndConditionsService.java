package com.eaton.platform.core.services;

import com.eaton.platform.integration.auth.models.AuthenticationToken;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

public interface TermsAndConditionsService {

    String getTermAndConditionPath(SlingHttpServletRequest request);
    String getTermAndConditionPath(Resource resource);
    boolean shouldRedirect(AuthenticationToken authToken);
    String getDefaultPath();
}
