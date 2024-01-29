package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class EloquaCloudConfigModel {
	public static final String CONFIG_NAME = "eloquaconfig";

	/** The eloquaserverurl. */
	@Inject @Optional
	private String  eloquaserverurl;

	@Inject @Optional
	private String eloquaSubmitUrl;

	@Inject @Optional
	private String  eloquaCompanyName;
	
	@Inject @Optional
	private String  eloquaEndpointUrl;
	
	@Inject @Optional
	private String  eloquaUsername;
	
	@Inject @Optional
	private String  eloquaPwd;
	
	@Inject @Optional
	private String  eloquaSiteId;
	
	@Inject @Optional
	private String  lookupIdVisitor;
	
	@Inject @Optional
	private String  lookupIdPrimary;
	
	@Inject @Optional
	private String  visitorUniqueField;
	
	@Inject @Optional
	private String  primaryUniqueField;
	
	@Inject @Optional
	private String  cookieTrackingUrl;
	
	@Inject @Optional
	private String  recaptchasitekey;
	
	@Inject @Optional
	private String  recaptchasecret;
	
	@Inject @Optional
	private boolean  eloquaServerStatus;
	
	/**
	 * @return the eloquaServerStatus
	 */
	public boolean getEloquaServerStatus() {
		return eloquaServerStatus;
	}

	/**
	 * @return the eloquaserverurl
	 */
	public String getEloquaserverurl() {
		return eloquaserverurl;
	}

	/**
	 * @return the Eloqua Submit URL
	 */
	public String getEloquaSubmitUrl() {
		return eloquaSubmitUrl;
	}

	/**
	 * @return the eloquaCompanyName
	 */
	public String getEloquaCompanyName() {
		return eloquaCompanyName;
	}

	/**
	 * @return the eloquaEndpointUrl
	 */
	public String getEloquaEndpointUrl() {
		return eloquaEndpointUrl;
	}

	/**
	 * @return the eloquaUsername
	 */
	public String getEloquaUsername() {
		return eloquaUsername;
	}

	/**
	 * @return the eloquaPwd
	 */
	public String getEloquaPwd() {
		return eloquaPwd;
	}

	/**
	 * @return the eloquaSiteId
	 */
	public String getEloquaSiteId() {
		return eloquaSiteId;
	}

	/**
	 * @return the lookupIdVisitor
	 */
	public String getLookupIdVisitor() {
		return lookupIdVisitor;
	}

	/**
	 * @return the lookupIdPrimary
	 */
	public String getLookupIdPrimary() {
		return lookupIdPrimary;
	}

	/**
	 * @return the visitorUniqueField
	 */
	public String getVisitorUniqueField() {
		return visitorUniqueField;
	}

	/**
	 * @return the primaryUniqueField
	 */
	public String getPrimaryUniqueField() {
		return primaryUniqueField;
	}

	/**
	 * @return the cookieTrackingUrl
	 */
	public String getCookieTrackingUrl() {
		return cookieTrackingUrl;
	}

	/**
	 * @return the recaptchasitekey
	 */
	public String getRecaptchasitekey() {
		return recaptchasitekey;
	}

	/**
	 * @return the recaptchasecret
	 */
	public String getRecaptchasecret() {
		return recaptchasecret;
	}
	
	
}
