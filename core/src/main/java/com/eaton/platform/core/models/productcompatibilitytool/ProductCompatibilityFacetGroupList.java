package com.eaton.platform.core.models.productcompatibilitytool;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Object for using the facets in ProductCompatibilityModel list
 */
public class ProductCompatibilityFacetGroupList {

    private final String facetsArrayString;
    private final List<Filter> filterList;

    /**
     * List of ProductCompatibilityFacetGroup objects
     *
     * @param facetsArrayString facets in their current order
     * @param filterList filterList to order facets by
     */
    public ProductCompatibilityFacetGroupList(String facetsArrayString, List<Filter> filterList) {
        this.facetsArrayString = facetsArrayString;
        this.filterList = filterList;
    }

    /**
     * @return facet list ordered following the filterList order of appearance
     */
    public String orderedFacetList() {
        List<ProductCompatibilityFacetGroup> productCompatibilityFacetGroups
                = Arrays.asList(new Gson().fromJson(facetsArrayString, TypeToken.getArray(ProductCompatibilityFacetGroup.class).getType()));
        LinkedList<ProductCompatibilityFacetGroup> orderedFilters = new LinkedList<>();
        if(filterList == null || filterList.isEmpty()) {
            return new Gson().toJson(productCompatibilityFacetGroups);
        } else {
            filterList.forEach(filter -> {
                String filterName = filter.getFilterAttributeVaue();
                Optional<ProductCompatibilityFacetGroup> optionalProductCompatibilityFacetGroup
                        = productCompatibilityFacetGroups.stream().filter(group -> group.getName().equals(filterName)).findFirst();
                optionalProductCompatibilityFacetGroup.ifPresent(orderedFilters::add);
            });
        }
        return new Gson().toJson(orderedFilters);
    }
}
