package com.eaton.platform.core.bean;

import java.util.List;
import java.util.Map;

public class AttrGrpMapListBean {
List<Map<String, Map<String, String>>> attrGrpIdNmCDataMapList;
List<Map<String, Map<String, Map<String, String>>>> attrNmItemsMapList;

	
	public List<Map<String, Map<String, String>>> getAttrGrpIdNmCDataMapList() {
	return attrGrpIdNmCDataMapList;
}
	public void setAttrGrpNmCDataMap(List<Map<String, Map<String, String>>> attrNmCDataMapList) {
		this.attrGrpIdNmCDataMapList = attrNmCDataMapList;
		
	}
	public void setAttrGrpNmItemsMap(
			List<Map<String, Map<String, Map<String, String>>>> attrNmItemsMapList) {
		this.attrNmItemsMapList = attrNmItemsMapList;
		
	}
	public List<Map<String, Map<String, Map<String, String>>>> getAttrNmItemsMapList() {
		return attrNmItemsMapList;
	}

}
