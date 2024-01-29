package com.eaton.platform.core.vgselector.models;


import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * The Class AdditionalFacetsModel.
 */
@Model(adaptables = Resource.class)
public class ReturnFacetsAxleModel {
	
	/** The return facets axle. */
	@Inject 
	private String returnFacetsAxle;

	/**
	 * Gets the return facets axle.
	 *
	 * @return the returnFacetsAxle
	 */
	public String getReturnFacetsAxle() {
		return returnFacetsAxle;
	}

	/**
	 * Sets the return facets axle.
	 *
	 * @param returnFacetsAxle the returnFacetsAxle to set
	 */
	public void setReturnFacetsAxle(String returnFacetsAxle) {
		this.returnFacetsAxle = returnFacetsAxle;
	}
	
}
	