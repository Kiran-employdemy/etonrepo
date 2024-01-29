
package com.eaton.platform.integration.bullseye.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "categoryGroup",
    "categories"
})
public class CategoryGroup {

    @JsonProperty("categoryGroup")
    private String categoryGroup;
    @JsonProperty("categories")
    private List<String> categories = null;

    @JsonProperty("categoryGroup")
    public String getCategoryGroup() {
        return categoryGroup;
    }

    @JsonProperty("categoryGroup")
    public void setCategoryGroup(String categoryGroup) { this.categoryGroup = categoryGroup;
    }

    @JsonProperty("categories")
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("categoryGroup", categoryGroup).append("categories", categories).toString();
    }

}
