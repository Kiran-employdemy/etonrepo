package com.eaton.platform.core.bean.file.purge;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * The Class FilePurgeConfigBean.
 *
 * @author Jaroslav Rassadin
 */
public class FilePurgeConfigBean {

	/**
	 * The Enum Unit.
	 */
	public enum Unit {

		/** The day. */
		DAY(ChronoUnit.DAYS),

		/** The week. */
		WEEK(ChronoUnit.WEEKS),

		/** The month. */
		MONTH(ChronoUnit.MONTHS),

		/** The year. */
		YEAR(ChronoUnit.YEARS);

		private transient ChronoUnit chronoUnit;

		/**
		 * Instantiates a new unit.
		 *
		 * @param chronoUnit
		 *            the chrono unit
		 */
		Unit(final ChronoUnit chronoUnit) {
			this.chronoUnit = chronoUnit;
		}

		/**
		 * Gets the chrono unit.
		 *
		 * @return the chronoUnit
		 */
		public ChronoUnit getChronoUnit() {
			return this.chronoUnit;
		}

	}

	private final boolean enabled;

	private final int maxAge;

	private final String path;

	private final Unit unit;

	/**
	 * Instantiates a new file purge config bean.
	 *
	 * @param enabled
	 *            the enabled
	 * @param maxAge
	 *            the max age
	 * @param path
	 *            the path
	 * @param unit
	 *            the unit
	 */
	public FilePurgeConfigBean(final boolean enabled, final int maxAge, final String path, final Unit unit) {
		this.enabled = enabled;
		this.maxAge = maxAge;
		this.path = path;
		this.unit = unit;
	}

	/**
	 * Checks if is enabled.
	 *
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Gets the max age.
	 *
	 * @return the maxAge
	 */
	public int getMaxAge() {
		return this.maxAge;
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
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public Unit getUnit() {
		return this.unit;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.enabled, this.maxAge, this.path, this.unit);
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
		final FilePurgeConfigBean other = (FilePurgeConfigBean) obj;
		return (this.enabled == other.enabled) && (this.maxAge == other.maxAge) && Objects.equals(this.path, other.path) && (this.unit == other.unit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "FilePurgeConfigBean [enabled=" + this.enabled + ", maxAge=" + this.maxAge + ", path=" + this.path + ", unit=" + this.unit + "]";
	}

}
