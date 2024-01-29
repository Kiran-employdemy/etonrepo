package com.eaton.platform.core.models.submittalbuilder;

import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static com.eaton.platform.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class SubmittalScopeTest {
    private final static String SUBMITTAL_SCOPE_NAME = "submittalScope";

    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Before
    public void setup() throws Exception {
        context.load().json(EXAMPLE_COMPONENTS, EATON_CONTENT_PATH);
        context.load().json(EXAMPLE_PAGES, GENERIC_PAGE_PATH);
        context.addModelsForPackage(SUBMITTAL_BUILDER_MODELS_PACACKAGE);
    }

    @Test
    public void notAuthoredTest() {
        SubmittalScope scope = context.resourceResolver()
                .getResource(SUBMITTAL_BUILDER_2_PATH + "/" + SUBMITTAL_SCOPE_NAME)
                .adaptTo(SubmittalScope.class);

        assertNotNull(scope);
        assertEquals(scope.getFamilies(), new ArrayList<SelectedTag>());
    }
}
