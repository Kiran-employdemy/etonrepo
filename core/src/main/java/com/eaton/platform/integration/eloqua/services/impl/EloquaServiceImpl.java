package com.eaton.platform.integration.eloqua.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.EloquaCloudConfigModel;
import com.eaton.platform.integration.eloqua.constant.EloquaConstants;
import com.eaton.platform.integration.eloqua.services.EloquaService;
import com.eaton.platform.integration.eloqua.services.config.EloquaServiceConfig;
import com.google.common.net.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.SM;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * <html> Description: This class is used to get the form details from Eloqua.
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 *
 */
@Component(service = EloquaService.class,immediate = true,
		property = {
				AEMConstants.SERVICE_DESCRIPTION + "Eloqua Service",
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.PROCESS_LABEL + "EloquaServiceImpl"
		})
@Designate(ocd = EloquaServiceConfig.class)
public class EloquaServiceImpl implements EloquaService {

	private PoolingHttpClientConnectionManager conMgr;
	private int maxConnections;
	private int maxHostConnections;
	private boolean doProxy;
	private String defaultRedirect;
	private int guidTimeout;

	@Reference
	private HttpClientBuilderFactory httpFactory;

	@Activate
	@Modified
	protected final void activate(final EloquaServiceConfig config)  {
		if (null != config) {
			maxConnections = config.maxConnections();
			maxHostConnections = config.maxHostConnections();
			doProxy = config.doProxy();
			defaultRedirect = config.defaultRedirect();
			guidTimeout = config.guidTimeout();
		}
	}

	public boolean doProxy() {
		return doProxy;
	}

	public String defaultRedirect() { return defaultRedirect; }

	public int guidTimeout() {
		return guidTimeout;
	}
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(EloquaService.class);
	@Override
	public String getEloquaForm(final EloquaCloudConfigModel cloudConfig, final String formId) {
		LOG.info("entered into EloquaServiceImpl getEloquaForm() method");
		// Get _authToken of eloqua form
		final String _authToken = getAuthToken(cloudConfig);
		// set api end point url: /api/REST/1.0/assets/form/{id}CommentedCode
		final String uri = cloudConfig.getEloquaEndpointUrl().concat(formId);
		String body = null;
		body = getResponseBodyFromEloqua(cloudConfig,uri,_authToken);
		return body;
	}

	@Override
	public String getEloquaOptionList(final EloquaCloudConfigModel cloudConfig, final String formId) {
		LOG.info("entered into EloquaServiceImpl getEloquaForm() method");
		// Get _authToken of eloqua form
		final String _authToken = getAuthToken(cloudConfig);
		// set api end point url: /api/REST/1.0/assets/optionList/{id}?depth=complete
		String eloquaEndpointUrl = cloudConfig.getEloquaEndpointUrl();
		if(eloquaEndpointUrl.contains(EloquaConstants.FORM)){
			eloquaEndpointUrl = eloquaEndpointUrl.replace(EloquaConstants.FORM,EloquaConstants.OPTION_LIST);
		}
		final String uri = eloquaEndpointUrl.concat(formId).concat(EloquaConstants.DEPTH_COMPLETE);
		String body = null;
		body = getResponseBodyFromEloqua(cloudConfig,uri,_authToken);
		return body;
	}

