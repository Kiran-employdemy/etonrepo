package com.eaton.platform.core.bean.packaging;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * The Class PackagePathFilter.
 *
 * @author Jaroslav Rassadin
 */
public class PackagePathFilter {

	private String rootPath;

	private Set<Map.Entry<String, Boolean>> rules;

	/**
	 * Instantiates a new package path filter.
	 */
	public PackagePathFilter() {
	}

	/**
	 * Instantiates a new package path filter.
	 *
	 * @param rootPath
	 *            the root path
	 */
	public PackagePathFilter(final String rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * Instantiates a new package path filter.
	 *
	 * @param rootPath
	 *            the root path
	 * @param rules
	 *            the rules
	 */
	public PackagePathFilter(final String rootPath, final Set<Entry<String, Boolean>> rules) {
		this.rootPath = rootPath;
		this.rules = rules;
	}

	/**
	 * Gets the root path.
	 *
	 * @return the rootPath
	 */
	public String getRootPath() {
		return this.rootPath;
	}

	/**
	 * Sets the root path.
	 *
	 * @param rootPath
	 *            the rootPath to set
	 */
	public void setRootPath(final String rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * Gets the rules.
	 *
	 * @return the rules
	 */
	public Set<Map.Entry<String, Boolean>> getRules() {
		return this.rules;
	}

	/**
	 * Sets the rules.
	 *
	 * @param rules
	 *            the rules to set
	 */
	public void setRules(final Set<Map.Entry<String, Boolean>> rules) {
		this.rules = rules;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.rootPath, this.rules);
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
		final PackagePathFilter other = (PackagePathFilter) obj;
		return Objects.equals(this.rootPath, other.rootPath) && Objects.equals(this.rules, other.rules);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PackagePathFilter [rootPath=" + this.rootPath + ", rules=" + this.rules + "]";
	}

}
