package com.eaton.platform.core.servlets.drilldownselectiontool;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.DrilldownSelectionToolConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.drilldownselectiontool.DrilldownSelectionToolModel;
import com.eaton.platform.core.models.drilldownselectiontool.DropdownModel;
import com.eaton.platform.core.models.drilldownselectiontool.DropdownOptionModel;
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
        ServletConstants.SLING_SERVLET_SELECTORS + DrilldownSelectionToolConstants.DROPDOWN_SERVLET_SELECTOR,
        ServletConstants.SLING_SERVLET_EXTENSION_JSON
    })
public class DropdownOptionsServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropdownOptionsServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {

        LOGGER.debug("doGet: START");

        response.setContentType(CommonConstants.APPLICATION_JSON);

        String selectedDropdownOptionTagsString = request.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS);
        List<String> selectedDropdownOptionTagsList = StringUtils.isNotBlank(selectedDropdownOptionTagsString)
            ? List.of(StringUtils.split(selectedDropdownOptionTagsString, CommonConstants.PIPE))
            : new ArrayList<>();
        LOGGER.debug("selectedDropdownOptionTagsList: {}", selectedDropdownOptionTagsList);

        int dropdownIndex = 0;
        try {
            String dropdownIndexString = request.getParameter(DrilldownSelectionToolConstants.NEXT_DROPDOWN_INDEX);
            dropdownIndex = Integer.parseInt(dropdownIndexString);
        } catch (NumberFormatException e) {
            LOGGER.error("Error while parsing nextDropdownIndex from string to int", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while parsing nextDropdownIndex");
            return;
        }

        DrilldownSelectionToolModel drilldownSelectionToolModel = request.adaptTo(DrilldownSelectionToolModel.class);
        DropdownModel dropdownModel = Objects.requireNonNull(drilldownSelectionToolModel).getDropdown(dropdownIndex, selectedDropdownOptionTagsList);
        List<DropdownOptionModel> dropdownOptions = dropdownModel.getDropdownOptions();
        LOGGER.debug("dropdownOptions: {}", dropdownOptions);
        if (dropdownOptions.isEmpty()) {
            LOGGER.warn("dropdownOptions is empty");
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "No dropdown option values found with results");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            String responseJson = new Gson().toJson(dropdownOptions);
            LOGGER.debug("responseJson: {}", responseJson);
            out.write(responseJson);
        } catch (IOException e) {
            LOGGER.error("Error printing json response.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error printing json response.");
        }

        LOGGER.debug("doGet: END");

    }

}
