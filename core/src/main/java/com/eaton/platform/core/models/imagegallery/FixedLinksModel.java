package com.eaton.platform.core.models.imagegallery;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in ImgGalleryViewContentList class
 *  to load Fixed links multifield</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FixedLinksModel {

	/** The page link. */
	@Inject @Named("imagePath")
	private String imgPath;
	
	/** The img path thumbnail. */
	private String imgPathThumbnail;
	
	/** The img path preview. */
	private String imgPathPreview;
	
	/** The img path zoom. */
	private String imgPathZoom;
	
	/** The img path download. */
	private String imgPathDownload;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
	/**
	 * Gets the img path.
	 *
	 * @return the img path
	 */
	public String getImgPath() {
		return imgPath;
	}

	/**
	 * Gets the alt text.
	 *
	 * @return the alt text
	 */
	public String getAltText() {
		return CommonUtil.getAssetAltText(resourceResolver, imgPath);
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		String descriptionVal = StringUtils.EMPTY;
		if ((StringUtils.startsWith(imgPath, CommonConstants.CONTENT_DAM)) && (null != getAsset())) {
			descriptionVal = getAsset().getMetadataValue(DamConstants.DC_DESCRIPTION);
		} 
		return descriptionVal;
	}
	
	/**
	 * Gets the asset.
	 *
	 * @return the asset
	 */
	private Asset getAsset() {
		return CommonUtil.getAsset(getAssetResource());
	}

	/**
	 * Gets the asset resource.
	 *
	 * @return the asset resource
	 */
	private Resource getAssetResource() {
		return resourceResolver.getResource(imgPath);
	}

	/**
	 * Gets the img path thumbnail.
	 *
	 * @return the img path thumbnail
	 */
	public String getImgPathThumbnail() {
		return imgPathThumbnail;
	}

	/**
	 * Sets the img path thumbnail.
	 *
	 * @param imgPathThumbnail the new img path thumbnail
	 */
	public void setImgPathThumbnail(String thumbnailTrans) {
		if(StringUtils.isNotBlank(thumbnailTrans) && !StringUtils.contains(thumbnailTrans, CommonConstants.DEFALUT_DESKTOP)){
			 imgPathThumbnail = getImgPathDownload().trim().concat(CommonConstants.TRANSTORM).trim().concat(thumbnailTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	     } else {
	         imgPathThumbnail = getImgPathDownload().trim();
	     }
	}

	/**
	 * Gets the img path preview.
	 *
	 * @return the img path preview
	 */
	public String getImgPathPreview() {
		return imgPathPreview;
	}

	/**
	 * Sets the img path preview.
	 *
	 * @param imgPathPreview the new img path preview
	 */
	public void setImgPathPreview(String alignmentDesktop) {
		if(StringUtils.isNotBlank(alignmentDesktop) && !StringUtils.contains(alignmentDesktop, CommonConstants.DEFALUT_DESKTOP)){
	  	  	imgPathPreview = getImgPathDownload().trim().concat(CommonConstants.TRANSTORM).trim().concat(alignmentDesktop.trim()).concat(CommonConstants.IMAGE_JPG);
         } else {
        	imgPathPreview = getImgPathDownload().trim();
         }
	}

	/**
	 * Gets the img path zoom.
	 *
	 * @return the img path zoom
	 */
	public String getImgPathZoom() {
		return imgPathZoom;
	}

	/**
	 * Sets the img path zoom.
	 *
	 * @param imgPathZoom the new img path zoom
	 */
	public void setImgPathZoom(String alignmentDesktop) {
		if(StringUtils.isNotBlank(alignmentDesktop) && !StringUtils.contains(alignmentDesktop, CommonConstants.DEFALUT_DESKTOP)){
			imgPathZoom = getImgPathDownload().trim().concat(CommonConstants.TRANSTORM).trim().concat(alignmentDesktop.trim()).concat(CommonConstants.IMAGE_JPG);
         } else {
        	imgPathZoom = getImgPathDownload().trim();
         }
	}

	/**
	 * Gets the img path download.
	 *
	 * @return the img path download
	 */
	public String getImgPathDownload() {
		return imgPathDownload;
	}

	/**
	 * Sets the img path download.
	 *
	 * @param imgPathDownload the new img path download
	 */
	public void setImgPathDownload(String imgPathDownload) {
		this.imgPathDownload = imgPathDownload;
	}

}
