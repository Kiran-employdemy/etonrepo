package com.eaton.platform.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class EncryptionUtil {
    private static final Logger LOG = LoggerFactory.getLogger(EncryptionUtil.class);

    /**
     * Instantiates a new AuthEncryptionUtil
     */
    private EncryptionUtil() {
        LOG.debug("Inside EncryptionUtil constructor");
    }

    /*
    * encrypt plain text
    * @param plainText
    * @param algorithm - encryption algorithm (ex: DES)
    * @param mode - encryption mode (ex: DES/CBC/PKCS5Padding)
    * @param key - encryption key
    * @returns encryptionResults [randomIvParameterSpecStr, cipherTextBase64Str]
     */
    public static List<String> encrypt(String plainText, String algorithm, String mode, String key){
        LOG.debug("EncryptionUtil.encrypt: START");
        LOG.debug("plainText: {}", plainText);
        LOG.debug("algorithm: {}", algorithm);
        LOG.debug("mode: {}", mode);
        LOG.debug("key: {}", key);
        
        List<String> encryptionDetails = new ArrayList<>();

        try {
            SecretKey secretKey = getSecretKey(key, algorithm);
            IvParameterSpec randomIvParameterSpec = getIvSecureRandom(algorithm);
            String randomIvParameterSpecStr = Base64.getEncoder().encodeToString(randomIvParameterSpec.getIV());
            encryptionDetails.add(randomIvParameterSpecStr);
            LOG.debug("randomIvParameterSpecStr added to encryptionDetails list: {}", randomIvParameterSpecStr);
            
            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, randomIvParameterSpec);
            
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            String cipherTextBase64Str = Base64.getEncoder().encodeToString(cipherText);
            encryptionDetails.add(cipherTextBase64Str);
            LOG.debug("cipherTextBase64Str added to encryptionDetails list: {}", cipherTextBase64Str);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            LOG.error("Exception while encrypting value. Message: {}", e.getMessage());
            LOG.error("Exception while encrypting value. Cause: {}", e.getCause().toString());
        }

        LOG.debug("EncryptionUtil.encrypt END");
        return encryptionDetails;

    }

    /*
     * decrypt cipher text
     * @param cipherText - encrypted text
     * @param algorithm - encryption algorithm (ex: DES)
     * @param mode - encryption mode (ex: DES/CBC/PKCS5Padding)
     * @param key - encryption key
     * @param ivSpec - initialization vector parameter
     * @returns decrypted string
     */
    public static String decrypt(String cipherText, String algorithm, String mode, String key, IvParameterSpec ivSpec) {

        String plaintTextStr = null;
        try {
            LOG.debug("EncryptionUtil.decrypt END");
            Cipher cipher = Cipher.getInstance(mode);
            SecretKey secretKey = getSecretKey(key, algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] plainTextBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            plaintTextStr = new String(plainTextBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
            LOG.error("Exception while decrypting value. Message: {}", e.getMessage());
            LOG.error("Exception while decrypting value. Cause: {}", e.getCause().toString());
        }
        LOG.debug("EncryptionUtil.decrypt END");
        return plaintTextStr;
    }

    /*
    * Gets secret key
     * @param key - encryption key
     * @param algorithm - encryption algorithm (ex: DES)
    * @returns SecretKeySpec
     */
    public static SecretKey getSecretKey(String encryptionKey, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        LOG.debug("EncryptionUtil.getSecretKey START");
        SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), algorithm);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
        SecretKey secretKey = secretKeyFactory.generateSecret(secretKeySpec);
        LOG.debug("secretKey: {}", secretKeySpec.getEncoded());
        LOG.debug("EncryptionUtil.getSecretKey END");
        return secretKey;
    }

    /*
    * Gets random initialization vector
    * @param algorithm - encryption algorithm
    * @returns iv parameter spec
     */
    public static IvParameterSpec getIvSecureRandom(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
        LOG.debug("EncryptionUtil.getIvSecureRandom START");
        SecureRandom random = new SecureRandom();
        byte[] randomIv = new byte[Cipher.getInstance(algorithm).getBlockSize()];
        random.nextBytes(randomIv);
        IvParameterSpec ivSpec = new IvParameterSpec(randomIv);
        LOG.debug("randomIvParameterSpecStr added to encryptionDetails list: {}", ivSpec);
        LOG.debug("randomIvParameterSpecStr length: {}", ivSpec.toString().length());
        LOG.debug("EncryptionUtil.getIvSecureRandom END");
        return ivSpec;
    }

}
