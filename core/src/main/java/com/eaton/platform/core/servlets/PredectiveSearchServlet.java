package com.eaton.platform.core.servlets;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.sitesearch.KeywordBean;
import com.eaton.platform.core.bean.sitesearch.Keywords;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.FilterUtil;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.factories.SecureFilterBeanFactory;
import com.eaton.platform.integration.endeca.bean.typeahead.TypeAheadSiteSearchResponse;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component(service = Servlet.class,
immediate = true,
property = {
	ServletConstants.SLING_SERVLET_METHODS_GET,
	ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/search/predective-search",
})
public class PredectiveSearchServlet extends SlingSafeMethodsServlet{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final String SEARCH = "search";
	private static final String COUNTRY_CONSTANT = "Country";
	private static final String CURRENT_LANG = "currentLanguage";
	private static final String CURRENT_COUNTRY = "currentCountry";
	private static final String SEARCH_RESULTS_PAGE_URL = "searchResultsPageUrl";
	private static final String SEARCH_VIEW_TYPE_ADV_SEARCH = "advsearch";

	private String language;
	private String country;

	String jsonResponse;
	private static final Logger LOG = LoggerFactory.getLogger(PredectiveSearchServlet.class);

	/** The endeca service. */
	@Reference
	private transient EndecaService endecaService;

	/** The endeca config service. */
	@Reference
	private transient EndecaConfig endecaConfigService;

	//Inject a Sling ResourceResolverFactory
	@Reference
	private transient ResourceResolverFactory resolverFactory;

	@Reference
	private AuthorizationService authorizationService;

	@Reference
	private AuthenticationServiceConfiguration authenticationServiceConfig;

	@Reference
	private transient AdminService adminService;

	@Reference
	private transient EatonConfigService configService;
	String searchTerm;
	private String refererURL;
	private String viewType;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

		LOG.info("******** Predictive Search Servlet execution started ***********");
		response.setContentType("text/html;charset=UTF-8");
		String predictiveSearchInputJsonString = request.getParameter("url");
		searchTerm = request.getParameter("searchTerm");
		JSONParser parser = new JSONParser();
		JSONObject paramJson = new JSONObject();
		JSONArray valuesJSONarray = new JSONArray();
		String searchResultsPageUrl = StringUtils.EMPTY;

		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			refererURL = CommonUtil.getRefererURL(request);
			String pagePath = CommonUtil.getContentPath(adminResourceResolver, refererURL);
			if(predictiveSearchInputJsonString!=null){
				paramJson = (JSONObject) parser.parse(predictiveSearchInputJsonString);
			}

			if((paramJson!=null) && (paramJson.containsKey("dataAttribute"))){

				valuesJSONarray = (JSONArray) paramJson.get("dataAttribute");
				for(int i=0;i<valuesJSONarray.size();i++){
					JSONObject  innerJson = (JSONObject) valuesJSONarray.get(i);

					if(innerJson.containsKey(CURRENT_LANG)  && innerJson.get(CURRENT_LANG) != null){
						language = innerJson.get(CURRENT_LANG).toString();
					}else if(innerJson.containsKey(CURRENT_COUNTRY) && innerJson.get(CURRENT_COUNTRY) != null){
						country = innerJson.get(CURRENT_COUNTRY).toString();
					}else if(innerJson.containsKey(SEARCH_RESULTS_PAGE_URL) && innerJson.get(SEARCH_RESULTS_PAGE_URL) != null){
						searchResultsPageUrl = innerJson.get(SEARCH_RESULTS_PAGE_URL).toString();
					} else if(innerJson.containsKey(CommonConstants.SEARCH_VIEW_TYPE) && innerJson.get(CommonConstants.SEARCH_VIEW_TYPE) != null){
						viewType = innerJson.get(CommonConstants.SEARCH_VIEW_TYPE).toString();
					}
				}
			}

			Page currentPage = adminResourceResolver.resolve(pagePath).adaptTo(Page.class);
			String sitePageTitle = CommonConstants.APPLICATION_ID_EATON;
			if(null != currentPage)
			{
				sitePageTitle = currentPage.getAbsoluteParent(1).getName();
			}

			EndecaServiceRequestBean endecaServiceRequestBean = populateEndecaServiceRequestBean(sitePageTitle,pagePath, request);
			TypeAheadSiteSearchResponse predectiveSearchResponse = endecaService.getSearchKeywords(endecaServiceRequestBean);

			jsonResponse = StringUtils.EMPTY;
			Keywords keywords;
			if((predectiveSearchResponse!=null) && (predectiveSearchResponse.getTypeAheadResponse()!=null) && (predectiveSearchResponse.getTypeAheadResponse().getResultList()!=null)){
				if(searchResultsPageUrl!=null){
					searchResultsPageUrl = CommonUtil.dotHtmlLink(searchResultsPageUrl,adminResourceResolver);
					keywords  = setKeywords(predectiveSearchResponse.getTypeAheadResponse().getResultList(),searchResultsPageUrl,request);
				} else{
					keywords  = setKeywords(predectiveSearchResponse.getTypeAheadResponse().getResultList(),"",request);
				}
				jsonResponse = convertKeywordsTOJSON(keywords);
			}

