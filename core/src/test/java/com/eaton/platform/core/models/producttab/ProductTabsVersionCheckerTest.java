package com.eaton.platform.core.models.producttab;

import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

@ExtendWith({AemContextExtension.class})
class ProductTabsVersionCheckerTest {

    Page pageWithoutProductTabs;
    Page pageWithVersion1OfTabs;
    Page pageWithVersion2OfTabs;

    @BeforeEach
    void setUp(AemContext aemContext) {
        Resource resource = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("pageWithVersion1OfTabs.json")), "/content/pageWithVersion1OfTabs");
        pageWithVersion1OfTabs = resource.adaptTo(Page.class);
        resource = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("pageWithVersion2OfTabs.json")), "/content/pageWithVersion2OfTabs");
        pageWithVersion2OfTabs = resource.adaptTo(Page.class);
        resource = aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("pageWithoutProductTabs.json")), "/content/pageWithoutProductTabs");
        pageWithoutProductTabs = resource.adaptTo(Page.class);
    }

    @Test
    void testIfEverythingIsLoadedCorrectly() {
        Assertions.assertNotNull(pageWithoutProductTabs, "may not be null");
        Assertions.assertNotNull(pageWithVersion1OfTabs, "may not be null");
        Assertions.assertNotNull(pageWithVersion2OfTabs, "may not be null");
    }

    @Test
    @DisplayName("isVersion2 yields false on page without product tabs")
    void testIsVersion2OnPageWithoutProductTabs() {
        Assertions.assertFalse(new ProductTabsVersionChecker().isVersion2(pageWithoutProductTabs), "should be false");
    }

    @Test
    @DisplayName("isVersion2 yields false on page with verion 1 of tabs")
    void testIsVersion2OnPageWithVersion1OfTabs() {
        Assertions.assertFalse(new ProductTabsVersionChecker().isVersion2(pageWithVersion1OfTabs), "should be false");
    }

    @Test
    @DisplayName("isVersion2 yields true on page with verion 2 of tabs")
    void testIsVersion2OnPageWithVersion2OfTabs() {
        Assertions.assertTrue(new ProductTabsVersionChecker().isVersion2(pageWithVersion2OfTabs), "should be true");
    }
}