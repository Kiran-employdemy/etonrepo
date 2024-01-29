package com.eaton.platform.integration.sendgrid;

import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Map;
public class MockSendGridService extends AbstractSendGridService{

    private String bearerToken;
    private Map<String,String> requestAttr;
    private CloseableHttpClient httpClient;
    private String templateId;
    private String id;

    public MockSendGridService(String bearerToken, Map<String, String> requestAttr, CloseableHttpClient httpClient,
                               String templateId, String id) {
        this.bearerToken = bearerToken;
        this.requestAttr = requestAttr;
        this.templateId = templateId;
        this.httpClient = httpClient;
        this.id = id;
    }

    @Override
    protected String getBearerToken() {
        return bearerToken;
    }

    @Override
    protected Map<String, String> getRequestAttributes() {
        return requestAttr;
    }

    @Override
    protected CloseableHttpClient getClient() {
        return httpClient;
    }

    @Override
    protected String getEmailTemplateId() {
        return templateId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }
}
