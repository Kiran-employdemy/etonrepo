package com.eaton.platform.core.bean;

import java.io.Serializable;

public class ResourceListTagsBean implements Serializable {

	private static final long serialVersionUID = -1015511353688876509L;

	private String assetTitle;
	private String assetFileSize;
	private String assetFileType;
	private String assetLink;
	private String assetPublicationDate;
	private String newAsset;
	private String groupName;
	private boolean isExternal;
	private String anchorID;
	private String assetDescription;
	private boolean secure;
	private boolean isDrawingType = false;
	private String rendition140x100;
	private String rendition319x319;
	private String rendition48x48;
	private String bigImage;
	private String trackDownload;
	private String eccn;
	private String shaHash;
	private String eatonFileName;

	/**
	 * @return the assetTitle
	 */
	public String getAssetTitle() {
		return assetTitle;
	}
	/**
	 * @param assetTitle the assetTitle to set
	 */
	public void setAssetTitle(String assetTitle) {
		this.assetTitle = assetTitle;
	}
	/**
	 * @return the assetFileSize
	 */
	public String getAssetFileSize() {
		return assetFileSize;
	}
	/**
	 * @param assetFileSize the assetFileSize to set
	 */
	public void setAssetFileSize(String assetFileSize) {
		this.assetFileSize = assetFileSize;
	}
	/**
	 * @return the assetFileType
	 */
	public String getAssetFileType() {
		return assetFileType;
	}
	/**
	 * @param assetFileType the assetFileType to set
	 */
	public void setAssetFileType(String assetFileType) {
		this.assetFileType = assetFileType;
	}
	/**
	 * @return the assetLink
	 */
	public String getAssetLink() {
		return assetLink;
	}
	/**
	 * @param assetLink the assetLink to set
	 */
	public void setAssetLink(String assetLink) {
		this.assetLink = assetLink;
	}
	/**
	 * @return the assetPublicationDate
	 */
	public String getAssetPublicationDate() {
		return assetPublicationDate;
	}
	/**
	 * @param assetPublicationDate the assetPublicationDate to set
	 */
	public void setAssetPublicationDate(String assetPublicationDate) {
		this.assetPublicationDate = assetPublicationDate;
	}
	/**
	 * @return the newAsset
	 */
	public String getNewAsset() {
		return newAsset;
	}
	/**
	 * @param string the newAsset to set
	 */
	public void setNewAsset(String string) {
		this.newAsset = string;
	}

	public final String getGroupName() {
		return groupName;
	}

	public final void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	public final boolean isExternal() {
		return isExternal;
	}

	public final void setExternal(final boolean external) {
		isExternal = external;
	}

	public final String getAnchorID() {
		return anchorID;
	}

	public final void setAnchorID(String anchorID) {
		this.anchorID = anchorID;
	}

	public String getAssetDescription() {
		return assetDescription;
	}

	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public boolean isDrawingType() {
		return isDrawingType;
	}

	public void setDrawingType(boolean drawingType) {
		isDrawingType = drawingType;
	}

	public String getRendition140x100() {
		return rendition140x100;
	}

	public void setRendition140x100(String rendition140x100) {
		this.rendition140x100 = rendition140x100;
	}

	public String getRendition319x319() {
		return rendition319x319;
	}

	public void setRendition319x319(String rendition319x319) {
		this.rendition319x319 = rendition319x319;
	}

	public String getRendition48x48() {
		return rendition48x48;
	}

	public void setRendition48x48(String rendition48x48) {
		this.rendition48x48 = rendition48x48;
	}

	public String getBigImage() {
		return bigImage;
	}

	public void setBigImage(String bigImage) {
		this.bigImage = bigImage;
	}

	public String getTrackDownload() {
		return trackDownload;
	}

	public void setTrackDownload(String trackDownload) {
		this.trackDownload = trackDownload;
	}

	public String getEccn() {
		return eccn;
	}

	public void setEccn(String eccn) {
		this.eccn = eccn;
	}

	public String getShaHash() {
		return shaHash;
	}

	public void setShaHash(String shaHash) {
		this.shaHash = shaHash;
	}

	public String getEatonFileName() {
		return eatonFileName;
	}

	public void setEatonFileName(String eatonFileName) {
		this.eatonFileName = eatonFileName;
	}
}