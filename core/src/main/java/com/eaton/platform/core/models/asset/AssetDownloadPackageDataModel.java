package com.eaton.platform.core.models.asset;

import com.eaton.platform.core.constants.AssetDownloadConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AssetDownloadPackageDataModel {

    @Inject
    private String downloadFileName;

    @Inject
    private List<String> downloadAssetPaths;

    @Inject
    private boolean mergeAssetsToSinglePDF;

    @Inject
    private String mergeAssetsFileName;

    public String getDownloadFileName() {
        return Optional.ofNullable(downloadFileName).orElse(AssetDownloadConstants.DOWNLOAD_FILE_NAME_DEFAULT_VALUE);
    }

    public List<String> getDownloadAssetPaths() {
        return downloadAssetPaths;
    }

    public boolean isMergeAssetsToSinglePDF() {
        return mergeAssetsToSinglePDF;
    }

    public String getMergeAssetsFileName() {
        return Optional.ofNullable(mergeAssetsFileName).orElse(AssetDownloadConstants.MERGE_ASSETS_FILE_NAME_DEFAULT_VALUE);
    }
}
