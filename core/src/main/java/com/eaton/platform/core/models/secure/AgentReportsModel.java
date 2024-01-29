package com.eaton.platform.core.models.secure;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.services.secure.AgentReportsService;

/**
 * This class is used to inject the dialog properties
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AgentReportsModel {

	private static final Logger LOG = LoggerFactory.getLogger(AgentReportsModel.class);

	@Inject
	@Via("resource")
	@Default(values="Agent")
	private String agentLabel;
	
	@Inject
	@Via("resource")
	@Default(values="Division")
	private String divisionLabel;
	
	@Inject
	@Via("resource")
	@Default(values="Report name")
	private String reportNameLabel;
	
	@Inject
	@Via("resource")
	@Default(values="Date")
	private String dateLabel;
	
	@Inject
	@Via("resource")
	private String description;

	@Inject
	private AgentReportsService agentReportsService;

	@Inject
	private SlingHttpServletRequest request;

	private Map<String, String> agentList = new HashMap<>();

	@PostConstruct
	protected void init() {
		LOG.debug("AgentReportsModel Init Started");
        agentList = agentReportsService.getAgents(request);
		LOG.debug("agentList returned to AgentReportsModel: {}", agentList);
		LOG.debug("AgentReportsModel Init Ended");
	}

	public Map<String, String> getAgentList() {
		return agentList;
	}

	public String getAgentLabel() {
		return agentLabel;
	}

	public String getDivisionLabel() {
		return divisionLabel;
	}

	public String getReportNameLabel() {
		return reportNameLabel;
	}

	public String getDateLabel() {
		return dateLabel;
	}

	public String getDescription() {
		return description;
	}

}