	private String getResponseBodyFromEloqua(final EloquaCloudConfigModel cloudConfig, final String uri, final String _authToken){
		final StringBuilder responseBody = new StringBuilder();
		LOG.info("Starting from EloquaServiceImpl getEloquaForm() method");
		try{
			String baseUrl = cloudConfig.getEloquaserverurl();
			final HttpClient client = httpFactory.newBuilder().setConnectionManager(getMultiThreadedConf()).build();
			final URIBuilder uriBuilder = new URIBuilder(baseUrl.concat(uri));
			final URI build = uriBuilder.build();

			HttpGet httprequest = new HttpGet(build);
			httprequest.addHeader(CommonConstants.ACCEPT, CommonConstants.APPLICATION_JSON);
			httprequest.addHeader(EloquaConstants.AUTHORIZATION, _authToken);

			final HttpResponse httpResponse = client.execute(httprequest);
			final String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				String line;
				while (null != (line = bufferedReader.readLine()) ) {
					responseBody.append(line);
				}
				bufferedReader.close();

			} else {
				LOG.debug("Eloqua API request returned the following status: {}", reasonPhrase);
			}



		}  catch (URISyntaxException e) {
			LOG.error("URISyntaxException while calling the Eloqua api: {}", e.getMessage());
		} catch (ClientProtocolException e) {
			LOG.error("ClientProtocolException while calling the Eloqua api: {}", e.getMessage());
		} catch (IOException e) {
			LOG.error("IOException while calling the Eloqua api: {}", e.getMessage());
		}

		LOG.info("exited from EloquaServiceImpl getEloquaForm() method");
		return responseBody.toString();

	}

	private String getAuthToken(final EloquaCloudConfigModel cloudConfig){
		String _authToken =  StringUtils.EMPTY;
		if(cloudConfig != null) {
			final String user = cloudConfig.getEloquaUsername();
			final String password = cloudConfig.getEloquaPwd();
			// set authorization string: for example "CompanyName\" + user + ":" +
			// password
			final String authString = cloudConfig.getEloquaCompanyName().concat(EloquaConstants.CONST_BACKWARD_SLASH).concat(user)
					.concat(EloquaConstants.CONST_COLON).concat(password);
			// set authorization token
			_authToken = EloquaConstants.CONST_BASIC
					.concat(javax.xml.bind.DatatypeConverter.printBase64Binary(authString.getBytes()));
		}
		return _authToken;
	}

	public String proxyToEloqua(final SlingHttpServletRequest postBody, final String postCookies, final String eloquaUrl) {
		LOG.info("EloquaServiceImpl :: proxyToEloqua :: Started");

		LOG.debug("EloquaServiceImpl :: proxyToEloqua :: postBody :: {} ", postBody);
		LOG.debug("EloquaServiceImpl :: proxyToEloqua :: postCookies :: {}", postCookies);
		LOG.debug("EloquaServiceImpl :: proxyToEloqua :: eloquaUrl :: {}", eloquaUrl);

		HttpPost httpPost = null;
		String responseString = null;

		try {
			final HttpClient client = httpFactory.newBuilder().setConnectionManager(getMultiThreadedConf()).build();
			httpPost = new HttpPost(new URIBuilder(eloquaUrl).build());
			httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.FORM_DATA.toString());
			httpPost.setHeader(SM.COOKIE, postCookies);
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
			List<RequestParameter> listReqParam =postBody.getRequestParameterList();
			for(RequestParameter reqParam :listReqParam ){
				String eloquaFormKey=reqParam.getName();
				String eloquaFormValue=reqParam.getString();
				nameValuePairs.add(new BasicNameValuePair(eloquaFormKey,eloquaFormValue));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,CommonConstants.UTF_8));
			HttpResponse response = client.execute(httpPost);
			//String responseStatus = response.getStatusLine().toString();
			responseString = EntityUtils.toString(response.getEntity(), CommonConstants.UTF_8);

			LOG.info("EloquaServiceImpl :: proxyToEloqua :: response from Eloqua status :: {}",  response.getStatusLine());
		} catch (URISyntaxException e) {
			LOG.error("URISyntaxException while calling Eloqua API: ", e);
		} catch (ClientProtocolException e) {
			LOG.error("ClientProtocolException while calling Eloqua API: ", e);
		} catch (IOException e) {
			LOG.error("IOException while calling Eloqua API: ", e);
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}

		LOG.info("EloquaServiceImpl :: proxyToEloqua :: Finished");

		return responseString;
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
