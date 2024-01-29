package com.eaton.platform.core.bean;

import java.io.Serializable;

public class TopicLinkWithIconBean implements Serializable{
	
	private static final long serialVersionUID = 959358033157798275L;
	
	private String title;
	private String icon;
	private String link;
	private String description;
	private String newWindow;
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
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the newWindow
	 */
	public String getNewWindow() {
		return newWindow;
	}
	/**
	 * @param newWindow the newWindow to set
	 */
	public void setNewWindow(String newWindow) {
		this.newWindow = newWindow;
	}
	
	

}
