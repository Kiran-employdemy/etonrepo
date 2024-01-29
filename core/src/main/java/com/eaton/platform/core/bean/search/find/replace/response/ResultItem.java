package com.eaton.platform.core.bean.search.find.replace.response;

import java.util.Objects;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;

/**
 * The Class ResultItem.
 *
 * @author Jaroslav Rassadin
 */
public class ResultItem implements Comparable<ResultItem> {

	private String topContainerPath;

	private String topContainerTitle;

	private String containerPath;

	private String containerTitle;

	private ContentType contentType;

	private String path;

	private String title;

	/**
	 * Instantiates a new result item.
	 */
	public ResultItem() {
	}

	private ResultItem(final Builder builder) {
		this.topContainerPath = builder.topContainerPath;
		this.topContainerTitle = builder.topContainerTitle;
		this.containerPath = builder.containerPath;
		this.containerTitle = builder.containerTitle;
		this.contentType = builder.contentType;
		this.path = builder.path;
		this.title = builder.title;
	}

	/**
	 * Gets the top container path.
	 *
	 * @return the topContainerPath
	 */
	public String getTopContainerPath() {
		return this.topContainerPath;
	}

	/**
	 * Sets the top container path.
	 *
	 * @param topContainerPath
	 *            the topContainerPath to set
	 */
	public void setTopContainerPath(final String topContainerPath) {
		this.topContainerPath = topContainerPath;
	}

	/**
	 * Gets the top container title.
	 *
	 * @return the topContainerTitle
	 */
	public String getTopContainerTitle() {
		return this.topContainerTitle;
	}

	/**
	 * Sets the top container title.
	 *
	 * @param topContainerTitle
	 *            the topContainerTitle to set
	 */
	public void setTopContainerTitle(final String topContainerTitle) {
		this.topContainerTitle = topContainerTitle;
	}

	/**
	 * Gets the container path.
	 *
	 * @return the containerPath
	 */
	public String getContainerPath() {
		return this.containerPath;
	}

	/**
	 * Sets the container path.
	 *
	 * @param containerPath
	 *            the containerPath to set
	 */
	public void setContainerPath(final String containerPath) {
		this.containerPath = containerPath;
	}

	/**
	 * Gets the container title.
	 *
	 * @return the containerTitle
	 */
	public String getContainerTitle() {
		return this.containerTitle;
	}

	/**
	 * Sets the container title.
	 *
	 * @param containerTitle
	 *            the containerTitle to set
	 */
	public void setContainerTitle(final String containerTitle) {
		this.containerTitle = containerTitle;
	}

	/**
	 * Gets the content type.
	 *
	 * @return the contentType
	 */
	public ContentType getContentType() {
		return this.contentType;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(final ContentType contentType) {
		this.contentType = contentType;
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
	 * Sets the path.
	 *
	 * @param path
	 *            the path to set
	 */
	public void setPath(final String path) {
		this.path = path;
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
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.containerPath, this.containerTitle, this.contentType, this.path, this.title, this.topContainerPath, this.topContainerTitle);
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
		final ResultItem other = (ResultItem) obj;
		return Objects.equals(this.containerPath, other.containerPath) && Objects.equals(this.containerTitle, other.containerTitle)
				&& (this.contentType == other.contentType)
				&& Objects.equals(this.path, other.path) && Objects.equals(this.title, other.title) && Objects.equals(this.topContainerPath,
						other.topContainerPath) && Objects
								.equals(this.topContainerTitle, other.topContainerTitle);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final ResultItem other) {
		return this.path.compareTo(other.getPath());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ResultItem [topContainerPath=" + this.topContainerPath + ", topContainerTitle=" + this.topContainerTitle + ", containerPath="
				+ this.containerPath
				+ ", containerTitle=" + this.containerTitle + ", contentType=" + this.contentType + ", path=" + this.path + ", title=" + this.title + "]";
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
		private String topContainerPath;
		private String topContainerTitle;
		private String containerPath;
		private String containerTitle;
		private ContentType contentType;
		private String path;
		private String title;

		private Builder() {
		}

		/**
		 * With top container path.
		 *
		 * @param topContainerPath
		 *            the top container path
		 * @return the builder
		 */
		public Builder withTopContainerPath(final String topContainerPath) {
			this.topContainerPath = topContainerPath;
			return this;
		}

		/**
		 * With top container title.
		 *
		 * @param topContainerTitle
		 *            the top container title
		 * @return the builder
		 */
		public Builder withTopContainerTitle(final String topContainerTitle) {
			this.topContainerTitle = topContainerTitle;
			return this;
		}

		/**
		 * With container path.
		 *
		 * @param containerPath
		 *            the container path
		 * @return the builder
		 */
		public Builder withContainerPath(final String containerPath) {
			this.containerPath = containerPath;
			return this;
		}

		/**
		 * With container title.
		 *
		 * @param containerTitle
		 *            the container title
		 * @return the builder
		 */
		public Builder withContainerTitle(final String containerTitle) {
			this.containerTitle = containerTitle;
			return this;
		}

		/**
		 * With content type.
		 *
		 * @param contentType
		 *            the content type
		 * @return the builder
		 */
		public Builder withContentType(final ContentType contentType) {
			this.contentType = contentType;
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
		 * With title.
		 *
		 * @param title
		 *            the title
		 * @return the builder
		 */
		public Builder withTitle(final String title) {
			this.title = title;
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the result item
		 */
		public ResultItem build() {
			return new ResultItem(this);
		}
	}

}
