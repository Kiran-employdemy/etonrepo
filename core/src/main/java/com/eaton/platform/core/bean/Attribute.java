package com.eaton.platform.core.bean;

public class Attribute {

	String attributeLabel;
	String attributeId;
	//String attributeValue;
	AttributeValue attributeValue;

	String attributeType;
	String attributeOpenNewWindow;
	Boolean attributeIsNew;
	String attributeSize;
	String attributeDate;
	private Integer rowId;

	/**
	 * @return the attributeLabel
	 */
	public String getAttributeLabel() {
		return attributeLabel;
	}
	/**
	 * @param attributeLabel the attributeLabel to set
	 */
	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}
	/**
	 * @return the attributeId
	 */
	public String getAttributeId() {
		return attributeId;
	}
	/**
	 * @param attributeId the attributeId to set
	 */
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public String getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	public String getAttributeOpenNewWindow() {
		return attributeOpenNewWindow;
	}
	public void setAttributeOpenNewWindow(String attributeOpenNewWindow) {
		this.attributeOpenNewWindow = attributeOpenNewWindow;
	}
	public Boolean getAttributeIsNew() {
		return attributeIsNew;
	}
	public void setAttributeIsNew(Boolean attributeIsNew) {
		this.attributeIsNew = attributeIsNew;
	}
	public String getAttributeSize() {
		return attributeSize;
	}
	public void setAttributeSize(String attributeSize) {
		this.attributeSize = attributeSize;
	}
	public String getAttributeDate() {
		return attributeDate;
	}
	public void setAttributeDate(String attributeDate) {
		this.attributeDate = attributeDate;
	}
	public AttributeValue getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(AttributeValue attributeValue) {
		this.attributeValue = attributeValue;
	}


	public void setRowId(Integer rowid) {
		this.rowId = rowid;
	}

	public Integer getRowId() {
		return rowId;
	}
}
