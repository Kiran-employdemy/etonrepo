package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.util.List;

public class MultiColumnAttributeBean implements Serializable{

	/**
	 * 	 The Constant serialVersionUID. 
	 */
	private static final long serialVersionUID = -5713451393730459115L;
	
	/** The attribute Name. */
	private String attributeName;
	
	/** The attribute Value */
	private List<String> attributeValueList;

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return the attributeValue
	 */
	public List<String> getAttributeValueList() {
		return attributeValueList;
	}

	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValueList(List<String> attributeValue) {
		this.attributeValueList = attributeValue;
	}
	
	

}
