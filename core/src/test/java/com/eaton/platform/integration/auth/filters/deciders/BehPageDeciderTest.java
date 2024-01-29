/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.eaton.platform.integration.auth.filters.deciders;

import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.eaton.platform.integration.auth.constants.SecureConstants.SECURE_DIGITAL_CONTENT_PATH_SUFFIX;
import static com.eaton.platform.integration.auth.constants.SecureConstants.SOFTWARE_DELIVERY_TEMPLATE_PATH;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
public class BehPageDeciderTest {

    AemContext context = new AemContextBuilder().build();
    Page testPage;
    String pagePath = SECURE_DIGITAL_CONTENT_PATH_SUFFIX + "/testpage";

    String redirectTo = "/content/awesome/redirect/page";

    @Test
    @DisplayName(value = "Test to Ensure if template or path match should return condition matched")
    void ensureConditionMatchedWhenTemplateAndPathSuffixMatch(){
        testPage = context.create().page(pagePath,SOFTWARE_DELIVERY_TEMPLATE_PATH);
        BehPageDecider testable = new BehPageDecider(testPage,redirectTo);
        Assertions.assertTrue(testable.conditionMatched());
        Assertions.assertEquals(testable.redirectTo(),redirectTo);
    }

    @Test
    @DisplayName(value = "Test to ensure if template or path does not match should return condition matched false")
    void ensureConditionDoesNotMatchWhenTemplateDoesNotMatch(){
        testPage = context.create().page("/content/awesome/some/path","not-found/template");
        BehPageDecider testable = new BehPageDecider(testPage,"");
        Assertions.assertFalse(testable.conditionMatched());
    }
}
