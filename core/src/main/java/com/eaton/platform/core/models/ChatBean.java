package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import com.google.gson.JsonArray;
import org.apache.sling.models.annotations.Model;

/**
 * This class is for ChatConfig Chat Views Bean.
 * @author TCS
 *
 */
@Model(adaptables = Resource.class)
public class ChatBean {

	/** The business division. */
	@Inject
	private String businessdivision;
	
	/** The URL To chat. */
	@Inject
	private String urltochat;
	
	/** The URL o Agent. */
	@Inject
	private String urltoagent;
	
	/** The Online Content. */
	@Inject
	private String onlinecontent;
	
	/** The Offline Content. */
	@Inject
	private String offlinecontent;
	
	/** The Entry Point ID. */
	@Inject
	private String entrypointid;
	
	/** The AEM Tags. */
	@Inject
	private JsonArray aemtags;
	
	/** The Error Messages. */
	@Inject
	private String errmsgs;

	public String getBusinessdivision() {
		return businessdivision;
	}

	public void setBusinessdivision(String businessdivision) {
		this.businessdivision = businessdivision;
	}

	public String getUrltochat() {
		return urltochat;
	}

	public void setUrltochat(String urltochat) {
		this.urltochat = urltochat;
	}

	public String getUrltoagent() {
		return urltoagent;
	}

	public void setUrltoagent(String urltoagent) {
		this.urltoagent = urltoagent;
	}

	public String getOnlinecontent() {
		return onlinecontent;
	}

	public void setOnlinecontent(String onlinecontent) {
		this.onlinecontent = onlinecontent;
	}

	public String getOfflinecontent() {
		return offlinecontent;
	}

	public void setOfflinecontent(String offlinecontent) {
		this.offlinecontent = offlinecontent;
	}

	public String getEntrypointid() {
		return entrypointid;
	}

	public void setEntrypointid(String entrypointid) {
		this.entrypointid = entrypointid;
	}

	public JsonArray getAemtags() {
		return aemtags;
	}

	public void setAemtags(JsonArray jsonArray) {
		this.aemtags = jsonArray;
	}

	public String getErrMsgs() {
		return errmsgs;
	}

	public void setErrMsgs(String errmsgs) {
		this.errmsgs = errmsgs;
	}
	
}
