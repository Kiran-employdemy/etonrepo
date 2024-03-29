package com.eaton.platform.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResponsiveControlModel {
	@Inject
	private String hideInMobile;

	@Inject
	private String hideInDesktop;

	public String getHideInMobile(){
		return hideInMobile;
	}

	public String getHideInDesktop(){
		return hideInDesktop;
	}
}
