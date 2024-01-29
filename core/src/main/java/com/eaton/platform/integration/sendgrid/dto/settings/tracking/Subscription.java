package com.eaton.platform.integration.sendgrid.dto.settings.tracking;


public class Subscription extends Open {
    private String text;
    private String html;

    public Subscription(boolean enable, String substitutionTag,String text, String html) {
        super(enable, substitutionTag);
        this.html = html;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getHtml() {
        return html;
    }
}
