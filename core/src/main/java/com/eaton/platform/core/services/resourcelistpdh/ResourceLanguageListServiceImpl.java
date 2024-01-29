package com.eaton.platform.core.services.resourcelistpdh;

import com.day.cq.dam.api.Asset;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AdminService;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of the ResourceLanguageListService interface
 * The list ResourceLanguage is updated whenever there is new version of the json file uploaded and one of the access methods
 * is called.
 */
@Component(service = ResourceLanguageListService.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton Service for handling the ResourceLanguageList.json",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "Eaton Include Asset Properties"
        })
public class ResourceLanguageListServiceImpl implements ResourceLanguageListService {
    @VisibleForTesting
    static final String RESOURCE_LANGUAGE_LIST_JSON_LOCATION = "/content/dam/eaton/resources/siteconfig/resourceLanguageList.json";
    @Reference
    private AdminService adminService;
    private ResourceLanguageList resourceLanguageList;
    private LocalDateTime lastUpdated;
    @Override
    public String resourceGroupName(String key, Locale locale) {
        if (resourceLanguageList().hasKey(key)) {
            return resourceLanguageList().getResourceGroupName(key, locale);
        }
        return null;
    }

    private ResourceLanguageList resourceLanguageList() {
        try (ResourceResolver resourceResolver = adminService.getReadService()) {
            Resource resource = resourceResolver.getResource(RESOURCE_LANGUAGE_LIST_JSON_LOCATION);
            Asset asset = resource.adaptTo(Asset.class);
            long lastModified = asset.getLastModified();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(lastModified), ZoneId.systemDefault());
            boolean updated = lastUpdated != null && localDateTime.isAfter(lastUpdated);
            if (resourceLanguageList == null || updated) {
                resourceLanguageList = new Gson().fromJson(new InputStreamReader(asset.getOriginal().getStream(), StandardCharsets.UTF_8), ResourceLanguageList.class);
                lastUpdated = LocalDateTime.now();
            }
        }
        return resourceLanguageList;
    }

    @Override
    public Map<String, List<String>> getResourceLanguageList(Locale locale) {
        return resourceLanguageList().getResourceLanguagesFor(locale);
    }
}
