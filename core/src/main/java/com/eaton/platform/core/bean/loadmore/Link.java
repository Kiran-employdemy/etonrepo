
package com.eaton.platform.core.bean.loadmore;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
	"text",
    "url",
    "target",
    "linkType"
})
public class Link {

	@JsonProperty("text")
	private String text;
    @JsonProperty("url")
    private String url;
    
    private String completeUrl;
    
    @JsonProperty("target")
    private String target = "_self";
    @JsonProperty("linkType")
    private String linkType;

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("target")
    public String getTarget() {
        return target;
    }

    @JsonProperty("target")
    public void setTarget(String target) {
        this.target = target;
    }

    @JsonProperty("linkType")
    public String getLinkType() {
        return linkType;
    }

    @JsonProperty("linkType")
    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }
    
    public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this).append("url", url).append("target", target).toString();
    }

	public String getCompleteUrl() {
		return completeUrl;
	}

	public void setCompleteUrl(String completeUrl) {
		this.completeUrl = completeUrl;
	}
	
	

}
