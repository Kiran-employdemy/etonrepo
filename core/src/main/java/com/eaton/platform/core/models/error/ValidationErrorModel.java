package com.eaton.platform.core.models.error;

import java.io.Serializable;
import java.util.Objects;

/**
 * The Class ValidationErrorModel.
 *
 * @author Jaroslav Rassadin
 */
public class ValidationErrorModel extends GenericErrorModel implements Serializable {

	private static final long serialVersionUID = 5181382782857491159L;

	/** The Constant TYPE. */
	public static final String TYPE = "validation";

	private String property;

	/**
	 * Instantiates a new validation error model.
	 */
	public ValidationErrorModel() {
		this.setType(TYPE);
	}

	/**
	 * Instantiates a new validation error model.
	 *
	 * @param property
	 *            the property
	 * @param description
	 *            the description
	 */
	public ValidationErrorModel(final String property, final String description) {
		this();

		this.setDescription(description);
		this.property = property;
	}

	/**
	 * Gets the property.
	 *
	 * @return the property
	 */
	public String getProperty() {
		return this.property;
	}

	/**
	 * Sets the property.
	 *
	 * @param property
	 *            the property to set
	 */
	public void setProperty(final String property) {
		this.property = property;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + Objects.hash(this.property);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final ValidationErrorModel other = (ValidationErrorModel) obj;
		return Objects.equals(this.property, other.property);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ValidationErrorModel [property=" + this.property + ", getId()=" + this.getId() + ", getCode()=" + this.getCode() + ", getTitle()="
				+ this.getTitle()
				+ ", getType()=" + this.getType() + ", getDescription()=" + this.getDescription() + "]";
	}

}
