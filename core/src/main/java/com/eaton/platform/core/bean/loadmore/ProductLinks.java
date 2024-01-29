
package com.eaton.platform.core.bean.loadmore;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "specificationsURL",
    "resourcesURL"
})
public class ProductLinks {

    @JsonProperty("specificationsURL")
    private String specificationsURL;
    @JsonProperty("resourcesURL")
    private String resourcesURL;

    @JsonProperty("specificationsURL")
    public String getSpecificationsURL() {
        return specificationsURL;
    }

    @JsonProperty("specificationsURL")
    public void setSpecificationsURL(String specificationsURL) {
        this.specificationsURL = specificationsURL;
    }

    @JsonProperty("resourcesURL")
    public String getResourcesURL() {
        return resourcesURL;
    }

    @JsonProperty("resourcesURL")
    public void setResourcesURL(String resourcesURL) {
        this.resourcesURL = resourcesURL;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("specificationsURL", specificationsURL).append("resourcesURL", resourcesURL).toString();
    }

}
