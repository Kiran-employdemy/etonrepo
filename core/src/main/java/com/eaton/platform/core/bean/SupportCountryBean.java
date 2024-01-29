package com.eaton.platform.core.bean;

import java.io.Serializable;

public class SupportCountryBean implements Serializable {
	
	private static final long serialVersionUID = 2774540130111279667L;
	private String text;
	private String value;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
