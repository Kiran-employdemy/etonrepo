package com.eaton.platform.core.models.search.find.replace;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.eaton.platform.core.injectors.annotations.RequestParameter;

/**
 * The Class TextModificationRequestModel.
 *
 * @author Jaroslav Rassadin
 */
@Model(
		adaptables = SlingHttpServletRequest.class,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextModificationRequestModel extends TextSearchRequestModel {

	@Self
	private SlingHttpServletRequest request;

	@RequestParameter
	private boolean backup;

	@RequestParameter
	private List<String> modificationPaths;

	@RequestParameter
	private boolean replicate;

	@RequestParameter
	private boolean rootSearch;

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
		return Objects.hash(this.backup, this.modificationPaths, this.replicate, this.rootSearch, this.userId);
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
		final TextModificationRequestModel other = (TextModificationRequestModel) obj;
		return (this.backup == other.backup) && Objects.equals(this.modificationPaths, other.modificationPaths) && (this.replicate == other.replicate)
				&& (this.rootSearch == other.rootSearch) && Objects.equals(this.userId, other.userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "TextModificationRequestModel [backup=" + this.backup + ", modificationPaths=" + this.modificationPaths + ", replicate=" + this.replicate
				+ ", rootSearch="
				+ this.rootSearch + ", userId=" + this.userId + "]";
	}

}
