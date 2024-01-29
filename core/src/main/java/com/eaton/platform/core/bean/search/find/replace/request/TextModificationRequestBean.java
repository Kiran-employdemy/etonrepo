package com.eaton.platform.core.bean.search.find.replace.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;

/**
 * The Class TextModificationRequestBean.
 *
 * @author Jaroslav Rassadin
 */
public class TextModificationRequestBean extends TextSearchRequestBean {

	private boolean backup;

	private List<String> modificationPaths;

	private boolean replicate;

	private boolean rootSearch;

	private String userId;

	/**
	 * Instantiates a new text modification request bean.
	 */
	public TextModificationRequestBean() {

	}

	/**
	 * Instantiates a new text modification request bean.
	 *
	 * @param source
	 *            the source
	 */
	public TextModificationRequestBean(final TextModificationRequestBean source) {
		super(source);

		this.backup = source.isBackup();
		this.replicate = source.isReplicate();
		this.rootSearch = source.isRootSearch();

		if (CollectionUtils.isNotEmpty(source.getModificationPaths())) {
			this.modificationPaths = new ArrayList<>(source.getModificationPaths());
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
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + Objects.hash(this.backup, this.modificationPaths, this.replicate, this.rootSearch, this.userId);
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
		final TextModificationRequestBean other = (TextModificationRequestBean) obj;
		return (this.backup == other.backup) && Objects.equals(this.modificationPaths, other.modificationPaths) && (this.replicate == other.replicate)
				&& (this.rootSearch == other.rootSearch) && Objects.equals(this.userId, other.userId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "TextModificationRequestBean [backup=" + this.backup + ", modificationPaths=" + this.modificationPaths + ", replicate=" + this.replicate
				+ ", rootSearch=" + this.rootSearch + ", userId=" + this.userId + "]";
	}

}
