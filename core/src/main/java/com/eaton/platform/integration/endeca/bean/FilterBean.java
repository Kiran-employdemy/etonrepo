package com.eaton.platform.integration.endeca.bean;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Bean to use for parsing Json
 */
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
        "filterName",
        "filterValue"
})
public class FilterBean implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 6075497050778063518L;
    @JsonProperty("filterName")
    private String filterName;
    @JsonProperty("filterValue")
    private List<String> filterValues;

    public FilterBean() {
    }

    /**
     * constructor taking
     * @param filterName name of the filter
     * @param filterValues values for the filter
     *
     */
    public FilterBean(String filterName, List<String> filterValues) {
        this.filterName = filterName;
        this.filterValues = filterValues;
    }

    @JsonProperty("filterName")
    public String getFilterName() {
        return filterName;
    }

    @JsonProperty("filterName")
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    @JsonProperty("filterValue")
    public List<String> getFilterValues() {
        return filterValues;
    }

    @JsonProperty("filterValue")
    public void setFilterValues(List<String> filterValues) {
        this.filterValues = filterValues;
    }

    @Override
    public String toString() {
        return "FilterBean{" +
                "filterName='" + filterName + '\'' +
                ", filterValues=" + filterValues +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilterBean that = (FilterBean) o;
        return Objects.equals(filterName, that.filterName) && Objects.equals(filterValues, that.filterValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterName, filterValues);
    }
}
