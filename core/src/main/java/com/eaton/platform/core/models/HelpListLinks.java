package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: It is a Sling Model class which automatically maps from Sling objects
 * i.e. dialog fields are injected </html> 
 * @author Eaton
 * @version 1.0
 * @since 2020
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HelpListLinks {
	
	/** The feature block link text. */
	@Inject 
	private String helplistLinkText;
	
	/** The feature block link path. */
	@Inject 
	private String helpListLinkPath;

	/**
	 * Gets the feature block link text.
	 *
	 * @return the feature block link text
	 */
	public String getHelplistLinkText() {
		return helplistLinkText;
	}

	/**
	 * Gets the feature block link path.
	 *
	 * @return the feature block link path
	 */
	public String getHelpListLinkPath() {
		return helpListLinkPath;
	}

}