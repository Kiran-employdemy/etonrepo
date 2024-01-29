package com.eaton.platform.core.vgselector.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.vgSelector.ConfigModel;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.services.EndecaConfig;

/**
 * <html> Description: This class is a util class that contains the generic endeca request setting methods which is being used by 
 * VGProductSelectorHelper.java and VGProductSelectorServlet.java classes</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2018
 */
public final class VGSelectorUtil {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(VGSelectorUtil.class);
	
	/** The Constant SITE_CLOUD_CONFIG_NODE_NAME. */
	private static final String COUNTRY_CONSTANT = "Country";
	
	/** The Constant RETURN_FACETS_FOR. */
	private static final String RETURN_FACETS_FOR = "ReturnFacetsFor";
	
	/** The Constant FACET_VALUES. */
	private static final String FACET_VALUES = "Facets";
	
	/** The Constant SORT_BY. */
	private static final String SORT_BY = "SortBy";
	
	/** The Constant SEARCH. */
	private static final String SEARCH = "search";
	
	/** The return facets for. */
	private static List<String> returnFacetsFor;
	
	/** The sku card parameters. */
	private static List<String> skuCardParameters;
	
	/** The facet values. */
	private static List<String> facetValues;
	
	/** The country. */
	private static String country;
	
	/** The sort type. */
	private static String sortType;
	
	/**
	 * Instantiates a new common util.
	 */
	private VGSelectorUtil() {
		LOGGER.debug("Inside CommonUtil constructor");
	}

	/**
	 * Populate endeca service request bean.
	 *
	 * @param currentRes the current res
	 * @param resourceResolver the resource resolver
	 * @param endecaConfigService the endeca config service
	 * @param endecaServiceRequestBean the endeca service request bean
	 * @param selectorToolType the selector tool type
	 * @param searchTerm the search term
	 * @param returnFacets the return facets
	 * @param skuCards the sku cards
	 * @param facetsValues the facets values
	 * @param countryCode the country code
	 * @param countInt the count int
	 * @param pageSize the page size
	 * @param sortOption the sort option
	 * @return the endeca service request bean
	 */
	public static EndecaServiceRequestBean populateEndecaServiceRequestBean(Resource currentRes, ResourceResolver resourceResolver, EndecaConfig endecaConfigService, EndecaServiceRequestBean endecaServiceRequestBean, String selectorToolType, String searchTerm, List<String> returnFacets, List<String> skuCards, List<String> facetsValues, String countryCode, int countInt, int pageSize, String sortOption) {
		
		LOGGER.debug("VGSelectorUtil :: populateEndecaServiceRequestBean() :: Started");
		EndecaConfigServiceBean endecaConfigBean = null;
		
		returnFacetsFor = returnFacets;
		facetValues = facetsValues;
		country = countryCode;
		skuCardParameters = skuCards;
		sortType = sortOption;
		if (endecaConfigService != null) {
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
		}
		// Setting common attributes of Endeca Request Bean
		if (endecaConfigBean != null) {
			endecaServiceRequestBean.setSearchApplicationKey(endecaConfigBean.getEspAppKey());
		}
		
		endecaServiceRequestBean.setFunction(SEARCH);
		endecaServiceRequestBean.setSearchTerms(searchTerm);

		// Populated the EndecaRequestBean for sub-category page
		if ((null != selectorToolType) && (null != endecaConfigBean) && selectorToolType.equals(VGCommonConstants.CLUTCH_PAGE)) {
			endecaServiceRequestBean.setSearchApplication(endecaConfigBean.getClutchSelctorAppName());
			populateClutchToolEndecaRequestBean(resourceResolver, endecaServiceRequestBean,searchTerm,countInt,pageSize);
		} else if ((null != selectorToolType) && (null != endecaConfigBean) && selectorToolType.equals(VGCommonConstants.TORQUE_PAGE)) {
			endecaServiceRequestBean.setSearchApplication(endecaConfigBean.getTorqueSelectorAppName());
			populateTorqueToolEndecaRequestBean(resourceResolver,endecaServiceRequestBean,searchTerm,countInt,pageSize);
		} 
		
		LOGGER.debug("VGSelectorUtil :: populateEndecaServiceRequestBean() :: Ended");
		return endecaServiceRequestBean;
	}

	/**
	 * Populate clutch tool endeca request bean.
	 *
	 * @param resourceResolver the resource resolver
	 * @param endecaServiceRequestBean the endeca service request bean
	 * @param searchTerm the search term
	 * @param countInt the count int
	 * @param pageSize the page size
	 */
	public static void populateClutchToolEndecaRequestBean(ResourceResolver resourceResolver,EndecaServiceRequestBean endecaServiceRequestBean, String searchTerm, int countInt, int pageSize) {
		LOGGER.debug("VGSelectorUtil :: populateClutchToolEndecaRequestBean() :: Started");
		List<FilterBean> filters = new ArrayList<>();
		FilterBean filterBean = new FilterBean();
		List<String> filterValues = new ArrayList<>();
	
		//Setting Facets
		filterBean = new FilterBean();
		filterBean.setFilterName(FACET_VALUES);
		filterValues = new ArrayList<>();
		if(filterValues.isEmpty()){
			filterValues.add(StringUtils.EMPTY);
		}
		filterValues = facetValues;
		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);
		
