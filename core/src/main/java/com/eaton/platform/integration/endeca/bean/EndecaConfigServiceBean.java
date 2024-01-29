package com.eaton.platform.integration.endeca.bean;

import com.eaton.platform.core.search.api.FacetValueIdsProvider;

import java.io.Serializable;

/**
 * The Class EndecaConfigServiceBean.
 */
public class EndecaConfigServiceBean implements Serializable, FacetValueIdsProvider {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7316005083888601077L;

	/** The esp service URL. */
	private String espServiceURL;

	/** The sku details app name. */
	private String skuDetailsAppName;

	/** The esp app key. */
	private String espAppKey;

	/** The esp app key. */
	private String compatibilityAppName;

	/** The stub response path. */
	private String stubResponsePath;

	/** The stub res enabled. */
	private Boolean stubResEnabled;

	/** The products tab id. */
	private String productsTabId;

	/** The news tab id. */
	private String newsTabId;

	/** The resources tab id. */
	private String resourcesTabId;

	/** The services tab id. */
	private String servicesTabId;

	/** The endeca user agent value. */
	private String endecaUserAgentValue;

	/** The productfamily sitemap app name. */
	private String productfamilySitemapAppName;

	/** The productfamily sitemap app name. */
	private String pfSitemapNumberOfRecordsToReturn;

	/** The orphan sku sitemap no of records to return */
	private String skuSitemapNumberOfRecordsToReturn;

	/** The productfamily app name. */
	private String productfamilyAppName;

	/** The orphan sku app name. */
	private String skuSitemapAppName;

	/** The subcategory app name. */
	private String subcategoryAppName;

	/** The LearnPage - facetValue. */
	private String facetLearnPage;

	/** The sitesearch app name. */
	private String sitesearchAppName;

	/** The endeca PDH 1 PDH 2 servie URL. */
	private String endecaPDH1PDH2ServieURL;

	/** The clutch selctor app name. */
	// VGSelector
	private String clutchSelctorAppName;

	/** The torque selector app name. */
	private String torqueSelectorAppName;

	/** The vg selector endeca path. */
	private String vgSelectorEndecaURL;

	/** The submittal builder app name. */
	private String submittalBuilderAppName;

	/** The eaton content hub app name. */
	private String eatonContentHubAppName;

	/** The cross reference app name. */
	private String crossReferenceAppName;

	/** File types. */
	private String[] fileTypes;

	/** The cross comparison app name. */
	private String comparisonAppName;

	private String productFamilyActiveFacetID;

	/**
	 * Gets the esp service URL.
	 *
	 * @return the espServiceURL
	 */
	public String getEspServiceURL() {
		return espServiceURL;
	}

	/**
	 * Sets the esp service URL.
	 *
	 * @param espServiceURL the espServiceURL to set
	 */
	public void setEspServiceURL(String espServiceURL) {
		this.espServiceURL = espServiceURL;
	}

	/**
	 * Gets the sku details app name.
	 *
	 * @return the skuDetailsAppName
	 */
	public String getSkuDetailsAppName() {
		return skuDetailsAppName;
	}

	/**
	 * Sets the sku details app name.
	 *
	 * @param skuDetailsAppName the skuDetailsAppName to set
	 */
	public void setSkuDetailsAppName(String skuDetailsAppName) {
		this.skuDetailsAppName = skuDetailsAppName;
	}

	/**
	 * Gets the esp app key.
	 *
	 * @return the compatibilityAppName
	 */
	public String getCompatibilityAppName() {
		return compatibilityAppName;
	}

	/**
	 * Sets the sku details app name.
	 *
	 * @param compatibilityAppName the skuDetailsAppName to set
	 */
	public void setCompatibilityAppName(String compatibilityAppName) {
		this.compatibilityAppName = compatibilityAppName;
	}

	/**
	 * Gets the esp app key.
	 *
	 * @return the espAppKey
	 */
	public String getEspAppKey() {
		return espAppKey;
	}

	/**
	 * Sets the esp app key.
	 *
	 * @param espAppKey the espAppKey to set
	 */
	public void setEspAppKey(String espAppKey) {
		this.espAppKey = espAppKey;
	}

	/**
	 * Gets the stub response path.
	 *
	 * @return the stubResponsePath
	 */
	public String getStubResponsePath() {
		return stubResponsePath;
	}

	/**
	 * Sets the stub response path.
	 *
	 * @param stubResponsePath the stubResponsePath to set
	 */
	public void setStubResponsePath(String stubResponsePath) {
		this.stubResponsePath = stubResponsePath;
	}

	/**
	 * Gets the stub res enabled.
	 *
	 * @return the stubResEnabled
	 */
	public Boolean getStubResEnabled() {
		return stubResEnabled;
	}

