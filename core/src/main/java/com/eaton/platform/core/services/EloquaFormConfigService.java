package com.eaton.platform.core.services;

import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.models.EloquaCloudConfigModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * This service is used for Eloqua form manipulation.
 */
public interface EloquaFormConfigService {

    EloquaCloudConfigModel setUpEloquaServiceParameters(final ConfigurationManagerFactory configurationManagerFactory,
                                                        final String contentPath,
                                                        final ResourceResolver resourceResolver);

    String[] getLOVFromConfig(final SlingHttpServletRequest request, final ResourceResolver adminResourceResolver,
                              final ConfigurationManagerFactory configManagerFctry, final String propertyName);

    EloquaCloudConfigModel getEloquaConfigDetails(final Resource eloquaConfigRes);

}
