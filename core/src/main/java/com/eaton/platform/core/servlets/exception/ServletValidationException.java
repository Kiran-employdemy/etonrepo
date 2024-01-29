package com.eaton.platform.core.servlets.exception;

import java.util.Arrays;

import org.apache.sling.api.SlingHttpServletResponse;

import com.eaton.platform.core.models.error.ValidationErrorModel;

/**
 * The Class ServletValidationException.
 *
 * @author Jaroslav Rassadin
 */

public class ServletValidationException extends Exception {

	private static final long serialVersionUID = 2704810070646139119L;

	private final SlingHttpServletResponse response;

	private final ValidationErrorModel[] errors;

	/**
	 * Instantiates a new servlet validation exception.
	 *
	 * @param response
	 *            the response
	 * @param errors
	 *            the errors
	 */
	public ServletValidationException(final SlingHttpServletResponse response, final ValidationErrorModel... errors) {
		super("An validation error has occurred.");

		this.response = response;
		this.errors = errors;
	}

	/**
	 * Instantiates a new servlet validation exception.
	 *
	 * @param message
	 *            the message
	 * @param response
	 *            the response
	 * @param errors
	 *            the errors
	 */
	public ServletValidationException(final String message, final SlingHttpServletResponse response, final ValidationErrorModel... errors) {
		super(message);

		this.response = response;
		this.errors = errors;
	}

	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public SlingHttpServletResponse getResponse() {
		return this.response;
	}

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	public ValidationErrorModel[] getErrors() {
		return this.errors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ServletValidationException [errors=" + Arrays.toString(this.errors) + ", getMessage()=" + this.getMessage() + "]";
	}

}