			response.getWriter().println(jsonResponse);
		}catch(Exception e){
			LOG.error("Exception", e);
		}
	}

	public Keywords setKeywords(List<KeywordBean> keywordList, String searchResultsPageUrl, SlingHttpServletRequest request) {
		Keywords keywords = new Keywords();

		try {
			List<KeywordBean> keywordBeanList = new ArrayList<> ();
			for (KeywordBean keywordBean : keywordList) {
				if (keywordBean != null) {
					String encodedSearchTerm = CommonUtil.encodeSearchTermString(keywordBean.getTitle());
					if(viewType !=null && viewType.equals(SEARCH_VIEW_TYPE_ADV_SEARCH)){
						keywordBean.setLink("#");
					}else{
						keywordBean.setLink(searchResultsPageUrl + ".searchTerm$" + encodedSearchTerm + ".tabs$all"+appendUnCachedSelectorIfAuthenticated(request)+".html");

					}
				}
				keywordBeanList.add(keywordBean);
			}
			keywords.setResults(keywordBeanList);

		} catch(Exception e) {
			LOG.error("Exception occured in setskuListLoadMoreValues():", e);
		}

		return keywords;
	}

	public String convertKeywordsTOJSON(Keywords keywords) throws IOException {
		LOG.info("Entry into convertKeywordsTOJSON method");
		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest;

		try {
			jsonRequest = mapper.writeValueAsString(keywords);
		} catch(JsonGenerationException e) {
			throw new JsonGenerationException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch(JsonMappingException e) {
			throw new JsonMappingException(EndecaConstants.INVALID_ENTRY_MSG, e);
		}

		if (LOG.isDebugEnabled())
			LOG.debug("JSON request {}", jsonRequest);

		LOG.info("Exit from convertKeywordsTOJSON method");

		return jsonRequest;
	}

	public EndecaServiceRequestBean populateEndecaServiceRequestBean(final String sitePageTitle, final String pagePath, SlingHttpServletRequest request) {

		EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
		EndecaConfigServiceBean endecaConfigBean = null;

		if (endecaConfigService != null) {
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
		}
		// Setting common attributes of Endeca Request Bean
		if (endecaConfigBean != null) {
			endecaServiceRequestBean.setSearchApplicationKey(endecaConfigBean.getEspAppKey());
		}

		endecaServiceRequestBean.setFunction(SEARCH);
		endecaServiceRequestBean.setLanguage(language);
		populateSiteSearchPageEndecaRequestBean(endecaServiceRequestBean, sitePageTitle,pagePath, request);

		return endecaServiceRequestBean;
	}

	public void populateSiteSearchPageEndecaRequestBean(EndecaServiceRequestBean endecaServiceRequestBean,
			final String sitePageTitle, final String pagePath, SlingHttpServletRequest request) {


		endecaServiceRequestBean.setSearchApplication(EndecaConstants.NON_SECURE_APP_ID);

		if(searchTerm!=null){
			searchTerm = CommonUtil.decodeSearchTermString(searchTerm);
			endecaServiceRequestBean.setSearchTerms(searchTerm);
		}

		endecaServiceRequestBean.setStartingRecordNumber("0");
		endecaServiceRequestBean.setNumberOfRecordsToReturn("5");

		List<FilterBean> filters = new ArrayList<>();

		if ((country != null) && (!country.isEmpty())) {
			filters.add(FilterUtil.getFilterBean(COUNTRY_CONSTANT, country));
		}

		String applicationId = CommonUtil.getApplicationId(pagePath);

		if(sitePageTitle.equalsIgnoreCase(CommonConstants.EXTERNALIZER_DOMAIN_EATON_CUMMINS))
		{
			filters.add(FilterUtil.getFilterBean(EndecaConstants.APPLICATION_ID, CommonConstants.APPLICATION_ID_ECJV));
		}
		else if (applicationId.equals(CommonConstants.APPLICATION_ID_PHOENIXTEC) || applicationId.equals(CommonConstants.APPLICATION_ID_ECJV)) {
			filters.add(FilterUtil.getFilterBean(EndecaConstants.APPLICATION_ID, applicationId));
		}
		else if(refererURL.contains(CommonConstants.APPLICATION_ID_ECJV)) {
			filters.add(FilterUtil.getFilterBean(EndecaConstants.APPLICATION_ID, CommonConstants.APPLICATION_ID_ECJV));
		}

		// EAT-4643 Secure Search - Secure Search - Type Ahead
		endecaServiceRequestBean.setSearchApplication(SecureModule.SECURETYPEAHEAD.getAppId());
		filters.addAll(new SecureFilterBeanFactory()
				.createFilterBeans(authorizationService.getTokenFromSlingRequest(request),SecureModule.SECURETYPEAHEAD));
		endecaServiceRequestBean.setFilters(filters);
	}

	private String appendUnCachedSelectorIfAuthenticated(SlingHttpServletRequest request){
		if(null != authorizationService.getTokenFromSlingRequest(request)){
			return CommonConstants.UN_CACHED_SELECTOR;
		}
		return StringUtils.EMPTY;
	}

}
