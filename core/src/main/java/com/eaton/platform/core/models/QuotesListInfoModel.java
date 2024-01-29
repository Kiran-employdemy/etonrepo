package com.eaton.platform.core.models;

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
 * <html> Description: This is a Sling Model for Quote List Component.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class QuotesListInfoModel {
	
	/** The image path. */
	@Inject
	private String imagePath;
	
	/** The image alt text. */
	@Inject
	private String imageAltText;
	
	/** The alignment desktop. */
	@Inject @Named("quoteAlignmentDesktop")
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject @Named("quoteAlignmentMobile")
	private String alignmentMobile;
	
	/** The description. */
	@Inject
	private String description;
	
	/** The author. */
	@Inject
	private String author;
	
	/** The author designation. */
	@Inject
	private String authorDesignation;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    /** The mobile transformed url. */
	@Inject
	private String mobileTransformedUrl;
	
	/** The desktop transformed url. */
	@Inject
	private String desktopTransformedUrl;

    
	/**
	 * Gets the image alt text.
	 *
	 * @return the image alt text
	 */
	public String getImageAltText() {
		String imageAlternateText = this.imageAltText;
		if(null == imageAlternateText) {
			imageAlternateText = CommonUtil.getAssetAltText(this.resourceResolver, this.imagePath);
		}
		return imageAlternateText;
	}

	/**
	 * Gets the image path.
	 *
	 * @return the image path
	 */
	public String getImagePath() {
		String imageInfoPath = this.imagePath;
		if(null == imageInfoPath) {
			imageInfoPath = StringUtils.EMPTY;
		}
		return imageInfoPath;
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
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the author.
	 *
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Gets the author designation.
	 *
	 * @return the author designation
	 */
	public String getAuthorDesignation() {
		return authorDesignation;
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

}
