package com.customer360.backend.model;

public class CustomerPreference {

    private String customerId;
    private String membership;
    private String preferredChannel;

    private String preferredLanguage;
    private Boolean notificationOptIn;
    private Boolean marketingConsent;
    private String preferredContactTime;

    public CustomerPreference() {
    }

    // Old constructor kept for backward compatibility with existing tests
    public CustomerPreference(String customerId, String membership, String preferredChannel) {
        this.customerId = customerId;
        this.membership = membership;
        this.preferredChannel = preferredChannel;
        this.preferredLanguage = "Not Available";
        this.notificationOptIn = false;
        this.marketingConsent = false;
        this.preferredContactTime = "Not Available";
    }

    // New constructor with enriched preference fields
    public CustomerPreference(
            String customerId,
            String membership,
            String preferredChannel,
            String preferredLanguage,
            Boolean notificationOptIn,
            Boolean marketingConsent,
            String preferredContactTime
    ) {
        this.customerId = customerId;
        this.membership = membership;
        this.preferredChannel = preferredChannel;
        this.preferredLanguage = preferredLanguage;
        this.notificationOptIn = notificationOptIn;
        this.marketingConsent = marketingConsent;
        this.preferredContactTime = preferredContactTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getPreferredChannel() {
        return preferredChannel;
    }

    public void setPreferredChannel(String preferredChannel) {
        this.preferredChannel = preferredChannel;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Boolean getNotificationOptIn() {
        return notificationOptIn;
    }

    public void setNotificationOptIn(Boolean notificationOptIn) {
        this.notificationOptIn = notificationOptIn;
    }

    public Boolean getMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(Boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    public String getPreferredContactTime() {
        return preferredContactTime;
    }

    public void setPreferredContactTime(String preferredContactTime) {
        this.preferredContactTime = preferredContactTime;
    }
}
