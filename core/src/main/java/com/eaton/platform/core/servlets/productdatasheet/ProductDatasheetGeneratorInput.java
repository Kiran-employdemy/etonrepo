package com.eaton.platform.core.servlets.productdatasheet;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ProductDatasheetGeneratorInput {
    private List<String> skuIDs;
    private List<String> locales;

    public List<String> getSkuIDs() {
        return skuIDs.stream().map(String::toUpperCase).collect(Collectors.toList());
    }

    public List<Locale> getLocales() {
        return locales.stream().map((String locale) -> {
            String[] split = locale.split("-");
            return new Locale(split[0], split[1]);
        }).collect(Collectors.toList());
    }
}
