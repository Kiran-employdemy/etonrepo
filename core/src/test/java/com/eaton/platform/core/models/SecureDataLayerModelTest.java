package com.eaton.platform.core.models;

import com.eaton.platform.core.bean.SecureDataLayerBean;
import com.eaton.platform.core.services.secure.impl.UserProfileFixtures;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecureDataLayerModelTest {

    @InjectMocks
    SecureDataLayerModel secureDataLayerModel = new SecureDataLayerModel();
    @Mock
    SlingHttpServletRequest request;
    @Mock
    AuthorizationService authorizationService;
    @Mock
    AuthenticationToken authenticationToken;

    SecureDataLayerBean expectedSecureDataLayerBean;

    @BeforeEach
    void setUp() throws IOException {
        when(authorizationService.getTokenFromSlingRequest(request)).thenReturn(authenticationToken);
        when(authenticationToken.getUserProfile()).thenReturn(UserProfileFixtures.pptestAtCompanyWithMappedTags());
        expectedSecureDataLayerBean = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("expected-datalayer.json")), Charset.defaultCharset()), SecureDataLayerBean.class);
    }

    @Test
    void testCorrectJsonIsReturnedAfterInit() {
        secureDataLayerModel.init();
        assertEquals(expectedSecureDataLayerBean, new Gson().fromJson(secureDataLayerModel.getDataLayerJson(), SecureDataLayerBean.class), "Json must be correct");
    }
}