
package com.eaton.platform.core.bean.sitesearch;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "resultsList"
})
public class SiteSearch {

    @JsonProperty("resultsList")
    private List<SiteSearchResultsList> resultsList = null;

    @JsonProperty("resultsList")
    public List<SiteSearchResultsList> getResultsList() {
        return resultsList;
    }

    @JsonProperty("resultsList")
    public void setResultsList(List<SiteSearchResultsList> resultsList) {
        this.resultsList = resultsList;
    }

}
