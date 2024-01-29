package com.eaton.platform.core.models.vgSelector;


import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import com.eaton.platform.core.vgselector.models.AdditionalFacetsModel;
import com.eaton.platform.core.vgselector.models.ReturnFacetsAxleModel;
import com.eaton.platform.core.vgselector.models.SKUcardAttributesModel;
import com.eaton.platform.core.vgselector.models.ReturnFacetsModel;
import com.eaton.platform.core.vgselector.models.ReturnFacetsVehicleModel;
/**
 * The Class ConfigModel for VG Selector.
 */
@Model(adaptables = Resource.class)
public class ConfigModel {

	/** The selectorToolType. */
	@Inject @Optional
	private String selectorToolType;
	
	/** The facetValueCount. */
	@Inject @Optional
	private int facetValueCount;
	
	/** The facetCount. */
	@Inject @Optional
	private int facetCount;
	
	/** The expandedFacetCount. */
	@Inject @Optional
	private int expandedFacetCount;
	
	/** The page size */
	@Inject @Optional
	private int pageSize;

	/** The fallback image */
	@Inject @Optional
	private String fallbackImage;
	
	/** The Portfolio ration of good id. */
	@Inject @Optional
	private String good;
	
	/** The Portfolio ration of better id. */
	@Inject @Optional
	private String better;
	
	/** The Portfolio ration of best id. */
	@Inject @Optional
	private String best;
	
	/** The SKU Card attribute list. */
	@Inject @Optional
	private String[] skucardattributes;	
	
	private List <SKUcardAttributesModel> skucardattributesList;
	
	/** The SKU Card attribute list size. */
	private int skuCardAttributeListSize;
	
	/** The clutch_selector_facets list. */
	@Inject @Optional
	private String[] additional_facets;
	
	private List <AdditionalFacetsModel> additional_facetsList;
	
	/** The Additional Facet list size. */
	private int additional_facetsListSize;
	
	/** The returnFacetsFor list for clutch. */
	@Inject @Optional
	private String[] returnFacetsFor; 
	
	private List <ReturnFacetsModel> returnFacetsList;
	
	/** The Return Facet list size for clutch. */
	private int returnFacetsListSize;
	
	/** The returnFacetsFor list for vehicle. */
	@Inject @Optional
	private String[] returnFacetsForVehicle; 
	
	private List <ReturnFacetsVehicleModel> returnFacetsVehicleList;
	
	/** The Return Facet list size for vehicle. */
	private int returnFacetsVehicleListSize;
	
	/** The returnFacetsFor list for axle. */
	@Inject @Optional
	private String[] returnFacetsForAxle; 
	
	private List <ReturnFacetsAxleModel> returnFacetsAxleList;
	
	/** The Return Facet list size for axle. */
	private int returnFacetsAxleListSize;
	
	
	@Inject @Optional
	private String longDescriptionCheckbox;
	
	
	/**
	 * @return the selectorToolType
	 */
	public String getSelectorToolType() {
		return selectorToolType;
	}

	/**
	 * @param selectorToolType the selectorToolType to set
	 */
	public void setSelectorToolType(String selectorToolType) {
		this.selectorToolType = selectorToolType;
	}

	/**
	 * @return the facetValueCount
	 */
	public int getFacetValueCount() {
		return facetValueCount;
	}

	/**
	 * @param facetValueCount the facetValueCount to set
	 */
	public void setFacetValueCount(int facetValueCount) {
		this.facetValueCount = facetValueCount;
	}

	/**
	 * @return the facetCount
	 */
	public int getFacetCount() {
		return facetCount;
	}

	/**
	 * @param facetCount the facetCount to set
	 */
	public void setFacetCount(int facetCount) {
		this.facetCount = facetCount;
	}

	/**
	 * @return the expandedFacetCount
	 */
	public int getExpandedFacetCount() {
		return expandedFacetCount;
	}

