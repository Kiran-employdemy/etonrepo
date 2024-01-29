package com.eaton.platform.integration.akamai.services;

import com.adobe.granite.crypto.CryptoException;
import com.eaton.platform.integration.akamai.dto.AkamaiAuthResponse;
import com.eaton.platform.integration.akamai.exception.AkamaiNetStorageException;

import java.net.URISyntaxException;


public interface AkamaiNetStorageService {

    AkamaiAuthResponse getAuthHeaders(String action, String filename, String partNumber) throws AkamaiNetStorageException, CryptoException;
    String getDownloadUrl(String downloadUrl) throws AkamaiNetStorageException, URISyntaxException;
    boolean useShortLivedUrl();
}
