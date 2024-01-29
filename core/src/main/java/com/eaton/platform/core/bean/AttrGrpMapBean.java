package com.eaton.platform.core.bean;

import java.util.Map;

public class AttrGrpMapBean {

private String attrGrpId;	
private Map<String,Map<String,String>> attrNmItemsMap;

public Map<String, Map<String, String>> getAttrNmItemsMap() {
	return attrNmItemsMap;
}

public void setAttrNmItemsMap(Map<String, Map<String, String>> attrNmItemsMap) {
	this.attrNmItemsMap = attrNmItemsMap;
}

public String getAttrGrpId() {
	return attrGrpId;
}

public void setAttrGrpId(String attrGrpId) {
	this.attrGrpId = attrGrpId;
}

	

}
