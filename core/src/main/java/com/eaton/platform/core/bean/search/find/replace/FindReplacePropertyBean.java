package com.eaton.platform.core.bean.search.find.replace;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * The Class FindReplacePropertyBean.
 *
 * @author Jaroslav Rassadin
 */
public class FindReplacePropertyBean {

	private final boolean multiple;

	private final String name;

	private final Set<FindReplaceResourceTypeBean> resourceTypes;

	/**
	 * Instantiates a new find replace property bean.
	 *
	 * @param name
	 *            the name
	 */
	public FindReplacePropertyBean(final String name) {
		this(false, name, Collections.emptySet());
	}

	/**
	 * Instantiates a new find replace property bean.
	 *
	 * @param multiple
	 *            the multiple
	 * @param name
	 *            the name
	 */
	public FindReplacePropertyBean(final boolean multiple, final String name) {
		this(multiple, name, Collections.emptySet());
	}

	/**
	 * Instantiates a new find replace property bean.
	 *
	 * @param multiple
	 *            the multiple
	 * @param name
	 *            the name
	 * @param resourceTypes
	 *            the resource types
	 */
	public FindReplacePropertyBean(final boolean multiple, final String name, final Set<FindReplaceResourceTypeBean> resourceTypes) {
		this.multiple = multiple;
		this.name = name;
		this.resourceTypes = resourceTypes;
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
	 * Gets the resource types.
	 *
	 * @return the resourceTypes
	 */
	public Set<FindReplaceResourceTypeBean> getResourceTypes() {
		return this.resourceTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.multiple, this.name, this.resourceTypes);
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
		final FindReplacePropertyBean other = (FindReplacePropertyBean) obj;
		return (this.multiple == other.multiple) && Objects.equals(this.name, other.name) && Objects.equals(this.resourceTypes, other.resourceTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "FindReplacePropertyBean [multiple=" + this.multiple + ", name=" + this.name + ", resourceTypes=" + this.resourceTypes + "]";
	}

}
