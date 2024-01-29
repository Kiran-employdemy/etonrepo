package com.eaton.platform.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Resource.class)
public class EloquaFormModel   {
	public static final String PROP_ADD_CAPTCHA = "addCaptcha";

	/** The form id. */
	@Inject
	private String  formId;
	
	/** The add captcha. */
	@Inject @Optional
	private String addCaptcha;
	
	/** The prepopulate. */
	@Inject @Optional
	private String prepopulate;
	
	/** The disclaimer. */
	@Inject @Optional
	private String disclaimer;
	
	/** The country. */
	@Inject @Optional
	private String tag_country;
	
	/** The market. */
	@Inject @Optional
	private String tag_market;
	
	/** The topic. */
	@Inject @Optional
	private String tag_topic;
	
	/** The location. */
	@Inject @Optional
	private String tag_location;
	
	/** The cta. */
	@Inject @Optional
	private String tag_cta;
	
	/** The stage. */
	@Inject @Optional
	private String tag_stage;
	
	/** The product. */
	@Inject @Optional
	private String tag_product;
	
	/** The business grp. */
	@Inject @Optional
	private String tag_businessGrp;
	
	/** The asset. */
	@Inject @Optional
	private String tag_asset;

	/** The percolate Asset ID. */
	@Inject @Optional
	private String tag_percolateAssetId;

	/** The page reqd. */
	@Inject @Optional
	private String pageReqd;

	/** The redirectUrl. */
	@Inject @Optional
	private String redirectUrl;

	/** The toggleInnerGrid. */
	@Inject @Optional
	private String toggleInnerGrid;

	@Inject
	@Named("disclaimerlist")
	@Optional
	public Resource disclaimerlist;
		
	@Inject @Optional
	private String defaultmarketdisclaimer;
	

	public String getDefaultmarketdisclaimer() {
		return defaultmarketdisclaimer;
	}

	public void setDefaultmarketdisclaimer(String defaultmarketdisclaimer) {
		this.defaultmarketdisclaimer = defaultmarketdisclaimer;
	}

	@Inject @Optional
	private String privacypageurl;


	public String getPrivacypageurl() {
		return privacypageurl;
	}

	public void setPrivacypageurl(String privacypageurl) {
		this.privacypageurl = privacypageurl;
	}

	/**
	 * @return the tag_percolate
	 */
	public String getTag_percolateAssetId() {
		return tag_percolateAssetId;
	}

	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @return the addCaptcha
	 */
	public String getAddCaptcha() {
		return addCaptcha;
	}

	/**
	 * @return the prepopulate
	 */
	public String getPrepopulate() {
		return prepopulate;
	}

	/**
	 * @return the disclaimer
	 */
	public String getDisclaimer() {
		return disclaimer;
	}

	/**
	 * @return the tag_country
	 */
	public String getTag_country() {
		return tag_country;
	}

	/**
	 * @return the tag_market
	 */
	public String getTag_market() {
		return tag_market;
	}

	/**
	 * @return the tag_topic
	 */
	public String getTag_topic() {
		return tag_topic;
	}

	/**
	 * @return the tag_location
	 */
	public String getTag_location() {
		return tag_location;
	}

	/**
	 * @return the tag_cta
	 */
	public String getTag_cta() {
		return tag_cta;
	}

	/**
	 * @return the tag_stage
	 */
	public String getTag_stage() {
		return tag_stage;
	}

	/**
	 * @return the tag_product
	 */
	public String getTag_product() {
		return tag_product;
	}

	/**
	 * @return the tag_businessGrp
	 */
	public String getTag_businessGrp() {
		return tag_businessGrp;
	}

	/**
	 * @return the tag_asset
	 */
	public String getTag_asset() {
		return tag_asset;
	}
	
	/**
	 * @return the pageReqd
	 */
	public String getPageReqd() {
		return pageReqd;
	}

	public String getToggleInnerGrid() {
		return toggleInnerGrid;
	}

	public void setToggleInnerGrid(String toggleInnerGrid) {
		this.toggleInnerGrid = toggleInnerGrid;
	}

	// To override the value of the property (Niranjan).
	public final void setTag_product(String tag_product) {
		this.tag_product = tag_product;
	}

	public Resource getDisclaimerlist() {
		return disclaimerlist;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
