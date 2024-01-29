/**
 * Interface CategoryHighLightArticleService for
 * Category Highlight Article Component
 * @author E0362677
 *
 */
package com.eaton.platform.core.models.categoryhighlightarticle;


/**
 * Interface CategoryHighLightArticleService
 * @author E0362677
 *
 */
public interface CategoryHighLightArticleService {

	/**
	 * Gets the link image.
	 * 
	 * @return the link image
	 */
	String getTeaserImage();

	/**
	 * Gets the link image alt text.a
	 * 
	 * @return the link image alt text
	 */
	String getAltTxt();

	/**
	 * Gets the headline.
	 * 
	 * @return headline
	 */
	String getHeadline();

	/**
	 * Gets the page link.
	 * 
	 * @return the page link
	 */
	String getPagePath();

	/**
	 * Gets the new window.
	 * 
	 * @return the new window
	 */
	String getNewWindow();


	/**
	 * Gets the publication date.
	 * 
	 * @return the publication date
	 */
	String getPublicationDateDisplay();

	/**
	 * Gets the link eyebrow.
	 * 
	 * @return the link eyebrow
	 */
	String getEyebrow();

	/**
	 * Gets the alignment desktop.
	 *
	 * @return the alignment desktop
	 */
	String getAlignmentDesktop();

	/**
	 * Sets the alignment desktop.
	 *
	 * @param alignmentDesktop the new alignment desktop
	 */
	void setAlignmentDesktop(String alignmentDesktop);

	/**
	 * Gets the alignment mobile.
	 *
	 * @return the alignment mobile
	 */
	String getAlignmentMobile();

	/**
	 * Sets the alignment mobile.
	 *
	 * @param alignmentMobile the new alignment mobile
	 */
	void setAlignmentMobile(String alignmentMobile);

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
	 * Gets the tablet transformed url.
	 *
	 * @return the tabletTransformedUrl
	 */
	String getTabletTransformedUrl();

	/**
	 * Sets the tablet transformed url.
	 *
	 * @param tabletTrans the new tablet transformed url
	 */
	void setTabletTransformedUrl(String tabletTrans);
}
