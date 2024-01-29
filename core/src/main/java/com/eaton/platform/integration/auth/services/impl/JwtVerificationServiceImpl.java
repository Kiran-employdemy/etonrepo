/*
 * Copyright 2018-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * parseKeysFromStream uses function built from okta-jwt-verifier-java project
 */
package com.eaton.platform.integration.auth.services.impl;

import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.models.*;
import com.eaton.platform.integration.auth.models.impl.SimpleAuthenticationTokenImpl;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.JwtVerificationService;
import com.eaton.platform.integration.auth.util.EatonAuthUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component(service = JwtVerificationService.class,immediate = true)
public class JwtVerificationServiceImpl implements JwtVerificationService {
    private static final Logger LOG = LoggerFactory.getLogger(JwtVerificationServiceImpl.class);

    private static final String LDAP_PARAM = "eatonid";
    private static final String UID = "uid";

    @Reference
    private AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Reference
    private HttpClientBuilderFactory httpFactory;

    private PoolingHttpClientConnectionManager conMgr;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private volatile Map<String, Key> keyMap;
    private JwtParser jwtParser;

    @Override
    public AuthenticationToken parseToken(String token) {
        AuthenticationToken authenticationToken = null;
        try {
            LOG.debug("Attempting to parse token: " + token);
            Jws<Claims> jws  = jwtParser.parseClaimsJws(token);
            LOG.debug("Token is valid: " + token);

            Date expirationDate = jws.getBody().getExpiration();
            LOG.debug("Token Expiration date = " + expirationDate.toString());
            String LDAPId = jws.getBody().get(LDAP_PARAM,String.class);
            LOG.debug("Token LDAP ID =" + LDAPId);
            String uid = jws.getBody().get(UID,String.class);
            LOG.debug("UID {}",uid);
            String subject = jws.getBody().getSubject();
            LOG.debug("Token subject =" + subject);

            authenticationToken = new SimpleAuthenticationTokenImpl(subject,LDAPId, expirationDate.toInstant().getEpochSecond());
            authenticationToken.setUid(uid);
        } catch (JwtException e) {
            LOG.error("Error parsing token: " + e.getMessage());
        } catch (Exception ex) {
            LOG.error("Error parsing token: " + ex.getMessage());
        }
        return authenticationToken;
    }

    @Activate
    @Modified
    protected final void activate() {

        /* Call Okta for public, parse and store in key map
        *  create JWTParser */
        jwtParser = refreshJwtParser();

        //Todo:  Add support for key refresh
    }

    private JwtParser refreshJwtParser(){

        keyMap = getOktaKeys();

        return Jwts.parserBuilder()
                .setSigningKeyResolver(signingKeyResolver(keyMap))
                .setAllowedClockSkewSeconds(authenticationServiceConfiguration.getOktaJwtExpirationLeeway())
                .requireIssuer(authenticationServiceConfiguration.getOktaIssuer())
                .requireAudience(authenticationServiceConfiguration.getOktaAudience())
                .build();
    }

    private Map<String, Key> getOktaKeys(){

        Map<String, Key> oktaKeys = new HashMap<>();
        conMgr = EatonAuthUtil.getMultiThreadedConf(conMgr, authenticationServiceConfiguration);
        InputStream responseKeyContent = EatonAuthUtil.getPublicKeysHttpCall(authenticationServiceConfiguration.getOktaKeyURI(),
                conMgr,
                httpFactory,
                authenticationServiceConfiguration);

        if(responseKeyContent != null){
            oktaKeys = parseKeysFromStream(responseKeyContent);
        }

        return oktaKeys;
    }

    private SigningKeyResolver signingKeyResolver(Map<String, Key> keys) {
        return new OktaJwkSigningKeyResolver(keys);
    }

    private BigInteger base64ToBigInteger(String value) {
        return new BigInteger(1, Decoders.BASE64URL.decode((value)));
    }

    private Map<String, Key> parseKeysFromStream(InputStream inputStream){
        Map<String, Key> parsedKeys = new HashMap<>();
        try {
            Map<String, Key> newKeys = objectMapper.readValue(inputStream, JwkKeys.class).getKeys().stream()
                    .filter(jwkKey -> AuthConstants.SIG_PUBLIC_KEY_USE.equals(jwkKey.getPublicKeyUse()))
                    .filter(jwkKey -> AuthConstants.RSA_KEY_TYPE.equals(jwkKey.getKeyType()))
                    .collect(Collectors.toMap(JwkKey::getKeyId, jwkKey -> {
                        BigInteger modulus = base64ToBigInteger(jwkKey.getPublicKeyModulus());
                        BigInteger exponent = base64ToBigInteger(jwkKey.getPublicKeyExponent());
                        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
                        try {
                            KeyFactory keyFactory = KeyFactory.getInstance(AuthConstants.RSA_KEY_TYPE);
                            return keyFactory.generatePublic(rsaPublicKeySpec);
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                            throw new IllegalStateException("Failed to parse public key");
                        }
                    }));

            parsedKeys = Collections.unmodifiableMap(newKeys);

        } catch (IOException e) {
            throw new JwtException("Failed to fetch keys from OKTA. URL: ", e);
        }
        return parsedKeys;
    }
}
