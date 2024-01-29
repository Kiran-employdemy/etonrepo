package com.eaton.platform.core.vgselector.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.models.vgSelector.ConfigModel;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.core.vgselector.models.AdditionalFacetsModel;
import com.eaton.platform.core.vgselector.models.ReturnFacetsAxleModel;
import com.eaton.platform.core.vgselector.models.ReturnFacetsModel;
import com.eaton.platform.core.vgselector.models.ReturnFacetsVehicleModel;
import com.eaton.platform.core.vgselector.models.SKUcardAttributesModel;

/**
 * <html> Description: This class is a util class that contains the method to get the values of VG Selector Configuration from dialog to java file.
 * 
 * @author TCS
 * @version 1.0
 * @since 2018
 */
public final class VGSiteConfigUtil {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(VGSiteConfigUtil.class);
	
	/** The Constant SKU_ATTRIBUTES. */
	private static final String SKU_ATTRIBUTES = "skucardattributes";
	
	/** The Constant ADDITIONAL_FACETS. */
	private static final String ADDITIONAL_FACETS = "additional_facets";

	/** The Constant FACETS. */
	private static final String FACETS = "facets";
	
	/** The Constant RETURN_FACETS_FOR. */
	private static final String RETURN_FACETS_FOR = "returnFacetsFor";
	
	/** The Constant RETURN_FACETS_FOR. */
	private static final String RETURN_FACETS_FOR_VEHICLE = "returnFacetsForVehicle";
	
	/** The Constant RETURN_FACETS_FOR. */
	private static final String RETURN_FACETS_FOR_AXLE = "returnFacetsForAxle";
	
	/**
	 * Instantiates a new site config util.
	 */
	private VGSiteConfigUtil() {
		LOGGER.debug("Inside CommonUtil constructor");
	}
	/**
     * Gets the config details.
     *
     * @param vgSelectorCloudConfigRes the site config res
     * @return the vg config details
     */
    public static ConfigModel getVGSelectorConfigDetails(Resource vgSelectorCloudConfigRes){
    	
    	ConfigModel formConfigModel =null;
    	
    	if(null != vgSelectorCloudConfigRes){
    		formConfigModel = vgSelectorCloudConfigRes.adaptTo(ConfigModel.class);
    		
    		ValueMap vgSelectorVM = vgSelectorCloudConfigRes.getValueMap();
    		
    		List <AdditionalFacetsModel> additionalFacets = populateAdditionalFacets(vgSelectorVM);
    		formConfigModel.setAdditional_facetsListSize(additionalFacets.size());
    		formConfigModel.setAdditional_facetsList(additionalFacets); 
    		
    		List <SKUcardAttributesModel> skuCardAttributes = populateSKUAttributes(vgSelectorVM);
    		formConfigModel.setSkuCardAttributeListSize(skuCardAttributes.size());
    		formConfigModel.setSkucardattributesList(skuCardAttributes); 
    		
    		List <ReturnFacetsModel> returnFactes = populateReturnFacets(vgSelectorVM);
    		formConfigModel.setReturnFacetsListSize(returnFactes.size());
    		formConfigModel.setReturnFacetsList(returnFactes); 
    		
    		List <ReturnFacetsVehicleModel> returnFactesVehicle = populateReturnFacetsVehicle(vgSelectorVM);
    		formConfigModel.setReturnFacetsVehicleListSize(returnFactesVehicle.size()); 
    		formConfigModel.setReturnFacetsVehicleList(returnFactesVehicle);
    		
    		List <ReturnFacetsAxleModel> returnFactesAxle = populateReturnFacetsAxle(vgSelectorVM);
    		formConfigModel.setReturnFacetsAxleListSize(returnFactesAxle.size());
    		formConfigModel.setReturnFacetsAxleList(returnFactesAxle);
  	
    	}
    	LOGGER.info("Exited from getVGSelectorConfigDetails() method :::");
    	return formConfigModel;
    	
    }
	
