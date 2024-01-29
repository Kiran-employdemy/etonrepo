package com.eaton.platform.integration.bullseye.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BullseyeConfigModel {
    public static final String CONFIG_NAME = "bullseye-configurations";
    public static final String NO_FILTERS_MESSAGE = "None";
    public static final String FILTERS_APPLIED_MESSAGE = " filters applied.";

    @Inject
    private String apiKey;

    @Inject @Default(values = "5")
    private String pageSize;

    @Inject
    private String mappingVendor;

    @Inject
    private String mappingApiKey;

    @Inject
    private String clientId;

    @Inject
    private String defaultMapMarker;

    @Inject
    private String [] distances;

    @Inject
    private String [] markers;

    @Inject
    private String [] prefilters;

    @Inject
    private String printPrefilters;

    @Inject @Default(values = "100")
    private String defaultRadius;

    @Inject @Default(values = "kilometers")
    private String defaultDistanceUnit;

    public String getApiKey() {
        return apiKey;
    }

    public String getPageSize() {
        return pageSize;
    }

    public String getMappingApiKey() {
        return mappingApiKey;
    }

    public String getMappingVendor() {
        return mappingVendor;
    }

    public String getClientId() {
        return clientId;
    }

    public String[] getDistances() {
        return distances;
    }

    public String getDefaultRadius() {
        return defaultRadius;
    }

    public String getDefaultDistanceUnit() {
        return defaultDistanceUnit;
    }

    public String[] getMarkers() {
        return markers;
    }

    public String[] getPrefilters(){
        return prefilters;
    }

    public String getPrintPrefilters(){
        String[] filters = getPrefilters();
        if(null == filters){
            return NO_FILTERS_MESSAGE;
        }
        return filters.length + FILTERS_APPLIED_MESSAGE;
    }

    public String getDefaultMapMarker() {
        return defaultMapMarker;
    }
}