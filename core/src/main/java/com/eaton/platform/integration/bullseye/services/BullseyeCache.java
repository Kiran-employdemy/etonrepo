package com.eaton.platform.integration.bullseye.services;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface BullseyeCache {
    void build(List<NameValuePair> params) throws IOException, URISyntaxException;
    String getCache(List<NameValuePair> params) throws IOException, URISyntaxException;
    boolean isCacheService(String name);
}
