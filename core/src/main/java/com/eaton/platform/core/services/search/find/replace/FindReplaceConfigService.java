package com.eaton.platform.core.services.search.find.replace;

import java.util.List;
import java.util.Map;

import com.eaton.platform.core.bean.search.find.replace.FindReplacePropertyBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceTagBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;

/**
 * The Interface FindReplaceConfigService.
 *
 * Provides configurations for find and replaces functionality for text and tags.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceConfigService {

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	List<FindReplaceTagBean> getTags();

	/**
	 * Gets the tag properties map.
	 *
	 * @return the tag properties map
	 */
	Map<String, FindReplacePropertyBean> getTagPropertiesMap(ContentType contentType);
}
