package com.eaton.platform.core.services.search.find.replace;

import org.apache.sling.api.resource.Resource;

/**
 * The Interface FindReplaceComponentService.
 *
 * Provides data regarding component types.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceComponentService {

	/**
	 * Gets the component type title.
	 *
	 * @param componentInstance
	 *            the component instance
	 * @return the type title
	 */
	String getTypeTitle(Resource componentInstance);

	/**
	 * Gets the type title by resource type.
	 *
	 * @param path
	 *            the path
	 * @return the type title by resource type
	 */
	String getTypeTitleByResourceType(String resourceType);
}
