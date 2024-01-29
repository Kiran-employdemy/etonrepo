package com.eaton.platform.integration.endeca.bean.crossreference;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.stream.Collectors;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("response")
public class XRefSearchResults implements Serializable {
    @JsonProperty("totalCount")
    private String totalCount;

    @JsonProperty("document")
    private List<XRefResult> document;

    public String getTotalCount() {
        return totalCount;
    }

    public List<XRefResult> getDocument() {
        return document;
    }

    @SuppressWarnings("unchecked")
    public void setDocument(Object object) {
        document = ((ArrayList<LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>>) object)
                .stream()
                .map(result -> new XRefResult(result.get("content")))
                .collect(Collectors.toList());
    }
}
