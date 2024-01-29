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
public class TorqueListLoadMore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7321274928495095965L;
	/**
	 * 
	 */
	
	@JsonProperty("resultsList")
    private List<TorqueListResultsList> resultsList = null;


    @JsonProperty("resultsList")
    public List<TorqueListResultsList> getResultsList() {
        return resultsList;
    }

    @JsonProperty("resultsList")
    public void setResultsList(List<TorqueListResultsList> resultsList) {
        this.resultsList = resultsList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("resultsList", resultsList).toString();
    }
   
}
