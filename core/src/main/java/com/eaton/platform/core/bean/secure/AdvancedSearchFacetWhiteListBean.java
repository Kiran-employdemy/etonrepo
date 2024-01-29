package com.eaton.platform.core.bean.secure;

/**
 * This bean holds Advanced Search Whitelisted facets data.
 */
public class AdvancedSearchFacetWhiteListBean {

    private String facet;

    private String showAsGrid;

    private String facetSearchEnabled;

    private String singleFacetEnabled;


    public String getFacet() {
        return facet;
    }

    public String getShowAsGrid() {
        return showAsGrid;
    }

    public String getFacetSearchEnabled() {
        return facetSearchEnabled;
    }

    public String getSingleFacetEnabled() {
        return singleFacetEnabled;
    }

    public void setFacet(String facet) {
        this.facet = facet;
    }

    public void setShowAsGrid(String showAsGrid) {
        this.showAsGrid = showAsGrid;
    }

    public void setFacetSearchEnabled(String facetSearchEnabled) {
        this.facetSearchEnabled = facetSearchEnabled;
    }

    public void setSingleFacetEnabled(String singleFacetEnabled) {
        this.singleFacetEnabled = singleFacetEnabled;
    }
}