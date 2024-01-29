package com.eaton.platform.core.bean;


import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDetailsCardBeanTest {


    @InjectMocks
    ProductDetailsCardBean productDetailsCardBean;
    @Mock
    SKUDetailsBean skuDetailsBean;

    @Test
    @DisplayName("Get link should return empty if link is empty")
    void testIsFreeSampleButtonEnabledFalse()  {
        productDetailsCardBean.setFreeSampleButtonEnabled(false);
        assertFalse(productDetailsCardBean.isFreeSampleButtonEnabled(), "should be false");

    }

    @Test
    @DisplayName("Get link should return null if link is null")
    void testIsFreeSampleButtonEnabledTrue() {
        productDetailsCardBean.setFreeSampleButtonEnabled(true);
        assertTrue(productDetailsCardBean.isFreeSampleButtonEnabled(), "should be true");
    }

    @Test
    void testGetModelCodeNullSkuDetailsBean() {
        ProductDetailsCardBean productDetailsCardBean = new ProductDetailsCardBean();
        assertNull(productDetailsCardBean.getModelCode(), "should be null");
    }

    @Test
    void testGetModelCodeSkuDetailsBeanNoModelCode() {
        when(skuDetailsBean.getModelCode()).thenReturn(null);
        ProductDetailsCardBean productDetailsCardBean = ProductDetailsCardBean.of(skuDetailsBean);
        assertNull(productDetailsCardBean.getModelCode(), "should be null");
    }

    @Test
    void testGetModelCodeSkuDetailsBeanWithModelCode() {
        when(skuDetailsBean.getModelCode()).thenReturn("whatever");
        ProductDetailsCardBean productDetailsCardBean = ProductDetailsCardBean.of(skuDetailsBean);
        assertEquals("whatever",  productDetailsCardBean.getModelCode(), "should be whatever");
    }

}
