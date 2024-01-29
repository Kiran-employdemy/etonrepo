package com.eaton.platform.core.services.search.find.replace.tags;

import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsSearchResultBean;

/**
 * The Interface FindReplaceTagsFacade.
 *
 * Facade for "find and replace" functionality for tags.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceTagsFacade {

	/**
	 * Add tags.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @return the tags modification result bean
	 */
	TagsModificationResultBean add(TagsModificationRequestBean modificationRequest);

	/**
	 * Delete tags.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @return the tags modification result bean
	 */
	TagsModificationResultBean delete(TagsModificationRequestBean modificationRequest);

	/**
	 * Find tags.
	 *
	 * @param searchRequest
	 *            the search request
	 * @return the tags search result bean
	 */
	TagsSearchResultBean find(TagsSearchRequestBean searchRequest);

	/**
	 * Replace tags.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @return the tags modification result bean
	 */
	TagsModificationResultBean replace(TagsModificationRequestBean modificationRequest);
}
