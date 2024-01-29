package com.eaton.platform.core.bean.search.find.replace.request;

/**
 * The Enum Mode.
 *
 * @author Jaroslav Rassadin
 */
public enum Mode {

	/** The add. */
	ADD,

	/** The delete. */
	DELETE,

	/** The replace. */
	REPLACE;

	Mode() {
	}

	/**
	 * Get enum value from string.
	 *
	 * @param strValue
	 *            the string value
	 * @return the content type
	 */
	public static Mode fromValue(final String strValue) {
		if (strValue == null) {
			return null;
		}
		for (final Mode item : Mode.values()) {

			if (item.name().equalsIgnoreCase(strValue)) {
				return item;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + strValue + "'");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.name();
	}
}
