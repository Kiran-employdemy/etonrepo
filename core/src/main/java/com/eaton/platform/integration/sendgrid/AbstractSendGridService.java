package com.eaton.platform.integration.sendgrid;

import com.eaton.platform.integration.sendgrid.dto.RequestPayload;
import com.eaton.platform.integration.sendgrid.exception.FailureSendGridException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.eaton.platform.core.constants.CommonConstants.*;

public abstract class AbstractSendGridService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSendGridService.class);
    public static final String API_REQUEST_URL_KEY = "apiRequestUrl";
    protected Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    protected abstract String getBearerToken();
    protected abstract Map<String,String> getRequestAttributes();
    protected abstract CloseableHttpClient getClient();
    protected abstract String getEmailTemplateId();

    public abstract String getId();

    public boolean sendEmailToSendGrid(RequestPayload emailPostPayload) throws FailureSendGridException, UnsupportedEncodingException {
        if(null == emailPostPayload){
            throw new FailureSendGridException("Expected Non Null Request Payload");
        }
        String bearerToken = getBearerToken();
        if(StringUtils.isBlank(bearerToken)){
            throw new FailureSendGridException("Bearer Token cannot be empty");
        }
        Map<String,String> attributes = getRequestAttributes();
        if(!attributes.containsKey(API_REQUEST_URL_KEY) || StringUtils.isBlank(attributes.get(API_REQUEST_URL_KEY))){
            throw new FailureSendGridException("Missing API URL");
        }
        String sendGridApiUrl = attributes.get(API_REQUEST_URL_KEY);
        LOGGER.info("Send Grid API URL being used: {}",sendGridApiUrl);
        HttpPost httpPost = new HttpPost(sendGridApiUrl);
        String payloadStr = this.gson.toJson(emailPostPayload);
        LOGGER.info("Request Payload for Sendgrid: {}",payloadStr);
        httpPost.setEntity(new StringEntity(payloadStr));
        CloseableHttpClient client = getClient();
        if(null == client){
            throw new FailureSendGridException("Missing Http Client. Please prepare http client for api call");
        }
        httpPost.addHeader(AUTHORIZATION, bearerToken);
        httpPost.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        try(CloseableHttpResponse response = client.execute(httpPost)){
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            if(null == statusLine || null == entity ){
                return false;
            }
            int statusCode = statusLine.getStatusCode();
            LOGGER.info("Status Code from SendGrid API Request: {}",statusCode);
            String resp = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
            LOGGER.info("Send Grid Email Response Payload: {}", resp);
            return statusCode == HttpStatus.SC_ACCEPTED;
        } catch (IOException e) {
            throw new FailureSendGridException(e);
        }
    }
}
