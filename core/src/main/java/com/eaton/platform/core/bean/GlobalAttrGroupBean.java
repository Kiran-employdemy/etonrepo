package com.eaton.platform.core.bean;

import java.io.Serializable;

public class GlobalAttrGroupBean implements Serializable {
	
	private static final long serialVersionUID = 2774540130111279667L;
	
	/** The attr grp name. */
	//private String attrGrpName;
	
	/** The attr value. */
	private String attrValue;
	
	/**
	 * @return the attrValue
	 */
	public String getAttrValue() {
		return attrValue;
	}
	/**
	 * @param attrValue the attrValue to set
	 */
	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}
	
	
	

}
