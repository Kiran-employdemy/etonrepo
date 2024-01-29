package com.eaton.platform.integration.akamai.services.impl;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.eaton.platform.integration.akamai.config.AkamaiNetStorageConfig;
import com.eaton.platform.integration.akamai.dto.AkamaiAuthHeaders;
import com.eaton.platform.integration.akamai.dto.AkamaiAuthResponse;
import com.eaton.platform.integration.akamai.enums.NetStorageAction;
import com.eaton.platform.integration.akamai.exception.AkamaiNetStorageException;
import com.eaton.platform.integration.akamai.services.AkamaiNetStorageService;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

@Component(service = AkamaiNetStorageService.class)
@Designate(ocd = AkamaiNetStorageConfig.class,factory = true)
public class AkamaiNetStorageServiceImpl implements AkamaiNetStorageService {


    private static final Logger LOG = LoggerFactory.getLogger(AkamaiNetStorageServiceImpl.class);

    private static final String ACTION_HEADER_NAME = "x-akamai-acs-action";

    @Reference
    private CryptoSupport cryptoSupport;
    private Random rand;

    private AkamaiNetStorageConfig config;
    private static final char FIELD_DELIMITER = '~';
    private String algorithm = "HmacSHA256";

    @Activate
    protected void activate(AkamaiNetStorageConfig config){
        this.config = config;
    }

    @Override
    public AkamaiAuthResponse getAuthHeaders(String action, String filename, String uniquePath) throws AkamaiNetStorageException, CryptoException {
        LOG.debug("Starting getAuthHeaders");
        LOG.debug("Action type passed in: {}",action);
        LOG.debug("Filename passed in: {}",filename);
        if(!shouldProceed()){
            throw new AkamaiNetStorageException("Verify accessKey, account, cpCode, domain, downloadDomain and version Configuration values are valid and not empty");
        }
        NetStorageAction actionType = NetStorageAction.getAction(action);
        if(actionType == NetStorageAction.UNKNOWN){
            throw new AkamaiNetStorageException("Inability to identify action type");
        }
        if(StringUtils.isBlank(filename)){
            throw new AkamaiNetStorageException("Filename cannot be empty or null");
        }
        if(StringUtils.isBlank(uniquePath)){
            throw new AkamaiNetStorageException("Unique Path cannot be empty or null");
        }
        String fullStorageUrl = getNetStorageFullUrl(filename,uniquePath);
        String storageRelativePath = getNetStoragePath(filename,uniquePath);
        String authData = getAuthDataHeaderValue();
        String authSign = getAuthSignHeaderValue(authData,storageRelativePath,actionType);
        if(StringUtils.isNotBlank(authData) && StringUtils.isNotBlank(authSign)){
            LOG.debug("auth data and auth sign are not blank");
            AkamaiAuthHeaders header = new AkamaiAuthHeaders(authData,authSign,actionType.getActionValue(config.version()));
            String downloadPath = StringUtils.joinWith("/",config.downloadDomain(),uniquePath,filename);
            return new AkamaiAuthResponse()
                    .setStorageUrl(fullStorageUrl)
                    .setAuthHeaders(header)
                    .setStorageGroup(config.storageGroup())
                    .setDownloadPath(downloadPath);
        }
        throw new AkamaiNetStorageException("Unable to create Akamai Net Storage Auth Headers");
    }
    @Override
    public String getDownloadUrl(String pathToGenerateTokenWith) throws AkamaiNetStorageException {
        StringBuilder downloadPathPrefix = new StringBuilder().append(config.downloadDomain()).append(pathToGenerateTokenWith);
        if(config.isShortLiveUrl()){
            LOG.debug("PathToGenerateTokenWith URL passed in: {}",pathToGenerateTokenWith);
            String urlWithToken = downloadPathPrefix
                    .append("?token=")
                    .append(generateToken(pathToGenerateTokenWith))
                    .toString();
            return urlWithToken;
        }
        return downloadPathPrefix.toString();
    }

    @Override
    public boolean useShortLivedUrl() {
        return config.isShortLiveUrl();
    }

