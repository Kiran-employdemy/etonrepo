package com.eaton.platform.integration.sendgrid.dto;

public class Attachment {
    private String content;
    private String type;
    private String filename;
    private String disposition;
    private String contentId;

    public String getContent() {
        return content;
    }

    public Attachment setContent(String content) {
        this.content = content;
        return this;
    }

    public String getType() {
        return type;
    }

    public Attachment setType(String type) {
        this.type = type;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public Attachment setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getDisposition() {
        return disposition;
    }

    public Attachment setDisposition(String disposition) {
        this.disposition = disposition;
        return this;
    }

    public String getContentId() {
        return contentId;
    }

    public Attachment setContentId(String contentId) {
        this.contentId = contentId;
        return this;
    }
}
