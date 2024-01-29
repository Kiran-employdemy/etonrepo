package com.eaton.platform.core.bean;

import java.util.List;

public class ProductCardBean {


	private String productFamilyName;
	private String marketingDesc;
	private String primaryCTALabel;
	private String primaryCTAURL;
	private String primaryCTANewWindow;
	private String primaryImage;
	private String altPrimaryImage;
	private String isSuffixDisabled;
	private String primaryCTAEnableSourceTracking;
	private String externalMappedCurrentPagePath;
	private List<SecondaryLinksBean> secondaryLinksList;
	private String status;
	private List<String> primaryAndSecondaryImagesList;
	private String spinImages;
	private String primaryOrSpinImage;
	private Boolean primaryImageIsRepresentative;
	private boolean newBadgeVisible;
	
	public String getSpinImages() {
		return spinImages;
	}
	public void setSpinImages(String spinImages) {
		this.spinImages = spinImages;
	}
	public String getPrimaryOrSpinImage() {
		return primaryOrSpinImage;
	}
	public void setPrimaryOrSpinImage(String primaryOrSpinImage) {
		this.primaryOrSpinImage = primaryOrSpinImage;
	}
	public String getProductFamilyName() {
		return productFamilyName;
	}
	public void setProductFamilyName(String productFamilyName) {
		this.productFamilyName = productFamilyName;
	}
	public String getMarketingDesc() {
		return marketingDesc;
	}
	public void setMarketingDesc(String marketingDesc) {
		this.marketingDesc = marketingDesc;
	}
	public String getPrimaryCTALabel() {
		return primaryCTALabel;
	}
	public void setPrimaryCTALabel(String primaryCTALabel) {
		this.primaryCTALabel = primaryCTALabel;
	}
	public String getPrimaryCTAURL() {
		return primaryCTAURL;
	}
	public void setPrimaryCTAURL(String primaryCTAURL) {
		this.primaryCTAURL = primaryCTAURL;
	}
	public String getPrimaryCTANewWindow() {
		return primaryCTANewWindow;
	}
	public void setPrimaryCTANewWindow(String primaryCTANewWindow) {
		this.primaryCTANewWindow = primaryCTANewWindow;
	}
	public String getPrimaryImage() {
		return primaryImage;
	}
	public void setPrimaryImage(String primaryImage) {
		this.primaryImage = primaryImage;
	}
	public String getAltPrimaryImage() {
		return altPrimaryImage;
	}
	public void setAltPrimaryImage(String altPrimaryImage) {
		this.altPrimaryImage = altPrimaryImage;
	}
	public List<SecondaryLinksBean> getSecondaryLinksList() {
		return secondaryLinksList;
	}
	public void setSecondaryLinksList(List<SecondaryLinksBean> secondaryLinksList) {
		this.secondaryLinksList = secondaryLinksList;
	}
	
	
	public List<String> getPrimaryAndSecondaryImagesList() {
		return primaryAndSecondaryImagesList;
	}
	public void setPrimaryAndSecondaryImagesList(List<String> primaryAndSecondaryImagesList) {
		this.primaryAndSecondaryImagesList = primaryAndSecondaryImagesList;
	}
	
	public String getIsSuffixDisabled() {
		return this.isSuffixDisabled;
	}

	public void setIsSuffixDisabled(String isSuffixDisabled) {
		this.isSuffixDisabled = isSuffixDisabled;
	}

	public String getPrimaryCTAEnableSourceTracking() {
		return primaryCTAEnableSourceTracking;
	}

	public void setPrimaryCTAEnableSourceTracking(String primaryCTAEnableSourceTracking) {
		this.primaryCTAEnableSourceTracking = primaryCTAEnableSourceTracking;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getExternalMappedCurrentPagePath() { return externalMappedCurrentPagePath; }

	public void setExternalMappedCurrentPagePath(String externalMappedCurrentPagePath) { this.externalMappedCurrentPagePath = externalMappedCurrentPagePath; }

	public Boolean getPrimaryImageIsRepresentative() {
		return primaryImageIsRepresentative;
	}

	public void setPrimaryImageIsRepresentative(Boolean primaryImageIsRepresentative) {
		this.primaryImageIsRepresentative = primaryImageIsRepresentative;
	}
	
	public boolean getnewBadgeVisible() {
		return newBadgeVisible;
	}

	public void setnewBadgeVisible(boolean newBadgeVisible) {
		this.newBadgeVisible = newBadgeVisible;
	}
}