package com.eaton.platform.core.services.search.find.replace.exception;

/**
 * The Class FindReplaceBackupException.
 *
 * @author Jaroslav Rassadin
 */
public class FindReplaceBackupException extends FindReplaceException {

	private static final long serialVersionUID = 3096557385338014676L;

	/**
	 * Instantiates a new find replace backup exception.
	 *
	 * @param message
	 *            the message
	 */
	public FindReplaceBackupException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new find replace backup exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public FindReplaceBackupException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new find replace backup exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public FindReplaceBackupException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
