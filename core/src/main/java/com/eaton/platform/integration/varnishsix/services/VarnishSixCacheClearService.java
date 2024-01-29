package com.eaton.platform.integration.varnishsix.services;

import com.day.cq.replication.Agent;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpPost;
import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface VarnishSixCacheClearService {
    LinkedHashMap<Agent, ArrayList<String>> clearVarnishSixCache(final SlingHttpServletRequest request);
    HttpResponse loginSendRequest(final HttpPost request,
                                  final String user, final String password, String orgValue) throws AuthenticationException, JSONException;
}
