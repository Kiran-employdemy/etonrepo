
package com.eaton.platform.core.bean.sitesearch;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "text",
    "url",
    "target"
})
public class SecondaryLink {

    @JsonProperty("text")
    private String text;
    @JsonProperty("url")
    private String url;
    @JsonProperty("target")
    private String target;

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

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

}
