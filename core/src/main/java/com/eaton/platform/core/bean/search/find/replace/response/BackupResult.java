package com.eaton.platform.core.bean.search.find.replace.response;

import java.util.Objects;

/**
 * The Class BackupResult.
 *
 * @author Jaroslav Rassadin
 */
public class BackupResult {

	private String packagePath;

	/**
	 * Instantiates a new backup result.
	 */
	public BackupResult() {
	}

	/**
	 * Instantiates a new backup result.
	 *
	 * @param packagePath
	 *            the package path
	 */
	public BackupResult(final String packagePath) {
		this.packagePath = packagePath;
	}

	/**
	 * Gets the package path.
	 *
	 * @return the packagePath
	 */
	public String getPackagePath() {
		return this.packagePath;
	}

	/**
	 * Sets the package path.
	 *
	 * @param packagePath
	 *            the packagePath to set
	 */
	public void setPackagePath(final String packagePath) {
		this.packagePath = packagePath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.packagePath);
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
		final BackupResult other = (BackupResult) obj;
		return Objects.equals(this.packagePath, other.packagePath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "BackupResult [packagePath=" + this.packagePath + "]";
	}
}
