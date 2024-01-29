package com.eaton.platform.core.models.digital.v1;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.eaton.platform.core.constants.CommonConstants;

public class ContentServiceBean implements Serializable, ContentService {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2847358913108419130L;
	
	/** The Marketing Description. */
	private String marketingDescription;

	/** The link title. */
	private String linkTitle;

	/** The link path. */
	private String linkPath; 
	
	/** The link eyebrow. */
	private String linkEyebrow;
	
	/** The link image alt text. */
	private String linkImageAltText;
	
	/** The link image. */
	private String linkImage;
	
	private String buyNowCTAlabel;
	
	private String buyNowCTALink;
	
	/** The new window. */
	private String newWindow;

	/** The desktop transformed url. */
	private String desktopTransformedUrl;
	
	/** The mobile transformed url. */
	private String mobileTransformedUrl;
	
	private String eyebrowTitleLink;

	public List<ContentTileButtonMultiField> getCtaButtons() {
		return ctaButtons;
	}

	public void setCtaButtons(List<ContentTileButtonMultiField> ctaButtons) {
		this.ctaButtons = ctaButtons;
	}

	private List<ContentTileButtonMultiField> ctaButtons = new ArrayList<>();

	public void addToCtaButtons(ContentTileButtonMultiField ctaButton) {
		if(null != ctaButton){
			ctaButtons.add(ctaButton);
		}
	}

	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}
	
	@Override
	public String getLinkTitle() {
		return linkTitle;
	}

	@Override
	public String getLinkPath() {
		return this.linkPath;
	}

	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

	@Override
	public String getNewWindow() {
		return newWindow;
	}

	public void setNewWindow(String newWindow) {
		this.newWindow = newWindow;
	}

	@Override
	public String getLinkEyebrow() {
		return linkEyebrow;
	}

	public void setLinkEyebrow(String linkEyebrow) {
		this.linkEyebrow = linkEyebrow;
	}

	@Override
	public String getAltText() {
		return linkImageAltText;
	}

	public void setLinkImageAltText(String linkImageAltText) {
		this.linkImageAltText = linkImageAltText;
	}

	@Override
	public String getLinkImage() {
		return linkImage;
	}

	public void setLinkImage(String linkImage) {
		this.linkImage = linkImage;
	}

	@Override
     public String getDesktopTransformedUrl() {
        return desktopTransformedUrl;
     }

	@Override
     public void setDesktopTransformedUrl(String desktopTrans) {
        if(null!=linkImage){
        	if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
                desktopTransformedUrl = linkImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
        	} else{
                desktopTransformedUrl = linkImage.trim();
        	}
    	 }
     }
     
	@Override
     public String getMobileTransformedUrl() {
        return mobileTransformedUrl;
     }
     
	@Override
     public void setMobileTransformedUrl(String mobileTrans) {
        if(null!=linkImage){
            if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
                mobileTransformedUrl = linkImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
            } else {
            	mobileTransformedUrl = linkImage.trim();
            }
    	 }
     }
	
	public void setDescription(String marketingDescription) {
		this.marketingDescription = marketingDescription;
	}
	
	public String getDescription() {
		return marketingDescription;
	}

	public void setBuyNowCTAlabel(String buyNowCTAlabel) {
		this.buyNowCTAlabel=buyNowCTAlabel;
		
	}

	public String getBuyNowCTAlabel() {
		return buyNowCTAlabel;
		
	}
	
	public String getBuyNowCTAlink() {
		return buyNowCTALink;
	}
	
	public void setBuyNowCTAlink(String buyNowCTALink) {
		this.buyNowCTALink = buyNowCTALink;
	}
	
	public void setEyebrowTitleLink(String eyebrowTitleLink) {
		this.eyebrowTitleLink = eyebrowTitleLink;
		
	}
	
	public String getEyebrowTitleLink() {
		return eyebrowTitleLink;
	}

}
