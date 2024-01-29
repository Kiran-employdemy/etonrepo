package com.eaton.platform.core.services.search.find.replace.exception;

/**
 * The Class FindReplaceModificationException.
 *
 * @author Jaroslav Rassadin
 */
public class FindReplaceModificationException extends FindReplaceException {

	private static final long serialVersionUID = 2238429389760575355L;

	/**
	 * Instantiates a new find replace modification exception.
	 *
	 * @param message
	 *            the message
	 */
	public FindReplaceModificationException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new find replace modification exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public FindReplaceModificationException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new find replace modification exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public FindReplaceModificationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
