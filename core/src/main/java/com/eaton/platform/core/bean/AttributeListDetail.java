package com.eaton.platform.core.bean;

import com.eaton.platform.core.bean.attributetable.AttributeTable;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AttributeListDetail {

	String attributeGroupName;
	List<Attribute> attributeList;
	/**
	 * @return the attributeGroupName
	 */
	public String getAttributeGroupName() {
		return attributeGroupName;
	}
	/**
	 * @param attributeGroupName the attributeGroupName to set
	 */
	public void setAttributeGroupName(String attributeGroupName) {
		this.attributeGroupName = attributeGroupName;
	}
	/**
	 * @return the attributeList
	 */
	public List<Attribute> getAttributeList() {
		return attributeList;
	}
	/**
	 * @param attributeList the attributeList to set
	 */
	public void setAttributeList(List<Attribute> attributeList) {
		this.attributeList = attributeList;
	}


	public Set<AttributeTable> filterOutAttributeTables() {
		Set<AttributeTable> attributeTables = new LinkedHashSet<>();
		List<Attribute> newList = new LinkedList<>();
		attributeList.forEach((Attribute attribute) -> {
			if (attribute instanceof AttributeTable) {
				attributeTables.add((AttributeTable) attribute);
			} else {
				newList.add(attribute);
			}
		});
		attributeList = newList;
		return attributeTables;
	}
}
