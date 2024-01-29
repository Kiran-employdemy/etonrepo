package com.eaton.platform.core.models.digital.v1;

import java.util.List;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.api.resource.ResourceResolver;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.sling.models.annotations.Source;
import com.eaton.platform.core.services.DynamicMediaUrlService;
import com.eaton.platform.core.util.DynamicMediaUtils;
import javax.jcr.Node;
import org.apache.commons.lang3.StringUtils;
import javax.annotation.PostConstruct;


/**
 * <html> Description: This class is used to inject the dialog properties. </html> .
 *

 */
@Model(adaptables = Resource.class,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
		resourceType = "eaton/components/digital/core/content-tile-with-cta/v1/content-tile-with-cta")
public class ContentTileWithCTAModel {
	
	/** Image for content tile */
	@Inject
	private String image;
	
	/** The links. */
	@Inject
	private List<ContentTileMultifieldModel> links;

	/** Description for content tile. */
	@Inject
	private String description;

	/** Title for content tile. */
	@Inject
	private String title;

	/** Constant for Max Number of buttons index. */
	private final static int MAX_NUMBER_OF_BUTTONS = 2;
	
	/** The alignment desktop. */
	@Inject
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject
	private String alignmentMobile;

	/** The alignment mobile. */
	@Inject
	private String singleCtaAlign;

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
	


	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public String getImage() {
		return image;
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
	 * Gets the single CTA alignment
	 *
	 * @return the single CTA alignment
	 */
	public String getSingleCtaAlign() {
		return singleCtaAlign;
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
			desktopTransformedUrl = getImage().trim()
					.concat(CommonConstants.TRANSTORM).trim()
					.concat(getDesktopTrans().trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			desktopTransformedUrl = getImage().trim();
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
			mobileTransformedUrl = getImage().trim()
					.concat(CommonConstants.TRANSTORM).trim()
					.concat(getMobileTrans().trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			mobileTransformedUrl = getImage().trim();
		}
		return mobileTransformedUrl;
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
	 * Gets the link image.
	 *
	 * @return the link image
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<ContentTileMultifieldModel> getLinks() {
		return links;
	}
	
	@PostConstruct
	void init(){
	  if (null != links && links.size()== MAX_NUMBER_OF_BUTTONS){
	      // this mean there are 2 buttons
	      links.get(0).setCssClass("b-button__tertiary b-button__tertiary--light");
		  links.get(1).setCssClass("b-button__primary b-button__primary--light");
	   } else if (null != links && links.size()< MAX_NUMBER_OF_BUTTONS){
		// this mean there is 1 buttons
		links.get(0).setCssClass("b-button__tertiary b-button__tertiary--light");
	 }
	}
	

	
	/**
	 * Gets the desktop dynamic rendition url.
	 *
	 * @return the desktop dynamic rendition url
	 */
	public String getDesktopDynamicRenditionUrl() {
		String desktopDynamicRenditionUrl = null;
		if(null != desktopDynam && null != resourceResolver.getResource(image.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
			Node imageMetadataNode = resourceResolver.getResource(image.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(Node.class);
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
		if(null != mobileDynam && null != resourceResolver.getResource(image.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA))) {
			Node imageMetadataNode = resourceResolver.getResource(image.trim().concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_METADATA)).adaptTo(Node.class);
			String dynamicMediaPublishUrl = DynamicMediaUtils.getDynamicMediaPublishURL(imageMetadataNode, dynamicMediaUrlService.getDynamicMediaUrlBase());
			if(StringUtils.isNotBlank(dynamicMediaPublishUrl)) {
				mobileDynamicRenditionUrl = dynamicMediaPublishUrl.concat(mobileDynam);
			}
		}
		return mobileDynamicRenditionUrl;
	}
}