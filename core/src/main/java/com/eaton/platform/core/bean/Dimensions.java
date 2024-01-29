package com.eaton.platform.core.bean;

import java.util.ArrayList;

public class Dimensions {
	
	private String key;
	private String id;
	private String label;
	private String values;
	private String names;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getNames() {
		return names;
	}
	public void setNames(String names) {
		this.names = names;
	}
	
	public String getKeyLowerCase() {
		return key.toLowerCase();
	}
}
