package com.eaton.platform.core.search.api;

/**
 * Configuration for how facets need to be displayed.
 * This can be a configuration that comes from the component, site config or other places
 * The methods all return String, because the implementation can decide if it uses strings instead of boolean
 * A possibility would be to return a certain css class value like "checked" for singleFacetEnabled, etc...
 */
public interface FacetConfiguration {
    /**
     * @return the id of the facet, generally that would be a tag id
     */
    String getFacet();

    /**
     * @return the indication that the facet needs to be shown in grid
     */
    String getShowAsGrid();

    /**
     * @return the indication that a search box of the facets is enabled
     */
    String getFacetSearchEnabled();

    /**
     * @return the indication that the facet is for single selection only
     */
    String getSingleFacetEnabled();
}
