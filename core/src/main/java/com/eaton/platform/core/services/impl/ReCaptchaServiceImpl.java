package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.services.ReCaptchaService;
import com.eaton.platform.core.services.config.ReCaptchaServiceConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang.CharEncoding;
import org.apache.http.HttpResponse;
import java.net.URISyntaxException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.ClientProtocolException;

@Component(
	service = ReCaptchaService.class,
	immediate = true,
	property = {
		AEMConstants.SERVICE_DESCRIPTION + "ReCaptcha Service",
		AEMConstants.SERVICE_VENDOR_EATON,
		AEMConstants.PROCESS_LABEL + "ReCaptchaServiceImpl"
	})
@Designate(ocd = ReCaptchaServiceConfig.class)
public class ReCaptchaServiceImpl implements ReCaptchaService {
	private static final Logger LOG = LoggerFactory.getLogger(ReCaptchaServiceImpl.class);

	@Reference
	private CloudConfigService configService;

	@Reference
	private HttpClientBuilderFactory httpFactory;

	private PoolingHttpClientConnectionManager conMgr;
	private String validationUrl;
	private int maxConnections;
	private int maxHostConnections;

	@Activate
	@Modified
	protected final void activate(final ReCaptchaServiceConfig config)  {
		if (null != config) {
			validationUrl = config.validationUrl();
			maxConnections = config.maxConnections();
			maxHostConnections = config.maxHostConnections();
		}
	}

	/**
	 * @param reCaptchaResponse The "g-recaptcha-response" parameter generated from the reCaptcha client side code.
	 * @param reCaptchaSecret The reCaptcha secret configured in the Eloqua cloud config.
	 * @return Whether or not the reCaptcha validation was successful.
	 */
	public boolean validate(final String reCaptchaResponse, final String reCaptchaSecret) {
		LOG.info("ReCaptchaServiceImpl :: validating reCAPTCHA response from client: " + reCaptchaResponse);
		boolean isValid = false;
		HttpPost httpPost = null;

		try {
			final ArrayList<NameValuePair> postParameters = new ArrayList<>(Arrays.asList(
					new BasicNameValuePair(PARAM_RECAPTCHA_VERIFICATION_RESPONSE, reCaptchaResponse),
					new BasicNameValuePair(PARAM_RECAPTCHA_VERIFICATION_SECRET,reCaptchaSecret)));

			httpPost = new HttpPost(new URIBuilder(validationUrl).build());
			httpPost.setEntity(new UrlEncodedFormEntity(postParameters, CharEncoding.UTF_8));

			final HttpClient client = httpFactory.newBuilder().setConnectionManager(getMultiThreadedConf()).build();
			final HttpResponse response = client.execute(httpPost);

			LOG.info("ReCaptchaServiceImpl :: validate :: " + response.getStatusLine());

			final JsonElement jsonResponse = new JsonParser().parse(new InputStreamReader(response.getEntity().getContent()));

			LOG.debug("ReCaptchaServiceImpl :: Google Validation Service Response: " + jsonResponse.toString());

			isValid = jsonResponse.getAsJsonObject().get(PARAM_RECAPTCHA_VERIFICATION_SUCCESS).getAsBoolean();

			LOG.info("ReCaptchaServiceImpl :: isValid: " + Boolean.toString(isValid));
		} catch (URISyntaxException e) {
			LOG.error("URISyntaxException while calling the google reCaptcha validation API", e);
		} catch (ClientProtocolException e) {
			LOG.error("ClientProtocolException while calling the google reCaptcha validation API", e);
		} catch (IOException e) {
			LOG.error("IOException while calling the google reCaptcha validation API", e);
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}

		return isValid;
	}

	private PoolingHttpClientConnectionManager getMultiThreadedConf() {
		if (conMgr == null) {
			conMgr = new PoolingHttpClientConnectionManager();
			conMgr.setMaxTotal(maxConnections);
			conMgr.setDefaultMaxPerRoute(maxHostConnections);
		}
		return conMgr;
	}
}
