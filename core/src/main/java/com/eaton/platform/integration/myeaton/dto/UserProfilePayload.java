package com.eaton.platform.integration.myeaton.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

public class UserProfilePayload {

    @JsonProperty
    protected String currentEmailAddress;

    @JsonProperty
    protected boolean updateMyEatonTermsConditionsTimestamp;

    @JsonProperty
    protected boolean updateOrderCenterTermsConditionsTimestamp;

    @JsonProperty("userType")
    protected String eatonPersonType;

    @JsonProperty
    protected String updatorFirstName;

    @JsonProperty
    protected String updatorLastName;

    @JsonProperty
    protected String updatorIdentifier;

    @JsonProperty
    protected String emailAddress;

    public UserProfilePayload setCurrentEmailAddress(String currentEmailAddress) {
        this.currentEmailAddress = currentEmailAddress;
        return this;
    }

    public UserProfilePayload setUpdateMyEatonTermsConditionsTimestamp(boolean updateMyEatonTermsConditionsTimestamp) {
        this.updateMyEatonTermsConditionsTimestamp = updateMyEatonTermsConditionsTimestamp;
        return this;
    }

    public UserProfilePayload setUpdateOrderCenterTermsConditionsTimestamp(boolean updateOrderCenterTermsConditionsTimestamp) {
        this.updateOrderCenterTermsConditionsTimestamp = updateOrderCenterTermsConditionsTimestamp;
        return this;
    }

    public UserProfilePayload setEatonPersonType(String eatonPersonType) {
        this.eatonPersonType = eatonPersonType;
        return this;
    }

    public boolean isValidPayload() {
        return StringUtils.isNotBlank(this.currentEmailAddress)
                && StringUtils.isNotBlank(this.eatonPersonType)
                && StringUtils.isNotBlank(this.emailAddress)
                && StringUtils.isNotBlank(this.updatorFirstName)
                && StringUtils.isNotBlank(this.updatorLastName)
                && StringUtils.isNotBlank(this.updatorIdentifier);
    }

    public UserProfilePayload setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public UserProfilePayload setUpdatorFirstName(String updatorFirstName) {
        this.updatorFirstName = updatorFirstName;
        return this;
    }

    public UserProfilePayload setUpdatorLastName(String updatorLastName) {
        this.updatorLastName = updatorLastName;
        return this;
    }

    public UserProfilePayload setUpdatorIdentifier(String updatorIdentifier) {
        this.updatorIdentifier = updatorIdentifier;
        return this;
    }
}