	/**
	 * Sets the stub res enabled.
	 *
	 * @param stubResEnabled the stubResEnabled to set
	 */
	public void setStubResEnabled(Boolean stubResEnabled) {
		this.stubResEnabled = stubResEnabled;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the products tab id.
	 *
	 * @return the productsTabId
	 */
	public String getProductsTabId() {
		return productsTabId;
	}

	/**
	 * Sets the products tab id.
	 *
	 * @param productsTabId the productsTabId to set
	 */
	public void setProductsTabId(String productsTabId) {
		this.productsTabId = productsTabId;
	}

	/**
	 * Gets the news tab id.
	 *
	 * @return the newsTabId
	 */
	public String getNewsTabId() {
		return newsTabId;
	}

	/**
	 * Sets the news tab id.
	 *
	 * @param newsTabId the newsTabId to set
	 */
	public void setNewsTabId(String newsTabId) {
		this.newsTabId = newsTabId;
	}

	/**
	 * Gets the resources tab id.
	 *
	 * @return the resourcesTabId
	 */
	public String getResourcesTabId() {
		return resourcesTabId;
	}

	/**
	 * Sets the resources tab id.
	 *
	 * @param resourcesTabId the resourcesTabId to set
	 */
	public void setResourcesTabId(String resourcesTabId) {
		this.resourcesTabId = resourcesTabId;
	}

	/**
	 * Gets the services tab id.
	 *
	 * @return the services tab id
	 */
	public String getServicesTabId() {
		return servicesTabId;
	}

	/**
	 * Sets the services tab id.
	 *
	 * @param servicesTabId the new services tab id
	 */
	public void setServicesTabId(String servicesTabId) {
		this.servicesTabId = servicesTabId;
	}

	/**
	 * Gets the endeca user agent value.
	 *
	 * @return the endeca user agent value
	 */
	public String getEndecaUserAgentValue() {
		return endecaUserAgentValue;
	}

	/**
	 * Sets the endeca user agent value.
	 *
	 * @param endecaUserAgentValue the new endeca user agent value
	 */
	public void setEndecaUserAgentValue(String endecaUserAgentValue) {
		this.endecaUserAgentValue = endecaUserAgentValue;
	}

	/**
	 * Gets the productfamily sitemap app name.
	 *
	 * @return the productfamilySitemapAppName
	 */
	public String getProductfamilySitemapAppName() {
		return productfamilySitemapAppName;
	}

	/**
	 * Sets the productfamily sitemap app name.
	 *
	 * @param the productfamilySitemapAppName to set
	 */
	public void setProductfamilySitemapAppName(String productfamilySitemapAppName) {
		this.productfamilySitemapAppName = productfamilySitemapAppName;
	}

	/**
	 * Sets the productfamily sitemap number of records to return.
	 *
	 * @param the pfSitemapNumberOfRecordsToReturn to set
	 */
	public void setPfSitemapNumberOfRecordsToReturn(String pfSitemapNumberOfRecordsToReturn) {
		this.pfSitemapNumberOfRecordsToReturn = pfSitemapNumberOfRecordsToReturn;
	}

	/**
	 * Gets the productfamily sitemap number of records to return.
	 *
	 * @return the pfSitemapNumberOfRecordsToReturn
	 */
	public String getPfSitemapNumberOfRecordsToReturn() {
		return pfSitemapNumberOfRecordsToReturn;
	}

	/**
	 * Gets the Orphan sku sitemap app name.
	 *
	 * @return the skuSitemapAppName
	 */
	public String getSkuSitemapAppName() {
		return skuSitemapAppName;
	}

	/**
	 * Sets the Orphan sku sitemap app name.
	 *
	 * @param the skuSitemapAppName to set
	 */
	public void setSkuSitemapAppName(String skuSitemapAppName) {
		this.skuSitemapAppName = skuSitemapAppName;
	}

	/**
	 * Gets the Orphan sku sitemap number of records to return.
	 *
	 * @return the skuSitemapNumberOfRecordsToReturn
	 */
	public String getSkuSitemapNumberOfRecordsToReturn() {
		return skuSitemapNumberOfRecordsToReturn;
	}

	/**
	 * Sets the Orphan sku sitemap number of records to return.
	 *
	 * @param the skuSitemapNumberOfRecordsToReturn to set
	 */
	public void setSkuSitemapNumberOfRecordsToReturn(String skuSitemapNumberOfRecordsToReturn) {
		this.skuSitemapNumberOfRecordsToReturn = skuSitemapNumberOfRecordsToReturn;
	}

	/**
	 * Gets the productfamily app name.
	 *
	 * @return the productfamilyAppName
	 */
	public String getProductfamilyAppName() {
		return productfamilyAppName;
	}

	/**
	 * Sets the productfamily app name.
	 *
	 * @param productfamilyAppName the productfamilyAppName to set
	 */
	public void setProductfamilyAppName(String productfamilyAppName) {
		this.productfamilyAppName = productfamilyAppName;
	}

	/**
	 * Gets the subcategory app name.
	 *
	 * @return the subcategoryAppName
	 */
	public String getSubcategoryAppName() {
		return subcategoryAppName;
	}

	/**
	 * Sets the subcategory app name.
	 *
	 * @param subcategoryAppName the subcategoryAppName to set
	 */
	public void setSubcategoryAppName(String subcategoryAppName) {
		this.subcategoryAppName = subcategoryAppName;
	}

	/**
	 * Gets the sitesearch app name.
	 *
	 * @return the sitesearchAppName
	 */
	public String getSitesearchAppName() {
		return sitesearchAppName;
	}

	/**
	 * Sets the sitesearch app name.
	 *
	 * @param sitesearchAppName the sitesearchAppName to set
	 */
	public void setSitesearchAppName(String sitesearchAppName) {
		this.sitesearchAppName = sitesearchAppName;
	}

	/**
	 * Gets the endeca PDH 1 PDH 2 servie URL.
	 *
	 * @return the endecaPDH1PDH2ServieURL
	 */
	public String getEndecaPDH1PDH2ServieURL() {
		return endecaPDH1PDH2ServieURL;
	}

	/**
	 * Sets the endeca PDH 1 PDH 2 servie URL.
	 *
	 * @param endecaPDH1PDH2ServieURL the endecaPDH1PDH2ServieURL to set
	 */
	public void setEndecaPDH1PDH2ServieURL(String endecaPDH1PDH2ServieURL) {
		this.endecaPDH1PDH2ServieURL = endecaPDH1PDH2ServieURL;
	}

	/**
	 * Gets the clutch selctor app name.
	 *
	 * @return the clutchSelctorAppName
	 */
	public String getClutchSelctorAppName() {
		return clutchSelctorAppName;
	}

	/**
	 * Sets the clutch selctor app name.
	 *
	 * @param clutchSelctorAppName the clutchSelctorAppName to set
	 */
	public void setClutchSelctorAppName(String clutchSelctorAppName) {
		this.clutchSelctorAppName = clutchSelctorAppName;
	}

	/**
	 * Gets the torque selector app name.
	 *
	 * @return the torqueSelectorAppName
	 */
	public String getTorqueSelectorAppName() {
		return torqueSelectorAppName;
	}

	/**
	 * Sets the torque selector app name.
	 *
	 * @param torqueSelectorAppName the torqueSelectorAppName to set
	 */
	public void setTorqueSelectorAppName(String torqueSelectorAppName) {
		this.torqueSelectorAppName = torqueSelectorAppName;
	}

	/**
	 * Gets the vg selector endeca path.
	 *
	 * @return the vg selector endeca path
	 */
	public String getVgSelectorEndecaURL() {
		return vgSelectorEndecaURL;
	}

	/**
	 * Sets the vg selector endeca path.
	 *
	 * @param vgSelectorEndecaURL the new vg selector endeca path
	 */
	public void setVgSelectorEndecaURL(String vgSelectorEndecaURL) {
		this.vgSelectorEndecaURL = vgSelectorEndecaURL;
	}

	/**
	 * Gets the submittal builder app name.
	 *
	 * @return the submittalBuilderAppName
	 */
	public String getSubmittalBuilderAppName() {
		return submittalBuilderAppName;
	}

	/**
	 * Sets the submittal builder app name.
	 *
	 * @param submittalBuilderAppName the submittalBuilderAppName to set
	 */
	public void setSubmittalBuilderAppName(String submittalBuilderAppName) {
		this.submittalBuilderAppName = submittalBuilderAppName;
	}

	public String getEatonContentHubAppName() {
		return eatonContentHubAppName;
	}

	public void setEatonContentHubAppName(String eatonContentHubAppName) {
		this.eatonContentHubAppName = eatonContentHubAppName;
	}

	public String getCrossReferenceAppName() {
		return crossReferenceAppName;
	}

	public void setCrossReferenceAppName(String crossReferenceAppName) {
		this.crossReferenceAppName = crossReferenceAppName;
	}

	/**
	 * Gets the file types
	 *
	 * @return fileTypes
	 */
	public String[] getFileTypes() {
		return fileTypes;
	}

	/**
	 * Sets the file types.
	 *
	 * @param fileTypes
	 */
	public void setFileTypes(String[] fileTypes) {
		this.fileTypes = fileTypes;
	}

	public String getComparisonAppName() {
		return comparisonAppName;
	}

	public void setComparisonAppName(String comparisonAppName) {
		this.comparisonAppName = comparisonAppName;
	}

	/**
	 * Gets the Facet Value for Learn Page.
	 *
	 * @return the facetLearnPage
	 */
	public String getFacetLearnPage() {
		return facetLearnPage;
	}

	/**
	 * Sets the Facet Value for Learn Page.
	 *
	 * @param facetLearnPage the facetLearnPage to set
	 */
	public void setFacetLearnPage(String facetLearnPage) {
		this.facetLearnPage = facetLearnPage;
	}

	public String getProductFamilyActiveFacetID() {
		return productFamilyActiveFacetID;
	}

	public void setProductFamilyActiveFacetID(String productFamilyActiveFacetID) {
		this.productFamilyActiveFacetID = productFamilyActiveFacetID;
	}

}
