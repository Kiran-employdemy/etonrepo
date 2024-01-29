package com.eaton.platform.integration.endeca.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.factories.HttpPostFactory;
import com.eaton.platform.integration.endeca.pojo.pdh.EndecaPdhResponse;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaQRService;
import com.eaton.platform.integration.endeca.services.config.EndecaQRServiceConfig;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <html> Description: This class is used to get the details from Endeca.
 *
 * @author Victoria Pershick
 * @version 1.0
 * @since 2023
 *
 */

@Designate(ocd = EndecaQRServiceConfig.class)
@Component(service = EndecaQRService.class,immediate = true,
property = {
		AEMConstants.SERVICE_DESCRIPTION + "Endeca QR Service",
		AEMConstants.SERVICE_VENDOR_EATON,
		AEMConstants.PROCESS_LABEL + "EndecaQRServiceImpl"
})
public class EndecaQRServiceImpl implements EndecaQRService {

	private static final Logger LOG = LoggerFactory.getLogger(EndecaQRServiceImpl.class);
	private static final String SEARCH = "search";
	private static final String STARTING_RECORD_NUMBER = "0";
	private static final String RECORDS_TO_RETURN = "10";

	private String globalSkuPagePath;
	private EndecaConfigServiceBean endecaConfigBean;
	private PoolingHttpClientConnectionManager conMgr;
	private HttpClient httpClient;
	private HttpPostFactory httpPostFactory;

	@Reference
	protected EndecaConfig endecaConfig;

	private LoadingCache<EndecaServiceRequestBean, String> espServiceCache;

	/**
	 * Initialize the EndecaConfig inside this class
	 *
	 * @param ed the EndecaConfig
	 */
	public EndecaQRServiceImpl(EndecaConfig ed){
		this.endecaConfig =ed;
	}

	public EndecaQRServiceImpl(){}

	@Activate
	protected void init(final EndecaQRServiceConfig endecaQRServiceConfig) {
		globalSkuPagePath = endecaQRServiceConfig.global_sku_page_path();
		conMgr = new PoolingHttpClientConnectionManager();
		conMgr.setMaxTotal(endecaQRServiceConfig.max_connections());
		conMgr.setDefaultMaxPerRoute(endecaQRServiceConfig.max_host_connections());
		httpClient = HttpClients.custom().setConnectionManager(conMgr).build();
		httpPostFactory = new HttpPostFactory();
		endecaConfigBean = endecaConfig.getConfigServiceBean();
		espServiceCache = Caffeine.newBuilder()
				.maximumSize(endecaQRServiceConfig.max_cache_size())
				.expireAfterWrite(endecaQRServiceConfig.max_cache_duration(), TimeUnit.SECONDS)
				.build(endecaServiceRequestBean -> callESPService(endecaServiceRequestBean));
	}

	/**
	 * This method will return the global SKU page as specified in OSGi
	 *
	 * @return  globalSkuPagePath
	 */
	@Override
	public String getGlobalSkuPagePath() {
		return globalSkuPagePath;
	}

	/**
	 * This method will return the search results
	 *
	 * @param endecaRequestBean
	 * @return  Search results
	 */
	@Override
	public EndecaPdhResponse getEatonpdhlstcountries(EndecaServiceRequestBean endecaRequestBean) {
		Optional<String> cachedResponse = Optional.ofNullable(espServiceCache.get(endecaRequestBean));
		return cachedResponse.map(s -> new Gson().fromJson(s, EndecaPdhResponse.class)).orElse(null);
	}

	/**
	 * This method is to invoke the ESP service via SOAP HTTP.
	 *
	 * @param endecaServiceRequestBean
	 * @return JSON String response
	 * @throws IOException
	 */
	protected String callESPService(EndecaServiceRequestBean endecaServiceRequestBean) {
		LOG.info("Entry into callESPService method ");
		String strResponse = null;
		HttpPost httpPost = null;
		String request = "{}";
		try {
			if (endecaServiceRequestBean != null) {
				request = new Gson().toJson(endecaServiceRequestBean);
			}
			httpPost = httpPostFactory.createEndecaRequestHttpPost(endecaConfigBean.getEndecaPDH1PDH2ServieURL(), request);

			final HttpResponse response = httpClient.execute(httpPost);
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				strResponse = EntityUtils.toString(entity);
				if(LOG.isDebugEnabled()) {
					LOG.debug("Response from ESP:{}", strResponse);
				}
				int start = strResponse.indexOf("<return>");
				int end = strResponse.lastIndexOf("</return>");

				if(start > 0 && end > 0) {
					strResponse = strResponse.substring(start + 8, end);
				}
			}
		} catch (IOException e) {
			LOG.error(e + ", " + e.getMessage());
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		LOG.info("Exit from callESPService method ");
		return strResponse;
	}

	/*
	Can you move this method to EndecaQRServiceImpl.java
	 */
	public EndecaServiceRequestBean createPdhEndecaRequestBean(String catalogNumber) {
		EndecaServiceRequestBean endecaPdhRequestBean = new EndecaServiceRequestBean();
		endecaPdhRequestBean.setSearchApplication(EndecaConstants.PRODUCT_COUNTRY_LIST);
		endecaPdhRequestBean.setSearchApplicationKey(endecaConfig.getConfigServiceBean().getEspAppKey());
		endecaPdhRequestBean.setFunction(SEARCH);
		endecaPdhRequestBean.setSearchTerms(catalogNumber);
		endecaPdhRequestBean.setLanguage(EndecaConstants.SEARCH_TERM_IGNORE);
		endecaPdhRequestBean.setStartingRecordNumber(STARTING_RECORD_NUMBER);
		endecaPdhRequestBean.setNumberOfRecordsToReturn(RECORDS_TO_RETURN);
		return endecaPdhRequestBean;
	}

}
