/*
 * 
 */
package com.eaton.platform.core.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.eaton.platform.core.constants.CommonConstants;

/**
 * <html> Description: This bean class used in ImageGalleryHelper class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class ImageBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7130265809501204353L;

	/** The img path thumbnail. */
	private String imgPathThumbnail;
	
	/** The img path preview. */
	private String imgPathPreview;
	
	/** The img path zoom. */
	private String imgPathZoom;
	
	/** The img path download. */
	private String imgPathDownload;
	
	/** The title. */
	private String title;
	
	/** The description. */
	private String description;
	
	/** The is video. */
	private Boolean isVideo;
	
	/** The last modified date. */
	private String lastModifiedDate;
	
	/** The created date. */
	private String createdDate;

	/** The alt text. */
	private String altText;
	
	/**
	 * Gets the created date.
	 *
	 * @return the created date
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
	 * @return the last modified date
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
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the checks if is video.
	 *
	 * @return the checks if is video
	 */
	public Boolean getIsVideo() {
		return isVideo;
	}

	/**
	 * Sets the checks if is video.
	 *
	 * @param isVideo the new checks if is video
	 */
	public void setIsVideo(Boolean isVideo) {
		this.isVideo = isVideo;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the alt text.
	 *
	 * @return the alt text
	 */
	public String getAltText() {
		return altText;
	}

	/**
	 * Sets the alt text.
	 *
	 * @param altText the new alt text
	 */
	public void setAltText(String altText) {
		this.altText = altText;
	}

	/**
	 * Gets the img path thumbnail.
	 *
	 * @return the img path thumbnail
	 */
	public String getImgPathThumbnail() {
		return imgPathThumbnail;
	}

	/**
	 * Sets the img path thumbnail.
	 *
	 * @param imgPathThumbnail the new img path thumbnail
	 */
	public void setImgPathThumbnail(String thumbnailTrans) {
		 if(StringUtils.isNotBlank(thumbnailTrans) && !StringUtils.contains(thumbnailTrans, CommonConstants.DEFALUT_DESKTOP)){
			 imgPathThumbnail = getImgPathDownload().trim().concat(CommonConstants.TRANSTORM).trim().concat(thumbnailTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	     } else {
	         imgPathThumbnail = getImgPathDownload().trim();
	     }
	}

	/**
	 * Gets the img path preview.
	 *
	 * @return the img path preview
	 */
	public String getImgPathPreview() {
		return imgPathPreview;
	}

	/**
	 * Sets the img path preview.
	 *
	 * @param imgPathPreview the new img path preview
	 */
	public void setImgPathPreview(String alignmentDesktop) {
  	  	 if(StringUtils.isNotBlank(alignmentDesktop) && !StringUtils.contains(alignmentDesktop, CommonConstants.DEFALUT_DESKTOP)){
  	  		imgPathPreview = getImgPathDownload().trim().concat(CommonConstants.TRANSTORM).trim().concat(alignmentDesktop.trim()).concat(CommonConstants.IMAGE_JPG);
         } else {
        	imgPathPreview = getImgPathDownload().trim();
         }
	}

	/**
	 * Gets the img path zoom.
	 *
	 * @return the img path zoom
	 */
	public String getImgPathZoom() {
		return imgPathZoom;
	}

	/**
	 * Sets the img path zoom.
	 *
	 * @param imgPathZoom the new img path zoom
	 */
	public void setImgPathZoom(String alignmentDesktop) {
 	  	 if(StringUtils.isNotBlank(alignmentDesktop) && !StringUtils.contains(alignmentDesktop, CommonConstants.DEFALUT_DESKTOP)){
 	  		imgPathZoom = getImgPathDownload().trim().concat(CommonConstants.TRANSTORM).trim().concat(alignmentDesktop.trim()).concat(CommonConstants.IMAGE_JPG);
        } else {
        	imgPathZoom = getImgPathDownload().trim();
        }
	}

	/**
	 * Gets the img path download.
	 *
	 * @return the img path download
	 */
	public String getImgPathDownload() {
		return imgPathDownload;
	}

	/**
	 * Sets the img path download.
	 *
	 * @param imgPathDownload the new img path download
	 */
	public void setImgPathDownload(String imgPathDownload) {
		this.imgPathDownload = imgPathDownload;
	}

}
