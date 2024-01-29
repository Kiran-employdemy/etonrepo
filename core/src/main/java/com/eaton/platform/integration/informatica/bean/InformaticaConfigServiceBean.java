package com.eaton.platform.integration.informatica.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * This is a bean class for Informatica Configuration Services
 *
 * @author 499363
 *
 */
public class InformaticaConfigServiceBean implements Serializable {

	private static final long serialVersionUID = -983880181725288673L;

	/** The productfamily file name. */
	private String productfamilyFileName;

	/** The productfamily input file path. */
	private String productfamilyInputFilePath;

	/** The productfamily archive file path. */
	private String productfamilyArchiveFilePath;

	/** The productfamily failed file path. */
	private String productfamilyFailedFilePath;

	/** The productfamily lanuages */
	private List<String> productfamilyLanguages;

	/** The productfamily email ids. */
	private List<String> productfamilyEmailIds;

	/** The global att file name. */
	private String globalAttFileName;

	/** The taxonomy att file name. */
	private String taxonomyAttFileName;

	/** The attribute input file path. */
	private String attributeInputFilePath;

	/** The attribute archive file path. */
	private String attributeArchiveFilePath;

	/** The attribute failed file path. */
	private String attributeFailedFilePath;

	/** The attribute email ids. */
	private List<String> attributeEmailIds;

	/**
	 * @return the productfamilyFileName
	 */
	public String getProductfamilyFileName() {
		return productfamilyFileName;
	}

	/**
	 * @param productfamilyFileName
	 *            the productfamilyFileName to set
	 */
	public void setProductfamilyFileName(String productfamilyFileName) {
		this.productfamilyFileName = productfamilyFileName;
	}

	/**
	 * @return the productfamilyInputFilePath
	 */
	public String getProductfamilyInputFilePath() {
		return productfamilyInputFilePath;
	}

	/**
	 * @param productfamilyInputFilePath
	 *            the productfamilyInputFilePath to set
	 */
	public void
			setProductfamilyInputFilePath(String productfamilyInputFilePath) {
		this.productfamilyInputFilePath = productfamilyInputFilePath;
	}

	/**
	 * @return the productfamilyArchiveFilePath
	 */
	public String getProductfamilyArchiveFilePath() {
		return productfamilyArchiveFilePath;
	}

	/**
	 * @param productfamilyArchiveFilePath
	 *            the productfamilyArchiveFilePath to set
	 */
	public void setProductfamilyArchiveFilePath(
			String productfamilyArchiveFilePath) {
		this.productfamilyArchiveFilePath = productfamilyArchiveFilePath;
	}

	/**
	 * @return the productfamilyFailedFilePath
	 */
	public String getProductfamilyFailedFilePath() {
		return productfamilyFailedFilePath;
	}

	/**
	 * @param productfamilyFailedFilePath
	 *            the productfamilyFailedFilePath to set
	 */
	public void setProductfamilyFailedFilePath(
			String productfamilyFailedFilePath) {
		this.productfamilyFailedFilePath = productfamilyFailedFilePath;
	}

	/**
	 * @return the productfamilyEmailIds
	 */
	public List<String> getProductfamilyEmailIds() {
		return productfamilyEmailIds;
	}

	/**
	 * @param productfamilyEmailIds
	 *            the productfamilyEmailIds to set
	 */
	public void setProductfamilyEmailIds(List<String> productfamilyEmailIds) {
		this.productfamilyEmailIds = productfamilyEmailIds;
	}

	/**
	 * @return the globalAttFileName
	 */
	public String getGlobalAttFileName() {
		return globalAttFileName;
	}

	/**
	 * @param globalAttFileName
	 *            the globalAttFileName to set
	 */
	public void setGlobalAttFileName(String globalAttFileName) {
		this.globalAttFileName = globalAttFileName;
	}

	/**
	 * @return the taxonomyAttFileName
	 */
	public String getTaxonomyAttFileName() {
		return taxonomyAttFileName;
	}

	/**
	 * @param taxonomyAttFileName
	 *            the taxonomyAttFileName to set
	 */
	public void setTaxonomyAttFileName(String taxonomyAttFileName) {
		this.taxonomyAttFileName = taxonomyAttFileName;
	}

	/**
	 * @return the attributeInputFilePath
	 */
	public String getAttributeInputFilePath() {
		return attributeInputFilePath;
	}

