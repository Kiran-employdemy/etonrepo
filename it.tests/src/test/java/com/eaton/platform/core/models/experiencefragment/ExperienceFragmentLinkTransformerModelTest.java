package com.eaton.platform.core.models.experiencefragment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


import io.wcm.testing.mock.aem.junit.AemContext;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.net.ssl.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*",
        "org.w3c.*" })
public class ExperienceFragmentLinkTransformerModelTest {
    @Rule
    public final AemContext aemContext = new AemContext();

 
    @Mock
    private Resource childResource;

    @Mock
    private PageManager pageManager;
 

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource currentRsource;

    @Mock
    private Page page;

    private ExperienceFragmentLinkTransformerModel eatonExperiencExperienceFragmentLinkTransformerModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eatonExperiencExperienceFragmentLinkTransformerModel = new ExperienceFragmentLinkTransformerModel();
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(childResource)).thenReturn(page);
        
    }


    @Test
    public void testGetIsComponentInXSWithouyXF() {
        
        when(page.getPath()).thenReturn("/content/eaton");
        eatonExperiencExperienceFragmentLinkTransformerModel.initMethod();
        assertEquals(false, eatonExperiencExperienceFragmentLinkTransformerModel.getIsComponentInXS());
    }

   
}
