package com.eaton.platform.integration.varnish.services;

import com.day.cq.replication.Agent;
import org.apache.sling.api.SlingHttpServletRequest;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface VarnishCacheClearService {

    LinkedHashMap<Agent, ArrayList<String>> clearVarnishCache(final SlingHttpServletRequest request);
}
