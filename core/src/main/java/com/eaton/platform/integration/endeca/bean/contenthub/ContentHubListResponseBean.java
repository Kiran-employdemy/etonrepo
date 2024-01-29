package com.eaton.platform.integration.endeca.bean.contenthub;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
"response"
})

public class ContentHubListResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3367927048077294260L;
	
	@JsonProperty("response")
	private ContentHubResponseBean response;

	/**
	 * @return the response
	 */
	public ContentHubResponseBean getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(ContentHubResponseBean response) {
		this.response = response;
	}
	
}