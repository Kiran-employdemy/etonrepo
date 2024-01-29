package com.eaton.platform.core.models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.bean.ProductDetailsCardBean;
import com.eaton.platform.core.services.FeatureEnablementService;

@ExtendWith(MockitoExtension.class)
public class ProductDetailsCardModelTest {
    @InjectMocks
    ProductDetailsCardModel productDetailsCardModel = new ProductDetailsCardModel();

    @Mock
	protected FeatureEnablementService enablementService;
    
    @Mock
    ProductDetailsCardBean productDetailsCardBean;
    
    @Mock
    SlingHttpServletRequest slingRequest;
    
    @Test
    void testIsFreeSampleButtonDisabled(){
        when(enablementService.isFreeSampleButtonEnabled()).thenReturn(false);
        when(productDetailsCardBean.isFreeSampleButtonEnabled()).thenReturn(false);
        when(slingRequest.getRequestPathInfo()).thenReturn(null);
        assertEquals(enablementService.isFreeSampleButtonEnabled(),productDetailsCardBean.isFreeSampleButtonEnabled());
        productDetailsCardModel.init();
    }
    @Test
    void testIsFreeSampleButtonEnabled(){
        when(enablementService.isFreeSampleButtonEnabled()).thenReturn(true);
        when(productDetailsCardBean.isFreeSampleButtonEnabled()).thenReturn(true);
        assertEquals(enablementService.isFreeSampleButtonEnabled(),productDetailsCardBean.isFreeSampleButtonEnabled());
        productDetailsCardModel.init();
    }
}
