package com.eaton.platform.integration.priceSpider.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PriceSpiderConfigModel {
    public static final String CONFIG_NAME = "pricespider-configurations";

    @Inject
    private String jsonLocation;

    @Inject
    private String universalScriptPath;

    @Inject
    private String productScriptPath;

    public String getJsonLocation() {
        return jsonLocation;
    }

    public String getUniversalScriptPath() {
        return universalScriptPath;
    }

    public String getProductScriptPath() {
        return productScriptPath;
    }
}