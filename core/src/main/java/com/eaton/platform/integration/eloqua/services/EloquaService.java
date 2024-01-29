package com.eaton.platform.integration.eloqua.services;

import com.eaton.platform.core.models.EloquaCloudConfigModel;
import org.apache.sling.api.SlingHttpServletRequest;

public interface EloquaService {

	String getEloquaForm(EloquaCloudConfigModel eloquaRequestBean, String formid);
	String getEloquaOptionList(EloquaCloudConfigModel eloquaRequestBean, String formid);
	String proxyToEloqua(final SlingHttpServletRequest postBody, final String postCookies, final String eloquaUrl);
	boolean doProxy();
	String defaultRedirect();
	int guidTimeout();

}