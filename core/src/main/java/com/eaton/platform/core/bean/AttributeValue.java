package com.eaton.platform.core.bean;

public class AttributeValue {
	
	String attributeType;
	String cdata;
	String cidCdata;
	String[] country;
	String language;
	String priority;
	
	String linkURL;
	String imageURL;
	String documentURL;
	
	public String getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	public String getCdata() {
		return cdata;
	}
	public void setCdata(String cdata) {
		this.cdata = cdata;
	}
	public String[] getCountry() {
		return country;
	}
	public void setCountry(String[] country) {
		this.country = country;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getCidCdata() {
		return cidCdata;
	}
	public void setCidCdata(String cidCdata) {
		this.cidCdata = cidCdata;
	}
	public String getLinkURL() {
		return linkURL;
	}
	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getDocumentURL() {
		return documentURL;
	}
	public void setDocumentURL(String documentURL) {
		this.documentURL = documentURL;
	}
	
	
	

}
