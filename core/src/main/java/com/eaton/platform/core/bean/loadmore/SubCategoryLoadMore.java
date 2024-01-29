
package com.eaton.platform.core.bean.loadmore;

import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "resultsList"
})
public class SubCategoryLoadMore {

    @JsonProperty("resultsList")
    private List<SubCategoryResults> resultsList = null;

    @JsonProperty("resultsList")
    public List<SubCategoryResults> getResultsList() {
        return resultsList;
    }

    @JsonProperty("resultsList")
    public void setResultsList(List<SubCategoryResults> resultsList) {
        this.resultsList = resultsList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("resultsList", resultsList).toString();
    }

}
