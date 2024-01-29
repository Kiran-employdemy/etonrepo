package com.eaton.platform.core.bean.loadmore;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "contentType",
    "contentItem"
})
public class SKUListResultsList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6424629272355763970L;
	@JsonProperty("contentType")
    private String contentType;
    @JsonProperty("contentItem")
    private SKUListContentItem contentItem;

    @JsonProperty("contentType")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("contentType")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("contentItem")
    public SKUListContentItem getContentItem() {
        return contentItem;
    }

    @JsonProperty("contentItem")
    public void setContentItem(SKUListContentItem contentItem) {
        this.contentItem = contentItem;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("contentType", contentType).append("contentItem", contentItem).toString();
    }
}
