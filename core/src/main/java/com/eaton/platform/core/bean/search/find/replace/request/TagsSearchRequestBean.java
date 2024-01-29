package com.eaton.platform.core.bean.search.find.replace.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;

/**
 * The Class TagsSearchRequestBean.
 *
 * @author Jaroslav Rassadin
 */
public class TagsSearchRequestBean {

	private ContentType contentType;

	private String path;

	private Map<String, List<String>> tagsToFind;

	/**
	 * Instantiates a new tags search request bean.
	 */
	public TagsSearchRequestBean() {
	}

	/**
	 * Instantiates a new tags search request bean.
	 *
	 * @param source
	 *            the source
	 */
	public TagsSearchRequestBean(final TagsSearchRequestBean source) {
		this.contentType = source.getContentType();
		this.path = source.getPath();

		if (MapUtils.isNotEmpty(source.getTagsToFind())) {
			final Map<String, List<String>> targetTagsToFind = new HashMap<>();

			source.getTagsToFind().entrySet().stream().forEach((final Map.Entry<String, List<String>> e) -> targetTagsToFind.put(e.getKey(),
					new ArrayList<>(e.getValue())));

			this.tagsToFind = targetTagsToFind;
		}
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
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Gets the tags to find.
	 *
	 * @return the tagsToFind
	 */
	public Map<String, List<String>> getTagsToFind() {
		return this.tagsToFind;
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
	 * Sets the path.
	 *
	 * @param path
	 *            the path to set
	 */
	public void setPath(final String path) {
		this.path = path;
	}

	/**
	 * Sets the tags to find.
	 *
	 * @param tagsToFind
	 *            the tagsToFind to set
	 */
	public void setTagsToFind(final Map<String, List<String>> tagsToFind) {
		this.tagsToFind = tagsToFind;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.contentType, this.path, this.tagsToFind);
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
		final TagsSearchRequestBean other = (TagsSearchRequestBean) obj;
		return (this.contentType == other.contentType) && Objects.equals(this.path, other.path) && Objects.equals(this.tagsToFind, other.tagsToFind);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "TagsSearchRequestBean [contentType=" + this.contentType + ", path=" + this.path + ", tagsToFind=" + this.tagsToFind + "]";
	}

}
