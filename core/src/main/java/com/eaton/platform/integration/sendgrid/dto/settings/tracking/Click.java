package com.eaton.platform.integration.sendgrid.dto.settings.tracking;

import com.eaton.platform.integration.sendgrid.dto.settings.Base;

public class Click extends Base {
    private String enableText;
    public Click(boolean enable,String enableText) {
        super(enable);
        this.enableText = enableText;
    }

    public String getEnableText() {
        return enableText;
    }
}
