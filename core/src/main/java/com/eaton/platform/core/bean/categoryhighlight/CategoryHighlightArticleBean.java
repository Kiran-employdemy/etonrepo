package com.eaton.platform.core.bean.categoryhighlight;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This bean class used in CategoryHighlightArticleBean
 * class to store content </html> .
 * 
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class CategoryHighlightArticleBean implements Serializable,CategoryHighLightArticleService{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5787490967828944572L;
	
	/** The Constant DEFAULT_DATE. */
	private static final  String DEFAULT_DATE = "January 01, 0000 00:00:00.000+00:01";
	
	/** The page path. */
	private String pagePath;

	/** The eyebrow. */
	private String eyebrow;

	/** The publication date. */
	private String publicationDate;
	
	/** The publicationDateDisplay. */
	private String publicationDateDisplay;
	
	/** The replication date. */
	private String replicationDate;
	
	/** The headline. */
	private String headline;

	/** The teaser image. */
	private String teaserImage;

	/** The last modified date. */
	private String lastModifiedDate;

	/** The created date. */
	private String createdDate;

	/** The template name. */
	private String templateName;

	/** The new window . */
	private String newWindow;

	/** The description feature. */
	private String descriptionFeature;

	/** The alt txt. */
	private String altTxt;

	/** Count of items to show. */
	private int count;
	
	/** The alignment desktop. */
	private String alignmentDesktop;

	/** The alignment mobile. */
	private String alignmentMobile;
	
	/** The desktop transformed url. */
	private String desktopTransformedUrl;

	/** The mobile transformed url. */
	private String mobileTransformedUrl;

	/** The tablet transformed url. */
	private String tabletTransformedUrl;

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
	 * @param lastModifiedDate
	 *            the new last modified date
	 */
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Sets the created date.
	 * 
	 * @param createdDate
	 *            the new created date
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
	 * Gets the page path.
	 * 
	 * @return pagePath
	 */
	public String getPagePath() {
		return CommonUtil.dotHtmlLink(pagePath);
	}

	/**
	 * Sets the page path.
	 * 
	 * @param pagePath
	 *            the new page path
	 */
	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	/**
	 * Gets the eyebrow.
	 * 
	 * @return eyebrow
	 */
	public String getEyebrow() {
		return eyebrow;
	}

	/**
	 * Sets the eyebrow.
	 * 
	 * @param eyebrow
	 *            the new eyebrow
	 */
	public void setEyebrow(String eyebrow) {
		this.eyebrow = eyebrow;
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
	 * Gets the publication date display.
	 *
	 * @return the publication date display
	 */
	public String getPublicationDateDisplay() {
		return publicationDateDisplay;
	}
	
	/**
	 * Sets the publication date display.
	 *
	 * @param publicationDateDisplay the publication date display.
	 */
	public void setPublicationDateDisplay(String publicationDateDisplay) {
		this.publicationDateDisplay = publicationDateDisplay;
	}
	/**
	 * Sets the publication date.
	 * 
	 * @param publicationDate
	 *            the new publication date
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
	 * Gets the headline.
	 * 
	 * @return headline
	 */
	public String getHeadline() {
		return headline;
	}

	/**
	 * Sets the headline.
	 * 
	 * @param headline
	 *            the new headline
	 */
	public void setHeadline(String headline) {
		this.headline = headline;
	}

	/**
	 * Gets the teaser image.
	 * 
	 * @return teaserInage
	 */
	public String getTeaserImage() {
		return teaserImage;
	}

	/**
	 * Sets the teaser image.
	 * 
	 * @param teaserImage
	 *            the new teaser image
	 */
	public void setTeaserImage(String teaserImage) {
		this.teaserImage = teaserImage;
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
	 * @param templateName
	 *            the new template name
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * Gets the window fixed.
	 * 
	 * @return windowFixed
	 */
	public String getNewWindow() {
		return newWindow;
	}

	/**
	 * Sets the window fixed.
	 * 
	 * @param newWindow
	 *            the new new window
	 */
	public void setNewWindow(String newWindow) {
		this.newWindow = newWindow;
	}

	/**
	 * Gets the description feature.
	 * 
	 * @return descriptionFeature
	 */
	public String getDescriptionFeature() {
		return descriptionFeature;
	}

	/**
	 * Sets the description feature.
	 * 
	 * @param descriptionFeature
	 *            the new description feature
	 */
	public void setDescriptionFeature(String descriptionFeature) {
		this.descriptionFeature = descriptionFeature;
	}

	/**
	 * Gets the alt txt.
	 * 
	 * @return altTxt
	 */
	public String getAltTxt() {
		return altTxt;
	}

	/**
	 * Sets the alt txt.
	 * 
	 * @param altTxt
	 *            the new alt txt
	 */
	public void setAltTxt(String altTxt) {
		this.altTxt = altTxt;
	}

	/**
	 * Gets the count.
	 * 
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 * 
	 * @param count
	 *            the new count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Gets the alignment desktop.
	 *
	 * @return the alignment desktop
	 */
	public String getAlignmentDesktop() {
		return alignmentDesktop;
	}

	/**
	 * Sets the alignment desktop.
	 *
	 * @param alignmentDesktop the new alignment desktop
	 */
	public void setAlignmentDesktop(String alignmentDesktop) {
		this.alignmentDesktop = alignmentDesktop;
	}

	/**
	 * Gets the alignment mobile.
	 *
	 * @return the alignment mobile
	 */
	public String getAlignmentMobile() {
		return alignmentMobile;
	}

	/**
	 * Sets the alignment mobile.
	 *
	 * @param alignmentMobile the new alignment mobile
	 */
	public void setAlignmentMobile(String alignmentMobile) {
		this.alignmentMobile = alignmentMobile;
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
		if(null!=getTeaserImage()){
		  if(StringUtils.isNotBlank(desktopTrans) && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
			desktopTransformedUrl = getTeaserImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		  } else {
			desktopTransformedUrl = getTeaserImage().trim();
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
		if(null!=getTeaserImage()){
		if (StringUtils.isNotBlank(mobileTrans) && !StringUtils.contains(mobileTrans,CommonConstants.DEFAULT_MOBILE)) {
			mobileTransformedUrl = getTeaserImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			mobileTransformedUrl = getTeaserImage().trim();
		}
	}
	}

	/**
	 * Gets the tablet transformed url.
	 *
	 * @return the tabletTransformedUrl
	 */
	public String getTabletTransformedUrl() {
		return tabletTransformedUrl;
	}

	/**
	 * Sets the tablet transformed url.
	 *
	 * @param tabletTrans the new tablet transformed url
	 */
	public void setTabletTransformedUrl(String tabletTrans) {
		if (StringUtils.isNotBlank(tabletTrans) && !StringUtils.contains(tabletTrans, CommonConstants.DEFAULT_TABLET)) {
			tabletTransformedUrl = getTeaserImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(tabletTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			tabletTransformedUrl = getTeaserImage().trim();
		}
	}


}
