package com.eaton.platform.core.models.brandingselectortool;

import com.eaton.platform.core.services.CloudConfigService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BrandFontSelectionModel {

    private boolean isBrandFontSelectionConfigPresent;

    @OSGiService
    private CloudConfigService cloudConfigService;

    @Inject
    private Resource resource;

    private BrandFontSelectionConfigModel brandFontSelectionConfigObj;

    @PostConstruct
    protected void init() {
        final Optional<BrandFontSelectionConfigModel> brandFontSelectionCloudConfigObj = cloudConfigService != null
                ? cloudConfigService.getBrandFontSelectionConfig(resource) : Optional.empty();
        if (brandFontSelectionCloudConfigObj.isPresent()) {
            isBrandFontSelectionConfigPresent = true;
            brandFontSelectionConfigObj = brandFontSelectionCloudConfigObj.get();
        }
    }

    public boolean isBrandFontSelectionConfigPresent() {
        return isBrandFontSelectionConfigPresent;
    }

    public BrandFontSelectionConfigModel getBrandFontSelectionConfigObj() {
        return brandFontSelectionConfigObj;
    }
}
