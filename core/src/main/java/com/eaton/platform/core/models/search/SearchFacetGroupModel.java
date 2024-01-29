package com.eaton.platform.core.models.search;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchFacetGroupModel {
    @Inject
    private boolean gridFacet;

    @Inject
    private boolean facetSearchEnabled;

    @Inject
    private String siteSearchFacetGroup;

    @Inject
    private boolean singleFacetEnabled;



    public boolean isGridFacet() {
        return gridFacet;
    }

    public void setGridFacet(boolean gridFacet) {
        this.gridFacet = gridFacet;
    }

    public boolean isFacetSearchEnabled() {
        return facetSearchEnabled;
    }

    public void setFacetSearchEnabled(boolean facetSearchEnabled) {
        this.facetSearchEnabled = facetSearchEnabled;
    }

    public String getSiteSearchFacetGroup() {
        return siteSearchFacetGroup;
    }

    public void setSiteSearchFacetGroup(String siteSearchFacetGroup) {
        this.siteSearchFacetGroup = siteSearchFacetGroup;
    }

    public boolean isSingleFacetEnabled() {
        return singleFacetEnabled;
    }

    public void setSingleFacetEnabled(boolean singleFacetEnabled) {
        this.singleFacetEnabled = singleFacetEnabled;
    }
}
