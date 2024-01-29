package com.eaton.platform.core.bean;

import java.util.ArrayList;

public class ResourceCategory {
	
	String ResourceCategoryID;
	ArrayList<String> PDHGroupIDs;
	
	public String getResourceCategoryID() {
		return ResourceCategoryID;
	}
	public void setResourceCategoryID(String resourceCategoryID) {
		ResourceCategoryID = resourceCategoryID;
	}
	public ArrayList<String> getPDHGroupIDs() {
		return PDHGroupIDs;
	}
	public void setPDHGroupIDs(ArrayList<String> pDHGroupIDs) {
		PDHGroupIDs = pDHGroupIDs;
	}
	
	
	

}
