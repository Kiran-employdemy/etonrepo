package com.eaton.platform.integration.akamai.dto;

import org.apache.commons.lang3.StringUtils;

public class AkamaiAuthResponse {

    private String status = "success";
    private AkamaiAuthHeaders authHeaders;
    private String storageUrl;
    private String message = StringUtils.EMPTY;
    private String downloadPath = StringUtils.EMPTY;
    private String storageGroup = StringUtils.EMPTY;

    public AkamaiAuthHeaders getAuthHeaders() {
        return authHeaders;
    }
    public AkamaiAuthResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getStorageGroup() {
        return storageGroup;
    }

    public AkamaiAuthResponse setStorageGroup(String storageGroup) {
        this.storageGroup = storageGroup;
        return this;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public AkamaiAuthResponse setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
        return this;
    }

    public AkamaiAuthResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public AkamaiAuthResponse setAuthHeaders(AkamaiAuthHeaders authHeaders) {
        this.authHeaders = authHeaders;
        return this;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public AkamaiAuthResponse setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
