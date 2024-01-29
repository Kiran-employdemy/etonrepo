package com.eaton.platform.core.bean;

import java.io.Serializable;

import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This bean class used in ListHelper class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class ListTypeBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7130265800501204353L;

	/** The page path. */
	private String pagePath;
	
	/** The eyebrow. */
	private String eyebrow;
	
	/** The publication date. */
	private String publicationDate;
	
	/** The headline. */
	private String headline;
	
	/** The teaser image. */
	private String teaserImage;
	
	/** The last modified date. */
	private String lastModifiedDate;
	
	/** The created date. */
	private String createdDate;
	
	/** The template name. */
	private String templateName;
	
	/** The new  window . */
	private String newWindow;
	
	/** The description feature. */
	private String descriptionFeature;
	
	/** The alt txt. */
	private String altTxt;
	
	/**  Count of items to show. */
	private int count;
	
	/** The window fixed. */
	private String windowFixed;

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
	 * Gets the eyebrow.
	 *
	 * @return eyebrow
	 */
	public String getEyebrow() {
		return eyebrow;
	}
	
	/**
	 * Sets the eyebrow.
	 *
	 * @param eyebrow the new eyebrow
	 */
	public void setEyebrow(String eyebrow) {
		this.eyebrow = eyebrow;
	}
	
	/**
	 * Gets the publication date.
	 *
	 * @return publicationDate
	 */
	public String getPublicationDate() {
		return publicationDate;
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
	 * Gets the teaser image.
	 *
	 * @return teaserInage
	 */
	public String getTeaserImage() {
		return teaserImage;
	}
	
	/**
	 * Sets the teaser image.
	 *
	 * @param teaserImage the new teaser image
	 */
	public void setTeaserImage(String teaserImage) {
		this.teaserImage = teaserImage;
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
	 * @param newWindow the new new window
	 */
	public void setNewWindow(String newWindow) {
		this.newWindow = newWindow;
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

	/**
	 * Gets the alt txt.
	 *
	 * @return altTxt
	 */
	public String getAltTxt() {
		return altTxt;
	}

	/**
	 * Sets the alt txt.
	 *
	 * @param altTxt the new alt txt
	 */
	public void setAltTxt(String altTxt) {
		this.altTxt = altTxt;
	}
	
	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count the new count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Gets the window fixed.
	 *
	 * @return the window fixed
	 */
	public String getWindowFixed() {
		return windowFixed;
	}

	/**
	 * Sets the window fixed.
	 *
	 * @param windowFixed the new window fixed
	 */
	public void setWindowFixed(String windowFixed) {
		this.windowFixed = windowFixed;
	}

}
