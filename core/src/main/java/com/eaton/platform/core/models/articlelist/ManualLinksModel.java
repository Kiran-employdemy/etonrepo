package com.eaton.platform.core.models.articlelist;

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
 * <html> Description: This Sling Model used in ArticleListModel class
 *  to load Manual links multifield</html> .
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ManualLinksModel {

	/** The link image. */
	@Inject @Named("manualLinkImage")
	private String linkImage;
	
	/** The link image alt text. */
	@Inject @Named("manualLinkImageAltText")
	private String linkImageAltText;
	
	/** The link eyebrow. */
	@Inject @Named("manualLinkEyebrow")
	private String linkEyebrow;
	
	/** The link title. */
	@Inject @Named("manualLinkTitle")
	private String linkTitle;
	
	/** The link path. */
	@Inject @Named("manualLinkPath")
	private String linkPath;
	
	/** The new window. */
	@Inject @Named("manualLinkOpenNewWindow")
	private String newWindow;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    /** The mobile transformed url. */
	
	private String mobileTransformedUrl;
	
	/** The desktop transformed url. */
	
	private String desktopTransformedUrl;
	
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
	public String getLinkImage() {
		return linkImage;
	}

	/**
	 * Gets the link image alt text.
	 *
	 * @return the link image alt text
	 */
	public String getLinkImageAltText() {
		return linkImageAltText;
	}

	/**
	 * Gets the link eyebrow.
	 *
	 * @return the link eyebrow
	 */
	public String getLinkEyebrow() {
		return linkEyebrow;
	}

	/**
	 * Gets the trans link title.
	 *
	 * @return the trans link title
	 */
	public String getLinkTitle() {
		String manualLinkTitle = linkTitle;
        // get the link title from page if link starts within the list of site root path configs.
		if(null == manualLinkTitle && CommonUtil.startsWithAnySiteContentRootPath(linkPath)) {
			manualLinkTitle = CommonUtil.getLinkTitle(linkTitle, linkPath, resourceResolver);
		}
		return manualLinkTitle;
	}

	/**
	 * Gets the page link.
	 *
	 * @return the page link
	 */
	public String getLinkPath() {
		return CommonUtil.dotHtmlLink(this.linkPath,this.resourceResolver);
	}

	/**
	 * Gets the new window manual.
	 *
	 * @return the new window manual
	 */
	public String getNewWindow() {
		String manualLinkOpenNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, newWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
			manualLinkOpenNewWindow = CommonConstants.TARGET_BLANK;
		}
		return manualLinkOpenNewWindow;
	}

	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if(null != linkPath && (StringUtils.startsWith(linkPath, CommonConstants.HTTP) || StringUtils.startsWith(linkPath, CommonConstants.HTTPS) || StringUtils.startsWith(linkPath, CommonConstants.WWW))) {
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
		Resource linkPathResource = resourceResolver.getResource(linkPath);
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
    	 if(null!=getLinkImage()){
	         if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
	         	desktopTransformedUrl = getLinkImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	         } else {
	         	desktopTransformedUrl = getLinkImage().trim();
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
    	 if(null!=getLinkImage()){
		   	  if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
		             mobileTransformedUrl = getLinkImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		         } else {
		         	mobileTransformedUrl = getLinkImage().trim();
		         }
    	 }
     }

}
