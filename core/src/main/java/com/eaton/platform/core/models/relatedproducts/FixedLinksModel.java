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
 *  to load Fixed links multifield for Generic views</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FixedLinksModel implements RelatedProductsService{

	/** The page link. */
	@Inject @Named("fixedLinkPath")
	private String linkPath;
	
	/** The new window. */
	@Inject @Named("fixedLinkOpenNewWindow")
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
    	return getPageProperty(CommonConstants.TEASER_IMAGE_PATH);
    }
    
    /**
     * Gets the link image alt text.
     *
     * @return the link image alt text
     */
    @Override
    public String getAltText() {
    	return CommonUtil.getAssetAltText(resourceResolver, getLinkImage());
    }
	/**
	 * Gets the link title.
	 *
	 * @return the link title
	 */
    @Override
	public String getLinkTitle() {
		return CommonUtil.getLinkTitle(null, linkPath, resourceResolver);
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
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	@Override
	public String getNewWindow() {
		String fixedLinkOpenNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, this.newWindow)) {
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
		if(null != this.linkPath && StringUtils.startsWith(this.linkPath, CommonConstants.HTTP) ||
				StringUtils.startsWith(this.linkPath, CommonConstants.HTTPS) ||
				StringUtils.startsWith(this.linkPath, CommonConstants.WWW)) {
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
	 * Gets the link eyebrow.
	 *
	 * @return the link eyebrow
	 */
	@Override
	public String getLinkEyebrow() {
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
		Resource linkPathResource = this.resourceResolver.getResource(this.linkPath);
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
        if(null!=getLinkImage()){
        	if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
	            mobileTransformedUrl = getLinkImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	        } else {
                mobileTransformedUrl = getLinkImage().trim();
	        }
        }
    }
}