    private String generateToken(String softwarePath) throws AkamaiNetStorageException {
        Long startTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() / 1000L;
        Long endTime = startTime + config.shortLiveTokenTTL();

        if ( endTime <= startTime) {
            LOG.error("Token will have already expired.");
        }
        StringBuilder newToken = new StringBuilder();

        newToken.append("exp=");
        newToken.append(endTime);
        newToken.append(FIELD_DELIMITER);

        newToken.append("acl=");
        newToken.append(softwarePath+"*");
        newToken.append(FIELD_DELIMITER);

        StringBuilder hashSource = new StringBuilder(newToken);
        hashSource.deleteCharAt(hashSource.length() - 1);
        try {
            Mac hmac = Mac.getInstance(this.algorithm);
            byte[] keyBytes = DatatypeConverter.parseHexBinary(this.getShortLiveEncryptionKey());
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, this.algorithm);
            hmac.init(secretKey);
            byte[] hmacBytes = hmac.doFinal(hashSource.toString().getBytes());
            return newToken + "hmac=" + String.format("%0" + (2*hmac.getMacLength()) +  "x", new BigInteger(1, hmacBytes));
        } catch (NoSuchAlgorithmException | CryptoException | InvalidKeyException e) {
            LOG.error("NoSuchAlgorithmException={} and its message={}", e, e.getMessage());
            throw new AkamaiNetStorageException(e);
        } catch (Exception e){
            LOG.error("NoSuchAlgorithmException={} and its message={}", e, e.getMessage());
            throw new AkamaiNetStorageException(e);
        }
    }


    private String getNetStorageFullUrl(String filename,String uniquePath){
        return StringUtils.joinWith("/", config.domain(),getNetStoragePath(filename,uniquePath));
    }

    private String getNetStoragePath(String filename,String uniquePath){
        return StringUtils.joinWith("/", config.cpCode(),uniquePath,filename);
    }

    protected String getAccessKey() throws CryptoException {
        if(cryptoSupport.isProtected(config.accessKey())){
            return cryptoSupport.unprotect(config.accessKey());
        }
        return config.accessKey();
    }

    protected String getShortLiveEncryptionKey() throws CryptoException {
        if(cryptoSupport.isProtected(config.shortLiveTokenEncryptionKey())){
            return cryptoSupport.unprotect(config.shortLiveTokenEncryptionKey());
        }
        return config.shortLiveTokenEncryptionKey();
    }

    private String getAuthSignHeaderValue(String authData, String uploadPath,NetStorageAction action) throws CryptoException {
            LOG.debug("auth data: {}" ,authData );
            String actionValue = action.getActionValue(config.version());
            LOG.debug("Action Value: {}", actionValue);
            String signStr = String.format("/%s\n%s:%s\n", uploadPath, ACTION_HEADER_NAME, actionValue);
            LOG.debug("Sign String is: {}", signStr);
            String data = authData + signStr;
            byte[] hash = cryptoSupport.hmac_sha256(getAccessKey().getBytes(StandardCharsets.UTF_8),data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
    }

    private String getAuthDataHeaderValue() {
        LOG.debug("Starting to create auth data");
        String authData;
        Date currentTime = new Date();
        int randomInteger = getRandom().nextInt(Integer.MAX_VALUE);
        long epochTime = currentTime.getTime()/1000;
        LOG.debug("epochTime: {}",epochTime);
        authData = String.format(
                "%d, 0.0.0.0, 0.0.0.0, %d, %d, %s",
                config.signVersion(),
                epochTime,
                randomInteger,
                config.account());
        LOG.debug("Creating auth data from account: {} and sign version: {}",config.account(),config.version());
        return authData;
    }

    private boolean shouldProceed(){
        return StringUtils.isNotBlank(config.accessKey())
                && StringUtils.isNotBlank(config.account())
                && StringUtils.isNotBlank(config.cpCode())
                && StringUtils.isNotBlank(config.domain())
                && StringUtils.isNotBlank(config.version())
                && StringUtils.isNotBlank(config.downloadDomain());
    }

    private Random getRandom()  {
        if(null != rand){
           return rand;
        }
        rand = new Random();
        return rand;
    }

}
