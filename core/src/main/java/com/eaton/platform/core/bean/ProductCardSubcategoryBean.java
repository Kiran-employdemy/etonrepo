package com.eaton.platform.core.bean;

/**
 * This class maintains Product subcategory page information, including labels for buttons and modals.
 */
public class ProductCardSubcategoryBean {
	
	private String ajaxRequestUrl;
	private String ajaxRequestNextPage;
	private String goTo;
	private boolean isPriceDisclaimerEnabled = false;
	private String priceDisclaimer;
	private String loadMore;
	private String specificationTabTitle;
	private String modelCodeTitle;
	private String resourceTabTitle;
	private String compareTabTitle;	
	private String compareProducts;
	private String comparisionTable;
	private String clearSelection;
	private String compareButton;
	private String maxErrorHeading;
	private String maxErrorDescription;
	private String maxErrorOk;
	private String minErrorHeading;
	private String minErrorDescription;
	private String minErrorCancel;
	private String backToSelection;
	private String highlightDifferences;
	private String showOnlyDifferences;
	private String hideBlankFeatures;
	
	
	public String getSpecificationTabTitle() {
		return specificationTabTitle;
	}
	public void setSpecificationTabTitle(String specificationTabTitle) {
		this.specificationTabTitle = specificationTabTitle;
	}
	public String getModelCodeTitle() {
		return modelCodeTitle;
	}
	public void setModelCodeTitle(String modelCodeTitle) {
		this.modelCodeTitle = modelCodeTitle;
	}
	public String getResourceTabTitle() {
		return resourceTabTitle;
	}
	public void setResourceTabTitle(String resourceTabTitle) {
		this.resourceTabTitle = resourceTabTitle;
	}
	public String getAjaxRequestUrl() {
		return ajaxRequestUrl;
	}
	public void setAjaxRequestUrl(String ajaxRequestUrl) {
		this.ajaxRequestUrl = ajaxRequestUrl;
	}
	public String getAjaxRequestNextPage() {
		return ajaxRequestNextPage;
	}
	public void setAjaxRequestNextPage(String ajaxRequestNextPage) {
		this.ajaxRequestNextPage = ajaxRequestNextPage;
	}
	public String getGoTo() {
		return goTo;
	}
	public void setGoTo(String goTo) {
		this.goTo = goTo;
	}
	public boolean isPriceDisclaimerEnabled() {
	    return this.isPriceDisclaimerEnabled;
	}

	public void setIsPriceDisclaimerEnabled(boolean enabled) {
		this.isPriceDisclaimerEnabled = enabled;
	}
	public String getPriceDisclaimer() {
		return priceDisclaimer;
	}
	public void setPriceDisclaimer(String priceDisclaimer) {
		this.priceDisclaimer = priceDisclaimer;
	}
	public String getLoadMore() {
		return loadMore;
	}
	public void setLoadMore(String loadMore) {
		this.loadMore = loadMore;
	}
	
	public String getCompareTabTitle() {
		return compareTabTitle;
	}
	public void setCompareTabTitle(String compareTabTitle) {
		this.compareTabTitle = compareTabTitle;
	}
	public String getCompareProducts() {
		return compareProducts;
	}
	public void setCompareProducts(String compareProducts) {
		this.compareProducts = compareProducts;
	}
	public String getComparisionTable() {
		return comparisionTable;
	}
	public void setComparisionTable(String comparisionTable) {
		this.comparisionTable = comparisionTable;
	}
	public String getClearSelection() {
		return clearSelection;
	}
	public void setClearSelection(String clearSelection) {
		this.clearSelection = clearSelection;
	}
	public String getCompareButton() {
		return compareButton;
	}
	public void setCompareButton(String compareButton) {
		this.compareButton = compareButton;
	}
	public String getMaxErrorHeading() {
		return maxErrorHeading;
	}
	public void setMaxErrorHeading(String maxErrorHeading) {
		this.maxErrorHeading = maxErrorHeading;
	}
	public String getMaxErrorDescription() {
		return maxErrorDescription;
	}
	public void setMaxErrorDescription(String maxErrorDescription) {
		this.maxErrorDescription = maxErrorDescription;
	}
	public String getMaxErrorOk() {
		return maxErrorOk;
	}
	public void setMaxErrorOk(String maxErrorOk) {
		this.maxErrorOk = maxErrorOk;
	}
	public String getMinErrorHeading() {
		return minErrorHeading;
	}
	public void setMinErrorHeading(String minErrorHeading) {
		this.minErrorHeading = minErrorHeading;
	}
	public String getMinErrorDescription() {
		return minErrorDescription;
	}
	public void setMinErrorDescription(String minErrorDescription) {
		this.minErrorDescription = minErrorDescription;
	}
	public String getMinErrorCancel() {
		return minErrorCancel;
	}
	public void setMinErrorCancel(String minErrorCancel) {
		this.minErrorCancel = minErrorCancel;
	}
	public String getBackToSelection() {
		return backToSelection;
	}
	public void setBackToSelection(String backToSelection) {
		this.backToSelection = backToSelection;
	}
	public String getHighlightDifferences() {
		return highlightDifferences;
	}
	public void setHighlightDifferences(String highlightDifferences) {
		this.highlightDifferences = highlightDifferences;
	}
	public String getShowOnlyDifferences() {
		return showOnlyDifferences;
	}
	public void setShowOnlyDifferences(String showOnlyDifferences) {
		this.showOnlyDifferences = showOnlyDifferences;
	}
	public String getHideBlankFeatures() {
		return hideBlankFeatures;
	}
	public void setHideBlankFeatures(String hideBlankFeatures) {
		this.hideBlankFeatures = hideBlankFeatures;
	}
}
