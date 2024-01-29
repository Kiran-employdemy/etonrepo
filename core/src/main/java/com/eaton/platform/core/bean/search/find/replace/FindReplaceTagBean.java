package com.eaton.platform.core.bean.search.find.replace;

import java.util.Objects;

/**
 * The Class FindReplaceTagBean.
 *
 * @author Jaroslav Rassadin
 */
public class FindReplaceTagBean {

	private final String id;

	private final boolean multiple;

	private final boolean ownProperty;

	private final String path;

	private final String title;

	/**
	 * Instantiates a new find replace tag bean.
	 *
	 * @param id
	 *            the id
	 * @param multiple
	 *            the multiple
	 * @param ownProperty
	 *            the own property
	 * @param path
	 *            the path
	 * @param title
	 *            the title
	 */
	public FindReplaceTagBean(final String id, final boolean multiple, final boolean ownProperty, final String path, final String title) {
		this.id = id;
		this.multiple = multiple;
		this.ownProperty = ownProperty;
		this.path = path;
		this.title = title;
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
	 * Checks if is multiple.
	 *
	 * @return the multiple
	 */
	public boolean isMultiple() {
		return this.multiple;
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
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return this.path;
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
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.multiple, this.ownProperty, this.path, this.title);
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
		final FindReplaceTagBean other = (FindReplaceTagBean) obj;
		return Objects.equals(this.id, other.id) && (this.multiple == other.multiple) && (this.ownProperty == other.ownProperty) && Objects.equals(this.path,
				other.path) && Objects
						.equals(this.title, other.title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "FindReplaceTagBean [id=" + this.id + ", multiple=" + this.multiple + ", ownProperty=" + this.ownProperty + ", path=" + this.path + ", title="
				+ this.title + "]";
	}

}
