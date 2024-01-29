package com.eaton.platform.core.services.msm.impl;

/**
 * The Enum ConflictResolution.
 *
 * @author Jaroslav Rassadin
 */
public enum ConflictResolutionStatus {

	/** The fixed. */
	FIXED("Conflict fixed"),

	/** The has children. */
	HAS_CHILDREN("Resource has children"),

	/** The has live relationship. */
	HAS_LIVE_RELATIONSHIP("Resource has live relationship"),

	/** The no blueprint resource. */
	NO_BLUEPRINT_RESOURCE("Blueprint resource not found"),

	/** The not MSM moved resource. */
	NOT_MSM_MOVED_RESOURCE("Not msm moved resource"),

	/** The not possible. */
	NOT_POSSIBLE("Can not enable inheritance."),

	/** The error. */
	ERROR("An error has occurred while enabling inheritance"),

	/** The possible. */
	POSSIBLE("Enabling inheritance is possible");

	private final String description;

	private ConflictResolutionStatus(final String description) {
		this.description = description;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.name();
	}
}
