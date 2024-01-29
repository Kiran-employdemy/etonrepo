package com.eaton.platform.core.services.msm;

/**
 * The Class RolloutConflictException.
 *
 * @author Jaroslav Rassadin
 */
public class RolloutConflictException extends RuntimeException {

	private static final long serialVersionUID = 595421442624231421L;

	/**
	 * Instantiates a new rollout conflict exception.
	 *
	 * @param message
	 *            the message
	 */
	public RolloutConflictException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new rollout conflict exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public RolloutConflictException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new rollout conflict exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public RolloutConflictException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
