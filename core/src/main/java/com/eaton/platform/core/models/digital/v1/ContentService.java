package com.eaton.platform.core.models.digital.v1;

import java.util.List;

public interface ContentService {

	/**
	 * Gets the link eyebrow.
	 *
	 * @return the link eyebrow
	 */
	String getLinkEyebrow();

	/**
	 * Gets the link title.
	 *
	 * @return the link Title
	 */
	String getLinkTitle();

	List<ContentTileButtonMultiField> getCtaButtons();
	/**
	 * Gets the link page path.
	 *
	 * @return linkPagePath
	 */
	String getLinkPath();
	
	/**
	 * Gets the link image alt text.
	 *
	 * @return the link image alt text
	 */
	String getAltText();

	/**
	 * Gets the link image.
	 *
	 * @return the link image
	 */
	String getLinkImage();

	/**
	 * Gets the desktop transformed url.
	 *
	 * @return the desktop transformed url
	 */
	String getDesktopTransformedUrl();

	/**
	 * Sets the desktop transformed url.
	 *
	 * @param desktopTrans the new desktop transformed url
	 */
	void setDesktopTransformedUrl(String desktopTrans);

	/**
	 * Gets the mobile transformed url.
	 *
	 * @return the mobile transformed url
	 */
	String getMobileTransformedUrl();

	/**
	 * Sets the mobile transformed url.
	 *
	 * @param mobileTrans the new mobile transformed url
	 */
	void setMobileTransformedUrl(String mobileTrans);
	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	 String getNewWindow();

}