package com.eaton.platform.core.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ProductFamilyDetailsTest {

    public static final String LINK_TO_HOW_TO_BUY = "Link/to/howToBuy";

    @Test
    @DisplayName("When HowToBuy List is null, should return null")
    void testGetHowToBuyLinkWhenNull() {
        Assertions.assertNull(new ProductFamilyDetails().getHowToBuyLink(), "Should return null");
    }
    @Test
    @DisplayName("When HowToBuy List has more than 1 option, should return null")
    void testGetHowToBuyLinkWhenListGreaterThan1() {
        List<HowToBuyBean> howToBuyBeans = Arrays.asList(new HowToBuyBean(), new HowToBuyBean());
        ProductFamilyDetails productFamilyDetails = new ProductFamilyDetails();
        productFamilyDetails.setHowToBuyList(howToBuyBeans);
        Assertions.assertNull(productFamilyDetails.getHowToBuyLink(), "Should return null");
    }

    @Test
    @DisplayName("When HowToBuy List has 1 option, should return the link")
    void testGetHowToBuyLinkWhenListWith1Option() {
        HowToBuyBean howToBuyBean = new HowToBuyBean();
        howToBuyBean.setLink(LINK_TO_HOW_TO_BUY);
        List<HowToBuyBean> howToBuyBeans = Collections.singletonList(howToBuyBean);
        ProductFamilyDetails productFamilyDetails = new ProductFamilyDetails();
        productFamilyDetails.setHowToBuyList(howToBuyBeans);
        Assertions.assertEquals(LINK_TO_HOW_TO_BUY, productFamilyDetails.getHowToBuyLink(), "Should be the expected value");
    }
}