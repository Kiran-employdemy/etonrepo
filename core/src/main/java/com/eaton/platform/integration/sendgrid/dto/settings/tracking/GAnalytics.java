package com.eaton.platform.integration.sendgrid.dto.settings.tracking;

import com.eaton.platform.integration.sendgrid.dto.settings.Base;

public class GAnalytics extends Base {

    private String utmSource;
    private String utmMedium;
    private String utmTerm;
    private String utmContent;
    private String utmCampaign;

    public GAnalytics(boolean enable) {
        super(enable);
    }

    public String getUtmSource() {
        return utmSource;
    }

    public GAnalytics setUtmSource(String utmSource) {
        this.utmSource = utmSource;
        return this;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public GAnalytics setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
        return this;
    }

    public String getUtmTerm() {
        return utmTerm;
    }

    public GAnalytics setUtmTerm(String utmTerm) {
        this.utmTerm = utmTerm;
        return this;
    }

    public String getUtmContent() {
        return utmContent;
    }

    public GAnalytics setUtmContent(String utmContent) {
        this.utmContent = utmContent;
        return this;
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public GAnalytics setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
        return this;
    }
}
