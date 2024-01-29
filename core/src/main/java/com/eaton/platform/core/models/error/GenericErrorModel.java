package com.eaton.platform.core.models.error;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Class GenericErrorModel.
 *
 * @author Jaroslav Rassadin
 */
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({
		@Type(
				value = ValidationErrorModel.class,
				name = "validation")
})
public class GenericErrorModel implements Serializable {

	private static final long serialVersionUID = 9063436167971385738L;

	private static final String DEFAULT_TYPE = "default";

	private String id;

	private int code;

	private String title;

	private String type;

	private String description;

	/**
	 * Instantiates a new generic error model.
	 */
	public GenericErrorModel() {
		this.type = DEFAULT_TYPE;
	}

	/**
	 * Instantiates a new generic error model.
	 *
	 * @param type
	 *            the type
	 */
	public GenericErrorModel(final String description) {
		this();
		this.description = description;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the id to set
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code
	 *            the code to set
	 */
	public void setCode(final int code) {
		this.code = code;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title
	 *            the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the type to set
	 */
	protected void setType(final String type) {
		this.type = type;
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
	 * Sets the description.
	 *
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.code, this.description, this.id, this.title, this.type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final GenericErrorModel other = (GenericErrorModel) obj;
		return (this.code == other.code) && Objects.equals(this.description, other.description) && Objects.equals(this.id, other.id)
				&& Objects.equals(this.title, other.title)
				&& Objects.equals(this.type, other.type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "GenericErrorModel [id=" + this.id + ", code=" + this.code + ", title=" + this.title + ", type=" + this.type + ", description="
				+ this.description + "]";
	}

}
