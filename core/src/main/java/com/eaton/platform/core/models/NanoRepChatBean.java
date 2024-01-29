package com.eaton.platform.core.models;


import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import com.google.gson.JsonArray;
import org.apache.sling.models.annotations.Model;

/**
 * This class is for ChatConfig Chat Views Bean.
 * @author TCS
 *
 */
@Model(adaptables = Resource.class)
public class NanoRepChatBean {

	/** The business division. */
	@Inject  @Named("nrbusinessdivision")
	private String businessdivision;
	
	
	/** The Entry Point ID. */
	@Inject  @Named("nrentrypointid")
	private String kbNumber;
	
	/** The AEM Tags. */
	@Inject  @Named("nraemtags")
	private JsonArray aemtags;
	
	/** The Error Messages. */
	@Inject  @Named("nrerrmsgs")
	private String errmsgs;

	public String getBusinessdivision() {
		return businessdivision;
	}

	public void setBusinessdivision(String businessdivision) {
		this.businessdivision = businessdivision;
	}

	/**
	 * @return the kbNumber
	 */
	public String getKbNumber() {
		return kbNumber;
	}

	/**
	 * @param kbNumber the kbNumber to set
	 */
	public void setKbNumber(String kbNumber) {
		this.kbNumber = kbNumber;
	}

	/**
	 * @param errmsgs the errmsgs to set
	 */
	public void setErrmsgs(String errmsgs) {
		this.errmsgs = errmsgs;
	}

	/**
	 * @return the errmsgs
	 */
	public String getErrmsgs() {
		return errmsgs;
	}

	public JsonArray getAemtags() {
		return aemtags;
	}

	public void setAemtags(JsonArray jsonArray) {
		this.aemtags = jsonArray;
	}

	
	
}
