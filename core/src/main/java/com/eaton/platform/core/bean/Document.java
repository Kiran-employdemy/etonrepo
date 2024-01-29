package com.eaton.platform.core.bean;

public class Document {
	
	private String documentName;
	private String documentId;
	private String documentLink;
	private String documentType;
	private String documentOpenNewWindow;
	private Boolean documentIsNew;
	private String documentSize;
	private String documentDate;

	/**
	 * @return the documentName
	 */
	public String getDocumentName() {
		return documentName;
	}
	/**
	 * @param documentName the documentName to set
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	/**
	 * @return the documentId
	 */
	public String getDocumentId() {
		return documentId;
	}
	/**
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	/**
	 * @return the documentLink
	 */
	public String getDocumentLink() {
		return documentLink;
	}
	/**
	 * @param documentLink the documentLink to set
	 */
	public void setDocumentLink(String documentLink) {
		this.documentLink = documentLink;
	}
	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}
	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	/**
	 * @return the documentOpenNewWindow
	 */
	public String getDocumentOpenNewWindow() {
		return documentOpenNewWindow;
	}
	/**
	 * @param documentOpenNewWindow the documentOpenNewWindow to set
	 */
	public void setDocumentOpenNewWindow(String documentOpenNewWindow) {
		this.documentOpenNewWindow = documentOpenNewWindow;
	}
	/**
	 * @return the documentIsNew
	 */
	public Boolean getDocumentIsNew() {
		return documentIsNew;
	}
	/**
	 * @param documentIsNew the documentIsNew to set
	 */
	public void setDocumentIsNew(Boolean documentIsNew) {
		this.documentIsNew = documentIsNew;
	}
	/**
	 * @return the documentSize
	 */
	public String getDocumentSize() {
		return documentSize;
	}
	/**
	 * @param documentSize the documentSize to set
	 */
	public void setDocumentSize(String documentSize) {
		this.documentSize = documentSize;
	}
	/**
	 * @return the documentDate
	 */
	public String getDocumentDate() {
		return documentDate;
	}
	/**
	 * @param documentDate the documentDate to set
	 */
	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}
}
