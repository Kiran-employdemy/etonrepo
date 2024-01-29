package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ResultsModelTest {


    @InjectMocks
    ResultsModel resultsModel = new ResultsModel();

    @Mock
    private SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private EatonSiteConfigService eatonSiteConfigService;


    private ResourceResolver resourceResolver;
    private Page currentPage;

    @BeforeEach
    void setup(AemContext aemContext){
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("SupportCategoryTags.json")), DrilldownSelectionToolTestConstants.DROPDOWN_1_TAG_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("ProductCategoryTags.json")), DrilldownSelectionToolTestConstants.DROPDOWN_2_TAG_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("Homepage.json")), DrilldownSelectionToolTestConstants.HOMEPAGE_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("PageResults.json")), DrilldownSelectionToolTestConstants.PAGE_RESULTS_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("PageWithComponent.json")), DrilldownSelectionToolTestConstants.PAGE_WITH_COMPONENT_PATH);

        currentPage = aemContext.currentPage(DrilldownSelectionToolTestConstants.PAGE_WITH_COMPONENT_PATH);
        resourceResolver = aemContext.resourceResolver();
    }

    @Test
    @DisplayName("Test getting a ResultsModel with results")
    void testGetResultsModelWithResults() {

        List<String> selectedDropdownOptionTags = new ArrayList<>();
        selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);

        int startIndex = 0;
        int pageSize = 1;

        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_1_PATH_JCR_CONTENT)).thenReturn(false);
        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_2_PATH_JCR_CONTENT)).thenReturn(true);
        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_3_PATH_JCR_CONTENT)).thenReturn(true);

        ResultsModel resultsModel1 = ResultsModel.of(slingHttpServletRequest, authorizationService, resourceResolver, eatonSiteConfigService, currentPage, selectedDropdownOptionTags, startIndex, pageSize);

        Assertions.assertEquals(1, resultsModel1.getResultsItemModelList().size(), "should equal");
        Assertions.assertEquals(2, resultsModel1.getTotal(), "should equal");

    }

    @Test
    @DisplayName("Test getting a ResultsModel with no results")
    void testGetResultsModelWithNoResults() {

        List<String> selectedDropdownOptionTags = new ArrayList<>();
        selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_4_TAG_PATH);

        int startIndex = 0;
        int pageSize = 2;

        ResultsModel resultsModel1 = ResultsModel.of(slingHttpServletRequest, authorizationService, resourceResolver, eatonSiteConfigService, currentPage, selectedDropdownOptionTags, startIndex, pageSize);

        Assertions.assertEquals(0, resultsModel1.getResultsItemModelList().size(), "should equal");
        Assertions.assertEquals(0, resultsModel1.getTotal(), "should equal");
    }

    @Test
    @DisplayName("Test getting a ResultsModel with no authorized access to results")
    void testGetResultsModelWithNoAuthorizedAccessToResults() {

        List<String> selectedDropdownOptionTags = new ArrayList<>();
        selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);

        int startIndex = 0;
        int pageSize = 1;

        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_1_PATH_JCR_CONTENT)).thenReturn(false);
        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_2_PATH_JCR_CONTENT)).thenReturn(false);
        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_3_PATH_JCR_CONTENT)).thenReturn(false);

        ResultsModel resultsModel1 = ResultsModel.of(slingHttpServletRequest, authorizationService, resourceResolver, eatonSiteConfigService, currentPage, selectedDropdownOptionTags, startIndex, pageSize);

        Assertions.assertEquals(0, resultsModel1.getResultsItemModelList().size(), "should equal");
        Assertions.assertEquals(0, resultsModel1.getTotal(), "should equal");
    }

    @Test
    @DisplayName("Test getting a ResultsModel with multiple selected dropdown option tags")
    void testGetResultsModelWithMultipleSelectedDropdownOptionTags() {

        List<String> selectedDropdownOptionTags = new ArrayList<>();
        selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);
        selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_2_OPTION_1_TAG_PATH);

        int startIndex = 0;
        int pageSize = 1;

        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_1_PATH_JCR_CONTENT)).thenReturn(true);
        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_3_PATH_JCR_CONTENT)).thenReturn(true);

        ResultsModel resultsModel1 = ResultsModel.of(slingHttpServletRequest, authorizationService, resourceResolver, eatonSiteConfigService, currentPage, selectedDropdownOptionTags, startIndex, pageSize);

        Assertions.assertEquals(1, resultsModel1.getResultsItemModelList().size(), "should equal");
        Assertions.assertEquals(2, resultsModel1.getTotal(), "should equal");
    }


}
