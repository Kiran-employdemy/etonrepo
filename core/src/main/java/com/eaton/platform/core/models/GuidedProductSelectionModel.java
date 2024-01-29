package com.eaton.platform.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@Model(adaptables = Resource.class)
public class GuidedProductSelectionModel {

    @Inject @Optional
    private String tooltipTitle;

    @Inject @Optional
    private String tooltipDescription;

    @Inject @Optional
    private String tooltipImage;

    @Inject @Optional
    private String tooltipIcon;
    
    @Inject @Optional
    private String closeIcon;
    
    @Inject @Default(values="Close")
    private String closeTitle;

    public String getTooltipTitle() {
        return tooltipTitle;
    }

    public String getTooltipDescription() {
        return tooltipDescription;
    }

    public String getTooltipImage() {
        return tooltipImage;
    }

    public String getTooltipIcon() {
        return tooltipIcon;
    }

    public String getCloseIcon() {
        return closeIcon;
    }
    
    public String getCloseTitle() {
        return closeTitle;
    }
}
