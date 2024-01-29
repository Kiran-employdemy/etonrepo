package com.eaton.platform.core.bean;

/**
 * <html> Description: This bean class used in ProductFeatures class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class ProductFeaturesBean  {

	

	/** The page path. */
	private String text;
	private String title;
	private String imgSrc;
	private String imgAlt;
	private String backgroundColor;
	private String url;
	
	
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

	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	public String getImgAlt() {
		return imgAlt;
	}

	public void setImgAlt(String imgAlt) {
		this.imgAlt = imgAlt;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
