package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;

import com.eaton.platform.core.services.DynamicMediaUrlService;
import com.eaton.platform.core.util.DynamicMediaUtils;
import com.eaton.platform.core.util.XFUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.wcm.api.Page;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.experiencefragment.ExperienceFragmentLinkTransformerModel;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.models.annotations.Via;


/**
 * <html> Description: It is a Sling Model class which automatically maps from Sling objects
 * i.e. dialog fields are injected </html> 
 * @author TCS
 * @version 1.0
 * @since 2017
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FeatureBlockModel extends ExperienceFragmentLinkTransformerModel {

	@Inject
	private Page page;

	/** The color. */
	@Inject
	@Via("resource")
	@Named("featureBlockColor")
	private String color;
	
	/** The header. */
	@Inject
	@Via("resource")
	@Named("featureHeader")
	private String header;
	
	/** The description. */
	@Inject
	@Via("resource")
	@Named("featureDesc")
	private String description;
	
	/** The feature block link. */
	@Inject
	@Via("resource")
	Resource featureBlockLink;
	
	/** The image path. */
	@Inject
	@Via("resource")
	@Named("featureImage")
	private String imagePath;
	
	/** The alttxt. */
	@Inject
	@Via("resource")
	@Named("featureImageAltText")
	private String alttxt;
	
	/** The alignment desktop. */
	@Inject
	@Via("resource")
	@Named("featureAlignmentDesktop")
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject
	@Via("resource")
	@Named("featureAlignmentMobile")
	private String alignmentMobile;
	
	/** The image alignment. */
	@Inject
	@Via("resource")
	@Named("featureImageAlignment")
	private String imageAlignment;

	/** The link. */
	@Inject
	@Via("resource")
	@Named("featureCTALink")
	private String link;
	
	/** The link title. */
	@Inject
	@Via("resource")
	@Named("featureCTATitle")
	private String linkTitle;

	/** The new window. */
	@Inject
	@Via("resource")
	@Named("featureCTAOpenNewWindow")
	private String newWindow;

	/** The modal choice. */
	@Inject
	@Via("resource")
	@Default(values = "false")
	private String modal;
	
	/** The desktop trans. */
	@Inject
	@Via("resource")
	private String desktopTrans;
	
	/** The mobile trans. */
	@Inject
	@Via("resource")
	private String mobileTrans;

	/** The desktop dynam. */
	@Inject
	@Via("resource")
	private String desktopDynam;

	/** The mobile dynam. */
	@Inject
	@Via("resource")
	private String mobileDynam;

	/** The dynamic media url service. */
	@Inject
	@Via("resource")
	private DynamicMediaUrlService dynamicMediaUrlService;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

	/** The links. */
	private List<FeatureBlockLinks> featureBlockLists;


	/** The Source tracking javascript. */
	@Inject
	@Via("resource")
	@Default(values = "false")
	private String enableSourceTracking;

	private String localeSpecificLink = null;

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		
		featureBlockLists = new ArrayList<>();
		populateModel(featureBlockLink);

	}

	/**
	 * Populate model.
	 *
	 * @param resource the resource
	 * @return List
	 */

	public List<FeatureBlockLinks> populateModel(Resource resource) {
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();
			List<FeatureBlockLinks> featureListsModel = new ArrayList<>();
			while (linkResources.hasNext()) {
				FeatureBlockLinks featureListFields = linkResources.next().adaptTo(FeatureBlockLinks.class);
				featureListFields.setCurrentpage(page);
				featureListsModel.add(featureListFields);

			}
			featureBlockLists = featureListsModel;

		}
		return featureBlockLists;
	}

	/**
	 * if author has not entered any value in alt txt field in dialog box then
	 * return the image node name from DAM.
	 *
	 * @return the alttxt
	 */
	public String getAlttxt() {
		String alternateTxt = alttxt;
		if(null == alternateTxt) {
			alternateTxt = CommonUtil.getAssetAltText(resourceResolver, getImagePath());
		}
		return alternateTxt;
	}

	/**
	 * Gets the link.
	 *
	 * @return the link
	 */
	public String getLink() {
		if (Boolean.TRUE.equals(this.getIsComponentInXS())) {
			localeSpecificLink = XFUtil.updateHTMLLinkInXF(page, resourceResolver,
					link);
		}
		return localeSpecificLink != null ? CommonUtil.dotHtmlLink(localeSpecificLink)
				: CommonUtil.dotHtmlLink(link);
	}

	/**
	 * Gets the flat format link.
	 *
	 * @return the link in flat format
	 */
	public String getFlatLink() {
		link = CommonUtil.dotHtmlLink(link);
		return link;
	}

	/**
	 * if author has not entered the link title then
	 * return navigation title if it is not authored, then return the page title.
	 *
	 * @return the link title
	 */
	public String getLinkTitle() {
		return CommonUtil.getLinkTitle(linkTitle, link, resourceResolver);
	}
	
	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	public String getNewWindow() {
		String openNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, newWindow)) {
			openNewWindow = CommonConstants.TARGET_BLANK;
		}
		return openNewWindow;
	}

	/**
	 * Gets the modal.
	 *
	 * @return modal
	 */
	public String getModal(){
		return modal;
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public String getHeader() {
		return header;
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
	 * Gets the feature block link.
	 *
	 * @return the feature block link
	 */
	public Resource getFeatureBlockLink() {
		return featureBlockLink;
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
	 * Gets the image alignment.
	 *
	 * @return the image alignment
	 */
	public String getImageAlignment() {
		return imageAlignment;
	}

	/**
	 * Gets the desktop trans.
	 *
	 * @return the desktop trans
	 */
	public String getDesktopTrans() {
		if(null == desktopTrans) {
			desktopTrans = StringUtils.EMPTY;
		}
		return desktopTrans;
	}

	/**
	 * Gets the mobile trans.
	 *
	 * @return the mobile trans
	 */
	public String getMobileTrans() {
		if(null == mobileTrans) {
			mobileTrans = StringUtils.EMPTY;
		}
		return mobileTrans;
	}

	/**
	 * Gets the resource resolver.
	 *
	 * @return the resource resolver
	 */
	public ResourceResolver getResourceResolver() {
		return resourceResolver;
	}

	/**
     * Gets the desktop transformed url.
     *
     * @return the desktop transformed url
     */
     public String getDesktopTransformedUrl() {
            String desktopTransformedUrl = imagePath.trim();
            if(null != desktopTrans && !StringUtils.contains(getDesktopTrans(), CommonConstants.DEFALUT_DESKTOP)){
            	desktopTransformedUrl = imagePath.trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
            } else if(null != desktopTrans && StringUtils.contains(getDesktopTrans(), CommonConstants.DEFALUT_DESKTOP)){
            	desktopTransformedUrl = imagePath.trim();
            }
            return desktopTransformedUrl;
     }
 
     /**
     * Gets the mobile transformed url.
     *
     * @return the mobile transformed url
     */
     public String getMobileTransformedUrl() {
            String mobileTransformedUrl = imagePath.trim();
            if(null != mobileTrans && !StringUtils.contains(getMobileTrans(), CommonConstants.DEFAULT_MOBILE)){
                mobileTransformedUrl = imagePath.trim().concat(CommonConstants.TRANSTORM).trim().concat(getMobileTrans().trim()).concat(CommonConstants.IMAGE_JPG);
            } else if(null != mobileTrans && StringUtils.contains(getMobileTrans(), CommonConstants.DEFAULT_MOBILE)){
            	mobileTransformedUrl = imagePath.trim();
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

	public String getEnableSourceTracking() {
		return enableSourceTracking;
	}

	/**
	 * Gets the feature block lists.
	 *
	 * @return the feature block lists
	 */
	public List<FeatureBlockLinks> getFeatureBlockLists() {
		return featureBlockLists;
	}
}
