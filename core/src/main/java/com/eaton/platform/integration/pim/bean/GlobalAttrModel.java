package com.eaton.platform.integration.pim.bean;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class GlobalAttrModel {

	@Inject @Optional
	private String	attributeName;
	
	@Inject @Optional
	private String attributeDisplayName;

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * @return the attributeDisplayName
	 */
	public String getAttributeDisplayName() {
		return attributeDisplayName;
	}
	
	
}
