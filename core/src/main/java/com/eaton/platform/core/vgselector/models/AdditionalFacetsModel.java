package com.eaton.platform.core.vgselector.models;


import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * The Class AdditionalFacetsModel.
 */
@Model(adaptables = Resource.class)
public class AdditionalFacetsModel {

	
	/** The facets. */
	@Inject 
	private String facets;

	/**
	 * Gets the facets.
	 *
	 * @return the facets
	 */
	public String getFacets() {
		return facets;
	}

	/**
	 * Sets the facets.
	 *
	 * @param facets the facets to set
	 */
	public void setFacets(String facets) {
		this.facets = facets;
	}

}
	