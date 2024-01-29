package com.eaton.platform.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GlobalAttributeModel {
	


	
	@Inject @Named("attributevalue")
	private String attributevalue;
	
	

	

	public String getAttributevalue() {
		return attributevalue;
	}


	public void setAttributevalue(String attributevalue) {
		this.attributevalue = attributevalue;
	}


	
	

}
