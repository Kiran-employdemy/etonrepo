package com.eaton.platform.core.bean;

import java.util.List;

public class TaxynomyAttributeGroupBean {
	
	String groupName;
	List<String> attributeList;
	String groupId;
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return the attributeList
	 */
	public List<String> getAttributeList() {
		return attributeList;
	}
	/**
	 * @param attributeList the attributeList to set
	 */
	public void setAttributeList(List<String> attributeList) {
		this.attributeList = attributeList;
	}
	
	

	public String getGroupId() {
		return groupId;

	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String toString() {
		return "GroupName: "+groupName + " attributeList: "+attributeList;
	}
	
	

}
