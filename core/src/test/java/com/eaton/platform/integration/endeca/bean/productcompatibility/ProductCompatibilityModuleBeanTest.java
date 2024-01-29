package com.eaton.platform.integration.endeca.bean.productcompatibility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProductCompatibilityModuleBeanTest {

    private static final String DEFAULT_URL = "/content/dam/eaton/resources/default-sku-image.jpg";
    private static final String VALID_URL = "https://www.eaton.com/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/images/bulb-icons/eaton-bulb-icon-par-parabolic-reflector.png";
    private static final String BLANK_URL = "";

    ProductCompatibilityModuleBean productCompatibilityModuleBean = new ProductCompatibilityModuleBean();

    @Test
    void testPopulateLEDImageAttributeValidUrl() {
        productCompatibilityModuleBean.setLEDImageURL(VALID_URL);
        String result = productCompatibilityModuleBean.getLEDImageURL();
        Assertions.assertEquals(VALID_URL, result, "should return expected image source url");
    }

    @Test
    void testPopulateLEDImageAttributeNullUrl() {
        productCompatibilityModuleBean.setLEDImageURL(null);
        String result = productCompatibilityModuleBean.getLEDImageURL();
        Assertions.assertEquals(DEFAULT_URL, result, "should return default url if expected image source is null");
    }

    @Test
    void testPopulateLEDImageAttributeBlankUrl() {
        productCompatibilityModuleBean.setLEDImageURL(BLANK_URL);
        String result = productCompatibilityModuleBean.getLEDImageURL();
        Assertions.assertEquals(DEFAULT_URL, result, "should return default url if expected image source is blank");
    }

}
