package com.eaton.platform.core.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This bean class used in ContentHubModel class to store content </html> .
 *
 * 
 */
public class ContentHubBean implements Serializable {

	/** The Constant serialVersionUID. */	
	private static final long serialVersionUID = 3267200244905251891L;

	/** The link title. */
	private String linkTitle;

	/** The link path. */
	private String linkPath; 

	/** The link eyebrow. */
	private String linkEyebrow;
	
	/** The eyebrow link path. */
	private String eyebrowLink; 

	/** The link image alt text. */
	private String linkImageAltText;

	/** The link image. */
	private String linkImage;

	/** The new window. */
	private String newWindow;

	/** The template name. */
	private String templateName;
	
	/** The CQ Tags */ 
	private String cqTags;
	
	/** The CQ Tags */ 
	private String[] cqTagsWithPath;
	
	/** The publication date. */
	private String publicationDate;
	
	/** The replication date. */
	private String replicationDate;
	
	/** The publication date display. */
	private String publicationDateDisplay;

	/** The desktop transformed url. */
	private String desktopTransformedUrl;

	/** The mobile transformed url. */
	private String mobileTransformedUrl;
	
	private ImageRenditionBean productImageBean;

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
	 * Gets the eyebrowLink.
	 *
	 * @return eyebrowLink
	 */
	public String getEyebrowLink() {
		return this.eyebrowLink;
	}

	/**
	 * Sets the eyebrowLink.
	 *
	 * @param eyebrowLink the new link 
	 */
	public void setEyebrowLink(String eyebrowLink) {
		this.eyebrowLink = eyebrowLink;
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
	 * Gets the CQ tags.
	 *
	 * @return cq tags
	 */
	public String getCqTags() {
		return cqTags;
	}
	
	/**
	 * Sets the CQ tags.
	 *
	 * @param cqTags 
	 */
	public void setCqTags(String cqTags) {
		this.cqTags = cqTags;
	}
	
	public String[] getCqTagsWithPath() {
		return cqTagsWithPath;
	}

	public void setCqTagsWithPath(String[] cqTagsWithPath) {
		this.cqTagsWithPath = cqTagsWithPath;
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
				publishDate = CommonConstants.DEFAULT_DATE_TIME;
			}
		}
		return publishDate;
	}

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
	
	public ImageRenditionBean getProductImageBean() {
		return productImageBean;
	}
	public void setProductImageBean(ImageRenditionBean productImageBean) {
		this.productImageBean = productImageBean;
	}
	
	public String getPublicationDateDisplay() {
		return publicationDateDisplay;
	}

	public void setPublicationDateDisplay(String publicationDateDisplay) {
		this.publicationDateDisplay = publicationDateDisplay;
	}

}
