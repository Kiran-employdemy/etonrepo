package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchModel {
	
	/** The view. */
	@Inject
	private String view;
    
    /** The trans field aid. */
    @Inject
    private String transFieldAid;
    
    /** The trans search header. */
    @Inject
    private String transSearchHeader;
    
    /** The trans subhead. */
    @Inject
    private String transSubhead;
    
    /** The search resutls path. */
    @Inject
    private String searchResutlsPath;
    
    /** The relevant links. */
    @Inject
    private Resource relevantLinks;

	/**
	 * Gets the view.
	 *
	 * @return view
	 */
	public String getView() {
		return view;
	}
	
	/**
	 * Gets the relevant links.
	 *
	 * @return relevantLinks
	 */
	public Resource getRelevantLinks() {
		return relevantLinks;
	}
	
	/**
	 * Gets the trans field aid.
	 *
	 * @return transFieldAid
	 */
	public String getTransFieldAid() {
		return transFieldAid;
	}
	
	/**
	 * Gets the trans search header.
	 *
	 * @return transSearchHeader
	 */
	public String getTransSearchHeader() {
		return transSearchHeader;
	}
	
	/**
	 * Gets the trans subhead.
	 *
	 * @return transSubhead
	 */
	public String getTransSubhead() {
		return transSubhead;
	}
	
	/**
	 * Gets the search resutls path.
	 *
	 * @return searchResutlsPath
	 */
	public String getSearchResutlsPath() {
		return searchResutlsPath;
	}
	
	/**
	 * Sets the view.
	 *
	 * @param view the new view
	 */
	public void setView(String view) {
		this.view = view;
	}
	
	/**
	 * Sets the trans field aid.
	 *
	 * @param transFieldAid the new trans field aid
	 */
	public void setTransFieldAid(String transFieldAid) {
		this.transFieldAid = transFieldAid;
	}
	
	/**
	 * Sets the trans search header.
	 *
	 * @param transSearchHeader the new trans search header
	 */
	public void setTransSearchHeader(String transSearchHeader) {
		this.transSearchHeader = transSearchHeader;
	}
	
	/**
	 * Sets the trans subhead.
	 *
	 * @param transSubhead the new trans subhead
	 */
	public void setTransSubhead(String transSubhead) {
		this.transSubhead = transSubhead;
	}
	
	/**
	 * Sets the search resutls path.
	 *
	 * @param searchResutlsPath the new search resutls path
	 */
	public void setSearchResutlsPath(String searchResutlsPath) {
		this.searchResutlsPath = searchResutlsPath;
	}
	
	/**
	 * Sets the relevant links.
	 *
	 * @param relevantLinks the new relevant links
	 */
	public void setRelevantLinks(Resource relevantLinks) {
		this.relevantLinks = relevantLinks;
	}
}
