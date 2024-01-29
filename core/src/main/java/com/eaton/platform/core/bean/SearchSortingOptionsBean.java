package com.eaton.platform.core.bean;

import java.io.Serializable;


/**
 * The Class SearchSortingOptionsBean.
 */
public class SearchSortingOptionsBean implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2774540130111279667L;
	
	/** The attr value. */
	private String sortLabel;
	
	/** The sort value. */
	private String sortValue;

	/**
	 * Gets the sort lable.
	 *
	 * @return the sort lable
	 */
	public String getSortLabel() {
		return sortLabel;
	}

	/**
	 * Sets the sort lable.
	 *
	 * @param sortLable the new sort lable
	 */
	public void setSortLabel(String sortLabel) {
		this.sortLabel = sortLabel;
	}

	/**
	 * Gets the sort value.
	 *
	 * @return the sort value
	 */
	public String getSortValue() {
		return sortValue;
	}

	/**
	 * Sets the sort value.
	 *
	 * @param sortValue the new sort value
	 */
	public void setSortValue(String sortValue) {
		this.sortValue = sortValue;
	}

	

}
