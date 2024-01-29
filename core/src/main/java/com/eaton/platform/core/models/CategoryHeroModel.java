package com.eaton.platform.core.models;
import javax.inject.Inject;
import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.DynamicMediaUrlService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.DynamicMediaUtils;

/**
 * <html> Description: It is a Sling Model class which automatically maps from Sling objects
 * i.e. dialog fields are injected </html> 
 * @author TCS
 * @version 1.0
 * @since 2017
 *
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 

public class CategoryHeroModel {
	
	@Inject
	private Page currentPage;
	
	/** The image alt text. */
	@Inject @Via("resource")
	private String imageAltText;
	
	/** The image path. */
	@Inject @Via("resource") 
	private String imagePath;
	
	/** The description. */
	@Inject @Via("resource") 
	private String description;
	
	/** The title text. */
	@Inject @Via("resource") 
	private String titleText;
	
	/** The title type. */
	@Inject @Via("resource") 
	private String categoryHeroHeaderType;

	/** The Eyebrow Text. */
	@Inject @Via("resource")
	private String eyebrowText;

	/** The Eyebrow Link URL. */
	@Inject @Via("resource")
	private String eyebrowLinkURL;
	
	/** The alignment desktop. */
	@Inject @Via("resource") 
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject @Via("resource") 
	private String alignmentMobile;
	
	/** The tablet trans. */
	@Inject @Via("resource") 
	private String tabletTrans;
	
	/** The desktop trans. */
	@Inject @Via("resource") 
	private String desktopTrans;
	
	/** The mobile trans. */
	@Inject @Via("resource") 
	private String mobileTrans;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
	/** The desktop dynam. */
	@Inject @Via("resource") 
	private String desktopDynam;

	/** The mobile dynam. */
	@Inject @Via("resource") 
	private String mobileDynam;
	
	/** The dynamic media url service. */
	@Inject
	private DynamicMediaUrlService dynamicMediaUrlService;

    /**
	 * if author has not entered any value in alt txt field in dialog box then
	 * return the image node name from DAM. 
	 *
	 * @return the image alt text
	 */
	public String getImageAltText() {
		String imageAlternateText = this.imageAltText;
		if(null == imageAlternateText) {
			imageAlternateText = CommonUtil.getAssetAltText(resourceResolver, imagePath);
		}
		return imageAlternateText;
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
	 * Gets the title text.
	 *
	 * @return the title text
	 */
	public String getTitleText() {
		return titleText;
	}

	/**
	 * Gets the title header type.
	 *
	 * @return the title header type 
	 */
	public String getCategoryHeroHeaderType() {
		return categoryHeroHeaderType;
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
     * Gets the desktop transformed url.
     *
     * @return the desktop transformed url
     */
	public String getDesktopTransformedUrl() {
		String desktopTransformedUrl;
		if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
			desktopTransformedUrl = getImagePath().trim().concat(CommonConstants.TRANSTORM).trim().concat(getDesktopTrans().trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			desktopTransformedUrl = getImagePath().trim();
		}
		return desktopTransformedUrl.trim();
	}
	/**
     * Gets the mobile transformed url.
     *
     * @return the mobile transformed url
     */
     public String getMobileTransformedUrl() {
    	 String mobileTransformedUrl;
    	 if(null != mobileTrans && !StringUtils.contains(mobileTrans,CommonConstants.DEFAULT_MOBILE)){
    		 mobileTransformedUrl = getImagePath().trim().concat(CommonConstants.TRANSTORM).trim().concat(getMobileTrans().trim()).concat(CommonConstants.IMAGE_JPG);
    	 } else {
    		 mobileTransformedUrl = getImagePath().trim();
    	 }
    	 return mobileTransformedUrl;
     }
     
     /**
      * Gets the tablet transformed url.
      *
      * @return the tablet transformed url
      */
     public String getTabletTransformedUrl() {

		if(getImagePath() != null && getMobileTrans() != null ) {
			if (null != tabletTrans && !StringUtils.contains(tabletTrans, CommonConstants.DEFAULT_TABLET)) {
				return getImagePath().trim().concat(CommonConstants.TRANSTORM).trim().concat(getMobileTrans().trim()).concat("/image.jpg");
			} else {
				return getImagePath().trim();
			}
		}
	    return StringUtils.EMPTY;
     }

	/**
	 * Gets the desktop trans.
	 *
	 * @return the desktopTrans
	 */
	public String getDesktopTrans() {
		return desktopTrans;
	}

	/**
	 * Gets the mobile trans.
	 *
	 * @return the mobileTrans
	 */
	public String getMobileTrans() {
		return mobileTrans;
	}
	
	/**
	 * Gets the cat link title.
	 *
	 * @return the cat link title
	 */
	public String getCatLinkTitle(){
		
		String heroLinktitle = StringUtils.EMPTY;
		Page categoryHeroParentPage = currentPage.getParent();

		if (StringUtils.isNotEmpty(categoryHeroParentPage.getNavigationTitle())) {
			heroLinktitle = categoryHeroParentPage.getNavigationTitle();
			}
			else if(StringUtils.isNotEmpty(categoryHeroParentPage.getPageTitle())) {
		    	heroLinktitle=categoryHeroParentPage.getPageTitle();
		    }
		    else if(StringUtils.isNotEmpty(categoryHeroParentPage.getTitle())) {
		    	heroLinktitle=categoryHeroParentPage.getTitle();
		  }

		return heroLinktitle;
	}

	public String getCurrentPageTitle()
	 {

		 Page titlePage = currentPage;
		 String returnTitle = StringUtils.EMPTY;
		 if(StringUtils.isNotEmpty(titlePage.getPageTitle()))
		 {
			 returnTitle = titlePage.getPageTitle();

		 }else if(StringUtils.isNotEmpty(titlePage.getTitle()))
		 {
			 returnTitle = titlePage.getTitle();
		 }

		 return returnTitle;
	 }

	/**
	 * Gets the cat link destination.
	 *
	 * @return the cat link destination
	 */
	public String getCatLinkDestination() {

		String catlink = currentPage.getParent().getPath();

		return CommonUtil.dotHtmlLink(catlink);
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

	/**
	 * @return the EyebrowText.
	 */
	public String getEyebrowText() {
		String eyebrowTitle = null;
		if(StringUtils.isNotEmpty(eyebrowText)){
			eyebrowTitle= eyebrowText;
		}
		else{
			if(null != currentPage.getParent().getNavigationTitle()) {
				eyebrowTitle = currentPage.getParent().getNavigationTitle();
			}else if(null != getParentPageTitle()){
				eyebrowTitle = getParentPageTitle();
			}
		}
		return eyebrowTitle;
	}

	/**
	 * @return the EyebrowLinkURL.
	 */
	public String getEyebrowLinkURL() {
		String url;
		if(StringUtils.isNotEmpty(eyebrowLinkURL)) {
			url = CommonUtil.dotHtmlLink(eyebrowLinkURL);
		}
		else {
			url=currentPage.getParent().getPath();
			url = CommonUtil.dotHtmlLink(url);
		}
		return url;
	}
	public String getParentPageTitle(){
	        Page titlePage = currentPage.getParent();
	        String returnTitle = StringUtils.EMPTY;
	        if(StringUtils.isNotEmpty(titlePage.getPageTitle()))
	        {
	            returnTitle = titlePage.getPageTitle();

	        }else if(StringUtils.isNotEmpty(titlePage.getTitle()))
	        {
	            returnTitle = titlePage.getTitle();
	        }

	        return returnTitle;
	 }
}


