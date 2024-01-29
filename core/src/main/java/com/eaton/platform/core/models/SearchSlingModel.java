package com.eaton.platform.core.models;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;

import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.injectors.annotations.FacetURLBeanServiceResponseInjector;
import java.util.Optional;

import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.acs.commons.util.ModeUtil;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.search.SearchModel;
import com.eaton.platform.core.util.CommonUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * The Class SearchSlingModel.
 */
@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchSlingModel extends EatonBaseSlingModel {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchSlingModel.class);

	/** The Constant SEARCH_HEADER_SELECTOR. */
	private static final String SEARCH_HEADER_SELECTOR = "search-header";

	/** The Constant SEARCH_HEADER_MOBILE_SELECTOR. */
	private static final String SEARCH_HEADER_MOBILE_SELECTOR = "search-mobile";

	/** The Constant SEARCH_HEADER_BAR_SELECTOR. */
	private static final String SEARCH_HEADER_BAR_SELECTOR = "search-bar";

	/** The Constant SEARCH_HEADER_COMPONENT. */
	private static final String SEARCH_HEADER_COMPONENT = "/jcr:content/root/header/search-comp";

	@Inject
	AuthorizationService authorizationService;

	/** The search model. */
	private SearchModel searchModel;

	private String predectiveSearchInputJSONString;
	@EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;
	private String searchResultPageUrl;
	private String crossRefSearchResultsURL;
	private String searchTerm;
	private boolean isSearchTermPresent;

	@FacetURLBeanServiceResponseInjector
	private FacetURLBeanServiceResponse facetURLBeanServiceResponse;
	String selector = null;

	@PostConstruct
	public void init() {
		LOGGER.debug("SearchSlingModel :: setComponentValues() :: Start");
		ProductGridSelectors productGridSelectors;
		Resource searchRes = null;

		isSearchTermPresent = false;
		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		selector = slingRequest.getRequestPathInfo().getSelectorString();
		productGridSelectors = null != facetURLBeanServiceResponse
				? facetURLBeanServiceResponse.getProductGridSelectors()
				: null;

		if (siteResourceSlingModel.isPresent()) {
			searchResultPageUrl = siteResourceSlingModel.get().getSearchResultsURL();
			crossRefSearchResultsURL = siteResourceSlingModel.get().getCrossRefSearchResultsURL();
			if (!ModeUtil.isAuthor()) {
				searchResultPageUrl = CommonUtil.dotHtmlLink(searchResultPageUrl, resourceResolver);
				crossRefSearchResultsURL = CommonUtil.dotHtmlLink(crossRefSearchResultsURL, resourceResolver);
			}
		}

		if ((null != selector && (StringUtils.equals(selector, SEARCH_HEADER_SELECTOR)
				|| StringUtils.equals(selector, SEARCH_HEADER_MOBILE_SELECTOR)
				|| StringUtils.equals(selector, SEARCH_HEADER_BAR_SELECTOR)))) {
			searchRes = resourceResolver.getResource(homePagePath.concat(SEARCH_HEADER_COMPONENT));
		} else {
			searchRes = resource;
		}

		if (null != searchRes) {
			searchModel = searchRes.adaptTo(SearchModel.class);
		}

		if ((productGridSelectors != null) && (productGridSelectors.getSearchTerm() != null)) {
			try {
				searchTerm = URLDecoder.decode(productGridSelectors.getSearchTerm(), CommonConstants.UTF_8);
				if (searchTerm.contains("&amp;")) {
					searchTerm = searchTerm.replace("&amp;", "&");
				}
				if (searchTerm.contains("::")) {
					searchTerm = searchTerm.replace("::", ".");
				}
			} catch (UnsupportedEncodingException e) {
				LOGGER.debug("SearchSlingModel :: Exception Occured :: Exit", e);
			}
			isSearchTermPresent = true;
		}

		populateLoadMoreJson();

		LOGGER.debug("SearchSlingModel :: setComponentValues() :: Exit");

	}

	/**
	 * Gets the search model.
	 *
	 * @return the search model
	 */
	public SearchModel getSearchModel() {
		return searchModel;
	}

	@SuppressWarnings("unchecked")
	public void populateLoadMoreJson() {

		try {

			String country;
			String searchResultsPageUrl = StringUtils.EMPTY;
			if (siteResourceSlingModel.isPresent()) {
				searchResultsPageUrl = siteResourceSlingModel.get().getSearchResultsURL();
			}
			JSONObject predectiveSearchInputJSON = new JSONObject();
			JSONArray jsonArray = new JSONArray();

			JSONObject languageJson = new JSONObject();
			JSONObject countryJson = new JSONObject();
			JSONObject viewType = new JSONObject();
			JSONObject searchResultsPageUrlJson = new JSONObject();

			if (searchResultsPageUrl != null) {
				searchResultsPageUrlJson.put("searchResultsPageUrl", searchResultsPageUrl);
			}

			Locale languageValue = currentPage.getLanguage(false);
			if (languageValue != null && StringUtils.isNotBlank(languageValue.getLanguage())) {
				String lang = languageValue.getLanguage();
				if (StringUtils.isNotBlank(languageValue.getCountry())) {
					country = languageValue.getCountry();
					languageJson.put("currentLanguage", lang + "_" + country);

					Page countryPage = currentPage.getAbsoluteParent(2);
					String countryString = countryPage.getName();
					if ((!country.equalsIgnoreCase(countryString))
							&& (!countryString.equals(CommonConstants.LANGUAGE_MASTERS_NODE_NAME))) {
						country = countryString.toUpperCase();
					}
					countryJson.put("currentCountry", country);
				}
			}
			viewType.put(CommonConstants.SEARCH_VIEW_TYPE,searchModel.getView());

			jsonArray.add(languageJson);
			jsonArray.add(countryJson);
			jsonArray.add(searchResultsPageUrlJson);
			jsonArray.add(viewType);

			predectiveSearchInputJSON.put("dataAttribute", jsonArray);
			predectiveSearchInputJSONString = predectiveSearchInputJSON.toJSONString();

		} catch (Exception e) {
			LOGGER.error("Exception in creating parameter for Load More Servlet: {}", e);
		}
	}

	public String getPredectiveSearchInputJSONString() {
		return predectiveSearchInputJSONString;
	}

	public void setPredectiveSearchInputJSONString(String predectiveSearchInputJSONString) {
		this.predectiveSearchInputJSONString = predectiveSearchInputJSONString;
	}

	public String getSearchResultPageUrl() {
		return searchResultPageUrl;
	}

	public void setSearchResultPageUrl(String searchResultPageUrl) {
		this.searchResultPageUrl = searchResultPageUrl;
	}

	public String getCrossRefSearchResultsURL() {
		return crossRefSearchResultsURL;
	}

	public void setCrossRefSearchResultsURL(String crossRefSearchResultsURL) {
		this.crossRefSearchResultsURL = crossRefSearchResultsURL;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public boolean isSearchTermPresent() {
		return isSearchTermPresent;
	}

	public void setSearchTermPresent(boolean isSearchTermPresent) {
		this.isSearchTermPresent = isSearchTermPresent;
	}

	/**
	 * This method checks if the user is authenticated.
	 * Return '.uncached' selector if the user is authenticated.
	 * Return Empty if the user is not authenticated.
	 * @return
	 */
	public String isUnCached() {
		AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
		if(null != authenticationToken && null != authenticationToken.getUserProfile()){
			return CommonConstants.UN_CACHED_SELECTOR;
		}
		return StringUtils.EMPTY;
	}

}
