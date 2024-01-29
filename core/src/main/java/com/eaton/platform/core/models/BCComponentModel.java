package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

/**
 * The Class BCComponentModel.
 */
@Model(adaptables = Resource.class)
public class BCComponentModel   {
	
	/** contentType. */
	@Inject
	private String  contentType;

	/** contentId. */
	@Inject
	private String contentId;
	
	
	/** brightcovePlayerID. */
	@Inject
	private String brightcovePlayerID;
	
	/** headLine. */
	@Inject @Optional
	private String headLine;
	
	/** description. */
	@Inject @Optional
	private String description;
	
	/** toggleInnerGrid. */
	@Inject @Optional
	private String toggleInnerGrid;
	
		
	/**
	 * @return the toggleInnerGrid
	 */
	public String getToggleInnerGrid() {
		return toggleInnerGrid;
	}

	/**
	 * @return the headLine
	 */
	public String getHeadLine() {
		return headLine;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}



	/**
	 * Gets the content type.
	 *
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Gets the content id.
	 *
	 * @return the contentId
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * Sets the content id.
	 *
	 * @param contentId the contentId to set
	 */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}


	/**
	 * Gets the brightcove player ID.
	 *
	 * @return the brightcovePlayerID
	 */
	public String getBrightcovePlayerID() {
		return brightcovePlayerID;
	}

	/**
	 * Sets the brightcove player ID.
	 *
	 * @param brightcovePlayerID the brightcovePlayerID to set
	 */
	public void setBrightcovePlayerID(String brightcovePlayerID) {
		this.brightcovePlayerID = brightcovePlayerID;
	}
	
	
	
}
