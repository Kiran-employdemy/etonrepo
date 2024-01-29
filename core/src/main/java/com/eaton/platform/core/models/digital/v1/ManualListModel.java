package com.eaton.platform.core.models.digital.v1;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.DynamicMediaUrlService;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.DynamicMediaUtils;

import java.util.List;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class ManualListModel implements ContentService{
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ManualListModel.class);
	private static final int MAX_NUMBER_OF_BUTTONS = 2;

	/** The link image. */
	@Inject @Named("manualLinkImage")
	private String linkImage;
	
	/** The link image. */
	@Inject @Named("buyNowCTAlabel")
	private String buyNowCTAlabel;
	
	@Inject @Named("buyNowCTAlink")
	private String buyNowCTAlink;
	
	/** The link image. */
	@Inject @Named("buyNowNewWindow")
	private String buyNowNewWindow;
	
	/** The link image. */
	@Inject @Named("buyNowenableSourceTracking")
	private String buyNowenableSourceTracking;
	
	/** The link image. */
	@Inject @Named("buyNowApplyNoFollowTag")
	private String buyNowApplyNoFollowTag;
	
	/** The link image. */
	@Inject @Named("buyNowCTAcolor")
	private String buyNowCTAcolor;
	
	/** The link image. */
	@Inject @Named("learnMoreCTALabel")
	private String learnMoreCTALabel;
	
	/** The link image. */
	@Inject @Named("learnMoreCTALink")
	private String learnMoreCTALink;
	
	/** The link image. */
	@Inject @Named("learnMoreNewWindow")
	private String learnMoreNewWindow;
	
	/** The link image. */
	@Inject @Named("learnMoreEnableSourceTracking")
	private String learnMoreEnableSourceTracking;
	
	/** The link image. */
	@Inject @Named("learnMoreApplyNoFollowTag")
	private String learnMoreApplyNoFollowTag;
	
	/** The link image. */
	@Inject @Named("learnMoreCTAColor")
	private String learnMoreCTAColor;
	
	/** The link image alt text. */
	@Inject @Named("manualLinkImageAltText")
	private String linkImageAltText;
	
	/** The link eyebrow. */
	@Inject @Named("manualLinkEyebrow")
	private String linkEyebrow;

	/** The link path. */
	@Inject @Named("manualLinkPath")
	private String linkPath;

	/** The new window. */
	@Inject @Named("manualLinkOpenNewWindow")
	private String newWindow;

	/** The new window. */
	@Inject @Named("description")
	private String description;
	
	/** The mobile transformed url. */
	@Inject
	private String mobileTransformedUrl;
	
	/** The desktop transformed url. */
	@Inject
	private String desktopTransformedUrl;
	
	/** The desktopDynam. */
	@Inject
	private String desktopDynam;

	/** The mobileDynam. */
	@Inject
	private String mobileDynam;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

	public List<ContentTileButtonMultiField> getCtaButtons() {
		return ctaButtons;
	}

	public void setCtaButtons(List<ContentTileButtonMultiField> ctaButtons) {
		this.ctaButtons = ctaButtons;
	}

	@Inject @Named("ctaButtons")
    private List<ContentTileButtonMultiField> ctaButtons;

    
    /** The dynamic media url service. */
	@Inject
	private DynamicMediaUrlService dynamicMediaUrlService;
	
    @PostConstruct
	protected void init() {
    	try {
			learnMoreCTALink = CommonUtil.dotHtmlLink(learnMoreCTALink,resourceResolver);
			buyNowCTAlink = CommonUtil.dotHtmlLink(buyNowCTAlink,resourceResolver);
			
			if (null != ctaButtons && ctaButtons.size()== MAX_NUMBER_OF_BUTTONS){
				// this mean there are 2 buttons
				ctaButtons.get(0).setCssClass("b-button__tertiary--light");
			}
        }
    	catch(NullPointerException e){
    		LOG.error("Error in ContentTileSliderModel",e.getMessage());
    	}
    }
    
	public String getLinkImage() {
		return linkImage;
	}

	public String getBuyNowCTAlabel() {
		return buyNowCTAlabel;
	}
	
	public String getBuyNowCTAlink() {
		return buyNowCTAlink;
	}
	
	public String getBuyNowNewWindow() {
		return buyNowNewWindow;
	}
	
	public String getBuyNowenableSourceTracking() {
		return buyNowenableSourceTracking;
	}
	
	public String getBuyNowApplyNoFollowTag() {
		if (buyNowApplyNoFollowTag!=null && buyNowApplyNoFollowTag.equalsIgnoreCase("true")) {
			return "nofollow";
		} else {
			return "follow";
		}
	}
	
	public String getBuyNowCTAcolor() {
		return buyNowCTAcolor;
	}
	
	public String getLearnMoreCTALabel() {
		return learnMoreCTALabel;
	}
	
	public String getLearnMoreCTALink() {
		return learnMoreCTALink;
	}
	
	public String getLearnMoreNewWindow() {
		return learnMoreNewWindow;
	}
	
	public String getLearnMoreEnableSourceTracking() {
		return learnMoreEnableSourceTracking;
	}
	
	public String getLearnMoreApplyNoFollowTag() {
		if (learnMoreApplyNoFollowTag!=null && learnMoreApplyNoFollowTag.equalsIgnoreCase("true")) {
			return "nofollow";
		} else {
			return "follow";
		}
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getAltText() {
		if(null == linkImageAltText) {
			linkImageAltText = CommonUtil.getAssetAltText(resourceResolver, getLinkImage());
		}
		return linkImageAltText;
	}

	public String getLinkEyebrow() {
		return linkEyebrow;
	}


	public String getLinkPath() {
	  return CommonUtil.dotHtmlLink(linkPath, resourceResolver);
	}

	public String getNewWindow() {
		String manualLinkOpenNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, this.newWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
			manualLinkOpenNewWindow = CommonConstants.TARGET_BLANK;
		}
		return manualLinkOpenNewWindow;
	}

	public String getLinkTitle() {
		return StringUtils.EMPTY;
	}

	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if(null != this.linkPath && (StringUtils.startsWith(this.linkPath, CommonConstants.HTTP) || StringUtils.startsWith(this.linkPath, CommonConstants.HTTPS) || StringUtils.startsWith(this.linkPath, CommonConstants.WWW))) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}

    public String getDesktopTransformedUrl() {
           return desktopTransformedUrl;
    }


    public void setDesktopTransformedUrl(String desktopTrans) {
 	   if(null!=linkImage.trim()){
            if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
            	desktopTransformedUrl = linkImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
            } else {
        	desktopTransformedUrl = linkImage.trim();
           }
 	   }
    }

    public String getMobileTransformedUrl() {
          return mobileTransformedUrl;
    }
    

    public void setMobileTransformedUrl(String mobileTrans) {
    	 if(null!=linkImage.trim()){
	    	  if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
	            mobileTransformedUrl = linkImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	        } else {
	        	 mobileTransformedUrl = linkImage.trim();
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
		if(null != desktopDynam && null != resourceResolver.getResource(linkImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
			Node imageMetadataNode = resourceResolver.getResource(linkImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(Node.class);
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
		if(null != mobileDynam && null != resourceResolver.getResource(linkImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
			Node imageMetadataNode = resourceResolver.getResource(linkImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(Node.class);
			String dynamicMediaPublishUrl = DynamicMediaUtils.getDynamicMediaPublishURL(imageMetadataNode, dynamicMediaUrlService.getDynamicMediaUrlBase());
			if(StringUtils.isNotBlank(dynamicMediaPublishUrl)) {
				mobileDynamicRenditionUrl = dynamicMediaPublishUrl.concat(mobileDynam);
			}
		}
		return mobileDynamicRenditionUrl;
	}
}
