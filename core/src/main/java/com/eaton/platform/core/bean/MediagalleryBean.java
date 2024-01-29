package com.eaton.platform.core.bean;

import org.apache.commons.lang3.StringUtils;

public class MediagalleryBean {
	
	private String title;
	private String text;
	private boolean isVideo ;
	private boolean downloadEnabled;
	private boolean isZoomEnabled;
	private boolean isSpinImage;
	
	private String thumbnail;
	private String preview;
	private String zoom;
	private String download;
	private String altText = StringUtils.EMPTY;
	private String seq;
	
	
	/**
	 * @return the seq
	 */
	public String getSeq() {
		return seq;
	}
	/**
	 * @param seq the seq to set
	 */
	public void setSeq(String seq) {
		this.seq = seq;
	}
	/**
	 * @return the altText
	 */
	public String getAltText() {
		return altText;
	}
	/**
	 * @param altText the altText to set
	 */
	public void setAltText(String altText) {
		this.altText = altText;
	}
	/**
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}
	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	
	
	/**
	 * @return the preview
	 */
	public String getPreview() {
		return preview;
	}
	/**
	 * @param preview the preview to set
	 */
	public void setPreview(String preview) {
		this.preview = preview;
	}

	/**
	 * @return the preview
	 */
	public boolean getIsSpinImage() {
		return isSpinImage;
	}
	/**
	 * @param isSpinImage the preview to set
	 */
	public void setIsSpinImage(Boolean isSpinImage) {
		this.isSpinImage = isSpinImage;
	}

	/**
	 * @return the zoom
	 */
	public String getZoom() {
		return zoom;
	}
	/**
	 * @param zoom the zoom to set
	 */
	public void setZoom(String zoom) {
		this.zoom = zoom;
	}
	/**
	 * @return the download
	 */
	public String getDownload() {
		return download;
	}
	/**
	 * @param download the download to set
	 */
	public void setDownload(String download) {
		this.download = download;
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
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the isVideo
	 */
	public boolean isVideo() {
		return isVideo;
	}
	/**
	 * @param isVideo the isVideo to set
	 */
	public void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}
	/**
	 * @return the downloadEnabled
	 */
	public boolean getDownloadEnabled() {
		return downloadEnabled;
	}
	/**
	 * @param downloadEnabled the downloadEnabled to set
	 */
	public void setDownloadEnabled(boolean downloadEnabled) {
		this.downloadEnabled = downloadEnabled;
	}
	/**
	 * @return the isZoomEnabled
	 */
	public boolean isZoomEnabled() {
		return isZoomEnabled;
	}
	/**
	 * @param isZoomEnabled the isZoomEnabled to set
	 */
	public void setZoomEnabled(boolean isZoomEnabled) {
		this.isZoomEnabled = isZoomEnabled;
	}
	
	
	

}
