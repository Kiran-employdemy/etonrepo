package com.eaton.platform.core.services.search.find.replace.text;

import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextSearchResultBean;

/**
 * The Interface FindReplaceTextFacade.
 *
 * Facade for "find and replace" functionality for text.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceTextFacade {

	/**
	 * Find tags.
	 *
	 * @param searchRequest
	 *            the search request
	 * @return the text search result bean
	 */
	TextSearchResultBean find(TextSearchRequestBean searchRequest);

	/**
	 * Replace tags.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @return the text modification result bean
	 */
	TextModificationResultBean replace(TextModificationRequestBean modificationRequest);
}
