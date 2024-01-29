/**
 * 
 */
package com.eaton.platform.core.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This bean class used in VerticalLinkListModel class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class VerticalLinkListBean implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 385024822450814329L;
	
	/** The Constant DEFAULT_DATE. */
	private static final  String DEFAULT_DATE = "January 01, 0000 00:00:00.000+00:01";
	
	/** The page path. */
	private String pagePath;
	
	/** The publication date. */
	private String publicationDate;
	
	/** The replication date. */
	private String replicationDate;
	
	/** The headline. */
	private String headline;
	
	/** The last modified date. */
	private String lastModifiedDate;
	
	/** The created date. */
	private String createdDate;
	
	/** The template name. */
	private String templateName;
	
	/** The new  window . */
	private String newWindow;

	/** The enable source tracking . */
	private String enableSourceTracking;
	
	/** The description feature. */
	private String descriptionFeature;
	
	/** Count of items to show */
	private int count;

	private boolean secure;

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
	 * Sets the created date.
	 *
	 * @param createdDate the new created date
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	/**
	 * Gets the created date.
	 *
	 * @return createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}
	
	/**
	 * Gets the page path.
	 *
	 * @return pagePath
	 */
	public String getPagePath() {
		return CommonUtil.dotHtmlLink(pagePath);
	}
	
	/**
	 * Sets the page path.
	 *
	 * @param pagePath the new page path
	 */
	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}
	
	/**
	 * Gets the publication date.
	 *
	 * @return publicationDate
	 */
	public String getPublicationDate() {
		String publishDate = publicationDate;
		if(StringUtils.isBlank(publishDate)) {
			publishDate = getReplicationDate();
			if(StringUtils.isBlank(publishDate)) {
				publishDate = DEFAULT_DATE;
			}
		}
		return publishDate;
	}
	
	/**
	 * Sets the publication date.
	 *
	 * @param publicationDate the new publication date
	 */
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	/**
	 * Gets the replication date.
	 *
	 * @return the replication date
	 */
	public String getReplicationDate() {
		return replicationDate;
	}

	/**
	 * Sets the replication date.
	 *
	 * @param replicationDate the new replication date
	 */
	public void setReplicationDate(String replicationDate) {
		this.replicationDate = replicationDate;
	}
	
	/**
	 * Gets the headline.
	 *
	 * @return headline
	 */
	public String getHeadline() {
		return headline;
	}
	
	/**
	 * Sets the headline.
	 *
	 * @param headline the new headline
	 */
	public void setHeadline(String headline) {
		this.headline = headline;
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
	 * Gets the window fixed.
	 *
	 * @return windowFixed
	 */
	public String getNewWindow() {
		return newWindow;
	}

	/**
	 * Sets the window fixed.
	 *
	 * @param windowFixed the new window fixed
	 */
	public void setNewWindow(String newWindow) {
		this.newWindow = newWindow;
	}

	public String getEnableSourceTracking() {
		return enableSourceTracking;
	}

	public void setEnableSourceTracking(String enableSourceTracking) {
		this.enableSourceTracking = enableSourceTracking;
	}

	/**
	 * Gets the description feature.
	 *
	 * @return descriptionFeature
	 */
	public String getDescriptionFeature() {
		return descriptionFeature;
	}

	/**
	 * Sets the description feature.
	 *
	 * @param descriptionFeature the new description feature
	 */
	public void setDescriptionFeature(String descriptionFeature) {
		this.descriptionFeature = descriptionFeature;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}
}
