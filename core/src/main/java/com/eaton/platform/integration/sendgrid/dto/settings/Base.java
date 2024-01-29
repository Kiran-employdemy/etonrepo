package com.eaton.platform.integration.sendgrid.dto.settings;

public class Base {
    protected boolean enable;

    public Base(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }
}
