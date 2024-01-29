package com.eaton.platform.core.services.impl;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.eaton.platform.core.models.EloquaCloudConfigModel;
import com.eaton.platform.integration.bullseye.models.BullseyeConfigModel;
import com.eaton.platform.core.models.brandingselectortool.BrandColorSelectionConfigModel;
import com.eaton.platform.core.models.brandingselectortool.BrandFontSelectionConfigModel;
import com.eaton.platform.core.models.ModelViewerCloudConfig;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.EndecaConfigModel;
import com.eaton.platform.core.models.PdhConfigModel;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.integration.priceSpider.models.PriceSpiderConfigModel;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Optional;


@Component(service = CloudConfigService.class,immediate = true)
public class CloudConfigServiceImpl implements CloudConfigService {

    @Reference
    private AdminService adminService;

    /**
     * Inherits the Endeca Cloud Config from the nearest ancestor that has an Endeca Cloud Config.
     * @param resource Any resource under the /content path.
     * @return The inherited Endeca Cloud Config.
     */
    public Optional<EndecaConfigModel> getEndecaCloudConfig(Resource resource) {
        return getInheritedConfig(resource, EndecaConfigModel.CONFIG_NAME, EndecaConfigModel.class);
    }

    /**
     * Inherits the PDH Cloud Config from the nearest ancestor that has an Pdh Cloud Config.
     * @param resource Any resource under the /content path.
     * @return The inherited Pdh Cloud Config.
     */
    public Optional<PdhConfigModel> getPdhCloudConfig(Resource resource) {
        return getInheritedConfig(resource, PdhConfigModel.CONFIG_NAME, PdhConfigModel.class);
    }

    public Optional<BullseyeConfigModel> getBullsEyeCloudConfig(Resource resource) {
        return getInheritedConfig(resource, BullseyeConfigModel.CONFIG_NAME, BullseyeConfigModel.class);
    }

    public Optional<EloquaCloudConfigModel> getEloquaCloudConfig(Resource resource) {
        return getInheritedConfig(resource, EloquaCloudConfigModel.CONFIG_NAME, EloquaCloudConfigModel.class);
    }

    /**
     * Inherits the Brand Color Selection Cloud Config from the nearest ancestor that has Brand Color selection Cloud Config.
     * @param resource Any resource under the /content path.
     * @return The inherited Brand Color Selection Config.
     */
    public Optional<BrandColorSelectionConfigModel> getBrandColorSelectionConfig(Resource resource) {
        return getInheritedConfig(resource, BrandColorSelectionConfigModel.CONFIG_NAME, BrandColorSelectionConfigModel.class);
    }

    /**
     * Inherits the Brand Font Selection Cloud Config from the nearest ancestor that has Brand Font selection Cloud Config.
     * @param resource Any resource under the /content path.
     * @return The inherited Brand Font Selection Config.
     */
    public Optional<BrandFontSelectionConfigModel> getBrandFontSelectionConfig(Resource resource) {
        return getInheritedConfig(resource, BrandFontSelectionConfigModel.CONFIG_NAME, BrandFontSelectionConfigModel.class);
    }

    @Override
    public Optional<ModelViewerCloudConfig> getModelViewerCloudConfig(Resource resource) {
        return getInheritedConfig(resource, ModelViewerCloudConfig.CONFIG_NAME, ModelViewerCloudConfig.class);
    }


    public Optional<PriceSpiderConfigModel> getPriceSpiderCloudConfig(Resource resource) {
        return getInheritedConfig(resource, PriceSpiderConfigModel.CONFIG_NAME, PriceSpiderConfigModel.class);
    }

    /**
     * Inherits the specified cloud config from the nearest ancestor that has the specified cloud config.
     * @param resource Any resource under the /content path.
     * @param configName The page name of the page under which the cloud config exists such as /etc/cloudservices/<configName>
     * @return
     */
    private <T> Optional<T> getInheritedConfig(Resource resource, String configName, Class<T> clazz) {
        Optional<T> adaptedConfig = Optional.empty();
        try (ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
            final ConfigurationManager cfgMgr = adminReadResourceResolver.adaptTo(ConfigurationManager.class);
            Page searchPage = resource.getResourceResolver().adaptTo(PageManager.class).getContainingPage(resource);

            while (!adaptedConfig.isPresent() && searchPage != null) {
                final HierarchyNodeInheritanceValueMap pageProperties = new HierarchyNodeInheritanceValueMap(searchPage.getContentResource());
                final String[] allServices = pageProperties.getInherited(AEMConstants.CQ_CLOUD_SERVICE_CONFIGS, new String[0]);

                final Configuration config = cfgMgr.getConfiguration(configName, allServices);
                if (config != null) {
                    final Page configPage = config.getResource().adaptTo(Page.class);
                    if (configPage != null) {
                        final Resource contentResource = configPage.getContentResource();
                        if (contentResource != null) {
                            adaptedConfig = Optional.ofNullable(contentResource.adaptTo(clazz));
                        }
                    }
                }

                searchPage = searchPage.getParent();
            }
        }

        return adaptedConfig;
    }
}