	/**
	 * @param attributeInputFilePath
	 *            the attributeInputFilePath to set
	 */
	public void setAttributeInputFilePath(String attributeInputFilePath) {
		this.attributeInputFilePath = attributeInputFilePath;
	}

	/**
	 * @return the attributeArchiveFilePath
	 */
	public String getAttributeArchiveFilePath() {
		return attributeArchiveFilePath;
	}

	/**
	 * @param attributeArchiveFilePath
	 *            the attributeArchiveFilePath to set
	 */
	public void setAttributeArchiveFilePath(String attributeArchiveFilePath) {
		this.attributeArchiveFilePath = attributeArchiveFilePath;
	}

	/**
	 * @return the attributeFailedFilePath
	 */
	public String getAttributeFailedFilePath() {
		return attributeFailedFilePath;
	}

	/**
	 * @param attributeFailedFilePath
	 *            the attributeFailedFilePath to set
	 */
	public void setAttributeFailedFilePath(String attributeFailedFilePath) {
		this.attributeFailedFilePath = attributeFailedFilePath;
	}

	/**
	 * @return the attributeEmailIds
	 */
	public List<String> getAttributeEmailIds() {
		return attributeEmailIds;
	}

	/**
	 * @param attributeEmailIds
	 *            the attributeEmailIds to set
	 */
	public void setAttributeEmailIds(List<String> attributeEmailIds) {
		this.attributeEmailIds = attributeEmailIds;
	}

	/**
	 * @return the productfamilyLanguages
	 */
	public List<String> getProductfamilyLanguages() {
		return productfamilyLanguages;
	}

	/**
	 * @param productfamilyLanguages
	 *            the productfamilyLanguages to set
	 */
	public void setProductfamilyLanguages(List<String> productfamilyLanguages) {
		this.productfamilyLanguages = productfamilyLanguages;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		InformaticaConfigServiceBean that = (InformaticaConfigServiceBean) o;
		return Objects.equals(productfamilyFileName, that.productfamilyFileName) && Objects.equals(productfamilyInputFilePath, that.productfamilyInputFilePath)
				&& Objects.equals(productfamilyArchiveFilePath, that.productfamilyArchiveFilePath)
				&& Objects.equals(productfamilyFailedFilePath, that.productfamilyFailedFilePath) && Objects.equals(productfamilyLanguages, that.productfamilyLanguages)
				&& Objects.equals(productfamilyEmailIds, that.productfamilyEmailIds) && Objects.equals(globalAttFileName, that.globalAttFileName)
				&& Objects.equals(taxonomyAttFileName, that.taxonomyAttFileName) && Objects.equals(attributeInputFilePath, that.attributeInputFilePath)
				&& Objects.equals(attributeArchiveFilePath, that.attributeArchiveFilePath) && Objects.equals(attributeFailedFilePath, that.attributeFailedFilePath)
				&& Objects.equals(attributeEmailIds, that.attributeEmailIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(productfamilyFileName, productfamilyInputFilePath, productfamilyArchiveFilePath
				, productfamilyFailedFilePath, productfamilyLanguages, productfamilyEmailIds, globalAttFileName
				, taxonomyAttFileName, attributeInputFilePath, attributeArchiveFilePath, attributeFailedFilePath, attributeEmailIds);
	}

	@Override
	public String toString() {
		return "InformaticaConfigServiceBean{" +
				"productfamilyFileName='" + productfamilyFileName + '\'' +
				", productfamilyInputFilePath='" + productfamilyInputFilePath + '\'' +
				", productfamilyArchiveFilePath='" + productfamilyArchiveFilePath + '\'' +
				", productfamilyFailedFilePath='" + productfamilyFailedFilePath + '\'' +
				", productfamilyLanguages=" + productfamilyLanguages +
				", productfamilyEmailIds=" + productfamilyEmailIds +
				", globalAttFileName='" + globalAttFileName + '\'' +
				", taxonomyAttFileName='" + taxonomyAttFileName + '\'' +
				", attributeInputFilePath='" + attributeInputFilePath + '\'' +
				", attributeArchiveFilePath='" + attributeArchiveFilePath + '\'' +
				", attributeFailedFilePath='" + attributeFailedFilePath + '\'' +
				", attributeEmailIds=" + attributeEmailIds +
				'}';
	}
}
