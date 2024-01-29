package com.eaton.platform.core.bean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HowToBuyBeanTest {

    @Test
    @DisplayName("Get link should return null if link is null")
    void testGetLinkWhenNull() {
        HowToBuyBean howToBuyBean = new HowToBuyBean();
        assertNull(howToBuyBean.getLink(), "should be null");
    }

    @Test
    @DisplayName("Get link should return empty if link is empty")
    void testGetLinkWhenEmpty() {
        HowToBuyBean howToBuyBean = new HowToBuyBean();
        howToBuyBean.setLink("");
        assertEquals("", howToBuyBean.getLink(), "should be empty string");
    }
}