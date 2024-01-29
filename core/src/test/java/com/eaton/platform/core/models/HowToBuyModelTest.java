package com.eaton.platform.core.models;

import com.eaton.platform.core.bean.HowToBuyBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class HowToBuyModelTest {

    private HowToBuyModel howToBuyModel = new HowToBuyModel();
    private List<HowToBuyBean> howToBuyList;
    private List<HowToBuyBean> modifiedHowToBuyList;

    private HowToBuyBean howToBuyBean1;
    private HowToBuyBean howToBuyBean2;
    private HowToBuyBean howToBuyBean3;
    private HowToBuyBean howToBuyBean4;

    @BeforeEach
    void setUp() {
        howToBuyList = new ArrayList<>();
        modifiedHowToBuyList = new ArrayList<>();
        howToBuyBean1 = new HowToBuyBean();
        howToBuyBean2 = new HowToBuyBean();
        howToBuyBean3 = new HowToBuyBean();
        howToBuyBean4 = new HowToBuyBean();
        howToBuyList.add(howToBuyBean1);
        howToBuyList.add(howToBuyBean2);
        howToBuyList.add(howToBuyBean3);
        howToBuyList.add(howToBuyBean4);
        modifiedHowToBuyList = howToBuyModel.addClearfixCondition(howToBuyList, 3);
    }

    @Test
    @DisplayName("test that clearfix is added to fourth item, and not to the first")
    void testAddClearfixCondition() {
        Assertions.assertEquals(false, modifiedHowToBuyList.get(0).hasClearfix());
        Assertions.assertEquals(true, modifiedHowToBuyList.get(3).hasClearfix());
    }
}