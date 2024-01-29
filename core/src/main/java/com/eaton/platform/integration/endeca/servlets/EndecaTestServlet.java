package com.eaton.platform.integration.endeca.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.bean.typeahead.TypeAheadSiteSearchResponse;
import com.eaton.platform.integration.endeca.services.EndecaService;

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/Endeca",
		})
public class EndecaTestServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8826409808306493894L;

	private static final Logger LOG = LoggerFactory.getLogger(EndecaTestServlet.class);

	private static final String PRODUCT_FAMILY = "getProductFamily";
	private static final String SKU_LIST = "getSKUList";
	private static final String SKU_DETAILS = "getSKUDetails";
	private static final String TYPE_AHEAD = "getSearchKeywords";
	private static final String SITE_SEARCH = "getSearchResults";


	/** The endeca service. */
	@Reference
	private transient EndecaService endecaService;// SonarQube private or
													// transient issue

	/**
	 * Test Servlet to test the Endeca Service
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		LOG.info("******** EndecaTestServlet servlet execution started ***********");
		response.getOutputStream().println("Endeca Test Servlet Invoked.");

		EndecaServiceRequestBean endecaServiceRequestBean;

		String[] selectors = request.getRequestPathInfo().getSelectors();
		String status = null;
		

		response.setContentType("text/html");

		endecaServiceRequestBean = new EndecaServiceRequestBean();

		if (selectors.length != 0) {

			LOG.info("selector Value is ", selectors[0]);
			response.getOutputStream().println("\n \n selector Value: " + selectors[0]);

			if (Arrays.asList(selectors).contains(PRODUCT_FAMILY)) {

				endecaServiceRequestBean.setSearchApplication("");
				endecaServiceRequestBean.setSearchApplicationKey("abc123");
				endecaServiceRequestBean.setFunction("search");
				endecaServiceRequestBean.setSearchTerms("");
				endecaServiceRequestBean.setLanguage("");
				endecaServiceRequestBean.setStartingRecordNumber("0");
				endecaServiceRequestBean.setNumberOfRecordsToReturn("10");

				setFilters(endecaServiceRequestBean,PRODUCT_FAMILY);
				if (LOG.isInfoEnabled()) {
					LOG.info("Subcategory Request" + endecaServiceRequestBean);
				}
				// To fetch the product family list
				FamilyListResponseBean familyList = endecaService.getProductFamilyList(endecaServiceRequestBean);
				status = familyList.getResponse().getStatus();
			}
			if (Arrays.asList(selectors).contains(SKU_LIST)) {

				endecaServiceRequestBean.setSearchApplication("eatonpdh2");
				endecaServiceRequestBean.setSearchApplicationKey("abc123");
				endecaServiceRequestBean.setFunction("search");
				endecaServiceRequestBean.setSearchTerms("29178683");
				endecaServiceRequestBean.setLanguage("en_US");
				endecaServiceRequestBean.setStartingRecordNumber("0");
				endecaServiceRequestBean.setNumberOfRecordsToReturn("10");

				setFilters(endecaServiceRequestBean,SKU_LIST);

				List<FilterBean> filters = endecaServiceRequestBean.getFilters();
				FilterBean skuCardParameters = new FilterBean();
				skuCardParameters.setFilterName("SKUCardParameters");
				List<String> skuCardValues = new ArrayList<>();
				skuCardValues.add("Interrupt_Rating");
				skuCardValues.add("Trip_Type");
				skuCardValues.add("Amperage_Rating");
				skuCardParameters.setFilterValues(skuCardValues);
				filters.add(skuCardParameters);
				endecaServiceRequestBean.setFilters(filters);
				if (LOG.isInfoEnabled()) {
					LOG.info("SKU List Request" + endecaServiceRequestBean);
				}
				// To fetch the SKU list for Family Module
				SKUListResponseBean skuList = endecaService.getSKUList(endecaServiceRequestBean);
				status = skuList.getFamilyModuleResponse().getStatus();
			}
			if (Arrays.asList(selectors).contains(SKU_DETAILS)) {

				endecaServiceRequestBean.setSearchApplication("eatonpdh1");
				endecaServiceRequestBean.setSearchApplicationKey("abc123");
				endecaServiceRequestBean.setFunction("search");
				endecaServiceRequestBean.setSearchTerms("EGE7110FFG");
				endecaServiceRequestBean.setLanguage("en_US");

				List<FilterBean> filters = new ArrayList<>();

				FilterBean countryBean = new FilterBean();
				countryBean.setFilterName("Country");
				List<String> countryValues = new ArrayList<>();
				countryValues.add("US");
				countryBean.setFilterValues(countryValues);
				filters.add(countryBean);

				FilterBean maxRelationSKU = new FilterBean();
				maxRelationSKU.setFilterName("MaxRelationSKU");
				List<String> maxRelationValue = new ArrayList<>();
				maxRelationValue.add("3");
				maxRelationSKU.setFilterValues(maxRelationValue);
				filters.add(maxRelationSKU);
				endecaServiceRequestBean.setFilters(filters);
				if (LOG.isInfoEnabled()) {
					LOG.info("SKU Details Request" + endecaServiceRequestBean);
				}
				// To fetch the SKU Details
				SKUDetailsResponseBean skuDetails = endecaService.getSKUDetails(endecaServiceRequestBean);
				status = skuDetails.getSkuResponse().getStatus();
			}
			if (Arrays.asList(selectors).contains(TYPE_AHEAD)) {

				endecaServiceRequestBean.setSearchApplication("eatoncomtypeahead");
				endecaServiceRequestBean.setSearchApplicationKey("abc123");
				endecaServiceRequestBean.setFunction("search");
				endecaServiceRequestBean.setSearchTerms("eaton");
				endecaServiceRequestBean.setLanguage("en_US");
				endecaServiceRequestBean.setStartingRecordNumber("0");
				endecaServiceRequestBean.setNumberOfRecordsToReturn("10");

				List<FilterBean> filters = new ArrayList<>();

				FilterBean countryBean = new FilterBean();
				countryBean.setFilterName("Country");
				List<String> countryValues = new ArrayList<>();
				countryValues.add("US");
				countryBean.setFilterValues(countryValues);
				filters.add(countryBean);
				endecaServiceRequestBean.setFilters(filters);
				if (LOG.isInfoEnabled()) {
					LOG.info("TypeAhead Request" + endecaServiceRequestBean);
				}
				// To fetch the SKU Details
				TypeAheadSiteSearchResponse typeAheadSearchResponse = endecaService
						.getSearchKeywords(endecaServiceRequestBean);
				status = typeAheadSearchResponse.getTypeAheadResponse().getStatus();
			}
			if (Arrays.asList(selectors).contains(SITE_SEARCH)) {

				endecaServiceRequestBean.setSearchApplication("eatonsitesearch");
				endecaServiceRequestBean.setSearchApplicationKey("abc123");
				endecaServiceRequestBean.setFunction("search");
				endecaServiceRequestBean.setSearchTerms("eaton");
				endecaServiceRequestBean.setLanguage("en_US");
				endecaServiceRequestBean.setStartingRecordNumber("0");
				endecaServiceRequestBean.setNumberOfRecordsToReturn("10");

				setFilters(endecaServiceRequestBean,SITE_SEARCH);
				List<FilterBean> filters = endecaServiceRequestBean.getFilters();
				
				FilterBean autoCorrect = new FilterBean();
				autoCorrect.setFilterName("autoCorrect");
				List<String> autoCorrectValues = new ArrayList<>();
				autoCorrectValues.add("true");
				autoCorrect.setFilterValues(autoCorrectValues);
				filters.add(autoCorrect);
				endecaServiceRequestBean.setFilters(filters);
				if (LOG.isInfoEnabled()) {
					LOG.info("Siet Search Request" + endecaServiceRequestBean);
				}
				
				// To fetch the search results
				SiteSearchResponse sitesearchResponse = endecaService.getSearchResults(endecaServiceRequestBean, request, false);
				status = sitesearchResponse.getPageResponse().getStatus();
			}
		} else {
			LOG.info("There is no selectors mentioned in requested URL");
		}

		response.getOutputStream().println(".Status :" + status);

		LOG.info("******** EndecaTestServlet servlet execution ended ***********");

	}

	private void setFilters(EndecaServiceRequestBean endecaServiceRequestBean,String selector) {

		List<FilterBean> filters = new ArrayList<>();

		FilterBean countryBean = new FilterBean();
		countryBean.setFilterName("Country");
		List<String> countryValues = new ArrayList<>();
		countryValues.add("US");
		countryBean.setFilterValues(countryValues);
		filters.add(countryBean);

		FilterBean facetsBean = new FilterBean();
		facetsBean.setFilterName("Facets");
		List<String> facetValue = new ArrayList<>();
		if(selector.equalsIgnoreCase(PRODUCT_FAMILY))
			facetValue.add("");
		else if(selector.equalsIgnoreCase(SKU_LIST))
			facetValue.add("");
		else
			facetValue.add("");
		facetsBean.setFilterValues(facetValue);
		filters.add(facetsBean);
		
		FilterBean returnFacets = new FilterBean();
		returnFacets.setFilterName("ReturnFacetsFor");
		List<String> returnFacetValues = new ArrayList<>();
		if(selector.equalsIgnoreCase(PRODUCT_FAMILY)){
			returnFacetValues.add("Brand");
			returnFacetValues.add("Application");
			returnFacetValues.add("Topology");
		}else if(selector.equalsIgnoreCase(SITE_SEARCH)){
			returnFacetValues.add("File_Type");
			returnFacetValues.add("Content_Type");
		}else{
			returnFacetValues.add("Mounting");
			returnFacetValues.add("Brand");
		}
		returnFacets.setFilterValues(returnFacetValues);
		filters.add(returnFacets);

		FilterBean sortBy = new FilterBean();
		sortBy.setFilterName("SortBy");
		List<String> sortValue = new ArrayList<>();
		if(selector.equalsIgnoreCase(SITE_SEARCH))
			sortValue.add("relevance");
		else
			sortValue.add("desc");
		sortBy.setFilterValues(sortValue);
		filters.add(sortBy);


		endecaServiceRequestBean.setFilters(filters);

	}
}
