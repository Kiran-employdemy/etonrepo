/**
 * 
 */
package com.eaton.platform.core.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;


/**
 * <html> Description: This bean class used in DownloadLinkListModel class to store content </html> .
 *
 * @author Eaton
 * @version 1.0
 * @since 2020
 */
public class DownloadLinkBean implements Serializable{
	
	private static final long serialVersionUID = 6856237390555375580L;

	/** The Constant DEFAULT_DATE. */
	private static final  String DEFAULT_DATE = "January 01, 0000 00:00:00.000+00:01";
	
	private String assetTitle;
	private String assetFileSize;
	private String assetFileType;
	private String assetLink;
	private String assetPublicationDate;
	private String assetDescription;
	/** SHA. */
	private String SHA;

	/** ECCN */
	private String ECCN;
	private boolean secure = false;
	
	/** The publication date. */
	private String publicationDate;
	
	/** The replication date. */
	private String replicationDate;
	
	/** The last modified date. */
	private String lastModifiedDate;
	
	/** The created date. */
	private String createdDate;

	private String trackDownload;
    private String eatonFileName;

	
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

	public String getTrackDownload() {
		return trackDownload;
	}

	public void setTrackDownload(String trackDownload) {
		this.trackDownload = trackDownload;
	}

	public String getSHA() {
		return SHA;
	}

	public void setSHA(String SHA) {
		this.SHA = SHA;
	}

	public String getECCN() {
		return ECCN;
	}

	public void setECCN(String ECCN) {
		this.ECCN = ECCN;
	}

    public String getEatonFileName() {
        return eatonFileName;
    }

    public void setEatonFileName(String eatonFileName) {
        this.eatonFileName = eatonFileName;
    }
}
