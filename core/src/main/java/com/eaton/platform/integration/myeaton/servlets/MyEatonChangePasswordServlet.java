/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.servlets;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.services.UserProfileService;
import com.eaton.platform.integration.myeaton.services.MyEatonUserLookupService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;

/** Servlet for looking up users on MyEaton */
@Component(
    service = Servlet.class,
    immediate = true,
    property = {
        ServletConstants.SLING_SERVLET_METHODS_GET,
        ServletConstants.SLING_SERVLET_PATHS + "/eaton/my-eaton/save-password",
        ServletConstants.SLING_SERVLET_EXTENSIONS + ServletConstants.SLING_SERVLET_EXTENSION_JSON
    })
public final class MyEatonChangePasswordServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MyEatonChangePasswordServlet.class);
    private static final long serialVersionUID = 8783747842113629999L;
    private final static String PARAM_OLD_PASSWORD = "oldPassword";
    private final static String PARAM_NEW_PASSWORD = "newPassword";


    @Reference
    private transient MyEatonUserLookupService userLookupService;

    @Reference
    private AuthorizationService authorizationService;

    @Reference
    private UserProfileService profileService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        LOG.debug("****** Start :  MyEatonChangePasswordServlet ");
        response.setContentType("application/json");
        String oldPassword = request.getParameter(PARAM_OLD_PASSWORD);
        String newPassword = request.getParameter(PARAM_NEW_PASSWORD);
        LOG.info("****** MyEatonChangePasswordServlet Servlet :  Old Password {} ",oldPassword);
        LOG.info("****** MyEatonChangePasswordServlet Servlet :  New Password {} ",newPassword);
        try (PrintWriter out = response.getWriter()) {
            if(authorizationService.getTokenFromSlingRequest(request) != null) {
                UserProfile userProfile = authorizationService.getTokenFromSlingRequest(request).getUserProfile();
                LOG.debug("Outputting user Change Password Results");
                JSONObject results = profileService.changePassword(oldPassword, newPassword, userProfile);
                out.println(results);
            }
        } catch(IOException e) {
            LOG.error("Error while change password out to PrintWriter.", e);
        }
        LOG.debug("****** End :  MyEatonChangePasswordServlet ");
    }
}
