package com.eaton.platform.core.servlets.drilldownselectiontool;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.DrilldownSelectionToolConstants;
import com.eaton.platform.core.models.drilldownselectiontool.DrilldownSelectionToolModel;
import com.eaton.platform.core.models.drilldownselectiontool.DrilldownSelectionToolTestConstants;
import com.eaton.platform.core.models.drilldownselectiontool.DropdownModel;
import com.eaton.platform.core.models.drilldownselectiontool.DropdownOptionModel;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static junitx.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class DropdownOptionsServletTest {

    private static final String RESPONSE_JSON_STRING_EXPECTED = "[{\"tagPath\":\"test tag path 1\",\"title\":\"test title 1\"},{\"tagPath\":\"test tag path 2\",\"title\":\"test title 2\"}]";

    @InjectMocks
    DropdownOptionsServlet dropdownOptionsServlet = new DropdownOptionsServlet();

    @Mock
    private DrilldownSelectionToolModel drilldownSelectionToolModel;
    @Mock
    private DropdownModel dropdownModel;
    @Mock
    private SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    private SlingHttpServletResponse slingHttpServletResponse;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    private List<DropdownOptionModel> getTestDropdownOptions() {

        List<DropdownOptionModel> dropdownOptionModelList = new ArrayList<>();

        DropdownOptionModel dropdownOptionModel1 = new DropdownOptionModel();
        dropdownOptionModel1.setTitle("test title 1");
        dropdownOptionModel1.setTagPath("test tag path 1");
        dropdownOptionModelList.add(dropdownOptionModel1);

        DropdownOptionModel dropdownOptionModel2 = new DropdownOptionModel();
        dropdownOptionModel2.setTitle("test title 2");
        dropdownOptionModel2.setTagPath("test tag path 2");
        dropdownOptionModelList.add(dropdownOptionModel2);
        return dropdownOptionModelList;
    }

    private void setupResponseSuccess() throws IOException {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);
    }

    private void verifyResponseSuccess() throws IOException {
        verify(slingHttpServletResponse).getWriter();
        String output = stringWriter.toString();
        assertEquals(RESPONSE_JSON_STRING_EXPECTED, output.trim());
    }

    @Test
    @DisplayName("Test getting dropdown options with multiple pre-selected dropdown options tags")
    void testGettingDropdownOptionsWithMultipleSelectedDropdownOptionTags() throws IOException {

        List<String> selectedDropdownOptionTagsList = new ArrayList<>();
        selectedDropdownOptionTagsList.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
        selectedDropdownOptionTagsList.add(DrilldownSelectionToolTestConstants.DROPDOWN_2_OPTION_1_TAG_PATH);
        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(StringUtils.join(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH, CommonConstants.PIPE, DrilldownSelectionToolTestConstants.DROPDOWN_2_OPTION_1_TAG_PATH));

        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.NEXT_DROPDOWN_INDEX)).thenReturn("2");

        when(slingHttpServletRequest.adaptTo(DrilldownSelectionToolModel.class)).thenReturn(drilldownSelectionToolModel);
        when(drilldownSelectionToolModel.getDropdown(2, selectedDropdownOptionTagsList)).thenReturn(dropdownModel);
        when(dropdownModel.getDropdownOptions()).thenReturn(getTestDropdownOptions());

        setupResponseSuccess();
        dropdownOptionsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
        verifyResponseSuccess();

    }

    @Test
    @DisplayName("Test getting dropdown options with one pre-selected dropdown options tag")
    void testGettingDropdownOptionsWithOneSelectedDropdownOptionTags() throws IOException {

        List<String> selectedDropdownOptionTagsList = new ArrayList<>();
        selectedDropdownOptionTagsList.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);

        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.NEXT_DROPDOWN_INDEX)).thenReturn("1");

        when(slingHttpServletRequest.adaptTo(DrilldownSelectionToolModel.class)).thenReturn(drilldownSelectionToolModel);
        when(drilldownSelectionToolModel.getDropdown(1, selectedDropdownOptionTagsList)).thenReturn(dropdownModel);
        when(dropdownModel.getDropdownOptions()).thenReturn(getTestDropdownOptions());

        setupResponseSuccess();
        dropdownOptionsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
        verifyResponseSuccess();
    }

    @Test
    @DisplayName("Test getting dropdown options with no pre-selected dropdown options tags")
    void testGettingDropdownOptionsWithNoSelectedDropdownOptionTags() throws IOException {

        List<String> selectedDropdownOptionTagsList = new ArrayList<>();
        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(null);

        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.NEXT_DROPDOWN_INDEX)).thenReturn("1");

        when(slingHttpServletRequest.adaptTo(DrilldownSelectionToolModel.class)).thenReturn(drilldownSelectionToolModel);
        when(drilldownSelectionToolModel.getDropdown(1, selectedDropdownOptionTagsList)).thenReturn(dropdownModel);
        when(dropdownModel.getDropdownOptions()).thenReturn(getTestDropdownOptions());

        setupResponseSuccess();
        dropdownOptionsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
        verifyResponseSuccess();
    }

    @Test
    @DisplayName("Verify 500 response when nextDropdownIndex servlet parameter cannot be parsed")
    void verify500ResponseWhenDropdownIndexCannotBeParsed() throws IOException {
        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.NEXT_DROPDOWN_INDEX)).thenReturn("test");

        dropdownOptionsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while parsing nextDropdownIndex");

    }

    @Test
    @DisplayName("Verify 500 response when dropdownIndex servlet parameter not found")
    void verify500ResponseWhenDropdownIndexNotFound() throws IOException {
        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.NEXT_DROPDOWN_INDEX)).thenReturn(null);

        dropdownOptionsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while parsing nextDropdownIndex");

    }

    @Test
    @DisplayName("Verify 204 response when no valid dropdown options")
    void verify204ResponseWhenNoValidDropdownOptions() throws IOException {
        List<String> selectedDropdownOptionTagsList = new ArrayList<>();
        selectedDropdownOptionTagsList.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);

        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.NEXT_DROPDOWN_INDEX)).thenReturn("1");

        when(slingHttpServletRequest.adaptTo(DrilldownSelectionToolModel.class)).thenReturn(drilldownSelectionToolModel);
        when(drilldownSelectionToolModel.getDropdown(1, selectedDropdownOptionTagsList)).thenReturn(dropdownModel);
        when(dropdownModel.getDropdownOptions()).thenReturn(new ArrayList<>());

        dropdownOptionsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_NO_CONTENT, "No dropdown option values found with results");

    }

    @Test
    @DisplayName("Verify 500 response when error parsing/printing responseJson")
    void verify500ResponseWhenExceptionPrintingResponseJson() throws IOException {
        List<String> selectedDropdownOptionTagsList = new ArrayList<>();
        selectedDropdownOptionTagsList.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);

        when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.NEXT_DROPDOWN_INDEX)).thenReturn("1");

        when(slingHttpServletRequest.adaptTo(DrilldownSelectionToolModel.class)).thenReturn(drilldownSelectionToolModel);
        when(drilldownSelectionToolModel.getDropdown(1, selectedDropdownOptionTagsList)).thenReturn(dropdownModel);
        when(dropdownModel.getDropdownOptions()).thenReturn(getTestDropdownOptions());

        OngoingStubbing<PrintWriter> stub = Mockito.when(slingHttpServletResponse.getWriter());
        stub.thenThrow(new IOException("Mocked IOException"));

        dropdownOptionsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error printing json response.");

    }


}
