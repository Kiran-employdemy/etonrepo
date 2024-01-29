package com.eaton.platform.core.bean.packaging;

import java.util.Objects;

/**
 * The Class PackageBean.
 *
 * @author Jaroslav Rassadin
 */
public class PackageBean {

	private final String description;

	private final String group;

	private final String id;

	private final String name;

	private final String path;

	private final String version;

	private PackageBean(final Builder builder) {
		this.description = builder.description;
		this.group = builder.group;
		this.id = builder.id;
		this.name = builder.name;
		this.path = builder.path;
		this.version = builder.version;
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
	 * Gets the group.
	 *
	 * @return the group
	 */
	public String getGroup() {
		return this.group;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
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
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.description, this.group, this.id, this.name, this.path, this.version);
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
		final PackageBean other = (PackageBean) obj;
		return Objects.equals(this.description, other.description) && Objects.equals(this.group, other.group) && Objects.equals(this.id, other.id) && Objects
				.equals(this.name,
						other.name) && Objects.equals(this.path, other.path) && Objects.equals(this.version, other.version);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PackageBean [description=" + this.description + ", group=" + this.group + ", id=" + this.id + ", name=" + this.name + ", path=" + this.path
				+ ", version=" + this.version
				+ "]";
	}

	/**
	 * Builder.
	 *
	 * @return the builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Class Builder.
	 */
	public static final class Builder {

		private String description;

		private String group;

		private String id;

		private String name;

		private String path;

		private String version;

		private Builder() {
		}

		/**
		 * With description.
		 *
		 * @param description
		 *            the description
		 * @return the builder
		 */
		public Builder withDescription(final String description) {
			this.description = description;
			return this;
		}

		/**
		 * With group.
		 *
		 * @param group
		 *            the group
		 * @return the builder
		 */
		public Builder withGroup(final String group) {
			this.group = group;
			return this;
		}

		/**
		 * With id.
		 *
		 * @param id
		 *            the id
		 * @return the builder
		 */
		public Builder withId(final String id) {
			this.id = id;
			return this;
		}

		/**
		 * With name.
		 *
		 * @param name
		 *            the name
		 * @return the builder
		 */
		public Builder withName(final String name) {
			this.name = name;
			return this;
		}

		/**
		 * With path.
		 *
		 * @param path
		 *            the path
		 * @return the builder
		 */
		public Builder withPath(final String path) {
			this.path = path;
			return this;
		}

		/**
		 * With version.
		 *
		 * @param version
		 *            the version
		 * @return the builder
		 */
		public Builder withVersion(final String version) {
			this.version = version;
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the package bean
		 */
		public PackageBean build() {
			return new PackageBean(this);
		}
	}

}
