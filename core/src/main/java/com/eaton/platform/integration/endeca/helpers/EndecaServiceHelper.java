package com.eaton.platform.integration.endeca.helpers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.sitesearch.KeywordBean;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.models.productgrid.ProductGridModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.impl.AdminServiceImpl;
import com.google.gson.JsonElement;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.bean.ImageRenditionBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.FacetsBean;
import com.eaton.platform.integration.endeca.bean.familymodule.FamilyModuleBean;
import com.eaton.platform.integration.endeca.bean.familymodule.FamilyModuleResponseBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUCardParameterBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityListResponseBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityModuleBean;
import com.eaton.platform.integration.endeca.bean.productcompatibility.ProductCompatibilityResponseBean;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchPageResponse;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResultsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.ProductFamilyBean;
import com.eaton.platform.integration.endeca.bean.typeahead.TypeAheadSiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchTool;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchToolResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueTool;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueToolResponse;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.constants.EndecaErrorCodeMap;
import com.eaton.platform.integration.endeca.util.FileTypesUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static com.eaton.platform.core.constants.CommonConstants.EATON;


/**
 * 
 * @author 308059
 * 
 */
public class EndecaServiceHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(EndecaServiceHelper.class);


    private String message;
	private boolean isValidEntry = true;
	private String flow;
	private ResourceResolver resourceResolver;
	
	private int productsCount;
	private int newsCount;
	private int resourcesCount;
	private int servicesCount;
	private int totalCount;
	public static final String CONTENT_ROOT_FOLDER = "/content/eaton";
	public static final String APPLICATION_ZIP = "application/zip";
	public static final String XWD_STRING = "image/x-xwindowdump";
    
	private Map<String,Object> facetValueMap =  new HashMap<>();
	/**
	 * This method converts the request bean to JSON String using Jackson API
	 * parser
	 * 
	 * @param aemServiceRequestBean
	 * @param endecaConfigServiceBean
	 * @return JSON String
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public String convertBeanTOJSON(EndecaServiceRequestBean endecaServiceRequestBean) throws IOException {
		LOGGER.info("Entry into convertBeanTOJSON method");
		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest = StringUtils.EMPTY;

		try {

			// Object to JSON in String using Jackson mapper
			jsonRequest = mapper.writeValueAsString(endecaServiceRequestBean);
			JsonObject jsonObject = new JsonParser().parse(jsonRequest).getAsJsonObject();
			jsonObject.remove("hashCode");
			jsonRequest = jsonObject.toString();
		} catch (JsonGenerationException e) {
			throw new JsonGenerationException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch (JsonMappingException e) {
			throw new JsonMappingException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch (Exception e) {
			LOGGER.error(String.format("Exception from EndecaServiceHelper class of convertBeanTOJSON():%s",e.getMessage()));
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("JSON request :" + jsonRequest);
		}
		LOGGER.info("Exit from convertBeanTOJSON method");
		return jsonRequest;
	}
	
	/**
	 * To convert the JSON Response to bean
	 * 
	 * @param jsonResponse
	 * @return Product family List or SubCategory List
	 * @throws Exception
	 */
	public FamilyListResponseBean convertFamilyListJSONTOBean(String jsonResponse) throws Exception {
		LOGGER.info("Entry into convertFamilyListJSONTOBean method ");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		FamilyListResponseBean familyList = new FamilyListResponseBean();
		flow = EndecaConstants.SUBCATEGORY_STRING;

		try {
			// Auto conversion of JSON string to Java bean
			familyList = mapper.readValue(jsonResponse, FamilyListResponseBean.class);

			LOGGER.debug(" Family List after Jackson Mapping : " + familyList.getResponse());

			if (familyList.getResponse() != null && !CommonUtil.isBlankOrNull(familyList.getResponse().getStatus())) {
				if (familyList.getResponse().getStatus().equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)) {
					validateFacets(familyList.getResponse().getFacets(),flow);
					
					familyList.getResponse().setFacetValueMap(facetValueMap);
					// To map search results explicitly
					mapFamilyListJSONToResponse(familyList, jsonResponse);

				} else if (familyList.getResponse().getStatus().equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
					message = familyList.getResponse().getStatusdetails().getMessages().get(0);
					throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
				}
			}

		} catch (EatonSystemException e) {
			// To set the failure message
			setFailureErrorlog(message,flow);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_021_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.SUBCATEGORY_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage() + e);
		}
		LOGGER.info("Exit from convertFamilyListJSONTOBean method ");

		return familyList;
	}

	/**
	 * To map the Search results in JSON response to bean
	 * 
	 * @param familyBean
	 * @param jsonResponse
	 * @throws JSONException
	 * @throws JsonMappingException
	 */
	private void mapFamilyListJSONToResponse(FamilyListResponseBean familyBean, String jsonResponse)
			throws Exception {
		LOGGER.info("Entry into mapFamilyListJSONToResponse method");
		JsonObject familyResponse;

		try {

			FamilyResponseBean familyResponseBean = familyBean.getResponse();
			familyResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();

			JsonObject response = familyResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
			JsonObject searchResults = response.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
			familyResponseBean.setTotalCount(searchResults.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt());

			JsonArray document = searchResults.get(EndecaConstants.DOCUMENT_STRING).getAsJsonArray();
			if (familyResponseBean.getTotalCount() > 0 && document != null) {
				totalCount = familyResponseBean.getTotalCount();
				List<ProductFamilyBean> productFamilyList = getProductFamilyList(document);
				familyResponseBean.setProductFamilyBean(productFamilyList);
				familyResponseBean.setTotalCount(totalCount);
			} else {
				JsonObject request = response.get(EndecaConstants.REQUEST_STRING).getAsJsonObject();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(EndecaConstants.NO_MATCHING_SEARCH_RESULTS
							+ request.get(EndecaConstants.SEARCH_TERMS_STRING).getAsString());
				}
			}
			familyBean.setResponse(familyResponseBean);

		} catch (Exception e) {
			familyBean.getResponse().setStatus(EndecaConstants.FAIL_STRING);
			throw new Exception(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		LOGGER.info("Exit from mapFamilyListJSONToResponse method");
	}
	
	/**
	 * To fetch the list of Product families
	 * @param document
	 * @return
	 * @throws JSONException
	 */
	private List<ProductFamilyBean> getProductFamilyList(JsonArray document) throws Exception{
		LOGGER.info("Entry into getProductFamilyList method");
		List<ProductFamilyBean> productFamilyList = new ArrayList<>();
		ProductFamilyBean productFamily = null;

		for (int i = 0; i < document.size(); i++) {
			isValidEntry = true;
			JsonObject contentObject = document.get(i).getAsJsonObject();
			JsonArray contentArray = contentObject.get(EndecaConstants.CONTENT_STRING).getAsJsonArray();
			if (contentArray != null) {
				productFamily = new ProductFamilyBean();
				for (int j = 0; j < contentArray.size(); j++) {
					JsonObject contentItem = contentArray.get(j).getAsJsonObject();

					String name = contentItem.get(EndecaConstants.NAME_STRING).getAsString();
					String value = contentItem.get(EndecaConstants.VALUE_STRING).getAsString();
					isValidEntry = setProductFamilyValues(productFamily,name,value);
				}
				if (isValidEntry && !CommonUtil.isBlankOrNull(productFamily.getProductName())
						&& !CommonUtil.isBlankOrNull(productFamily.getUrl())) {
					productFamilyList.add(productFamily);
				} else {
					updateTotalCount();
				}
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Family Results :" + productFamilyList);
		}
		LOGGER.info("Exit from getProductFamilyList method");
		return productFamilyList;
	}
	/**
	 * To set the values in the product family bean
	 * @param productFamily
	 * @param name
	 * @param value
	 * @return
	 */
	private boolean setProductFamilyValues(ProductFamilyBean productFamily,String name,String value) {
		LOGGER.info("Entry into setProductFamilyValues method");
		String errorLogMsg = StringUtils.EMPTY;
		String altText;
		switch (name) {
		case EndecaConstants.PRODUCT_NAME_STRING:
			if (!CommonUtil.isBlankOrNull(value)) {
				productFamily.setProductName(value);
			} else {
				isValidEntry = false;
				errorLogMsg = EndecaConstants.ERR_ENDECA_021_STRING + EndecaConstants.HYPHEN
						+ EndecaConstants.SUBCATEGORY_STRING + EndecaConstants.HYPHEN + name;
			}
			break;
		case EndecaConstants.PRODUCT_IMAGE_STRING:
			productFamily.setProductImage(value);
			ImageRenditionBean imageRenditionBean = new ImageRenditionBean();
			imageRenditionBean.setDesktopTransformedUrl(value);
			imageRenditionBean.setMobileTransformedUrl(value);
			imageRenditionBean.setTabletTransformedUrl(value);
			if(productFamily.getProductName()!= null){
				altText = productFamily.getProductName();
			}else {
				if ((CommonUtil.getAssetAltText(resourceResolver,
						value)) != null) {
					altText = CommonUtil.getAssetAltText(resourceResolver,
							value);
				} else {
					altText = CommonConstants.BLANK_SPACE;
				}
			}
			imageRenditionBean.setAltText(altText);
			productFamily.setProductImageBean(imageRenditionBean);
			break;
		case EndecaConstants.SUB_CATEGORY_NAME_STRING:
			productFamily.setSubCategoryName(value);
			break;
		case EndecaConstants.URL_STRING:
			if (!CommonUtil.isBlankOrNull(value)){
				productFamily.setUrl(value);
				
			} else {
				isValidEntry = false;
				errorLogMsg = EndecaConstants.ERR_ENDECA_021_STRING + EndecaConstants.HYPHEN
						+ EndecaConstants.SUBCATEGORY_STRING + EndecaConstants.HYPHEN + name;
			}
			break;
		case EndecaConstants.PRODUCT_STATUS:
			if(!CommonUtil.isBlankOrNull(value)){
				productFamily.setProductStatus(value);
			}
			break;
		case EndecaConstants.PRODUCT_GRID_DESCRIPTION:
			if(!CommonUtil.isBlankOrNull(value)){
				LOGGER.debug("inside product description {} ",value);
				productFamily.setProductGridDescription(value);
			}
			break;
		case EndecaConstants.SECURE_STRING:
			productFamily.setSecure(!CommonUtil.isBlankOrNull(value) && value.equals(CommonConstants.YES) ? true : false);
			break;

		case EndecaConstants.COMPANY_NAME:
			if(!CommonUtil.isBlankOrNull(value)) {
				LOGGER.debug("inside company name {} ",value);
				productFamily.setCompanyName(value);
			}else {
				productFamily.setCompanyName(EATON);
			}
			break;

		case  EndecaConstants.PUBLISH_DATE:
				if(!CommonUtil.isBlankOrNull(value))
				{
					LOGGER.debug("Inside publish date {}",value);
					productFamily.setPublishDate(value);
				}
				break;
			case EndecaConstants.PARTNER_BADGE:
				if(!CommonUtil.isBlankOrNull(value))
				{
					LOGGER.debug("Partner badge visible {} ",value);
					productFamily.setPartnerBadgeVisible(Boolean.parseBoolean(value));
				}
				break;
			case EndecaConstants.PRODUCT_TYPE_VALUE:
				if(!CommonUtil.isBlankOrNull(value))
				{
					productFamily.setProductType(value);
				}
				break;
		default:
			errorLogMsg = EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.SUBCATEGORY_STRING;
			break;
		}
		if (!CommonUtil.isBlankOrNull(errorLogMsg)) {
			LOGGER.debug(errorLogMsg);
		}
		LOGGER.info("Exit from setProductFamilyValues method");
		return isValidEntry;
	}
		/**
	 * TO Convert the SKU List JSON Response to bean
	 * 
	 * @param jsonResponse
	 * @return SKU List
	 */

		public ProductCompatibilityListResponseBean convertProductCompatibilityDetailsJSONTOBeanList(String jsonResponse,List<String> compatibilityExcelTable) {

		LOGGER.info("Entry into convertProductCompatibilityDetailsJSONTOBeanList method");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ProductCompatibilityListResponseBean skuListResponse = new ProductCompatibilityListResponseBean();
		flow = EndecaConstants.FAMILY_STRING;
		
		try {
			if (null != jsonResponse) {
				// Auto conversion of JSON string to Java bean
				skuListResponse = mapper.readValue(jsonResponse, ProductCompatibilityListResponseBean.class);
				LOGGER.debug(" SKU/Family Module Lst after Jackson Mapping : " + skuListResponse.getFamilyModuleResponse());

				if (skuListResponse.getFamilyModuleResponse() != null
						&& !CommonUtil.isBlankOrNull(skuListResponse.getFamilyModuleResponse().getStatus())) {
					if (skuListResponse.getFamilyModuleResponse().getStatus()
							.equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)) {
						// validate facet values
						validateFacets(skuListResponse.getFamilyModuleResponse().getFacets(),flow);

						skuListResponse.getFamilyModuleResponse().setFacetValueMap(facetValueMap);

						// To Map the search results explicitly
						mapProductComptabilityListJSONToResponse(skuListResponse, jsonResponse,compatibilityExcelTable);
					} else if (skuListResponse.getFamilyModuleResponse().getStatus()
							.equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
						message = skuListResponse.getFamilyModuleResponse().getStatusDetails().getMessages().get(0);
						throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
					}
				}
			}
		} catch (EatonSystemException e) {
			// To set the failure message
			setFailureErrorlog(message,flow);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_020_STRING + EndecaConstants.HYPHEN + EndecaConstants.FAMILY_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		}
		LOGGER.info("End Of convertProductCompatibilityDetailsJSONTOBeanList method ");
		return skuListResponse;
	}

	/**
	 * TO Convert the SKU List JSON Response to bean
	 * 
	 * @param jsonResponse
	 * @return SKU List
	 */
	public SKUListResponseBean convertSKUListJSONTOBean(String jsonResponse,JsonArray filtervalue,String languageForRuntimeGraph) {

		LOGGER.info("Entry into convertSKUListJSONTOBean method");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SKUListResponseBean skuListResponse = new SKUListResponseBean();
		flow = EndecaConstants.FAMILY_STRING;
		
		try {
			if (null != jsonResponse) {
				// Auto conversion of JSON string to Java bean
				skuListResponse = mapper.readValue(jsonResponse, SKUListResponseBean.class);
				LOGGER.debug(" SKU/Family Module Lst after Jackson Mapping : " + skuListResponse.getFamilyModuleResponse());

				if (skuListResponse.getFamilyModuleResponse() != null
						&& !CommonUtil.isBlankOrNull(skuListResponse.getFamilyModuleResponse().getStatus())) {
					if (skuListResponse.getFamilyModuleResponse().getStatus()
							.equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)) {
						// validate facet values
						validateFacets(skuListResponse.getFamilyModuleResponse().getFacets(),flow);

						skuListResponse.getFamilyModuleResponse().setFacetValueMap(facetValueMap);

						// To Map the search results explicitly
						mapSKUListJSONToResponse(skuListResponse, jsonResponse,filtervalue,languageForRuntimeGraph);
					} else if (skuListResponse.getFamilyModuleResponse().getStatus()
							.equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
						message = skuListResponse.getFamilyModuleResponse().getStatusDetails().getMessages().get(0);
						throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
					}
				}
			}
		} catch (EatonSystemException e) {
			// To set the failure message
			setFailureErrorlog(message,flow);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_020_STRING + EndecaConstants.HYPHEN + EndecaConstants.FAMILY_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		}
		LOGGER.info("End Of convertSKUListJSONTOBean method ");
		return skuListResponse;
	}
    /**
	 * To map the search results in SKU list response to bean
	 * 
	 * @param skuList
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void mapProductComptabilityListJSONToResponse(ProductCompatibilityListResponseBean skuList, String jsonResponse,List<String> compatibilityExcelTable) throws Exception {
		LOGGER.info("Inside mapSKUListJSONToResponse method");
		JsonObject skuListResponse;

		try {
			ProductCompatibilityResponseBean familyModuleResponse = skuList.getFamilyModuleResponse();
			skuListResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();

			JsonObject response = skuListResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
			JsonObject searchResults = response.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
			familyModuleResponse.setTotalCount(searchResults.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt());

			JsonArray document = searchResults.get(EndecaConstants.DOCUMENT_STRING).getAsJsonArray();
			if (familyModuleResponse.getTotalCount() > 0 && document != null) {
				totalCount =familyModuleResponse.getTotalCount();
				List<ProductCompatibilityModuleBean> familyModuleList = getProductCompatibilityModuleList(document,compatibilityExcelTable);
				familyModuleResponse.setFamilyModule(familyModuleList);
				familyModuleResponse.setTotalCount(totalCount);
			}else{
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(EndecaConstants.NO_MATCHING_SEARCH_RESULTS);
				}
			}

			skuList.setFamilyModuleResponse(familyModuleResponse);
			skuList.setCompatibilityTableHeaders(compatibilityExcelTable);

		} catch (Exception e) {
			skuList.getFamilyModuleResponse().setStatus(EndecaConstants.FAIL_STRING);
			throw new Exception(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		LOGGER.info("End of mapSKUListJSONToResponse method");
	}
	/**
	 * To map the search results in SKU list response to bean
	 * 
	 * @param skuList
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void mapSKUListJSONToResponse(SKUListResponseBean skuList, String jsonResponse,JsonArray filtervalue,String languageForRuntimeGraph) throws Exception {
		LOGGER.info("Inside mapSKUListJSONToResponse method");
		JsonObject skuListResponse;

		try {
			FamilyModuleResponseBean familyModuleResponse = skuList.getFamilyModuleResponse();
			skuListResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();

			JsonObject response = skuListResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
			JsonObject searchResults = response.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
			familyModuleResponse.setTotalCount(searchResults.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt());

			JsonArray document = searchResults.get(EndecaConstants.DOCUMENT_STRING).getAsJsonArray();
			if (familyModuleResponse.getTotalCount() > 0 && document != null) {
				totalCount =familyModuleResponse.getTotalCount();
				List<FamilyModuleBean> familyModuleList = getFamilyModuleList(document,filtervalue,languageForRuntimeGraph);
				familyModuleResponse.setFamilyModule(familyModuleList);
				familyModuleResponse.setTotalCount(totalCount);
			}else{
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(EndecaConstants.NO_MATCHING_SEARCH_RESULTS);
				}
			}

			skuList.setFamilyModuleResponse(familyModuleResponse);

		} catch (Exception e) {
			skuList.getFamilyModuleResponse().setStatus(EndecaConstants.FAIL_STRING);
			throw new Exception(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		LOGGER.info("End of mapSKUListJSONToResponse method");
	}

	/**
	 * To fetch the list SKU card parameters from the request
	 * @param filters
	 * @return
	 * @throws JSONException
	 */
	public JsonArray getSKUCardParameters(String skuListJsonRequest) throws Exception {
		LOGGER.info("Entry into getSKUCardParameters method ");
		JsonArray filterValue = new JsonArray();
		JsonArray tempArray = new JsonArray();
		JsonObject skuListRequestObject = new JsonParser().parse(skuListJsonRequest).getAsJsonObject();

		JsonArray filters = skuListRequestObject.get(EndecaConstants.FILTERS_STRING).getAsJsonArray();
		
		for (int i = 0; i < filters.size(); i++) {
			JsonObject filterItem = filters.get(i).getAsJsonObject();
			String filterName = filterItem.get(EndecaConstants.FILTER_NAME_STRING).getAsString();
			if (!CommonUtil.isBlankOrNull(filterName)
					&& filterName.equalsIgnoreCase(EndecaConstants.SKU_CARD_PARAMETERS_STRING)) {
				tempArray = filterItem.get(EndecaConstants.FILTER_VALUE_STRING).getAsJsonArray();
				break;
			}
		}
		for (int j = 0; j < tempArray.size(); j++) {
			if (tempArray.get(j) != null) {
				filterValue.add(tempArray.get(j));
			}
		}
		LOGGER.info("Exit from getSKUCardParameters method ");
		return filterValue;
	}

	public String getLanguageForRuntimeGraph(String skuListJsonRequest) throws Exception {
		String result = StringUtils.EMPTY;
		JsonObject skuListRequestObject = new JsonParser().parse(skuListJsonRequest).getAsJsonObject();
		if(skuListRequestObject.has(EndecaConstants.LANGUAGE)) {
			result = skuListRequestObject.get(EndecaConstants.LANGUAGE).toString().toLowerCase().replace("_", "-") + "/";
			result = result.contains("\"") ? result.replace("\"","") : result;
		}
		return result.isEmpty() ? "en-us/" : result;
	}
		/**
	 * To fetch the family module list  from the JSON response
	 * @param document
	 * @param filtervalue
	 * @return
	 * @throws JSONException
	 */
	private List<ProductCompatibilityModuleBean> getProductCompatibilityModuleList(JsonArray document,List<String> compatibilityExcelTable) throws Exception{
		LOGGER.info("Entry into getProductCompatibilityModuleList method ");
		
		List<ProductCompatibilityModuleBean> familyModuleList = new ArrayList<>();

		for (int i = 0; i < document.size(); i++) {
			isValidEntry = true;
			JsonObject contentObject = document.get(i).getAsJsonObject();
			JsonArray contentArray = contentObject.get(EndecaConstants.CONTENT_STRING).getAsJsonArray();
			ProductCompatibilityModuleBean familyModule = new ProductCompatibilityModuleBean();
			isValidEntry =  setProductCompatibilityModuleValues(contentArray,familyModule,compatibilityExcelTable);
			
			if (isValidEntry) {
				familyModuleList.add(familyModule);
			} else {
				updateTotalCount();
			}
		}
			
		LOGGER.info("Exit from getProductCompatibilityModuleList method ");
		return familyModuleList;
	}
	
	/**
	 * To fetch the family module list  from the JSON response
	 * @param document
	 * @param filtervalue
	 * @return
	 * @throws JSONException
	 */
	private List<FamilyModuleBean> getFamilyModuleList(JsonArray document,JsonArray filtervalue,String languageForRuntimeGraph) throws Exception{
		LOGGER.info("Entry into getFamilyModuleList method ");
		
		List<FamilyModuleBean> familyModuleList = new ArrayList<>();

		for (int i = 0; i < document.size(); i++) {
			isValidEntry = true;
			JsonObject contentObject = document.get(i).getAsJsonObject();
			JsonArray contentArray = contentObject.get(EndecaConstants.CONTENT_STRING).getAsJsonArray();
			FamilyModuleBean familyModule = new FamilyModuleBean();
			isValidEntry =  setFamilyModuleValues(contentArray,familyModule,filtervalue,languageForRuntimeGraph);
			
			if (isValidEntry && (!CommonUtil.isBlankOrNull(familyModule.getCatalogNumber())
					|| familyModule.getSkusArray().length > 0)) {
				familyModuleList.add(familyModule);
			} else {
				updateTotalCount();
			}
		}
			
		LOGGER.info("Exit from getFamilyModuleList method ");
		return familyModuleList;
	}
	/**
	 * To set the family module values in the bean
	 * @param contentArray
	 * @param familyModule
	 * @param filtervalue
	 * @return
	 * @throws JSONException
	 */
	private boolean setProductCompatibilityModuleValues(JsonArray contentArray,
	ProductCompatibilityModuleBean familyModule,List<String> compatibilityExcelTable)
			throws Exception {
		LOGGER.info("Entry into setProductCompatibilityModuleValues method ");
		
		
		String errorLogMsg = null;
		
		for (int j = 0; j < contentArray.size(); j++) {
			JsonObject contentItem = contentArray.get(j).getAsJsonObject();
            String name = contentItem.get(EndecaConstants.NAME_STRING).getAsString();
			if(!compatibilityExcelTable.contains(name)){
				compatibilityExcelTable.add(name);
			}
			if(!contentItem.get(EndecaConstants.VALUE_STRING).isJsonNull()){
				String value = contentItem.get(EndecaConstants.VALUE_STRING).getAsString();
				if(name.equals("Mktg_Desc")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setMktgDesc(value);
					}
				} else if(name.equals("Country")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setCountry(value);
					}
					
				} else if(name.equals("TST_Ext_Model_Number")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtModelNumber(value);
					}
					
				} else if(name.equals(EndecaConstants.LONG_DESC_STRING)){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setLongDesc(value);
					}
				} 
				else if(name.equals(EndecaConstants.PRIMARY_IMAGE_STRING)){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setPrimaryImage(value);
						ImageRenditionBean imageRenditionBean = new ImageRenditionBean();
						imageRenditionBean.setDesktopTransformedUrl(value);
						imageRenditionBean.setMobileTransformedUrl(value);
						imageRenditionBean.setTabletTransformedUrl(value);
						imageRenditionBean.setAltText("");
						familyModule.setProductImageBean(imageRenditionBean);
					}
				}
				else if(name.equals(EndecaConstants.RENDITION_ONE_STRING)){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setDsktopRendition(value);
					}
				}
				else if(name.equals(EndecaConstants.RENDITION_TWO_STRING)){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setMobileRendition(value);
					}
				}
				else if(name.equals("TST_Ext_Type")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtType(value);
					}
				}else if(name.equals("TST_Control_Family")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setControlFamily(value);
					}
			    } else if(name.equals("TST_Family_ID")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setFamilyID(value);
					}
	         	} else if(name.equals("TST_Dimmer_Catalog_Number")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setDimmerCatalogNumber(value);
					}
	         	} else if(name.equals("TST_Lowes_Item_Number")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setLowesItemNumber(value);
					}
	         	} else if(name.equals("TST_Lowes_Model_Number")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setLowesModelNumber(value);
					}
	         	} else if(name.equals("TST_LED_Rating")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setLedRating(value);
					}
	         	} else if(name.equals("TST_Max_of_loads")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setMaxofloads(value);
					}
	         	} else if(name.equals("TST_EXT_Lamp_Model_Number")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtLampModelNumber(value);
					}
	         	} else if(name.equals("TST_EXT_Image_URL")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtImageURL(value);
					}
	         	} else if(name.equals("TST_EXT_Lamp_Voltage")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtLampVoltage(value);
					}
	         	} else if(name.equals("TST_EXT_Color_Temperature")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtColorTemperature(value);
					}
	         	} else if(name.equals("TST_LED_Compatible_Score")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setLedCompatibleScore(value);
					}
	         	} else if(name.equals("TST_EXT_Base")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtBase(value);
					}
	         	} else if(name.equals("TST_EXT_Lamp_Manufacturer")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtLampManufacturer(value);
					}
	         	} else if(name.equals("TST_EXT_Date_Code")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtDateCode(value);
					}
	         	} else if(name.equals("TST_EXT_Lamp_Lumen_Output")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setExtLampLumenOutput(value);
					}
	         	} else if(name.equals("TST_Flicker")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setFlicker(value);
					}
	         	} else if(name.equals("TST_Low_end_trim")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setLowEndTrim(value);
					}
	         	} else if(name.equals("TST_Low_End")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setLowEnd(value);
					}
	         	}else if(name.equals("TST_Relative_Low_End")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setRelativeLowEnd(value);
					}
	         	} else if(name.equals("TST_Perceived_Dimming_range")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setPerceivedDimmingrange(value);
					}
	         	} else if(name.equals("TST_Perceived_Low_end")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setPerceivedLowend(value);
					}
	         	} else if(name.equals("TST_Test_Notes")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setTestNotes(value);
					}
	         	} else if(name.equals("TST_Low_End")){
					if(!CommonUtil.isBlankOrNull(value)){
						familyModule.setLowEnd(value);
					}
	         	} 
			}
		}
		LOGGER.info("Exit from setFamilyModuleValues method ");
		return isValidEntry;
	}

	
	/**
	 * To set the family module values in the bean
	 * @param contentArray
	 * @param familyModule
	 * @param filtervalue
	 * @return
	 * @throws JSONException
	 */
	private boolean setFamilyModuleValues(JsonArray contentArray,
			FamilyModuleBean familyModule, JsonArray filtervalue ,String languageForRuntimeGraph)
			throws Exception {
		LOGGER.info("Entry into setFamilyModuleValues method ");
		
		List<SKUCardParameterBean> skuCardList = new ArrayList<>();
		String errorLogMsg = null;
		
		for (int j = 0; j < contentArray.size(); j++) {
			JsonObject contentItem = contentArray.get(j).getAsJsonObject();

			String name = contentItem.get(EndecaConstants.NAME_STRING).getAsString();
			String value = contentItem.get(EndecaConstants.VALUE_STRING).getAsString();
			
			switch (name){
			case  EndecaConstants.CATALOG_NUMBER_STRING:
				if (!CommonUtil.isBlankOrNull(value)) {
					familyModule.setCatalogNumber(value);
				} else {
					isValidEntry = false;
					errorLogMsg = EndecaConstants.ERR_ENDECA_021_STRING + EndecaConstants.HYPHEN
							+ EndecaConstants.FAMILY_STRING + EndecaConstants.HYPHEN + name;
					LOGGER.debug(errorLogMsg);
				}
				break;
			case EndecaConstants.FAMILY_NAME_STRING:
				familyModule.setFamilyName(value);
				break;
			case EndecaConstants.MKTG_DESC_STRING:
				familyModule.setMktgDesc(value);
				break;
			case EndecaConstants.LONG_DESC_STRING:
				familyModule.setLongDesc(value);
				break;
			case EndecaConstants.MODEL_CODE_STRING:
				familyModule.setModelCode(value);
				break;
			case EndecaConstants.PRIMARY_IMAGE_STRING:
				familyModule.setPrimaryImage(value);
				ImageRenditionBean imageRenditionBean = new ImageRenditionBean();
				imageRenditionBean.setDesktopTransformedUrl(value);
				imageRenditionBean.setMobileTransformedUrl(value);
				imageRenditionBean.setTabletTransformedUrl(value);
				imageRenditionBean.setAltText("");
				familyModule.setProductImageBean(imageRenditionBean);
				break;
			case EndecaConstants.RENDITION_ONE_STRING:
				familyModule.setDsktopRendition(value);
				break;
			case EndecaConstants.RENDITION_TWO_STRING:
				familyModule.setMobileRendition(value);
				break;
			case EndecaConstants.ALT_SKUS:
				if(!CommonUtil.isBlankOrNull(value)){
					familyModule.setAltSku(Boolean.valueOf(value));
				} else {
					familyModule.setAltSku(false);
				}
				break;
			case EndecaConstants.SKUS:
				if (!CommonUtil.isBlankOrNull(value)) {
					familyModule.setSkusArray(value.split(EndecaConstants.PIPE_STRING));
				} else {
					isValidEntry = false;
					errorLogMsg = EndecaConstants.ERR_ENDECA_021_STRING + EndecaConstants.HYPHEN
							+ EndecaConstants.FAMILY_STRING + EndecaConstants.HYPHEN + name;
					LOGGER.debug(errorLogMsg);
				}
				break;
			default :
				for (int index = 0; index < filtervalue.size(); index++) {
					if (filtervalue.get(index).getAsString().equalsIgnoreCase(name) && !CommonUtil.isBlankOrNull(value)
							&& value.contains("||") && skuCardList.size()<3) {
						SKUCardParameterBean skuCardParameters = new SKUCardParameterBean();
						String[] splitString = value.split(Pattern.quote("||"));
						if(splitString.length>=2) {
							skuCardParameters.setLabel(splitString[0]);
							skuCardParameters.setSkuCardValues(StringEscapeUtils.unescapeHtml(splitString[1]));
						}
						if(name.equalsIgnoreCase(EndecaConstants.RUNTIME_GRAPH)){
							skuCardParameters.setRuntimeGraphURL(EndecaConstants.RUNTIME_GRAPH_URL + languageForRuntimeGraph + familyModule.getCatalogNumber());
						}
						skuCardList.add(skuCardParameters);

					}
				}
				break;
			}
		}
		familyModule.setSkuCardParameters(skuCardList);
		
		LOGGER.info("Exit from setFamilyModuleValues method ");
		return isValidEntry;
	}



	/**
	 * To convert the SKU details JSON response to bean
	 * 
	 * @param jsonResponse
	 * @return SKUDetailsResponseBean
	 */
	public SKUDetailsResponseBean convertSKUDetailsJSONTOBeanList(String jsonResponse) {
		LOGGER.info("Entry into convertSKUDetailsJSONTOBean method ");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SKUDetailsResponseBean skuDetailsResponse = new SKUDetailsResponseBean();
		flow = EndecaConstants.SKU_STRING;
		try {
			// Auto conversion of JSON string to Java bean
			skuDetailsResponse = mapper.readValue(jsonResponse, SKUDetailsResponseBean.class);
			LOGGER.debug(" SKU Details after Jackson Mapping : " + skuDetailsResponse.getSkuResponse());

			if (skuDetailsResponse.getSkuResponse() != null) {
				if (skuDetailsResponse.getSkuResponse().getStatus().equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)) {
					// To map the search results explicitly
					mapSKUDetailsJSONToResponseList(skuDetailsResponse, jsonResponse);
				} else if (skuDetailsResponse.getSkuResponse().getStatus()
						.equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
					message = skuDetailsResponse.getSkuResponse().getStatusDetails().getMessages().get(0);
					throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
				}
			}
		} catch (EatonSystemException e) {
			// To set the failure message
			setFailureErrorlog(message,flow);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_019_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN
					+ e);
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		}
		LOGGER.info("Exit from convertSKUDetailsJSONTOBean method ");

		return skuDetailsResponse;
	}
	
	/**
	 * To convert the SKU details JSON response to bean
	 * 
	 * @param jsonResponse
	 * @return SKUDetailsResponseBean
	 */
	public SKUDetailsResponseBean convertSKUDetailsJSONTOBean(String jsonResponse) {
		LOGGER.info("Entry into convertSKUDetailsJSONTOBean method ");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SKUDetailsResponseBean skuDetailsResponse = new SKUDetailsResponseBean();
		flow = EndecaConstants.SKU_STRING;
		try {
			// Auto conversion of JSON string to Java bean
			skuDetailsResponse = mapper.readValue(jsonResponse, SKUDetailsResponseBean.class);
			LOGGER.debug(" SKU Details after Jackson Mapping : " + skuDetailsResponse.getSkuResponse());

			if (skuDetailsResponse.getSkuResponse() != null) {
				if (skuDetailsResponse.getSkuResponse().getStatus().equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)) {
					// To map the search results explicitly
					mapSKUDetailsJSONToResponse(skuDetailsResponse, jsonResponse);
				} else if (skuDetailsResponse.getSkuResponse().getStatus()
						.equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
					message = skuDetailsResponse.getSkuResponse().getStatusDetails().getMessages().get(0);
					throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
				}
			}

		} catch (EatonSystemException e) {
			// To set the failure message
			setFailureErrorlog(message,flow);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_019_STRING + EndecaConstants.HYPHEN + EndecaConstants.SKU_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN
					+ e);
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		}
		LOGGER.info("Exit from convertSKUDetailsJSONTOBean method ");

		return skuDetailsResponse;
	}

	/**
	 * To map the search results of the SKU details Response to the bean
	 * 
	 * @param skuDetailsResponse
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void mapSKUDetailsJSONToResponse(SKUDetailsResponseBean skuDetailsResponse, String jsonResponse)
			throws Exception {
		LOGGER.info("Entry into mapSKUListJSONToResponse method");
		JsonObject skuResponseJSON;
		try {
			SKUResponseBean skuResponse = skuDetailsResponse.getSkuResponse();
			skuResponseJSON = new JsonParser().parse(jsonResponse).getAsJsonObject();

			JsonObject response = skuResponseJSON.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
			JsonObject searchResults = response.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
			skuResponse.setTotalCount(searchResults.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt());

			JsonArray document = searchResults.get(EndecaConstants.DOCUMENT_STRING).getAsJsonArray();
			if (document != null) {
				for (int i = 0; i < document.size(); i++) {
					JsonObject contentObject = document.get(i).getAsJsonObject();
					JsonArray contentArray = contentObject.get(EndecaConstants.CONTENT_STRING).getAsJsonArray();
					List<SKUDetailsBean> skuDtlsList = new ArrayList<SKUDetailsBean>();
					SKUDetailsBean skuDtls = new SKUDetailsBean();
					for (int j = 0; j < contentArray.size(); j++) {
						JsonObject contentItem = contentArray.get(j).getAsJsonObject();
						String name = contentItem.get(EndecaConstants.NAME_STRING).getAsString();
						String value = contentItem.get(EndecaConstants.VALUE_STRING).getAsString();
						setSKUDetailValues(skuDtls,name,value);
						
					}
					skuDtlsList.add(skuDtls);
					skuResponse.setSkuDetailsList(skuDtlsList);
				}
			}
			skuDetailsResponse.setSkuResponse(skuResponse);

		} catch (Exception e) {
			skuDetailsResponse.getSkuResponse().setStatus(EndecaConstants.FAIL_STRING);
			throw new Exception(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		LOGGER.info("Exit from mapSKUListJSONToResponse method");
	}
	
	/**
	 * To map the search results of the SKU details Response to the bean
	 * 
	 * @param skuDetailsResponse
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void mapSKUDetailsJSONToResponseList(SKUDetailsResponseBean skuDetailsResponse, String jsonResponse)
			throws Exception {
		LOGGER.info("Entry into mapSKUListJSONToResponse method");
		JsonObject skuResponseJSON;
		try {
			SKUResponseBean skuResponse = skuDetailsResponse.getSkuResponse();
			skuResponseJSON = new JsonParser().parse(jsonResponse).getAsJsonObject();
			List<SKUDetailsBean> skuDtlsList = new ArrayList<SKUDetailsBean>();
			JsonObject response = skuResponseJSON.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
			JsonObject searchResults = response.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
			skuResponse.setTotalCount(searchResults.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt());
			JsonArray document = searchResults.get(EndecaConstants.DOCUMENT_STRING).getAsJsonArray();
			if (document != null) {
				for (int i = 0; i < document.size(); i++) {
					JsonObject contentObject = document.get(i).getAsJsonObject();
					JsonArray contentArray = contentObject.get(EndecaConstants.CONTENT_STRING).getAsJsonArray();
					SKUDetailsBean skuDtls = new SKUDetailsBean();
					for (int j = 0; j < contentArray.size(); j++) {
						JsonObject contentItem = contentArray.get(j).getAsJsonObject();
						String name = contentItem.get(EndecaConstants.NAME_STRING).getAsString();
						String value = contentItem.get(EndecaConstants.VALUE_STRING).getAsString();
						setSKUDetailValues(skuDtls,name,value);				
					}
					skuDtlsList.add(skuDtls);
				}	
				skuResponse.setSkuDetailsList(skuDtlsList);
			}
			skuDetailsResponse.setSkuResponse(skuResponse);
		} catch (Exception e) {
			skuDetailsResponse.getSkuResponse().setStatus(EndecaConstants.FAIL_STRING);
			throw new Exception(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		LOGGER.info("Exit from mapSKUListJSONToResponse method");
	}
	
	/**
	 * To set the SKU Details in the bean 
	 * @param skuDtls
	 * @param name
	 * @param value
	 */
	private void setSKUDetailValues(SKUDetailsBean skuDtls, String name,
			String value) {
		LOGGER.info("Entry into setSKUDetailValues method");
		
		String errMsg =  null;
		switch (name) {
		case EndecaConstants.INVENTORY_ID_STRING:
			skuDtls.setInventoryId(value);
			break;
		case EndecaConstants.EXTENSION_ID_STRING:
			skuDtls.setExtensionId(value);
			break;
		case EndecaConstants.BRAND_STRING:
			skuDtls.setBrand(value);
			break;
		case EndecaConstants.SUBBRAND_STRING:
			skuDtls.setSubBrand(value);
			break;
		case EndecaConstants.TRADE_NAME_STRING:
			skuDtls.setTradeName(value);
			break;
		case EndecaConstants.FAMILY_NAME_STRING:
			skuDtls.setFamilyName(value);
			break;
		case EndecaConstants.MKTG_DESC_STRING:
			skuDtls.setMktgDesc(value);
			break;
		case EndecaConstants.STATUS_STRING:
			skuDtls.setStatus(value);
			break;
		case EndecaConstants.GLOBAL_ATTRS_STRING:
			skuDtls.setGlobalAttrs(CommonUtil.convertXMLString(value));
			break;
		case EndecaConstants.TAXONOMY_ATTRS_STRING:
			skuDtls.setTaxonomyAttrs(CommonUtil.convertXMLString(value));
			break;
		case EndecaConstants.SKU_IMGS_STRING:
			skuDtls.setSkuImgs(CommonUtil.convertXMLString(value));
			break;
		case EndecaConstants.DOCUMENTS_STRING:
			skuDtls.setDocuments(CommonUtil.convertXMLString(value));
			break;
		case EndecaConstants.UPSELL_STRING:
			skuDtls.setUpSell(CommonUtil.convertXMLString(value));
			break;
		case EndecaConstants.CROSS_SELL_STRING:
			skuDtls.setCrossSell(CommonUtil.convertXMLString(value));
			break;
		case EndecaConstants.CID_STRING:
			skuDtls.setCid(CommonUtil.convertXMLString(value));
			break;
		case EndecaConstants.REPLACEMENT_STRING:
			skuDtls.setReplacement(CommonUtil.convertXMLString(value));
			break;
		case EndecaConstants.SERIAL_FLAG:
			if (StringUtils.isNotEmpty(value)) {
				skuDtls.setSerialFlag(Boolean.parseBoolean(value));
			}
			break;
		case EndecaConstants.SERIAL_AUTH_FLAG:
			if (StringUtils.isNotEmpty(value)) {
				skuDtls.setSerialAuthFlag(Boolean.parseBoolean(value));
			}
			break;
		case EndecaConstants.REP_EMAIL:
			if (StringUtils.isNotEmpty(value)) {
				skuDtls.setRepEmail(CommonUtil.convertXMLString(value));
			}
			break;
		case EndecaConstants.COUNTRY_CONSTANT:
			if (StringUtils.isNotEmpty(value)){
				skuDtls.setCountryList(value.split("\\|"));
			}
			break;
		default:
			errMsg = EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.SKU_STRING;
			LOGGER.debug(errMsg);
			break;
		}
		
		LOGGER.info("Exit from setSKUDetailValues method");
	}

	
	/**
	 * To convert the type Ahead Search results to java bean
	 * 
	 * @param jsonResponse
	 * @return TypeAheadSiteSearchResponse
	 */
	public TypeAheadSiteSearchResponse convertSearchKeywordsJSONTOBean(String jsonResponse, EndecaServiceRequestBean endecaServiceRequestBean) {
		LOGGER.info("Entry into convertSearchKeywordsJSONTOBean method ");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		TypeAheadSiteSearchResponse typeAheadResults = new TypeAheadSiteSearchResponse();
		flow = EndecaConstants.TYPE_AHEAD_STRING;
		try {
			// Auto conversion of JSON string to Java bean
			typeAheadResults = mapper.readValue(jsonResponse, TypeAheadSiteSearchResponse.class);
			
			JsonObject typeAheadResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();
			LOGGER.debug(" SKU Details after Jackson Mapping : " + typeAheadResults.getTypeAheadResponse());

			if(typeAheadResults.getTypeAheadResponse() !=null  && !CommonUtil.isBlankOrNull(typeAheadResults.getTypeAheadResponse().getStatus())){
				if(typeAheadResults.getTypeAheadResponse().getStatus().equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)){
					JsonObject response = typeAheadResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
					String results;
					if(endecaServiceRequestBean.getSearchApplication().equalsIgnoreCase(EndecaConstants.SECURE_APP_ID)) {
						results = response.get(EndecaConstants.SEARCH_RESULTS_STRING).toString();
					}else{
						results = response.get(EndecaConstants.RESULTS).getAsString();
					}
					if(!endecaServiceRequestBean.getSearchApplication().equalsIgnoreCase(EndecaConstants.SECURE_APP_ID)) {
						typeAheadResults.getTypeAheadResponse().setResultList(getSplitStringList(results));
					}else{
						typeAheadResults.getTypeAheadResponse().setResultList(getSecureResponseKeywords(results));
					}
				}else if (typeAheadResults
						.getTypeAheadResponse().getStatus().equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
					message = typeAheadResults.getTypeAheadResponse().getStatusDetails().getMessages().get(0);
					throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
				}
			}
		} catch (EatonSystemException e) {
			// To set the failure message
			setFailureErrorlog(message,flow);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_031_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.TYPE_AHEAD_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		}
		LOGGER.info("Exit from convertSearchKeywordsJSONTOBean method ");

		return typeAheadResults;
	}

	/**
	 *
	 * @param results
	 * @return Suggestions with secure attribute true/false
	 */
	private ArrayList<KeywordBean> getSecureResponseKeywords(String results) {
		JsonObject typeAheadResponse = new JsonParser().parse(results).getAsJsonObject();
		ArrayList<KeywordBean> resultList = new ArrayList<>();
		JsonArray documents = typeAheadResponse.getAsJsonArray(EndecaConstants.DOCUMENT_STRING);
		for(JsonElement document : documents){
			JsonObject documentObj = document.getAsJsonObject();
			JsonElement keywordElement= documentObj.getAsJsonArray(EndecaConstants.CONTENT_STRING).get(0);
			if(keywordElement!=null) {
				JsonObject keywordObj = keywordElement.getAsJsonObject();
				KeywordBean keywordBean = new KeywordBean();
				keywordBean.setTitle(keywordObj.get(EndecaConstants.VALUE_STRING).getAsString());
				keywordBean.setSecure(keywordObj.get(EndecaConstants.NAME_STRING).getAsString().contains("01"));
				resultList.add(keywordBean);
			}
		}
		return resultList;
	}

	/**
	 * To convert the site Search results to java bean
	 * 
	 * @param jsonResponse
	 * @return SiteSearchResponse
	 */
	public SiteSearchResponse convertSiteSearchResultsJSONTOBean(String jsonResponse, ResourceResolver resourceResolver, SlingHttpServletRequest  slingRequest, boolean isUnitedStatesDateFormat) {
		LOGGER.info("Inside convertSiteSearchResultsJSONTOBean method ");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SiteSearchResponse siteSearchResponse = new SiteSearchResponse();
		flow = EndecaConstants.SITE_SEARCH_STRING;
		try {
			// Auto conversion of JSON string to Java bean
			siteSearchResponse = mapper.readValue(jsonResponse, SiteSearchResponse.class);
			LOGGER.debug("Site Search results after Jackson Mapping : " + siteSearchResponse.getPageResponse());

			if (siteSearchResponse.getPageResponse() != null
					&& !CommonUtil.isBlankOrNull(siteSearchResponse.getPageResponse().getStatus())) {
				if (siteSearchResponse.getPageResponse().getStatus().equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)) {
					validateFacets(siteSearchResponse.getPageResponse().getFacets(),flow);
					
					setSiteSearchCounts(siteSearchResponse.getPageResponse());

					// To map search results explicitly
					mapSiteSearchListJSONToResponse(siteSearchResponse, jsonResponse, resourceResolver, slingRequest, isUnitedStatesDateFormat);

				} else if (!CommonUtil.isBlankOrNull(siteSearchResponse.getPageResponse().getStatus())
						&& siteSearchResponse.getPageResponse().getStatus()
								.equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
					message = siteSearchResponse.getPageResponse().getStatusDetails().getMessages().get(0);
					throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
				}

			}

		} catch (EatonSystemException e) {
			// To set the failure message
			setFailureErrorlog(message,flow);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_032_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.SITE_SEARCH_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		}
		LOGGER.info("Exit from convertSiteSearchResultsJSONTOBean method ");

		return siteSearchResponse;
	}

	/**
	 * To map the Search results of Site Search in JSON response to bean
	 * 
	 * @param familyBean
	 * @param jsonResponse
	 * @throws JSONException
	 * @throws JsonMappingException
	 */
	private void mapSiteSearchListJSONToResponse(SiteSearchResponse siteSearchResponse, String jsonResponse, ResourceResolver resourceResolver, SlingHttpServletRequest slingRequest, boolean isUnitedStatesDateFormat)
			throws Exception {
		LOGGER.info("Entry into mapSiteSearchListJSONToResponse method");
		JsonObject pageJSONResponse;

		try {
			SiteSearchPageResponse pageResponse = siteSearchResponse.getPageResponse();
			pageJSONResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();

			JsonObject response = pageJSONResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
			JsonObject searchResults = response.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
			
			pageResponse.setTotalCount(searchResults.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt());
			JsonArray document = searchResults.get(EndecaConstants.DOCUMENT_STRING).getAsJsonArray();

			if (pageResponse.getTotalCount() > 0 && document != null) {
				
				totalCount = pageResponse.getTotalCount();
				List<SiteSearchResultsBean> siteSearchResults = new ArrayList<>();
				SiteSearchResultsBean siteResults = null;
				for (int i = 0; i < document.size(); i++) {
					isValidEntry = true;
					JsonObject contentObject = document.get(i).getAsJsonObject();
					JsonArray contentArray = contentObject.get(EndecaConstants.CONTENT_STRING).getAsJsonArray();
					siteResults = new SiteSearchResultsBean();
					for (int j = 0; j < contentArray.size(); j++) {
						JsonObject contentItem = contentArray.get(j).getAsJsonObject();
						
						String name = contentItem.get(EndecaConstants.NAME_STRING).getAsString();
						String value = contentItem.get(EndecaConstants.VALUE_STRING).getAsString();
						isValidEntry = setSiteSearchResults(siteResults,name,value);
					}
					if(StringUtils.isNotEmpty(siteResults.getPublishDate())){
						getPublishDate(siteResults, isUnitedStatesDateFormat);
					}
					siteSearchFileType(siteResults);
					siteSearchResults.add(siteResults);
				}
				pageResponse.setSiteSearchResults(siteSearchResults);
				pageResponse.setTotalCount(totalCount);
			}
			siteSearchResponse.setPageResponse(pageResponse);

		} catch (Exception e) {
			throw new Exception(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		LOGGER.info("Exit from mapSiteSearchListJSONToResponse method");
	}

	private SiteSearchResultsBean siteSearchFileType (SiteSearchResultsBean siteResults){
		if((siteResults.getFileType() != null) && (siteResults.getDcFormat() != null)){
			if(FileTypesUtil.fileTypeMap().containsKey(siteResults.getDcFormat())){
				String finalValue = FileTypesUtil.fileTypeMap().get(siteResults.getDcFormat());
				siteResults.setFileType(finalValue);
			}
		}
		return siteResults;
	}
	
	private void getPublishDate(SiteSearchResultsBean siteResults, boolean isUnitedStatesDateFormat)  {
		LOGGER.info("Entry into getDateByLocale method");
		try {
			SimpleDateFormat publicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
			if(isUnitedStatesDateFormat) {
				publicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
			}
			Date date = new SimpleDateFormat(CommonConstants.DATE_FORMAT_PUBLISH).parse(siteResults.getPublishDate());
			Calendar dateVal = Calendar.getInstance();
			dateVal.setTime(date);
			siteResults.setPublishDate(CommonUtil.format(dateVal, publicationDateFormat));
		} catch (Exception e) {
			LOGGER.error("Exception while formatting Date from String object: {} " + e.getMessage());
		}
		LOGGER.info("Exit from getDateByLocale method");
	}
	
	private boolean setSiteSearchResults(SiteSearchResultsBean siteResults,
			String name, String value) {
		LOGGER.info("Entry into setSiteSearchResults method");
		String errMsg =  null;
		
		switch (name){
		case EndecaConstants.SECURE_STRING:
			if(!CommonUtil.isBlankOrNull(value)){
				siteResults.setSecure(value.toLowerCase().equals("yes"));
			}
		case  EndecaConstants.TITLE_STRING:
			if (!CommonUtil.isBlankOrNull(value)) {
				siteResults.setTitle(value);
			} else {
				isValidEntry = false;
				errMsg = EndecaConstants.ERR_ENDECA_032_STRING + EndecaConstants.HYPHEN
						+ EndecaConstants.SITE_SEARCH_STRING + EndecaConstants.HYPHEN
						+ EndecaConstants.INVALID_ENTRY_MSG + name;
			}
			break;
		case EndecaConstants.DESCRIPTION_STRING:
			siteResults.setDescription(value);
			break;
		case EndecaConstants.URL_STRING:
			if (CommonUtil.isBlankOrNull(value)) {
				siteResults.setUrl(StringUtils.EMPTY);
			} else {
				siteResults.setUrl(value);
			}
			break;
		case EndecaConstants.IMAGE_STRING:
			siteResults.setImage(value);
			break;
		case EndecaConstants.PUBLISH_DATE_STRING:
			siteResults.setPublishDate(value);
			break;
		case EndecaConstants.EPOCH_PUBLISH_DATE_STRING:
				siteResults.setEpochPublishDate(value);
				break;
		case EndecaConstants.FILE_TYPE_STRING:
			siteResults.setFileType(value);
			break;
		case EndecaConstants.FILE_SIZE_STRING:
			siteResults.setFileSize(value);
			break;
		case EndecaConstants.MIDDLE_TAB_NAME_STRING:
			siteResults.setMiddleTabName(value);
			break;
		case EndecaConstants.MIDDLE_TAB_URL_STRING:
			siteResults.setMiddleTabURL(value);
			break;
		case "trackDownload":
			siteResults.setTrackDownload(value);
			break;
		case EndecaConstants.CONTENT_TYPE_STRING:
			siteResults.setContentType(value);
			if (null !=value && !value.equals(EndecaConstants.PRODUCTS_STRING)
					&& CommonUtil.isBlankOrNull(siteResults.getUrl())) {
				isValidEntry = false;
				errMsg = EndecaConstants.ERR_ENDECA_032_STRING + EndecaConstants.HYPHEN
						+ EndecaConstants.SITE_SEARCH_STRING + EndecaConstants.HYPHEN
						+ EndecaConstants.INVALID_ENTRY_MSG + name;
			}
			break;
		case EndecaConstants.RENDITION_ONE_STRING:
			siteResults.setDsktopRendition(value);
			break;
		case EndecaConstants.RENDITION_TWO_STRING:
			siteResults.setMobileRendition(value);
			break;
		case EndecaConstants.STATUS_STRING:
			siteResults.setStatus(value);
			break;
		case EndecaConstants.LANGUAGE_STRING:
			siteResults.setLanguage(value);
			break;	
		case EndecaConstants.ECOMMERCE_PAGE_TYPE:
			if(!CommonUtil.isBlankOrNull(value)){
				siteResults.setEcommPageType(value);
			}
			break;
		case EndecaConstants.BUY_PAGE_REFERENCE:
			if(!CommonUtil.isBlankOrNull(value)){
				siteResults.setBuyPageReference(value);
			}
			break;
		case EndecaConstants.EATON_SHA:
			siteResults.setEatonSHA(value);
			break;
		case EndecaConstants.EATON_ECCN:
			siteResults.setEatonECCN(value);
			break;
		case EndecaConstants.DC_FORMAT_STRING:
			siteResults.setDcFormat(value);
			break;
		default :
			errMsg = EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN
			+ EndecaConstants.SITE_SEARCH_STRING;
			break;
		}
		
		if (!CommonUtil.isBlankOrNull(errMsg)) {
			LOGGER.debug(errMsg);
		}
		LOGGER.info("Exit from setSiteSearchResults method");
		return isValidEntry;
	}
	
	private void setsiteSearchPageCounts(FacetValueBean facetValueBean) {
		switch (facetValueBean.getFacetValueLabel()) {
		case EndecaConstants.PRODUCTS_STRING:
			productsCount = facetValueBean.getFacetValueDocs();
			break;
		case EndecaConstants.RESOURCES_STRING:
			resourcesCount = facetValueBean.getFacetValueDocs();
			break;
		case EndecaConstants.NEWS_STRING:
			newsCount = facetValueBean.getFacetValueDocs();
			break;
		case EndecaConstants.SERVICES_STRING:
			servicesCount = facetValueBean.getFacetValueDocs();
			break;
		default:
			LOGGER.info("Page counts not set for :" + facetValueBean.getFacetValueLabel());
			break;
		}
	}

	/**
	 * To validate the facet values in the response
	 * 
	 * @param facets
	 * @throws JSONException
	 */
	private void validateFacets(FacetsBean facets,String flow) {
		LOGGER.info("Entry into validateFacets");
		String errorMsg= null;
		String errorCode = fetchFacetsErrorCode(flow);
		if (facets != null) {
			List<FacetGroupBean> facetGroupList = facets.getFacetGroupList();
			if (!CommonUtil.isNullOrEmpty(facetGroupList)) {
				validateFacetGroupList(facetGroupList,errorCode,flow);
			} else {
				errorMsg = errorCode + EndecaConstants.HYPHEN
						+ EndecaConstants.FACET_GROUP_NULL_MSG;
			}
			if (!CommonUtil.isBlankOrNull(errorMsg)) {
				LOGGER.debug(errorMsg);
			}
		} else {
			LOGGER.info("Facets are null");
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("FACET VALUE MAP" + Arrays.asList(facetValueMap));
		}
		LOGGER.info("Exit from validateFacets");
	}
	
	/**
	 * Validate the facet groups
	 * @param facetGroupList
	 * @param errorCode
	 * @return error message
	 */
	private void  validateFacetGroupList(List<FacetGroupBean> facetGroupList,String errorCode,String flow){
		LOGGER.info("Entry into validateFacetGroupList method");
		String errorMsg = null;
		
		Iterator<FacetGroupBean> iter = facetGroupList.iterator();
		while (iter.hasNext()) {
			FacetGroupBean facetGroupBean = iter.next();
			if (!CommonUtil.isBlankOrNull(facetGroupBean.getFacetGroupLabel())) {
			  if (CommonUtil.isBlankOrNull(facetGroupBean.getFacetGroupId()) || CommonUtil.isNullOrEmpty(facetGroupBean.getFacetValueList())) {
				  LOGGER.info("Facet removed from the list :"+facetGroupBean.getFacetGroupLabel());
				  iter.remove();
					errorMsg = errorCode + EndecaConstants.HYPHEN
							+ EndecaConstants.INVALID_ENTRY_MSG + facetGroupBean.getFacetGroupLabel();
					LOGGER.debug(errorMsg);
				} else if (!CommonUtil.isNullOrEmpty(facetGroupBean.getFacetValueList()) && !CommonUtil.isBlankOrNull(facetGroupBean.getFacetGroupId())) {
					//To check the size of FacetValue list and remove the Facet Group if the size <=1  
					validateFacetValues(facetGroupBean.getFacetValueList(), flow, errorCode,
							facetGroupBean.getFacetGroupLabel());
				}
		  }
		}
		LOGGER.info("Exit from validateFacetGroupList method");
	}
	
	
	private void validateFacetValues(List<FacetValueBean> facetValList,String flow,String errorCode,String facetGroupLabel){
		LOGGER.info("Entry into validateFacetValues");
		String errorMsg = null;
		
		Iterator<FacetValueBean> iter = facetValList.iterator();
		while (iter.hasNext()) {
			FacetValueBean facetValueBean = iter.next();
			if (!CommonUtil.isBlankOrNull(facetValueBean.getFacetValueLabel())){
				if( CommonUtil.isBlankOrNull(facetValueBean.getFacetValueId())) {
					iter.remove();
					errorMsg = errorCode + EndecaConstants.HYPHEN
							+ EndecaConstants.FACET_VALUE_ID_NULL_MSG + facetValueBean.getFacetValueLabel();
					LOGGER.debug(errorMsg);
				}else{
					String facetLabel = facetValueBean.getFacetValueLabel();
					facetLabel = StringEscapeUtils.unescapeHtml(facetLabel);
					facetValueBean.setFacetValueLabel(facetLabel);
					
					facetValueMap.put(facetValueBean.getFacetValueId(), facetValueBean);
					if(flow.equalsIgnoreCase(EndecaConstants.SITE_SEARCH_STRING) && facetGroupLabel.equalsIgnoreCase(EndecaConstants.CONTENT_TYPE_FACET)) {
						setsiteSearchPageCounts(facetValueBean);
					}
				}	
			}else {
				iter.remove();
				errorMsg = errorCode + EndecaConstants.HYPHEN
						+ EndecaConstants.FACET_VALUE_NULL_MSG+facetGroupLabel;
				LOGGER.debug(errorMsg);
			}
		}
		LOGGER.info("Exit from into validateFacetValues");
	}
	
	private String fetchFacetsErrorCode(String flow){
		LOGGER.info("Entry into fetchFacetsErrorCode method");
		String errorCode = null;
		
		if (flow.equalsIgnoreCase(EndecaConstants.SUBCATEGORY_STRING)) {
			errorCode = EndecaConstants.ERR_ENDECA_021_STRING;
		} else if (flow.equalsIgnoreCase(EndecaConstants.FAMILY_STRING)) {
			errorCode = EndecaConstants.ERR_ENDECA_017_STRING;
		} else if (flow.equalsIgnoreCase(EndecaConstants.SITE_SEARCH_STRING)) {
			errorCode = EndecaConstants.ERR_ENDECA_032_STRING;
		}
		LOGGER.info("Exit from fetchFacetsErrorCode method");
		return errorCode;
	}
	

	/**
	 * To set the error code in Logs for Product Family
	 * 
	 */
	private void setFailureErrorlog(String message,String flow) {
		LOGGER.info("Entry into setFailureErrorlog  method ");

		String errorMsg = message.replaceAll("\\s+", " ");
		String logMsg = null;
		String errorCode = null;
		
		errorCode = fetchErrorcode(errorMsg,flow);
		
		logMsg = errorCode+EndecaConstants.HYPHEN + flow
				+ EndecaConstants.HYPHEN+ errorMsg;
		
		LOGGER.debug(logMsg);
		LOGGER.info("Exit from setFailureErrorlog  method ");
	}

	private ArrayList<KeywordBean> getSplitStringList(String results) {
		StringTokenizer st = new StringTokenizer(results, "||");
		ArrayList<KeywordBean> resultList = new ArrayList<>();
		while (st.hasMoreTokens()) {
			KeywordBean keywordBean = new KeywordBean();
			keywordBean.setTitle(st.nextToken().trim());
			resultList.add(keywordBean);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Result list of TypeAhead" + resultList);
		}

		return resultList;
	}
	/**
	 * To fetch the correct error code based on the error message  
	 * @param errorMessage
	 * @param flow
	 * @return
	 */
	private String fetchErrorcode(String errorMessage,String flow){
		LOGGER.info("Entry into fetchErrorcode method");
		String errorCode = null;
		String msg = null;
		
		String[] splitString = errorMessage.split(Pattern.quote("-"));
		msg = splitString[0].trim();
		msg = msg.toUpperCase();
		EndecaErrorCodeMap errorCodeMap = new EndecaErrorCodeMap();
		
		switch(flow){
		case EndecaConstants.SUBCATEGORY_STRING:
			errorCode = errorCodeMap.productFamilyErrorMsgMap.get(msg);
			break;
		case EndecaConstants.FAMILY_STRING:
			errorCode = errorCodeMap.familyModuleErrorMsgMap.get(msg);
			break;
		case EndecaConstants.SKU_STRING:
			errorCode = errorCodeMap.skuDetailsErrorMsgMap.get(msg);
			break;
		case EndecaConstants.TYPE_AHEAD_STRING:
			errorCode = errorCodeMap.typeAheadErrorMsgMap.get(msg);
			break;
		case EndecaConstants.SITE_SEARCH_STRING:
			errorCode = errorCodeMap.siteSearchErrorMsgMap.get(msg);
			break;
		case EndecaConstants.CLUTCH_STRING:
			errorCode = errorCodeMap.clutchSelectorErrorMsgMap.get(msg);
			break;
		case EndecaConstants.TORQUE_STRING:
			errorCode = errorCodeMap.torqueSelectorErrorMsgMap.get(msg);
			break;
		case EndecaConstants.CONTENTHUB_STRING:
			errorCode = errorCodeMap.contentHubErrorMsgMap.get(msg);
			break;
		default :
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No Matching error code");
			}
		 	break;
		}
		
		errorCode = CommonUtil.isBlankOrNull(errorCode)? EndecaConstants.ERR_ENDECA_022_STRING :errorCode;
		
		LOGGER.info("Exit from fetchErrorcode method");
		return errorCode;
	}
	
	private void setSiteSearchCounts(SiteSearchPageResponse pageResponse){
		
		pageResponse.setFacetValueMap(facetValueMap);
		pageResponse.setNewsCount(newsCount);
		pageResponse.setProductsCount(productsCount);
		pageResponse.setResourcesCount(resourcesCount);
		pageResponse.setServicesCount(servicesCount);
	}
	
	private void updateTotalCount(){
		
		totalCount = totalCount-1;
	}
	
	/**
	 * This method converts the json string to ClutchSelectorResponse
	 * @param jsonResponse
	 * @param filtervalue
	 * @return ClutchSelectorResponse
	 */
	public ClutchSelectorResponse convertClutchToolListJSONTOBean(String jsonResponse,JsonArray filtervalue) {

		LOGGER.info("Entry into convertClutchToolListJSONTOBean method");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ClutchSelectorResponse clutchListResponse = new ClutchSelectorResponse();
		flow = EndecaConstants.CLUTCH_STRING;
		
		try {
			// Auto conversion of JSON string to Java bean
			clutchListResponse = mapper.readValue(jsonResponse, ClutchSelectorResponse.class);

			if (clutchListResponse.getClutchToolResponse() != null
					&& !CommonUtil.isBlankOrNull(clutchListResponse.getClutchToolResponse().getStatus())) {
				if (clutchListResponse.getClutchToolResponse().getStatus()
						.equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)) {
					// validate facet values
					validateFacets(clutchListResponse.getClutchToolResponse().getFacets(),flow);
					
					clutchListResponse.getClutchToolResponse().setFacetValueMap(facetValueMap);

					// To Map the search results explicitly
					mapClutchListJSONToResponse(clutchListResponse, jsonResponse,filtervalue);
				} else if (clutchListResponse.getClutchToolResponse().getStatus()
						.equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
					message = clutchListResponse.getClutchToolResponse().getStatusDetails().getMessages().get(0);
					throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
				}
			}

		} catch (EatonSystemException e) {
			// To set the failure message
			setFailureErrorlog(message,flow);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_059_STRING + EndecaConstants.HYPHEN + EndecaConstants.CLUTCH_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		}
		LOGGER.info("End Of convertClutchToolListJSONTOBean method ");
		return clutchListResponse;
	}	
	
	/**
	 * To map the search results in Clutch list response to bean
	 * 
	 * @param clutchList
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void mapClutchListJSONToResponse(ClutchSelectorResponse clutchList, String jsonResponse,JsonArray filtervalue) throws Exception {
		LOGGER.info("Inside mapClutchListJSONToResponse method");
		JsonObject clutchListResponse;

		try {
			ClutchToolResponse clutchToolResponse = clutchList.getClutchToolResponse();
			clutchListResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();

			JsonObject response = clutchListResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
			JsonObject searchResults = response.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
			clutchToolResponse.setTotalCount(searchResults.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt());

			JsonArray document = searchResults.get(EndecaConstants.DOCUMENT_STRING).getAsJsonArray();
			if (clutchToolResponse.getTotalCount() > 0 && document != null) {
				totalCount =clutchToolResponse.getTotalCount();
				 List<ClutchTool> clutchToolList = getClutchToolList(document,filtervalue);
				 clutchToolResponse.setClutchSearchResults(clutchToolList);
				 clutchToolResponse.setTotalCount(totalCount);
			}else{
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(EndecaConstants.NO_RESULTS);
				}
			}

			clutchList.setClutchToolResponse(clutchToolResponse);

		} catch (Exception e) {
			clutchList.getClutchToolResponse().setStatus(EndecaConstants.FAIL_STRING);
			throw new Exception(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		LOGGER.info("End of mapClutchListJSONToResponse method");
	}
	
	/**
	 * To fetch the  clutchTool list  from the JSON response
	 * @param document
	 * @param filtervalue
	 * @return
	 * @throws JSONException
	 */
	private List<ClutchTool> getClutchToolList(JsonArray document, JsonArray filtervalue) throws Exception {
		LOGGER.info("Entry into getClutchToolList method ");
		List<ClutchTool> clutchToolList = new ArrayList<>();
		for (int i = 0; i < document.size(); i++) {
			isValidEntry = true;
			JsonObject contentObject = document.get(i).getAsJsonObject();
			JsonArray contentArray = contentObject.get(EndecaConstants.CONTENT_STRING).getAsJsonArray();
			ClutchTool clutchTool = new ClutchTool();
			isValidEntry =  setClutchToolValues(contentArray,clutchTool,filtervalue);
			
			if (isValidEntry && !CommonUtil.isBlankOrNull(clutchTool.getCatalogNumber())) {
				clutchToolList.add(clutchTool);
			} else {
				updateTotalCount();
			}
		}
			
		LOGGER.info("Exit from getClutchToolList method ");
		return clutchToolList;
	}
	
	/**
	 * To set the clutch tool values in the bean
	 * @param contentArray
	 * @param ClutchTool
	 * @param filtervalue
	 * @return
	 * @throws JSONException
	 */
	private boolean setClutchToolValues(JsonArray contentArray,
			ClutchTool clutchTool, JsonArray filtervalue) throws Exception {
		LOGGER.info("Entry into setClutchToolValues method ");
		
		List<SKUCardParameterBean> skuCardList = new ArrayList<>();
		String errorLogMsg = null;
		
		for (int j = 0; j < contentArray.size(); j++) {
			JsonObject contentItem = contentArray.get(j).getAsJsonObject();

			String name = contentItem.get(EndecaConstants.NAME_STRING).getAsString();
			String value = contentItem.get(EndecaConstants.VALUE_STRING).getAsString();
			
			switch (name){
			case  EndecaConstants.CATALOG_NUMBER_STRING:
				if (!CommonUtil.isBlankOrNull(value)) {
					clutchTool.setCatalogNumber(value);
				} else {
					isValidEntry = false;
					errorLogMsg = EndecaConstants.ERR_ENDECA_021_STRING + EndecaConstants.HYPHEN
							+ EndecaConstants.FAMILY_STRING + EndecaConstants.HYPHEN + name;
					LOGGER.debug(errorLogMsg);
				}
				break;
			case EndecaConstants.PRODUCT_NAME:
				clutchTool.setProductName(value);
				break;
			case EndecaConstants.LONG_DESC_STRING:
				clutchTool.setLongDesc(value);
				break;
			case EndecaConstants.PRIMARY_IMAGE_STRING:
				clutchTool.setPrimaryImage(value);
				break;
			case EndecaConstants.IMAGE_ONE:
				clutchTool.setDsktopRendition(value);
				break;
			case EndecaConstants.IMAGE_TWO:
				clutchTool.setMobileRendition(value);
				break;
			case EndecaConstants.CLUTCH_SIZE:
				clutchTool.setClutchSize(value);
				break;
			case EndecaConstants.DAMPER_SPRING_COUNT:
				clutchTool.setPreDamper(value);
				break;
			case EndecaConstants.TORQUE_CAPACITY:
				clutchTool.setTorqueCapacity(value);
				break;
			case EndecaConstants.INPUT_SHAFT_SIZE:
				clutchTool.setInputShaftSize(value);
				break;
			case EndecaConstants.PRE_DAMPER:
				clutchTool.setPreDamper(value);
				break;
			case EndecaConstants.QLTY_DESIGNATION:
				clutchTool.setQualityDesignation(value);
				break;
			default :
				for (int index = 0; index < filtervalue.size(); index++) {
					if (filtervalue.get(index).getAsString().equalsIgnoreCase(name) && !CommonUtil.isBlankOrNull(value)
							&& skuCardList.size()<3	&& value.contains("||")) {
						SKUCardParameterBean skuCardParameters = new SKUCardParameterBean();
						String[] splitString = value.split(Pattern.quote("||"));
						if(splitString.length>=2) {
							skuCardParameters.setLabel(splitString[0]);
							skuCardParameters.setSkuCardValues(StringEscapeUtils.unescapeHtml(splitString[1]));
						}
						if(name.equalsIgnoreCase(EndecaConstants.RUNTIME_GRAPH)){
							skuCardParameters.setRuntimeGraphURL(EndecaConstants.RUNTIME_GRAPH_URL + clutchTool.getCatalogNumber());
						}
						skuCardList.add(skuCardParameters);
					}
				}
				break;
			}
		}
		clutchTool.setSkuCardParameters(skuCardList);
		
		LOGGER.info("Exit from setClutchToolValues method ");
		return isValidEntry;
	}
	
	/**
	 * This method converts the json string to TorqueSelectorResponse
	 * @param jsonResponse
	 * @param filtervalue
	 * @return TorqueSelectorResponse
	 */
	public TorqueSelectorResponse convertTorqueToolListJSONTOBean(String jsonResponse,JsonArray filtervalue) {

		LOGGER.info("Entry into convertTorqueToolListJSONTOBean method");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		TorqueSelectorResponse torqueListResponse = new TorqueSelectorResponse();
		flow = EndecaConstants.TORQUE_STRING;
		
		try {
			// Auto conversion of JSON string to Java bean
			torqueListResponse = mapper.readValue(jsonResponse, TorqueSelectorResponse.class);

			if (torqueListResponse.getTorqueToolResponse() != null
					&& !CommonUtil.isBlankOrNull(torqueListResponse.getTorqueToolResponse().getStatus())) {
				if (torqueListResponse.getTorqueToolResponse().getStatus()
						.equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)) {
					// validate facet values
					validateFacets(torqueListResponse.getTorqueToolResponse().getFacets(),flow);
					
					torqueListResponse.getTorqueToolResponse().setFacetValueMap(facetValueMap);

					// To Map the search results explicitly
					mapTorqueListJSONToResponse(torqueListResponse, jsonResponse,filtervalue);
				} else if (torqueListResponse.getTorqueToolResponse().getStatus()
						.equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
					message = torqueListResponse.getTorqueToolResponse().getStatusDetails().getMessages().get(0);
					throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
				}
			}

		} catch (EatonSystemException e) {
			// To set the failure message
			setFailureErrorlog(message,flow);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_060_STRING + EndecaConstants.HYPHEN + EndecaConstants.TORQUE_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage());
		}
		LOGGER.info("End Of convertTorqueToolListJSONTOBean method ");
		return torqueListResponse;
	}	
	
	/**
	 * To map the search results in torque list response to bean
	 * 
	 * @param torqueList
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void mapTorqueListJSONToResponse(TorqueSelectorResponse torqueList, String jsonResponse,JsonArray filtervalue) throws Exception {
		LOGGER.info("Inside mapTorqueListJSONToResponse method");
		JsonObject torqueListResponse;

		try {
			TorqueToolResponse torqueToolResponse  = torqueList.getTorqueToolResponse();
			torqueListResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();

			JsonObject response = torqueListResponse.get(EndecaConstants.RESPONSE_STRING).getAsJsonObject();
			JsonObject searchResults = response.get(EndecaConstants.SEARCH_RESULTS_STRING).getAsJsonObject();
			torqueToolResponse.setTotalCount(searchResults.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt());

			JsonArray document = searchResults.get(EndecaConstants.DOCUMENT_STRING).getAsJsonArray();
			if (torqueToolResponse.getTotalCount() > 0 && document != null) {
				totalCount =torqueToolResponse.getTotalCount();
				 List<TorqueTool> torqueToolList = getTorqueToolList(document,filtervalue);
				 torqueToolResponse.setTorqueSearchResults(torqueToolList);
				 torqueToolResponse.setTotalCount(totalCount);
			}else{
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(EndecaConstants.NO_RESULTS);
				}
			}

			torqueList.setTorqueToolResponse(torqueToolResponse);

		} catch (Exception e) {
			torqueList.getTorqueToolResponse().setStatus(EndecaConstants.FAIL_STRING);
			throw new Exception(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		LOGGER.info("End of mapTorqueListJSONToResponse method");
	}
	
	/**
	 * To fetch the torque tool list  from the JSON response
	 * @param document
	 * @param filtervalue
	 * @return
	 * @throws JSONException
	 */
	private List<TorqueTool> getTorqueToolList(JsonArray document,JsonArray filtervalue) throws Exception{
		LOGGER.info("Entry into getTorqueToolList method ");
		List<TorqueTool> torqueToolList = new ArrayList<>();
		for (int i = 0; i < document.size(); i++) {
			isValidEntry = true;
			JsonObject contentObject = document.get(i).getAsJsonObject();
			JsonArray contentArray = contentObject.get(EndecaConstants.CONTENT_STRING).getAsJsonArray();
			TorqueTool torqueTool = new TorqueTool();
			isValidEntry =  setTorqueToolValues(contentArray,torqueTool,filtervalue);
			
			if (isValidEntry && !CommonUtil.isBlankOrNull(torqueTool.getCatalogNumber())) {
				torqueToolList.add(torqueTool);
			} else {
				updateTotalCount();
			}
		}
			
		LOGGER.info("Exit from getTorqueToolList method ");
		return torqueToolList;
	}
	
	/**
	 * To set the torque tool values in the bean
	 * @param contentArray
	 * @param TorqueTool
	 * @param filtervalue
	 * @return
	 * @throws JSONException
	 */
	private boolean setTorqueToolValues(JsonArray contentArray,TorqueTool torqueTool,JsonArray filtervalue) throws Exception{
		LOGGER.info("Entry into setTorqueToolValues method ");
		
		List<SKUCardParameterBean> skuCardList = new ArrayList<>();
		String errorLogMsg = null;
		
		for (int j = 0; j < contentArray.size(); j++) {
			JsonObject contentItem = contentArray.get(j).getAsJsonObject();

			String name = contentItem.get(EndecaConstants.NAME_STRING).getAsString();
			String value = contentItem.get(EndecaConstants.VALUE_STRING).getAsString();
			
			switch (name){
			case  EndecaConstants.CATALOG_NUMBER_STRING:
				if (!CommonUtil.isBlankOrNull(value)) {
					torqueTool.setCatalogNumber(value);
				}else {
					isValidEntry = false;
					errorLogMsg = EndecaConstants.ERR_ENDECA_021_STRING + EndecaConstants.HYPHEN
							+ EndecaConstants.FAMILY_STRING + EndecaConstants.HYPHEN + name;
					LOGGER.debug(errorLogMsg);
				}
				break;
			case EndecaConstants.LONG_DESC_STRING:
				torqueTool.setLongDesc(value);
				break;
			case EndecaConstants.IMAGE_ONE:
				torqueTool.setDsktopRendition(value);
				break;
			case EndecaConstants.IMAGE_TWO:
				torqueTool.setMobileRendition(value);
				break;
			case EndecaConstants.AXLE_STRING:
				torqueTool.setAxleInfo(value);
				break;
			case EndecaConstants.SPLINE_COUNT_STRING:
				torqueTool.setSplineCount(value);
				break;
			default :
				for (int index = 0; index < filtervalue.size(); index++) {
					if (filtervalue.get(index).getAsString().equalsIgnoreCase(name) && skuCardList.size()<3
							&& !CommonUtil.isBlankOrNull(value) && value.contains("||")) {
						SKUCardParameterBean skuCardParameters = new SKUCardParameterBean();
						String[] splitString = value.split(Pattern.quote("||"));
						if(splitString.length>=2) {
							skuCardParameters.setLabel(splitString[0]);
							skuCardParameters.setSkuCardValues(StringEscapeUtils.unescapeHtml(splitString[1]));
						}
						if(name.equalsIgnoreCase(EndecaConstants.RUNTIME_GRAPH)){
							skuCardParameters.setRuntimeGraphURL(EndecaConstants.RUNTIME_GRAPH_URL + torqueTool.getCatalogNumber());
						}
						skuCardList.add(skuCardParameters);

					}
				}
				break;
			}
		}
		torqueTool.setSkuCardParameters(skuCardList);
		
		LOGGER.info("Exit from setTorqueToolValues method ");
		return isValidEntry;
	}

	/**
	 * To convert the JSON Response
	 *
	 * @param jsonResponse
	 * @return Submittal Builder family List
	 * @throws Exception
	 */
	public JsonObject convertSubmitBuilderListJSON(final String jsonResponse) throws Exception {
		LOGGER.info("Entry into convertSubmitBuilderListJSONTOBean method ");
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		FamilyListResponseBean familyList = new FamilyListResponseBean();
		JsonObject submitalBuilderResponse = null;
		try {
			familyList = mapper.readValue(jsonResponse, FamilyListResponseBean.class);
			if (StringUtils.isNotBlank(jsonResponse) && StringUtils.isNotBlank(familyList.getResponse().getStatus())) {
				if (familyList.getResponse().getStatus().equalsIgnoreCase(EndecaConstants.SUCCESS_STRING)) {
					submitalBuilderResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();
				} else if (familyList.getResponse().getStatus().equalsIgnoreCase(EndecaConstants.FAIL_STRING)) {
					message = familyList.getResponse().getStatusdetails().getMessages().get(0);
					throw new EatonSystemException(EndecaConstants.ERR_ENDECA_FAIL_STRING, message);
				}
			}
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_021_STRING + EndecaConstants.HYPHEN
					+ EndecaConstants.INVALID_ENTRY_MSG + EndecaConstants.HYPHEN + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(EndecaConstants.ERR_ENDECA_022_STRING + EndecaConstants.HYPHEN + EndecaConstants.SYS_ERR_STRING
					+ EndecaConstants.HYPHEN + EndecaConstants.TECHNICAL_ISSUE_ERROR_MSG + EndecaConstants.HYPHEN
					+ e.getMessage() + e);
		}
		LOGGER.info("Exit from convertSubmitBuilderListJSONTOBean method ");
		return submitalBuilderResponse;
	}

}

