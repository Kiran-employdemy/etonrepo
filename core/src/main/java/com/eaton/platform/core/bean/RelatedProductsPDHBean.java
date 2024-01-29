package com.eaton.platform.core.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.RelatedProductsService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This bean class used in RelatedProductsPDHModel class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class RelatedProductsPDHBean implements Serializable, RelatedProductsService {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2847358913108419130L;
	
	/** The Constant DEFAULT_DATE. */
	private static final  String DEFAULT_DATE = "January 01, 0000 00:00:00.000+00:01";
	
	/** The link eyebrow. */
	private String linkEyebrow;
	
	/** The link title. */
	private String linkTitle;
	
	/** The link path. */
	private String linkPath; 
	
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
	
	/** The last modified date. */
	private String lastModifiedDate;
	
	/** The created date. */
	private String createdDate;
	
	/** The desktop transformed url. */
	private String desktopTransformedUrl;
	
	/** The mobile transformed url. */
	private String mobileTransformedUrl;

	/** The Marketing Description. */
	private String marketingDescription;

	/*Secure Flag*/
	private boolean isSecure;
	
	/**
	 * Gets the link eyebrow.
	 *
	 * @return the link eyebrow
	 */
	@Override
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
	 * Gets the link title.
	 *
	 * @return linkTitle
	 */
	@Override
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
	@Override
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
	@Override
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
	 * Sets the publication date.
	 *
	 * @param publicationDate the new publication date
	 */
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	/**
	 * Gets the link image alt text.
	 *
	 * @return the link image alt text
	 */
	@Override
	public String getAltText() {
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
	@Override
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
	@Override
     public String getDesktopTransformedUrl() {
		return desktopTransformedUrl;
     }
 
     /**
      * Sets the desktop transformed url.
      *
      * @param desktopTrans the new desktop transformed url
      */
	@Override
     public void setDesktopTransformedUrl(String desktopTrans) {
        if(null!=linkImage){
            String trimmedImageLink = linkImage.trim();
	        if(StringUtils.isNotBlank(desktopTrans) && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP) && StringUtils.startsWith(trimmedImageLink,CommonConstants.CONTENT_DAM)){
	            desktopTransformedUrl = trimmedImageLink.concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	         } else{
                desktopTransformedUrl = trimmedImageLink;
	         }
    	 }
     }
     
     /**
     * Gets the mobile transformed url.
     *
     * @return the mobile transformed url
     */
	@Override
     public String getMobileTransformedUrl() {
        return mobileTransformedUrl;
     }
     
     /**
      * Sets the mobile transformed url.
      *
      * @param mobileTrans the new mobile transformed url
      */
	@Override
     public void setMobileTransformedUrl(String mobileTrans) {
        if(null!=linkImage){
    	    String trimmedImageLink = linkImage.trim();
	        if(StringUtils.isNotBlank(mobileTrans) && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE) && StringUtils.startsWith(trimmedImageLink,CommonConstants.CONTENT_DAM)){
	            mobileTransformedUrl = trimmedImageLink.concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	        } else{
                mobileTransformedUrl = trimmedImageLink;
	        }
        }
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

	public String getMarketingDescription() {
		return marketingDescription;
	}

	public void setMarketingDescription(String marketingDescription) {
		this.marketingDescription = marketingDescription;
	}

	/**
	 *
	 * @return isSecure flag
	 */
	public boolean getIsSecure() {
		return isSecure;
	}

	/**
	 *
	 * @param isSecure
	 */
	public void setIsSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}
}
