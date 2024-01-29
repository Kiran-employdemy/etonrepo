package com.eaton.platform.core.models.submittalbuilder;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SubmittalDownload {
    @Inject
    private String title;

    @Inject
    private String preferredOptionText;

    @Inject
    private String sendEmailText;

    @Inject
    private String expirationText;

    @Inject
    private String fileSizeLimitText;

    @Inject
    private String thankYouDownloadMessage;

    @Inject
    private String thankYouEmailMessage;

    @Inject
    private String invalidEmailMessage;

    @Inject
    private String eatonCommunicationsPage;

    @Inject
    private String eatonCommunicationsMessage;

    @Inject
    private String assetRequiredMessage;

    @Inject
    private String maxSizeExceededMessage;

    @Inject
    private String fileNamePrefix;

    @Inject
    private String mergeAssetsFileName;

    @Inject
    private String emailTemplatePath;

    @Inject
    private String senderEmailAddress;

    @Inject
    private String senderName;

    @Inject
    private String emailSubject;

    @Inject
    private String emailErrorPage;

    @Inject
    private String emailNewsLetterLink;

    @Inject
    private String emailSubmittalBuilderToolLink;

    @Inject
    private String emailContactUsLink;

    @Inject
    private String emailWhereToBuyLink;

    @PostConstruct
    protected void init() {
    }

    public String getTitle() {
        return title;
    }

    public String getPreferredOptionText() {
        return preferredOptionText;
    }

    public String getSendEmailText() {
        return sendEmailText;
    }

    public String getExpirationText() {
        return expirationText;
    }

    public String  getFileSizeLimitText() {
        return fileSizeLimitText;
    }

    public String getThankYouDownloadMessage() {
        return thankYouDownloadMessage;
    }

    public String getThankYouEmailMessage() {
        return thankYouEmailMessage;
    }

    public String getInvalidEmailMessage() {
        return invalidEmailMessage;
    }

    public String getEatonCommunicationsPage() {
        return eatonCommunicationsPage;
    }

    public String getEatonCommunicationsMessage() {
        return eatonCommunicationsMessage;
    }

    public String getAssetRequiredMessage() {
        return assetRequiredMessage;
    }

    public String getMaxSizeExceededMessage() {
        return maxSizeExceededMessage;
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public String getEmailTemplatePath() { return emailTemplatePath; }

    public String getSenderEmailAddress() { return senderEmailAddress; }

    public String getSenderName() { return senderName; }

    public String getEmailSubject() { return emailSubject; }

    public String getEmailErrorPage() { return emailErrorPage; }

    public String getEmailWhereToBuyLink() { return emailWhereToBuyLink; }

    public String getEmailContactUsLink() { return emailContactUsLink; }

    public String getEmailSubmittalBuilderToolLink() { return emailSubmittalBuilderToolLink; }

    public String getEmailNewsLetterLink() { return emailNewsLetterLink; }

    public String getMergeAssetsFileName() { return mergeAssetsFileName; }
}
