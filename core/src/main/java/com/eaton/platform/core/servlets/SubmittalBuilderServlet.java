package com.eaton.platform.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import com.google.gson.JsonObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import java.util.List;
import java.util.Arrays;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/content/submittalbuilder",
                ServletConstants.SLING_SERVLET_SELECTORS + "submittalbuilderresults",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class SubmittalBuilderServlet extends SlingSafeMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SubmittalBuilderServlet.class);

    @Reference
    private EndecaRequestService endecaRequestService;

    @Reference
    private EndecaService endecaService;

    @Reference
    private AdminService adminService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {
        final List missingParams = new ArrayList<>();
        final PrintWriter responseWriter = response.getWriter();
        response.setContentType(AssetDownloadConstants.APPLICATION_JSON);

        Arrays.asList(AssetDownloadConstants.REQUIRED_PARAMS).stream().forEach(param -> {
            if (! request.getRequestParameterMap().containsKey(param)) {
                LOG.warn("Parameter missing from submittal builder request: " + param);
                missingParams.add(param);
            }
        });

        if (missingParams.size() > 0) {
            writeParamError(missingParams, responseWriter);
        } else {
            writeResponse(request, responseWriter);
        }

        responseWriter.flush();
    }

    private void writeParamError(final List<String> missingParams, final PrintWriter responseWriter) {
        responseWriter.write("{ \"error\": \"Missing required parameters: " + String.join(",", missingParams) + "\" }");
    }

    private void writeResponse(final SlingHttpServletRequest request, final PrintWriter responseWriter) throws IOException {
        try (ResourceResolver resourceResolver = adminService.getReadService()) {
        final Resource resource = request.getResource();
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        final Page page = pageManager.getContainingPage(resource);
        final int pageSize = request.adaptTo(EatonSiteConfigModel.class).getSiteConfig().getPageSize();
        final int startingRecord;
        if (request.getRequestParameterMap().containsKey(AssetDownloadConstants.STARTING_RECORD)) {
            startingRecord = Integer.parseInt(request.getParameter(AssetDownloadConstants.STARTING_RECORD));
        } else {
            startingRecord = 0;
        }
        final EndecaServiceRequestBean endecaRequestBean =
                endecaRequestService.getSubmitBuilderEndecaRequestBean(page, resourceResolver, request.getParameter(AssetDownloadConstants.SCOPE), request.getParameter(AssetDownloadConstants.FACETS), request.getParameter(AssetDownloadConstants.SORT_ORDER), request.getParameter(AssetDownloadConstants.ACTIVE_FILTERS), pageSize, startingRecord);
            final JsonObject submittalResponseJson = endecaService.getSubmittalResponse(endecaRequestBean);
            if(null != submittalResponseJson) {
                responseWriter.write(submittalResponseJson.toString());
            }
        } catch (Exception e) {
            LOG.error("Malformed json from endeca service", e);
            responseWriter.write(AssetDownloadConstants.ENDECA_ERROR_RESPONSE);
        }
    }
}
