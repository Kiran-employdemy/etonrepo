package com.eaton.platform.core.bean;

import java.util.List;
import com.google.gson.Gson;

public class ResourceListDetail {
	
	String resourceGroupName;
	String resourceGroupDescription;
	List<Document> documentList;
	String partSolutionURL;
	List<String> documentLinks;
	private int counter;
	/**
	 * @return the resourceGroupName
	 */
	public String getResourceGroupName() {
		return resourceGroupName;
	}
	/**
	 * @param resourceGroupName the resourceGroupName to set
	 */
	public void setResourceGroupName(String resourceGroupName) {
		this.resourceGroupName = resourceGroupName;
	}
	/**
	 * @return the resourceGroupDescription
	 */
	public String getResourceGroupDescription() {
		return resourceGroupDescription;
	}
	/**
	 * @param resourceGroupDescription the resourceGroupDescription to set
	 */
	public void setResourceGroupDescription(String resourceGroupDescription) {
		this.resourceGroupDescription = resourceGroupDescription;
	}
	/**
	 * @return the documentList
	 */
	public List<Document> getDocumentList() {
		return documentList;
	}
	/**
	 * @param documentList the documentList to set
	 */
	public void setDocumentList(List<Document> documentList) {
		this.documentList = documentList;
	}

	public String getPartSolutionURL() {
		return partSolutionURL;
	}

	public void setPartSolutionURL(String partSolutionURL) {
		this.partSolutionURL = partSolutionURL;
	}

	public List<String> getDocumentLinks() {
		return documentLinks;
	}

	public String getDocumentLinksJson() {
		return new Gson().toJson(documentLinks);
	}

	public void setDocumentLinks(List<String> documentLinks) {
		this.documentLinks = documentLinks;
	}

	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
}
