package com.eaton.platform.integration.eloqua.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ResourceDecorator;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.eloqua.constant.EloquaConstants;


@ExtendWith(value = { MockitoExtension.class })
public class EloquaUtilTest {

    @Mock
    AdminService adminService;

    @Mock
    PageManager pageManager;

    @Mock
    ResourceResolver adminResourceResolver;

    @Mock
    Page page;

    @Mock
    Resource jcrContentResource;

    @Mock
    EloquaUtil eloquaUtil;

    @Mock
    ResourceDecorator resourceDecorator;

    String pagePath = "/content/eaton/mx/es-mx/forms/crouse-hinds-contact-me-form.html";

    @Test
    public void testHasEloquaForm() {
        ResourceResolver adminResourceResolver = mock(ResourceResolver.class);
        AdminService adminService = mock(AdminService.class);
        PageManager pageManager = mock(PageManager.class);
        Page page = mock(Page.class);
        when(adminService.getReadService()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        String updatedPagePath = pagePath.replace(CommonConstants.HTML_EXTN, StringUtils.EMPTY).trim();
        when(pageManager.getPage(updatedPagePath)).thenReturn(page);
        ResourceDecorator resourceDecorator = mock(ResourceDecorator.class);
        Resource jcrContentResource = mock(Resource.class);
        when(page.getContentResource()).thenReturn(jcrContentResource);
        when(jcrContentResource.adaptTo(any())).thenReturn(resourceDecorator);
        Resource eloquaFormResource = mock(Resource.class);
        when(resourceDecorator.findByResourceType(EloquaConstants.ELOQUA_FORM_RESOURCE_TYPE)).thenReturn(java.util.Optional.of(eloquaFormResource));
        boolean result = EloquaUtil.hasEloquaForm(pagePath,adminService);
        assertTrue(result);
    }
    
}
