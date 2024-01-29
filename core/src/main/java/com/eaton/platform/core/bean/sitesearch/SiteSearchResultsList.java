
package com.eaton.platform.core.bean.sitesearch;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "contentType",
    "contentItem"
})
public class SiteSearchResultsList {

    @JsonProperty("contentType")
    private String contentType;
    @JsonProperty("contentItem")
    private SiteSearchContentItem contentItem;

    @JsonProperty("contentType")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("contentType")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("contentItem")
    public SiteSearchContentItem getContentItem() {
        return contentItem;
    }

    @JsonProperty("contentItem")
    public void setContentItem(SiteSearchContentItem contentItem) {
        this.contentItem = contentItem;
    }

}
