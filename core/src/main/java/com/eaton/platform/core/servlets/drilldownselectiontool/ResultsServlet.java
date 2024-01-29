package com.eaton.platform.core.servlets.drilldownselectiontool;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.DrilldownSelectionToolConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.drilldownselectiontool.DrilldownSelectionToolModel;
import com.eaton.platform.core.models.drilldownselectiontool.ResultsModel;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component(service = Servlet.class,
    immediate = true,
    property = {
        ServletConstants.SLING_SERVLET_METHODS_GET,
        ServletConstants.SLING_SERVLET_RESOURCE_TYPES + DrilldownSelectionToolConstants.DRILLDOWN_SELECTION_TOOL_RESOURCE_TYPE,
        ServletConstants.SLING_SERVLET_SELECTORS + DrilldownSelectionToolConstants.RESULTS_SERVLET_SELECTOR,
        ServletConstants.SLING_SERVLET_EXTENSION_JSON
    })
public class ResultsServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultsServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {

        LOGGER.debug("doGet: START");

        response.setContentType(CommonConstants.APPLICATION_JSON);

        String selectedDropdownOptionTagsString = request.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS);
        List<String> selectedDropdownOptionTags = StringUtils.isNotBlank(selectedDropdownOptionTagsString)
            ? List.of(StringUtils.split(selectedDropdownOptionTagsString, CommonConstants.PIPE))
            : new ArrayList<>();
        LOGGER.debug("selectedDropdownOptionTags: {}", selectedDropdownOptionTags);

        int startIndex = 0;
        try {
            startIndex = Integer.parseInt(request.getParameter(DrilldownSelectionToolConstants.START_INDEX_PARAM_NAME));
        } catch (NumberFormatException e) {
            LOGGER.error("Error while parsing startIndex from string to int");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while parsing startIndex");
            return;
        }
        LOGGER.debug("startIndex: {}", startIndex);

        int pageSize = 0;
        try {
            pageSize = Integer.parseInt(request.getParameter(CommonConstants.PAGE_SIZE));
        } catch (NumberFormatException e) {
            LOGGER.error("Error while parsing pageSize from string to int");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while parsing pageSize");
            return;
        }
        LOGGER.debug("pageSize: {}", pageSize);

        DrilldownSelectionToolModel drilldownSelectionToolModel = request.adaptTo(DrilldownSelectionToolModel.class);
        ResultsModel resultsModel = Objects.requireNonNull(drilldownSelectionToolModel).getResultsModel(selectedDropdownOptionTags, startIndex, pageSize);

        if (resultsModel.getResultsItemModelList().isEmpty()) {
            LOGGER.warn("resultsModel.getResultsItemModelList is empty.");
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "No results found");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            String responseJson = new Gson().toJson(resultsModel);
            LOGGER.debug("responseJson: {}", responseJson);
            out.write(responseJson);
        } catch (IOException e) {
            LOGGER.error("Error printing json response.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error printing json response.");
        }

        LOGGER.debug("doGet: END");

    }

}
