package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.wcm.api.Page;
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
public class DropdownModelTest {

    @InjectMocks
    DropdownModel dropdownModel = new DropdownModel();

    @Mock
    private SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    private AuthorizationService authorizationService;


    private ResourceResolver resourceResolver;
    private Page currentPage;

    @BeforeEach
    void setup(AemContext aemContext) {

        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("SupportCategoryTags.json")), DrilldownSelectionToolTestConstants.DROPDOWN_1_TAG_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("ProductCategoryTags.json")), DrilldownSelectionToolTestConstants.DROPDOWN_2_TAG_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("Homepage.json")), DrilldownSelectionToolTestConstants.HOMEPAGE_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("PageResults.json")), DrilldownSelectionToolTestConstants.PAGE_RESULTS_PATH);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("PageWithComponent.json")), DrilldownSelectionToolTestConstants.PAGE_WITH_COMPONENT_PATH);

        currentPage = aemContext.currentPage(DrilldownSelectionToolTestConstants.PAGE_WITH_COMPONENT_PATH);

        resourceResolver = aemContext.resourceResolver();
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
    }

    @Test
    @DisplayName("Test setting dropdown options when no previous dropdown options have been selected")
    void testSetDropdownOptionsWhenNoPreviousOptionsSelected() {

        List<String> selectedDropdownOptionTags = new ArrayList<>();

        dropdownModel.setLabel(DrilldownSelectionToolTestConstants.DROPDOWN_1_LABEL);
        dropdownModel.setDropdownTagPath(DrilldownSelectionToolTestConstants.DROPDOWN_1_TAG_PATH);

        dropdownModel.setDropdownOptions(selectedDropdownOptionTags, slingHttpServletRequest, currentPage);

        List<DropdownOptionModel> dropdownOptions = dropdownModel.getDropdownOptions();
        Assertions.assertEquals(2, dropdownOptions.size(), "should equal 2");
        Assertions.assertEquals(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_2_TAG_PATH, dropdownOptions.get(0).getTagPath(), "should equal");
        Assertions.assertEquals(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_2_TITLE_EN_US, dropdownOptions.get(0).getTitle(), "should equal");
        Assertions.assertEquals(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_3_TAG_PATH, dropdownOptions.get(1).getTagPath(), "should equal");
        Assertions.assertEquals(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_3_TITLE_EN_US, dropdownOptions.get(1).getTitle(), "should equal");

    }

    @Test
    @DisplayName("Test setting dropdown options when previous dropdown options have been selected")
    void testSetDropdownOptionsWhenPreviousOptionsSelected() {

        List<String> selectedDropdownOptionTags = new ArrayList<>();

        selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);

        dropdownModel.setLabel(DrilldownSelectionToolTestConstants.DROPDOWN_2_LABEL);
        dropdownModel.setDropdownTagPath(DrilldownSelectionToolTestConstants.DROPDOWN_2_TAG_PATH);

        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_1_PATH_JCR_CONTENT)).thenReturn(false);
        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_2_PATH_JCR_CONTENT)).thenReturn(true);
        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_3_PATH_JCR_CONTENT)).thenReturn(true);

        dropdownModel.setDropdownOptions(selectedDropdownOptionTags, slingHttpServletRequest, currentPage);

        List<DropdownOptionModel> dropdownOptions = dropdownModel.getDropdownOptions();
        Assertions.assertEquals(2, dropdownOptions.size(), "should equal 2");
        Assertions.assertEquals(DrilldownSelectionToolTestConstants.DROPDOWN_2_OPTION_1_TAG_PATH, dropdownOptions.get(0).getTagPath(), "should equal");
        Assertions.assertEquals(DrilldownSelectionToolTestConstants.DROPDOWN_2_OPTION_1_TITLE_EN_US, dropdownOptions.get(0).getTitle(), "should equal");
        Assertions.assertEquals(DrilldownSelectionToolTestConstants.DROPDOWN_2_OPTION_2_TAG_PATH, dropdownOptions.get(1).getTagPath(), "should equal");
        Assertions.assertEquals(DrilldownSelectionToolTestConstants.DROPDOWN_2_OPTION_2_TITLE_EN_US, dropdownOptions.get(1).getTitle(), "should equal");

    }


    @Test
    @DisplayName("Test setting dropdown options when all potential dropdown options don't have results")
    void testSetDropdownOptionsWhenAllInvalidDropdownOptions() {
        List<String> selectedDropdownOptionTags = new ArrayList<>();

        selectedDropdownOptionTags.add(DrilldownSelectionToolTestConstants.DROPDOWN_1_OPTION_1_TAG_PATH);

        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_1_PATH_JCR_CONTENT)).thenReturn(false);
        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_2_PATH_JCR_CONTENT)).thenReturn(false);
        when(authorizationService.isAuthorized(slingHttpServletRequest, DrilldownSelectionToolTestConstants.PAGE_RESULT_3_PATH_JCR_CONTENT)).thenReturn(false);

        dropdownModel.setLabel(DrilldownSelectionToolTestConstants.DROPDOWN_2_LABEL);
        dropdownModel.setDropdownTagPath(DrilldownSelectionToolTestConstants.DROPDOWN_2_TAG_PATH);

        dropdownModel.setDropdownOptions(selectedDropdownOptionTags, slingHttpServletRequest, currentPage);

        List<DropdownOptionModel> dropdownOptions = dropdownModel.getDropdownOptions();
        Assertions.assertEquals(0, dropdownOptions.size(), "should equal 0");

    }



}
