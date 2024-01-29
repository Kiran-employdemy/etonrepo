package com.eaton.platform.integration.endeca.servlets;

import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.services.EndecaAdvancedSearchService;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class EndecaAdvancedSearchServletTest {

    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private EndecaAdvancedSearchService endecaAdvancedSearchService;

    @InjectMocks
    EndecaAdvancedSearchServlet endecaAdvancedSearchServlet;

    @Mock
    EndecaServiceRequestBean endecaServiceRequestBean;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Mockito.when( endecaAdvancedSearchService.constructEndecaRequestBean(context.request())).thenReturn(endecaServiceRequestBean);
    }

    @Test
    public void testDoGet() throws Exception {
        JSONObject responseObject = new JSONObject();
        responseObject.put("status","Success" );
        Mockito.when(endecaAdvancedSearchService.getAdvanceSearchResults(endecaServiceRequestBean,context.request())).thenReturn(responseObject);
        endecaAdvancedSearchServlet.doGet(context.request(), context.response());
        assertEquals(200, context.response().getStatus());
    }
}
