package com.eaton.platform.core.models.featuredstory;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This is a Sling Model for Feature Story</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FeatureStoryModel {

	/** The feature story image. */
	@Inject
	private String featureStoryImage;
	
	/** The feature story image alt text. */
	@Inject
	private String featureStoryImageAltText;
	
	/** The alignment desktop. */
	@Inject @Named("featureStoryAlignmentDesktop")
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject @Named("featureStoryAlignmentMobile")
	private String alignmentMobile;
	
	/** The feature story eyebrow. */
	@Inject
	private String featureStoryEyebrow;
	
	/** The feature story link title. */
	@Inject
	private String featureStoryLinkTitle;
	
	/** The feature story link path. */
	@Inject
	private String featureStoryLinkPath;
	
	/** The tablet transformed url. */
	@Inject
	private String tabletTransformedUrl;
	
	/** The mobile transformed url. */
	@Inject
	private String mobileTransformedUrl;
	
	/** The desktop transformed url. */
	@Inject
	private String desktopTransformedUrl;
	
	/** The feature story link open new window. */
	@Inject
	private String featureStoryLinkOpenNewWindow;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
	/**
	 * Gets the feature story image alt text.
	 *
	 * @return the feature story image alt text
	 */
	public String getAltText() {
		String imageAltText = featureStoryImageAltText;
		if(null == imageAltText) {
			imageAltText = CommonUtil.getAssetAltText(resourceResolver, featureStoryImage);
		}
		return imageAltText;
	}

	/**
	 * Gets the feature story image.
	 *
	 * @return the feature story image
	 */
	public String getFeatureStoryImage() {
		String imagePath = StringUtils.EMPTY;
		if(null != featureStoryImage) {
			imagePath = featureStoryImage;
		}
		return imagePath;
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
	 * Gets the feature story link path.
	 *
	 * @return the feature story link path
	 */
	public String getFeatureStoryLinkPath() {
		return CommonUtil.dotHtmlLink(featureStoryLinkPath);
	}

	/**
	 * Gets the feature story link open new window.
	 *
	 * @return the feature story link open new window
	 */
	public String getFeatureStoryLinkOpenNewWindow() {
		String newWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, featureStoryLinkOpenNewWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
			newWindow = CommonConstants.TARGET_BLANK;
		}
		return newWindow;
	}

	/**
	 * Gets the feature story eyebrow.
	 *
	 * @return the feature story eyebrow
	 */
	public String getFeatureStoryEyebrow() {
		return featureStoryEyebrow;
	}

	/**
	 * Gets the feature story link title.
	 *
	 * @return the feature story link title
	 */
	public String getFeatureStoryLinkTitle() {
		return CommonUtil.getLinkTitle(featureStoryLinkTitle, featureStoryLinkPath, resourceResolver);
	}
	
	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if((null != featureStoryLinkPath) && (StringUtils.startsWith(featureStoryLinkPath, CommonConstants.HTTP) || StringUtils.startsWith(featureStoryLinkPath, CommonConstants.HTTPS) || StringUtils.startsWith(featureStoryLinkPath, CommonConstants.WWW))) {
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
     if(null!=featureStoryImage){
         if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
         	desktopTransformedUrl = featureStoryImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
         } else {
         	desktopTransformedUrl = featureStoryImage.trim();
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
     if(null!=featureStoryImage){
   	  	 if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
             mobileTransformedUrl = featureStoryImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
         } else {
         	 mobileTransformedUrl = featureStoryImage.trim();
         }
   	  }
     }
     
     /**
     * Gets the tablet transformed url.
     *
     * @return the tablet transformed url
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
		 if(null!=featureStoryImage) {
			 if (null != tabletTrans && !StringUtils.contains(tabletTrans, CommonConstants.DEFAULT_TABLET)) {
				 tabletTransformedUrl = featureStoryImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(tabletTrans.trim()).concat(CommonConstants.IMAGE_JPG);
			 } else {
				 tabletTransformedUrl = featureStoryImage.trim();
			 }
		 }
     }
     
}
