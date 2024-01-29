package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: It is a Sling Model class which automatically maps from Sling objects
 * i.e. dialog fields are injected </html> 
 * @author TCS
 * @version 1.0
 * @since 2017
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageOverviewModel {
	
	/** The page overview header. */
	@Inject
	private String pageOverviewHeader;
	
	/** The page overview header type. */
	@Inject
	private String pageOverviewHeaderType;
	
	/** The page overview desc. */
	@Inject
	private String pageOverviewDesc;
	
	/** The page overview link path. */
	@Inject
	private String pageOverviewLinkPath;

	/** The page overview link title. */
	@Inject
	private String pageOverviewLinkTitle;

	/** The page overview link open new window. */
	@Inject
	private String pageOverviewLinkOpenNewWindow;

    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

	/**
	 * Gets the page overview header.
	 *
	 * @return the page overview header
	 */
	public String getPageOverviewHeader() {
		return pageOverviewHeader;
	}
	
	/**
	 * Gets the page overview header style.
	 *
	 * @return the page overview header type
	 */
	public String getPageOverviewHeaderType() {
		return pageOverviewHeaderType;
	}

	/**
	 * Gets the page overview desc.
	 *
	 * @return the page overview desc
	 */
	public String getPageOverviewDesc() {
		return pageOverviewDesc;
	}

	/**
	 * Gets the page overview link path.
	 *
	 * @return the page overview link path
	 */
	public String getPageOverviewLinkPath() {
		return CommonUtil.dotHtmlLink(this.pageOverviewLinkPath, this.resourceResolver);
	}

	/**
	 * if author has not entered the link title then
	 * return navigation title if it is not authored, then return the page title.
	 *
	 * @return linkTitle
	 */
	public String getPageOverviewLinkTitle() {
		return CommonUtil.getLinkTitle(this.pageOverviewLinkTitle, this.pageOverviewLinkPath, this.resourceResolver);
	}

	/**
	 * Gets the page overview link open new window.
	 *
	 * @return the page overview link open new window
	 */
	public String getPageOverviewLinkOpenNewWindow() {
		String newWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, this.pageOverviewLinkOpenNewWindow)) {
			newWindow = CommonConstants.TARGET_BLANK;
		}
		return newWindow;
	}

}
