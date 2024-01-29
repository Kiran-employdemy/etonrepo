package com.eaton.platform.core.bean;

import java.io.Serializable;

import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This bean class used in LinkListHelper class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class SectionNavigationBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6214590228582466637L;
	
	/** The link title. */
	private String linkTitle;
	
	
	/** The link page path. */
	private String pageLink; 
	
	
	/**
	 * Gets the link title.
	 *
	 * @return linkTitle
	 */
	public String getLinkTitle() {
		return linkTitle;
	}
	
	/**
	 * Sets the link title.
	 *
	 * @param linkTitle the new link title
	 */
	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}
	
	/**
	 * Gets the link page path.
	 *
	 * @return linkPagePath
	 */
	public String getPageLink() {
		return CommonUtil.dotHtmlLink(pageLink);
	}
	
	/**
	 * Sets the link page path.
	 *
	 * @param linkPagePath the new link page path
	 */
	public void setPageLink(String pageLink) {
		this.pageLink = pageLink;
	}
	
}
