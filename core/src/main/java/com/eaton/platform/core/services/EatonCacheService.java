package com.eaton.platform.core.services;


import java.util.Optional;

public interface EatonCacheService {
    Optional<String> getCachedResponse(final String key);
    void setCachedResponse(final String key, final String response);
}
