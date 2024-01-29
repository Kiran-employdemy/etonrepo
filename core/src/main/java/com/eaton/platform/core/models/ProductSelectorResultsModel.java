package com.eaton.platform.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class ProductSelectorResultsModel {
    @Inject @Optional
    private String hideGlobalFacetSearch;

	public String getHideGlobalFacetSearch() {
		return hideGlobalFacetSearch;
	}
}
