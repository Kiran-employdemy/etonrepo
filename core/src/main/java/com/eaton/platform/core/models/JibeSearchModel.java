package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.Source;
import com.day.cq.wcm.api.Page;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.search.SearchRelevantLinksModel;
import com.eaton.platform.core.util.CommonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <html> Description: This class is used to inject the dialog
 * properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class JibeSearchModel {

	@Inject
	private Page currentPage;	
	
	@Inject @Source("sling-object")
	private SlingHttpServletRequest slingRequest;
	
	@Inject @Source("sling-object") 
	private ResourceResolver resourceResolver;
	
	@Inject @Via("resource")
	private Resource res;
	
	/** The view. */
	@Inject @Via("resource")
	private String view;

	/** The trans search header. */
	@Inject @Via("resource")
	private String transSearchHeader;

	/** The trans Location search header. */
	@Inject @Via("resource")
	private String transLocationSearchHeader;

	/** The trans field aid. */
	@Inject @Via("resource")
	private String transFieldAid;

	/** The trans Location field aid. */
	@Inject @Via("resource")
	private String transLocationFieldAid;

	/** The trans subhead. */
	@Inject @Via("resource")
	private String transSubhead;

	/** The search icon. */
	@Inject @Via("resource")
	private String searchIcon;

	/** The trans search key. */
	@Inject @Via("resource")
	private String transSearchKey;
	
	/** The search resutls path. */
	@Inject @Via("resource")
	private String searchResutlsPath;

	/** The relevant links. */
	@Inject @Via("resource")
	private Resource relevantLinks;


	/** The links. */
	public List<SearchRelevantLinksModel> links;
	
	private static final Logger LOG = LoggerFactory.getLogger(JibeSearchModel.class);
	private JibeSearchModel searchModel;
	
		/* The search box name */
		
	private String jibeSearchBoxName = StringUtils.EMPTY;
	
	private static final String SEARCH_HEADER_SELECTOR = "search-header";
    private static final String SEARCH_HEADER_MOBILE_SELECTOR ="search-mobile";
	private static final String SEARCH_HEADER_BAR_SELECTOR ="search-bar";
	private static final String SEARCH_HEADER_COMPONENT="/jcr:content/root/header/search-comp";
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.debug("SearchHelper :: setComponentValues() :: Start");
		this.links = new ArrayList<SearchRelevantLinksModel>();
		Resource searchRes = null;
		String selector = null;
		
		
		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		selector = slingRequest.getRequestPathInfo().getSelectorString();
		final String resourcePath = slingRequest.getRequestPathInfo().getResourcePath().toString();
		if (resourcePath.substring(resourcePath.lastIndexOf(CommonConstants.SLASH_STRING)+1).startsWith(CommonConstants.SEARCH_BOX_TEXT)){
			jibeSearchBoxName = resourcePath.substring(resourcePath.lastIndexOf(CommonConstants.SLASH_STRING)+1);
		}
		if((null != selector && ( StringUtils.equals(selector, SEARCH_HEADER_SELECTOR) || StringUtils.equals(selector, SEARCH_HEADER_MOBILE_SELECTOR) || StringUtils.equals(selector, SEARCH_HEADER_BAR_SELECTOR)))){
			searchRes = resourceResolver.getResource(homePagePath.concat(SEARCH_HEADER_COMPONENT));
		} else {
			searchRes = res;
		}
		
		if(null != searchRes) {
			searchModel = searchRes.adaptTo(JibeSearchModel.class);
		}
		if (null != this.relevantLinks) {
			populateModel(this.links, this.relevantLinks);
		}
		
		LOG.debug("SearchHelper :: setComponentValues() :: Exit");
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
	 * Gets the trans Location field aid.
	 *
	 * @return transLocationFieldAid
	 */
	public String getTransLocationFieldAid() {
		return transLocationFieldAid;
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
	 * Gets the trans Location search header.
	 *
	 * @return transLocationSearchHeader
	 */
	public String getTransLocationSearchHeader() {
		return transLocationSearchHeader;
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
		return CommonUtil.dotHtmlLink(this.searchResutlsPath, this.resourceResolver);
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
	 * Gets the trans search key.
	 *
	 * @return the trans search key
	 */
	public String getTransSearchKey() {
		return transSearchKey;
	}

	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if (null != this.searchResutlsPath) {
			if (StringUtils.startsWith(this.searchResutlsPath, CommonConstants.HTTP)
					|| StringUtils.startsWith(this.searchResutlsPath, CommonConstants.HTTPS)
					|| StringUtils.startsWith(this.searchResutlsPath, CommonConstants.WWW)) {
				isExternal = CommonConstants.TRUE;
			}
		}
		return isExternal;
	}

}
