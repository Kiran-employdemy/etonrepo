package com.eaton.platform.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCMSUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CCMSUtils.class);

    /**
     * Returns {@link ResourceResolver} of the given sub-service. It returns null in case given
     * {@link ResourceResolverFactory} and subService are null.
     *
     * @param resourceResolverFactory {@link ResourceResolverFactory}
     * @param subService              {@link String} sub-service defined in Sling Apache User Mapping configuration
     * @return {@link ResourceResolver}
     */
    public static ResourceResolver getResourceResolver(final ResourceResolverFactory resourceResolverFactory,
        final String subService) {
        ResourceResolver resourceResolver = null;
        if (null != resourceResolverFactory && null != subService) {
            try {
                final Map<String, Object> authInfo = new HashMap<>();
                authInfo.put(ResourceResolverFactory.SUBSERVICE, subService);
                resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo);
            } catch (final LoginException loginException) {
                LOGGER.error("EatonCCMSUtil getResourceResolver() : Exception while getting resource resolver for subservice {} : {}",
                    subService, loginException);
            }
        }
        return resourceResolver;
    }

}
