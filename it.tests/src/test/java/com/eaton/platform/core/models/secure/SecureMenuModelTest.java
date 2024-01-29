package com.eaton.platform.core.models.secure;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.TestConstants;
import com.eaton.platform.core.bean.LinkListTypeBean;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*","javax.security.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class SecureMenuModelTest {

    @Rule
    public final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    Resource resource;

    @Mock
    ResourceResolver resourceResolver;

    @InjectMocks
    SecureMenuModel secureMenuModel;

    @Mock
    AuthorizationService authorizationService;

    @Mock
    AuthenticationToken authenticationToken;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    PageManager pageManager;

    @Mock
    Page page;

    private List<LinkListTypeBean> childPageList;

    private Resource pageResource;

    private final String PARENT_PAGE_PATH = "/content/eaton/us/en-us/secure/parent_page";
    private final String CHILD_PAGE_PATH = "/content/eaton/us/en-us/secure/parent_page/child_page";
    private final String TEMPLATE_PATH = "/conf/eaton/settings/wcm/templates/secure-generic-page";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        aemContext.load().json(TestConstants.PARENT_SECURE_PAGE, PARENT_PAGE_PATH);
        aemContext.load().json(TestConstants.PARENT_SECURE_PAGE, CHILD_PAGE_PATH);
        aemContext.request().setPathInfo(CHILD_PAGE_PATH);
        page = aemContext.pageManager().create(PARENT_PAGE_PATH, "parent_page", TEMPLATE_PATH, "Page-creation-for-parent", true);
        page = aemContext.pageManager().create(PARENT_PAGE_PATH+"/", "child_page", TEMPLATE_PATH, "Page-creation-for-child", true);
        Mockito.when(resourceResolver.adaptTo(PageManager.class)).thenReturn(aemContext.pageManager());
        childPageList = new ArrayList<>();
        FieldSetter.setField(secureMenuModel, secureMenuModel.getClass().getDeclaredField("childPageList"),  childPageList);
    }

    @Test
    public void testChildPageList() {
       List<LinkListTypeBean> list = secureMenuModel.populateParentModel(PARENT_PAGE_PATH, authenticationToken);
       assertNotNull(list);
    }
}