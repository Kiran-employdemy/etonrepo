package com.eaton.platform.core.services.msm.impl;

import java.util.List;
import java.util.Objects;

/**
 * The Class RolloutConflictSearchResultItem.
 *
 * @author Jaroslav Rassadin
 */
public class RolloutConflictSearchResultItem {

	private String blueprintPath;

	private String liveCopyMsmMovedPath;

	private String liveCopyPath;

	private List<String> msmMovedComponentPaths;

	/**
	 * @return the blueprintPath
	 */
	public String getBlueprintPath() {
		return this.blueprintPath;
	}

	/**
	 * @param blueprintPath
	 *            the blueprintPath to set
	 */
	public void setBlueprintPath(final String blueprintPath) {
		this.blueprintPath = blueprintPath;
	}

	/**
	 * @return the liveCopyMsmMovedPath
	 */
	public String getLiveCopyMsmMovedPath() {
		return this.liveCopyMsmMovedPath;
	}

	/**
	 * @param liveCopyMsmMovedPath
	 *            the liveCopyMsmMovedPath to set
	 */
	public void setLiveCopyMsmMovedPath(final String liveCopyMsmMovedPath) {
		this.liveCopyMsmMovedPath = liveCopyMsmMovedPath;
	}

	/**
	 * @return the liveCopyPath
	 */
	public String getLiveCopyPath() {
		return this.liveCopyPath;
	}

	/**
	 * @param liveCopyPath
	 *            the liveCopyPath to set
	 */
	public void setLiveCopyPath(final String liveCopyPath) {
		this.liveCopyPath = liveCopyPath;
	}

	/**
	 * @return the msmMovedComponentPaths
	 */
	public List<String> getMsmMovedComponentPaths() {
		return this.msmMovedComponentPaths;
	}

	/**
	 * @param msmMovedComponentPaths
	 *            the msmMovedComponentPaths to set
	 */
	public void setMsmMovedComponentPaths(final List<String> msmMovedComponentPaths) {
		this.msmMovedComponentPaths = msmMovedComponentPaths;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.blueprintPath, this.liveCopyMsmMovedPath, this.liveCopyPath, this.msmMovedComponentPaths);
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
		final RolloutConflictSearchResultItem other = (RolloutConflictSearchResultItem) obj;
		return Objects.equals(this.blueprintPath, other.blueprintPath) && Objects.equals(this.liveCopyMsmMovedPath, other.liveCopyMsmMovedPath)
				&& Objects.equals(this.liveCopyPath, other.liveCopyPath) && Objects.equals(this.msmMovedComponentPaths, other.msmMovedComponentPaths);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "RolloutConflictSearchResultItem [blueprintPath=" + this.blueprintPath + ", liveCopyMsmMovedPath=" + this.liveCopyMsmMovedPath
				+ ", liveCopyPath=" + this.liveCopyPath + ", msmMovedComponentPaths=" + this.msmMovedComponentPaths + "]";
	}

}
