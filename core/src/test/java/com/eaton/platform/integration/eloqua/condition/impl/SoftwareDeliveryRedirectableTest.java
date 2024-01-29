package com.eaton.platform.integration.eloqua.condition.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.integration.akamai.exception.AkamaiNetStorageException;
import com.eaton.platform.integration.akamai.services.AkamaiNetStorageService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
public class SoftwareDeliveryRedirectableTest {

    @Mock
    private AkamaiNetStorageService akamaiNetStorageService;

    private AemContext context = new AemContext();
    private String redirectUrl = "http://something.com/download";

    @Test
    @DisplayName("Ensure Software Delivery Redirectable should be redirectable")
    void ensureSoftwareDeliveryRedirectableIsRedirectable() throws AkamaiNetStorageException, URISyntaxException {
        prepare(SoftwareDeliveryRedirectable.SOFTWARE_DELIVERY_TEMPLATE);
        when(akamaiNetStorageService.getDownloadUrl(anyString())).thenReturn(redirectUrl);
        SoftwareDeliveryRedirectable fixture = new SoftwareDeliveryRedirectable(context.request(), akamaiNetStorageService);
        Assertions.assertNotNull(fixture);
        Assertions.assertTrue(fixture.shouldRedirect());
        Assertions.assertEquals(fixture.redirectUrl(),redirectUrl);
    }

    @Test
    @DisplayName("Ensure Software Delivery Redirectable should be false if it is not a software delivery template")
    void ensureIfItIsNotSoftwareDeliveryTemplateItReturnFalseForRedirect()  {
        prepare("/conf/eaton/settings/template/sometemplate");
        SoftwareDeliveryRedirectable fixture = new SoftwareDeliveryRedirectable(context.request(), akamaiNetStorageService);
        Assertions.assertNotNull(fixture);
        Assertions.assertFalse(fixture.shouldRedirect());
        Assertions.assertTrue(StringUtils.isBlank(fixture.redirectUrl()));
    }

    @Test
    @DisplayName("Ensure false is return if current resource cannot be identified from request")
    void ensureReturnFalseIfCurrentResourceIsNotAvailable(){
        Page page = context.create().page("/content/eaton/us/en-us/testpage","/conf/eaton/settings/template/sometemplate");
        Resource nonExistingResource = context.create().resource(page,"formbody",Collections.singletonMap(ResourceResolver.PROPERTY_RESOURCE_TYPE,Resource.RESOURCE_TYPE_NON_EXISTING));
        context.request().setResource(nonExistingResource);
        SoftwareDeliveryRedirectable fixture = new SoftwareDeliveryRedirectable(context.request(), akamaiNetStorageService);
        Assertions.assertNotNull(fixture);
        Assertions.assertFalse(fixture.shouldRedirect());
        Assertions.assertTrue(StringUtils.isBlank(fixture.redirectUrl()));
    }

    @Test
    @DisplayName("Ensure false is return if containing page is null")
    void ensureReturnFalseIfContainingPageIsNull(){
        Resource resource =  context.create().resource("/content/eaton/us/en-us/somepage/jcr:content/par/somecomponent",Collections.singletonMap(ResourceResolver.PROPERTY_RESOURCE_TYPE,"/some/resourcetype"));
        context.request().setResource(resource);
        SoftwareDeliveryRedirectable fixture = new SoftwareDeliveryRedirectable(context.request(), akamaiNetStorageService);
        Assertions.assertNotNull(fixture);
        Assertions.assertFalse(fixture.shouldRedirect());
        Assertions.assertTrue(StringUtils.isBlank(fixture.redirectUrl()));
    }


    private void prepare(String template){
       Page page = context.create().page("/content/eaton/us/en-us/testpage",template);
       Resource resource = context.create().resource(page,"formbody",Collections.singletonMap(ResourceResolver.PROPERTY_RESOURCE_TYPE,"/some/resourceType"));
       context.request().setResource(resource);
       context.request().setParameterMap(Collections.singletonMap(SoftwareDeliveryRedirectable.DOWNLOAD_PATH_VAR,"/EAR99/IPM-SUB-OP3/something.zip"));
    }



}
