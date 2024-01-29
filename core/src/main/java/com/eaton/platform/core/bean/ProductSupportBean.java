package com.eaton.platform.core.bean;

import java.io.Serializable;

public class ProductSupportBean implements Serializable{

	private static final long serialVersionUID = 3619492524453972499L;
	
	private String supportLabel;
	private String supportInformation;
	private String country;
	
	/**
	 * @return the supportLabel
	 */
	public String getSupportLabel() {
		return supportLabel;
	}
	/**
	 * @param supportLabel the supportLabel to set
	 */
	public void setSupportLabel(String supportLabel) {
		this.supportLabel = supportLabel;
	}
	/**
	 * @return the supportInformation
	 */
	public String getSupportInformation() {
		return supportInformation;
	}
	/**
	 * @param supportInformation the supportInformation to set
	 */
	public void setSupportInformation(String supportInformation) {
		this.supportInformation = supportInformation;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	

}
