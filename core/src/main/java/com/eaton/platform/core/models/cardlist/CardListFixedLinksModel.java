package com.eaton.platform.core.models.cardlist;

import java.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.wcm.api.NameConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in CardListModel class
 *  to load Fixed links multifield for Generic views</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardListFixedLinksModel {

	/** The page link. */
	@Inject @Named("fixedLinkPath")
	private String pagePath;
	
	/** The new window. */
	@Inject @Named("fixedLinkOpenNewWindow")
	private String newWindow;
	
	/** The alignment desktop. */
	@Inject @Named("cardListAlignmentDesktop")
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject @Named("cardListAlignmentMobile")
	private String alignmentMobile;
	
	/** The mobile transformed url. */
	@Inject
	private String mobileTransformedUrl;
	
	/** The desktop transformed url. */
	@Inject
	private String desktopTransformedUrl;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    /** The isUnitedStatesDateFormat. */
	private boolean isUnitedStatesDateFormat;
	
    /**
     * @param isUnitedStatesDateFormat
     */
	public void setUnitedStatesDateFormat(boolean isUnitedStatesDateFormat) {
		this.isUnitedStatesDateFormat = isUnitedStatesDateFormat;
	}

    /**
     * Gets the link image.
     *
     * @return the link image
     */
    public String getTeaserImage() {
    	return getPageProperty(CommonConstants.TEASER_IMAGE_PATH);
    }
    
    /**
     * Gets the link image alt text.
     *
     * @return the link image alt text
     */
    public String getAltTxt() {
    	return CommonUtil.getAssetAltText(this.resourceResolver, getTeaserImage());
    }
 
	/**
	 * Gets the headline.
	 *
	 * @return headline
	 */
	public String getHeadline() {
		String teaserTitle = getPageProperty(CommonConstants.TEASER_TITLE); 
		return (StringUtils.isNotBlank(teaserTitle) ? teaserTitle : CommonUtil.getLinkTitle(null, this.pagePath, this.resourceResolver));
		
	}
	
	
	/**
	 * Gets the page link.
	 * 
	 * @return the page link
	 */
	public String getPagePath() {
		return CommonUtil.dotHtmlLink(this.pagePath, this.resourceResolver);
	}

	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	public String getNewWindow() {
		String fixedLinkOpenNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, this.newWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal()) ){
			fixedLinkOpenNewWindow = CommonConstants.TARGET_BLANK;
		}
		return fixedLinkOpenNewWindow;
	}
	
	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if(null != this.pagePath && StringUtils.startsWith(this.pagePath, CommonConstants.HTTP) || StringUtils.startsWith(this.pagePath, CommonConstants.HTTPS) || StringUtils.startsWith(this.pagePath, CommonConstants.WWW)) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}

	/**
	 * Gets the publication date.
	 *
	 * @return the publication date
	 */
	public String getPublicationDateDisplay() {
		String publicationDate = StringUtils.EMPTY;
		Resource linkPathResource = this.resourceResolver.getResource(this.pagePath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				SimpleDateFormat publicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
				if(isUnitedStatesDateFormat) {
					publicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
				}
				publicationDate = CommonUtil.getDateProperty(pageProperties, CommonConstants.PUBLICATION_DATE, publicationDateFormat);
				if(StringUtils.isEmpty(publicationDate)) {
					publicationDate = CommonUtil.getDateProperty(pageProperties,NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat);
				}
			}
		}
		return publicationDate;
	}
	
	/**
	 * Gets the link eyebrow.
	 *
	 * @return the link eyebrow
	 */
	public String getEyebrow() {
		return getPageProperty(CommonConstants.EYEBROW_TITLE);
	}

	/**
	 * Gets the page property.
	 *
	 * @param propertyName the property name
	 * @return the page property
	 */
	private String getPageProperty(String propertyName) {
		String eyebrow = StringUtils.EMPTY;
		Resource linkPathResource = this.resourceResolver.getResource(this.pagePath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				eyebrow = CommonUtil.getStringProperty(pageProperties, propertyName);
			}
		}
		return eyebrow;
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
	    	 if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
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
	    	 if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
	             mobileTransformedUrl = getTeaserImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	         } else {
	         	mobileTransformedUrl = getTeaserImage().trim();
	         }
    	 }
     }
     
}
