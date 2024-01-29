package com.eaton.platform.integration.endeca.services.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.EndecaConfigModel;
import com.eaton.platform.core.models.EndecaFacetTag;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.crossreference.CrossReferenceResponseBean;
import com.eaton.platform.integration.endeca.bean.crossreference.XRefResult;
import com.eaton.platform.integration.endeca.bean.crossreference.XRefSearchResults;
import com.eaton.platform.integration.endeca.bean.familymodule.FamilyModuleResponseBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityListResponseBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityResponseBean;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchPageResponse;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyResponseBean;
import com.eaton.platform.integration.endeca.bean.typeahead.TypeAheadResponse;
import com.eaton.platform.integration.endeca.bean.typeahead.TypeAheadSiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchToolResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueToolResponse;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.helpers.EndecaServiceHelper;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.integration.endeca.services.config.EndecaServiceConfig;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * <html> Description: This class is used to get the details from Endeca.
 *
 * @author TCSgetProductCompareSKUList
 * @version 1.0
 * @since 2017
 *
 */
@Designate(ocd = EndecaServiceConfig.class)
@Component(service = EndecaService.class,immediate = true,
		property = {
				AEMConstants.SERVICE_DESCRIPTION + "Endeca Service",
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.PROCESS_LABEL + "EndecaServiceImpl"
		})
public class EndecaServiceImpl implements EndecaService {

	private static final Logger LOG = LoggerFactory.getLogger(EndecaServiceImpl.class);
	private EndecaConfigServiceBean endecaConfigBean;
	private PoolingHttpClientConnectionManager conMgr;

	private SlingHttpServletRequest slingRequest;

	@Reference
	protected EndecaConfig endecaConfigService;

	@Reference
	private CloudConfigService cloudConfigService;

	@Reference
	AdminService adminService;

	private EndecaServiceHelper endecaServiceHelper;
	private LoadingCache<EndecaServiceRequestBean, String> espServiceCache;

	public EndecaServiceImpl(EndecaConfig ed){
		this.endecaConfigService =ed;
	}

	public EndecaServiceImpl(){}
	private static int CONFIGURABLE_MAX_CACHE_SIZE;
	private static int CONFIGURABLE_DURATION;
	private static int CONNECTION_TIMEOUT;
	private static int SOCKET_TIMEOUT;
	private static int MAX_CONNECTIONS;
	private static int MAX_HOST_CONNECTIONS;
	@Activate
	protected void init(final EndecaServiceConfig endecaServiceConfig) {
		CONFIGURABLE_MAX_CACHE_SIZE = endecaServiceConfig.MAX_CACHE_SIZE();
		CONFIGURABLE_DURATION = endecaServiceConfig.MAX_CACHE_DURATION();
		CONNECTION_TIMEOUT = endecaServiceConfig.CONNECTION_TIMEOUT();
		SOCKET_TIMEOUT = endecaServiceConfig.SOCKET_TIMEOUT();
		MAX_CONNECTIONS = endecaServiceConfig.MAX_CONNECTIONS();
		MAX_HOST_CONNECTIONS = endecaServiceConfig.MAX_HOST_CONNECTIONS();
		espServiceCache = Caffeine.newBuilder()
				.maximumSize(CONFIGURABLE_MAX_CACHE_SIZE)
				.expireAfterWrite(CONFIGURABLE_DURATION, TimeUnit.SECONDS)
				.build(endecaServiceRequestBean -> callESPService(endecaServiceRequestBean));
	}

