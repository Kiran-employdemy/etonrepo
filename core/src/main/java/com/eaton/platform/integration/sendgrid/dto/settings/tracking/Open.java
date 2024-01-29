package com.eaton.platform.integration.sendgrid.dto.settings.tracking;

import com.eaton.platform.integration.sendgrid.dto.settings.Base;

public class Open extends Base {
    private String substitutionTag;
    public Open(boolean enable,String substitutionTag) {
        super(enable);
        this.substitutionTag = substitutionTag;
    }

    public String getSubstitutionTag() {
        return substitutionTag;
    }
}
