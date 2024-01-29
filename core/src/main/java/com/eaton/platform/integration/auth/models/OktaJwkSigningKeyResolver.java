package com.eaton.platform.integration.auth.models;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import java.security.Key;
import java.util.Map;

public final class OktaJwkSigningKeyResolver implements SigningKeyResolver {

    private volatile Map<String, Key> keyMap;

    public OktaJwkSigningKeyResolver(Map<String, Key> keyMap) {
        this.keyMap = keyMap;
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        return getKey(header.getKeyId());
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, String plaintext) {
        return getKey(header.getKeyId());
    }

    private Key getKey(String keyId) {
        return keyMap.get(keyId);
    }
}
