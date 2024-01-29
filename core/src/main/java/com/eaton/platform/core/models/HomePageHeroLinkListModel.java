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
import com.eaton.platform.core.services.DynamicMediaUrlService;
import com.eaton.platform.core.util.DynamicMediaUtils;
import javax.jcr.Node;

/**
 * <html> Description: This is a Sling Model for Landing(L1) Hero and Category Hero Views.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HomePageHeroLinkListModel {

	/** The image alt text. */
	@Inject
	private String imageAltText;
	
	/** The image path. */
	@Inject
	private String imagePath;
	
	/** The link destination. */
	@Inject
	private String linkDestination;
	
	/** The new window. */
	@Inject
	private String newWindow;
	
	/** The description. */
	@Inject
	private String description;
	
	/** The title text. */
	@Inject
	private String titleText;
	
	/** The linkTitle. */
	@Inject
	private String linkTitle;
	
	/** The alignment desktop. */
	@Inject
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject
	private String alignmentMobile;
	
	 /** The desktop transformed url. */
    private String desktopTransformedUrl;
    
    /** The mobile transformed url. */
    private String mobileTransformedUrl;
	
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    /** The desktop dynam. */
	@Inject
	private String desktopDynam;

	/** The mobile dynam. */
	@Inject
	private String mobileDynam;

	/** The dynamic media url service. */
	@Inject
	private DynamicMediaUrlService dynamicMediaUrlService;
	
    
	/**
	 * Gets the image alt text.
	 *
	 * @return the image alt text
	 */
	public String getImageAltText() {
		String imageAltTextValue = this.imageAltText;
		if(null == imageAltTextValue) {
			imageAltTextValue = CommonUtil.getAssetAltText(this.resourceResolver, this.imagePath);
		}
		return imageAltTextValue;
	}

	/**
	 * Gets the image path.
	 *
	 * @return the image path
	 */
	public String getImagePath() {
		return imagePath;
	}
	
	/**
	 * Gets the desktop Dynam.
	 *
	 * @return the desktop Dynam
	 */
	public String getDesktopDynam() {
		return desktopDynam;
	}

	/**
	 * Gets the mobile Dynam.
	 *
	 * @return the mobile Dynam
	 */
	public String getMobileDynam() {
		return mobileDynam;
	}	

	/**
	 * Gets the link destination.
	 *
	 * @return the link destination
	 */
	public String getLinkDestination() {
		return CommonUtil.dotHtmlLink(linkDestination);
	}

	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	public String getNewWindow() {
		String newWindowOpen = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, this.newWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
			newWindowOpen = CommonConstants.TARGET_BLANK;
		}
		return newWindowOpen;
	}

	/**
	 * Gets the title text.
	 *
	 * @return the title text
	 */
	public String getTitleText() {
		return titleText;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the link title.
	 *
	 * @return the link title
	 */
	public String getLinkTitle() {
		return CommonUtil.getLinkTitle(this.linkTitle, this.linkDestination, this.resourceResolver);
	}
	
	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if((null != this.linkDestination) && (StringUtils.startsWith(this.linkDestination, CommonConstants.HTTP) || StringUtils.startsWith(this.linkDestination, CommonConstants.HTTPS) || StringUtils.startsWith(this.linkDestination, CommonConstants.WWW))) {
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
	 * Gets the alignment desktop.
	 *
	 * @return the alignment desktop
	 */
	public String getAlignmentDesktop() {
		return alignmentDesktop;
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
      * Sets the desktop transformed url.
      *
      * @param desktopTrans the new desktop transformed url
      */
     public void setDesktopTransformedUrl(String desktopTrans) {
    	 if(null!=getImagePath()){
    	  if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
         	desktopTransformedUrl = getImagePath().trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
         } else {
         	desktopTransformedUrl = getImagePath().trim();
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
      if(null!=getImagePath()){
    	 if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
             mobileTransformedUrl = getImagePath().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
         } else {
         	mobileTransformedUrl = getImagePath().trim();
         }
     }
     }
     
     /**
		 * Gets the desktop dynamic rendition url.
		 *
		 * @return the desktop dynamic rendition url
		 */
		public String getDesktopDynamicRenditionUrl() {
			String desktopDynamicRenditionUrl = null;
			if(null != desktopDynam && null != resourceResolver.getResource(imagePath.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
				Node imageMetadataNode = resourceResolver.getResource(imagePath.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(Node.class);
				String dynamicMediaPublishUrl = DynamicMediaUtils.getDynamicMediaPublishURL(imageMetadataNode, dynamicMediaUrlService.getDynamicMediaUrlBase());
				if(StringUtils.isNotBlank(dynamicMediaPublishUrl)) {
					desktopDynamicRenditionUrl = dynamicMediaPublishUrl.concat(desktopDynam);
				}
			}
			return desktopDynamicRenditionUrl;
		}

		/**
		 * Gets the mobile dynamic rendition url.
		 *
		 * @return the mobile dynamic rendition url
		 */
		public String getMobileDynamicRenditionUrl() {
			String mobileDynamicRenditionUrl = null;
			if(null != mobileDynam && null != resourceResolver.getResource(imagePath.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
				Node imageMetadataNode = resourceResolver.getResource(imagePath.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(Node.class);
				String dynamicMediaPublishUrl = DynamicMediaUtils.getDynamicMediaPublishURL(imageMetadataNode, dynamicMediaUrlService.getDynamicMediaUrlBase());
				if(StringUtils.isNotBlank(dynamicMediaPublishUrl)) {
					mobileDynamicRenditionUrl = dynamicMediaPublishUrl.concat(mobileDynam);
				}
			}
			return mobileDynamicRenditionUrl;
		}	
				

}
