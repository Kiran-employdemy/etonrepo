package com.eaton.platform.integration.qr.services.impl;

import com.eaton.platform.integration.qr.services.AESService;
import com.eaton.platform.integration.qr.services.config.AESServiceConfig;
import org.apache.commons.codec.binary.Hex;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component(service = AESService.class, immediate = true)
@Designate(ocd = AESServiceConfig.class)
public class AESServiceImpl implements AESService {

    private Logger LOGGER = LoggerFactory.getLogger(AESServiceImpl.class);
    private static String algorithm;
    private static String aesKey;
    private static String algoWithPadding;
    private static String ivParameter;

    @Activate
    @Modified
    protected void activate(final AESServiceConfig config) {
        this.algorithm = config.algorithm();
        this.aesKey = config.key();
        this.algoWithPadding = config.algo_padding();
        this.ivParameter = config.iv_parameter();
    }
    private static Key key;
    private static Cipher decryptor;

    private void init() throws Exception
    {
        key = generateKey();
        IvParameterSpec iv = new IvParameterSpec(Hex.decodeHex(ivParameter.toCharArray()));
        decryptor=Cipher.getInstance(algoWithPadding);
        decryptor.init(Cipher.DECRYPT_MODE, key,iv);
    }

    private String getDecrypt(String encryptedData) throws Exception {
        byte[] decordedValue =   Base64.getDecoder().decode(encryptedData);
        byte[] decValue = decryptor.doFinal(decordedValue);
       final String decryptedValue = new String(decValue);
        LOGGER.debug("End decrypt method of serial number. :: decrypted data "+decryptedValue);
        return decryptedValue;
    }

    private static Key generateKey() {
        return new SecretKeySpec(aesKey.getBytes(), algorithm);
    }

    @Override
    public String decrypt(String encrypted) {
        try {
            LOGGER.debug("Start decrypt method of serial number. :: encrypted data "+encrypted);
            init();
            return getDecrypt(Base64.getEncoder().encodeToString(Hex.decodeHex(encrypted.toCharArray())));
        } catch (Exception e) {
            LOGGER.error("While Decrypting the "+e.getMessage());
        }
        return null;
    }
}