package com.eaton.platform.core.vgselector.bean.loadmore;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "resultsList"
})
public class ClutchListLoadMore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1442660107056872738L;
	
	@JsonProperty("resultsList")
    private List<ClutchListResultsList> resultsList = null;


    @JsonProperty("resultsList")
    public List<ClutchListResultsList> getResultsList() {
        return resultsList;
    }

    @JsonProperty("resultsList")
    public void setResultsList(List<ClutchListResultsList> resultsList) {
        this.resultsList = resultsList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("resultsList", resultsList).toString();
    }
   
}
