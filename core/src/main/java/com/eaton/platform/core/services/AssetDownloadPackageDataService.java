package com.eaton.platform.core.services;

import com.eaton.platform.core.models.asset.AssetDownloadPackageDataModel;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceNotFoundException;

import java.util.Map;

public interface AssetDownloadPackageDataService {

    public String saveAssetDownloadPackageData(final String resourcePath, final Map<String, Object> metadata) throws PersistenceException;

    public AssetDownloadPackageDataModel getAssetDownloadPackageData(final String resourcePath, final String downloadPackageIdentifier) throws ResourceNotFoundException;

    public boolean deleteAssetDownloadPackageData(final String resourcePath, final String downloadPackageIdentifier) throws PersistenceException, ResourceNotFoundException;
}
