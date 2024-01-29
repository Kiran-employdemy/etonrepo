package com.eaton.platform.integration.akamai.services;

import com.day.cq.replication.Agent;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface AkamaiClear {

    LinkedHashMap<Agent, ArrayList<String>> clearAkamaiCache(final SlingHttpServletRequest request);
}
