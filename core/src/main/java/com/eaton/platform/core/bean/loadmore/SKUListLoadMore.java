
package com.eaton.platform.core.bean.loadmore;

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
public class SKUListLoadMore implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1555225874558786414L;
	@JsonProperty("resultsList")
    private List<SKUListResultsList> resultsList = null;

    @JsonProperty("resultsList")
    public List<SKUListResultsList> getResultsList() {
        return resultsList;
    }

    @JsonProperty("resultsList")
    public void setResultsList(List<SKUListResultsList> resultsList) {
        this.resultsList = resultsList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("resultsList", resultsList).toString();
    }

}
