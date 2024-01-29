package com.eaton.platform.core.servlets.secure;

import java.util.ArrayList;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.eaton.platform.core.bean.secure.ResponseOracleDM;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.secure.AgentReportsService;
import com.eaton.platform.integration.auth.constants.SecureConstants;

@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "= Download file by using docName",
		ServletConstants.SLING_SERVLET_PATHS + "/eaton/agentreports/getFileDownload", ServletConstants.SLING_SERVLET_METHODS_POST })
public class AgentReportsFileDowloadServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(AgentReportsFileDowloadServlet.class);

	@Reference
	private AgentReportsService agentReportsService;

	ArrayList<ResponseOracleDM> advSearResList = new ArrayList<>();

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException {
		LOG.debug("Started AgentReportsFileDowloadServlet.doPost");
		try {
			ServletOutputStream outputStream = response.getOutputStream();
			String docName = request.getParameter(SecureConstants.DOC_NAME);
			LOG.info("AgentReportsFileDowloadServlet.doPost :: docName: {}" , docName );
			Document document = agentReportsService.getFileDownloaded(docName);
			if (document != null) {
				NodeList fileName = document.getElementsByTagName(SecureConstants.FILE_NAME);
				NodeList fileContent = document.getElementsByTagName(SecureConstants.FILE_CONTENT);
				byte[] decodedBytes = Base64.decodeBase64(fileContent.item(0).getTextContent());
				response.setContentType(CommonConstants.APPLICATION_OCTET_STREAM);
				response.setHeader(CommonConstants.CONTENT_DISPOSITION,
						"attachment;filename=" + fileName.item(0).getTextContent());
				outputStream.write(decodedBytes);
				outputStream.flush();
				outputStream.close();
			}
		} catch (Exception e) {
			LOG.error("Exception while downloading file: {}", e.getMessage());
		}
		LOG.debug("Finished AgentReportsFileDowloadServlet.doPost");
	}

}