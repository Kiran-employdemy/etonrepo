/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.bean.fields;

import com.google.gson.annotations.Expose;

/** Valid country parameters from fields service */
public class ValidCountriesBean {
    @Expose
    private int countryId;
    @Expose
    private String countryName;
    @Expose
    private String countryCode;

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
