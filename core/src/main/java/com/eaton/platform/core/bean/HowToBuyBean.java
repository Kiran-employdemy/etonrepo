package com.eaton.platform.core.bean;

import com.eaton.platform.core.util.CommonUtil;
import com.google.common.base.Strings;

/**
 * The HowToBuyBean with aspects of each item in the "how to buy" component grid.
 */
public class HowToBuyBean {

	private String title;
	private String dscription;
	private String icon;
	private String link;
	private String openInNewWindow;
	private String dropdownIcon;
	private boolean hasClearfix;
	private boolean skuOnly;
	private boolean isSuffixEnabled;
	private boolean isModalEnabled;
	private boolean isSourceTrackingEnabled;


	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDscription() {
		return dscription;
	}
	public void setDscription(String dscription) {
		this.dscription = dscription;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getDropdownIcon() {
		return dropdownIcon;
	}
	public void setDropdownIcon(String dropdownIcon) {
		this.dropdownIcon = dropdownIcon;
	}
	public String getLink() {
		if (link == null) {
			return null;
		}
		String[] returnVal;
		String selector;
		returnVal = this.link.split("\\?");
		if(returnVal.length > 1){
			for (int i = 0; i< returnVal.length-1 ; i++){
				selector = CommonUtil.dotHtmlLink(returnVal[i]);
				link = selector.concat("?").concat(returnVal[i+1]);
		   }
			return link ;
		} else {
			return CommonUtil.dotHtmlLink(link);
		}
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getOpenInNewWindow() {
		if(!Strings.isNullOrEmpty(openInNewWindow)) {
			return openInNewWindow;
		} else {
			return "_blank";
		}
	}
	public void setOpenInNewWindow(String openInNewWindow) {

		this.openInNewWindow = openInNewWindow;
	}

	/** Return whether to use clearfix in the HTL, based upon number of columns expected.
	 *
	 * @return hasClearfix
	 */
	public boolean hasClearfix() {
		return this.hasClearfix;
	}

	public void setHasClearfix(boolean addClearfix) {
		this.hasClearfix = addClearfix;
	}

	public boolean isSkuOnly() {
		return this.skuOnly;
	}

	public void setSkuOnly(boolean skuOnly) {
		this.skuOnly = skuOnly;
	}

	public boolean isSuffixEnabled() {
		return isSuffixEnabled;
	}

	public void setSuffixEnabled(boolean suffixEnabled) {
		isSuffixEnabled = suffixEnabled;
	}

	public boolean isModalEnabled() {
		return isModalEnabled;
	}

	public void setModalEnabled(boolean modalEnabled) {
		isModalEnabled = modalEnabled;
	}

	public boolean isSourceTrackingEnabled() {
		return isSourceTrackingEnabled;
	}

	public void setSourceTrackingEnabled(boolean sourceTrackingEnabled) {
		isSourceTrackingEnabled = sourceTrackingEnabled;
	}
}
