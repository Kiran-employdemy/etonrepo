package com.eaton.platform.integration.sendgrid.dto.settings.mail;

import com.eaton.platform.integration.sendgrid.dto.settings.Base;

public class ByPassSpamManagement extends Base {
    public ByPassSpamManagement(boolean enable) {
        super(enable);
    }
}
