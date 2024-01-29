package com.eaton.platform.integration.endeca.bean.productcompatibility;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "response"
})
public class ProductCompatibilityListResponseBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 395590690781430407L;

	private List<String> compatibilityTableHeaders;
	
	public List<String> getCompatibilityTableHeaders() {
		return compatibilityTableHeaders;
	}

	public void setCompatibilityTableHeaders(List<String> compatibilityTableHeaders) {
		this.compatibilityTableHeaders = compatibilityTableHeaders;
	}

	@JsonProperty("response")
    private ProductCompatibilityResponseBean familyModuleResponse;

	/**
	 * @return the familyModuleResponse
	 */
	public ProductCompatibilityResponseBean getFamilyModuleResponse() {
		return familyModuleResponse;
	}

	/**
	 * @param familyModuleResponse the familyModuleResponse to set
	 */
	public void setFamilyModuleResponse(
			ProductCompatibilityResponseBean familyModuleResponse) {
		this.familyModuleResponse = familyModuleResponse;
	}
}
