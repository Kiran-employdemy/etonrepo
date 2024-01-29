package com.eaton.platform.core.bean.search.find.replace.response;

import java.util.List;

/**
 * The Class TextModificationResultBean.
 *
 * @author Jaroslav Rassadin
 */
public class TextModificationResultBean extends BaseResultBean {

	/**
	 * Instantiates a new text modification result bean.
	 */
	public TextModificationResultBean() {

	}

	/**
	 * Instantiates a new text modification result bean.
	 *
	 * @param results
	 *            the results
	 */
	public TextModificationResultBean(final List<ResultItem> results) {
		this.setResults(results);
	}

	/**
	 * Instantiates a new text modification result bean.
	 *
	 * @param results
	 *            the results
	 * @param operations
	 *            the operations
	 */
	public TextModificationResultBean(final List<ResultItem> results, final ModificationOperationsResult operations) {
		this.setResults(results);
		this.setOperations(operations);
	}
}
