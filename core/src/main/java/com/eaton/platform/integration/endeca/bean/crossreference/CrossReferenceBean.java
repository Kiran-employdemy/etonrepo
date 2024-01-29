package com.eaton.platform.integration.endeca.bean.crossreference;

import com.eaton.platform.integration.endeca.bean.FacetsBean;
import com.eaton.platform.integration.endeca.bean.StatusDetailsBean;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrossReferenceBean implements Serializable {
    @JsonProperty("status")
    private String status;

    @JsonProperty("statusDetails")
    private StatusDetailsBean statusDetails;

    @JsonProperty("binning")
    private FacetsBean binning;

    @JsonProperty("searchResults")
    private XRefSearchResults searchResults;

    public String getStatus() {
        return status;
    }

    public StatusDetailsBean getStatusDetails() {
        return statusDetails;
    }

    public FacetsBean getBinning() {
        return binning;
    }

    public XRefSearchResults getSearchResults() {
        return searchResults;
    }
}
