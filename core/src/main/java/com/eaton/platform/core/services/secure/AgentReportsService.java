package com.eaton.platform.core.services.secure;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.w3c.dom.Document;

import com.eaton.platform.core.bean.secure.ResponseOracleDM;
import com.google.gson.JsonObject;

/**
 * Interface for AgentReportsService
 */
public interface AgentReportsService {

	/**
	 * get the list of agent id's from user info
	 * @param request  SlingHttpServletRequest
	 * @return agentList Map<String, String>
	 */
	Map<String, String> getAgents(final SlingHttpServletRequest request);

	/**
	 * getReport for the agentId and its division
	 * @param agentnumber  agentnumber
	 * @param division division
	 * @return searchResList ArrayList<ResponseOracleDM>
	 * @throws IOException IOException
	 */
	ArrayList<ResponseOracleDM> getReport(String agentnumber, String division)
			throws IOException;

	/**
	 * getFileDownloaded
	 * @param documentTitle  documentTitle
	 * @return document Document
	 * @throws IOException IOException
	 */
	Document getFileDownloaded(String documentTitle) throws IOException;

}
