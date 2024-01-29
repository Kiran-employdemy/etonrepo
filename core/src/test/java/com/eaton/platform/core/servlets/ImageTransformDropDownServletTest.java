package com.eaton.platform.core.servlets;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@ExtendWith(value = {AemContextExtension.class, MockitoExtension.class})
public class ImageTransformDropDownServletTest {
    @InjectMocks
    public ImageTranformDropDownServlet imageTranformDropDownServlet= new ImageTranformDropDownServlet();

    AemContext context = new AemContext();

    @Mock
    Resource mockResource;

    @Mock
    Resource configRes;

    @Mock
    AdminService adminService;

    @Mock
    Resource dataResource;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Iterator iterator;
    
    @Mock
    Resource configItemRes;

    @Mock
    ValueMap valueMap;

    @Mock
    ValueMap parameters;

    @BeforeEach
    public void setUp(){
        context.load().json("/json/ImageTranformDropDown.json", "/content/desktop");
    }

    @Test
    public void testDoGetForImageTransformation() throws Exception {
        when(adminService.getReadService()).thenReturn(resourceResolver);
        when(slingHttpServletRequest.getResource()).thenReturn(dataResource);
        when(dataResource.getChild("datasource")).thenReturn(mockResource);
        when(mockResource.getValueMap()).thenReturn(parameters);
        when(resourceResolver.getResource(CommonConstants.CONFIG_PATH)).thenReturn(configRes);
        when(configRes.hasChildren()).thenReturn(true, false);
        when(configRes.listChildren()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(configItemRes);
        when(configItemRes.getName()).thenReturn("com.adobe.acs.commons.images.impl" +
                ".NamedImageTransformerImpl-default-desktop-config");
        when(configItemRes.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("name")).thenReturn("desktop");
        when(parameters.get(CommonConstants.SELECTOR)).thenReturn("desktop");
        imageTranformDropDownServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
        Assertions.assertTrue(StringUtils.isNotBlank(context.response().toString()));
    }
}
