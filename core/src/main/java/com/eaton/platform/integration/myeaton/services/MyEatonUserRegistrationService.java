/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services;

import com.eaton.platform.integration.myeaton.bean.UserRegistrationResponseBean;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

/** User Registration Service for My Eaton - Handles new registrations. */
public interface MyEatonUserRegistrationService {

    /**
     Process the data from adaptive forms and pass it along to my eaton service
     * @param request Sling Request from AEM
     * @param resourceResolver ResourceResolver from AEM
     * @return Response object containing details on the response
     */
    UserRegistrationResponseBean handleFormData(SlingHttpServletRequest request, ResourceResolver resourceResolver);
}
