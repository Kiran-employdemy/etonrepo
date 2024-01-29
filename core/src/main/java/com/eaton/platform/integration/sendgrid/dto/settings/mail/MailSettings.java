package com.eaton.platform.integration.sendgrid.dto.settings.mail;

public class MailSettings {
    private ByPassListManagement byPassListManagement;
    private ByPassSpamManagement byPassSpamManagement;
    private ByPassBounceManagement byPassBounceManagement;
    private ByPassUnsubscribeManagement byPassUnsubscribeManagement;
    private Footer footer;


    public ByPassListManagement getByPassListManagement() {
        return byPassListManagement;
    }

    public MailSettings setByPassListManagement(ByPassListManagement byPassListManagement) {
        this.byPassListManagement = byPassListManagement;
        return this;
    }

    public ByPassSpamManagement getByPassSpamManagement() {
        return byPassSpamManagement;
    }

    public MailSettings setByPassSpamManagement(ByPassSpamManagement byPassSpamManagement) {
        this.byPassSpamManagement = byPassSpamManagement;
        return this;
    }

    public ByPassBounceManagement getByPassBounceManagement() {
        return byPassBounceManagement;
    }

    public MailSettings setByPassBounceManagement(ByPassBounceManagement byPassBounceManagement) {
        this.byPassBounceManagement = byPassBounceManagement;
        return this;
    }

    public ByPassUnsubscribeManagement getByPassUnsubscribeManagement() {
        return byPassUnsubscribeManagement;
    }

    public MailSettings setByPassUnsubscribeManagement(ByPassUnsubscribeManagement byPassUnsubscribeManagement) {
        this.byPassUnsubscribeManagement = byPassUnsubscribeManagement;
        return this;
    }

    public Footer getFooter() {
        return footer;
    }

    public MailSettings setFooter(Footer footer) {
        this.footer = footer;
        return this;
    }
}
