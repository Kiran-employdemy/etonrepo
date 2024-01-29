package com.eaton.platform.core.services.impl;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import java.util.Optional;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = EatonSiteConfigService.class,immediate = true)
public class EatonSiteConfigServiceImpl implements EatonSiteConfigService {

	private static final String CQ_CLOUDSERVICECONFIGS = "cq:cloudserviceconfigs";
	private static final String SITECONFIG = "siteconfig";

	@Reference
	AdminService adminService;

	@Override
	public Optional<Resource> getSiteConfigResource(final Page currentPage, final Optional<ConfigurationManager> configurationManagerOptional) {
		Optional<Resource> siteContentResource = Optional.empty();
		final Resource contentResource = currentPage.getContentResource();
		final HierarchyNodeInheritanceValueMap valueMap = new HierarchyNodeInheritanceValueMap(contentResource);
		final String[] services = valueMap.getInherited(CQ_CLOUDSERVICECONFIGS, new String[]{});
		if (configurationManagerOptional.isPresent()) {
			final ConfigurationManager configurationManager = configurationManagerOptional.get();
			final Optional<Configuration> configurationOptional = Optional.ofNullable(configurationManager
					.getConfiguration(SITECONFIG, services));
			if (configurationOptional.isPresent()) {
				siteContentResource = Optional.ofNullable(configurationOptional.get().getContentResource());
			}
		}
		return siteContentResource;
	}

	@Override
	public Optional<SiteResourceSlingModel> getSiteConfig(final Page currentPage) {
		Optional<SiteResourceSlingModel> siteConfig = Optional.empty();
		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			final Optional<ConfigurationManager> configurationManagerOptional = Optional
					.ofNullable(adminResourceResolver.adaptTo(ConfigurationManager.class));
			Optional<Resource> siteConfigResource = getSiteConfigResource(currentPage, configurationManagerOptional);
			if (siteConfigResource.isPresent()) {
				siteConfig = Optional.ofNullable(adminResourceResolver.resolve(siteConfigResource.get().getPath()).adaptTo(SiteResourceSlingModel.class));
			}
		}
		return siteConfig;
	}
}
