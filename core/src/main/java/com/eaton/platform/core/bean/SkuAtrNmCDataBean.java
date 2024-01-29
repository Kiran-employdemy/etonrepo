package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.util.Map;

public class SkuAtrNmCDataBean implements Serializable {
	private transient Map<String,String> attrNmIdCDataMap;//SonarQube private or transient issue


	public Map<String, String> getAttrNmIdCDataMap() {
		return attrNmIdCDataMap;
	}

	public void setAttrNmIdCDataMap(Map<String, String> attrNmIdCDataMap) {
		this.attrNmIdCDataMap = attrNmIdCDataMap;
	}

	
}
