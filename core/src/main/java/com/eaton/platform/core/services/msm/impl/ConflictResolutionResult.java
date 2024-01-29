package com.eaton.platform.core.services.msm.impl;

/**
 * The Class ConflictResolutionResult.
 *
 * @author Jaroslav Rassadin
 */
public class ConflictResolutionResult {

	private String initialPath;

	private String newPath;

	private ConflictResolutionStatus status;

	/**
	 * Instantiates a new conflict resolution result.
	 */
	public ConflictResolutionResult() {
	}

	/**
	 * Instantiates a new conflict resolution result.
	 *
	 * @param initialPath
	 *            the initial path
	 * @param newPath
	 *            the new path
	 * @param status
	 *            the status
	 */
	public ConflictResolutionResult(final String initialPath, final String newPath, final ConflictResolutionStatus status) {
		this.initialPath = initialPath;
		this.newPath = newPath;
		this.status = status;
	}

	/**
	 * Gets the initial path.
	 *
	 * @return the initialPath
	 */
	public String getInitialPath() {
		return this.initialPath;
	}

	/**
	 * Sets the initial path.
	 *
	 * @param initialPath
	 *            the initialPath to set
	 */
	public void setInitialPath(final String initialPath) {
		this.initialPath = initialPath;
	}

	/**
	 * Gets the new path.
	 *
	 * @return the newPath
	 */
	public String getNewPath() {
		return this.newPath;
	}

	/**
	 * Sets the new path.
	 *
	 * @param newPath
	 *            the newPath to set
	 */
	public void setNewPath(final String newPath) {
		this.newPath = newPath;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public ConflictResolutionStatus getStatus() {
		return this.status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final ConflictResolutionStatus status) {
		this.status = status;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMsg() {
		return this.status != null ? this.status.getDescription() : "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ConflictResolutionResult [initialPath=" + this.initialPath + ", newPath=" + this.newPath + ", status=" + this.status + "]";
	}

}
