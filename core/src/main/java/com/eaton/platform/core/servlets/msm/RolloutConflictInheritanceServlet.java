package com.eaton.platform.core.servlets.msm;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.NameConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.msm.RolloutConflictInheritanceService;
import com.eaton.platform.core.services.msm.impl.ConflictResolutionResult;
import com.eaton.platform.core.services.msm.impl.ConflictResolutionStatus;
import com.eaton.platform.core.servlets.ServletHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class RolloutConflictInheritanceServlet.
 *
 * Endpoint for inheritance enabling functionality. To be called from component edit toolbar and page properties.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = Servlet.class,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = { ServletConstants.SLING_SERVLET_METHODS_POST, //
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + ServletConstants.SLING_SERVLET_DEFAULT_RESOURCE_TYPE, //
				ServletConstants.SLING_SERVLET_SELECTORS + "eaton.msm.enable", //
				ServletConstants.SLING_SERVLET_EXTENSION_JSON })
public class RolloutConflictInheritanceServlet extends SlingAllMethodsServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(RolloutConflictInheritanceServlet.class);

	private static final long serialVersionUID = -3653899175437970727L;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Reference
	private transient RolloutConflictInheritanceService inheritanceService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
		try {
			ServletHelper.setJsonContentType(response);

			final String initialPath = request.getResource().getPath();
			ConflictResolutionResult result = null;

			if (NameConstants.NT_PAGE.equals(request.getResource().getResourceType())) {
				result = this.inheritanceService.enableInheritanceForPage(initialPath, true);

			} else {
				result = this.inheritanceService.enableInheritanceForResource(initialPath, true);
			}
			if (ConflictResolutionStatus.FIXED.equals(result.getStatus())) {
				response.getWriter().write(new ObjectMapper().writer().writeValueAsString(result));

			} else {
				response.getWriter().write(OBJECT_MAPPER.writer().writeValueAsString(result));
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (final Exception ex) {
			final String msg = "An error has occurred while enabling inheritance.";
			LOGGER.error(msg, ex);

			response.getWriter().write(OBJECT_MAPPER.writer().writeValueAsString(Map.of("msg", msg)));
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);

		} finally {
			response.flushBuffer();
		}
	}

}
