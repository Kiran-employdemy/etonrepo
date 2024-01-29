package com.eaton.platform.core.bean.search.find.replace.response;

import java.util.List;

/**
 * The Class TagsSearchResultBean.
 *
 * @author Jaroslav Rassadin
 */
public class TagsSearchResultBean extends BaseResultBean {

	/**
	 * Instantiates a new tags search result bean.
	 */
	public TagsSearchResultBean() {

	}

	/**
	 * Instantiates a new tags search result bean.
	 *
	 * @param results
	 *            the results
	 */
	public TagsSearchResultBean(final List<ResultItem> results) {
		this.setResults(results);
	}
}
