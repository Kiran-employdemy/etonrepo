package com.eaton.platform.core.models.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This class is used to inject the dialog
 * properties.</html> .
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

	/** The trans search header. */
	@Inject
	private String transSearchHeader;

	/** The trans field aid. */
	@Inject
	private String transFieldAid;

	/** The trans subhead. */
	@Inject
	private String transSubhead;

	/** The search icon. */
	@Inject
	private String searchIcon;

	/** The search resutls path. */
	@Inject
	private String searchResutlsPath;

	/** The relevant links. */
	@Inject
	private Resource relevantLinks;

	/** The trans search key. */
	@Inject
	private String transSearchKey;

	/** The links. */
	public List<SearchRelevantLinksModel> links;

	@PostConstruct
	protected void init() {
		this.links = new ArrayList<SearchRelevantLinksModel>();
		if (null != this.relevantLinks) {
			populateModel(this.links, this.relevantLinks);
		}
	}

	/**
	 * Populate model.
	 *
	 * @param relevantLinksMultiField
	 *            the array
	 * @param resource
	 *            the resource
	 * @return social links
	 */
	public List<SearchRelevantLinksModel> populateModel(List<SearchRelevantLinksModel> relevantLinksMultiField,
			Resource resource) {
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				SearchRelevantLinksModel searchRelevantLinks = linkResources.next()
						.adaptTo(SearchRelevantLinksModel.class);
				if (searchRelevantLinks != null) {
					relevantLinksMultiField.add(searchRelevantLinks);
				}
			}
		}
		return relevantLinksMultiField;
	}

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
		return CommonUtil.dotHtmlLink(this.searchResutlsPath);
	}

	/**
	 * Gets the search icon.
	 *
	 * @return the search icon
	 */
	public String getSearchIcon() {
		return searchIcon;
	}

	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if  ((null != this.searchResutlsPath) && (StringUtils.startsWith(this.searchResutlsPath, CommonConstants.HTTP)
				|| StringUtils.startsWith(this.searchResutlsPath, CommonConstants.HTTPS)
				|| StringUtils.startsWith(this.searchResutlsPath, CommonConstants.WWW))) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}

	/**
	 * Gets the trans search key.
	 *
	 * @return the trans search key
	 */
	public String getTransSearchKey() {
		return transSearchKey;
	}

	public void setSearchResutlsPath(String searchResutlsPath) {
		this.searchResutlsPath = searchResutlsPath;
	}
	
	
}
