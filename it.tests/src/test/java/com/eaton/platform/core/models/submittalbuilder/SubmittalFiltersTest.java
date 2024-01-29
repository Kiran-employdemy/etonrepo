package com.eaton.platform.core.models.submittalbuilder;

import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.eaton.platform.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class SubmittalFiltersTest {
    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Before
    public void setup() throws Exception {
        context.load().json(EXAMPLE_COMPONENTS, EATON_CONTENT_PATH);
        context.addModelsForPackage(SUBMITTAL_BUILDER_MODELS_PACACKAGE);
    }

    @Test
    public void notAuthoredTest() {
        SubmittalFilters filters = context.resourceResolver()
                .getResource(SUBMITTAL_FILTERS_1)
                .adaptTo(SubmittalFilters.class);

        assertNotNull(filters);
        assertEquals(filters.getTitle(), null);
        assertEquals(filters.getMobileDialogTitle(), null);

    }

    @Test
    public void emptyPropertiesTest() {
        SubmittalFilters filters = context.resourceResolver()
                .getResource(SUBMITTAL_FILTERS_2)
                .adaptTo(SubmittalFilters.class);

        assertNotNull(filters);
        assertEquals(filters.getTitle(), "");
        assertEquals(filters.getMobileDialogTitle(), "");
    }

    @Test
    public void fullyAuthoredTest() {
        SubmittalFilters filters = context.resourceResolver()
                .getResource(SUBMITTAL_FILTERS_3)
                .adaptTo(SubmittalFilters.class);

        assertNotNull(filters);
        assertEquals(filters.getTitle(), "Example");
        assertEquals(filters.getMobileDialogTitle(), "example mobile dialog title");
    }

    @Test
    public void partiallyAuthoredTest() {
        SubmittalFilters filters = context.resourceResolver()
                .getResource(SUBMITTAL_FILTERS_4)
                .adaptTo(SubmittalFilters.class);

        assertNotNull(filters);
        assertEquals(filters.getTitle(), "Example");
        assertEquals(filters.getMobileDialogTitle(), null);
    }
}
