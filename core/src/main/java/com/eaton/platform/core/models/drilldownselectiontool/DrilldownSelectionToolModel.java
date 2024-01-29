package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.inject.Inject;
import java.util.List;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DrilldownSelectionToolModel {
    @Inject
    @ChildResource
    private List<DropdownModel> dropdowns;

    @Inject
    private SlingHttpServletRequest slingHttpServletRequest;

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private Page currentPage;

    @Inject
    private AuthorizationService authorizationService;

    @Inject
    private PageManager pageManager;

    @Inject
    private EatonSiteConfigService eatonSiteConfigService;


    public List<DropdownModel> getDropdowns() {
        return dropdowns;
    }

    public void setDropdowns(List<DropdownModel> dropdowns) {
        this.dropdowns = dropdowns;
    }

    public DropdownModel getDropdown(int dropdownIndex, List<String> selectedDropdownOptionTagsList) {
        this.dropdowns.get(dropdownIndex).setDropdownOptions(selectedDropdownOptionTagsList, slingHttpServletRequest, currentPage);
        return this.dropdowns.get(dropdownIndex);
    }

    public ResultsModel getResultsModel(List<String> selectedDropdownOptionTags, int startIndex, int pageSize) {
        return ResultsModel.of(slingHttpServletRequest, authorizationService, resourceResolver, eatonSiteConfigService, currentPage, selectedDropdownOptionTags, startIndex, pageSize);

    }

}
