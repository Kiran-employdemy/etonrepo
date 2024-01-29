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
public class LinkListTypeBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6214590228582466637L;
	
	/** The link title. */
	private String linkTitle;
	
	
	/** The link page path. */
	private String pageLink; 
	
	
	/** The template name. */
	private String templateName;
	
	
	/** The publication date. */
	private Long publicationDate;
	
	/** The last modified date. */
	private String lastModifiedDate;
	
	/** The created date. */
	private Long createdDate;
	
	/** The nav title. */
	private String navTitle;
	
	/** The nav name. */
	private String navName;
	
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
	
	/**
	 * Gets the template name.
	 *
	 * @return templateName
	 */
	public String getTemplateName() {
		return templateName;
	}
	
	/**
	 * Sets the template name.
	 *
	 * @param templateName the new template name
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	/**
	 * Gets the nav title.
	 *
	 * @return navTitle
	 */
	public String getNavTitle() {
		return navTitle;
	}

	/**
	 * Sets the nav title.
	 *
	 * @param navTitle the new nav title
	 */
	public void setNavTitle(String navTitle) {
		this.navTitle = navTitle;
	}

	/**
	 * Gets the nav name.
	 *
	 * @return navName
	 */
	public String getNavName() {
		return navName;
	}

	/**
	 * Sets the nav name.
	 *
	 * @param navName the new nav name
	 */
	public void setNavName(String navName) {
		this.navName = navName;
	}
	
	/**
	 * Gets the created date.
	 *
	 * @return createdDate
	 */
	public Long getCreatedDate() {
		return createdDate;
	}
	
	/**
	 * Sets the created date.
	 *
	 * @param createdDate the new created date
	 */
	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}
	
	/**
	 * Gets the last modified date.
	 *
	 * @return lastModifiedDate
	 */
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	/**
	 * Sets the last modified date.
	 *
	 * @param lastModifiedDate the new last modified date
	 */
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	/**
	 * Gets the publication date.
	 *
	 * @return publicationDate
	 */
	public Long getPublicationDate() {
		return publicationDate;
	}
	
	/**
	 * Sets the publication date.
	 *
	 * @param publicationDate the new publication date
	 */
	public void setPublicationDate(Long publicationDate) {
		this.publicationDate = publicationDate;
	}
	
}
