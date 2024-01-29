package com.eaton.platform.integration.endeca.services.impl;

import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.factories.HttpPostFactory;
import com.eaton.platform.integration.endeca.pojo.pdh.EndecaPdhResponse;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EndecaQRServiceImplTest {

    @InjectMocks
    EndecaQRServiceImpl endecaQRServiceImpl = new EndecaQRServiceImpl();

    @Mock
    EndecaConfigServiceBean endecaConfigServiceBean;

    @Mock
    HttpClient httpClient;

    @Mock
    HttpEntity httpEntity;

    @Mock
    HttpPostFactory httpPostFactory;

    @Mock
    HttpPost httpPost;

    @Mock
    HttpResponse httpResponse;

    @Mock
    LoadingCache<EndecaServiceRequestBean, String> espServiceCache;

    @BeforeEach
    void setUp() throws IOException {

    }

    @Test
    @DisplayName("test espServiceCache object")
    void testEspServiceCache() {
        EndecaServiceRequestBean endecaRequestBean = EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean();
        Mockito.when(espServiceCache.get(endecaRequestBean)).thenReturn(EndecaServiceResponseBeanFixtures.SAMPLE_RESULT);
        EndecaPdhResponse pdhResponse = endecaQRServiceImpl.getEatonpdhlstcountries(endecaRequestBean);
        assertNotNull(pdhResponse, "The response (EndecaPdhResponse) from the EndecaQRService should not be null");
        assertEquals(new Gson().fromJson(EndecaServiceResponseBeanFixtures.SAMPLE_RESULT, EndecaPdhResponse.class), pdhResponse, "The sample response string when converted to EndecaPdhResponse should match the one returned from EndecaQRService");
    }

    @Test
    @DisplayName("test callESPService - SOAP response from endeca is accurate")
    void testCallESPservice() throws IOException {
        Mockito.when(endecaConfigServiceBean.getEndecaPDH1PDH2ServieURL()).thenReturn(EndecaServiceResponseBeanFixtures.PDH1_PDH2_URL);
        Mockito.when(httpClient.execute(Mockito.any(HttpPost.class))).thenReturn(httpResponse);
        Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
        Mockito.when(httpEntity.getContent()).thenReturn(
                new ByteArrayInputStream(EndecaServiceResponseBeanFixtures.SAMPLE_RESULT.getBytes(StandardCharsets.UTF_8))
        );
        EndecaServiceRequestBean endecaRequestBean = EndecaServiceResponseBeanFixtures.createEndecaServiceRequestBean();
        Mockito.when(httpPostFactory.createEndecaRequestHttpPost(EndecaServiceResponseBeanFixtures.PDH1_PDH2_URL, new Gson().toJson(endecaRequestBean))).thenReturn(httpPost);
        String responseStr = endecaQRServiceImpl.callESPService(endecaRequestBean);
        assertNotNull(responseStr, "The response string coming back from the Endeca service should not be null");
        assertEquals(EndecaServiceResponseBeanFixtures.SAMPLE_RESULT, responseStr, "The sample response string (JSON) should match the result from the Endeca service");
    }

}