package com.eaton.platform.core.models.developerportal.v1;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
		resourceType = "eaton/components/developer-portal/core/iconwithlink/v1/iconwithlink")
public class IconWithLinkModel {
	
	@Inject
	private String icon;
	
	@Inject
	private String heading;

	@Inject
	private String subheading;

	@Inject
	private String linktext;

	@Inject
	private String linkurl;

	@Inject
	private String newWindow;

	public String getIcon() {
		return icon;
	}

	public String getHeading() {
		return heading;
	}

	public String getSubheading() {
		return subheading;
	}

	public String getLinktext() {
		return linktext;
	}

	public String getLinkurl() {
		return linkurl;
	}

	public String getNewWindow() {
		return newWindow;
	}
}