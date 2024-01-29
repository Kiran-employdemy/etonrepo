package com.eaton.platform.integration.endeca.bean.sitesearch;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eaton.platform.core.bean.sitesearch.SecondaryLink;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

public class SiteSearchResultsBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7595058603266879727L;

	private String title;
	private String description;
	private String url;
	private String urlTarget;
	private String completeUrl ;
	private String image;
	private String publishDate;
	private String epochPublishDate;
	private String fileType;
	private String fileSize;
	private String middleTabName;
	private String middleTabURL;
	private String contentType;
	private String linkType;
	private String status;
	private boolean newBadgeVisible;
	private List<SecondaryLink> secondaryLinkList;

	private String dsktopRendition;
	private String mobileRendition;

	private boolean secure;

	private String trackDownload;
	private String language;
	private String eatonSHA;
	private String eatonECCN;

	private String ecommPageType;
	private String buyPageReference;
	private String dcFormat;

	/**
	 * @return the dsktopRendition
	 */
	public String getDsktopRendition() {
		return dsktopRendition;
	}
	/**
	 * @param dsktopRendition the dsktopRendition to set
	 */
	public void setDsktopRendition(String dsktopRendition) {
		this.dsktopRendition = dsktopRendition;
	}
	/**
	 * @return the mobileRendition
	 */
	public String getMobileRendition() {
		return mobileRendition;
	}
	/**
	 * @param mobileRendition the mobileRendition to set
	 */
	public void setMobileRendition(String mobileRendition) {
		this.mobileRendition = mobileRendition;
	}

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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * @return the publishDate
	 */
	public String getPublishDate() {
		return publishDate;
	}
	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}
	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	/**
	 * @return the fileSize
	 */
	public String getFileSize() {
		return fileSize;
	}
	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * @return the middleTabName
	 */
	public String getMiddleTabName() {
		return middleTabName;
	}
	/**
	 * @param middleTabName the middleTabName to set
	 */
	public void setMiddleTabName(String middleTabName) {
		this.middleTabName = middleTabName;
	}
	/**
	 * @return the middleTabURL
	 */
	public String getMiddleTabURL() {
		return middleTabURL;
	}
	/**
	 * @param middleTabURL the middleTabURL to set
	 */
	public void setMiddleTabURL(String middleTabURL) {
		this.middleTabURL = middleTabURL;
	}

	 @Override
	 public String toString() {
    	 StringBuilder result = new StringBuilder();
    	    String newLine = System.getProperty("line.separator");

    	    result.append(newLine);

    	    //determine fields declared in this class only (no fields of superclass)
    	    Field[] fields = this.getClass().getDeclaredFields();

    	    //print field names paired with their values
    	    for (Field field : fields) {
    	    	if(!field.getName().equalsIgnoreCase("serialVersionUID")){
    	    		 result.append("  ");
    	    	      try {
    	    	        result.append(field.getName());
    	    	        result.append(": ");
    	    	        //requires access to private field:
    	    	        result.append(field.get(this));
    	    	      }
    	    	      catch (IllegalAccessException ex) {
    	    	    	  return "Error in toString of SiteSearchResultsBean"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }

	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getLinkType() {
		return linkType;
	}
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public List<SecondaryLink> getSecondaryLinkList() {
		return secondaryLinkList;
	}
	public void setSecondaryLinkList(List<SecondaryLink> secondaryLinkList) {
		this.secondaryLinkList = secondaryLinkList;
	}
	public String getCompleteUrl() {
		return completeUrl;
	}
	public void setCompleteUrl(String completeUrl) {
		this.completeUrl = completeUrl;
	}
	public String getUrlTarget() {
		return urlTarget;
	}
	public void setUrlTarget(String urlTarget) {
		this.urlTarget = urlTarget;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public String getEpochPublishDate() {
		return epochPublishDate;
	}

	public void setEpochPublishDate(String epochPublishDate) {
		this.epochPublishDate = epochPublishDate;
	}

	public void setTrackDownload(String trackDownload) {
		this.trackDownload = trackDownload;
	}

	public String getTrackDownload() {
		return trackDownload;
	}


	public boolean getnewBadgeVisible() {
		return newBadgeVisible;
	}

	public void setnewBadgeVisible(boolean newBadgeVisible) {
		this.newBadgeVisible = newBadgeVisible;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLanguage() {
		return language;
	}
	public String getEcommPageType() {
		return ecommPageType;
	}

	public void setEcommPageType(String ecommPageType) {
		this.ecommPageType = ecommPageType;
	}

	public String getBuyPageReference() {
		return buyPageReference;
	}

	public void setBuyPageReference(String buyPageReference) {
		this.buyPageReference = buyPageReference;
	}

	public String getEatonSHA() {
		return eatonSHA;
	}

	public void setEatonSHA(String eatonSHA) {
		this.eatonSHA = eatonSHA;
	}

	public String getEatonECCN() {
		return eatonECCN;
	}

	public void setEatonECCN(String eatonECCN) {
		this.eatonECCN = eatonECCN;
	}

	public String getDcFormat() {
		return dcFormat;
	}
	public void setDcFormat(String dcFormat) {
		this.dcFormat = dcFormat;
	}

}
