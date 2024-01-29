package com.eaton.platform.core.models.digital.v1.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.digital.v1.CTALink;
import com.eaton.platform.core.models.digital.v1.Hero;
import com.eaton.platform.core.services.DynamicMediaUrlService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.DynamicMediaUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.List;

/**
 * <html> Description: This class is used to inject the dialog
 * properties.</html> .
 * 
 */
@Model(adaptables = Resource.class, adapters = Hero.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = "eaton/components/digital/hero-with-cta/v1/hero-with-cta")
public class HeroCTA implements Hero {

	/** The hero with CTA header. */
	@Inject
	private String heroHeader;

	/** The title type. */
	@Inject
	private String heroCTAHeaderType;

	/** The hero with CTA desc. */
	@Inject
	private String heroDesc;

	/** The hero with CTA image. */
	@Inject
	private String heroImage;

	/** The hero with CTA image alt text. */
	@Inject
	private String heroImageAltText;

	/** The CTA links from the multifield. */
	@Inject
	private List<CTALink> ctaLinks;

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

	/** The desktopDynam. */
	@Inject
	private String desktopDynam;

	/** The mobileDynam. */
	@Inject
	private String mobileDynam;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	/** The dynamic media url service. */
	@Inject
	private DynamicMediaUrlService dynamicMediaUrlService;

	@Self
	protected Resource resource;

	/**
	 * Gets the hero with CTA header.
	 *
	 * @return the hero with CTA header
	 */
	public String getHeroHeader() {
		if (StringUtils.isNotBlank(heroHeader)) {
			return heroHeader;
		}
		PageManager pageManager;
		if (null != resource && null != resourceResolver
				&& null != (pageManager = resourceResolver.adaptTo(PageManager.class))) {
			Page currentPage = pageManager.getContainingPage(resource);
			return StringUtils.isNotBlank(currentPage.getNavigationTitle()) ? currentPage.getNavigationTitle()
					: currentPage.getTitle();
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Gets the hero with CTA desc.
	 *
	 * @return the hero with CTA desc
	 */
	public String getHeroDesc() {
		return heroDesc;
	}

	public String getHeroCTAHeaderType() {
		return heroCTAHeaderType;
	}

	public List<CTALink> getCtaLinks() {
		return ctaLinks;
	}

	/**
	 * Gets the hero with CTA image.
	 *
	 * @return the hero with CTA image
	 */
	public String getHeroImage() {
		return heroImage;
	}

	/**
	 * Gets the hero with CTA image alt text.
	 *
	 * @return the hero with CTA image alt text
	 */
	public String getHeroImageAltText() {
		String alttxt = this.heroImageAltText;
		if (null == alttxt) {
			alttxt = CommonUtil.getAssetAltText(resourceResolver, getHeroImage());
		}
		return alttxt;
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
		if (null != getDesktopTrans() && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)) {
			desktopTransformedUrl = getHeroImage().trim()
					.concat(CommonConstants.TRANSTORM).trim()
					.concat(getDesktopTrans().trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			desktopTransformedUrl = getHeroImage().trim();
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
		if (null != getDesktopTrans() && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)) {
			mobileTransformedUrl = getHeroImage().trim()
					.concat(CommonConstants.TRANSTORM).trim()
					.concat(getMobileTrans().trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			mobileTransformedUrl = getHeroImage().trim();
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
		if (null != desktopDynam && null != resourceResolver.getResource(
				heroImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
			Node imageMetadataNode = resourceResolver.getResource(
					heroImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))
					.adaptTo(Node.class);
			String dynamicMediaPublishUrl = DynamicMediaUtils.getDynamicMediaPublishURL(imageMetadataNode,
					dynamicMediaUrlService.getDynamicMediaUrlBase());
			if (StringUtils.isNotBlank(dynamicMediaPublishUrl)) {
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
		if (null != mobileDynam && null != resourceResolver.getResource(
				heroImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
			Node imageMetadataNode = resourceResolver.getResource(
					heroImage.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))
					.adaptTo(Node.class);
			String dynamicMediaPublishUrl = DynamicMediaUtils.getDynamicMediaPublishURL(imageMetadataNode,
					dynamicMediaUrlService.getDynamicMediaUrlBase());
			if (StringUtils.isNotBlank(dynamicMediaPublishUrl)) {
				mobileDynamicRenditionUrl = dynamicMediaPublishUrl.concat(mobileDynam);
			}
		}
		return mobileDynamicRenditionUrl;
	}

}
