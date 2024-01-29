package com.eaton.platform.core.bean.search.find.replace.response;

import java.util.List;

/**
 * The Class TagsModificationResultBean.
 *
 * @author Jaroslav Rassadin
 */
public class TagsModificationResultBean extends BaseResultBean {

	/**
	 * Instantiates a new tags modification result bean.
	 */
	public TagsModificationResultBean() {
	}

	/**
	 * Instantiates a new tags modification result bean.
	 *
	 * @param results
	 *            the results
	 */
	public TagsModificationResultBean(final List<ResultItem> results) {
		this.setResults(results);
	}

	/**
	 * Instantiates a new tags modification result bean.
	 *
	 * @param results
	 *            the results
	 * @param operations
	 *            the operations
	 */
	public TagsModificationResultBean(final List<ResultItem> results, final ModificationOperationsResult operations) {
		this.setResults(results);
		this.setOperations(operations);
	}
}