		// Setting startingRecordNumber
		String countString = Integer.toString(countInt);
		if(countString != null && StringUtils.isNotEmpty(countString)){
			endecaServiceRequestBean.setStartingRecordNumber(countString);
		}else{
		endecaServiceRequestBean.setStartingRecordNumber(VGCommonConstants.ZERO);
		}

		//Setting NumberOfRecordsToReturn for Clutch Results Page 
		String pageSizeStr = Integer.toString(pageSize);
		if(StringUtils.isNotBlank(pageSizeStr)){
			endecaServiceRequestBean.setNumberOfRecordsToReturn(pageSizeStr);
		}
		
		setEndecaRequestFilter(filters, returnFacetsFor, RETURN_FACETS_FOR);
					
		//Setting Country
		filterBean = new FilterBean();
		filterBean.setFilterName(COUNTRY_CONSTANT);
		filterValues = new ArrayList<>();
			filterValues.add(country);
		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);
	
		if(StringUtils.equals(VGCommonConstants.ENDECA_RESULT, searchTerm)) {
			//Setting SKUCardParameters
			setEndecaRequestFilter(filters, skuCardParameters, VGCommonConstants.SKU_CARD_PARAMETERS);
		}
			
		// Setting Facet List
		endecaServiceRequestBean.setFilters(filters);
		LOGGER.debug("VGSelectorUtil :: populateClutchToolEndecaRequestBean() :: Ended");
	}
	
	/**
	 * Populate torque tool endeca request bean.
	 *
	 * @param resourceResolver the resource resolver
	 * @param endecaServiceRequestBean the endeca service request bean
	 * @param searchTerm the search term
	 * @param countInt the count int
	 * @param pageSize the page size
	 */
	public static void populateTorqueToolEndecaRequestBean(ResourceResolver resourceResolver,EndecaServiceRequestBean endecaServiceRequestBean, String searchTerm, int countInt, int pageSize) {
		LOGGER.debug("VGSelectorUtil :: populateTorqueToolEndecaRequestBean() :: Started");
		List<FilterBean> filters = new ArrayList<>();
		FilterBean filterBean = new FilterBean();
		List<String> filterValues = new ArrayList<>();

		// Setting startingRecordNumber
		String countString = Integer.toString(countInt);
		if(countString != null && StringUtils.isNotEmpty(countString)){
			endecaServiceRequestBean.setStartingRecordNumber(countString);
		}else{
		endecaServiceRequestBean.setStartingRecordNumber(VGCommonConstants.ZERO);
		}

		//Setting NumberOfRecordsToReturn for Clutch Results Page 
		String pageSizeStr = Integer.toString(pageSize);
		if(StringUtils.isNotBlank(pageSizeStr)){
			endecaServiceRequestBean.setNumberOfRecordsToReturn(pageSizeStr);
		}
		
		//Setting Return Facets for Torque Results Page 
		setEndecaRequestFilter(filters, returnFacetsFor, RETURN_FACETS_FOR);		
		
		//Setting Country
		filterBean = new FilterBean();
		filterBean.setFilterName(COUNTRY_CONSTANT);
		filterValues = new ArrayList<>();
		filterValues.add(country);
		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);
		
		//Setting SortBy for Torque Results Page 
		filterBean = new FilterBean();
		filterBean.setFilterName(SORT_BY);
		filterValues = new ArrayList<>();
		filterValues.add(sortType);
		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);
		
		if(StringUtils.equals(VGCommonConstants.ENDECA_RESULT, searchTerm)){
			
			//Setting SKUCardParameters
			setEndecaRequestFilter(filters, skuCardParameters, VGCommonConstants.SKU_CARD_PARAMETERS);
		}
		
		List<String> deleteFacets = new ArrayList<>();
		for(String facetValue : facetValues) {
			List<String> vehicleInfo = new ArrayList<>();
			if(StringUtils.endsWith(facetValue, VGCommonConstants.MAKE)) {
				vehicleInfo.add(StringUtils.removeEnd(facetValue, VGCommonConstants.MAKE));
				deleteFacets.add(facetValue);
				// Setting Make
				setEndecaRequestFilter(filters, vehicleInfo, VGCommonConstants.MAKE);
			} else if(StringUtils.endsWith(facetValue, VGCommonConstants.MODEL)) {
				vehicleInfo.add(StringUtils.removeEnd(facetValue, VGCommonConstants.MODEL));
				deleteFacets.add(facetValue);
				// Setting Model
				setEndecaRequestFilter(filters, vehicleInfo, VGCommonConstants.MODEL);
			}else if(StringUtils.endsWith(facetValue, VGCommonConstants.YEAR)) {
				vehicleInfo.add(StringUtils.removeEnd(facetValue, VGCommonConstants.YEAR));
				deleteFacets.add(facetValue);
				// Setting Year
				setEndecaRequestFilter(filters, vehicleInfo, VGCommonConstants.YEAR);
			}
		}
		filterValues = new ArrayList<String>();
		CollectionUtils.addAll(filterValues, facetValues);

		for(String facetValue : deleteFacets) {
			filterValues.remove(facetValue);
		}
		if(filterValues.isEmpty()){
			filterValues.add(StringUtils.EMPTY);
		}
		
		//Setting Facets
		filterBean = new FilterBean();
		filterBean.setFilterName(FACET_VALUES);
		if(filterValues.isEmpty()){
			filterValues.add(StringUtils.EMPTY);
		}

		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);
		
		// Setting Facet List
		endecaServiceRequestBean.setFilters(filters);
		LOGGER.debug("VGSelectorUtil :: populateTorqueToolEndecaRequestBean() :: Started");		
	}

	/**
	 * Sets the endeca request filter.
	 *
	 * @param filters the filters
	 * @param vehicleInfo the vehicle info
	 * @param filterName the filter name
	 */
	private static void setEndecaRequestFilter(List<FilterBean> filters,
			List<String> vehicleInfo, String filterName) {
		FilterBean filterBean;
		List<String> filterValues;
		filterBean = new FilterBean();
		filterBean.setFilterName(filterName);
		filterValues = new ArrayList<>();
		filterValues = vehicleInfo;
		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);
	}

	/**
	 * Gets the form element names.
	 *
	 * @param currentRes the current res
	 * @param resourceResolver the resource resolver
	 * @param searchTerm the search term
	 * @param selectorType the selector type
	 * @return the form element names
	 */
	public static List<String> getFormElementNames(Resource currentRes, ResourceResolver resourceResolver,String searchTerm, String selectorType){
		LOGGER.debug("VGSelectorUtil :: getFormElementNames() :: Start");

		List<String> returnFacetsFor = new ArrayList<>();
		Resource formResource = resourceResolver.getResource(currentRes.getPath().concat(VGCommonConstants.FORM_ELEMENTS_PATH));
		if(null != formResource) {
			Iterator<Resource> formElements = formResource.listChildren();

				while(formElements.hasNext()){
			        Resource formPanelElements = formElements.next();
			        Iterator<Resource> panelElement = formPanelElements.listChildren();
			        while(panelElement.hasNext()) {
			        	Resource panelChildren = panelElement.next();
			        	if(null != panelChildren && StringUtils.equals(VGCommonConstants.ITEMS, panelChildren.getName())) {
			        		Iterator<Resource> elements = panelChildren.listChildren();
							while(elements.hasNext()){
						        Resource formElement = elements.next();
						        if(null != formElement) {
						        	ValueMap formPropeties = formElement.getValueMap();
						        	if(StringUtils.equals(VGCommonConstants.FORM_DROPDOWN_ELEMENT, formPropeties.get(VGCommonConstants.SLING_RESOURCE_TYPE_PROPERTY).toString())) {
						        		returnFacetsFor.add(formPropeties.get(VGCommonConstants.PROPERTY_NAME_FIELD).toString());
						        	}
						        }
							}
			        	}
			        }
			   }
		}
		if(searchTerm.equalsIgnoreCase("RESULT")){
			//Awaiting path on form button click to take additional facets and 
		}
		LOGGER.debug("VGSelectorUtil :: getFormElementNames() :: Exit");
		return returnFacetsFor;
	}
	
	/**
	 * Gets the current page properties.
	 *
	 * @param currentRes the current res
	 * @return the current page properties
	 */
	public static ValueMap getCurrentPageProperties(Resource currentRes) {
		LOGGER.debug("VGSelectorUtil :: getCurrentPageProperties() :: Started");
		
		ValueMap currentPageProperties = null;
		if(null != currentRes) {
			currentPageProperties = currentRes.getValueMap();
		}
		LOGGER.debug("VGSelectorUtil :: getCurrentPageProperties() :: Ended");
		return currentPageProperties;
	}
	
	
	/**
	 * Populate site configuration.
	 *
	 * @param configFactory the config factory
	 * @param adminResourceResolver the admin resource resolver
	 * @param currentPageResourse the current page resourse
	 * @return the config model
	 */
	public static ConfigModel populateSiteConfiguration(ConfigurationManagerFactory configFactory, ResourceResolver adminResourceResolver, Resource currentPageResourse) {
		
		LOGGER.debug("Entry into populateSiteConfiguration method");

		Resource siteCloudConfigRes = null;
		ConfigModel siteResourceSlingModel = null;

		Configuration configObj = CommonUtil.getCloudConfigObj(configFactory, adminResourceResolver,
				currentPageResourse, VGCommonConstants.VG_SITE_CLOUD_CONFIG_NODE_NAME);

		if (null != configObj) {
			siteCloudConfigRes = adminResourceResolver.resolve(configObj.getPath().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_STR));
			siteResourceSlingModel = siteCloudConfigRes.adaptTo(ConfigModel.class);
		}
		
		LOGGER.debug("Exit from populateSiteConfiguration method");
		return siteResourceSlingModel;
	}
	

}