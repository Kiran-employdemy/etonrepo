package com.eaton.platform.core.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.error.GenericErrorModel;
import com.eaton.platform.core.models.error.GenericErrorsModel;
import com.eaton.platform.core.servlets.exception.ServletValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ServletHelper.
 *
 * @author Jaroslav Rassadin
 */
public final class ServletHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServletHelper.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * The Interface ValidationExecutor.
	 */
	@FunctionalInterface
	public interface ValidationExecutor {

		/**
		 * Apply.
		 *
		 * @return true, if successful
		 * @throws ServletValidationException
		 *             the servlet validation exception
		 */
		boolean apply() throws ServletValidationException;
	}

	private ServletHelper() {

	}

	/**
	 * Sets the no cache headers.
	 *
	 * @param response
	 *            the new no cache headers
	 */
	public static void setNoCacheHeaders(final HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Dispatcher", "no-cache");
	}

	/**
	 * Sets the json content type.
	 *
	 * @param response
	 *            the new json content type
	 */
	public static void setJsonContentType(final ServletResponse response) {
		response.setCharacterEncoding(CommonConstants.UTF_8);
		response.setContentType(CommonConstants.APPLICATION_JSON);
	}

	/**
	 * Send json error.
	 *
	 * @param response
	 *            the response
	 * @param code
	 *            the code
	 * @param message
	 *            the message
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void sendJsonError(final HttpServletResponse response, final int code, final String message) throws IOException {

		response.getWriter().write(OBJECT_MAPPER.writer().forType(GenericErrorsModel.class)
				.writeValueAsString(new GenericErrorsModel(Collections.singletonList(new GenericErrorModel(message)))));

		response.setStatus(code);
	}

	/**
	 * Validate.
	 *
	 * @param executor
	 *            the executor
	 * @return true, if valid
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static boolean validate(final ValidationExecutor executor) throws IOException {
		try {
			return executor.apply();

		} catch (final ServletValidationException vex) {
			LOGGER.error(vex.toString(), vex);

			vex.getResponse().getWriter().write(OBJECT_MAPPER.writer().forType(GenericErrorsModel.class)
					.writeValueAsString(new GenericErrorsModel(Arrays.asList(vex.getErrors()))));
			vex.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);

			return false;
		}
	}

}
