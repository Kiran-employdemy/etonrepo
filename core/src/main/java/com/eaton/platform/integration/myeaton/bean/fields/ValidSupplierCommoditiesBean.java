/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.bean.fields;

import com.google.gson.annotations.Expose;

/** Valid supplier commodity parameters from fields service */
public class ValidSupplierCommoditiesBean {
    @Expose
    private int commodityId;
    @Expose
    private int commodityCode;
    @Expose
    private String commodityLabel;

    public int getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(int commodityId) {
        this.commodityId = commodityId;
    }

    public int getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityCode(int commodityCode) {
        this.commodityCode = commodityCode;
    }

    public String getCommodityLabel() {
        return commodityLabel;
    }

    public void setCommodityLabel(String commodityLabel) {
        this.commodityLabel = commodityLabel;
    }
}
