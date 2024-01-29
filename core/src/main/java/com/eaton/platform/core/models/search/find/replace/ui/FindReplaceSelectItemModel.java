package com.eaton.platform.core.models.search.find.replace.ui;

import java.util.Map;
import java.util.Objects;

/**
 * The Class FindReplaceSelectItemModel.
 *
 * @author Jaroslav Rassadin
 */
public class FindReplaceSelectItemModel {

	private Map<String, Object> attributes;

	private boolean selected;

	private String title;

	private String value;

	/**
	 * Instantiates a new find replace select item model.
	 */
	public FindReplaceSelectItemModel() {

	}

	/**
	 * Instantiates a new find replace select item model.
	 *
	 * @param attributes
	 *            the attributes
	 * @param title
	 *            the title
	 * @param value
	 *            the value
	 */
	public FindReplaceSelectItemModel(final Map<String, Object> attributes, final String title, final String value) {
		this.attributes = attributes;
		this.title = title;
		this.value = value;
	}

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(final Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Checks if is selected.
	 *
	 * @return the selected
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * Sets the selected.
	 *
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(final boolean selected) {
		this.selected = selected;
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
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.attributes, this.selected, this.title, this.value);
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
		final FindReplaceSelectItemModel other = (FindReplaceSelectItemModel) obj;
		return Objects.equals(this.attributes, other.attributes) && (this.selected == other.selected) && Objects.equals(this.title, other.title)
				&& Objects.equals(this.value, other.value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "FindReplaceSelectItemModel [attributes=" + this.attributes + ", selected=" + this.selected + ", title=" + this.title + ", value=" + this.value
				+ "]";
	}

}
