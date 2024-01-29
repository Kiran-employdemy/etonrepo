package com.eaton.platform.integration.bullseye.services.impl;

import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.integration.bullseye.services.BullseyeCache;
import com.eaton.platform.integration.bullseye.services.config.BullseyeCacheConfig;
import com.eaton.platform.integration.bullseye.services.impl.SearchCacheImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*","javax.net.ssl.*"})
@PrepareForTest(Caffeine.class)
public class SearchCacheImplTest {

    @Rule
    public final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    public BullseyeCache fixture;

    @Mock
    Cache<String,String> mockCache;

    @Mock
    HttpClientBuilderFactory mockHttpClientBuilderFactory;

    @Mock
    PoolingHttpClientConnectionManager mockConMgr;

    @Mock
    CloudConfigService mockCloud;

    @Mock
    HttpClientBuilder mockBuilder;

    @Mock
    CloseableHttpClient mockClient;

    @Mock
    CloseableHttpResponse mockResponse;

    @Before
    public void setup(){
        aemContext.registerService(HttpClientBuilderFactory.class,mockHttpClientBuilderFactory);
        aemContext.registerService(PoolingHttpClientConnectionManager.class,mockConMgr);
        aemContext.registerService(CloudConfigService.class,mockCloud);
    }

    @Test
    public void testGetCacheReturnCache() throws IOException, URISyntaxException {

        Caffeine mockCacheCenteral = Mockito.mock(Caffeine.class,RETURNS_SELF);

        PowerMockito.mockStatic(Caffeine.class);
        PowerMockito.when(Caffeine.newBuilder()).thenReturn(mockCacheCenteral);
        when(mockCacheCenteral.build()).thenReturn(mockCache);

        when(mockCache.getIfPresent(any())).thenReturn("abc");
        Map<String,Object> properties = new HashMap<>();
        properties.put("ttl",60);
        properties.put("cacheSize",1000);
        properties.put("cacheUrl","");
        properties.put("maxConnection",10);
        properties.put("maxHostConnections",10);
                fixture = aemContext.registerInjectActivateService(new SearchCacheImpl(),properties);

        List<NameValuePair> parmas = new ArrayList<>();
        parmas.add(new BasicNameValuePair("apiKey","weoirweroi"));
        String cache = fixture.getCache(parmas);
        Assert.assertNotNull(cache);
        Assert.assertEquals(cache,"abc");

    }

    @Test
    public void testGetCacheSetCacheAndReturnCache() throws IOException, URISyntaxException {

        StatusLine mockStatusLine = Mockito.mock(StatusLine.class);
        BasicHttpEntity testEntity = new BasicHttpEntity();
        testEntity.setContent(IOUtils.toInputStream("abc","UTF-8"));
        when(mockHttpClientBuilderFactory.newBuilder()).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockClient);
        when(mockClient.execute(any())).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        when(mockResponse.getEntity()).thenReturn(testEntity);

        Map<String,Object> properties = new HashMap<>();
        properties.put("ttl",60);
        properties.put("cacheSize",1000);
        properties.put("cacheUrl","http://api.com");
        properties.put("maxConnection",10);
        properties.put("maxHostConnections",10);
        fixture = aemContext.registerInjectActivateService(new SearchCacheImpl(),properties);

        List<NameValuePair> parmas = new ArrayList<>();
        parmas.add(new BasicNameValuePair("apiKey","weoirweroi"));
        String cache = fixture.getCache(parmas);
        Assert.assertNotNull(cache);
        Assert.assertEquals(cache,"abc");

        //ensure client is called
        verify(mockClient,times(1)).execute(any());
        verify(mockResponse,times(1)).getEntity();
    }
}
