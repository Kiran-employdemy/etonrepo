package com.eaton.platform.integration.bullseye.services.impl;

import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.integration.bullseye.services.BullseyeCache;
import com.eaton.platform.integration.bullseye.services.config.BullseyeCacheConfig;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.util.concurrent.TimeUnit;

@Component(immediate = true, service = BullseyeCache.class)
@Designate(ocd = BullseyeCacheConfig.class)
public class CategoryGroupCacheImpl extends BullseyeCacheImpl {

    public static final String NAME = "categorygroupcache";

    @Reference
    void setCloudConfigService(CloudConfigService cloudConfigService){
        super.configService = cloudConfigService;
    }

    @Reference
    void setHttpClientBuilderFactory( HttpClientBuilderFactory httpFactory){
        super.httpFactory = httpFactory;
    }

    @Override
    public boolean isCacheService(String name) {
        return CategoryGroupCacheImpl.NAME.equals(name);
    }

    @Activate
    protected void activate(BullseyeCacheConfig config){
        this.config = config;
        super.cache = Caffeine.newBuilder()
                .maximumSize(config.cacheSize())
                .expireAfterWrite(config.ttl(), TimeUnit.SECONDS)
                .build();
    }


    @Override
    public String getCacheKey() {
        return CategoryGroupCacheImpl.NAME;
    }
}
