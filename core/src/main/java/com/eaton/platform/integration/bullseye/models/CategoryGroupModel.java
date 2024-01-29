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
public class CategoryGroupModel {

    @Inject
    private String categoryGroup;
    
    @Inject @Default(values="false")
    private String singleFacetEnabled;

    @Inject
    private List<CategoryModel> categoryList;

    public String getCategoryGroup() {
        return categoryGroup;
    }

    public void setCategoryGroup(String categoryGroup) {
        this.categoryGroup = categoryGroup;
    }
    
    public String getSingleFacetEnabled() {
		return singleFacetEnabled;
	}

	public void setSingleFacetEnabled(String singleFacetEnabled) {
		this.singleFacetEnabled = singleFacetEnabled;
	}

	public List<CategoryModel> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
    }
}
