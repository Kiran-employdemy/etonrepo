package com.eaton.platform.core.services.search.find.replace.text.impl;

import java.util.regex.Pattern;

/**
 * The Class PropertyModificationInfoHolder.
 *
 * @author Jaroslav Rassadin
 */
class PropertyModificationInfoHolder {

	private final boolean caseSensitive;

	private final Pattern pattern;

	private final String propertyName;

	private final String replaceText;

	private final String searchText;

	/**
	 * Instantiates a new property modification info holder.
	 *
	 * @param propertyName
	 *            the property name
	 * @param pattern
	 *            the pattern
	 * @param searchText
	 *            the search text
	 * @param replaceText
	 *            the replace text
	 * @param caseSensitive
	 *            the case sensitive
	 */
	public PropertyModificationInfoHolder(final String propertyName, final Pattern pattern, final String searchText, final String replaceText,
			final boolean caseSensitive) {
		this.propertyName = propertyName;
		this.pattern = pattern;
		this.searchText = searchText;
		this.replaceText = replaceText;
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Checks if is case sensitive.
	 *
	 * @return the caseSensitive
	 */
	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}

	/**
	 * Gets the pattern.
	 *
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return this.pattern;
	}

	/**
	 * Gets the property name.
	 *
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * Gets the replace text.
	 *
	 * @return the replaceText
	 */
	public String getReplaceText() {
		return this.replaceText;
	}

	/**
	 * Gets the search text.
	 *
	 * @return the searchText
	 */
	public String getSearchText() {
		return this.searchText;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PropertyModificationInfoHolder [caseSensitive=" + this.caseSensitive + ", pattern=" + this.pattern + ", propertyName=" + this.propertyName
				+ ", replaceText="
				+ this.replaceText + ", searchText=" + this.searchText + "]";
	}

}
