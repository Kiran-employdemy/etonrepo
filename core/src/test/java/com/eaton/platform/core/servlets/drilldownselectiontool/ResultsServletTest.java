package com.eaton.platform.core.servlets.drilldownselectiontool;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.DrilldownSelectionToolConstants;
import com.eaton.platform.core.models.drilldownselectiontool.DrilldownSelectionToolModel;
import com.eaton.platform.core.models.drilldownselectiontool.DrilldownSelectionToolTestConstants;
import com.eaton.platform.core.models.drilldownselectiontool.ResultsItemModel;
import com.eaton.platform.core.models.drilldownselectiontool.ResultsModel;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
public class ResultsServletTest {
    private static final String RESPONSE_JSON_STRING_EXPECTED = "{\"total\":3,\"resultsItemModelList\":[{\"path\":\"/content/eaton/us/en-us/support/test-page-1\",\"title\":\"Test page 1\",\"description\":\"\\u003cp\\u003eTest page 1 product grid description\\u003c/p\\u003e\",\"imagePath\":\"/content/dam/eaton/test-image-1\"},{\"path\":\"/content/eaton/us/en-us/support/test-page-2\",\"title\":\"Test page 2\",\"description\":\"\\u003cp\\u003eTest page 2 product grid description\\u003c/p\\u003e\",\"imagePath\":\"/content/dam/eaton/test-image-2\"},{\"path\":\"/content/eaton/us/en-us/support/test-page-3\",\"title\":\"Test page 3\",\"description\":\"\\u003cp\\u003eTest page 3 product grid description\\u003c/p\\u003e\",\"imagePath\":\"/content/dam/eaton/test-image-3\"}]}";

    @InjectMocks
    ResultsServlet resultsServlet = new ResultsServlet();

    @Mock
    private SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    private SlingHttpServletResponse slingHttpServletResponse;
    @Mock
    private DrilldownSelectionToolModel drilldownSelectionToolModel;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private EatonSiteConfigService eatonSiteConfigService;
    @Mock
    private ResultsModel resultsModel;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    private Page currentPage;
    private ResourceResolver resourceResolver;

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

    private ResultsItemModel getResultsItemModel(String path, String title, String description, String imagePath) {
        ResultsItemModel resultsItemModel = new ResultsItemModel();
        resultsItemModel.setPath(path);
        resultsItemModel.setTitle(title);
        resultsItemModel.setDescription(description);
        resultsItemModel.setImagePath(imagePath);
        return resultsItemModel;
    }

    private ResultsModel getResultsModel() {
        ResultsModel resultsModel = new ResultsModel();
        resultsModel.setTotal(3);
        List<ResultsItemModel> resultsItemModels = new ArrayList<>();

        resultsItemModels.add(getResultsItemModel(
            DrilldownSelectionToolTestConstants.PAGE_RESULT_1_PATH,
            DrilldownSelectionToolTestConstants.PAGE_RESULT_1_TITLE,
            DrilldownSelectionToolTestConstants.PAGE_RESULT_1_DESCRIPTION,
            DrilldownSelectionToolTestConstants.PAGE_RESULT_1_IMAGE_PATH));

        resultsItemModels.add(getResultsItemModel(
            DrilldownSelectionToolTestConstants.PAGE_RESULT_2_PATH,
            DrilldownSelectionToolTestConstants.PAGE_RESULT_2_TITLE,
            DrilldownSelectionToolTestConstants.PAGE_RESULT_2_DESCRIPTION,
            DrilldownSelectionToolTestConstants.PAGE_RESULT_2_IMAGE_PATH));

        resultsItemModels.add(getResultsItemModel(
            DrilldownSelectionToolTestConstants.PAGE_RESULT_3_PATH,
            DrilldownSelectionToolTestConstants.PAGE_RESULT_3_TITLE,
            DrilldownSelectionToolTestConstants.PAGE_RESULT_3_DESCRIPTION,
            DrilldownSelectionToolTestConstants.PAGE_RESULT_3_IMAGE_PATH));


        resultsModel.setResultsItemModelList(resultsItemModels);

        return resultsModel;
    }

    @Nested
    class ResultsModelSuccessTest {

