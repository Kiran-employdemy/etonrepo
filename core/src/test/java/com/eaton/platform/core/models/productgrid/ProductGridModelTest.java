package com.eaton.platform.core.models.productgrid;

import java.util.Locale;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.MockSling;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ProductGridSlingModel;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ProductGridModelTest {

    @Mock
    private MockSlingHttpServletRequest request;

    @Mock
    ProductGridSlingModel productGridSlingModel;
    
    @Mock
    private ProductFamilyDetails productFamilyDetails;
    
    @Mock
    private TagManager tagManager;
    
    @InjectMocks
    ProductGridModel productGridModel;
    
    @BeforeEach
    void setUp(AemContext aemContext) {
        ResourceResolver resourceResolver = MockSling.newResourceResolver(aemContext.bundleContext());
        request = new MockSlingHttpServletRequest(resourceResolver, aemContext.bundleContext());
    }

    @Test
    @DisplayName("Check if getProductTypeJson function return nulls when pf product type is null or when turn off product type configuration is checked")
    void testGetProductTypeJsonIsNull() {
        Mockito.when(productGridSlingModel.isTurnOffProductTypes()).thenReturn(false);
        Mockito.when(productFamilyDetails.getProductType()).thenReturn(null);
        Assertions.assertNull(productGridModel.getProductTypeJson(), "should be null");

        Mockito.when(productGridSlingModel.isTurnOffProductTypes()).thenReturn(true);
        Assertions.assertNull(productGridModel.getProductTypeJson(), "should be null");

    }

    @Test
    @DisplayName("Check if getProductTypeJson function return values when turn off product type configuration is not checked")
    void testGetProductTypeJsonIsNotNull() {
        Mockito.when(productGridSlingModel.isTurnOffProductTypes()).thenReturn(false);
        Mockito.when(productFamilyDetails.getProductType()).thenReturn("Bearings");
        
        JSONObject productTypeJson = new JSONObject();
        productTypeJson.put(CommonConstants.PRODUCTTYPE, "Bearings");
        
        Assertions.assertEquals(productTypeJson, productGridModel.getProductTypeJson(), "json objects should be equal");

    }
      
    @Test
    @DisplayName("Check if getLocalizedTagTitle() function works as expected")
    void testGetLocalizedTagTitle() {
    	Locale locale = new Locale("fr");
    	
    	Assertions.assertEquals("Test", productGridModel.getLocalizedTagTitle("/content/cq:tags/Eaton/Active", locale, "Test"), "Localized tag title should return default value");
    	
    	Tag tag = Mockito.mock(Tag.class);
    	Mockito.when(tag.getTitle(locale)).thenReturn("Actif");
    	Mockito.when(tagManager.resolve("/content/cq:tags/Eaton/Active")).thenReturn(tag);
    	
    	Assertions.assertEquals("Actif", productGridModel.getLocalizedTagTitle("/content/cq:tags/Eaton/Active", locale, "Test"), "Localized tag title should be equal");
    }
}
