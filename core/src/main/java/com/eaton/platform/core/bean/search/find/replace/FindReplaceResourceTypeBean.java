package com.eaton.platform.core.bean.search.find.replace;

import java.util.Objects;

/**
 * The Class FindReplaceResourceTypeBean.
 *
 * @author Jaroslav Rassadin
 */
public class FindReplaceResourceTypeBean {

	private final String name;

	private final String subType;

	/**
	 * Instantiates a new find replace resource type bean.
	 *
	 * @param name
	 *            the name
	 */
	public FindReplaceResourceTypeBean(final String name) {
		this.name = name;
		this.subType = null;
	}

	/**
	 * Instantiates a new find replace resource type bean.
	 *
	 * @param name
	 *            the name
	 * @param subType
	 *            the sub type
	 */
	public FindReplaceResourceTypeBean(final String name, final String subType) {
		this.name = name;
		this.subType = subType;
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
	 * Gets the sub type.
	 *
	 * @return the subType
	 */
	public String getSubType() {
		return this.subType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.subType);
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
		final FindReplaceResourceTypeBean other = (FindReplaceResourceTypeBean) obj;
		return Objects.equals(this.name, other.name) && Objects.equals(this.subType, other.subType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "FindReplaceResourceTypeBean [name=" + this.name + ", subType=" + this.subType + "]";
	}

}
