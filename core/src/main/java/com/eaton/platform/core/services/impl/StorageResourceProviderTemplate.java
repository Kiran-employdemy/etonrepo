package com.eaton.platform.core.services.impl;

import com.adobe.cq.social.srp.SocialResourceProvider;
import com.adobe.cq.social.srp.config.SocialResourceConfiguration;
import com.adobe.cq.social.srp.utilities.api.SocialResourceUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageResourceProviderTemplate {

    protected final static String BASE_PATH = "/content/usergenerated/asi/cloud";

    private static final Logger LOG = LoggerFactory.getLogger(StorageResourceProviderTemplate.class);

    /**
     *
     * @param resourceResolver
     * @return
     */
    protected SocialResourceProvider getSocialResourceProvider(final ResourceResolver resourceResolver) {
        SocialResourceProvider socialResourceProvider = null;
        final SocialResourceUtilities socialUtils = resourceResolver.adaptTo(SocialResourceUtilities.class);

        if (null != socialUtils) {
            final Resource resource = resourceResolver.getResource(BASE_PATH);
            final SocialResourceConfiguration storageConfig = socialUtils.getStorageConfig(resource);
            if(null != resource) {
                socialResourceProvider = socialUtils.getSocialResourceProvider(resource);
                if(null !=socialResourceProvider) {
                socialResourceProvider.setConfig(storageConfig);
                }
            }
        }
        return socialResourceProvider;
    }

    /**
     *
     * @param resourceResolver
     * @param path
     * @return
     */
    protected Resource getResource(final ResourceResolver resourceResolver, final String path) {
        Resource resource = null;
        try {
            final SocialResourceProvider socialResourceProvider = getSocialResourceProvider(resourceResolver);
            final String resourcePath;

            if(StringUtils.isNotEmpty(path) && !path.startsWith(BASE_PATH)) {
                resourcePath = socialResourceProvider.getASIPath().concat(path);
            } else {
                resourcePath = path;
            }
            resource = socialResourceProvider.getResource(resourceResolver, resourcePath);
        } catch (Exception exc) {
            LOG.error(exc.getLocalizedMessage());
        }
        return resource;
    }
}
