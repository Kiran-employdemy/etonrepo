package com.eaton.platform.integration.bullseye.services.impl;

import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.integration.bullseye.services.BullseyeCache;
import com.eaton.platform.integration.bullseye.services.config.BullseyeCacheConfig;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component(immediate = true, service = BullseyeCache.class)
@Designate(ocd = BullseyeCacheConfig.class)
public class SearchCacheImpl extends BullseyeCacheImpl {

    public static final String NAME = "searchcache";

    @Reference
    void setCloudConfigService(CloudConfigService cloudConfigService){
        super.configService = cloudConfigService;
    }

    @Reference
    void setHttpClientBuilderFactory( HttpClientBuilderFactory httpFactory){
        super.httpFactory = httpFactory;
    }

    @Override
    public String getCacheKey() {
        return StringUtils.EMPTY;
    }

    @Override
    public void build(List<NameValuePair> params) throws IOException, URISyntaxException {
        String result = executeApi(params);
        cache.put(convertParamsToKey(params),result);
    }
    @Override
    public String getCache(List<NameValuePair> params) throws IOException, URISyntaxException {
        String cacheValue = StringUtils.EMPTY;
        String key = convertParamsToKey(params);
        if(null != this.cache ){
            cacheValue = this.cache.getIfPresent(key);
            if(null == cacheValue){
                build(params);
                cacheValue = this.cache.getIfPresent(key);
            }
        }
        return cacheValue;
    }

    @Activate
    protected void activate(BullseyeCacheConfig config){
        this.config = config;
        super.cache = Caffeine.newBuilder()
                .maximumSize(config.cacheSize())
                .expireAfterWrite(config.ttl(), TimeUnit.SECONDS)
                .build();
    }

    private String convertParamsToKey(List<NameValuePair> params){
        String toStringVal =Integer.toString(params.hashCode());
        return Base64.getEncoder().encodeToString(toStringVal.getBytes());
    }

    @Override
    public boolean isCacheService(String name) {
        return SearchCacheImpl.NAME.equals(name);
    }
}
