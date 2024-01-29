package com.eaton.platform.core.models;

import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.*;

import javax.inject.Inject;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BuyNowModel {

	public static final String DI_ENDPOINT_PREFIX = "/distributor-inventory/v1/products/";
	public static final String DI_ENDPOINT_SUFFIX = "/ranked-distributors";
	public static final int PAGE_SIZE = 50;
	public static final int LOAD_SIZE = 5;
	public static final String RESULTS_LINK_PATH_DEFAULT = "/content/eaton/us/en-us";

	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	@Inject @Via("resource")
	@Default(values = "/content/dam/eaton/resources/buy-now/geolocation_icon.png")
	private String geolocationIconPath;
	
	@Inject @Via("resource")
	@Default(values = "View Website")
	private String tableRowLinkText;

	@Inject @Via("resource")
	@Default(values = RESULTS_LINK_PATH_DEFAULT)
	private String tableRowLinkDefault;

	@Inject @Via("resource")
	@Default(intValues = 50)
	private int defaultRadius;

	public String getGeolocationIconPath() {
		return geolocationIconPath;
	}

	public void setGeolocationIconPath(String geolocationIconPath) {
		this.geolocationIconPath = geolocationIconPath;
	}

	public String getTableRowLinkText() {
		return tableRowLinkText;
	}

	public void setTableRowLinkText(String tableRowLinkText) {
		this.tableRowLinkText = tableRowLinkText;
	}

	public String getTableRowLinkDefault() {

		return StringUtils.isEmpty(tableRowLinkDefault)
				? CommonUtil.dotHtmlLink(RESULTS_LINK_PATH_DEFAULT, resourceResolver)
				: CommonUtil.dotHtmlLink(tableRowLinkDefault, resourceResolver);

	}

	public void setTableRowLinkDefault(String tableRowLinkDefault) {
		this.tableRowLinkDefault = tableRowLinkDefault;
	}

	public int getDefaultRadius() {
		return defaultRadius;
	}

	public void setDefaultRadius(int defaultRadius) {
		this.defaultRadius = defaultRadius;
	}
	
}
