package com.eaton.platform.core.bean;

import java.util.List;

/**
 * <html> Description: This bean class used in TabbedMenuListModel class to store content </html>
 * @version 2.0
 * @since 2022
 *
 */
public class TabbedMenuItem{

	private String title;
	private String titleLink;
	private String titleTarget;
	private String headerLink;
	private String headerTarget;
	private String headerText;
	private String publicationDate;
	private String lastModifiedDate;
	private String createdDate;
	private String templateName;
	private List<TabbedMenuLink> tabbedMenuLinkList;
	
	
	/**
	 * @return the titleTarget
	 */
	public String getTitleTarget() {
		return titleTarget;
	}

	/**
	 * @param titleTarget the titleTarget to set
	 */
	public void setTitleTarget(String titleTarget) {
		this.titleTarget = titleTarget;
	}

	/**
	 * @return the headerLink
	 */
	public String getHeaderLink() {
		return headerLink;
	}

	/**
	 * @param headerLink the headerLink to set
	 */
	public void setHeaderLink(String headerLink) {
		this.headerLink = headerLink;
	}

	/**
	 * @return the headertext
	 */
	public String getHeaderText() {
		return headerText;
	}

	/**
	 * @param headerText the headerText to set
	 */
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	/**
	 * @return the tabbedMenuLinkList
	 */
	public List<TabbedMenuLink> getTabbedMenuLinkList() {
		return tabbedMenuLinkList;
	}

	/**
	 * @param tabbedMenuLinkList the tabbedMenuLinkList to set
	 */
	public void setTabbedMenuLinkList(List<TabbedMenuLink> tabbedMenuLinkList) {
		this.tabbedMenuLinkList = tabbedMenuLinkList;
	}

	/**
	 * @return the titleLink
	 */
	public String getTitleLink() {
		return titleLink;
	}

	/**
	 * @param titleLink the titleLink to set
	 */
	public void setTitleLink(String titleLink) {
		this.titleLink = titleLink;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the publicationDate
	 */
	public String getPublicationDate() {
		return publicationDate;
	}

	/**
	 * @param publicationDate the publicationDate to set
	 */
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}


	/**
	 * @return the lastModifiedDate
	 */
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the headerTarget
	 */
	public String getHeaderTarget() {
		return headerTarget;
	}

	/**
	 * @param headerTarget the headerTarget to set
	 */
	public void setHeaderTarget(String headerTarget) {
		this.headerTarget = headerTarget;
	}

	

	
	
}
