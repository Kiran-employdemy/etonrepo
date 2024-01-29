package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This is a Sling Model for Thumbnail Image List.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ThumbnailImageMultifieldModel {
	
	/** The thumbnail image. */
	@Inject
	private String thumbnailImage;
	
	/** The thumbnail image alt text. */
	@Inject
	private String thumbnailImageAltText;
	
	/** The image list link title. */
	@Inject
	private String imageListLinkTitle;
	
	/** The image list link path. */
	@Inject
	private String imageListLinkPath;
	
	/** The image list link open new window. */
	@Inject
	private String imageListLinkOpenNewWindow;
	
	/** The mobile transformed url. */
	@Inject
	private String mobileTransformedUrl;
	
	/** The desktop transformed url. */
	@Inject
	private String desktopTransformedUrl;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
	/**
	 * Gets the alt text.
	 *
	 * @return the alt text
	 */
	public String getAltText() {
		String imageAltText = thumbnailImageAltText;
		if(null == imageAltText) {
			imageAltText = CommonUtil.getAssetAltText(resourceResolver, thumbnailImage);
		}
		return imageAltText;
	}

	/**
	 * Gets the thumbnail image.
	 *
	 * @return the thumbnail image
	 */
	public String getThumbnailImage() {
		return thumbnailImage;
	}

	/**
	 * Gets the image list link path.
	 *
	 * @return the image list link path
	 */
	public String getImageListLinkPath() {
		return CommonUtil.dotHtmlLink(imageListLinkPath);
	}

	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	public String getNewWindow() {
		String newWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, imageListLinkOpenNewWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
			newWindow = CommonConstants.TARGET_BLANK;
		}
		return newWindow;
	}

	/**
	 * Gets the image list link title.
	 *
	 * @return the image list link title
	 */
	public String getImageListLinkTitle() {
		return CommonUtil.getLinkTitle(imageListLinkTitle, imageListLinkPath, resourceResolver);
	}
	
	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if(null != imageListLinkPath && (StringUtils.startsWith(imageListLinkPath, CommonConstants.HTTP) || StringUtils.startsWith(imageListLinkPath, CommonConstants.HTTPS) || StringUtils.startsWith(imageListLinkPath, CommonConstants.WWW))) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
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
     if(null!=thumbnailImage){
    	 if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
         	desktopTransformedUrl = thumbnailImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
         } else {
         	desktopTransformedUrl = thumbnailImage.trim();
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
    	if(null!=thumbnailImage){
    	 if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
             mobileTransformedUrl = thumbnailImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
         } else {
         	mobileTransformedUrl = thumbnailImage.trim();
         }
     }
   }
}
