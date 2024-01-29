package com.eaton.platform.core.bean.sitemap;

import com.eaton.platform.core.util.CommonUtil;

import java.io.Serializable;

/**
 * This class is Level 1 Sitemap Pojo class.
 * @author TCS
 *
 */
public class SitemapBean  implements Serializable {
	
	private static final long serialVersionUID = 7016198865363495003L;

	/** The link title. */
	private String linkTitle;
	
	/** The link path. */
	private String linkPath;
	
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
	 * Gets the link path.
	 *
	 * @return linkPath
	 */
	public String getLinkPath() {
		return CommonUtil.dotHtmlLink(linkPath);
	}
	
	/**
	 * Sets the link path.
	 *
	 * @param linkPath the new link path
	 */
	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

}
