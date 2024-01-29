package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.constants.CommonConstants;

public class DrilldownSelectionToolTestConstants {

    public static final String PAGE_WITH_COMPONENT_PATH = "/content/eaton/us/en-us/test/drilldown-selection-tool-test";
    public static final String HOMEPAGE_PATH = "/content/eaton/us/en-us";

    public static final String PAGE_RESULTS_PATH = "/content/eaton/us/en-us/support";

    public static final String PAGE_RESULT_1_PATH = PAGE_RESULTS_PATH + "/test-page-1";
    public static final String PAGE_RESULT_1_PATH_JCR_CONTENT = PAGE_RESULT_1_PATH + CommonConstants.SLASH_STRING + JcrConstants.JCR_CONTENT;
    public static final String PAGE_RESULT_1_TITLE = "Test page 1";
    public static final String PAGE_RESULT_1_IMAGE_PATH = "/content/dam/eaton/test-image-1";
    public static final String PAGE_RESULT_1_DESCRIPTION = "<p>Test page 1 product grid description</p>";
    public static final String PAGE_RESULT_2_PATH = PAGE_RESULTS_PATH + "/test-page-2";
    public static final String PAGE_RESULT_2_PATH_JCR_CONTENT = PAGE_RESULT_2_PATH + CommonConstants.SLASH_STRING + JcrConstants.JCR_CONTENT;
    public static final String PAGE_RESULT_2_TITLE = "Test page 2";
    public static final String PAGE_RESULT_2_IMAGE_PATH = "/content/dam/eaton/test-image-2";
    public static final String PAGE_RESULT_2_DESCRIPTION = "<p>Test page 2 product grid description</p>";
    public static final String PAGE_RESULT_3_PATH = PAGE_RESULTS_PATH + "/test-page-3";
    public static final String PAGE_RESULT_3_PATH_JCR_CONTENT = PAGE_RESULT_3_PATH + CommonConstants.SLASH_STRING + JcrConstants.JCR_CONTENT;
    public static final String PAGE_RESULT_3_TITLE = "Test page 3";
    public static final String PAGE_RESULT_3_IMAGE_PATH = "/content/dam/eaton/test-image-3";
    public static final String PAGE_RESULT_3_DESCRIPTION = "<p>Test page 3 product grid description</p>";
    public static final String PAGE_RESULT_4_PATH = PAGE_RESULTS_PATH + "/test-page-4";
    public static final String PAGE_RESULT_4_PATH_JCR_CONTENT = PAGE_RESULT_4_PATH + CommonConstants.SLASH_STRING + JcrConstants.JCR_CONTENT;
    public static final String PAGE_RESULT_5_PATH = PAGE_RESULTS_PATH + "/test-page-5";
    public static final String PAGE_RESULT_5_PATH_JCR_CONTENT = PAGE_RESULT_5_PATH + CommonConstants.SLASH_STRING + JcrConstants.JCR_CONTENT;
    public static final String PAGE_RESULT_6_PATH = PAGE_RESULTS_PATH + "/test-page-6";
    public static final String PAGE_RESULT_6_PATH_JCR_CONTENT = PAGE_RESULT_6_PATH + CommonConstants.SLASH_STRING + JcrConstants.JCR_CONTENT;

    public static final String DROPDOWN_1_TAG_PATH = "/content/cq:tags/eaton/support-taxonomy/category";
    public static final String DROPDOWN_1_LABEL = "Select the type of support you're looking for";
    public static final String DROPDOWN_1_OPTION_1_TAG_PATH = DROPDOWN_1_TAG_PATH + "/technical-support";
    public static final String DROPDOWN_1_OPTION_1_TITLE_EN_US = "Contact technical support";
    public static final String DROPDOWN_1_OPTION_2_TAG_PATH = DROPDOWN_1_TAG_PATH + "/training-education";
    public static final String DROPDOWN_1_OPTION_2_TITLE_EN_US = "Training and education";
    public static final String DROPDOWN_1_OPTION_3_TAG_PATH = DROPDOWN_1_TAG_PATH + "/tools";
    public static final String DROPDOWN_1_OPTION_3_TITLE_EN_US = "Tools";
    public static final String DROPDOWN_1_OPTION_4_TAG_PATH = DROPDOWN_1_TAG_PATH + "/become-partner";

    public static final String DROPDOWN_2_TAG_PATH = "/content/cq:tags/eaton/services/product-category";
    public static final String DROPDOWN_2_LABEL = "Select the type of product";
    public static final String DROPDOWN_2_OPTION_1_TAG_PATH = DROPDOWN_2_TAG_PATH + "/furniture";
    public static final String DROPDOWN_2_OPTION_1_TITLE_EN_US = "Furniture";
    public static final String DROPDOWN_2_OPTION_2_TAG_PATH = DROPDOWN_2_TAG_PATH + "/aerospace-control";
    public static final String DROPDOWN_2_OPTION_2_TITLE_EN_US = "Aerospace actuators and motion control";
}
