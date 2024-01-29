package com.eaton.platform.integration.sendgrid.dto;

public class EmailAttribute {
    private String email;
    private String name;

    public String getEmail() {
        return email;
    }

    public EmailAttribute setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public EmailAttribute setName(String name) {
        this.name = name;
        return this;
    }
}
