package com.eaton.platform.integration.sendgrid.dto;

import com.eaton.platform.integration.sendgrid.dto.settings.mail.MailSettings;
import com.eaton.platform.integration.sendgrid.dto.settings.tracking.TrackingSettings;

import java.util.List;
import java.util.Map;

public class RequestPayload {
    private List<Personalization> personalizations;
    private EmailAttribute from;
    private EmailAttribute replyTo;
    private List<EmailAttribute> replyToList;
    private String subject;
    private List<ContentType> content;
    private List<Attachment> attachments;
    private String templateId;
    private Map<String,String> headers;
    private List<String> categories;
    private String customArgs;
    private long sendAt;
    private String batchId;
    private String ipPoolName;
    private MailSettings mailSettings;
    private TrackingSettings trackingSettings;

    public List<Personalization> getPersonalizations() {
        return personalizations;
    }

    public RequestPayload setPersonalizations(List<Personalization> personalizations) {
        this.personalizations = personalizations;
        return this;
    }

    public EmailAttribute getFrom() {
        return from;
    }

    public RequestPayload setFrom(EmailAttribute from) {
        this.from = from;
        return this;
    }

    public EmailAttribute getReplyTo() {
        return replyTo;
    }

    public RequestPayload setReplyTo(EmailAttribute replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    public List<EmailAttribute> getReplyToList() {
        return replyToList;
    }

    public RequestPayload setReplyToList(List<EmailAttribute> replyToList) {
        this.replyToList = replyToList;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public RequestPayload setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public RequestPayload setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public String getTemplateId() {
        return templateId;
    }

    public RequestPayload setTemplateId(String templateId) {
        this.templateId = templateId;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public RequestPayload setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public List<String> getCategories() {
        return categories;
    }

    public RequestPayload setCategories(List<String> categories) {
        this.categories = categories;
        return this;
    }

    public String getCustomArgs() {
        return customArgs;
    }

    public RequestPayload setCustomArgs(String customArgs) {
        this.customArgs = customArgs;
        return this;
    }

    public long getSendAt() {
        return sendAt;
    }

    public RequestPayload setSendAt(long sendAt) {
        this.sendAt = sendAt;
        return this;
    }

    public String getBatchId() {
        return batchId;
    }

    public RequestPayload setBatchId(String batchId) {
        this.batchId = batchId;
        return this;
    }

    public List<ContentType> getContent() {
        return content;
    }

    public RequestPayload setContent(List<ContentType> content) {
        this.content = content;
        return this;
    }

    public String getIpPoolName() {
        return ipPoolName;
    }

    public RequestPayload setIpPoolName(String ipPoolName) {
        this.ipPoolName = ipPoolName;
        return this;
    }

    public MailSettings getMailSettings() {
        return mailSettings;
    }

    public RequestPayload setMailSettings(MailSettings mailSettings) {
        this.mailSettings = mailSettings;
        return this;
    }

    public TrackingSettings getTrackingSettings() {
        return trackingSettings;
    }

    public RequestPayload setTrackingSettings(TrackingSettings trackingSettings) {
        this.trackingSettings = trackingSettings;
        return this;
    }
}
