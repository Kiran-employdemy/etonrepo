package com.eaton.platform.core.services.search.find.replace.text;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.services.DelegableService;

/**
 * The Interface FindReplaceTextDataTypeService.
 *
 * Service that executes "find and replace" functionality for text for certain content type.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceTextDataTypeService extends FindReplaceTextDataService, DelegableService<ContentType> {

}
