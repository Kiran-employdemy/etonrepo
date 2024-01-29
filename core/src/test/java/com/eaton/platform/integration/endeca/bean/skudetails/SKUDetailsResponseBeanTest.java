package com.eaton.platform.integration.endeca.bean.skudetails;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SKUDetailsResponseBeanTest {

    @Mock
    SKUResponseBean skuResponseBean;

    @Test
    @DisplayName("getSkuDetails returns null when response is null")
    void testThatGetSkuDetailsReturnsNullWhenResponseNull() {
        assertNull(new SKUDetailsResponseBean().getSkuDetails(), "should be null");
    }
    @Test
    @DisplayName("getSkuDetails returns skuDetails of response when response is not null")
    void testThatGetSkuDetailsReturnsFromResponseWhenResponseNotNull() {
        SKUDetailsResponseBean skuDetailsResponseBean = new SKUDetailsResponseBean();
        skuDetailsResponseBean.setSkuResponse(skuResponseBean);
        SKUDetailsBean skuDetailsBean = new SKUDetailsBean();
        when(skuResponseBean.getSkuDetails()).thenReturn(skuDetailsBean);
        assertSame(skuDetailsBean, skuDetailsResponseBean.getSkuDetails(), "should be details from internal response");
    }

}