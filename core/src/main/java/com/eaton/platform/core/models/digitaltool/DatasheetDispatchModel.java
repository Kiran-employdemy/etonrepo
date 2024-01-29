package com.eaton.platform.core.models.digitaltool;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
/**
 * <html> Description: This Sling Model used in Send email functionality if required. </html>
 *
 * @author Eaton
 * @version 1.0
 * @since 2022
 */

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DatasheetDispatchModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DatasheetDispatchModel.class);

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.info("DatasheetDispatchModel :: init() :: Started");
	}
}