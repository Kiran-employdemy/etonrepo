package com.eaton.platform.core.bean;

import java.util.Map;

public class ImageGroupBean {
	
	private String id;
	private String label;
	private Map<String,AttrNmBean> imageRenditionMap;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Map<String, AttrNmBean> getImageRenditionMap() {
		return imageRenditionMap;
	}
	public void setImageRenditionMap(Map<String, AttrNmBean> imageRenditionMap) {
		this.imageRenditionMap = imageRenditionMap;
	}
	
	

}