	/**
	 * Populate Additional facets.
	 *
	 * @param vgSelectorVM 
	 * @return the list
	 */
	private static List<AdditionalFacetsModel> populateAdditionalFacets(ValueMap vgSelectorVM) {
		List <AdditionalFacetsModel> additionalFacetsModelList = new ArrayList <>();
    	
    	if (null != vgSelectorVM) {
			String[] jsonString = vgSelectorVM.get(ADDITIONAL_FACETS,String[].class);
			if(jsonString != null && jsonString.length != 0){
			for (int i = 0; i <jsonString.length; i++){
				AdditionalFacetsModel additionalFacets = new AdditionalFacetsModel();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(jsonString[i]);
					
					additionalFacets.setFacets(jsonObject.get(FACETS).toString()); 
					additionalFacetsModelList.add(additionalFacets);
					
				} catch (ParseException pe) {
					LOGGER.error(VGCommonConstants.PARSING_EXCEPTION, pe);
					}
				}
						
			}
    	}
		return additionalFacetsModelList;
	}
	
	/**
	 * Populate SKU attributes.
	 *
	 * @param vgSelectorVM the vg selector VM
	 * @return the list
	 */
	private static List<SKUcardAttributesModel> populateSKUAttributes(ValueMap vgSelectorVM) {
		List <SKUcardAttributesModel> skuCardModelList = new ArrayList <>();
    	
    	if (null != vgSelectorVM) {
			String[] jsonString =  vgSelectorVM.get(SKU_ATTRIBUTES,String[].class);
			if(jsonString != null && jsonString.length != 0){
			for (int i = 0; i <jsonString.length; i++){
				SKUcardAttributesModel skuCardAttributes = new SKUcardAttributesModel();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(jsonString[i]);
					
					skuCardAttributes.setSKUattribute(jsonObject.get(VGCommonConstants.SKU_ATTRIBUTES).toString());
					skuCardModelList.add(skuCardAttributes);
					
				} catch (ParseException pe) {
					LOGGER.error(VGCommonConstants.PARSING_EXCEPTION, pe);
					}
				}
						
			}
    	}
		return skuCardModelList;
	}
	
	/**
	 * Populate Return facets for clutch.
	 *
	 * @param vgSelectorVM 
	 * @return the list
	 */
	private static List<ReturnFacetsModel> populateReturnFacets(ValueMap vgSelectorVM) {
		List <ReturnFacetsModel> returnFacetsModelList = new ArrayList <>();
    	
    	if (null != vgSelectorVM) {
			String[] jsonString =  vgSelectorVM.get(RETURN_FACETS_FOR,String[].class);
			if(jsonString != null && jsonString.length != 0){
			for (int i = 0; i <jsonString.length; i++){
				ReturnFacetsModel returnFacets = new ReturnFacetsModel();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(jsonString[i]);
					
					returnFacets.setReturnFacets(jsonObject.get(VGCommonConstants.RETURN_FACETS).toString());  
					returnFacetsModelList.add(returnFacets);
					
				} catch (ParseException pe) {
					LOGGER.error(VGCommonConstants.PARSING_EXCEPTION, pe);
					}
				}
			}
    	}
		return returnFacetsModelList;
	}
	
	/**
	 * Populate Return facets for vehicle.
	 *
	 * @param vgSelectorVM 
	 * @return the list
	 */
	private static List<ReturnFacetsVehicleModel> populateReturnFacetsVehicle(ValueMap vgSelectorVM) {
		List <ReturnFacetsVehicleModel> returnFacetsVehicleModelList = new ArrayList <>();
    	
    	if (null != vgSelectorVM) {
			String[] jsonString =  vgSelectorVM.get(RETURN_FACETS_FOR_VEHICLE,String[].class);
			if(jsonString != null && jsonString.length != 0){
			for (int i = 0; i <jsonString.length; i++){
				ReturnFacetsVehicleModel returnFacetsVehicle = new ReturnFacetsVehicleModel();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(jsonString[i]);
					
					returnFacetsVehicle.setReturnFacetsVehicle(jsonObject.get(VGCommonConstants.RETURN_FACETS_VEHICLE).toString());  
					returnFacetsVehicleModelList.add(returnFacetsVehicle);
					
				} catch (ParseException pe) {
					LOGGER.error(VGCommonConstants.PARSING_EXCEPTION, pe);
					}
				}
			}
    	}
		return returnFacetsVehicleModelList;
	}
	
	/**
	 * Populate Return facets for axle.
	 *
	 * @param vgSelectorVM 
	 * @return the list
	 */
	private static List<ReturnFacetsAxleModel> populateReturnFacetsAxle(ValueMap vgSelectorVM) {
		List <ReturnFacetsAxleModel> returnFacetsAxleModelList = new ArrayList <>();
    	
    	if (null != vgSelectorVM) {
			String[] jsonString =  vgSelectorVM.get(RETURN_FACETS_FOR_AXLE,String[].class);
			if(jsonString != null && jsonString.length != 0){
			for (int i = 0; i <jsonString.length; i++){
				ReturnFacetsAxleModel returnFacetsAxle = new ReturnFacetsAxleModel();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(jsonString[i]);
					
					returnFacetsAxle.setReturnFacetsAxle(jsonObject.get(VGCommonConstants.RETURN_FACETS_AXLE).toString()); 
					returnFacetsAxleModelList.add(returnFacetsAxle);
					
				} catch (ParseException pe) {
					LOGGER.error(VGCommonConstants.PARSING_EXCEPTION, pe);
					}
				}
			}
    	}
		return returnFacetsAxleModelList;
	}

}