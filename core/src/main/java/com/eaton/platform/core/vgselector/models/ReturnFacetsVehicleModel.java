package com.eaton.platform.core.vgselector.models;


import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * The Class AdditionalFacetsModel.
 */
@Model(adaptables = Resource.class)
public class ReturnFacetsVehicleModel {
	
	/** The return facets vehicle. */
	@Inject 
	private String returnFacetsVehicle;

	/**
	 * Gets the return facets vehicle.
	 *
	 * @return the returnFacetsVehicle
	 */
	public String getReturnFacetsVehicle() {
		return returnFacetsVehicle;
	}

	/**
	 * Sets the return facets vehicle.
	 *
	 * @param returnFacetsVehicle the returnFacetsVehicle to set
	 */
	public void setReturnFacetsVehicle(String returnFacetsVehicle) {
		this.returnFacetsVehicle = returnFacetsVehicle;
	}

}
	