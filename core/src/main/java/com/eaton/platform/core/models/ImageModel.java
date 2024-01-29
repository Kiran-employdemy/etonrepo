package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageModel {
	
	/** Variable The applyNoFollowTag. */
	@Inject @Default(values = "false")
	private String applyNoFollowTag;
	
	/** Variable The alt. */
	@Inject
	private String alt;

	/** Variable The isDecorative. */
	@Inject
	private String isDecorative;
	
	/** Variable The fileReference. */
	@Inject
	private String fileReference;
	
	/** Variable The linkURL. */
	@Inject
	private String linkURL;
	
	/** Variable The resourceResolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    /** The desktop trans. */
    @Inject
    private String desktopTrans;
    
	/** The alignment desktop. */
	@Inject
	private String alignmentDesktop;
	
	/** The alignment mobile. */
	@Inject
	private String alignmentMobile;
    
    
	/** The mobile trans. */
    @Inject
    private String mobileTrans;

	@Inject
	private String fileName;

    @Inject
	private Resource resource;

	/**
	 * @return the desktopTrans
	 */
	public String getDesktopTrans() {
		return desktopTrans;
	}

	/**
	 * @return the mobileTrans
	 */
	public String getMobileTrans() {
		return mobileTrans;
	}

	/**
	 * @return isDecorative
	 */
	public String getIsDecorative() {
		return isDecorative;
	}
	/**
	 * if author has not entered any value in alt txt field in dialog box then
	 * return the image node name from DAM.
	 *
	 * @return transAlttxt
	 */
	public String getAltText() {
		String altText = alt;
		if (StringUtils.isNotBlank(isDecorative)) {
			if (isDecorative.equals("false")) {
				altText = CommonUtil.getAssetAltText(resourceResolver, fileReference);
			} else if (isDecorative.equals("true")) {
				altText = StringUtils.EMPTY;
			}
		}
		return altText;
	}

	/**
	 * Gets the file reference if the Image Configured from DAM ,
	 * Or will return the resource-path of the configured Image if the Image is Uploaded using browse button.
	 *
	 * @return image path
	 */
	public String getFileReference() {
		String fileReferencePath = fileReference;
		if(StringUtils.isEmpty(fileReferencePath) && StringUtils.isNotEmpty(fileName)){
			fileReferencePath = resource.getPath();
		}
		return fileReferencePath;
	}

	/**
	 * Gets the link URL.
	 *
	 * @return linkURL
	 */
	public String getLinkURL() {
		return CommonUtil.dotHtmlLink(linkURL);
	}

	/**
	 * Gets the alignment desktop
	 * @return alignmentDesktop
	 */
	public String getAlignmentDesktop() {
		return alignmentDesktop;
	}

	/**
	 * Gets the alignment Mobile
	 * @return alignmentMobile;
	 */
	public String getAlignmentMobile() {
		return alignmentMobile;
	}

	/**
	 * Gets the desktop transformed url.
	 *
	 * @return the desktop transformed url
	 */
	public String getDesktopTransformedUrl() {
		String desktopTransformedUrl = null;
		if(null != getFileReference()) {
			if (getDesktopTrans() != null && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)) {
				desktopTransformedUrl = getFileReference().trim().concat(CommonConstants.TRANSTORM).trim().concat(getDesktopTrans().trim()).concat(CommonConstants.IMAGE_JPG);
			} else {
				desktopTransformedUrl = getFileReference().trim();
			}
		}
		return desktopTransformedUrl;
	}

	/**
	 * Gets the mobile transformed url.
	 *
	 * @return the mobile transformed url
	 */
	public String getMobileTransformedUrl() {
		String mobileTransformedUrl = null;
		if(null != getFileReference()) {
			if (getMobileTrans() != null && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)) {
				mobileTransformedUrl = getFileReference().trim().concat(CommonConstants.TRANSTORM).trim().concat(getMobileTrans().trim()).concat(CommonConstants.IMAGE_JPG);
			} else {
				mobileTransformedUrl = getFileReference().trim();
			}
		}
		return mobileTransformedUrl;
	}
    
	public String getApplyNoFollowTag() {
        return applyNoFollowTag;
	}
}
