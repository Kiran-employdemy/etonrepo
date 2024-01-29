package com.eaton.platform.integration.sendgrid.dto.settings.tracking;

public class TrackingSettings {
    private Click clickTracking;
    private Open openTracking;
    private Subscription subscriptionTracking;
    private GAnalytics ganalytics;

    public Click getClickTracking() {
        return clickTracking;
    }

    public TrackingSettings setClickTracking(Click clickTracking) {
        this.clickTracking = clickTracking;
        return this;
    }

    public Open getOpenTracking() {
        return openTracking;
    }

    public TrackingSettings setOpenTracking(Open openTracking) {
        this.openTracking = openTracking;
        return this;
    }

    public Subscription getSubscriptionTracking() {
        return subscriptionTracking;
    }

    public TrackingSettings setSubscriptionTracking(Subscription subscriptionTracking) {
        this.subscriptionTracking = subscriptionTracking;
        return this;
    }

    public GAnalytics getGanalytics() {
        return ganalytics;
    }

    public TrackingSettings setGanalytics(GAnalytics ganalytics) {
        this.ganalytics = ganalytics;
        return this;
    }
}
