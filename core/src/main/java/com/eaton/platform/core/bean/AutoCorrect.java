package com.eaton.platform.core.bean;

public class AutoCorrect {
	
	private boolean enabled;
	private String searchTerm;
	private String correctedTerm;
	private String url;
	private String target;
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	public String getCorrectedTerm() {
		return correctedTerm;
	}
	public void setCorrectedTerm(String correctedTerm) {
		this.correctedTerm = correctedTerm;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	
	

}
