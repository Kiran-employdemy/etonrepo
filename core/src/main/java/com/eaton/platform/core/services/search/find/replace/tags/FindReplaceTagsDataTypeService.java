package com.eaton.platform.core.services.search.find.replace.tags;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.services.DelegableService;

/**
 * The Interface FindReplaceTagsDataTypeService.
 *
 * Service that executes "find and replace" functionality for tags for certain content type.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceTagsDataTypeService extends FindReplaceTagsDataService, DelegableService<ContentType> {

	/**
	 * Gets the supported content type.
	 *
	 * @return the content type
	 */
	ContentType getContentType();
}
