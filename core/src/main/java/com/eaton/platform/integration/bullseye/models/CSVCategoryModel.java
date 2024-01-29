package com.eaton.platform.integration.bullseye.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * MapModel
 * This is a Sling Model for Map.
 *
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CSVCategoryModel {

    @Inject
    private String csvCategory;

    public String getCSVCategory() {
        return csvCategory;
    }

    public void setCSVCategory(String csvCategory) {
        this.csvCategory = csvCategory;
    }
}
