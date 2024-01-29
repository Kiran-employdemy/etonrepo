package com.eaton.platform.integration.bullseye.services.impl;

import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.integration.bullseye.constants.BullseyeConstant;
import com.eaton.platform.integration.bullseye.services.BullseyeCache;
import com.eaton.platform.integration.bullseye.services.config.BullseyeCacheConfig;
import com.github.benmanes.caffeine.cache.Cache;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class BullseyeCacheImpl implements BullseyeCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(BullseyeCacheImpl.class);

    protected BullseyeCacheConfig config;
    protected HttpClientBuilderFactory httpFactory;
    protected PoolingHttpClientConnectionManager conMgr;
    protected CloudConfigService configService;

    protected Cache<String,String> cache;

    public abstract String getCacheKey();

    protected String executeApi(List<NameValuePair> params) throws URISyntaxException, IOException {
        LOGGER.debug("Start with executeAPI method ::");
        final StringBuilder content = new StringBuilder();
        conMgr = getMultiThreadedConf();
        try {
            final HttpClient client = httpFactory.newBuilder().setConnectionManager(conMgr).build();
            final URIBuilder uriBuilder = new URIBuilder(config.cacheUrl());
            uriBuilder.addParameters(params);
            final URI build = uriBuilder.build();
            final HttpGet httpGet = new HttpGet(build);
            final HttpResponse execute = client.execute(httpGet);
            final String reasonPhrase = execute.getStatusLine().getReasonPhrase();
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                try (final BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(execute.getEntity().getContent()))) {
                    String line;
                    while (null != (line = bufferedReader.readLine())) {
                        content.append(line);
                    }

                    }

            }
            LOGGER.info("reasonPhrase :: {}", reasonPhrase);

            LOGGER.debug("End with executeAPI method ::");
        } catch (ClientProtocolException e) {
            LOGGER.error("Error in Bulls eye api {}", e.getLocalizedMessage());
        } catch (IOException ioe) {
            LOGGER.error("Error in Bulls eye api {}", ioe.getLocalizedMessage());
        } catch (Exception generice) {
            LOGGER.error("Error in Bulls eye api {}", generice.getLocalizedMessage());
        } finally {
            if (conMgr != null) {
                conMgr.closeExpiredConnections();
                conMgr.closeIdleConnections(50L, TimeUnit.SECONDS);
            }
        }
        return content.toString();
    }

    /**
     * This method is to get Cache Key dynamically based on Category ID.
     * if category id is not present, get the  static key.
     * @param params list of input parameters.
     * @return Cache Key
     */
    private String getCacheKey(List<NameValuePair> params) {
        for (NameValuePair param : params) {
            if (param.getName().equals(BullseyeConstant.CATEGORY_GROUP_ID)) {
                return param.getValue();
            }
        }
        return getCacheKey();
    }

    private PoolingHttpClientConnectionManager getMultiThreadedConf() {
        if (conMgr == null) {
            conMgr = new PoolingHttpClientConnectionManager();
            conMgr.setMaxTotal(config.maxConnections());
            conMgr.setDefaultMaxPerRoute(config.maxHostConnections());
        }
        return conMgr;
    }


    @Override
    public void build(List<NameValuePair> params) throws IOException, URISyntaxException {
        String result = executeApi(params);
        cache.put(getCacheKey(params),result);
    }


    public String getCache(List<NameValuePair> params) throws IOException, URISyntaxException {
      String cacheValue = StringUtils.EMPTY;
      if(null != this.cache ){
          cacheValue = this.cache.getIfPresent(getCacheKey(params));
          if(null == cacheValue){
              build(params);
              cacheValue = this.cache.getIfPresent(getCacheKey(params));
          }
      }
      return cacheValue;
    }
}
