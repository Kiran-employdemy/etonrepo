/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.models;

import com.adobe.acs.commons.models.injectors.annotation.AemObject;
import com.adobe.aemds.guide.utils.GuideSubmitUtils;
import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.myeaton.bean.UserRegistrationResponseBean;
import com.eaton.platform.integration.myeaton.services.MyEatonUserRegistrationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/** Handles custom submit action for My Eaton Self Registration submission */
@Model(
    adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UserRegistrationSubmitActionModel {
    private static final String STATUS = "status";
    private static final String OWNER = "owner";
    private static final String FAILURE = "failure";
    private static final String SUCCESS = "success";
    private static final Logger LOG = LoggerFactory.getLogger(UserRegistrationSubmitActionModel.class);

    @AemObject
    private ResourceResolver resourceResolver;

    @Inject
    @Via("resource")
    private String redirect;

    @OSGiService
    private Externalizer externalizer;

    @OSGiService
    private MyEatonUserRegistrationService userRegistrationService;

    /** Handle submit action. Passing to the UserRegistration service, then appropriately redirect
     * based on response */
    public void doSubmitAction(SlingHttpServletRequest request) {
        LOG.debug("Entered into UserRegistrationSubmitActionModel.doSubmitAction()");

        UserRegistrationResponseBean userRegistrationResponseBean = userRegistrationService.handleFormData(request,
            resourceResolver);

        LOG.debug("Result of user registration:: {}", userRegistrationResponseBean);

        if (userRegistrationResponseBean.isAccountCreatedSuccessfully()) {
            if (userRegistrationResponseBean.isRemedyTicketIDSet()) {
                LOG.debug("Using the returned Remedy Ticket for Thank You Page");

                GuideSubmitUtils.setRedirectParameters(request,
                        redirectParameters(userRegistrationResponseBean.getRemedyTicketID(), request));
            } else {
                LOG.debug("Using SUCCESS for Thank You Page");

                GuideSubmitUtils.setRedirectParameters(request,
                        redirectParameters(SUCCESS, request));
            }
            GuideSubmitUtils.setRedirectUrl(request, CommonUtil.dotHtmlLink(redirect, resourceResolver));
        }
        else{
            LOG.debug("Using FAILURE for Thank You Page");

            GuideSubmitUtils.setRedirectParameters(request,redirectParameters(FAILURE, request));
            GuideSubmitUtils.setRedirectUrl(request, CommonUtil.dotHtmlLink(redirect, resourceResolver));
        }
        // else handle error
    }

    /**
     * This removes the status and owner redirect parameters from the ones provided in the request.
     * @return The map of redirect parameters to add to the GuideSubmitUtils.
     */
    private Map<String, String> redirectParameters(String confirmationCode, SlingHttpServletRequest request) {
        Map<String, String> redirectParameters = GuideSubmitUtils.getRedirectParameters(request);

        if (redirectParameters == null) {
            return new HashMap<>();
        }

        redirectParameters.put(STATUS, "");
        redirectParameters.put(OWNER, "");

        redirectParameters.put("confirmation", confirmationCode);

        return redirectParameters;
    }

}
