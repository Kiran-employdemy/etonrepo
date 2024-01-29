package com.eaton.platform.core.bean;

import java.util.List;

public class ProductGridSelectors {

	private List<FacetBean> facets;
	private String sortyByOption;
	private String sortFacet;
	private int loadMoreOption;
	private String searchTerm;
	private String extensionId;
	private String selectedTab;
	private String autoCorrect;
	private int productCount;
	private int newCount;
	private int resourceCount;
	private int allCount;
	private int serviceCount;
	
	public List<FacetBean> getFacets() {
		return facets;
	}
	public void setFacets(List<FacetBean> facets) {
		this.facets = facets;
	}
	
	public String getSortyByOption() {
		return sortyByOption;
	}
	public void setSortyByOption(String sortyByOption) {
		this.sortyByOption = sortyByOption;
	}
	public int getLoadMoreOption() {
		return loadMoreOption;
	}
	public void setLoadMoreOption(int loadMoreOption) {
		this.loadMoreOption = loadMoreOption;
	}
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	public String getExtensionId() {
		return extensionId;
	}
	public void setExtensionId(String extensionId) {
		this.extensionId = extensionId;
	}
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	public String getAutoCorrect() {
		return autoCorrect;
	}
	public void setAutoCorrect(String autoCorrect) {
		this.autoCorrect = autoCorrect;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public int getNewCount() {
		return newCount;
	}
	public void setNewCount(int newCount) {
		this.newCount = newCount;
	}
	public int getResourceCount() {
		return resourceCount;
	}
	public void setResourceCount(int resourceCount) {
		this.resourceCount = resourceCount;
	}
	public int getAllCount() {
		return allCount;
	}
	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}
	public int getServiceCount() {
		return serviceCount;
	}
	public void setServiceCount(int serviceCount) {
		this.serviceCount = serviceCount;
	}

	public String getSortFacet() {
		return sortFacet;
	}

	public void setSortFacet(String sortFacet) {
		this.sortFacet = sortFacet;
	}
}
