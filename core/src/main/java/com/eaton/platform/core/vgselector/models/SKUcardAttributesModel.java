
package com.eaton.platform.core.vgselector.models;


import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * The Class SKUcardAttributesModel.
 */
@Model(adaptables = Resource.class)
public class SKUcardAttributesModel {

	/** The SK uattribute. */
	@Inject @Named("attributes")
	private String SKUattribute;

	/**
	 * Gets the SK uattribute.
	 *
	 * @return the SK uattribute
	 */
	public String getSKUattribute() {
		return SKUattribute;
	}

	/**
	 * Sets the SK uattribute.
	 *
	 * @param sKUattribute the new SK uattribute
	 */
	public void setSKUattribute(String sKUattribute) {
		SKUattribute = sKUattribute;
	}
	
}
	