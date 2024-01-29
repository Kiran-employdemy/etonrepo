package com.eaton.platform.core.bean.search.find.replace.request;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.NameConstants;

/**
 * The Enum ContentType.
 *
 * @author Jaroslav Rassadin
 */
public enum ContentType {

	/** The asset. */
	ASSET(DamConstants.NT_DAM_ASSET),

	/** The component. */
	COMPONENT(JcrConstants.NT_UNSTRUCTURED),

	/** The page. */
	PAGE(NameConstants.NT_PAGE);

	private String primaryType;

	ContentType(final String primaryType) {
		this.primaryType = primaryType;
	}

	/**
	 * Get enum value from string.
	 *
	 * @param strValue
	 *            the string value
	 * @return the content type
	 */
	public static ContentType fromValue(final String strValue) {
		if (strValue == null) {
			return null;
		}
		for (final ContentType item : ContentType.values()) {

			if (item.name().equalsIgnoreCase(strValue)) {
				return item;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + strValue + "'");
	}

	/**
	 * Gets the primary type.
	 *
	 * @return the primaryType
	 */
	public String getPrimaryType() {
		return this.primaryType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.name();
	}
}
