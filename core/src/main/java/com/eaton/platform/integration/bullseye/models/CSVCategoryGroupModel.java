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
public class CSVCategoryGroupModel {

    @Inject
    private String csvCategoryGroup;
    
    @Inject @Default(values="false")
    private String singleFacetEnabled;

    @Inject
    private List<CSVCategoryModel> categoryList;

    public String getCSVCategoryGroup() {
        return csvCategoryGroup;
    }

    public void setCSVCategoryGroup(String csvCategoryGroup) {
        this.csvCategoryGroup = csvCategoryGroup;
    }
    
    public String getSingleFacetEnabled() {
		return singleFacetEnabled;
	}

	public void setSingleFacetEnabled(String singleFacetEnabled) {
		this.singleFacetEnabled = singleFacetEnabled;
	}

    public List<CSVCategoryModel> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CSVCategoryModel> categoryList) {
        this.categoryList = categoryList;
    }
}
