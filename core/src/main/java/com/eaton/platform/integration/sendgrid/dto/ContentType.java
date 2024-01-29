package com.eaton.platform.integration.sendgrid.dto;

public class ContentType {

    private String type;
    private String content;

    public ContentType(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
