package com.eaton.platform.core.models;

import javax.inject.Inject;
import javax.annotation.PostConstruct;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FaviconIconModel {
	
	private static final Logger LOG = LoggerFactory.getLogger(FaviconIconModel.class);
	
	@Inject
	private SlingHttpServletRequest slingRequest;
	
	private String faviconpath = "";
	
	@PostConstruct
	protected void init() {
		LOG.debug("FaviconIcon Model Init Started");
		
		final EatonSiteConfigModel eatonSiteConfigModel = slingRequest.adaptTo(EatonSiteConfigModel.class);
				if (null != eatonSiteConfigModel) {
					final SiteConfigModel siteConfiguration = eatonSiteConfigModel.getSiteConfig();
						if(null != siteConfiguration){
							faviconpath = siteConfiguration.getFavionIcon();
						}
				}
				
		LOG.debug("FaviconIcon Model Init Ended");		
	}
	
	public String getFaviconpath() {
		return faviconpath;
	}

}