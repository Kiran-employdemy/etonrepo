package com.eaton.platform.core.bean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttrNmBeanTest {

    @Test
    @DisplayName("toBeDisplayed yields true if displayFlag = 'y'")
    void testToBeDisplayedTrueWithYes() {
        AttrNmBean attrNmBean = new AttrNmBean();
        attrNmBean.setDisplayFlag("Y");
        assertTrue(attrNmBean.toBeDisplayed(), "must be true");
    }

    @Test
    @DisplayName("toBeDisplayed yields true if displayFlag = null")
    void testToBeDisplayedTrueWithNull() {
        AttrNmBean attrNmBean = new AttrNmBean();
        assertTrue(attrNmBean.toBeDisplayed(), "must be true");
    }
    @Test
    @DisplayName("toBeDisplayed yields false if displayFlag = 'N'")
    void testToBeDisplayedFalseWithNo() {
        AttrNmBean attrNmBean = new AttrNmBean();
        attrNmBean.setDisplayFlag("N");
        assertFalse(attrNmBean.toBeDisplayed(), "must be false");
    }
}