	/**
	 * This method will fetch the sub category or the product family list based
	 * on the filters selected
	 *
	 * @param endecaServiceRequestBean
	 * @return Product Family List or Sub Category List
	 */
	@Override
	public FamilyListResponseBean getProductFamilyList(EndecaServiceRequestBean endecaServiceRequestBean) {
		LOG.info("Entry into getProductFamilyList method");
		String familyListJsonResponse = null;
		FamilyListResponseBean familyListResponseBean = null;
		try (ResourceResolver resourceResolver = adminService.getReadService()){
			endecaServiceHelper = new EndecaServiceHelper();
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
			Optional<String> cachedResponse = Optional.ofNullable(espServiceCache.get(endecaServiceRequestBean));
			if (cachedResponse.isPresent()) {
				familyListResponseBean = endecaServiceHelper.convertFamilyListJSONTOBean(StringEscapeUtils
						.unescapeHtml(cachedResponse.get()));
			} else {
				if (endecaConfigBean.getStubResEnabled() && endecaConfigBean.getStubResponsePath() != null) {
					// Response File from DAM
					String filepath = endecaConfigBean.getStubResponsePath()+EndecaConstants.SUBCATEGORY_RESPONSE_FILENAME;
					LOG.debug("Stub Response File Path::"+filepath);
					familyListJsonResponse = CommonUtil.getResponseStringFromDAM(adminService,filepath);
					LOG.debug("Stub Response::"+familyListJsonResponse);
				} else {
					familyListJsonResponse = getResponseJSONString(EndecaConstants.SUBCATEGORY_RESPONSE_FILENAME);
				}

				// To convert JSON Response to bean
				familyListResponseBean = endecaServiceHelper.convertFamilyListJSONTOBean(StringEscapeUtils.unescapeHtml(familyListJsonResponse));
			}
		} catch (JsonGenerationException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_021_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.SUBCATEGORY_STRING + EndecaConstants.HYPHEN + e);
		} catch (IOException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_018_STRING + EndecaConstants.HYPHEN + EndecaConstants.SUBCATEGORY_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.CONNECTIVITY_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} catch (Exception e) {
			LOG.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SUBCATEGORY_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} finally {
			try (ResourceResolver resourceResolver = adminService.getReadService()) {
				TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
				// In case there is any exception the bean would be returned with fail status
				if (familyListResponseBean == null || familyListResponseBean.getResponse() == null) {
					familyListResponseBean = new FamilyListResponseBean();
					FamilyResponseBean response = new FamilyResponseBean();
					response.setStatus(EndecaConstants.FAIL_STRING);
					familyListResponseBean.setResponse(response);
				}

				if (familyListResponseBean != null && null != familyListResponseBean.getResponse().getFacets()) {
					for (FacetGroupBean facetGroup : familyListResponseBean.getResponse().getFacets().getFacetGroupList()) {
						//Removed PageType Filter Facets for from Facet Tagging as it is not part of TAGs in the Page
						if((!facetGroup.getFacetGroupId().equalsIgnoreCase(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL))&& (!facetGroup.getFacetGroupId().equalsIgnoreCase(EndecaConstants.EATON_PAGE_TYPE_FILTER))){
							final EndecaFacetTag endecaFacetTag = tagManager.resolve(EndecaFacetTag.convertId(facetGroup.getFacetGroupId())).adaptTo(EndecaFacetTag.class);

							if (endecaFacetTag != null) {
								facetGroup.setFacetGroupLabel(endecaFacetTag.getLocalizedFacetLabel(endecaServiceRequestBean.getLocale()));
							}
						}

					}
				}
				LOG.debug("Response of getProductFamilyList:" + familyListResponseBean.getResponse());
			}

		}
		LOG.info("Exit  from getProductFamilyList method");
		return familyListResponseBean;
	}

	/**
	 * @param endecaServiceRequestBean The request to send to Endeca in order to retrieve a list of SKU's.
	 * @param resource The resource to use as a basis for retrieve a Endeca cloud config.
	 * @return The sku list with facet groups names converted into AEM appropriate names based upon the Endeca cloud config.
	 */
	public SKUListResponseBean getSKUList(EndecaServiceRequestBean endecaServiceRequestBean, Resource resource) {
		SKUListResponseBean skuListResponse = getSKUList(endecaServiceRequestBean);

		if (null != cloudConfigService && null != skuListResponse) {
			final java.util.Optional<EndecaConfigModel> endecaCloudConfig = cloudConfigService.getEndecaCloudConfig(resource);
			// This will convert the name of the facet group to the value configured in the Endeca config model if an entry exists.
			if (null != skuListResponse.getFamilyModuleResponse()
					&& null != skuListResponse.getFamilyModuleResponse().getFacets()
					&& null != skuListResponse.getFamilyModuleResponse().getFacets().getFacetGroupList()) {
				skuListResponse.getFamilyModuleResponse().getFacets().getFacetGroupList().stream()
						.map(facetGroupBean -> new ImmutablePair<>(endecaCloudConfig.get().getAemAttributeName(facetGroupBean.getFacetGroupId()), facetGroupBean))
						.filter(pair -> pair.getKey().isPresent())
						.forEach(pair -> pair.getValue().setFacetGroupId(pair.getKey().get()));
			}
		}

		return skuListResponse;
	}

	/**
	 * This method will fetch the SKU list based on AEM request
	 *
	 * @param endecaServiceRequestBean
	 * @return SKU list
	 */
	@Override
	public SKUListResponseBean getSKUList(EndecaServiceRequestBean endecaServiceRequestBean) {
		LOG.info("Entry into getSKUList method ");
		String skuListJsonRequest = null;
		String skuListJsonResponse = null;
		SKUListResponseBean skuListResponse = null;

		try {
			endecaServiceHelper = new EndecaServiceHelper();
			// to get the endeca config details
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
			Optional<String> cachedResponse = Optional.ofNullable(espServiceCache.get(endecaServiceRequestBean));

			// TO fetch the JSON request
			if (endecaServiceRequestBean != null) {
				skuListJsonRequest = endecaServiceHelper.convertBeanTOJSON(endecaServiceRequestBean);
				LOG.info("sku List JsonRequest" + skuListJsonRequest);

			}

			//to fetch the SKU card Parameters from request
			JsonArray filtervalue=  endecaServiceHelper.getSKUCardParameters(skuListJsonRequest);

			//to fetch Language details for constructing Runtime Graph URL
			String languageForRunTimeGraphURL = endecaServiceHelper.getLanguageForRuntimeGraph(skuListJsonRequest);

			//To fetch the JSON response
			if(!endecaConfigBean.getStubResEnabled()){
				//Response from ESP Service with HTTP
				if (cachedResponse.isPresent()) {
					skuListResponse = endecaServiceHelper.convertSKUListJSONTOBean(StringEscapeUtils.unescapeHtml(cachedResponse.get()), filtervalue , languageForRunTimeGraphURL);
				} else {
					LOG.warn("No Endeca response was found");
				}
			}else if(endecaConfigBean.getStubResEnabled()){

				if(endecaConfigBean.getStubResponsePath() != null){
					// Response File from DAM
					String filepath = endecaConfigBean.getStubResponsePath()+EndecaConstants.PRODUCTFAMILY_RESPONSE_FILENAME;
					LOG.debug("Stub Response File Path::"+filepath);
					skuListJsonResponse = CommonUtil.getResponseStringFromDAM(adminService,filepath);
					LOG.debug("Stub Response::"+skuListJsonResponse);
					if (null != skuListJsonResponse) {
						skuListResponse = endecaServiceHelper.convertSKUListJSONTOBean(StringEscapeUtils.unescapeHtml(skuListJsonResponse),filtervalue,languageForRunTimeGraphURL);
					}
				}else {
					skuListJsonResponse = getResponseJSONString(EndecaConstants.PRODUCTFAMILY_RESPONSE_FILENAME);
					if (null != skuListJsonResponse) {
						skuListResponse = endecaServiceHelper.convertSKUListJSONTOBean(StringEscapeUtils.unescapeHtml(skuListJsonResponse),filtervalue,languageForRunTimeGraphURL);
					}
				}
			}
		} catch (JsonGenerationException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_020_STRING + EndecaConstants.HYPHEN + EndecaConstants.FAMILY_STRING
					+ EndecaConstants.HYPHEN + e);
		} catch (IOException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_017_STRING + EndecaConstants.HYPHEN + EndecaConstants.FAMILY_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.CONNECTIVITY_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} catch (Exception e) {
			LOG.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.FAMILY_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} finally {
			// In case there is any exception the bean would be returned with fail status
			if (skuListResponse == null || skuListResponse.getFamilyModuleResponse() == null) {
				skuListResponse = new SKUListResponseBean();
				FamilyModuleResponseBean response = new FamilyModuleResponseBean();
				response.setStatus(EndecaConstants.FAIL_STRING);
				skuListResponse.setFamilyModuleResponse(response);
			}
			LOG.debug("Response of getSKUList:" + skuListResponse.getFamilyModuleResponse());
		}
		LOG.info("Exit from getSKUList method ");
		return skuListResponse;
	}
	/**
	 * This method will fetch the SKU details based for each SKU Catalog number
	 *
	 * @param endecaServiceRequestBean
	 * @return SKU details
	 */
	@Override
	public SKUDetailsResponseBean getSKUDetails(EndecaServiceRequestBean endecaServiceRequestBean) {
		LOG.info("Entry into getSKUDetails method ");
		String skuDetailsJsonResponse = null;
		SKUDetailsResponseBean skuDetailsResponse = null;

		try {
			endecaServiceHelper = new EndecaServiceHelper();

			// to get the endeca config details
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
			Optional<String> cachedResponse = Optional.ofNullable(espServiceCache.get(endecaServiceRequestBean));

			// To fetch the JSON response
			if(!endecaConfigBean.getStubResEnabled()){
				if (cachedResponse.isPresent()) {
					skuDetailsJsonResponse = cachedResponse.get();
				} else {
					LOG.warn("No Endeca response was found");
				}
			} else if(endecaConfigBean.getStubResEnabled()){
				if(endecaConfigBean.getStubResponsePath() != null){
					// Response File from DAM
					String filepath = endecaConfigBean.getStubResponsePath()+EndecaConstants.SKU_RESPONSE_FILENAME;
					LOG.debug("Stub Response File Path::"+filepath);
					skuDetailsJsonResponse = CommonUtil.getResponseStringFromDAM(adminService,filepath);
					LOG.debug("Stub Response::"+skuDetailsJsonResponse);
				}else{
					skuDetailsJsonResponse= getResponseJSONString(EndecaConstants.SKU_RESPONSE_FILENAME);
				}
			}

			// To convert JSON to bean
			skuDetailsResponse = endecaServiceHelper.convertSKUDetailsJSONTOBean(skuDetailsJsonResponse);

		} catch (JsonGenerationException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_019_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING
					+ EndecaConstants.HYPHEN + e);
		} catch (IOException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_016_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.CONNECTIVITY_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} catch (Exception e) {
			LOG.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} finally {
			// In case there is any exception the bean would be returned with
			// fail status
			if (skuDetailsResponse == null || skuDetailsResponse.getSkuResponse() ==  null) {
				skuDetailsResponse = new SKUDetailsResponseBean();
				SKUResponseBean response = new SKUResponseBean();
				response.setStatus(EndecaConstants.FAIL_STRING);
				skuDetailsResponse.setSkuResponse(response);
			}
			//To print the SKU response in logs
			SKUDetailsResponseBean skuResponseforLogs = getResponseforLogs(skuDetailsResponse);
			LOG.debug("Response of getSKUDetails After parsing:" + skuResponseforLogs.getSkuResponse());
		}
		LOG.info("Exit from getSKUDetails method");
		return skuDetailsResponse;
	}

	/**
	 * This method will return the search results on type ahead
	 *
	 * @param endecaServiceRequestBean
	 * @return List of Strings
	 */
	@Override
	public ProductCompatibilityListResponseBean getProductCompatibilitySkuList(EndecaServiceRequestBean endecaServiceRequestBean,List<String> compatibilityExcelTable) {
		LOG.info("Entry into getSKUDetails method ");
		String skuDetailsJsonResponse = null;
		ProductCompatibilityListResponseBean skuDetailsResponse = new ProductCompatibilityListResponseBean();
		try {
			endecaServiceHelper = new EndecaServiceHelper();

			// to get the endeca config details
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
			Optional<String> cachedResponse = Optional.ofNullable(espServiceCache.get(endecaServiceRequestBean));
			// To fetch the JSON response
			if(!endecaConfigBean.getStubResEnabled()){
				if (cachedResponse.isPresent()) {
					skuDetailsJsonResponse = cachedResponse.get();
				} else {
					LOG.warn("No Endeca response was found");
				}
			} else if(endecaConfigBean.getStubResEnabled()){
				if(endecaConfigBean.getStubResponsePath() != null){
					// Response File from DAM
					String filepath = endecaConfigBean.getStubResponsePath()+EndecaConstants.SKU_RESPONSE_FILENAME;
					LOG.debug("Stub Response File Path::"+filepath);
					skuDetailsJsonResponse = CommonUtil.getResponseStringFromDAM(adminService,filepath);
					LOG.debug("Stub Response::"+skuDetailsJsonResponse);
				}else{
					skuDetailsJsonResponse= getResponseJSONString(EndecaConstants.SKU_RESPONSE_FILENAME);
				}
			}
			// To convert JSON to bean
			skuDetailsResponse = endecaServiceHelper.convertProductCompatibilityDetailsJSONTOBeanList(skuDetailsJsonResponse,compatibilityExcelTable);
		} catch (JsonGenerationException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_019_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING
					+ EndecaConstants.HYPHEN + e);
		} catch (IOException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_016_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.CONNECTIVITY_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} catch (Exception e) {
			LOG.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} finally {
			// In case there is any exception the bean would be returned with
			// fail status
			if (skuDetailsResponse == null || skuDetailsResponse.getFamilyModuleResponse() ==  null) {
				skuDetailsResponse = new ProductCompatibilityListResponseBean();
				ProductCompatibilityResponseBean response = new ProductCompatibilityResponseBean();
				response.setStatus(EndecaConstants.FAIL_STRING);
				skuDetailsResponse.setFamilyModuleResponse(response);
			}

			LOG.debug("Response of getSKUList:" + skuDetailsResponse.getFamilyModuleResponse());

		}
		LOG.info("Exit from getSKUDetails method");
		return skuDetailsResponse;
	}


	/**
	 * This method will return the search results on type ahead
	 *
	 * @param endecaServiceRequestBean
	 * @return List of Strings
	 */
	@Override
	public SKUDetailsResponseBean getProductCompareSKUList(EndecaServiceRequestBean endecaServiceRequestBean) {
		LOG.info("Entry into getSKUDetails method ");
		String skuDetailsJsonResponse = null;
		SKUDetailsResponseBean skuDetailsResponse = new SKUDetailsResponseBean();
		try {
			endecaServiceHelper = new EndecaServiceHelper();

			// to get the endeca config details
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
			Optional<String> cachedResponse = Optional.ofNullable(espServiceCache.get(endecaServiceRequestBean));
			// To fetch the JSON response
			if(!endecaConfigBean.getStubResEnabled()){
				if (cachedResponse.isPresent()) {
					skuDetailsJsonResponse = cachedResponse.get();
				} else {
					LOG.warn("No Endeca response was found");
				}
			} else if(endecaConfigBean.getStubResEnabled()){
				if(endecaConfigBean.getStubResponsePath() != null){
					// Response File from DAM
					String filepath = endecaConfigBean.getStubResponsePath()+EndecaConstants.SKU_RESPONSE_FILENAME;
					LOG.debug("Stub Response File Path::"+filepath);
					skuDetailsJsonResponse = CommonUtil.getResponseStringFromDAM(adminService,filepath);
					LOG.debug("Stub Response::"+skuDetailsJsonResponse);
				}else{
					skuDetailsJsonResponse= getResponseJSONString(EndecaConstants.SKU_RESPONSE_FILENAME);
				}
			}
			// To convert JSON to bean
			skuDetailsResponse = endecaServiceHelper.convertSKUDetailsJSONTOBeanList(skuDetailsJsonResponse);
		} catch (JsonGenerationException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_019_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING
					+ EndecaConstants.HYPHEN + e);
		} catch (IOException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_016_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.CONNECTIVITY_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} catch (Exception e) {
			LOG.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} finally {
			// In case there is any exception the bean would be returned with
			// fail status
			if (skuDetailsResponse == null || skuDetailsResponse.getSkuResponse() ==  null) {
				skuDetailsResponse = new SKUDetailsResponseBean();
				SKUResponseBean response = new SKUResponseBean();
				response.setStatus(EndecaConstants.FAIL_STRING);
				skuDetailsResponse.setSkuResponse(response);
			}
			//To print the SKU response in logs
			SKUDetailsResponseBean skuResponseforLogs = getResponseforLogs(skuDetailsResponse);
			LOG.debug("Response of getSKUDetails After parsing:" + skuResponseforLogs.getSkuResponse());
		}
		LOG.info("Exit from getSKUDetails method");
		return skuDetailsResponse;
	}

	/**
	 * This method will return the search results on type ahead
	 *
	 * @param endecaServiceRequestBean
	 * @return List of Strings
	 */
	@Override
	public TypeAheadSiteSearchResponse getSearchKeywords(EndecaServiceRequestBean endecaServiceRequestBean) {

		LOG.info("Entry into getSearchKeywords method ");
		String typeAheadJsonResponse = null;
		TypeAheadSiteSearchResponse typeAheadSearchResponse = null;

		try {
			endecaServiceHelper = new EndecaServiceHelper();

			// to get the endeca config details
			endecaConfigBean = endecaConfigService.getConfigServiceBean();

			// To fetch the JSON response
			if(!endecaConfigBean.getStubResEnabled()){
				typeAheadJsonResponse = espServiceCache.get(endecaServiceRequestBean);
			}else if(endecaConfigBean.getStubResEnabled()) {

				if(endecaConfigBean.getStubResponsePath() != null){
					// Response File from DAM
					String filePath = endecaConfigBean.getStubResponsePath() + EndecaConstants.TYPE_AHEAD_RESPONSE_FILENAME;
					LOG.debug("Stub Response File Path::"+filePath);
					typeAheadJsonResponse = CommonUtil.getResponseStringFromDAM(adminService, filePath);
					LOG.debug("Stub Response::"+typeAheadJsonResponse);
				}else{
					typeAheadJsonResponse = getResponseJSONString(EndecaConstants.TYPE_AHEAD_RESPONSE_FILENAME);
				}


			}
			// To convert JSON to bean
			typeAheadSearchResponse = endecaServiceHelper.convertSearchKeywordsJSONTOBean(typeAheadJsonResponse, endecaServiceRequestBean);

			if (typeAheadSearchResponse != null) {
				LOG.debug("Response of TypeAhead:" + typeAheadSearchResponse.getTypeAheadResponse());
			}

		} catch (JsonGenerationException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_031_STRING + EndecaConstants.HYPHEN + EndecaConstants.TYPE_AHEAD_STRING
					+ EndecaConstants.HYPHEN + e);
		} catch (IOException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_029_STRING + EndecaConstants.HYPHEN + EndecaConstants.TYPE_AHEAD_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.CONNECTIVITY_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} catch (Exception e) {
			LOG.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.TYPE_AHEAD_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} finally {
			// In case there is any exception the bean would be returned with fail status
			if (typeAheadSearchResponse == null) {
				typeAheadSearchResponse = new TypeAheadSiteSearchResponse();
				TypeAheadResponse typeAheadSearch = new TypeAheadResponse();
				typeAheadSearch.setStatus(EndecaConstants.FAIL_STRING);
				typeAheadSearchResponse.setTypeAheadResponse(typeAheadSearch);
			}
		}
		LOG.info("Exit from getSKUDetails method ");
		return typeAheadSearchResponse;
	}

	/**
	 * This method will return the search results on site search
	 *
	 * @param endecaServiceRequestBean
	 * @return Site Search results
	 */
	@Override
	public SiteSearchResponse getSearchResults(
			EndecaServiceRequestBean endecaServiceRequestBean,
			final SlingHttpServletRequest slingRequest,
			boolean isUnitedStatesDateFormat) {
		LOG.debug("Entry into getSearchKeywords method ");
		SiteSearchResponse siteSearchResponse = null;

		try (ResourceResolver resourceResolver = adminService.getReadService()) {
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			endecaServiceHelper = new EndecaServiceHelper();
			// to get the endeca config details
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
			String siteSearchJsonResponse = getSearchResultJson(endecaServiceRequestBean);

			// To convert JSON to bean
			siteSearchResponse = endecaServiceHelper.convertSiteSearchResultsJSONTOBean(siteSearchJsonResponse, resourceResolver, slingRequest, isUnitedStatesDateFormat);
			if(null != siteSearchResponse.getPageResponse().getFacets().getFacetGroupList()){
				for (FacetGroupBean facetGroup : siteSearchResponse.getPageResponse().getFacets().getFacetGroupList()) {
					String facet = facetGroup.getFacetGroupId();
					if(!facet.equals(CommonConstants.TAG_NAMESPACE_EATON
							.concat(CommonConstants.TAG_NAMESPACE_SEPARATOR)
							.concat(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY))){
						facet = EndecaFacetTag.convertId(facet);
					}
					final Tag tag = tagManager.resolve(facet);
					String label = StringUtils.EMPTY;

					if (tag != null) {
						final EndecaFacetTag endecaFacetTag = tag.adaptTo(EndecaFacetTag.class);

						if (endecaFacetTag != null) {
							label = endecaFacetTag.getLocalizedFacetLabel(endecaServiceRequestBean.getLocale());
						}
					}

					if (StringUtils.isBlank(label)) {
						label = CommonUtil.getI18NFromLocale(slingRequest, EndecaConstants.I18N_LABEL_PREFIX + facetGroup.getFacetGroupLabel(), endecaServiceRequestBean.getLocale());
					}

					facetGroup.setFacetGroupLabel(label);
				}

				//Call method to remove the duplicate facets.
				getCallToRemoveDuplicateFacetsValue(siteSearchResponse.getPageResponse().getFacets().getFacetGroupList(),siteSearchResponse,null,null);
			}

			if (siteSearchResponse != null) {
				siteSearchResponse.getPageResponse().setNewsId(endecaConfigBean.getNewsTabId());
				siteSearchResponse.getPageResponse().setProductsId(endecaConfigBean.getProductsTabId());
				siteSearchResponse.getPageResponse().setResourcesId(endecaConfigBean.getResourcesTabId());
				siteSearchResponse.getPageResponse().setServicesId(endecaConfigBean.getServicesTabId());

				LOG.debug("Response of SiteSearch: {}", siteSearchResponse.getPageResponse());
			}

		} catch (JsonGenerationException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_032_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.SITE_SEARCH_STRING + EndecaConstants.HYPHEN + e);
		} catch (IOException e) {
			LOG.error(
					EndecaConstants.ERR_ENDECA_030_STRING + EndecaConstants.HYPHEN + EndecaConstants.SITE_SEARCH_STRING+EndecaConstants.HYPHEN
							+ EndecaConstants.CONNECTIVITY_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} catch (Exception e) {
			LOG.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.TYPE_AHEAD_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} finally {
			// In case there is any exception the bean would be returned with
			// fail status
			if (siteSearchResponse == null) {
				siteSearchResponse = new SiteSearchResponse();
				SiteSearchPageResponse pageResponse = new SiteSearchPageResponse();
				pageResponse.setStatus(EndecaConstants.FAIL_STRING);
				siteSearchResponse.setPageResponse(pageResponse);
			}
		}
		LOG.info("Exit from getSKUDetails method ");
		return siteSearchResponse;
	}
	@Override
	public String getSearchResultJson(EndecaServiceRequestBean endecaServiceRequestBean) throws IOException {
		String siteSearchJsonResponse = null;
		Optional<String> cachedResponse = Optional.ofNullable(espServiceCache.get(endecaServiceRequestBean));
		// To fetch the JSON response
		if(!endecaConfigBean.getStubResEnabled()){
			//Response from ESP Service with HTTP
			if (cachedResponse.isPresent()) {
				siteSearchJsonResponse = cachedResponse.get();
			} else {
				LOG.warn("No Endeca response was found");
			}
		}else if (endecaConfigBean.getStubResEnabled()) {
			if(endecaConfigBean.getStubResponsePath() != null){
				// Response File from DAM
				String filePath = endecaConfigBean.getStubResponsePath()
						+ EndecaConstants.SITE_SEARCH_RESPONSE_FILENAME;
				LOG.debug("Stub Response File Path: {}", filePath);
				siteSearchJsonResponse = CommonUtil.getResponseStringFromDAM(adminService, filePath);
				LOG.debug("Stub Response: {}", siteSearchJsonResponse);

			}else{
				siteSearchJsonResponse = getResponseJSONString(EndecaConstants.SITE_SEARCH_RESPONSE_FILENAME);
			}
		}
		return siteSearchJsonResponse;
	}

	/**
	 * This method is to Remove the duplicate facets while site search.
	 *
	 * @param siteSearchResponse
	 * @param pageResponse*/


	/**
	 * This method is to invoke the ESP service via SOAP HTTP.
	 *
	 * @param endecaServiceRequestBean
	 * @return JSON String response
	 */
	private String callESPService(EndecaServiceRequestBean endecaServiceRequestBean) {
		final String endecaAppName = endecaServiceRequestBean.getSearchApplication();
		List<String> filterList = new ArrayList<>();
		List<String> facetsGroup = new ArrayList<>();
		List<String> productTaxonomyAllFacets = new ArrayList<>();
		List<String> originalFacetsGroup = new ArrayList<>();
		List<List<String>> fixedFilterList = new ArrayList<>();
		String strResponse = null;
		try (ResourceResolver resourceResolver = adminService.getReadService()) {
			if (endecaServiceRequestBean != null) {
				List<FilterBean> filters = endecaServiceRequestBean.getFilters();
				//This applies only to Advance Search Component
				if(endecaServiceRequestBean.getSearchApplication().equals(EndecaConstants.SECURE_ASSET_EATON_SEARCH)) {
					fixedFilterList = getValidFacetsGroup(endecaServiceRequestBean, endecaAppName, resourceResolver);
					if (fixedFilterList.size() > 1) {
						filterList = fixedFilterList.get(0);
						facetsGroup = fixedFilterList.get(1);
						productTaxonomyAllFacets = fixedFilterList.get(2);
						originalFacetsGroup = fixedFilterList.get(3);
					}
					filters = getMergedFilters(filters, facetsGroup, filterList, productTaxonomyAllFacets);
					endecaServiceRequestBean.setFilters(filters);
				}

				String request = endecaServiceHelper.convertBeanTOJSON(endecaServiceRequestBean);
				// Create a StringEntity for the SOAP XML.
				final String body = EndecaConstants.SOAP_ENVELOPE_PART_START
						.concat(EndecaConstants.SOAP_BODY_PART_START)
						.concat(request)
						.concat(EndecaConstants.SOAP_BODY_PART_END)
						.concat(EndecaConstants.SOAP_ENVELOPE_PART_END);
				LOG.debug("SOAP BODY for ESP service: {}", body);
				strResponse = getStringResponse(endecaAppName, body);
				if(!facetsGroup.equals(originalFacetsGroup)){
					strResponse = getOriginalFacetGroup(strResponse, facetsGroup, originalFacetsGroup);
				}
			}
		} catch (IOException e) {
			LOG.error("Error while trying to get response: {}", e.getMessage());
		}
		return strResponse;
	}

	private String getOriginalFacetGroup(String strResponse, List<String> facetsGroup, List<String> originalFacetsGroup){
		for(String facet:facetsGroup){
			if(facet.equals(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY)){
				for(String originalFacet:originalFacetsGroup){
					if(originalFacet.contains(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY)){
						strResponse = strResponse.replace(facet, originalFacet);
					}
				}
			}
		}
		return strResponse;
	}

	private List<FilterBean> getMergedFilters(
			List<FilterBean> filters,
			List<String> facetsGroup,
			List<String> filterList,
			List<String> productTaxonomyAllFacets){
		for(int i=0;i<filters.size();i++){
			switch (filters.get(i).getFilterName()){
				case EndecaConstants.RETURN_FACETS_FOR:{
					if(facetsGroup.size()==0){
						facetsGroup=filters.get(i).getFilterValues();
					}
					filters.get(i).setFilterValues(facetsGroup);
					break;
				}
				case EndecaConstants.FACETS_STRING_WITH_F:{
					if(filterList.size()>0) {
						List<String> requestedFilters = filters.get(i).getFilterValues();
						//It validates if some of the requested facets in filters is a product taxonomy one
						//if so, it won't add the sub level selected from the component author side
						if(!requestedFilterIsInProductTaxonomy(productTaxonomyAllFacets, requestedFilters)) {
							for (String f : filterList) {
								if (!requestedFilters.contains(f)) {
									requestedFilters.add(f);
								}
							}
						}
						filters.get(i).setFilterValues(requestedFilters);
					}
					break;
				}
				default:{}
			}
		}
		return filters;
	}

	private boolean requestedFilterIsInProductTaxonomy(List<String> productTaxonomyAllFacets, List<String> requestedFilters){
		for(String requested:requestedFilters){
			for(String index:productTaxonomyAllFacets){
				if(requested.equals(index)){
					return true;
				}
			}
		}
		return false;
	}

	private List<List<String>> getValidFacetsGroup(
			EndecaServiceRequestBean endecaServiceRequestBean,
			String endecaAppName,
			ResourceResolver resourceResolver){
		List<List<String>> fixedFilterList = new ArrayList<>();
		List<String> facetsGroup=getFacetsGroup(endecaServiceRequestBean);
		List<String> originalFacetsGroup=facetsGroup;
		List<String> filterList = new ArrayList<>();
		fixedFilterList.add(filterList);
		fixedFilterList.add(facetsGroup);
		if(hasProductTaxonomySubLevels(facetsGroup)) {
			fixedFilterList = getFixedFilterList(endecaServiceRequestBean, endecaAppName, resourceResolver);
		}else{
			//Adding an empty list so that the ProductTaxonomy filters list spot is there
			fixedFilterList.add(filterList);
		}
		fixedFilterList.add(originalFacetsGroup);
		return fixedFilterList;
	}

	private List<String> getFacetsGroup(EndecaServiceRequestBean endecaServiceRequestBean){
		List<String> facets = new ArrayList<>();
		for(FilterBean filter:endecaServiceRequestBean.getFilters()){
			if(filter.getFilterName().equals(EndecaConstants.RETURN_FACETS_FOR)){
				for(String f:filter.getFilterValues()){
					if(!facets.contains(f)){
						facets.add(f);
					}else{
						facets.add(f);
					}
				}
			}
		}
		return facets;
	}

	private boolean hasProductTaxonomySubLevels(List<String> facets){
		for(String facet:facets){
			if (facet.contains(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY) && !facet.equals(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is to validate the list of tags that exist in Endeca
	 *
	 * @param endecaServiceRequestBean
	 * @param endecaAppName
	 * @param resourceResolver
	 * @return List<String> response
	 * @throws IOException
	 */
	private List<List<String>> getFixedFilterList(
			EndecaServiceRequestBean endecaServiceRequestBean,
			String endecaAppName,
			ResourceResolver resourceResolver){
		List<List<String>> fixedList = new ArrayList<>();
		List<String> requestedFacetsGroup = getFacetsGroup(endecaServiceRequestBean);
		EndecaServiceRequestBean shortBean = getShortBean(endecaServiceRequestBean);
		try {
			String initialRequest = endecaServiceHelper.convertBeanTOJSON(shortBean);
			final String initialBody = EndecaConstants.SOAP_ENVELOPE_PART_START
					.concat(EndecaConstants.SOAP_BODY_PART_START)
					.concat(initialRequest)
					.concat(EndecaConstants.SOAP_BODY_PART_END)
					.concat(EndecaConstants.SOAP_ENVELOPE_PART_END);
			String siteSearchJsonResponse = getStringResponse(endecaAppName, initialBody);
			SiteSearchResponse siteSearchResponse = endecaServiceHelper.convertSiteSearchResultsJSONTOBean(
					siteSearchJsonResponse,
					resourceResolver,
					slingRequest,
					true);
			if(null != siteSearchResponse.getPageResponse().getFacets().getFacetGroupList()){
				fixedList = getOptimizedFixedList(requestedFacetsGroup, siteSearchResponse);
			}
		} catch (IOException e) {
			LOG.error("Error while trying to convert bean to json: {}", e.getMessage());
		}
		return fixedList;
	}

	private List<List<String>> getOptimizedFixedList(List<String> requestedFacetsGroup, SiteSearchResponse siteSearchResponse){
		List<String> validatedFacetGroups = new ArrayList<>();
		List<String> filterList = new ArrayList<>();
		List<String> productTaxonomyAllFacets = new ArrayList<>();
		List<List<String>> fixedList = new ArrayList<>();
		//Iterating with the initial facet group list
		for(String requestedFacet:requestedFacetsGroup) {
			if (!requestedFacet.equals(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL)) {
				String mainFacet = requestedFacet;
				String lastLevelRequestFacet = CommonUtil.getLastLevel(requestedFacet);
				boolean hasResponseData = false;
				if(lastLevelRequestFacet.equals(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY)) {
					hasResponseData=true;
				}else{
					//Iterating with the response facet group list to validate
					for (FacetGroupBean responseFacetGroup : siteSearchResponse.getPageResponse().getFacets().getFacetGroupList()) {
						String responseFacet = responseFacetGroup.getFacetGroupLabel();
						if (mainFacet.contains(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY)) {
							mainFacet = CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY;
							if (mainFacet.equals(responseFacet)) {
								//This section is to only add sub-levels to the filterList when valid
								if (responseFacet.equals(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY)) {
									for (FacetValueBean responseFacetValue : responseFacetGroup.getFacetValueList()) {
										productTaxonomyAllFacets.add(responseFacetValue.getFacetValueId());
										String responseLevel = responseFacetValue.getFacetValueLabel();
										if (responseLevel.equals(lastLevelRequestFacet) && !hasResponseData) {
											filterList.add(responseFacetValue.getFacetValueId());
											hasResponseData = true;
										}
									}
									break;
								} else {
									hasResponseData = true;
								}
							}
						}else{
							hasResponseData=true;
						}

					}
				}
				if (hasResponseData) {
					validatedFacetGroups.add(mainFacet);
				}
			}else{
				validatedFacetGroups.add(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL);
			}
		}
		fixedList.add(filterList);
		fixedList.add(validatedFacetGroups);
		fixedList.add(productTaxonomyAllFacets);
		return fixedList;
	}

	/**
	 * This method is to get the response from Endeca
	 *
	 * @param endecaAppName
	 * @param body
	 * @return String
	 */
	String getStringResponse(String endecaAppName, String body){
		String strResponse = null;
		HttpPost httpPost = null;
		final StringEntity stringEntity = new StringEntity(body, "UTF-8");
		stringEntity.setChunked(true);
		final PoolingHttpClientConnectionManager connectionManager = getMultiThreadedConf();
		final HttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
		// Request parameters and other properties.
		if(StringUtils.equals(endecaAppName, EndecaConstants.TORQUE_APPLICATION_VALUE) ||
				StringUtils.equals(endecaAppName, EndecaConstants.CLUTCH_APPLICATION_VALUE)) {
			httpPost = new HttpPost(endecaConfigBean.getVgSelectorEndecaURL());
		} else if(StringUtils.equals(endecaAppName, "eatonpdh1") ||
				StringUtils.equals(endecaAppName, "eatonpdh2") || StringUtils.equals(endecaAppName, "eatonpdhcomparision")){
			httpPost = new HttpPost(endecaConfigBean.getEndecaPDH1PDH2ServieURL());
		} else {
			httpPost = new HttpPost(endecaConfigBean.getEspServiceURL());
		}
		httpPost.setEntity(stringEntity);
		httpPost.addHeader("Accept", "text/xml; charset=UTF-8");
		httpPost.addHeader("SOAPAction", "");

		try{
			final HttpResponse response = httpClient.execute(httpPost);
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				strResponse = EntityUtils.toString(entity);
				if(LOG.isDebugEnabled()) {
					LOG.debug("Response from Endeca:" + strResponse);
				}
				int start = strResponse.indexOf("<return>");
				int end = strResponse.lastIndexOf("</return>");
				if(start > 0 && end > 0) {
					strResponse = strResponse.substring(start + 8, end);
				}
			}
		} catch (IOException e) {
			// We are logging the error here so that we can report the error in application code instead of
			// it getting swallowed by the espServiceCache.
			// We also do not want the LoadingCache#load method to throw errors.
			// A null Endeca response is okay to produce from this method as the other methods
			// of this service handle the null scenario.
			LOG.error(e.getMessage());
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return strResponse;
	}

	/**
	 * This method is to get a short version of the original bean so that is more efficient for the initial request
	 *
	 * @param fullBean
	 * @return EndecaServiceRequestBean
	 */
	private EndecaServiceRequestBean getShortBean(EndecaServiceRequestBean fullBean){
		EndecaServiceRequestBean shortBean = new EndecaServiceRequestBean();
		shortBean.setSearchApplication(fullBean.getSearchApplication());
		shortBean.setSearchApplicationKey(fullBean.getSearchApplicationKey());
		shortBean.setFunction(fullBean.getFunction());
		shortBean.setSearchTerms(fullBean.getSearchTerms());
		shortBean.setLanguage(fullBean.getLanguage());
		shortBean.setStartingRecordNumber(fullBean.getStartingRecordNumber());
		shortBean.setNumberOfRecordsToReturn("1");
		List<FilterBean> filters = fullBean.getFilters();
		List<FilterBean> minimalFilters = new ArrayList<>();
		for(FilterBean f:filters){
			if(!f.getFilterName().equals(EndecaConstants.FILE_TYPES)){
				if(f.getFilterName().equals(EndecaConstants.RETURN_FACETS_FOR)){
					List<String> values = f.getFilterValues();
					for(int i=0;i<values.size();i++){
						if(values.get(i).contains(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY)){
							values.set(i, CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY);
							break;
						}
					}
					f.setFilterValues(values);
				}
				minimalFilters.add(f);
			}
		}
		shortBean.setFilters(minimalFilters);
		return shortBean;
	}

	/**
	 * To print the SKU details response in the logs in readble format
	 * @param skuResponseforLogs
	 * @return
	 */
	private SKUDetailsResponseBean getResponseforLogs(SKUDetailsResponseBean skuResponseforLogs){

		LOG.info("Entry into getResponseforLogs method ");
		if(skuResponseforLogs != null && skuResponseforLogs.getSkuResponse()!=null && skuResponseforLogs.getSkuResponse().getSkuDetailsList()!=null && !skuResponseforLogs.getSkuResponse().getSkuDetailsList().isEmpty()){
			List<SKUDetailsBean> skuDetailsList = skuResponseforLogs.getSkuResponse().getSkuDetailsList();
			for(int i=0;i<skuDetailsList.size();i++) {
				skuDetailsList.get(i).setCid(CommonUtil.convertXMLString(skuDetailsList.get(i).getCid()));
				skuDetailsList.get(i).setCrossSell(CommonUtil.convertXMLString(skuDetailsList.get(i).getCrossSell()));
				skuDetailsList.get(i).setDocuments(CommonUtil.convertXMLString(skuDetailsList.get(i).getDocuments()));
				skuDetailsList.get(i).setUpSell(CommonUtil.convertXMLString(skuDetailsList.get(i).getUpSell()));
				skuDetailsList.get(i).setGlobalAttrs(CommonUtil.convertXMLString(skuDetailsList.get(i).getGlobalAttrs()));
				skuDetailsList.get(i).setReplacement(CommonUtil.convertXMLString(skuDetailsList.get(i).getReplacement()));
				skuDetailsList.get(i).setSkuImgs(CommonUtil.convertXMLString(skuDetailsList.get(i).getSkuImgs()));
				skuDetailsList.get(i).setTaxonomyAttrs(CommonUtil.convertXMLString(skuDetailsList.get(i).getTaxonomyAttrs()));

			}
			skuResponseforLogs.getSkuResponse().setSkuDetailsList(skuDetailsList);
		}
		LOG.info("Exit from getResponseforLogs method ");
		return skuResponseforLogs;
	}

	/**
	 * To fetch the tools and facets for the Clutch Selector
	 * @param endecaServiceRequestBean
	 * @return ClutchSelectorResponse
	 */
	@Override
	public ClutchSelectorResponse getClutchToolDetails(EndecaServiceRequestBean endecaServiceRequestBean) {
		LOG.info("Entry into getClutchToolDetails method ");
		String clutchJsonRequest = null;
		String clutchJsonResponse = null;
		ClutchSelectorResponse clutchResponse = null;

		JsonArray filtervalue= null;

		try {
			endecaServiceHelper = new EndecaServiceHelper();
			// to get the endeca config details
			endecaConfigBean = endecaConfigService.getConfigServiceBean();

			// TO fetch the JSON request
			if (endecaServiceRequestBean != null) {
				clutchJsonRequest = endecaServiceHelper.convertBeanTOJSON(endecaServiceRequestBean);

				//to fetch the SKU card Parameters from request
				filtervalue=  endecaServiceHelper.getSKUCardParameters(clutchJsonRequest);

				//To fetch the JSON response
				if(!endecaConfigBean.getStubResEnabled()){
					clutchJsonResponse = espServiceCache.get(endecaServiceRequestBean);
				}else if(endecaConfigBean.getStubResEnabled()){
					if(endecaConfigBean.getStubResponsePath() != null){
						// Response File from DAM
						String filepath = endecaConfigBean.getStubResponsePath()+EndecaConstants.CLUTCH_SELECTOR_RESPONSE_FILENAME;
						LOG.debug("Stub Response File Path::"+filepath);
						clutchJsonResponse = CommonUtil.getResponseStringFromDAM(adminService,filepath);
						LOG.debug("Stub Response::"+clutchJsonResponse);
					}else
						clutchJsonResponse = getResponseJSONString(EndecaConstants.CLUTCH_SELECTOR_RESPONSE_FILENAME);

				}

			}

			// To convert JSON to bean
			clutchResponse = endecaServiceHelper.convertClutchToolListJSONTOBean(StringEscapeUtils.unescapeHtml(clutchJsonResponse),filtervalue);
			if(clutchResponse != null) {
				getCallToRemoveDuplicateFacetsValue(clutchResponse.getClutchToolResponse().getFacets().getFacetGroupList(), null, clutchResponse, null);
				LOG.debug("Response of getClutchToolDetails:" + clutchResponse.getClutchToolResponse());
			}
		} catch (JsonGenerationException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_059_STRING + EndecaConstants.HYPHEN + EndecaConstants.CLUTCH_STRING
					+ EndecaConstants.HYPHEN + e);
		} catch (IOException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_057_STRING + EndecaConstants.HYPHEN + EndecaConstants.CLUTCH_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.CONNECTIVITY_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} catch (Exception e) {
			LOG.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.CLUTCH_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} finally {
			// In case there is any exception the bean would be returned with fail status
			if (clutchResponse == null || clutchResponse.getClutchToolResponse() == null) {
				clutchResponse = new ClutchSelectorResponse();
				ClutchToolResponse response = new ClutchToolResponse();
				response.setStatus(EndecaConstants.FAIL_STRING);
				clutchResponse.setClutchToolResponse(response);
			}
		}
		LOG.info("Exit from getClutchToolDetails method ");
		return clutchResponse;
	}

	/**
	 * To fetch the tools and facets for the Torque Selector
	 * @param endecaServiceRequestBean
	 * @return TorqueSelectorResponse
	 */
	@Override
	public TorqueSelectorResponse getTorqueToolDetails(EndecaServiceRequestBean endecaServiceRequestBean) {
		LOG.info("Entry into getTorqueToolDetails method ");
		String torqueJsonRequest = null;
		String torqueJsonResponse = null;
		TorqueSelectorResponse torqueSelectorResponse = null;

		JsonArray filtervalue= null;

		try {
			endecaServiceHelper = new EndecaServiceHelper();
			// to get the endeca config details
			endecaConfigBean = endecaConfigService.getConfigServiceBean();

			// TO fetch the JSON request
			if (endecaServiceRequestBean != null) {
				torqueJsonRequest = endecaServiceHelper.convertBeanTOJSON(endecaServiceRequestBean);

				//to fetch the SKU card Parameters from request
				filtervalue=  endecaServiceHelper.getSKUCardParameters(torqueJsonRequest);

				//To fetch the JSON response
				if(!endecaConfigBean.getStubResEnabled()){
					torqueJsonResponse = espServiceCache.get(endecaServiceRequestBean);
				}else if(endecaConfigBean.getStubResEnabled()){

					if(endecaConfigBean.getStubResponsePath() != null){
						// Response File from DAM
						String filepath = endecaConfigBean.getStubResponsePath()+EndecaConstants.TORQUE_SELECTOR_RESPONSE_FILENAME;
						LOG.debug("Stub Response File Path::"+filepath);
						torqueJsonResponse = CommonUtil.getResponseStringFromDAM(adminService,filepath);
						LOG.debug("Stub Response Request Path::"+torqueJsonResponse);
					}else
						torqueJsonResponse = getResponseJSONString(EndecaConstants.TORQUE_SELECTOR_RESPONSE_FILENAME);
				}

			}

			// To convert JSON to bean
			torqueSelectorResponse = endecaServiceHelper.convertTorqueToolListJSONTOBean(StringEscapeUtils.unescapeHtml(torqueJsonResponse),filtervalue);
			if (torqueSelectorResponse != null) {
				getCallToRemoveDuplicateFacetsValue(torqueSelectorResponse.getTorqueToolResponse().getFacets().getFacetGroupList(), null, null, torqueSelectorResponse);
				LOG.debug("Response of getTorqueToolDetails:" + torqueSelectorResponse.getTorqueToolResponse());
			}
		} catch (JsonGenerationException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_060_STRING + EndecaConstants.HYPHEN + EndecaConstants.TORQUE_STRING
					+ EndecaConstants.HYPHEN + e);
		} catch (IOException e) {
			LOG.error(EndecaConstants.ERR_ENDECA_058_STRING + EndecaConstants.HYPHEN + EndecaConstants.TORQUE_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.CONNECTIVITY_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} catch (Exception e) {
			LOG.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.TORQUE_STRING+EndecaConstants.HYPHEN
					+ EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN + e);
		} finally {
			// In case there is any exception the bean would be returned with fail status
			if (torqueSelectorResponse == null || torqueSelectorResponse.getTorqueToolResponse() == null) {
				torqueSelectorResponse = new TorqueSelectorResponse();
				TorqueToolResponse torqueToolResponse = new TorqueToolResponse();
				torqueToolResponse.setStatus(EndecaConstants.FAIL_STRING);
				torqueSelectorResponse.setTorqueToolResponse(torqueToolResponse);
			}
		}
		LOG.info("Exit from getTorqueToolDetails method ");
		return torqueSelectorResponse;
	}

	/**
	 * To read the file from the resources
	 * @param filename
	 * @return
	 */
	private String getResponseJSONString(String filename){
		String jsonResponse = null;
		try {
			if (filename != null && filename.startsWith("/")) {
				filename = filename.replace("/", "");
			}

			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(filename).getFile());
			jsonResponse = readFileAsString(file.getAbsolutePath());
		} catch (RuntimeException e) {
			LOG.error(String.format("getResponseJSONString operation failed with error message: %s", e.getMessage()));
		}
		return jsonResponse;
	}

	/**
	 * To read file as String
	 * @param filePath
	 * @return
	 */
	private String readFileAsString(String filePath){
		final int TMP_NUM = 1024;

		StringBuilder fileData = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new FileReader(filePath)))
		{
			char[] buf = new char[TMP_NUM];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
		}catch (IOException e) {
			LOG.error(String.format("readFileAsString operation failed with error message: %s",e.getMessage()));
		}
		return fileData.toString();
	}

	public JsonObject getSubmittalResponse(final EndecaServiceRequestBean endecaServiceRequestBean) throws Exception {
		LOG.info("Entry into getSubmittalResponse method");
		String submittalJsonRequest = null;
		String submittalJsonResponse = null;
		JsonObject submittalResponse = null;
		JsonObject groomedResponse = new JsonObject();
		endecaServiceHelper = new EndecaServiceHelper();
		endecaConfigBean = endecaConfigService.getConfigServiceBean();
		try {
			if (endecaServiceRequestBean != null) {
				submittalJsonRequest = endecaServiceHelper.convertBeanTOJSON(endecaServiceRequestBean);
			}
			if (!endecaConfigBean.getStubResEnabled()) {
				submittalJsonResponse = espServiceCache.get(endecaServiceRequestBean);
			} else {
				submittalJsonResponse = getResponseJSONString(EndecaConstants.SUBMITTAL_BUILDER_RESPONSE_FILENAME);
			}
			if (StringUtils.isNotBlank(submittalJsonResponse)) {
				submittalResponse =
						endecaServiceHelper.convertSubmitBuilderListJSON(StringEscapeUtils.unescapeHtml(submittalJsonResponse));
			}
			if (submittalResponse == null) {
				groomedResponse = new JsonObject();
				groomedResponse.add(EndecaConstants.STATUS, new Gson().toJsonTree(EndecaConstants.FAIL_STRING));
			} else {
				if (endecaServiceRequestBean != null) {
					final JsonArray groomedSubmittalFilters = getGroomedSubmittalFilters(submittalResponse,
							endecaServiceRequestBean.getLocale());
					groomedResponse.add(EndecaConstants.FILTERS_STRING, groomedSubmittalFilters);

					final JsonArray groomedSubmittalResults = getGroomedResponseResults(submittalResponse,
							endecaServiceRequestBean.getLocale());
					groomedResponse.add(EndecaConstants.RESULTS, groomedSubmittalResults);
				}
				groomedResponse.add(EndecaConstants.TOTAL_COUNT_STRING, new Gson().toJsonTree(getSubmittalTotalCount(submittalResponse)));
				groomedResponse.add(EndecaConstants.STATUS, new Gson().toJsonTree(EndecaConstants.SUCCESS_STRING));
			}
		} catch (IOException ioExc) {
			LOG.error("Error converting endecaServiceRequestBean to JSON.", ioExc);
			groomedResponse = new JsonObject();
			groomedResponse.add(EndecaConstants.STATUS, new Gson().toJsonTree(EndecaConstants.FAIL_STRING));
		} catch (Exception e) {
			groomedResponse = new JsonObject();
			groomedResponse.add(EndecaConstants.STATUS, new Gson().toJsonTree(EndecaConstants.FAIL_STRING));
		}

		LOG.info("Exit from getSubmittalResponse method");

		return groomedResponse;
	}

	private JsonArray getGroomedSubmittalFilters(final JsonObject submittalResponse, Locale locale) throws Exception {
		final JsonArray groomedSubmittalFilters = new JsonArray();
		if (null != adminService) {
			try (ResourceResolver resourceResolver = adminService.getReadService()) {
				JsonArray submittalFilters = new JsonArray();
				if (null != submittalResponse && submittalResponse.has(EndecaConstants.RESPONSE_STRING)) {
					final JsonObject responseJson = submittalResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
					if (null != responseJson && responseJson.has(EndecaConstants.BINNING)) {
						// The binning property may exist in the response but not be a JSON object and so we need to use the optJSONObject method.
						final JsonObject binningJson = responseJson.get(EndecaConstants.BINNING).getAsJsonObject();
						if (null != binningJson && binningJson.has(EndecaConstants.BINNING_SET)) {
							submittalFilters = binningJson.get(EndecaConstants.BINNING_SET).getAsJsonArray();
						}
					}
				}
				TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
				// Any surprise that JSONArray doesn't implement an iterator?
				for (int filterIndex = 0; filterIndex < submittalFilters.size(); filterIndex++) {
					final JsonObject filter = submittalFilters.get(filterIndex).getAsJsonObject();
					final JsonObject groomedFilter = new JsonObject();
					groomedFilter.add(EndecaConstants.NAME_STRING, new Gson().toJsonTree(filter.get(EndecaConstants.BSID).getAsString()));
					if (null != tagManager) {
						final Tag filterLabelTag = tagManager.resolve(EndecaFacetTag.convertId(
								filter.get(EndecaConstants.LABEL).getAsString()));
						if (filterLabelTag != null) {
							final String filterLabel = filterLabelTag.getTitle(locale);
							groomedFilter.add(EndecaConstants.TITLE, new Gson().toJsonTree(filterLabel));
							groomedFilter.add(EndecaConstants.VALUES, new JsonArray());
							for (int binIndex = 0; binIndex < filter.get(EndecaConstants.BIN).getAsJsonArray().size(); binIndex++) {
								final JsonObject filterValue = filter.get(EndecaConstants.BIN).getAsJsonArray().get(binIndex).getAsJsonObject();
								final JsonObject groomedFilterValue = new JsonObject();
								groomedFilterValue.add(EndecaConstants.NAME_STRING,
										new Gson().toJsonTree(filterValue.get(EndecaConstants.VALUE_STRING).getAsString()));
								final Tag filterValueTag = tagManager.resolve(EndecaFacetTag.convertId(
										filter.get(EndecaConstants.LABEL).getAsString() + "_" + filterValue.get(EndecaConstants.LABEL).getAsString()));
								if (filterValueTag != null) {
									final String filterValueLabel = filterValueTag.getTitle(locale);
									groomedFilterValue.add(EndecaConstants.TITLE, new Gson().toJsonTree(filterValueLabel));
									groomedFilterValue.add(EndecaConstants.NAME_STRING, new Gson().toJsonTree(filterValue.get(EndecaConstants.LABEL).getAsString()));
									groomedFilterValue.add(EndecaConstants.COUNT, new Gson().toJsonTree(filterValue.get(EndecaConstants.NDOCS).getAsString()));
									groomedFilterValue.add(EndecaConstants.ID_STRING, new Gson().toJsonTree(filterValue.get(EndecaConstants.VALUE_STRING).getAsString()));
									groomedFilter.get(EndecaConstants.VALUES).getAsJsonArray().add(groomedFilterValue);
								}
							}
							groomedSubmittalFilters.add(groomedFilter);
						}
					}
				}
			}
		}
		return groomedSubmittalFilters;
	}

	private int getSubmittalTotalCount(final JsonObject submittalResponse) throws Exception {
		int count = 0;

        if (null != submittalResponse && submittalResponse.has(EndecaConstants.RESPONSE_STRING)) {
            final JsonObject responseJson = submittalResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
            if (null != responseJson && responseJson.has(EndecaConstants.SEARCH_RESULTS_STRING)) {
                final JsonObject searchJson = responseJson.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
                if (null != searchJson && searchJson.has(EndecaConstants.TOTAL_COUNT_STRING)) {
                    count = searchJson.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt();
                }
            }
        }

		return count;
	}

	private JsonArray getGroomedResponseResults(final JsonObject submittalResponse, Locale locale) throws Exception {
		final JsonArray groomedSubmittalResults = new JsonArray();
		if (null != adminService) {
			try (ResourceResolver resourceResolver = adminService.getReadService()) {
				TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
				JsonArray submittalResults = new JsonArray();
				if (null != submittalResponse && submittalResponse.has(EndecaConstants.RESPONSE_STRING)) {
					final JsonObject responseJson = submittalResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
					if (null != responseJson && responseJson.has(EndecaConstants.SEARCH_RESULTS_STRING)) {
						final JsonObject searchJson = responseJson.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
						if (null != searchJson && searchJson.has(EndecaConstants.DOCUMENT_STRING)) {
							submittalResults = searchJson.get(EndecaConstants.DOCUMENT_STRING).getAsJsonArray();
						}
					}
				}
				for (int resultsIndex = 0; resultsIndex < submittalResults.size(); resultsIndex++) {
					final JsonObject result = submittalResults.get(resultsIndex).getAsJsonObject();
					final JsonObject groomedResult = new JsonObject();
					for (int contentIndex = 0; contentIndex < result.get(EndecaConstants.CONTENT_STRING).getAsJsonArray().size(); contentIndex++) {
						final JsonObject resultProperty = result.get(EndecaConstants.CONTENT_STRING).getAsJsonArray().get(contentIndex).getAsJsonObject();
						if (EndecaConstants.URL_STRING.equals(resultProperty.get(EndecaConstants.NAME_STRING).getAsString())) {
							groomedResult.add(EndecaConstants.URL, new Gson().toJsonTree(URLDecoder.decode(resultProperty.get(EndecaConstants.VALUE_STRING).getAsString(), "UTF-8")));
						} else if (EndecaConstants.TITLE.equals(resultProperty.get(EndecaConstants.NAME_STRING).getAsString())) {
							groomedResult.add(EndecaConstants.TITLE, new Gson().toJsonTree(resultProperty.get(EndecaConstants.VALUE_STRING).getAsString()));
						} else if (resultProperty.get(EndecaConstants.NAME_STRING).getAsString().contains(EndecaConstants.DAM_SIZE)) {
							groomedResult.add(EndecaConstants.SIZE, new Gson().toJsonTree(resultProperty.get(EndecaConstants.VALUE_STRING).getAsString()));
						} else {
							final String propertyName = resultProperty.get(EndecaConstants.NAME_STRING).getAsString();
							final String propertyValue = resultProperty.get(EndecaConstants.VALUE_STRING).getAsString();
							if (null != tagManager && !propertyValue.isEmpty()) {
								final Tag propertyValueTag = tagManager.resolve(EndecaFacetTag.convertId(propertyName + "_" + propertyValue));
								if (propertyValueTag != null) {
									final String localizedPropertyValue = propertyValueTag.getTitle(locale);
									groomedResult.add(propertyName, new Gson().toJsonTree(localizedPropertyValue));
								}
							}
						}
					}
					groomedSubmittalResults.add(groomedResult);
				}
			} catch (UnsupportedEncodingException e) {
				LOG.error("Failed to decode url for submittal builder result that was returned from Endeca", e);
			}
		}
		return groomedSubmittalResults;
	}

	private void getCallToRemoveDuplicateFacetsValue(List<FacetGroupBean> facetGroupList, SiteSearchResponse pageResponse, ClutchSelectorResponse clutchResponse, TorqueSelectorResponse torqueSelectorResponse){
		try {
			if(null != facetGroupList) {
				final int groupListSize = facetGroupList.size();
				for (int groupListIndex = 0; groupListIndex < groupListSize; groupListIndex++) {
					final Map<String, Integer> duplicateFacetsList = new HashMap<String, Integer>();
					int facetsListSize = facetGroupList.get(groupListIndex).getFacetValueList().size();
					for (int facetsListIndex = 0; facetsListIndex < facetsListSize; facetsListIndex++) {
						final FacetValueBean facetValueBean = facetGroupList.get(groupListIndex).getFacetValueList().get(facetsListIndex);
						final String facetValueLabel = facetValueBean.getFacetValueLabel().replaceAll("^\\s+|\\s+$", "");
						if (null != duplicateFacetsList && duplicateFacetsList.containsKey(facetValueLabel)) {
							int countNumber = duplicateFacetsList.get(facetValueLabel);
							String duplicateFacetsId = facetGroupList.get(groupListIndex).getFacetValueList().get(countNumber).getFacetValueId().toString();
							facetValueBean.setFacetValueId(duplicateFacetsId + EndecaConstants.DOLLAR_SYMBOL + facetValueBean.getFacetValueId().toString());
							facetGroupList.get(groupListIndex).getFacetValueList().remove(countNumber);
							if (facetsListIndex != 0) {facetsListIndex = facetsListIndex - 1;}
							facetsListSize = facetGroupList.get(groupListIndex).getFacetValueList().size();
						} else {
							if (null != facetValueLabel && !StringUtils.EMPTY.equals(facetValueLabel)) {
								duplicateFacetsList.put(facetValueLabel, facetsListIndex);
							}
						}
					}
				}
			}
		}catch (Exception e){
			LOG.error("Error while removing facet duplication:: "+e.getMessage());
		}
	}

	private PoolingHttpClientConnectionManager getMultiThreadedConf() {
		if (conMgr == null) {
			conMgr = new PoolingHttpClientConnectionManager();
			conMgr.setMaxTotal(MAX_CONNECTIONS);
			conMgr.setDefaultMaxPerRoute(MAX_HOST_CONNECTIONS);
		}
		return conMgr;
	}

	private CrossReferenceResponseBean getCrossReferenceResponseBean (final EndecaServiceRequestBean endecaServiceRequestBean) {
		LOG.info("Entry into getCrossReferenceResponseBean method");
		endecaServiceHelper = new EndecaServiceHelper();
		endecaConfigBean = endecaConfigService.getConfigServiceBean();
		final String responseString = espServiceCache.get(endecaServiceRequestBean);
		final ObjectMapper mapper = new ObjectMapper();
		CrossReferenceResponseBean responseBean = null;

		try {
			responseBean = mapper.readValue(responseString, CrossReferenceResponseBean.class);
		} catch (IOException e) {
			LOG.error("Error generating cross reference response ", e);
		}

		LOG.info("Exit from getCrossReferenceResponseBean method");
		return responseBean;
    }

	public List<XRefResult> getCrossReferencePartialMatches(final EndecaServiceRequestBean endecaServiceRequestBean) {
        LOG.info("Entry into getCrossReferencePartialMatches method");
		final CrossReferenceResponseBean responseBean = getCrossReferenceResponseBean(endecaServiceRequestBean);
		final List<XRefResult> partialMatches = new ArrayList<>();

		if (responseBean != null) {
			for (XRefResult resultBean : responseBean.getResponse().getSearchResults().getDocument()) {
				if (!resultBean.getCrossedPartNumber().equalsIgnoreCase(endecaServiceRequestBean.getSearchTerms().replaceAll("\\*", StringUtils.EMPTY))) {
					partialMatches.add(resultBean);
				}
			}
		} else {
			LOG.debug("Error generating cross reference partial matches response");
		}
		LOG.info("Exit from getCrossReferencePartialMatches method");
		return partialMatches;
	}

	public List<XRefResult> getCrossReferenceBestMatches(final EndecaServiceRequestBean endecaServiceRequestBean) {
        LOG.info("Entry into getCrossReferenceBestMatches method");
		final CrossReferenceResponseBean responseBean = getCrossReferenceResponseBean(endecaServiceRequestBean);
		final List<XRefResult> bestMatches = new ArrayList<>();

		if (responseBean != null) {
			for (XRefResult resultBean : responseBean.getResponse().getSearchResults().getDocument()) {
				if (resultBean.getCrossedPartNumber().equalsIgnoreCase(endecaServiceRequestBean.getSearchTerms().replaceAll("\\*", StringUtils.EMPTY))) {
					bestMatches.add(resultBean);
				}
			}
		} else {
			LOG.debug("Error generating cross reference best matches response");
		}

		LOG.info("Exit from getCrossReferenceBestMatches method");
		return bestMatches;
	}

	public JsonObject getCrossReferenceResponse(final EndecaServiceRequestBean endecaServiceRequestBean,
												SlingHttpServletRequest slingRequest, Page currentPage) {
		LOG.info("Entry into getCrossReferenceResponse method");
		final CrossReferenceResponseBean responseBean = getCrossReferenceResponseBean(endecaServiceRequestBean);
		final JsonObject response = new JsonObject();

		if (responseBean != null) {
			try {
				final ObjectMapper mapper = new ObjectMapper();
				final JsonArray results = new JsonArray();
				final JsonArray bestMatchResults = new JsonArray();
				final JsonArray partialMatchResults = new JsonArray();
				final XRefSearchResults searchResults = responseBean.getResponse().getSearchResults();
				if (null != searchResults) {
					Integer resultCount = 0;
					for (XRefResult resultBean : searchResults.getDocument()) {
						final JsonObject resultBeanJSON = new JsonParser().parse(mapper.writeValueAsString(resultBean)).getAsJsonObject();
						resultBeanJSON.add(EndecaConstants.RESULT_ID , new Gson().toJsonTree(resultBean.getResultID()
								.concat(resultCount.toString())));
						results.add(resultBeanJSON);
						if (resultBean.getCrossedPartNumber().equalsIgnoreCase(endecaServiceRequestBean.getSearchTerms()
								.replaceAll("\\*", StringUtils.EMPTY))) {
							bestMatchResults.add(resultBeanJSON);
						} else {
							partialMatchResults.add(resultBeanJSON);
						}
						resultCount ++;
					}
					response.add(EndecaConstants.TOTAL_COUNT_STRING, new Gson().toJsonTree(searchResults.getTotalCount()));
				} else {
					response.add(EndecaConstants.TOTAL_COUNT_STRING, new Gson().toJsonTree("0"));
				}
				response.add(EndecaConstants.RESULTS, results);
				response.add(EndecaConstants.BEST_MATCH_RESULTS, bestMatchResults);
				response.add(EndecaConstants.PARTIAL_MATCH_RESULTS, partialMatchResults);


				JsonArray facets = new JsonArray();
				for (int i = 0; i < responseBean.getFacetGroupListJson().size(); i++) {
					JsonObject facetGroupJson = responseBean.getFacetGroupListJson().get(i).getAsJsonObject();
					if (null != facetGroupJson.get(CommonConstants.TITLE)) {
						facetGroupJson.add(CommonConstants.TITLE, new Gson().toJsonTree(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,facetGroupJson.get(CommonConstants.TITLE).getAsString(), facetGroupJson.get(CommonConstants.TITLE).getAsString())));
					}
					if (facetGroupJson.get(EndecaConstants.VALUES).getAsJsonArray().size() > 1) {
						facets.add(facetGroupJson);
					}
				}
				response.add(EndecaConstants.FACETS_STRING, facets);

			} catch (IOException e) {
				LOG.error("Error generating cross reference response JSON", e);
			}
		} else {
			LOG.debug("Error generating cross reference response JSON");
		}
		LOG.info("Exit from getCrossReferenceResponse method");
		return response;
	}
}
