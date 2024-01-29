package com.eaton.platform.core.services.impl;

import com.adobe.cq.social.srp.SocialResourceProvider;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.asset.AssetDownloadPackageDataModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.AssetDownloadPackageDataService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;
import java.util.UUID;
@Component(service = AssetDownloadPackageDataService.class,immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "AssetDownloadPackageDataService",
                AEMConstants.PROCESS_LABEL + "AssetDownloadPackageDataService"
        })
public class AssetDownloadPackageDataServiceImpl extends StorageResourceProviderTemplate implements AssetDownloadPackageDataService {

    @Reference
    private AdminService adminService;

    @Override
    public String saveAssetDownloadPackageData(final String resourcePath, final Map<String, Object> metadata) throws PersistenceException {
        String response = StringUtils.EMPTY;
        try (ResourceResolver resourceResolver = adminService.getWriteService()) {
            final SocialResourceProvider socialResourceProvider = getSocialResourceProvider(resourceResolver);
            final String uuid = UUID.randomUUID().toString();
            final String assetUGCStoragePath = socialResourceProvider.getASIPath().concat(getUGCStoragePath(resourcePath, uuid));
            socialResourceProvider.create(resourceResolver, assetUGCStoragePath, metadata);
            resourceResolver.commit();
            response = uuid;
        }
        return response;
    }

    @Override
    public AssetDownloadPackageDataModel getAssetDownloadPackageData(final String resourcePath,
                                                                     final String downloadPackageIdentifier) throws ResourceNotFoundException {
        final AssetDownloadPackageDataModel assetDownloadPackageDataModel;
        try (ResourceResolver resourceResolver = adminService.getWriteService()) {
            final Resource resource = getResource(resourceResolver, getUGCStoragePath(resourcePath, downloadPackageIdentifier));
            if(resource == null) {
                throw new ResourceNotFoundException(getUGCStoragePath(resourcePath, downloadPackageIdentifier));
            } else {
                assetDownloadPackageDataModel = resource.adaptTo(AssetDownloadPackageDataModel.class);
            }
        }
        return assetDownloadPackageDataModel;
    }

    public boolean deleteAssetDownloadPackageData(final String resourcePath, final String downloadPackageIdentifier) throws ResourceNotFoundException, PersistenceException {
        boolean response = Boolean.FALSE;
        try(ResourceResolver resourceResolver = adminService.getWriteService()) {
            final Resource resource = getResource(resourceResolver, getUGCStoragePath(resourcePath, downloadPackageIdentifier));

            if (resource == null) {
                throw new ResourceNotFoundException(getUGCStoragePath(resourcePath, downloadPackageIdentifier));
            } else {
                final SocialResourceProvider socialResourceProvider = getSocialResourceProvider(resourceResolver);
                socialResourceProvider.delete(resourceResolver, resource.getPath());
                resourceResolver.commit();
                response = Boolean.TRUE;
            }
        }
        return response;
    }

    private String getUGCStoragePath(final String resourcePath, final String downloadPackageIdentifier) {
        return resourcePath.concat("/").concat(downloadPackageIdentifier);
    }
}