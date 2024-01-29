package com.eaton.platform.core.models.eatonsiteconfig;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;

import java.util.Optional;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import javax.inject.Inject;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class})
public class EatonSiteConfigModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(EatonSiteConfigModel.class);
    private static final String SITECONFIG = "siteconfig";
    private static final String CQ_CLOUDSERVICECONFIGS = "cq:cloudserviceconfigs";
    private String currentLanguage;

    @Inject
    Page currentPage;

    @Inject
    Resource resource;

    @OSGiService
    AdminService adminService;

    private SiteConfigModel siteConfig;

    public SiteConfigModel getSiteConfig() {
        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                final Resource contentResource = currentPage.getContentResource();
                final HierarchyNodeInheritanceValueMap valueMap = new HierarchyNodeInheritanceValueMap(contentResource);
                final String[] services = valueMap.getInherited(CQ_CLOUDSERVICECONFIGS, new String[]{});
                final Optional<ConfigurationManager> configurationManagerOptional = Optional
                        .ofNullable(adminResourceResolver.adaptTo(ConfigurationManager.class));
                if (configurationManagerOptional.isPresent()) {
                    final ConfigurationManager configurationManager = configurationManagerOptional.get();
                    final Optional<Configuration> configurationOptional = Optional.ofNullable(configurationManager
                            .getConfiguration(SITECONFIG, services));
                    if (configurationOptional.isPresent()) {
                        final Resource siteContentResource = configurationOptional.get().getContentResource();
                        siteConfig = siteContentResource.adaptTo(SiteConfigModel.class);
                    }
                } else {
                    LOGGER.error("Error while retrieving site config.");
                }
            }
        } else {
            LOGGER.error("Error while getting the resource resolver.");
        }
        return siteConfig;
    }
    //To get locale language into HTML tag
    public String getCurrentLanguage() {
    	Locale languageLocale=currentPage.getLanguage();
    	String country = CommonUtil.getCountryFromPagePath(currentPage);
    	if(languageLocale != null && country != null ) {
    			country = country.toLowerCase();
    			currentLanguage = languageLocale.getLanguage();
    			currentLanguage = currentLanguage+CommonConstants.HYPHEN+country;
    	}
		return currentLanguage;
    }
}
