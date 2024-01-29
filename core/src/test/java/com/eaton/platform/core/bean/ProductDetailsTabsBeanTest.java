package com.eaton.platform.core.bean;

import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDetailsTabsBeanTest {

    @Mock
    SKUDetailsBean skuDetailsBean;

    @Test
    void testSetModelCodeNullSkuDetailsBean() {
        ProductDetailsTabsBean productDetailsTabsBean = new ProductDetailsTabsBean();
        assertNull(productDetailsTabsBean.getModelCode(), "should be null");
    }

    @Test
    void testSetModelCodeSkuDetailsBeanNoModelCode() {
        when(skuDetailsBean.getModelCode()).thenReturn(null);
        ProductDetailsTabsBean productDetailsTabsBean = ProductDetailsTabsBean.of(skuDetailsBean);
        assertNull(productDetailsTabsBean.getModelCode(), "should be null");
    }

    @Test
    void testSetModelCodeSkuDetailsBeanWithModelCode() {
        when(skuDetailsBean.getModelCode()).thenReturn("whatever");
        ProductDetailsTabsBean productDetailsTabsBean = ProductDetailsTabsBean.of(skuDetailsBean);
        assertEquals("whatever",  productDetailsTabsBean.getModelCode(), "should be whatever");
    }

}