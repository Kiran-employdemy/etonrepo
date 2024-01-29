package com.eaton.platform.integration.endeca.pojo.sku;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SkuCIDContent {
    private final Long id;
    private final List<SkuCIDDoc> skuCIDDocList;

    public SkuCIDContent(Long id, List<SkuCIDDoc> skuCIDDocList) {
        this.id = id;
        this.skuCIDDocList = skuCIDDocList;
    }

    public Long getId() {
        return id;
    }

    public boolean containsDocs() {
        return CollectionUtils.isNotEmpty(skuCIDDocList);
    }

    public boolean isSingle() {
        return containsDocs() && skuCIDDocList.size() == 1;
    }

    public List<SkuCIDDoc> getSkuCIDDocList() {
        return skuCIDDocList;
    }
}
