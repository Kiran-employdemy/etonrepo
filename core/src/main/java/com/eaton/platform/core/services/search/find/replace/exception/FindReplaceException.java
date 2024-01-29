package com.eaton.platform.core.services.search.find.replace.exception;

/**
 * The Class FindReplaceModificationException.
 *
 * @author Jaroslav Rassadin
 */
public class FindReplaceException extends RuntimeException {

	private static final long serialVersionUID = 3296515537576918123L;

	/**
	 * Instantiates a new find replace exception.
	 *
	 * @param message
	 *            the message
	 */
	public FindReplaceException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new find replace exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public FindReplaceException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new find replace exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public FindReplaceException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
