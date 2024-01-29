/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.bean.fields;

import com.google.gson.annotations.Expose;

/** Valid supplier accreditation parameters from fields service */
public class ValidSupplierAccreditationsBean {
    @Expose
    private int accreditationId;
    @Expose
    private String accreditationCode;
    @Expose
    private String accreditationLabel;

    public int getAccreditationId() {
        return accreditationId;
    }

    public void setAccreditationId(int accreditationId) {
        this.accreditationId = accreditationId;
    }

    public String getAccreditationCode() {
        return accreditationCode;
    }

    public void setAccreditationCode(String accreditationCode) {
        this.accreditationCode = accreditationCode;
    }

    public String getAccreditationLabel() {
        return accreditationLabel;
    }

    public void setAccreditationLabel(String accreditationLabel) {
        this.accreditationLabel = accreditationLabel;
    }
}
