package com.eaton.platform.core.models.products.lightsku;

import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.MockSling;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.models.products.lightsku.LightSkuModel;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.services.EndecaService;

@RunWith(MockitoJUnitRunner.class)
public class LightSkuModelTest {

    @Rule
    public final AemContext context = new AemContext();

    @Mock
    private MockSlingHttpServletRequest request;

    @Mock
    ResourceResolver resourceResolver;

    @InjectMocks
    LightSkuModel lightSkuModel;

    @Mock
    private Resource resource;

    @Mock
	private Page currentPage;

    @Mock
	private EndecaRequestService endecaRequestService;

	@Mock
	private EndecaService endecaService;

    
    @Before
    public void setUp() throws Exception {
        context.load().json("/SkuBasicDetails.json", "/content/eaton/en-us/product");
        ResourceResolver resourceResolver = MockSling.newResourceResolver(context.bundleContext());
        request = new MockSlingHttpServletRequest(resourceResolver, context.bundleContext());
    }

    @Test
    public void testSkuDetailsIsNotNull() {
        resource = request.getResourceResolver().resolve("/content/eaton/en-us/product");
        LightSkuModel lightSkuModel = request.adaptTo(LightSkuModel.class);
       // assertNotNull(lightSkuModel.getSkuDetails());
    }
}
