package com.eaton.platform.core.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This bean class used in ArticleListModel class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class ArticleListBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5203647056732380514L;
	
	/** The Constant DEFAULT_DATE. */
	private static final  String DEFAULT_DATE = "January 01, 0000 00:00:00.000+00:01";
	
	/** The link title. */
	private String linkTitle;
	
	/** The link path. */
	private String linkPath; 
	
	/** The link eyebrow. */
	private String linkEyebrow;
	
	/** The link image alt text. */
	private String linkImageAltText;
	
	/** The link image. */
	private String linkImage;
	
	/** The new window. */
	private String newWindow;
	
	/** The template name. */
	private String templateName;
	
	/** The replication date. */
	private String replicationDate;
	
	/** The publication date. */
	private String publicationDate;
	
	/** The publicationDateDisplay. */
	private String publicationDateDisplay;
	
	/** The last modified date. */
	private String lastModifiedDate;
	
	/** The created date. */
	private String createdDate;
	
	/** The desktop transformed url. */
	private String desktopTransformedUrl;
	
	/** The mobile transformed url. */
	private String mobileTransformedUrl;
	
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
	public String getLinkPath() {
		return this.linkPath;
	}
	
	/**
	 * Sets the link path.
	 *
	 * @param linkPath the new link path
	 */
	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}
	
	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	public String getNewWindow() {
		return newWindow;
	}

	/**
	 * Sets the new window.
	 *
	 * @param newWindow the new new window
	 */
	public void setNewWindow(String newWindow) {
		this.newWindow = newWindow;
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
	 * Gets the created date.
	 *
	 * @return createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
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
	 * Gets the publication date display.
	 *
	 * @return the publication date display
	 */
	public String getPublicationDateDisplay() {
		return publicationDateDisplay;
	}
	
	/**
	 * Sets the publication date display.
	 *
	 * @param publicationDateDisplay the publication date display.
	 */
	public void setPublicationDateDisplay(String publicationDateDisplay) {
		this.publicationDateDisplay = publicationDateDisplay;
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
	 * Gets the link eyebrow.
	 *
	 * @return the link eyebrow
	 */
	public String getLinkEyebrow() {
		return linkEyebrow;
	}

	/**
	 * Sets the link eyebrow.
	 *
	 * @param linkEyebrow the new link eyebrow
	 */
	public void setLinkEyebrow(String linkEyebrow) {
		this.linkEyebrow = linkEyebrow;
	}

	/**
	 * Gets the link image alt text.
	 *
	 * @return the link image alt text
	 */
	public String getLinkImageAltText() {
		return linkImageAltText;
	}

	/**
	 * Sets the link image alt text.
	 *
	 * @param linkImageAltText the new link image alt text
	 */
	public void setLinkImageAltText(String linkImageAltText) {
		this.linkImageAltText = linkImageAltText;
	}

	/**
	 * Gets the link image.
	 *
	 * @return the link image
	 */
	public String getLinkImage() {
		return linkImage;
	}

	/**
	 * Sets the link image.
	 *
	 * @param linkImage the new link image
	 */
	public void setLinkImage(String linkImage) {
		this.linkImage = linkImage;
	}
	
	/**
     * Gets the desktop transformed url.
     *
     * @return the desktop transformed url
     */
     public String getDesktopTransformedUrl() {
            return desktopTransformedUrl;
     }
 
     /**
      * Sets the desktop transformed url.
      *
      * @param desktopTrans the new desktop transformed url
      */
     public void setDesktopTransformedUrl(String desktopTrans) {
    	 if(null!=getLinkImage()){
	         if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
	         	desktopTransformedUrl = getLinkImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	         } else {
	         	desktopTransformedUrl = getLinkImage().trim();
	         }
    	 }
     }
     
     /**
     * Gets the mobile transformed url.
     *
     * @return the mobile transformed url
     */
     public String getMobileTransformedUrl() {
            return mobileTransformedUrl;
     }
     
     /**
      * Sets the mobile transformed url.
      *
      * @param mobileTrans the new mobile transformed url
      */
     public void setMobileTransformedUrl(String mobileTrans) {
    	 if(null!=getLinkImage()){
	         if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
	             mobileTransformedUrl = getLinkImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	         } else {
	         	mobileTransformedUrl = getLinkImage().trim();
	         }
    	}
     }
}
