package com.eaton.platform.core.models.productcompatibilitytool;

import java.util.List;

/**
 * Product compatibility group of facets, contained inside "values" and labeled by the other attributes
 */

public class ProductCompatibilityFacetGroup {
    private boolean showAsGrid;
    private List<ProductCompatibilityFacet> values;
    private String name;
    private String title;
    private boolean singleFacetEnabled;
    private boolean facetSearchEnabled;

    public String getName() {
        return name;
    }

    public List<ProductCompatibilityFacet> getValues() {
        return values;
    }
}
