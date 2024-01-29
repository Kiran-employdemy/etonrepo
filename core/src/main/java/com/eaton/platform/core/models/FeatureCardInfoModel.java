package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This is a Sling Model for Feature Card Component.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FeatureCardInfoModel {

	/** The card header. */
	@Inject
	private String cardHeader;

	/** The card image. */
	@Inject
	private String cardImage;

	/** The card image alt text. */
	@Inject
	private String cardImageAltText;

	/** The card eyebrow. */
	@Inject
	private String cardEyebrow;

	/** The card link path. */
	@Inject
	private String cardLinkPath;

	/** The card link title. */
	@Inject
	private String cardLinkTitle;
	
	/** The card link description. */
	@Inject
	private String cardLinkDesc;

	/** The card link open new window. */
	@Inject
	private String cardLinkOpenNewWindow;

	/** The enable teaser content. */
	@Inject
	private String enableTeaserContent;

	/** The desktop trans. */
	@Inject
	private String desktopTrans;

	/** The mobile trans. */
	@Inject
	private String mobileTrans;
	
	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	/**
	 * Gets the card header.
	 *
	 * @return the card header
	 */
	public String getCardHeader() {
		return cardHeader;
	}

	/**
	 * Gets the image alt text.
	 *
	 * @return the image alt text
	 */
	public String getAltText() {
		String imageAltText = cardImageAltText;
		if (null == imageAltText) {
			imageAltText = CommonUtil.getAssetAltText(resourceResolver, cardImage);
		}
		return imageAltText;
	}

	/**
	 * Gets the card image.
	 *
	 * @return the card image
	 */
	public String getCardImage() {
		String imagePath = cardImage;
		if (null == imagePath) {
			imagePath = StringUtils.EMPTY;
		}
		return imagePath;
	}

	/**
	 * Gets the card eyebrow.
	 *
	 * @return the card eyebrow
	 */
	public String getCardEyebrow() {
		return cardEyebrow;
	}

	/**
	 * Gets the card link path.
	 *
	 * @return the card link path
	 */
	public String getCardLinkPath() {
		return CommonUtil.dotHtmlLink(cardLinkPath);
	}

	/**
	 * Gets the card link title.
	 *
	 * @return the card link title
	 */
	public String getCardLinkTitle() {
		if(null == cardLinkTitle) {
			if(StringUtils.equals(getEnableTeaserContent(), CommonConstants.FALSE)) {
				cardLinkTitle =	CommonUtil.getLinkTitle(cardLinkTitle, cardLinkPath, resourceResolver);
			} else {
				cardLinkTitle = getPageProperty(CommonConstants.TEASER_TITLE);
			}
		}
		return cardLinkTitle;
	}
	/**
	 * Gets the card link description.
	 * 
	 * @return the card link description
	 */
	public String getCardLinkDesc(){
		return cardLinkDesc;
	}

	/**
	 * Gets the card link open new window.
	 *
	 * @return the card link open new window
	 */
	public String getCardLinkOpenNewWindow() {
		
		String openNewWindow = StringUtils.EMPTY;
		if (StringUtils.equals(CommonConstants.TRUE, cardLinkOpenNewWindow)
				|| StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {		
			openNewWindow = CommonConstants.TARGET_BLANK;
		}
		return openNewWindow;
	}
	
	/**
	 * Gets the checks if is external.
	 * 
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if (null != this.cardLinkPath
				&& StringUtils.startsWith(this.cardLinkPath, CommonConstants.HTTP)
				|| StringUtils.startsWith(this.cardLinkPath, CommonConstants.HTTPS)
				|| StringUtils.startsWith(this.cardLinkPath, CommonConstants.WWW)) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}

	/**
	 * Gets the enable teaser content.
	 *
	 * @return the enable teaser content
	 */
	public String getEnableTeaserContent() {
		if(null == enableTeaserContent) {
			enableTeaserContent = CommonConstants.FALSE;
		} else {
			enableTeaserContent = CommonConstants.TRUE;
		}
		return enableTeaserContent;
	}

	/**
	 * Gets the desktop trans.
	 *
	 * @return the desktop trans
	 */
	public String getDesktopTrans() {
		return desktopTrans;
	}

	/**
	 * Gets the mobile trans.
	 *
	 * @return the mobile trans
	 */
	public String getMobileTrans() {
		return mobileTrans;
	}

	/**
	 * Gets the page property.
	 *
	 * @param propertyName the property name
	 * @return the page property
	 */
	private String getPageProperty(String propertyName) {
		String propertyValue = StringUtils.EMPTY;
		Resource linkPathResource = this.resourceResolver.getResource(cardLinkPath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				propertyValue = CommonUtil.getStringProperty(pageProperties, propertyName);
			}
		}
		return propertyValue;
	}
	
	/**
     * Sets the desktop transformed url.
     *
     * @param desktopTrans the new desktop transformed url
     */
    public String getDesktopTransformedUrl() {
    	String desktopTransformedUrl =null;
    	 if(null!=getCardImage()){
    	 if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
        	desktopTransformedUrl = getCardImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(getDesktopTrans().trim()).concat(CommonConstants.IMAGE_JPG);
        } else {
        	desktopTransformedUrl = getCardImage().trim();
        }
    }
    	return desktopTransformedUrl;
    }
    
    /**
     * Sets the mobile transformed url.
     *
     * @param mobileTrans the new mobile transformed url
     */
    public String getMobileTransformedUrl() {
    String mobileTransformedUrl =null;	
    if(null!=getCardImage()){
	    if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
	            mobileTransformedUrl = getCardImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(getMobileTrans().trim()).concat(CommonConstants.IMAGE_JPG);
	        } else {
	        	mobileTransformedUrl = getCardImage().trim();
	        }
	    }
  	return mobileTransformedUrl;
    }
 
}
