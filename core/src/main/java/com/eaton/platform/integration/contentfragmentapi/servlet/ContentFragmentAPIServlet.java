package com.eaton.platform.integration.contentfragmentapi.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.JcrQueryConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.contentfragmentapi.config.ContentFragmentServiceConfig;
import com.eaton.platform.integration.contentfragmentapi.models.ContentFragmentModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This Servlet is a generic service implementation to expose any content
 * fragments data in a JSON format. Servlet expects request param <b>api</b>
 * that identifies the API.
 * 
 * Configuration: ContentFragmentService Osgi Configuration expects two
 * properties based on the api value. 1. cfdam-{api-value} : Defines the
 * location of the API DAM structure. 2. cfmodel-{api-value} : Defines the model
 * of the content fragment being exposed as API, this content fragment can refer
 * any other type of content fragments.
 * 
 * Additional search operations: 'locale' - can be passed as request param to
 * qualify the DAM location with the locale to the base DAM path of the API
 * 'search' - can pass any text to search specific content
 * 
 * Servlet can accept additional request param's matching the content fragment
 * properties to further refine the content fragment search.
 * 
 * Response: HTTP 200 - JSON {List of Content fragments based on the search
 * results} HTTP 400 - Invalid request - No API request param HTTP 500 -
 * Unexpected Error HTTP 204 - No Content, when no results found HTTP 501 - Not
 * implemented, when API configuration doesn't exist
 * 
 * @author E0527858
 * 
 */
@Component(service = Servlet.class,
		immediate = true,
		property = { 
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/api/contentfragment",
				ServletConstants.SLING_SERVLET_EXTENSION_JSON 
		})

