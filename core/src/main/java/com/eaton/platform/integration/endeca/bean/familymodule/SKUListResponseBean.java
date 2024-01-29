package com.eaton.platform.integration.endeca.bean.familymodule;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "response"
})
public class SKUListResponseBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 395590690781430407L;
	
	@JsonProperty("response")
    private FamilyModuleResponseBean familyModuleResponse;

	/**
	 * @return the familyModuleResponse
	 */
	public FamilyModuleResponseBean getFamilyModuleResponse() {
		return familyModuleResponse;
	}

	/**
	 * @param familyModuleResponse the familyModuleResponse to set
	 */
	public void setFamilyModuleResponse(
			FamilyModuleResponseBean familyModuleResponse) {
		this.familyModuleResponse = familyModuleResponse;
	}
}
