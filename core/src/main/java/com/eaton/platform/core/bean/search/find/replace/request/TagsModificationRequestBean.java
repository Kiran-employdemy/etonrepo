package com.eaton.platform.core.bean.search.find.replace.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

/**
 * The Class TagsModificationRequestBean.
 *
 * @author Jaroslav Rassadin
 */
public class TagsModificationRequestBean extends TagsSearchRequestBean {

	private boolean backup;

	private Mode mode;

	private List<String> modificationPaths;

	private boolean replicate;

	private boolean rootSearch;

	private Map<String, List<String>> tagsForModification;

	private String userId;

	/**
	 * Instantiates a new tags modification request bean.
	 */
	public TagsModificationRequestBean() {

	}

	/**
	 * Instantiates a new tags modification request bean.
	 *
	 * @param source
	 *            the source
	 */
	public TagsModificationRequestBean(final TagsModificationRequestBean source) {
		super(source);

		this.backup = source.isBackup();
		this.mode = source.getMode();
		this.replicate = source.isReplicate();
		this.rootSearch = source.isRootSearch();

		if (CollectionUtils.isNotEmpty(source.getModificationPaths())) {
			this.modificationPaths = new ArrayList<>(source.getModificationPaths());
		}
		if (MapUtils.isNotEmpty(source.getTagsForModification())) {
			final Map<String, List<String>> targetTagsForModification = new HashMap<>();

			source.getTagsForModification().entrySet().stream().forEach((final Map.Entry<String, List<String>> e) -> {
				targetTagsForModification.put(e.getKey(), new ArrayList<>(e.getValue()));
			});
			this.tagsForModification = targetTagsForModification;
		}
	}

	/**
	 * Checks if is backup.
	 *
	 * @return the backup
	 */
	public boolean isBackup() {
		return this.backup;
	}

	/**
	 * Sets the backup.
	 *
	 * @param backup
	 *            the backup to set
	 */
	public void setBackup(final boolean backup) {
		this.backup = backup;
	}

	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public Mode getMode() {
		return this.mode;
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(final Mode mode) {
		this.mode = mode;
	}

	/**
	 * Gets the modification paths.
	 *
	 * @return the modificationPaths
	 */
	public List<String> getModificationPaths() {
		return this.modificationPaths;
	}

	/**
	 * Sets the modification paths.
	 *
	 * @param modificationPaths
	 *            the modificationPaths to set
	 */
	public void setModificationPaths(final List<String> modificationPaths) {
		this.modificationPaths = modificationPaths;
	}

	/**
	 * Checks if is replicate.
	 *
	 * @return the replicate
	 */
	public boolean isReplicate() {
		return this.replicate;
	}

	/**
	 * Sets the replicate.
	 *
	 * @param replicate
	 *            the replicate to set
	 */
	public void setReplicate(final boolean replicate) {
		this.replicate = replicate;
	}

	/**
	 * Checks if is root search.
	 *
	 * @return the rootSearch
	 */
	public boolean isRootSearch() {
		return this.rootSearch;
	}

	/**
	 * Sets the root search.
	 *
	 * @param rootSearch
	 *            the rootSearch to set
	 */
	public void setRootSearch(final boolean rootSearch) {
		this.rootSearch = rootSearch;
	}

	/**
	 * Gets the tags for modification.
	 *
	 * @return the tagsForModification
	 */
	public Map<String, List<String>> getTagsForModification() {
		return this.tagsForModification;
	}

	/**
	 * Sets the tags for modification.
	 *
	 * @param tagsForModification
	 *            the tagsForModification to set
	 */
	public void setTagsForModification(final Map<String, List<String>> tagsForModification) {
		this.tagsForModification = tagsForModification;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the userId
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(final String userId) {
		this.userId = userId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + Objects.hash(this.backup, this.mode, this.modificationPaths, this.replicate, this.rootSearch, this.tagsForModification,
				this.userId);
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
		final TagsModificationRequestBean other = (TagsModificationRequestBean) obj;
		return (this.backup == other.backup) && (this.mode == other.mode) && Objects.equals(this.modificationPaths, other.modificationPaths)
				&& (this.replicate == other.replicate)
				&& (this.rootSearch == other.rootSearch) && Objects.equals(this.tagsForModification, other.tagsForModification) && Objects.equals(this.userId,
						other.userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "TagsModificationRequestBean [backup=" + this.backup + ", mode=" + this.mode + ", modificationPaths=" + this.modificationPaths + ", replicate="
				+ this.replicate + ", rootSearch=" + this.rootSearch + ", tagsForModification=" + this.tagsForModification + ", userId=" + this.userId + "]";
	}

}
