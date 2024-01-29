package com.eaton.platform.integration.sendgrid.dto.settings.mail;

import com.eaton.platform.integration.sendgrid.dto.settings.Base;

public class Footer extends Base {

    private String text;
    private String html;
    public Footer(boolean enable,String text, String html) {
        super(enable);
        this.text = text;
        this.html = html;
    }

    public String getText() {
        return text;
    }

    public String getHtml() {
        return html;
    }
}
