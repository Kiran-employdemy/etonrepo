package com.eaton.platform.core.servlets;

import com.eaton.platform.core.constants.PimImporterConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.PimImporterService;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import com.google.gson.JsonObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + PimImporterConstants.EATON_COMPONENTS_PRODUCT_IMPORTER,
                ServletConstants.SLING_SERVLET_SELECTORS + PimImporterConstants.UPLOAD,
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class PimImporterServlet extends SlingAllMethodsServlet {

    @Reference
    private AdminService adminService;

    @Reference
    private PimImporterService pimImporterService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PimImporterServlet.class);

    @Override
    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader(PimImporterConstants.CACHE_CONTROL,
                PimImporterConstants.NO_CACHE_NO_STORE_MUST_REVALIDATE);
        response.setHeader(PimImporterConstants.PRAGMA, PimImporterConstants.NO_CACHE);
        response.setHeader(PimImporterConstants.EXPIRES, "0");
        response.setContentType(PimImporterConstants.APPLICATION_JSON);
        response.setCharacterEncoding(PimImporterConstants.UTF_8);
        final RequestParameter excelFileParameter = request.getRequestParameter(PimImporterConstants.EXCEL_FILE);
        final String basePath = request.getParameter(PimImporterConstants.BASE_FOLDER);
        final String replicate = request.getParameter(PimImporterConstants.REPLICATE);
        final ByteArrayInputStream fileInputStream = new
                ByteArrayInputStream(IOUtils.toByteArray(excelFileParameter.getInputStream()));
        final ResourceResolver resolver = request.getResourceResolver();
        if (null != fileInputStream) {
            final JsonObject responseData = pimImporterService.createPIMData(fileInputStream, basePath, replicate,
                    resolver);
            final PrintWriter writer = response.getWriter();
            writer.print(responseData);
            writer.flush();
        }
    }
}
