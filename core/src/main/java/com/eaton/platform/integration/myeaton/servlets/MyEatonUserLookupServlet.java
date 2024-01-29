/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.servlets;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.myeaton.services.MyEatonUserLookupService;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Servlet for looking up users on MyEaton */
@Component(
    service = Servlet.class,
    immediate = true,
    property = {
        ServletConstants.SLING_SERVLET_METHODS_GET,
        ServletConstants.SLING_SERVLET_PATHS + "/eaton/my-eaton/user-lookup",
        ServletConstants.SLING_SERVLET_EXTENSIONS + ServletConstants.SLING_SERVLET_EXTENSION_JSON
    })
public final class MyEatonUserLookupServlet extends SlingSafeMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MyEatonUserLookupServlet.class);
    private static final long serialVersionUID = 8783747842113629999L;

    @Reference
    private transient MyEatonUserLookupService userLookupService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setContentType("application/json");

        String email = request.getParameter("email");

        StringBuilder responseBody = new StringBuilder("{ \"accountExists\": ");

        responseBody.append(userLookupService.userLookup(email));

        responseBody.append(" }");

        try {
            LOG.debug("Outputting user lookup results");
            PrintWriter out = response.getWriter();
            out.println(responseBody.toString());
        } catch(IOException e) {
            LOG.error("Error writing user lookup results out to PrintWriter.", e);
        }
    }
}
