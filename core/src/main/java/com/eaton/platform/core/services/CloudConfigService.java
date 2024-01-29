package com.eaton.platform.core.services;

import com.eaton.platform.core.models.EloquaCloudConfigModel;
import com.eaton.platform.integration.bullseye.models.BullseyeConfigModel;
import com.eaton.platform.core.models.EndecaConfigModel;
import com.eaton.platform.core.models.PdhConfigModel;
import com.eaton.platform.core.models.brandingselectortool.BrandColorSelectionConfigModel;
import com.eaton.platform.core.models.brandingselectortool.BrandFontSelectionConfigModel;
import com.eaton.platform.core.models.ModelViewerCloudConfig;
import com.eaton.platform.integration.priceSpider.models.PriceSpiderConfigModel;
import org.apache.sling.api.resource.Resource;
import java.util.Optional;

public interface CloudConfigService {
    Optional<EndecaConfigModel> getEndecaCloudConfig(Resource resource);
    Optional<PdhConfigModel> getPdhCloudConfig(Resource resource);
    Optional<BrandColorSelectionConfigModel> getBrandColorSelectionConfig(Resource resource);
    Optional<BrandFontSelectionConfigModel> getBrandFontSelectionConfig(Resource resource);
    Optional<BullseyeConfigModel> getBullsEyeCloudConfig(Resource resource);
    Optional<ModelViewerCloudConfig> getModelViewerCloudConfig(Resource resource);
    Optional<EloquaCloudConfigModel> getEloquaCloudConfig(Resource resource);
    Optional<PriceSpiderConfigModel> getPriceSpiderCloudConfig(Resource resource);
}