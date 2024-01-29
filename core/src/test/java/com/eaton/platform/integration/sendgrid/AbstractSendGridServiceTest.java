package com.eaton.platform.integration.sendgrid;

import com.eaton.platform.integration.sendgrid.dto.EmailAttribute;
import com.eaton.platform.integration.sendgrid.dto.Personalization;
import com.eaton.platform.integration.sendgrid.dto.RequestPayload;
import com.eaton.platform.integration.sendgrid.exception.FailureSendGridException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractSendGridServiceTest {

    @Mock
    private CloseableHttpClient mockHttpClient;

    @Mock
    private CloseableHttpResponse mockResponse;


    @Test
    @DisplayName(value = "Ensure SendGrid Email API should return success")
    void testToEnsureSendGridEmail_IsSuccessFul() throws IOException, FailureSendGridException {
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity httpEntity = new StringEntity("success");
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_ACCEPTED);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockResponse.getEntity()).thenReturn(httpEntity);
        MockSendGridService fixture = new MockSendGridService("bearToken",
                Collections.singletonMap(AbstractSendGridService.API_REQUEST_URL_KEY,"http://mockserver.io/api/email"),
                mockHttpClient,"templateabc","mockId");
        boolean isSuccess = fixture.sendEmailToSendGrid(getEmailRequestPayLoad());
        Assertions.assertTrue(isSuccess);
        verify(mockHttpClient,times(1)).execute(any(HttpPost.class));
    }

    @Nested
    @DisplayName("Test for all possible FailureSendGridException")
    class TestExceptionThrown{
        MockSendGridService fixture = new MockSendGridService("",
                Collections.singletonMap(AbstractSendGridService.API_REQUEST_URL_KEY,""),
                mockHttpClient,"templateabc","mockId");

        @Test
        @DisplayName(value = "Test to ensure if missing api url an exception should be thrown")
        void testToEnsureSendGridEmail_willThrowExecptionIfApiUrlMissing(){
            fixture.setBearerToken("bearerToken");
            Exception exception = Assertions.assertThrows(FailureSendGridException.class,() -> {
                boolean isSuccess = fixture.sendEmailToSendGrid(getEmailRequestPayLoad());
                Assertions.assertFalse(isSuccess);
            });
            Assertions.assertTrue(exception instanceof FailureSendGridException);
            Assertions.assertTrue(exception.getMessage().contains("Missing API URL"));
        }

        @Test
        @DisplayName(value = "Test to ensure if missing bearer token an exception should be thrown")
        void testToEnsureSendGridEmail_willThrowExecptionIfBearerTokenMissing(){
            Exception exception = Assertions.assertThrows(FailureSendGridException.class,() -> {
                boolean isSuccess = fixture.sendEmailToSendGrid(getEmailRequestPayLoad());
                Assertions.assertFalse(isSuccess);
            });
            Assertions.assertTrue(exception instanceof FailureSendGridException);
            Assertions.assertTrue(exception.getMessage().contains("Bearer Token cannot be empty"));
        }

        @Test
        @DisplayName(value = "Test to ensure if null payload is pass in an exception should be thrown")
        void testToEnsureSendGridEmail_willThrowExecptionPayloadIsNull(){
            Exception exception = Assertions.assertThrows(FailureSendGridException.class,() -> {
                boolean isSuccess = fixture.sendEmailToSendGrid(null);
                Assertions.assertFalse(isSuccess);
            });
            Assertions.assertTrue(exception instanceof FailureSendGridException);
            Assertions.assertTrue(exception.getMessage().contains("Expected Non Null Request Payload"));
        }
    }



    private RequestPayload getEmailRequestPayLoad(){
        Set<EmailAttribute> sendTo = new HashSet<>();
        Map<String,Object> dynamicData = new HashMap<>();
        dynamicData.put("title","Awesome User");
        sendTo.add(new EmailAttribute().setEmail("goingsomewhere@gmail").setName("goingout"));
        return new RequestPayload()
                .setFrom(new EmailAttribute().setEmail("somewhere@gmail.com").setName("someone"))
                .setPersonalizations(Collections.singletonList(
                        new Personalization().setTo(sendTo)
                                .setDynamicTemplateData(dynamicData)
                ));

    }
}
