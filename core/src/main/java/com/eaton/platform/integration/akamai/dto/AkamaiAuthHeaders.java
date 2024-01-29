package com.eaton.platform.integration.akamai.dto;

public class AkamaiAuthHeaders {
    private String authData;
    private String authSign;

    private String actionValue;

    public AkamaiAuthHeaders(String authData, String authSign,String actionValue) {
        this.authData = authData;
        this.authSign = authSign;
        this.actionValue = actionValue;
    }

    public String getAuthSign() {
        return authSign;
    }

    public void setAuthSign(String authSign) {
        this.authSign = authSign;
    }

    public String getAuthData() {
        return authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public String getActionValue() {
        return actionValue;
    }

    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }
}
