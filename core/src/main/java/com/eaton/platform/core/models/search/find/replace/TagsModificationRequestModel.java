package com.eaton.platform.core.models.search.find.replace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.eaton.platform.core.injectors.annotations.RequestParameter;

/**
 * The Class FindReplaceTagsModificationModel.
 *
 * @author Jaroslav Rassadin
 */
@Model(
		adaptables = SlingHttpServletRequest.class,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TagsModificationRequestModel extends TagsSearchRequestModel {

	@Self
	private SlingHttpServletRequest request;

	@RequestParameter
	private boolean backup;

	@RequestParameter
	private String mode;

	@RequestParameter
	private List<String> modificationPaths;

	@RequestParameter
	private boolean replicate;

	@RequestParameter
	private boolean rootSearch;

	private Map<String, List<String>> tagsForModification;

	@RequestParameter(
			name = "newTags")
	private List<String> tagsForModificationRaw;

	private String userId;

	/**
	 * Initialize the model.
	 */
	@PostConstruct
	public void initialize() {
		this.userId = this.request.getResourceResolver().getUserID();
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
	public String getMode() {
		return this.mode;
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(final String mode) {
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
		if ((this.tagsForModification == null) && CollectionUtils.isNotEmpty(this.getTagsForModificationRaw())) {
			this.tagsForModification = new HashMap<>();
			this.convertTagListToMap(this.getTagsForModificationRaw(), this.tagsForModification);
		}
		return this.tagsForModification;
	}

	/**
	 * Gets the tags for modification raw.
	 *
	 * @return the tagsForModificationRaw
	 */
	public List<String> getTagsForModificationRaw() {
		return this.tagsForModificationRaw;
	}

	/**
	 * Sets the tags for modification raw.
	 *
	 * @param tagsForModificationRaw
	 *            the tagsForModificationRaw to set
	 */
	public void setTagsForModificationRaw(final List<String> tagsForModificationRaw) {
		this.tagsForModificationRaw = tagsForModificationRaw;
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
		result = (prime * result) + Objects.hash(this.backup, this.mode, this.modificationPaths, this.replicate, this.rootSearch, this.tagsForModificationRaw,
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
		final TagsModificationRequestModel other = (TagsModificationRequestModel) obj;
		return (this.backup == other.backup) && Objects.equals(this.mode, other.mode) && Objects.equals(this.modificationPaths, other.modificationPaths)
				&& (this.replicate == other.replicate) && (this.rootSearch == other.rootSearch) && Objects.equals(this.tagsForModificationRaw,
						other.tagsForModificationRaw) && Objects.equals(this.userId, other.userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "TagsModificationRequestModel [backup=" + this.backup + ", mode=" + this.mode + ", modificationPaths=" + this.modificationPaths + ", replicate="
				+ this.replicate
				+ ", rootSearch=" + this.rootSearch + ", tagsForModificationRaw=" + this.tagsForModificationRaw + ", userId=" + this.userId + "]";
	}

}
