package com.eaton.platform.integration.bullseye.models;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * MapModel
 * This is a Sling Model for Map.
 *
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PreSeltCategoryGroupModel {

    @Inject
    private String preSeltCategoryGroup;
    
    @Inject @Default(values="false")
    private String singleFacetEnabled;

    @Inject
    private List<PreSeltCategoryModel> preSeltCategoryList;

    public String getCategoryGroup() {
        return preSeltCategoryGroup;
    }

    public void setCategoryGroup(String preSeltCategoryGroup) {
        this.preSeltCategoryGroup = preSeltCategoryGroup;
    }
    
    public String getSingleFacetEnabled() {
		return singleFacetEnabled;
	}

	public void setSingleFacetEnabled(String singleFacetEnabled) {
		this.singleFacetEnabled = singleFacetEnabled;
	}

	public List<PreSeltCategoryModel> getCategoryList() {
        return preSeltCategoryList;
    }

    public void setCategoryList(List<PreSeltCategoryModel> categoryList) {
        this.preSeltCategoryList = categoryList;
    }
}
