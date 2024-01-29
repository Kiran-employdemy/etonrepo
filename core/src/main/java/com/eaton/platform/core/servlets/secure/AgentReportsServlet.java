package com.eaton.platform.core.servlets.secure;

import java.util.ArrayList;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.bean.secure.ResponseOracleDM;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.secure.AgentReportsService;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.google.gson.Gson;

@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "= Get reports by agent number & division",
		ServletConstants.SLING_SERVLET_PATHS + "/eaton/agentreports/getReports", ServletConstants.SLING_SERVLET_METHODS_POST })
public class AgentReportsServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(AgentReportsServlet.class);

	@Reference
	private AgentReportsService agentReportsService;
	ArrayList<ResponseOracleDM> advSearResList = new ArrayList<>();

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException {
		LOG.debug("Started AgentReportsServlet.doPost");
		try {
			String agentId = request.getParameter(SecureConstants.AGENT_ID);
			String division = request.getParameter(SecureConstants.DIVISION);
			LOG.info("AgentReportsServlet.doPost :: agentId: {}" , agentId );
			LOG.info("AgentReportsServlet.doPost :: division : {}" , division );
			advSearResList = agentReportsService.getReport(agentId, division);
			response.setContentType(CommonConstants.APPLICATION_JSON);
			response.getWriter().print(new Gson().toJson(advSearResList));
			response.getWriter().flush();
		} catch (Exception e) {
			LOG.error("Exception while fetching agent reports: {}", e.getMessage());
		}
		LOG.debug("Finished AgentReportsServlet.doPost");
	}
}
