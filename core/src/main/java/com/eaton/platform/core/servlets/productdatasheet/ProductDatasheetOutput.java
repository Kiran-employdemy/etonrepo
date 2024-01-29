package com.eaton.platform.core.servlets.productdatasheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDatasheetOutput {
    private final String skuId;
    private List<DataSheetDeepLinkList> skuDeepLinksList;


    public ProductDatasheetOutput(String skuId) {
        this.skuId = skuId;
    }

    public void addPresentLocale(Locale locale, String baseUrl) {
        addToSkuDeepLinksList(DataSheetDeepLinkList.of(locale, baseUrl, skuId));
    }

    public void addAbsentLocale(Locale locale) {
        addToSkuDeepLinksList(DataSheetDeepLinkList.empty(locale));
    }

    private void addToSkuDeepLinksList(DataSheetDeepLinkList locale) {
        if (skuDeepLinksList == null) {
            skuDeepLinksList = new ArrayList<>();
        }
        skuDeepLinksList.add(locale);
    }
}
