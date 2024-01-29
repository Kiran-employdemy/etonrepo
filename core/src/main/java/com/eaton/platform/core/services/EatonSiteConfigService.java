package com.eaton.platform.core.services;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import java.util.Optional;
import org.apache.sling.api.resource.Resource;

public interface EatonSiteConfigService {

    Optional<Resource> getSiteConfigResource(final Page currentPage, final Optional<ConfigurationManager> configurationManagerOptional);
    Optional<SiteResourceSlingModel> getSiteConfig(final Page currentPage);


}
