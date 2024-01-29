/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.servlets;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.myeaton.bean.MyEatonFieldsResponseBean;
import com.eaton.platform.integration.myeaton.services.MyEatonFieldsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        ServletConstants.SLING_SERVLET_PATHS + "/eaton/my-eaton/fields",
        ServletConstants.SLING_SERVLET_EXTENSIONS + ServletConstants.SLING_SERVLET_EXTENSION_JSON
    })
public final class MyEatonFieldsServlet extends SlingSafeMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MyEatonFieldsServlet.class);
    private static final long serialVersionUID = -2979379358407619390L;

    @Reference
    private transient MyEatonFieldsService myEatonFieldsService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setContentType("application/json");

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        MyEatonFieldsResponseBean fieldsResponse = myEatonFieldsService.getFields();

        try {
            LOG.debug("Outputting user lookup results");
            PrintWriter out = response.getWriter();
            out.println(gson.toJson(fieldsResponse));
        } catch(IOException e) {
            LOG.error("Error writing user lookup results out to PrintWriter.", e);
        }
    }
}
