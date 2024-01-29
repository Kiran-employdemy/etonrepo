package com.eaton.platform.core.webtools.servlets.freesample;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class FreeSampleMyOrderFixtures {
	

	public  static List<NameValuePair> createFixtureWithOrderID() {
		List<NameValuePair> params = new ArrayList<>();
		params.add(createNameValuePair("orderStatus", "submitted"));
		
		params.add(createNameValuePair("orderType", "Sample"));
		
		params.add(createNameValuePair("offset", "0"));
		params.add(createNameValuePair("limit", "10"));
		params.add(createNameValuePair("orderId", "o154030145"));
		return params;
	}
	
	public  static List<NameValuePair> createFixtureWithEmailID() {
		List<NameValuePair> params = new ArrayList<>();
		params.add(createNameValuePair("orderStatus", "submitted"));
		
		params.add(createNameValuePair("orderType", "Sample"));
		
		params.add(createNameValuePair("offset", "0"));
		params.add(createNameValuePair("limit", "10"));
		params.add(createNameValuePair("email","kurtis@beh.com"));
		return params;
	}
	
	private static NameValuePair createNameValuePair(String name, String value) {
		// TODO Auto-generated method stub

		return new BasicNameValuePair(name, value);
	}

   


}