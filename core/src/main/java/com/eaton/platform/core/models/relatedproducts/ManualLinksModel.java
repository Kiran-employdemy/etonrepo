package com.eaton.platform.core.models.relatedproducts;

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

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.RelatedProductsService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in ViewContentList class
 *  to load Manual links multifield for Generic views</html> .
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ManualLinksModel  implements RelatedProductsService{

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
	 * Gets the link image.
	 *
	 * @return the link image
	 */
	@Override
	public String getLinkImage() {
		return linkImage;
	}

	/**
	 * Gets the link image alt text.
	 *
	 * @return the link image alt text
	 */
	@Override
	public String getAltText() {
		if(null == linkImageAltText) {
			linkImageAltText = CommonUtil.getAssetAltText(resourceResolver, getLinkImage());
		}
		return linkImageAltText;
	}

	/**
	 * Gets the link eyebrow.
	 *
	 * @return the link eyebrow
	 */
	@Override
	public String getLinkEyebrow() {
		return linkEyebrow;
	}

	/**
	 * Gets the trans link title.
	 *
	 * @return the trans link title
	 */
	@Override
	public String getLinkTitle() {
		String manualLinkTitle = this.linkTitle;
        // get the link title from page if link starts within the list of site root path configs.
		if(null == manualLinkTitle && CommonUtil.startsWithAnySiteContentRootPath(this.linkPath)) {
			manualLinkTitle = CommonUtil.getLinkTitle(this.linkTitle, this.linkPath, this.resourceResolver);
		}
		return manualLinkTitle;
	}

	/**
	 * Gets the page link.
	 *
	 * @return the page link
	 */
	@Override
	public String getLinkPath() {
	  return CommonUtil.dotHtmlLink(linkPath, resourceResolver);
	}

	/**
	 * Gets the new window manual.
	 *
	 * @return the new window manual
	 */
	@Override
	public String getNewWindow() {
		String manualLinkOpenNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, this.newWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
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
		if(null != this.linkPath && (StringUtils.startsWith(this.linkPath, CommonConstants.HTTP) || StringUtils.startsWith(this.linkPath, CommonConstants.HTTPS) || StringUtils.startsWith(this.linkPath, CommonConstants.WWW))) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}
	

	/**
	 * Gets the publication date.
	 *
	 * @return the publication date
	 */
	public String getPublicationDate() {
		String publicationDate = StringUtils.EMPTY;
		Resource linkPathResource = this.resourceResolver.getResource(this.linkPath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				SimpleDateFormat publicationDateFormat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_PUBLISH);
				publicationDate = CommonUtil.getDateProperty(pageProperties, CommonConstants.PUBLICATION_DATE, publicationDateFormat);
			}
		}
		return publicationDate;
	}

	 /**
    * Gets the desktop transformed url.
    *
    * @return the desktop transformed url
    */
	@Override
    public String getDesktopTransformedUrl() {
           return desktopTransformedUrl;
    }

    /**
     * Sets the desktop transformed url.
     *
     * @param desktopTrans the new desktop transformed url
     */
	@Override
    public void setDesktopTransformedUrl(String desktopTrans) {
 	   if(null!=linkImage.trim()){
    	  if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
        	desktopTransformedUrl = linkImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
        } else {
        	desktopTransformedUrl = linkImage.trim();
        }
 	   }
    }
   
    /**
    * Gets the mobile transformed url.
    *
    * @return the mobile transformed url
    */
	@Override
    public String getMobileTransformedUrl() {
          return mobileTransformedUrl;
    }
    
    /**
     * Sets the mobile transformed url.
     *
     * @param mobileTrans the new mobile transformed url
     */
	@Override
    public void setMobileTransformedUrl(String mobileTrans) {
    	 if(null!=linkImage.trim()){
	    	  if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
	            mobileTransformedUrl = linkImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	        } else {
	        	 mobileTransformedUrl = linkImage.trim();
	        }
    	 }
    }
}
