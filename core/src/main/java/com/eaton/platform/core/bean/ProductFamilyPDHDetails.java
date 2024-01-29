package com.eaton.platform.core.bean;

import java.io.Serializable;

public class ProductFamilyPDHDetails implements Serializable {
		
	private static final long serialVersionUID = -7859567982762562998L;
	
	private String productName;
	private String marketingDescription;
	private String coreFeatures;
	private String supportInfo;
	private String primaryImgName;
	private String extensionId;
	private String pdhSpinImage;
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getPdhSpinImage() {
		return pdhSpinImage;
	}
	public void setPdhSpinImage(String pdhSpinImage) {
		this.pdhSpinImage = pdhSpinImage;
	}
	public String getMarketingDescription() {
		return marketingDescription;
	}
	public void setMarketingDescription(String marketingDescription) {
		this.marketingDescription = marketingDescription;
	}
	public String getCoreFeatures() {
		return coreFeatures;
	}
	public void setCoreFeatures(String coreFeatures) {
		this.coreFeatures = coreFeatures;
	}
	public String getSupportInfo() {
		return supportInfo;
	}
	public void setSupportInfo(String supportInfo) {
		this.supportInfo = supportInfo;
	}
	public String getPrimaryImgName() {
		return primaryImgName;
	}
	public void setPrimaryImgName(String primaryImgName) {
		this.primaryImgName = primaryImgName;
	}
	public String getExtensionId() {
		return extensionId;
	}
	public void setExtensionId(String extensionId) {
		this.extensionId = extensionId;
	}
	
	

}
