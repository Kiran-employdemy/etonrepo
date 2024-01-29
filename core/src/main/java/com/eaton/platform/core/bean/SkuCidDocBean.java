package com.eaton.platform.core.bean;

import java.io.Serializable;

public class SkuCidDocBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String docId;
	private String docPriority;
	private String docSubtype;
	private String docCdataValue;
	private String[] country;
	private String language;
	private String type;

	public void setDocId(String docId) {
		this.docId =docId;
		
	}

	public void setDocPriority(String docPriority) {
		this.docPriority=docPriority;
		
	}

	public void setDocSubtype(String docSubtype) {
		this.docSubtype=docSubtype;
		
	}

	public void setDocCdataValue(String docCdataValue) {
		this.docCdataValue =docCdataValue;
		
	}

	public String getDocId() {
		return docId;
	}

	public String getDocPriority() {
		return docPriority;
	}

	public String getDocSubtype() {
		return docSubtype;
	}

	public String getDocCdataValue() {
		return docCdataValue;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
