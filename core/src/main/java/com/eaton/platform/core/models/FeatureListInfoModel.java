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
 * <html> Description: This is a Sling Model for Feature List Component.</html>
 * .
 * 
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FeatureListInfoModel {

	/** The feature list image. */
	@Inject
	private String featureListImage;

	/** The feature list image alt text. */
	@Inject
	private String featureListImageAltText;

	/** The feature list description. */
	@Inject
	private String featureListDescription;

	/** The feature list link title. */
	@Inject
	private String featureListLinkTitle;

	/** The feature list link path. */
	@Inject
	private String featureListLinkPath;

	/** The feature list link open new window. */
	@Inject
	private String featureListLinkOpenNewWindow;

	/** The mobile transformed url. */

	private String mobileTransformedUrl;

	/** The desktop transformed url. */

	private String desktopTransformedUrl;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	/**
	 * Gets the feature list image alt text.
	 * 
	 * @return the feature list image alt text
	 */
	public String getAltText() {
		String imageAltText = featureListImageAltText;
		if (null == imageAltText) {
			imageAltText = CommonUtil.getAssetAltText(resourceResolver,	getFeatureListImage());
		}
		return imageAltText;
	}

	/**
	 * Gets the feature list image.
	 * 
	 * @return the feature list image
	 */
	public String getFeatureListImage() {
		String imagePath = featureListImage;
		if (null == imagePath) {
			imagePath = getPageProperty(CommonConstants.TEASER_IMAGE_PATH);
			if(StringUtils.isBlank(imagePath)) {
				imagePath = StringUtils.EMPTY;
			}
		}
		return imagePath;
	}

	/**
	 * Gets the feature list description.
	 * 
	 * @return the feature list description
	 */
	public String getFeatureListDescription() {
		return featureListDescription;
	}

	/**
	 * Gets the feature list link title.
	 * 
	 * @return the feature list link title
	 */
	public String getFeatureListLinkTitle() {
		return CommonUtil.getLinkTitle(featureListLinkTitle,
				featureListLinkPath, resourceResolver);
	}

	/**
	 * Gets the feature list link path.
	 * 
	 * @return the feature list link path
	 */
	public String getFeatureListLinkPath() {
		return CommonUtil.dotHtmlLink(featureListLinkPath);
	}

	/**
	 * Gets the feature list link open new window.
	 * 
	 * @return the feature list link open new window
	 */
	public String getFeatureListLinkOpenNewWindow() {
		return featureListLinkOpenNewWindow;
	}
	
	/**
	 * Gets the page property.
	 *
	 * @param propertyName the property name
	 * @return the page property
	 */
	private String getPageProperty(String propertyName) {
		String propertyValue = StringUtils.EMPTY;
		Resource linkPathResource = resourceResolver.getResource(featureListLinkPath);
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
	 * @param desktopTrans
	 *            the new desktop transformed url
	 */
	public void setDesktopTransformedUrl(String desktopTrans) {
		if(null!=getFeatureListImage()){
		 if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
			desktopTransformedUrl = getFeatureListImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
			} else {
			desktopTransformedUrl = getFeatureListImage().trim();
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
	 * @param mobileTrans
	 *            the new mobile transformed url
	 */
	public void setMobileTransformedUrl(String mobileTrans) {
		if(null!=getFeatureListImage()){
		  if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
			mobileTransformedUrl = getFeatureListImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			mobileTransformedUrl = getFeatureListImage().trim();
		}
	}
}

}
