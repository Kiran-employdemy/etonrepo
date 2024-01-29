package com.eaton.platform.core.bean;

import java.io.Serializable;

/**
 * The Class SortByOptionValueBean.
 */
public class SortByOptionValueBean implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3745583463864762442L;

	/** The label. */
	String label;

	/** The value. */
	String value;

	public SortByOptionValueBean() {
	}

	public SortByOptionValueBean(String label, String value) {
		this.label = label;
		this.value = value;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
