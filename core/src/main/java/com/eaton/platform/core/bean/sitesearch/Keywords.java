package com.eaton.platform.core.bean.sitesearch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
"results"
})
public class Keywords implements Serializable
{

@JsonProperty("results")
private List<KeywordBean> results = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = -4804681472241623796L;

@JsonProperty("results")
public List<KeywordBean> getResults() {
return results;
}

@JsonProperty("results")
public void setResults(List<KeywordBean> results) {
this.results = results;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}