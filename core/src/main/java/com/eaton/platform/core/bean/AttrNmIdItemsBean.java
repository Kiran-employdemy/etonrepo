package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.util.Map;

public class AttrNmIdItemsBean implements Serializable{


	private static final long serialVersionUID = 1L;
	private String atrNmId;
	private Map<String, String> atrNmElementMap;
	public String getAtrNmId() {
		return atrNmId;
	}
	public void setAtrNmId(String atrNmId) {
		this.atrNmId = atrNmId;
	}
	public Map<String, String> getAtrNmElementMap() {
		return atrNmElementMap;
	}
	public void setAtrNmElementMap(Map<String, String> atrNmElementMap) {
		this.atrNmElementMap = atrNmElementMap;
	}

}
