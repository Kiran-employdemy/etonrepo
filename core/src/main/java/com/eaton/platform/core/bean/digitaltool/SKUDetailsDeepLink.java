package com.eaton.platform.core.bean.digitaltool;

import java.io.Serializable;
import java.util.List;

public class SKUDetailsDeepLink implements Serializable {
    private static final long serialVersionUID = 1L;
    private String skuId;
    private List<SKUDeepLinks> skuDeepLinksList;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public List<SKUDeepLinks> getSkuDeepLinksList() {
        return skuDeepLinksList;
    }

    public void setSkuDeepLinksList(List<SKUDeepLinks> skuDeepLinksList) {
        this.skuDeepLinksList = skuDeepLinksList;
    }
}