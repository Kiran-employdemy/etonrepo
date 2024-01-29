package com.eaton.platform.samplemocks;

import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SamplePageMockTest {

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() throws Exception {
        context.load().json("/EatonPageMock.json", "/content/eaton/en-us/product");
    }

    @Test
    public void testPageTitle() {
        final Resource resource = context.resourceResolver().getResource("/content/eaton/en-us/product");
        final Page page = resource.adaptTo(Page.class);

        assert page.getTitle().equals("Global Prime (EN-US)");
    }
}
