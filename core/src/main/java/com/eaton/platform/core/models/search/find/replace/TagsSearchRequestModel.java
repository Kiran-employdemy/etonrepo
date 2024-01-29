package com.eaton.platform.core.models.search.find.replace;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.eaton.platform.core.injectors.annotations.RequestParameter;

/**
 * The Class TagsSearchRequestModel.
 *
 * @author Jaroslav Rassadin
 */
@Model(
		adaptables = SlingHttpServletRequest.class,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TagsSearchRequestModel {

	@RequestParameter
	private String contentType;

	@RequestParameter(
			name = "specificPath")
	private String path;

	@RequestParameter(
			name = "findTags")
	private List<String> tagsToFindRaw;

	private Map<String, List<String>> tagsToFind;

	/**
	 * Gets the content type.
	 *
	 * @return the contentType
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(final String contentType) {
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
	 * Gets the tags to find raw.
	 *
	 * @return the tagsToFindRaw
	 */
	public List<String> getTagsToFindRaw() {
		return this.tagsToFindRaw;
	}

	/**
	 * Sets the tags to find raw.
	 *
	 * @param tagsToFindRaw
	 *            the tagsToFindRaw to set
	 */
	public void setTagsToFindRaw(final List<String> tagsToFindRaw) {
		this.tagsToFindRaw = tagsToFindRaw;
	}

	/**
	 * Gets the tags to find.
	 *
	 * @return the tagsToFind
	 */
	public Map<String, List<String>> getTagsToFind() {
		if ((this.tagsToFind == null) && CollectionUtils.isNotEmpty(this.getTagsToFindRaw())) {
			this.tagsToFind = new HashMap<>();
			this.convertTagListToMap(this.getTagsToFindRaw(), this.tagsToFind);
		}
		return this.tagsToFind;
	}

	/**
	 * Convert tag list to map.
	 *
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 */
	protected void convertTagListToMap(final List<String> source, final Map<String, List<String>> target) {
		if (CollectionUtils.isNotEmpty(source)) {
			source.stream().distinct().forEach(s -> this.parseTagString(s, target));
		}
	}

	private void parseTagString(final String tagString, final Map<String, List<String>> tagsMap) {
		final String[] parts = tagString.split("\\|");
		List<String> values;

		if (parts.length > 1) {
			final String tagValue = this.parseTagValue(parts[1]);
			values = tagsMap.get(parts[0]);

			if (values != null) {
				values.add(tagValue);

			} else {
				values = new ArrayList<>();
				values.add(tagValue);
				tagsMap.put(parts[0], values);
			}
		}
	}

	private String parseTagValue(final String tagValue) {
		// tag value might contain commas, for that reason on UI value is twice URL encoded
		return URLDecoder.decode(tagValue, StandardCharsets.UTF_8);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.contentType, this.path, this.tagsToFindRaw);
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
		final TagsSearchRequestModel other = (TagsSearchRequestModel) obj;
		return Objects.equals(this.contentType, other.contentType) && Objects.equals(this.path, other.path)
				&& Objects.equals(this.tagsToFindRaw, other.tagsToFindRaw);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "TagsSearchRequestModel [contentType=" + this.contentType + ", path=" + this.path + ", tagsToFindRaw=" + this.tagsToFindRaw + "]";
	}

}
