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
public class SubmittalIntroTest {
    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Before
    public void setup() throws Exception {
        context.load().json(EXAMPLE_COMPONENTS, EATON_CONTENT_PATH);
        context.addModelsForPackage(SUBMITTAL_BUILDER_MODELS_PACACKAGE);
    }

    @Test
    public void notAuthoredTest() {
        SubmittalIntro intro = context.resourceResolver()
                .getResource(SUBMITTAL_INTRO_1)
                .adaptTo(SubmittalIntro.class);

        assertNotNull(intro);
        assertEquals(intro.getTitle(), null);
        assertEquals(intro.getDescription(), null);
        assertEquals(intro.getInstructionsTitle(), null);
        assertEquals(intro.getInstructions(), null);
        assertEquals(intro.getFiltersButtonText(), null);
        assertEquals(intro.getPackageButtonText(), null);
        assertEquals(intro.getDownloadButtonText(), null);
        assertEquals(intro.getViewSubmittalBuilderButtonText(), null);
        assertEquals(intro.getEditSubmittalBuilderButtonText(), null);
        assertEquals(intro.getFinishEditsButtonText(), null);
    }

    @Test
    public void emptyPropertiesTest() {
        SubmittalIntro intro = context.resourceResolver()
                .getResource(SUBMITTAL_INTRO_2)
                .adaptTo(SubmittalIntro.class);

        assertNotNull(intro);
        assertEquals(intro.getTitle(), "");
        assertEquals(intro.getDescription(), "");
        assertEquals(intro.getInstructionsTitle(), "");
        assertEquals(intro.getInstructions(), "");
        assertEquals(intro.getFiltersButtonText(), "");
        assertEquals(intro.getPackageButtonText(), "");
        assertEquals(intro.getDownloadButtonText(), "");
        assertEquals(intro.getViewSubmittalBuilderButtonText(), "");
        assertEquals(intro.getEditSubmittalBuilderButtonText(), "");
        assertEquals(intro.getFinishEditsButtonText(), "");
    }

    @Test
    public void fullyAuthoredTest() {
        SubmittalIntro intro = context.resourceResolver()
                .getResource(SUBMITTAL_INTRO_3)
                .adaptTo(SubmittalIntro.class);

        assertNotNull(intro);
        assertEquals(intro.getTitle(), "Example");
        assertEquals(intro.getDescription(), "Some stuff");
        assertEquals(intro.getInstructionsTitle(), "Another title");
        assertEquals(intro.getInstructions(), "A lot longer text than the other properties for testing purposes.");
        assertEquals(intro.getFiltersButtonText(), "Button A");
        assertEquals(intro.getPackageButtonText(), "Button B");
        assertEquals(intro.getDownloadButtonText(), "Button C");
        assertEquals(intro.getViewSubmittalBuilderButtonText(), "View Button Text");
        assertEquals(intro.getEditSubmittalBuilderButtonText(), "Edit Button Text");
        assertEquals(intro.getFinishEditsButtonText(), "Finish Button Text");
    }

    @Test
    public void partiallyAuthoredTest() {
        SubmittalIntro intro = context.resourceResolver()
                .getResource(SUBMITTAL_INTRO_4)
                .adaptTo(SubmittalIntro.class);

        assertNotNull(intro);
        assertEquals(intro.getTitle(), "Example");
        assertEquals(intro.getDescription(), "");
        assertEquals(intro.getInstructionsTitle(), "");
        assertEquals(intro.getInstructions(), null);
        assertEquals(intro.getFiltersButtonText(), "Button A");
        assertEquals(intro.getPackageButtonText(), null);
        assertEquals(intro.getDownloadButtonText(), "");
        assertEquals(intro.getViewSubmittalBuilderButtonText(), "");
        assertEquals(intro.getEditSubmittalBuilderButtonText(), "");
        assertEquals(intro.getFinishEditsButtonText(), "");
    }
}
