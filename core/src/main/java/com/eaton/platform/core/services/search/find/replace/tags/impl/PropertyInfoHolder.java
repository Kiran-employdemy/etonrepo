package com.eaton.platform.core.services.search.find.replace.tags.impl;

import java.util.Set;

import javax.jcr.ValueFactory;

/**
 * The Class PropertyInfoHolder.
 *
 * @author Jaroslav Rassadin
 */
class PropertyInfoHolder {

	private final boolean multiple;

	private final String name;

	private final Set<String> updatedTags;

	private final ValueFactory valueFactory;

	/**
	 * Instantiates a new property info holder.
	 *
	 * @param multiple
	 *            the multiple
	 * @param name
	 *            the name
	 * @param updatedTags
	 *            the updated tags
	 * @param valueFactory
	 *            the value factory
	 */
	public PropertyInfoHolder(final boolean multiple, final String name, final Set<String> updatedTags, final ValueFactory valueFactory) {
		this.multiple = multiple;
		this.name = name;
		this.updatedTags = updatedTags;
		this.valueFactory = valueFactory;
	}

	/**
	 * Checks if is multiple.
	 *
	 * @return the multiple
	 */
	public boolean isMultiple() {
		return this.multiple;
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
	 * Gets the updated tags.
	 *
	 * @return the updatedTags
	 */
	public Set<String> getUpdatedTags() {
		return this.updatedTags;
	}

	/**
	 * Gets the value factory.
	 *
	 * @return the valueFactory
	 */
	public ValueFactory getValueFactory() {
		return this.valueFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PropertyInfoHolder [multiple=" + this.multiple + ", name=" + this.name + ", updatedTags=" + this.updatedTags + ", valueFactory="
				+ this.valueFactory + "]";
	}

}
