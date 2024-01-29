package com.eaton.platform.integration.endeca.factories;

import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpPostFactoryTest {

    HttpPostFactory httpPostFactory = new HttpPostFactory();
    String url = "myurl";
    String request = "myrequest";

    @Test
    @DisplayName("test creation of httpPost, null url")
    void testCreateHttpPostNullUrlParam() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> httpPostFactory.createEndecaRequestHttpPost(null, "request"),
                "Expect IllegalStateException because null url not allowed"
        );
    }

    @Test
    @DisplayName("test creation of httpPost, empty url")
    void testCreateHttpPostEmptyUrlParam() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> httpPostFactory.createEndecaRequestHttpPost("", "request"),
                "Expect IllegalStateException because empty url not allowed"
        );
    }

    @Test
    @DisplayName("test creation of httpPost, null request")
    void testCreateHttpPostNullRequestParam() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> httpPostFactory.createEndecaRequestHttpPost(url, null),
                "Expect IllegalStateException because null request not allowed"
        );
    }

    @Test
    @DisplayName("test creation of httpPost, empty request")
    void testCreateHttpPostEmptyRequestParam() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> httpPostFactory.createEndecaRequestHttpPost(url, ""),
                "Expect IllegalStateException because empty request not allowed"
        );
    }

    @Test
    @DisplayName("test creation of httpPost, valid inputs")
    void testCreateHttpPost() {
        HttpPost httpPost = httpPostFactory.createEndecaRequestHttpPost(url, request);
        Assertions.assertAll(
                () -> Assertions.assertEquals(url, httpPost.getURI().toString(), "HttpPostFactory should have saved URL param as expected" ),
                () -> Assertions.assertEquals(createExpectedEntity().toString(), httpPost.getEntity().toString(), "HttpPostFactory should have created StringEntity as expected" )
        );
    }

    private StringEntity createExpectedEntity() {
        String body = EndecaConstants.SOAP_ENVELOPE_PART_START + EndecaConstants.SOAP_BODY_PART_START + request
                + EndecaConstants.SOAP_BODY_PART_END + EndecaConstants.SOAP_ENVELOPE_PART_END;
        StringEntity stringEntity = new StringEntity(body, "UTF-8");
        stringEntity.setChunked(true);
        return stringEntity;
    }

}