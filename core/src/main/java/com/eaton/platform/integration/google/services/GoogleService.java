package com.eaton.platform.integration.google.services;


import org.apache.http.NameValuePair;

import java.util.List;

public interface GoogleService {

    String getResponse(final String endpoint, final List<NameValuePair> headers, final List<NameValuePair> params);
    int responseTimeout();

}
