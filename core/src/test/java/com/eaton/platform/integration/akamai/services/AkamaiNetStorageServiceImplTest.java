package com.eaton.platform.integration.akamai.services;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.eaton.platform.integration.akamai.dto.AkamaiAuthResponse;
import com.eaton.platform.integration.akamai.exception.AkamaiNetStorageException;
import com.eaton.platform.integration.akamai.services.impl.AkamaiNetStorageServiceImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
public class AkamaiNetStorageServiceImplTest {

    private final AemContext context = new AemContext();

    @Mock
    private CryptoSupport mockCryptoSupport;

    private String downloadUrl = "/IPM-GRAPHITE/Foreseer-V7.6.zip";
    private String domainVal = "http://testing.com";
    private String downloadDomain = "http://testing.akamai.com";

    @BeforeEach
    void setup(){
        context.registerService(CryptoSupport.class,mockCryptoSupport);
    }


    @Test
    @DisplayName("Test to ensure get auth header should return with correct value")
    void testToEnsureFixtureGetAuthHeaderReturnNotNull() throws AkamaiNetStorageException, CryptoException {
        when(mockCryptoSupport.isProtected("123456789")).thenReturn(true);
        when(mockCryptoSupport.unprotect("123456789")).thenReturn("abcdefhi");
        when(mockCryptoSupport.hmac_sha256(any(byte[].class),any(byte[].class))).thenReturn("oiuwerwe".getBytes());
        AkamaiNetStorageService fixture = getFixtureWithProperties(Collections.emptyMap());
        AkamaiAuthResponse authResponse = fixture.getAuthHeaders("upload","somefile.zip","somePartNumber");
        Assertions.assertNotNull(authResponse);
        Assertions.assertTrue(authResponse.getStatus().equalsIgnoreCase("success"));
        Assertions.assertTrue(authResponse.getStorageUrl().contains("http://testing.com"));
        Assertions.assertEquals(authResponse.getAuthHeaders().getActionValue(),String.format("version=%s&action=%s","1","upload"));
        Assertions.assertNotNull(authResponse.getAuthHeaders().getAuthData());
        Assertions.assertNotNull(authResponse.getAuthHeaders().getAuthSign());
        verify(mockCryptoSupport,times(1)).hmac_sha256(any(byte[].class),any(byte[].class));
    }

    @Test
    @DisplayName("Ensure When incorrect action type is pass in will an exception will be thrown and handle")
    void testEnsureWhenIncorrectActionTypeIsPassInAnExceptionWillBeThrown_andHandle(){
        Exception exception = Assertions.assertThrows(AkamaiNetStorageException.class,()->{
            getFixtureWithProperties(Collections.emptyMap()).getAuthHeaders("asdfasdf","somefile.zip","somePartNumber");
        });
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof AkamaiNetStorageException);
        Assertions.assertTrue(exception.getMessage().contains("action type"));
    }

    @Test
    @DisplayName("Ensure when none filename pass in it will thrown an exception")
    void testEnsureWhenFilenameNotPassInAnExceptionWillBeThrown(){
        Exception exception = Assertions.assertThrows(AkamaiNetStorageException.class,()->{
            getFixtureWithProperties(Collections.emptyMap()).getAuthHeaders("upload", StringUtils.EMPTY,"somePartNumber");
        });
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof AkamaiNetStorageException);
        Assertions.assertTrue(exception.getMessage().contains("Filename cannot"));
    }

    @Test
    @DisplayName("Ensure if osgi properties does is empty it should thrown an exception")
    void testToEnsureIfOsgiPropertiesIsEmptyShouldThrownException(){
        AkamaiNetStorageService fixture = getFixtureWithoutProperties();
        Exception exception = Assertions.assertThrows(AkamaiNetStorageException.class,()->{
           fixture.getAuthHeaders("upload","something.zip","somePartNumber");
        });
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof  AkamaiNetStorageException);
        Assertions.assertTrue(exception.getMessage().contains("are valid and not empty"));
    }

    @Test
    @DisplayName("If Short-Live Url is false then it should return with whatever path it pass in")
    void testToEnsureIfShortLiveUrlIsFalseThenItShouldRetWithOriginalURL() throws AkamaiNetStorageException, URISyntaxException {
        AkamaiNetStorageService fixture = getFixtureWithProperties(Collections.emptyMap());
        String resultPath = fixture.getDownloadUrl(downloadUrl);
        Assertions.assertNotNull(resultPath);
        Assertions.assertEquals(resultPath,downloadDomain+downloadUrl);
    }

    @Test
    @DisplayName("If Short-Live Url is true then it should return with short-lived token append to the url")
    void testToEnsureIfShortLiveUrlIsTrueItShouldIncludeShortLiveToken() throws AkamaiNetStorageException, URISyntaxException {
        Map<String,Object> additionalProperties = new HashMap<>();
        additionalProperties.put("isShortLiveUrl",true);
        additionalProperties.put("shortLiveTokenEncryptionKey","abcd");
        AkamaiNetStorageService fixture = getFixtureWithProperties(additionalProperties);
        String resultPath = fixture.getDownloadUrl(downloadUrl);
        Assertions.assertNotNull(resultPath);
        Assertions.assertTrue(resultPath.contains("?token="));
    }

    @Test
    @DisplayName("If an issue occur while generating short-lived token an exception should be thrown")
    void testToEnsureExceptionIsThrownWhenIssueOccurs()  {
        Map<String,Object> additionalProperties = new HashMap<>();
        additionalProperties.put("isShortLiveUrl",true);
        // if we attempts to use shortlive url without encryption key an exception should occur
        AkamaiNetStorageService fixture = getFixtureWithProperties(additionalProperties);
        Exception exception = Assertions.assertThrows(AkamaiNetStorageException.class,()->{
            fixture.getDownloadUrl(downloadUrl);
        });
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof AkamaiNetStorageException);

    }
    private AkamaiNetStorageService getFixtureWithoutProperties(){
        return context.registerInjectActivateService(new AkamaiNetStorageServiceImpl(), Collections.EMPTY_MAP);
    }

    private AkamaiNetStorageService getFixtureWithProperties(Map<String, Object> additionalProperties){
        Map<String,Object> properties = new HashMap<>();
        properties.put("id","junit-test");
        properties.put("domain",domainVal);
        properties.put("account","unit-test");
        properties.put("accessKey","123456789");
        properties.put("version","1");
        properties.put("signVersion",5);
        properties.put("customPath","junit");
        properties.put("cpCode","abc");
        properties.put("downloadDomain",downloadDomain);
        properties.putAll(additionalProperties);
        return context.registerInjectActivateService(new AkamaiNetStorageServiceImpl(),properties);
    }

}
