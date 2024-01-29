package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.EatonSiteConfigService;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ResultsItemModelTest {

    private static final String PAGE_WITH_COMPONENT_PATH = "/content/eaton/us/en-us/test/drilldown-selection-tool-test";
    private static final String PAGE_WITH_COMPONENT_PATH_WITH_JCR_CONTENT = PAGE_WITH_COMPONENT_PATH + "/jcr:content";
    private static final String PAGE_RESULT_TITLE = "test-page-1";
    private static final String PAGE_RESULT_TEASER_IMAGE_PATH = "/content/dam/eaton/resources/image.png";
    private static final String FALLBACK_IMAGE_PATH_IMAGE_PATH = "/content/dam/eaton/resources/fallback.png";
    private static final String PAGE_RESULT_PRODUCT_GRID_DESCRIPTION = "<p>Test product grid description</P>";


    @InjectMocks
    ResultsItemModel resultsItemModel = new ResultsItemModel();

    @Mock
    private Page currentPage;

    @Mock
    private Resource resource;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private EatonSiteConfigService eatonSiteConfigService;

    @Mock
    private PageManager pageManager;

    @Mock
    private Page resultsPage;

    @Mock
    private ValueMap valueMap;

    @Mock
    private SiteResourceSlingModel siteResourceSlingModel;


    @BeforeEach
    void setup(){
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);

        when(resource.getPath()).thenReturn(PAGE_WITH_COMPONENT_PATH_WITH_JCR_CONTENT);
        when(pageManager.getPage(PAGE_WITH_COMPONENT_PATH)).thenReturn(resultsPage);
        when(resultsPage.getProperties()).thenReturn(valueMap);
        when(valueMap.get(JcrConstants.JCR_TITLE)).thenReturn(PAGE_RESULT_TITLE);
    }

    @Test
    @DisplayName("Test getting a new ResultsItemModel with a valid resource")
    void testResultsItemModelOfWithValidResource() {

        when(valueMap.get(CommonConstants.TEASER_IMAGE_PATH)).thenReturn(PAGE_RESULT_TEASER_IMAGE_PATH);
        when(valueMap.get(CommonConstants.PRODUCT_GRID_DESCRIPTION)).thenReturn(PAGE_RESULT_PRODUCT_GRID_DESCRIPTION);

        ResultsItemModel resultsItemModel1 = ResultsItemModel.of(resourceResolver, resource, eatonSiteConfigService, currentPage);

        String pageWithComponentPathHtml = StringUtils.join(PAGE_WITH_COMPONENT_PATH, CommonConstants.HTML_EXTN);
        Assertions.assertEquals(pageWithComponentPathHtml, resultsItemModel1.getPath(), "should equal");
        Assertions.assertEquals(PAGE_RESULT_TITLE, resultsItemModel1.getTitle(), "should equal");
        Assertions.assertEquals(PAGE_RESULT_TEASER_IMAGE_PATH, resultsItemModel1.getImagePath(), "should equal");
        Assertions.assertEquals(PAGE_RESULT_PRODUCT_GRID_DESCRIPTION, resultsItemModel1.getDescription(), "should equal");

    }

    @Test
    @DisplayName("Test getting a new ResultsItemModel when resource doesn't have a teaser image path and site config does exist")
    void testResultsItemModelOfWithNoTeaserImagePathAndSiteConfigValid() {
        when(valueMap.get(CommonConstants.PRODUCT_GRID_DESCRIPTION)).thenReturn(PAGE_RESULT_PRODUCT_GRID_DESCRIPTION);
        when(valueMap.get(CommonConstants.TEASER_IMAGE_PATH)).thenReturn(null);
        when(eatonSiteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.ofNullable(siteResourceSlingModel));
        when(siteResourceSlingModel.getSkuFallBackImage()).thenReturn(FALLBACK_IMAGE_PATH_IMAGE_PATH);


        ResultsItemModel resultsItemModel1 = ResultsItemModel.of(resourceResolver, resource, eatonSiteConfigService, currentPage);

        String pageWithComponentPathHtml = StringUtils.join(PAGE_WITH_COMPONENT_PATH, CommonConstants.HTML_EXTN);
        Assertions.assertEquals(pageWithComponentPathHtml, resultsItemModel1.getPath(), "should equal");
        Assertions.assertEquals(PAGE_RESULT_TITLE, resultsItemModel1.getTitle(), "should equal");
        Assertions.assertEquals(FALLBACK_IMAGE_PATH_IMAGE_PATH, resultsItemModel1.getImagePath(), "should equal");
        Assertions.assertEquals(PAGE_RESULT_PRODUCT_GRID_DESCRIPTION, resultsItemModel1.getDescription(), "should equal");
    }

    @Test
    @DisplayName("Test getting a new ResultsItemModel when resource doesn't have a teaser image path and site config doesn't exist")
    void testResultsItemModelOfWithNoTeaserImagePathAndSiteConfigNotValid() {
        when(valueMap.get(CommonConstants.PRODUCT_GRID_DESCRIPTION)).thenReturn(PAGE_RESULT_PRODUCT_GRID_DESCRIPTION);
        when(valueMap.get(CommonConstants.TEASER_IMAGE_PATH)).thenReturn(null);

        when(eatonSiteConfigService.getSiteConfig(currentPage)).thenReturn(Optional.empty());

        ResultsItemModel resultsItemModel1 = ResultsItemModel.of(resourceResolver, resource, eatonSiteConfigService, currentPage);

        String pageWithComponentPathHtml = StringUtils.join(PAGE_WITH_COMPONENT_PATH, CommonConstants.HTML_EXTN);
        Assertions.assertEquals(pageWithComponentPathHtml, resultsItemModel1.getPath(), "should equal");
        Assertions.assertEquals(PAGE_RESULT_TITLE, resultsItemModel1.getTitle(), "should equal");
        Assertions.assertEquals(StringUtils.EMPTY, resultsItemModel1.getImagePath(), "should equal");
        Assertions.assertEquals(PAGE_RESULT_PRODUCT_GRID_DESCRIPTION, resultsItemModel1.getDescription(), "should equal");
    }

    @Test
    @DisplayName("Test getting a new ResultsItemModel when resource doesn't have product grid description")
    void testResultsItemModelOfWithNoProductGridDescription() {
        when(valueMap.get(CommonConstants.PRODUCT_GRID_DESCRIPTION)).thenReturn(null);
        when(valueMap.get(CommonConstants.TEASER_IMAGE_PATH)).thenReturn(PAGE_RESULT_TEASER_IMAGE_PATH);

        ResultsItemModel resultsItemModel1 = ResultsItemModel.of(resourceResolver, resource, eatonSiteConfigService, currentPage);

        String pageWithComponentPathHtml = StringUtils.join(PAGE_WITH_COMPONENT_PATH, CommonConstants.HTML_EXTN);
        Assertions.assertEquals(pageWithComponentPathHtml, resultsItemModel1.getPath(), "should equal");
        Assertions.assertEquals(PAGE_RESULT_TITLE, resultsItemModel1.getTitle(), "should equal");
        Assertions.assertEquals(PAGE_RESULT_TEASER_IMAGE_PATH, resultsItemModel1.getImagePath(), "should equal");
        Assertions.assertEquals(StringUtils.EMPTY, resultsItemModel1.getDescription(), "should equal");
    }


}
