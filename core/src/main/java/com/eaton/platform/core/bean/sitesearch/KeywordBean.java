package com.eaton.platform.core.bean.sitesearch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
"title",
"link",
"target"
})
public class KeywordBean implements Serializable
{

@JsonProperty("title")
private String title;
@JsonProperty("link")
private String link;
@JsonProperty("target")
private String target;
@JsonProperty("secure")
private boolean secure;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = -4381770052728491516L;

@JsonProperty("title")
public String getTitle() {
return title;
}

@JsonProperty("title")
public void setTitle(String title) {
this.title = title;
}

@JsonProperty("link")
public String getLink() {
return link;
}

@JsonProperty("link")
public void setLink(String link) {
this.link = link;
}

@JsonProperty("target")
public String getTarget() {
return target;
}

@JsonProperty("target")
public void setTarget(String target) {
this.target = target;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

public boolean isSecure() {
    return secure;
}

public void setSecure(boolean secure) {
    this.secure = secure;
}

}