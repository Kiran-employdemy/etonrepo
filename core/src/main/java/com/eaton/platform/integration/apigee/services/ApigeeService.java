package com.eaton.platform.integration.apigee.services;

import org.apache.http.NameValuePair;

import java.util.List;

public interface ApigeeService {

    String getData(final String endpoint, final List<NameValuePair> params, String processFlow);
    int responseTimeout();

    String getFreeSampleOrders(String endpoint, List<NameValuePair> params);

}
