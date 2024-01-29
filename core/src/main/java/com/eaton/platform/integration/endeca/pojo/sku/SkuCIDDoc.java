package com.eaton.platform.integration.endeca.pojo.sku;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.enums.CIDDocType;

public class SkuCIDDoc {
    private String id;
    private CIDDocType type;
    private String language;
    private String country;
    private String value;
    private String priority;

    public void setId(String id) {
        this.id = id;
    }

    public void setType(CIDDocType type) {
        this.type = type;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public CIDDocType getType() {
        return type;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getValue() {
        return value;
    }

    public String[] getCountries() {
        return country.split(CommonConstants.COMMA);
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
