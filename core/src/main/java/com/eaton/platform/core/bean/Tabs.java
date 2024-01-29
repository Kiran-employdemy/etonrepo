package com.eaton.platform.core.bean;

public class Tabs {
	
	  private String label;
      private String id;
      private boolean isHiddenTab;
      private boolean isActiveTab;
      private boolean isDisabledTab;
      private int resultsCount;
      private String startUrl;
      private String endUrl;
      private String target;
      
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isHiddenTab() {
		return isHiddenTab;
	}
	public void setHiddenTab(boolean isHiddenTab) {
		this.isHiddenTab = isHiddenTab;
	}
	public boolean isActiveTab() {
		return isActiveTab;
	}
	public void setActiveTab(boolean isActiveTab) {
		this.isActiveTab = isActiveTab;
	}
	public boolean isDisabledTab() {
		return isDisabledTab;
	}
	public void setDisabledTab(boolean isDisabledTab) {
		this.isDisabledTab = isDisabledTab;
	}
	public int getResultsCount() {
		return resultsCount;
	}
	public void setResultsCount(int resultsCount) {
		this.resultsCount = resultsCount;
	}
	
	/*public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}*/
	
	public String getTarget() {
		return target;
	}
	public String getStartUrl() {
		return startUrl;
	}
	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}
	public String getEndUrl() {
		return endUrl;
	}
	public void setEndUrl(String endUrl) {
		this.endUrl = endUrl;
	}
	public void setTarget(String target) {
		this.target = target;
	}
      
      

}
