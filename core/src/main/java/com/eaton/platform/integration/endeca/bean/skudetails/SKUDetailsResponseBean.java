package com.eaton.platform.integration.endeca.bean.skudetails;

import com.google.gson.Gson;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;


@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
    "response"
})
public class SKUDetailsResponseBean  implements Serializable{

	@JsonProperty("response")
    private SKUResponseBean skuResponse;



	 /**
	 * @return the skuResponse
	 */
	public SKUResponseBean getSkuResponse() {
		return skuResponse;
	}



	/**
	 * @param skuResponse the skuResponse to set
	 */
	public void setSkuResponse(SKUResponseBean skuResponse) {
		this.skuResponse = skuResponse;
	}



	 @Override
	 public String toString() {
		return new Gson().toJson(this);
     }

	public SKUDetailsBean getSkuDetails() {
		if (skuResponse == null) {
			return null;
		}
		return skuResponse.getSkuDetails();
	}
}


