package com.eaton.platform.core.models.search.find.replace.ui;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The Class FindReplaceTagsHelpModel.
 *
 * @author Jaroslav Rassadin
 */
public class FindReplaceTagsHelpModel {

	private boolean multiple;

	private boolean ownProperty;

	private String tagId;

	private Map<String, List<String>> types;

	/**
	 * Instantiates a new find replace tags help model.
	 */
	public FindReplaceTagsHelpModel() {
	}

	/**
	 * Instantiates a new find replace tags help model.
	 *
	 * @param multiple
	 *            the multiple
	 * @param ownProperty
	 *            the own property
	 * @param tagId
	 *            the tag id
	 * @param types
	 *            the types
	 */
	public FindReplaceTagsHelpModel(final boolean multiple, final boolean ownProperty, final String tagId, final Map<String, List<String>> types) {
		this.multiple = multiple;
		this.ownProperty = ownProperty;
		this.tagId = tagId;
		this.types = types;
	}

	/**
	 * Checks if is multiple.
	 *
	 * @return the multiple
	 */
	public boolean isMultiple() {
		return this.multiple;
	}

	/**
	 * Sets the multiple.
	 *
	 * @param multiple
	 *            the multiple to set
	 */
	public void setMultiple(final boolean multiple) {
		this.multiple = multiple;
	}

	/**
	 * Checks if is own property.
	 *
	 * @return the ownProperty
	 */
	public boolean isOwnProperty() {
		return this.ownProperty;
	}

	/**
	 * Sets the own property.
	 *
	 * @param ownProperty
	 *            the ownProperty to set
	 */
	public void setOwnProperty(final boolean ownProperty) {
		this.ownProperty = ownProperty;
	}

	/**
	 * Gets the tag id.
	 *
	 * @return the tagId
	 */
	public String getTagId() {
		return this.tagId;
	}

	/**
	 * Sets the tag id.
	 *
	 * @param tagId
	 *            the tagId to set
	 */
	public void setTagId(final String tagId) {
		this.tagId = tagId;
	}

	/**
	 * Gets the types.
	 *
	 * @return the types
	 */
	public Map<String, List<String>> getTypes() {
		return this.types;
	}

	/**
	 * Sets the types.
	 *
	 * @param types
	 *            the types to set
	 */
	public void setTypes(final Map<String, List<String>> types) {
		this.types = types;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.multiple, this.ownProperty, this.tagId, this.types);
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
		final FindReplaceTagsHelpModel other = (FindReplaceTagsHelpModel) obj;
		return (this.multiple == other.multiple) && (this.ownProperty == other.ownProperty) && Objects.equals(this.tagId, other.tagId) && Objects.equals(
				this.types, other.types);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "FindReplaceTagsHelpModel [multiple=" + this.multiple + ", ownProperty=" + this.ownProperty + ", tagId=" + this.tagId + ", types=" + this.types
				+ "]";
	}

}
