/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.servlets;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.myeaton.services.MyEatonSupplierLookupService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
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
        ServletConstants.SLING_SERVLET_PATHS + "/eaton/my-eaton/supplier-lookup",
        ServletConstants.SLING_SERVLET_EXTENSIONS + ServletConstants.SLING_SERVLET_EXTENSION_JSON
    })
public final class MyEatonSupplierLookupServlet extends SlingSafeMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MyEatonSupplierLookupServlet.class);
    private static final long serialVersionUID = 8783747842113629998L;

    @Reference
    private transient MyEatonSupplierLookupService supplierLookupService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setContentType("application/json");

        String supplierNumber = request.getParameter("supplierNumber");
        StringBuilder responseBody = new StringBuilder("{ \"supplierExists\": ");
        responseBody.append(supplierLookupService.supplierLookup(supplierNumber));
        responseBody.append(" }");

        try {
            LOG.debug("Outputting supplier lookup results");
            PrintWriter out = response.getWriter();
            out.println(responseBody.toString());
        } catch(IOException e) {
            LOG.error("Error writing supplier lookup results out to PrintWriter.", e);
        }
    }
}
