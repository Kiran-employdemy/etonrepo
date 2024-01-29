package com.eaton.platform.integration.sendgrid.dto;


import java.util.Map;
import java.util.Set;

public class Personalization {
    private EmailAttribute from;
    private Set<EmailAttribute> to;
    private Set<EmailAttribute> cc;
    private Set<EmailAttribute> bcc;
    private String subject;
    private Map<String,String> headers;
    private String substitutions;
    private Map<String,Object> dynamicTemplateData;
    private Map<String,String> customArgs;
    private long sendAt;

    public EmailAttribute getFrom() {
        return from;
    }

    public Personalization setFrom(EmailAttribute from) {
        this.from = from;
        return this;
    }

    public Set<EmailAttribute> getTo() {
        return to;
    }

    public Personalization setTo(Set<EmailAttribute> to) {
        this.to = to;
        return this;
    }

    public Set<EmailAttribute> getCc() {
        return cc;
    }

    public Personalization setCc(Set<EmailAttribute> cc) {
        this.cc = cc;
        return this;
    }

    public Set<EmailAttribute> getBcc() {
        return bcc;
    }

    public Personalization setBcc(Set<EmailAttribute> bcc) {
        this.bcc = bcc;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Personalization setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Personalization setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getSubstitutions() {
        return substitutions;
    }

    public Personalization setSubstitutions(String substitutions) {
        this.substitutions = substitutions;
        return this;
    }

    public Map<String, Object> getDynamicTemplateData() {
        return dynamicTemplateData;
    }

    public Personalization setDynamicTemplateData(Map<String, Object> dynamicTemplateData) {
        this.dynamicTemplateData = dynamicTemplateData;
        return this;
    }

    public Map<String, String> getCustomArgs() {
        return customArgs;
    }

    public Personalization setCustomArgs(Map<String, String> customArgs) {
        this.customArgs = customArgs;
        return this;
    }

    public long getSendAt() {
        return sendAt;
    }

    public Personalization setSendAt(long sendAt) {
        this.sendAt = sendAt;
        return this;
    }
}
