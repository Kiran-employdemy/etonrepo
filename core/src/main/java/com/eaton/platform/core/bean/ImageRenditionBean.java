package com.eaton.platform.core.bean;

import java.io.Serializable;

public class ImageRenditionBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3267200244905251891L;

	private String desktopTransformedUrl ;
	private String mobileTransformedUrl ;
	private String tabletTransformedUrl;
	private String altText;
	
	public String getDesktopTransformedUrl() {
		return desktopTransformedUrl;
	}
	public void setDesktopTransformedUrl(String desktopTransformedUrl) {
		this.desktopTransformedUrl = desktopTransformedUrl;
	}
	public String getMobileTransformedUrl() {
		return mobileTransformedUrl;
	}
	public void setMobileTransformedUrl(String mobileTransformedUrl) {
		this.mobileTransformedUrl = mobileTransformedUrl;
	}
	public String getTabletTransformedUrl() {
		return tabletTransformedUrl;
	}
	public void setTabletTransformedUrl(String tabletTransformedUrl) {
		this.tabletTransformedUrl = tabletTransformedUrl;
	}
	public String getAltText() {
		return altText;
	}
	public void setAltText(String altText) {
		this.altText = altText;
	}

}
