package com.eaton.platform.core.models.submittalbuilder;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.codehaus.jackson.annotate.JsonProperty;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * This extends the concept of the selected tag to the specific scenario of a tag selected to be used as a facet.
 */
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SelectedFacetTag extends SelectedTag {
    @Inject @Named("facetSearchEnabled")
    private boolean facetSearchEnabled;

    @JsonProperty(value="facetSearchEnabled")
    public boolean isFacetSearchEnabled() {
        return facetSearchEnabled;
    }

    @Inject @Named("showAsGrid")
    private boolean showAsGrid;

    @JsonProperty(value="showAsGrid")
    public boolean isShowAsGrid() {
        return showAsGrid;
    }

    @Inject @Named("singleFacetEnabled")
    private boolean singleFacetEnabled;

    @JsonProperty(value="singleFacetEnabled")
    public boolean isSingleFacetEnabled() {
        return singleFacetEnabled;
    }
}
