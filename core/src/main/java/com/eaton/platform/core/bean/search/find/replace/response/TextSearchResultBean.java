package com.eaton.platform.core.bean.search.find.replace.response;

import java.util.List;

/**
 * The Class TextSearchResultBean.
 *
 * @author Jaroslav Rassadin
 */
public class TextSearchResultBean extends BaseResultBean {

	/**
	 * Instantiates a new text search result bean.
	 */
	public TextSearchResultBean() {
	}

	/**
	 * Instantiates a new text search result bean.
	 *
	 * @param results
	 *            the results
	 */
	public TextSearchResultBean(final List<ResultItem> results) {
		this.setResults(results);
	}
}