	/**
	 * @param expandedFacetCount the expandedFacetCount to set
	 */
	public void setExpandedFacetCount(int expandedFacetCount) {
		this.expandedFacetCount = expandedFacetCount;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the good
	 */
	public String getGood() {
		return good;
	}

	/**
	 * @param good the good to set
	 */
	public void setGood(String good) {
		this.good = good;
	}

	/**
	 * @return the better
	 */
	public String getBetter() {
		return better;
	}

	/**
	 * @param better the better to set
	 */
	public void setBetter(String better) {
		this.better = better;
	}

	/**
	 * @return the best
	 */
	public String getBest() {
		return best;
	}

	/**
	 * @param best the best to set
	 */
	public void setBest(String best) {
		this.best = best;
	}

	
	/**
	 * @return the skuCardAttributeListSize
	 */
	public int getSkuCardAttributeListSize() {
		return skuCardAttributeListSize;
	}

	/**
	 * @param skuCardAttributeListSize the skuCardAttributeListSize to set
	 */
	public void setSkuCardAttributeListSize(int skuCardAttributeListSize) {
		this.skuCardAttributeListSize = skuCardAttributeListSize;
	}

	/**
	 * @return the skucardattributes
	 */
	public String[] getSkucardattributes() {
		return skucardattributes;
	}

	/**
	 * @param skucardattributes to set
	 */
	public void setSkucardattributes(String[] skucardattributes) {
		this.skucardattributes = skucardattributes;
	}

	/**
	 * @return the skucardattributesList
	 */
	public List<SKUcardAttributesModel> getSkucardattributesList() {
		return skucardattributesList;
	}

	/**
	 * @param skucardattributesList the skucardattributesList to set
	 */
	public void setSkucardattributesList(List<SKUcardAttributesModel> skucardattributesList) {
		this.skucardattributesList = skucardattributesList;
	}

	/**
	 * @return the additional_facets
	 */
	public String[] getAdditional_facets() {
		return additional_facets;
	}

	/**
	 * @param additional_facets the additional_facets to set
	 */
	public void setAdditional_facets(String[] additional_facets) {
		this.additional_facets = additional_facets;
	}

	/**
	 * @return the additional_facetsList
	 */
	public List<AdditionalFacetsModel> getAdditional_facetsList() {
		return additional_facetsList;
	}

	/**
	 * @param additional_facetsList the additional_facetsList to set
	 */
	public void setAdditional_facetsList(List<AdditionalFacetsModel> additional_facetsList) {
		this.additional_facetsList = additional_facetsList;
	}

	/**
	 * @return the additional_facetsListSize
	 */
	public int getAdditional_facetsListSize() {
		return additional_facetsListSize;
	}

	/**
	 * @param additional_facetsListSize the additional_facetsListSize to set
	 */
	public void setAdditional_facetsListSize(int additional_facetsListSize) {
		this.additional_facetsListSize = additional_facetsListSize;
	}

	/**
	 * @return the returnFacetsFor
	 */
	public String[] getReturnFacetsFor() {
		return returnFacetsFor;
	}

	/**
	 * @param returnFacetsFor the returnFacetsFor to set
	 */
	public void setReturnFacetsFor(String[] returnFacetsFor) {
		this.returnFacetsFor = returnFacetsFor;
	}

	/**
	 * @return the returnFacetsList
	 */
	public List<ReturnFacetsModel> getReturnFacetsList() {
		return returnFacetsList;
	}

	/**
	 * @param returnFacetsList the returnFacetsList to set
	 */
	public void setReturnFacetsList(List<ReturnFacetsModel> returnFacetsList) {
		this.returnFacetsList = returnFacetsList;
	}

	/**
	 * @return the returnFacetsListSize
	 */
	public int getReturnFacetsListSize() {
		return returnFacetsListSize;
	}

	/**
	 * @param returnFacetsListSize the returnFacetsListSize to set
	 */
	public void setReturnFacetsListSize(int returnFacetsListSize) {
		this.returnFacetsListSize = returnFacetsListSize;
	}


	/**
	 * @return the fallbackImage
	 */
	public String getFallbackImage() {
		return fallbackImage;
	}

	/**
	 * @param fallbackImage the fallbackImage to set
	 */
	public void setFallbackImage(String fallbackImage) {
		this.fallbackImage = fallbackImage;
	}

	/**
	 * @return the longDescriptionCheckbox
	 */
	public String getLongDescriptionCheckbox() {
		return longDescriptionCheckbox;
	}

	/**
	 * @param longDescriptionCheckbox the longDescriptionCheckbox to set
	 */
	public void setLongDescriptionCheckbox(String longDescriptionCheckbox) {
		this.longDescriptionCheckbox = longDescriptionCheckbox;
	}

	/**
	 * @return the returnFacetsForVehicle
	 */
	public String[] getReturnFacetsForVehicle() {
		return returnFacetsForVehicle;
	}

	/**
	 * @param returnFacetsForVehicle the returnFacetsForVehicle to set
	 */
	public void setReturnFacetsForVehicle(String[] returnFacetsForVehicle) {
		this.returnFacetsForVehicle = returnFacetsForVehicle;
	}

	/**
	 * @return the returnFacetsVehicleList
	 */
	public List<ReturnFacetsVehicleModel> getReturnFacetsVehicleList() {
		return returnFacetsVehicleList;
	}

	/**
	 * @param returnFacetsVehicleList the returnFacetsVehicleList to set
	 */
	public void setReturnFacetsVehicleList(List<ReturnFacetsVehicleModel> returnFacetsVehicleList) {
		this.returnFacetsVehicleList = returnFacetsVehicleList;
	}

	/**
	 * @return the returnFacetsVehicleListSize
	 */
	public int getReturnFacetsVehicleListSize() {
		return returnFacetsVehicleListSize;
	}

	/**
	 * @param returnFacetsVehicleListSize the returnFacetsVehicleListSize to set
	 */
	public void setReturnFacetsVehicleListSize(int returnFacetsVehicleListSize) {
		this.returnFacetsVehicleListSize = returnFacetsVehicleListSize;
	}

	/**
	 * @return the returnFacetsForAxle
	 */
	public String[] getReturnFacetsForAxle() {
		return returnFacetsForAxle;
	}

	/**
	 * @param returnFacetsForAxle the returnFacetsForAxle to set
	 */
	public void setReturnFacetsForAxle(String[] returnFacetsForAxle) {
		this.returnFacetsForAxle = returnFacetsForAxle;
	}

	/**
	 * @return the returnFacetsAxleList
	 */
	public List<ReturnFacetsAxleModel> getReturnFacetsAxleList() {
		return returnFacetsAxleList;
	}

	/**
	 * @param returnFacetsAxleList the returnFacetsAxleList to set
	 */
	public void setReturnFacetsAxleList(List<ReturnFacetsAxleModel> returnFacetsAxleList) {
		this.returnFacetsAxleList = returnFacetsAxleList;
	}

	/**
	 * @return the returnFacetsAxleListSize
	 */
	public int getReturnFacetsAxleListSize() {
		return returnFacetsAxleListSize;
	}

	/**
	 * @param returnFacetsAxleListSize the returnFacetsAxleListSize to set
	 */
	public void setReturnFacetsAxleListSize(int returnFacetsAxleListSize) {
		this.returnFacetsAxleListSize = returnFacetsAxleListSize;
	}
	
}
	