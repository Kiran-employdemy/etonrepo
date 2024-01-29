package com.eaton.platform.core.vgselector.models;


import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * The Class AdditionalFacetsModel.
 */
@Model(adaptables = Resource.class)
public class ReturnFacetsModel {
	
	/** The return facets. */
	@Inject 
	private String returnFacets;

	/**
	 * Gets the return facets.
	 *
	 * @return the returnFacets
	 */
	public String getReturnFacets() {
		return returnFacets;
	}

	/**
	 * Sets the return facets.
	 *
	 * @param returnFacets the returnFacets to set
	 */
	public void setReturnFacets(String returnFacets) {
		this.returnFacets = returnFacets;
	}

}
	