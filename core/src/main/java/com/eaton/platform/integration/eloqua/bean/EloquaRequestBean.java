package com.eaton.platform.integration.eloqua.bean;

import java.io.Serializable;

public class EloquaRequestBean implements Serializable {
	
	private static final long serialVersionUID = -7316005083888601077L;
	
	/** The form id. */
	private String formId;
	
	/** The eloquaserverurl. */
	private String eloquaserverurl;
	
	/** The eloqua company name. */
	private String eloquaCompanyName;
	
	/** The eloqua endpoint url. */
	private String eloquaEndpointUrl;
	
	/** The eloqua username. */
	private String eloquaUsername;
	
	/** The eloqua pwd. */
	private String eloquaPwd;

	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * @return the eloquaserverurl
	 */
	public String getEloquaserverurl() {
		return eloquaserverurl;
	}

	/**
	 * @param eloquaserverurl the eloquaserverurl to set
	 */
	public void setEloquaserverurl(String eloquaserverurl) {
		this.eloquaserverurl = eloquaserverurl;
	}

	/**
	 * @return the eloquaCompanyName
	 */
	public String getEloquaCompanyName() {
		return eloquaCompanyName;
	}

	/**
	 * @param eloquaCompanyName the eloquaCompanyName to set
	 */
	public void setEloquaCompanyName(String eloquaCompanyName) {
		this.eloquaCompanyName = eloquaCompanyName;
	}

	/**
	 * @return the eloquaEndpointUrl
	 */
	public String getEloquaEndpointUrl() {
		return eloquaEndpointUrl;
	}

	/**
	 * @param eloquaEndpointUrl the eloquaEndpointUrl to set
	 */
	public void setEloquaEndpointUrl(String eloquaEndpointUrl) {
		this.eloquaEndpointUrl = eloquaEndpointUrl;
	}

	/**
	 * @return the eloquaUsername
	 */
	public String getEloquaUsername() {
		return eloquaUsername;
	}

	/**
	 * @param eloquaUsername the eloquaUsername to set
	 */
	public void setEloquaUsername(String eloquaUsername) {
		this.eloquaUsername = eloquaUsername;
	}

	/**
	 * @return the eloquaPwd
	 */
	public String getEloquaPwd() {
		return eloquaPwd;
	}

	/**
	 * @param eloquaPwd the eloquaPwd to set
	 */
	public void setEloquaPwd(String eloquaPwd) {
		this.eloquaPwd = eloquaPwd;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
