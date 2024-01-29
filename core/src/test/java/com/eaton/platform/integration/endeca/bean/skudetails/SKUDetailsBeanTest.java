package com.eaton.platform.integration.endeca.bean.skudetails;

import com.eaton.platform.core.util.SkuFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SKUDetailsBeanTest {

    @Test
    void testGetModelCodeWithNullGlobalAttrs() {
        Assertions.assertNull(new SKUDetailsBean().getModelCode(), "Should be null.");
    }

    @Test
    void testGetModelCodeWithNoModelCodeInGlobalAttrs() {
        SKUDetailsBean skuDetailsBean = new SKUDetailsBean();
        skuDetailsBean.setGlobalAttrs("no model code xml in the string");
        Assertions.assertNull(skuDetailsBean.getModelCode(), "Should be null.");
    }

    @Test
    void testThatGetModelCodeYieldsCorrectModelCode() {
        SKUDetailsBean skuDetailsBean = new SKUDetailsBean();
        skuDetailsBean.setGlobalAttrs(SkuFixtures.skuGlobalAttributesXML);
        Assertions.assertEquals("P1-40/EA/SVB", skuDetailsBean.getModelCode(), "Should be the model code of the xml.");
    }

}