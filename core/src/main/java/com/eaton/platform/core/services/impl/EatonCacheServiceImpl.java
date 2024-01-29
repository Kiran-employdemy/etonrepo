package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.services.EatonCacheService;
import com.eaton.platform.core.services.config.EatonCacheServiceConfig;
import java.util.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.concurrent.TimeUnit;


@Component(service = EatonCacheService.class,immediate = true)
public class EatonCacheServiceImpl implements EatonCacheService {

    private static int CONFIGURABLE_MAX_CACHE_SIZE;
    private static int CONFIGURABLE_DURATION;

    private static  Cache<String, String> cache;

    @Activate
    protected void activate(final EatonCacheServiceConfig config) {
        CONFIGURABLE_MAX_CACHE_SIZE = config.CONFIGURABLE_MAX_CACHE_SIZE();
        CONFIGURABLE_DURATION = config.CONFIGURABLE_DURATION();
        cache = CacheBuilder.newBuilder()
                .maximumSize(CONFIGURABLE_MAX_CACHE_SIZE)
                .expireAfterWrite(CONFIGURABLE_DURATION, TimeUnit.MINUTES)
                .build();

    }

    @Override
    public Optional<String> getCachedResponse(String key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    @Override
    public void setCachedResponse(String key, String response) {
        cache.put(key, response);
    }

}
