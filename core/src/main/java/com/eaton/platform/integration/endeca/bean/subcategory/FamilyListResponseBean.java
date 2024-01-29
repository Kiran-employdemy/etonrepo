package com.eaton.platform.integration.endeca.bean.subcategory;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
"response"
})

public class FamilyListResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2632864614251002040L;
	
	@JsonProperty("response")
	private FamilyResponseBean response;

	/**
	 * @return the response
	 */
	public FamilyResponseBean getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(FamilyResponseBean response) {
		this.response = response;
	}
	
}