@Designate(ocd = ContentFragmentServiceConfig.class)
@ServiceDescription("Content Fragment API Service")
public class ContentFragmentAPIServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = -7440464424178252272L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentFragmentAPIServlet.class);
	private static final String SEARCH = "search";
	private static final String API = "api";
	private static final String LOCALE = "locale";
	private static final List<String> configParams = Arrays.asList(LOCALE, SEARCH);
	private Map<String, String> contentFragmentConfigMap;

	private static final String CONFIG_CONTENT_DAM_PREFIX = "cfdam-";
	private static final String CONFIG_CONTENT_API_MODEL_PREFIX = "cfmodel-";

	@Activate
	protected void activate(final ContentFragmentServiceConfig config) {
		if (contentFragmentConfigMap == null) {
			contentFragmentConfigMap = new HashMap<>();
			Arrays.stream(config.contentfragment_service_config()).forEach(e -> {
				String[] kv = e.split(config.config_delimiter());
				contentFragmentConfigMap.put(kv[0], kv[1]);
			});
		}
	}

	/**
	 * HTTP GET Request Implementation
	 */
	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {

		final QueryBuilder queryBuilder = req.getResourceResolver().adaptTo(QueryBuilder.class);
		final Map<String, String> map = new HashMap<>(0);
		String apiContentDAMPathConfigParam = null;
		String contentFragmentDAMLocation = null;
		String apiContentFragmentModelConfigParam = null;
		String contentFragmentModel = null;

		final String locale = req.getParameter(LOCALE);
		String apiName = req.getParameter(API);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("ContentFragmentAPIServlet:doGet: locale:%s,apiName:%s", locale, apiName));
		}

		if (StringUtils.isNotBlank(apiName)) {
			apiContentDAMPathConfigParam = CONFIG_CONTENT_DAM_PREFIX.concat(apiName);
			contentFragmentDAMLocation = contentFragmentConfigMap.get(apiContentDAMPathConfigParam);

			apiContentFragmentModelConfigParam = CONFIG_CONTENT_API_MODEL_PREFIX.concat(apiName);
			contentFragmentModel = contentFragmentConfigMap.get(apiContentFragmentModelConfigParam);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"ContentFragmentAPIServlet:doGet:API configuration, contentFragmentModel:%s, contentFragmentDAMLocation:%s",
						contentFragmentModel, contentFragmentDAMLocation));
			}

			if (StringUtils.isNotBlank(contentFragmentModel) && StringUtils.isNotBlank(contentFragmentDAMLocation)) {

				// search for content fragments only
				map.put("boolproperty", CommonConstants.JCR_CONTENT_CONTENT_FRAGMENT);
				map.put("boolproperty.value", Boolean.TRUE.toString());

				// add locale to the assets DAM location
				if (StringUtils.isNotBlank(locale)) {
					contentFragmentDAMLocation = contentFragmentDAMLocation.concat(locale);
				}

				// search only in the configured DAM location
				map.put(JcrQueryConstants.PROP_TYPE, com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
				map.put(JcrQueryConstants.PROP_PATH, contentFragmentDAMLocation);

				// search for content fragments with configured model
				map.put(JcrQueryConstants.ZERO_PREFIX_PROPERTY, CommonConstants.JCR_CONTENT_DATA_MODEL);
				map.put(JcrQueryConstants.ZERO_PREFIX_PROPERTY_VALUE, contentFragmentModel);

				// search based on all request parameters - exclusively api & version attributes
				// (to filter the content)
				int paramCount = 1;
				for (final String key : req.getParameterMap().keySet()) {
					if (!configParams.contains(key)) {
						paramCount++;
						map.put(String.valueOf(paramCount).concat(JcrQueryConstants.UNDERSCORE_PROPERTY), CommonConstants.JCR_CONTENT_DATA_MASTER.concat(key));
						map.put(String.valueOf(paramCount).concat(JcrQueryConstants.PROPERTY_VALUE), req.getParameter(key));
					}
				}

				// do full text search if search is passed
				final String search = req.getParameter(SEARCH);
				if (StringUtils.isNotEmpty(search)) {
					map.put(JcrQueryConstants.FULL_TEXT, search);
					map.put(JcrQueryConstants.FULLTEXT_REL_PATH, CommonConstants.JCR_CONTENT_DATA_MASTER);
				}

				try {

					final Query query = queryBuilder.createQuery(PredicateGroup.create(map),
							req.getResourceResolver().adaptTo(Session.class));
					
					final SearchResult result = query.getResult();
					
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug(String.format("ContentFragmentAPIServlet Query executed: %s", result.getQueryStatement()));
					}
				    
					final List<ContentFragmentModel> apiResults = result.getHits().stream().map(hit -> {
						try {
							return req.getResourceResolver().resolve(hit.getPath()).adaptTo(ContentFragmentModel.class);
						} catch (RepositoryException e) {
							LOGGER.error("ContentFragmentAPIServlet- API Results contained unexpected resource, expecting a content fragment",e);
							return null;
						}
					}).filter(Objects::nonNull).collect(Collectors.toList());

					if (apiResults.isEmpty()) {
						writeErrorResponse(resp, HttpStatus.SC_NO_CONTENT, "No Content Found.");
					} else {
						writeResponse(resp, HttpStatus.SC_OK, new ObjectMapper().writeValueAsString(apiResults));
					}
				} catch (JsonProcessingException e) {
					LOGGER.error("ContentFragmentAPIServlet- Error generating JSON from apiResults",e);
					writeErrorResponse(resp, HttpStatus.SC_INTERNAL_SERVER_ERROR,
							"ContentFragmentAPIServlet- Unexpected error, " + e.getMessage());
				}
			} else {
				writeErrorResponse(resp, HttpStatus.SC_NOT_IMPLEMENTED, "API is not configured/implemented.");
			}
		} else {
			writeErrorResponse(resp, HttpStatus.SC_BAD_REQUEST, "API ID is required.");
		}
	}

	/**
	 * Write JSON Response
	 * 
	 * @param resp
	 * @param httpStatus
	 * @param json
	 */
	private static void writeResponse(final SlingHttpServletResponse resp, int httpStatus, String json) {
		try {
			resp.setStatus(httpStatus);
			resp.setContentType(CommonConstants.APPLICATION_JSON);
			resp.getWriter().write(json);
			resp.flushBuffer();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("ContentFragmentAPIServlet:Response- Status: %s, JSON Payload:%s", httpStatus,
						json));
			}
		} catch (IOException e) {
			LOGGER.error("ContentFragmentAPIServlet:writeResponse-Error writing response to client.", e);
		}
	}

	/**
	 * 
	 * Write JSON Error Response
	 * 
	 * @param resp
	 * @param httpStatus
	 * @param errorMsg
	 */
	private static void writeErrorResponse(final SlingHttpServletResponse resp, int httpStatus, String errorMsg) {
		Map<String, Object> jsonParams = new HashMap<>();
		jsonParams.put("ERROR", errorMsg);
		try {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(String.format("ContentFragmentAPIServlet:Error Response- HTTP Status: %s, Error Message:%s",
								httpStatus, errorMsg));
			}
			writeResponse(resp, httpStatus, new ObjectMapper().writeValueAsString(jsonParams));
		} catch (JsonProcessingException e) {
			LOGGER.error("ContentFragmentAPIServlet:writeErrorResponse-Error writing error response to client.", e);
		}
	}
}
