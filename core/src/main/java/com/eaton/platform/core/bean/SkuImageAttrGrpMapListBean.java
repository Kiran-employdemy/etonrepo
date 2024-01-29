package com.eaton.platform.core.bean;

import java.util.List;
import java.util.Map;

public class SkuImageAttrGrpMapListBean {
private List<Map<String,Map<String,String>>>attrGrpMapList;

public List<Map<String, Map<String, String>>> getAttrGrpMapList() {
	return attrGrpMapList;
}

public void setAttrGrpMapList(
		List<Map<String, Map<String, String>>> attrGrpMapList) {
	this.attrGrpMapList = attrGrpMapList;
}
}
