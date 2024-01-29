package com.eaton.platform.core.services.packaging;

/**
 * The Class PackageCreationException.
 *
 * @author Jaroslav Rassadin
 */
public class PackageCreationException extends RuntimeException {

	private static final long serialVersionUID = -5008846466945924922L;

	/**
	 * Instantiates a new package creation exception.
	 *
	 * @param message
	 *            the message
	 */
	public PackageCreationException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new package creation exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public PackageCreationException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new package creation exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public PackageCreationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
