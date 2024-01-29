package com.eaton.platform.core.bean;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * <html> Description: This bean class used in ContentHubAssetsModel class to store content </html> .
 *
 * 
 */
public class ContentHubAssetsBean implements Serializable {

	/** The Constant serialVersionUID. */	
	private static final long serialVersionUID = 3267200244905251891L;

	/** The asset title. */
	private String assetTitle;

	/** The asset path. */
	private String assetPath;

	/** The asset eyebrow. */
	private String assetEyebrowTag;

	/** The asset image. */
	private String assetImage;

	/** The new window. */
	private String newWindow;
	
	/** The CQ Tags */ 
	private String cqTags;
	
	/** The CQ Tags with path */
	private String[] cqTagsWithPath;
	
	/** The publication date. */
	private String publicationDate;

	/** The publication date display. */
	private String publicationDateDisplay;

	/** The desktop transformed url. */
	private String desktopTransformedUrl;

	/** The mobile transformed url. */
	private String mobileTransformedUrl;
	
	private ImageRenditionBean productImageBean;

	/** Type of the asset */
	private String assetType;

	/** Size of the asset */
	private String assetSize;

	/**
	 * Gets the asset title.
	 *
	 * @return assetTitle
	 */
	public String getAssetTitle() {
		return assetTitle;
	}

	/**
	 * Sets the asset title.
	 *
	 * @param linkTitle the new asset title
	 */
	public void setAssetTitle(String linkTitle) {
		this.assetTitle = linkTitle;
	}

	/**
	 * Gets the asset path.
	 *
	 * @return assetPath
	 */
	public String getAssetPath() {
		return this.assetPath;
	}

	/**
	 * Sets the asset path.
	 *
	 * @param assetPath the new asset path
	 */
	public void setAssetPath(String assetPath) {
		this.assetPath = assetPath;
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
	 * Gets the asset type.
	 *
	 * @return the assetType
	 */
	public String getAssetType() {
		return assetType;
	}

	/**
	 * Sets the asset type.
	 *
	 * @param assetType the new new window
	 */
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	/**
	 * Gets the asset size.
	 *
	 * @return the assetSize
	 */
	public String getAssetSize() {
		return assetSize;
	}

	/**
	 * Sets the asset size.
	 *
	 * @param assetSize the new new window
	 */
	public void setAssetSize(String assetSize) {
		this.assetSize = assetSize;
	}

	/**
	 * Gets the asset eyebrow.
	 *
	 * @return the asset eyebrow
	 */
	public String getAssetEyebrowTag() {
		return assetEyebrowTag;
	}

	/**
	 * Sets the asset eyebrow.
	 *
	 * @param assetEyebrowTag the new asset eyebrow
	 */
	public void setAssetEyebrowTag(String assetEyebrowTag) {
		this.assetEyebrowTag = assetEyebrowTag;
	}

	/**
	 * Gets the asset image.
	 *
	 * @return the asset image
	 */
	public String getAssetImage() {
		return assetImage;
	}

	/**
	 * Sets the asset image.
	 *
	 * @param assetImage the new asset image
	 */
	public void setAssetImage(String assetImage) {
		this.assetImage = assetImage;
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
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
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
		if(null!=getAssetImage()){
			if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
				desktopTransformedUrl = getAssetImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
			} else {
				desktopTransformedUrl = getAssetImage().trim();
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
		if(null!=getAssetImage()){
			if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
				mobileTransformedUrl = getAssetImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
			} else {
				mobileTransformedUrl = getAssetImage().trim();
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

	public void setPublicationDateDisplay() {
		String result = StringUtils.EMPTY;
		if(StringUtils.isNotBlank(getPublicationDate()) && !StringUtils.equals(CommonConstants.DEFAULT_DATE_TIME, getPublicationDate())) {
			result = getPublicationDate().substring(0, getPublicationDate().indexOf(CommonConstants.COLON));
			result = result.substring(0, result.length()-3);
		}
		this.publicationDateDisplay = result;
	}

}
