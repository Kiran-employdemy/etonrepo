package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The Class ConfigServiceBean.
 */
public class ConfigServiceBean implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1409656172706414690L;
	
	/** The lov icon list page path list. */
	private List<String> lovIconListPagePathList;
	
    /** The link list views. */
	private List<String> linkListViews;
	
    /** The Icon List proof point symbols. */
	private List<String> proofPointSymbols;
	
	/** The numof days to publish. */
	private String numofDaysToPublish;
	
	/** The middle tab list page path list. */
	private List<String> middleTabListPagePathList;
	
	private Map<String,String> dynamicDropdownPathList;
	
	private List<String> iconListSymbols;
	
	/** The priceList folder path. */
	private String priceListFolderPath;
	
	/** The SKUPagePath folder path. */
	private String skupagename;
	
	/** The damExcelUploadPath folder path. */
	private String damExcelUploadPath;
	
	/** The damExcelArchivePath folder path. */
	private String damExcelArchivePath;
	
	/** The damJsonUploadPath folder path. */
	private String damJsonUploadPath;
	
	/** The damJsonArchivePath folder path. */
	private String damJsonArchivePath;
	
	/** The jsonRootElementName folder path. */
	private String jsonRootElementName;
	
	/** The json file name */
	private String jsonFileName;

	private List<String> resourceListingDrawingsFileExtensionList;
    
	
	
	/**
	 * @return the damExcelUploadPath
	 */
	public String getDamExcelUploadPath() {
		return damExcelUploadPath;
	}

	/**
	 * @param damExcelUploadPath the damExcelUploadPath to set
	 */
	public void setDamExcelUploadPath(String damExcelUploadPath) {
		this.damExcelUploadPath = damExcelUploadPath;
	}

	/**
	 * @return the damExcelArchivePath
	 */
	public String getDamExcelArchivePath() {
		return damExcelArchivePath;
	}

	/**
	 * @param damExcelArchivePath the damExcelArchivePath to set
	 */
	public void setDamExcelArchivePath(String damExcelArchivePath) {
		this.damExcelArchivePath = damExcelArchivePath;
	}

	/**
	 * @return the damJsonUploadPath
	 */
	public String getDamJsonUploadPath() {
		return damJsonUploadPath;
	}

	/**
	 * @param damJsonUploadPath the damJsonUploadPath to set
	 */
	public void setDamJsonUploadPath(String damJsonUploadPath) {
		this.damJsonUploadPath = damJsonUploadPath;
	}

	/**
	 * @return the damJsonArchivePath
	 */
	public String getDamJsonArchivePath() {
		return damJsonArchivePath;
	}

	/**
	 * @param damJsonArchivePath the damJsonArchivePath to set
	 */
	public void setDamJsonArchivePath(String damJsonArchivePath) {
		this.damJsonArchivePath = damJsonArchivePath;
	}

	/**
	 * @return the jsonRootElementName
	 */
	public String getJsonRootElementName() {
		return jsonRootElementName;
	}

	/**
	 * @param jsonRootElementName the jsonRootElementName to set
	 */
	public void setJsonRootElementName(String jsonRootElementName) {
		this.jsonRootElementName = jsonRootElementName;
	}

	/**
	 * @return the skupagename
	 */
	public String getSkupagename() {
		return skupagename;
	}

	/**
	 * @param skupagename the skupagename to set
	 */
	public void setSkupagename(String skupagename) {
		this.skupagename = skupagename;
	}

	/**
	 * Gets the lov icon list page path list.
	 *
	 * @return the lov icon list page path list
	 */
	public List<String> getLovIconListPagePathList() {
		return lovIconListPagePathList;
	}
	
	/**
	 * Sets the lov icon list page path list.
	 *
	 * @param lovIconListPagePathList the new lov icon list page path list
	 */
	public void setLovIconListPagePathList(List<String> lovIconListPagePathList) {
		this.lovIconListPagePathList = lovIconListPagePathList;
	}

	/**
	 * Gets the link list views.
	 *
	 * @return the link list views
	 */
	public List<String> getLinkListViews() {
		return linkListViews;
	}

	/**
	 * Sets the link list views.
	 *
	 * @param linkListViews the new link list views
	 */
	public void setLinkListViews(List<String> linkListViews) {
		this.linkListViews = linkListViews;
	}

	/**
	 * Gets the proof point symbols.
	 *
	 * @return the proof point symbols
	 */
	public List<String> getProofPointSymbols() {
		return proofPointSymbols;
	}

	/**
	 * Sets the proof point symbols.
	 *
	 * @param proofPointSymbols the new proof point symbols
	 */
	public void setProofPointSymbols(List<String> proofPointSymbols) {
		this.proofPointSymbols = proofPointSymbols;
	}

	/**
	 * @return the numofDaysToPublish
	 */
	public String getNumofDaysToPublish() {
		return numofDaysToPublish;
	}

	/**
	 * @param numofDaysToPublish the numofDaysToPublish to set
	 */
	public void setNumofDaysToPublish(String numofDaysToPublish) {
		this.numofDaysToPublish = numofDaysToPublish;
	}

	/**
	 * @return the middleTabListPagePathList
	 */
	public List<String> getMiddleTabListPagePathList() {
		return middleTabListPagePathList;
	}

	/**
	 * @param middleTabListPagePathList the middleTabListPagePathList to set
	 */
	public void setMiddleTabListPagePathList(List<String> middleTabListPagePathList) {
		this.middleTabListPagePathList = middleTabListPagePathList;
	}
	
	public Map<String, String> getDynamicDropdownPathList() {
		return dynamicDropdownPathList;
	}
	/**
	 * @param dynamicDropdownPathList the dynamicDropdownPathList to set
	 */
	public void setDynamicDropdownPathList(Map<String, String> dynamicDropdownPathList) {
		this.dynamicDropdownPathList = dynamicDropdownPathList;
	}
	/**
	 * @return iconListSymbols the iconListSymbols to set
	 */
	public List<String> getIconListSymbols() {
		return iconListSymbols;
	}

	public void setIconListSymbols(List<String> iconListSymbols) {
		this.iconListSymbols = iconListSymbols;
	}

	/**
	 * @return the priceListFolderPath
	 */
	public String getPriceListFolderPath() {
		return priceListFolderPath;
	}

	/**
	 * @param priceListFolderPath the priceListFolderPath to set
	 */
	public void setPriceListFolderPath(String priceListFolderPath) {
		this.priceListFolderPath = priceListFolderPath;
	}

	public String getJsonFileName() {
		return jsonFileName;
	}

	public void setJsonFileName(String jsonFileName) {
		this.jsonFileName = jsonFileName;
	}

	public List<String>  getResourceListingDrawingsFileExtensionList() {
		return resourceListingDrawingsFileExtensionList;
	}

	public void setResourceListingDrawingsFileExtensionList(List<String>  resourceListingDrawingsFileExtensionList) {
		this.resourceListingDrawingsFileExtensionList = resourceListingDrawingsFileExtensionList;
	}
}
