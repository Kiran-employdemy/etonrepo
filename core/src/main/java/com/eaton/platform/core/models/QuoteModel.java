package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class)
public class QuoteModel {
	
	/** The trans author name. */
	@Inject @Default(values=StringUtils.EMPTY)
	private String transAuthorName;
	
	/** The trans designation. */
	@Inject @Default(values=StringUtils.EMPTY)
	private String transDesignation;
	
	/** The trans quote. */
	@Inject @Default(values=StringUtils.EMPTY)
	private String transQuote;
	
	/**
	 * Gets the trans author name.
	 *
	 * @return transAuthorName
	 */
	public String getTransAuthorName() {
		transAuthorName= transAuthorName.trim();
		return transAuthorName;
	}
	
	/**
	 * Gets the trans designation.
	 *
	 * @return transDesignation
	 */
	public String getTransDesignation() {
		transDesignation= transDesignation.trim();
		return transDesignation;
	}
	
	/**
	 * Gets the trans quote.
	 *
	 * @return quote
	 */
	public String getTransQuote() {
		return transQuote;
	}

}
