package com.eaton.platform.core.models;

import javax.inject.Inject;
import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.DynamicMediaUrlService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.DynamicMediaUtils;

/**
 * <html> Description: This class is used to inject the dialog
 * properties.</html> .
 * 
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LandingHeroModel {

	/** The landing hero header. */
	@Inject
	private String landingHeroHeader;

	/** The landing hero desc. */
	@Inject
	private String landingHeroDesc;

	/** The landing hero image. */
	@Inject
	private String landingHeroImage;

	/** The landing hero image alt text. */
	@Inject
	private String landingHeroImageAltText;

	/** The landing hero link path. */
	@Inject
	private String landingHeroLinkPath;

	/** The landing hero link title. */
	@Inject
	private String landingHeroLinkTitle;

	/** The new window. */
	@Inject
	private String newWindow;
	
	/** The alignment desktop. */
	@Inject
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject
	private String alignmentMobile;
	
	/** The desktop trans. */
	@Inject
	private String desktopTrans;

	/** The mobile trans. */
	@Inject
	private String mobileTrans;
	
	/** The desktop dynam. */
	@Inject
	private String desktopDynam;

	/** The mobile dynam. */
	@Inject
	private String mobileDynam;

	/** The dynamic media url service. */
	@Inject
	private DynamicMediaUrlService dynamicMediaUrlService;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	/**
	 * Gets the landing hero header.
	 * 
	 * @return the landing hero header
	 */
	public String getLandingHeroHeader() {
		return landingHeroHeader;
	}

	/**
	 * Gets the landing hero desc.
	 * 
	 * @return the landing hero desc
	 */
	public String getLandingHeroDesc() {
		return landingHeroDesc;
	}

	/**
	 * Gets the landing hero image.
	 * 
	 * @return the landing hero image
	 */
	public String getLandingHeroImage() {
		return landingHeroImage;
	}

	/**
	 * Gets the landing hero image alt text.
	 * 
	 * @return the landing hero image alt text
	 */
	public String getLandingHeroImageAltText() {
		String alttxt = this.landingHeroImageAltText;
		if (null == alttxt) {
			alttxt = CommonUtil.getAssetAltText(resourceResolver, getLandingHeroImage());
		}
		return alttxt;
	}

	/**
	 * Gets the landing hero link path.
	 * 
	 * @return the landing hero link path
	 */
	public String getLandingHeroLinkPath() {
		return CommonUtil.dotHtmlLink(landingHeroLinkPath);
	}

	/**
	 * Gets the landing hero link title.
	 * 
	 * @return the landing hero link title
	 */
	public String getLandingHeroLinkTitle() {
		return CommonUtil.getLinkTitle(landingHeroLinkTitle,
				landingHeroLinkPath, resourceResolver);
	}

	/**
	 * Gets the new window.
	 * 
	 * @return the new window
	 */
	public String getNewWindow() {
		String openNewWindow = StringUtils.EMPTY;
		if (StringUtils.equals(CommonConstants.TRUE, newWindow)	|| getIsExternal()) {
			openNewWindow = CommonConstants.TARGET_BLANK;
		}
		return openNewWindow;
	}

	/**
	 * Gets the checks if is external.
	 * 
	 * @return the checks if is external
	 */
	public boolean getIsExternal() {
		boolean isExternal = false;
		if (null != landingHeroLinkPath && StringUtils.startsWith(landingHeroLinkPath, CommonConstants.HTTP)
					|| StringUtils.startsWith(landingHeroLinkPath,
							CommonConstants.HTTPS)
					|| StringUtils.startsWith(landingHeroLinkPath,
							CommonConstants.WWW)) {
			isExternal = true;
		}
		return isExternal;
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
	 * Gets the desktop transformed url.
	 * 
	 * @return the desktop transformed url
	 */
	public String getDesktopTransformedUrl() {
		String desktopTransformedUrl;
		if (null != getDesktopTrans()&& !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)) {
			desktopTransformedUrl = getLandingHeroImage().trim()
					.concat(CommonConstants.TRANSTORM).trim()
					.concat(getDesktopTrans().trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			desktopTransformedUrl = getLandingHeroImage().trim();
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
		if (null != getDesktopTrans()&& !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)) {
			mobileTransformedUrl = getLandingHeroImage().trim()
					.concat(CommonConstants.TRANSTORM).trim()
					.concat(getMobileTrans().trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			mobileTransformedUrl = getLandingHeroImage().trim();
		}
		return mobileTransformedUrl;
	}
	
	/**
	 * Gets the desktop dynamic rendition url.
	 *
	 * @return the desktop dynamic rendition url
	 */
	public String getDesktopDynamicRenditionUrl() {
		String desktopDynamicRenditionUrl = null;
		if(null != desktopDynam && null != resourceResolver.getResource(landingHeroImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
			Node imageMetadataNode = resourceResolver.getResource(landingHeroImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(Node.class);
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
		if(null != mobileDynam && null != resourceResolver.getResource(landingHeroImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
			Node imageMetadataNode = resourceResolver.getResource(landingHeroImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(Node.class);
			String dynamicMediaPublishUrl = DynamicMediaUtils.getDynamicMediaPublishURL(imageMetadataNode, dynamicMediaUrlService.getDynamicMediaUrlBase());
			if(StringUtils.isNotBlank(dynamicMediaPublishUrl)) {
				mobileDynamicRenditionUrl = dynamicMediaPublishUrl.concat(mobileDynam);
			}
		}
		return mobileDynamicRenditionUrl;
	}

}
