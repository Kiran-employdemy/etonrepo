package com.eaton.platform.core.bean.packaging;

import java.util.Objects;
import java.util.Set;

/**
 * The Class PackageRequestBean.
 *
 * @author Jaroslav Rassadin
 */
public class PackageRequestBean {

	private String description;

	private Set<PackagePathFilter> filters;

	private String group;

	private String name;

	private String version;

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
	 * Gets the filters.
	 *
	 * @return the filters
	 */
	public Set<PackagePathFilter> getFilters() {
		return this.filters;
	}

	/**
	 * Sets the filters.
	 *
	 * @param filters
	 *            the filters to set
	 */
	public void setFilters(final Set<PackagePathFilter> filters) {
		this.filters = filters;
	}

	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public String getGroup() {
		return this.group;
	}

	/**
	 * Sets the group.
	 *
	 * @param group
	 *            the group to set
	 */
	public void setGroup(final String group) {
		this.group = group;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version
	 *            the version to set
	 */
	public void setVersion(final String version) {
		this.version = version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.description, this.filters, this.group, this.name, this.version);
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
		final PackageRequestBean other = (PackageRequestBean) obj;
		return Objects.equals(this.description, other.description) && Objects.equals(this.filters, other.filters) && Objects.equals(this.group, other.group)
				&& Objects.equals(this.name, other.name) && Objects.equals(this.version, other.version);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PackageRequestBean [description=" + this.description + ", filters=" + this.filters + ", group=" + this.group + ", name=" + this.name
				+ ", version=" + this.version + "]";
	}

}
