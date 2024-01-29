package com.eaton.platform.core.models.experiencefragment;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.eaton.platform.core.services.AdminService;

import io.wcm.testing.mock.aem.junit.AemContext;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.net.ssl.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*",
        "org.w3c.*" })
public class EatonExperienceFragmentModelTest {
    @Rule
    public final AemContext aemContext = new AemContext();

    @Mock
    private Page page;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private AdminService adminService;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource resource;

    private EatonExperienceFragmentModel eatonExperienceFragment;

    private String fragmentVariationPath = "/content/experience-fragments/eaton/en-us/experience-fragment/master";

    private String expectedLocalizedFragmentVariationPath = "/content/experience-fragments/eaton/en-us/experience-fragment/master";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eatonExperienceFragment = new EatonExperienceFragmentModel();
        eatonExperienceFragment.page = page;
        eatonExperienceFragment.request = request;
        eatonExperienceFragment.adminService = adminService;
        eatonExperienceFragment.fragmentVariationPath = fragmentVariationPath;
    }

  

    @Test
    public void testGetFragmentVariationPath() {
        when(resource.getPath()).thenReturn(expectedLocalizedFragmentVariationPath);
        eatonExperienceFragment.getFragmentVariationPath();
        assertEquals(expectedLocalizedFragmentVariationPath, eatonExperienceFragment.getFragmentVariationPath());
    }
}