        int startIndexInt;
        int pageSizeInt;
        List<String> selectedDropdownOptionTags = new ArrayList<>();
        @BeforeEach
        void setup() {
            String startIndexString = "0";
            startIndexInt = 0;
            String pageSizeString = "4";
            pageSizeInt = 4;

            when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.START_INDEX_PARAM_NAME)).thenReturn(startIndexString);
            when(slingHttpServletRequest.getParameter(CommonConstants.PAGE_SIZE)).thenReturn(pageSizeString);
        }

        @AfterEach
        void afterOperations() throws IOException {
            when(slingHttpServletRequest.adaptTo(DrilldownSelectionToolModel.class)).thenReturn(drilldownSelectionToolModel);
            when(drilldownSelectionToolModel.getResultsModel(selectedDropdownOptionTags, startIndexInt, pageSizeInt)).thenReturn(getResultsModel());

            setupResponseSuccess();
            resultsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
            verifyResponseSuccess();
        }


        @Test
        @DisplayName("Test getting results model with 1 selected option tag")
        void testGetResultsModelWithOneSelectedOptionTag() throws IOException {
            selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
            when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
        }

        @Test
        @DisplayName("Test getting results model with more than 1 selected option tag")
        void testGetResultsModelWithMoreThanOneSelectedOptionTag() throws IOException {
            selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
            selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_2_OPTION_1_TAG_PATH);
            when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(StringUtils.join(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH, CommonConstants.PIPE, DrilldownSelectionToolTestConstants.DROPDOWN_2_OPTION_1_TAG_PATH));
        }


        @Test
        @DisplayName("Test getting results model with no selected option tags")
        void testGetResultsModelWithNoSelectedOptionTag() throws IOException {
            when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(null);
        }
    }

    @Nested
    class ResultsModelFailureTest {

        private void setupServletParameters(String selectedDropdownOptionTags, String startIndex, String pageSize) {
            when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.SELECTED_DROPDOWN_OPTION_TAGS)).thenReturn(selectedDropdownOptionTags);
            when(slingHttpServletRequest.getParameter(DrilldownSelectionToolConstants.START_INDEX_PARAM_NAME)).thenReturn(startIndex);
            if (pageSize != null) {
                when(slingHttpServletRequest.getParameter(CommonConstants.PAGE_SIZE)).thenReturn(pageSize);
            }
        }
        @Test
        @DisplayName("Verify 400 response when error parsing startIndex")
        void verify400ResponseWhenErrorParsingStartIndex() throws IOException {
            setupServletParameters(null, "not-a-number", null);

            resultsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
            verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while parsing startIndex");

        }

        @Test
        @DisplayName("Verify 400 response when error parsing pageSize")
        void verify400ResponseWhenErrorParsingPageSize() throws IOException {
            setupServletParameters(null, "0", "not-a-number");

            resultsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
            verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while parsing pageSize");
        }


        @Test
        @DisplayName("Verify 204 response when no results found")
        void verify204ResponseWhenNoResultsFound() throws IOException {
            setupServletParameters(null, "0", "10");
            when(slingHttpServletRequest.adaptTo(DrilldownSelectionToolModel.class)).thenReturn(drilldownSelectionToolModel);
            when(drilldownSelectionToolModel.getResultsModel(new ArrayList<>(), 0, 10)). thenReturn(resultsModel);
            when(resultsModel.getResultsItemModelList()).thenReturn(new ArrayList<>());

            resultsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
            verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_NO_CONTENT, "No results found");
        }

        @Test
        @DisplayName("Verify 500 response when error printing json response")
        void verify500ResponseWhenErrorPrintingJsonResponse() throws IOException {
            setupServletParameters(null, "0", "10");
            when(slingHttpServletRequest.adaptTo(DrilldownSelectionToolModel.class)).thenReturn(drilldownSelectionToolModel);
            when(drilldownSelectionToolModel.getResultsModel(new ArrayList<>(), 0, 10)). thenReturn(resultsModel);
            when(resultsModel.getResultsItemModelList()).thenReturn(getResultsModel().getResultsItemModelList());

            OngoingStubbing<PrintWriter> stub = Mockito.when(slingHttpServletResponse.getWriter());
            stub.thenThrow(new IOException("Mocked IOException"));

            resultsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
            verify(slingHttpServletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error printing json response.");

        }

    }



}
