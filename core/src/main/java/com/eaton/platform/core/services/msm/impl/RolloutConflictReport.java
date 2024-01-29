package com.eaton.platform.core.services.msm.impl;

import java.util.Objects;

/**
 * The Class RolloutConflictReport.
 *
 * @author Jaroslav Rassadin
 */
public class RolloutConflictReport {

	private String format;

	private String name;

	private String reportPath;

	/**
	 * Instantiates a new rollout conflict report.
	 */
	public RolloutConflictReport() {
	}

	/**
	 * Instantiates a new rollout conflict report.
	 *
	 * @param format
	 *            the format
	 * @param name
	 *            the name
	 * @param reportPath
	 *            the report path
	 */
	public RolloutConflictReport(final String format, final String name, final String reportPath) {
		this.format = format;
		this.name = name;
		this.reportPath = reportPath;
	}

	/**
	 * Gets the format.
	 *
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * Sets the format.
	 *
	 * @param format
	 *            the format to set
	 */
	public void setFormat(final String format) {
		this.format = format;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
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
		return Objects.hash(this.format, this.name, this.reportPath);
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
		final RolloutConflictReport other = (RolloutConflictReport) obj;
		return Objects.equals(this.format, other.format) && Objects.equals(this.name, other.name) && Objects.equals(this.reportPath, other.reportPath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "RolloutConflictReport [format=" + this.format + ", name=" + this.name + ", reportPath=" + this.reportPath + "]";
	}

}
