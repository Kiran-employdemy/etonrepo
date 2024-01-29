
package com.eaton.platform.integration.bullseye.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "categoryGroups"
})
public class CategoryGroupFilters {

    @JsonProperty("categoryGroups")
    private List<CategoryGroup> categoryGroups = null;

    @JsonProperty("categoryGroups")
    public List<CategoryGroup> getCategoryGroups() {
        return categoryGroups;
    }

    @JsonProperty("categoryGroups")
    public void setCategoryGroups(List<CategoryGroup> categoryGroups) {
        this.categoryGroups = categoryGroups;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("categoryGroups", categoryGroups).toString();
    }

}
