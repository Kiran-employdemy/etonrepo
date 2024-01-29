package com.eaton.platform.core.bean.search.find.replace.response;

import java.util.Objects;

/**
 * The Class ReplicationResult.
 *
 * @author Daniel Gruszka
 */
public class ReplicationResult {

	private String reportPath;

	/**
	 * Instantiates a new replication result.
	 */
	public ReplicationResult() {
	}

	/**
	 * Instantiates a new replication result.
	 *
	 * @param reportPath
	 *            the replication report path
	 */
	public ReplicationResult(final String reportPath) {
		this.reportPath = reportPath;
	}

	/**
	 * Gets the report path.
	 *
	 * @return the reportPath
	 */
	public String getReportPath() {
		return this.reportPath;
	}

	/**
	 * Sets the report path.
	 *
	 * @param reportPath
	 *            the reportPath to set
	 */
	public void setReportPath(final String reportPath) {
		this.reportPath = reportPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.reportPath);
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
		final ReplicationResult other = (ReplicationResult) obj;
		return Objects.equals(this.reportPath, other.reportPath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ReplicationResult [reportPath=" + this.reportPath + "]";
	}
}
