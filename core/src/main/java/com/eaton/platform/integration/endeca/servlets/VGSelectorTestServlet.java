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
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueSelectorResponse;
import com.eaton.platform.integration.endeca.services.EndecaService;

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/VGSelector",
		})
public class VGSelectorTestServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8616330297710201811L;
	
	private static final Logger LOG = LoggerFactory.getLogger(EndecaTestServlet.class);

	private static final String CLUTCH_TOOL = "clutchTool";
	private static final String TORQUE_TOOL = "torqueTool";
	
	/** The endeca service. */
	@Reference
	private transient EndecaService endecaService;
	
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
			
			if (Arrays.asList(selectors).contains(CLUTCH_TOOL)) {
				endecaServiceRequestBean.setSearchApplication("etnvgselclutch");
				endecaServiceRequestBean.setSearchApplicationKey("abc123");
				endecaServiceRequestBean.setFunction("search");
				endecaServiceRequestBean.setSearchTerms("RESULT");
				endecaServiceRequestBean.setLanguage("fr_FR");
				endecaServiceRequestBean.setStartingRecordNumber("0");
				endecaServiceRequestBean.setNumberOfRecordsToReturn("10");
				
				setFilters(endecaServiceRequestBean,CLUTCH_TOOL);
				
				ClutchSelectorResponse clutchResponse = endecaService.getClutchToolDetails(endecaServiceRequestBean);
				status = clutchResponse.getClutchToolResponse().getStatus();
			}
			if (Arrays.asList(selectors).contains(TORQUE_TOOL)) {
				endecaServiceRequestBean.setSearchApplication("etnvgseltorque");
				endecaServiceRequestBean.setSearchApplicationKey("abc123");
				endecaServiceRequestBean.setFunction("search");
				endecaServiceRequestBean.setSearchTerms("RESULT");
				endecaServiceRequestBean.setLanguage("fr_FR");
				endecaServiceRequestBean.setStartingRecordNumber("0");
				endecaServiceRequestBean.setNumberOfRecordsToReturn("10");
				
				setFilters(endecaServiceRequestBean,TORQUE_TOOL);
				
				TorqueSelectorResponse torqueResponse = endecaService.getTorqueToolDetails(endecaServiceRequestBean);
				status = torqueResponse.getTorqueToolResponse().getStatus();
			}
		}
		
		response.getOutputStream().println(".Status :" + status);
}
	
	private void setFilters(EndecaServiceRequestBean endecaServiceRequestBean,String selector) {

		List<FilterBean> filters = new ArrayList<>();

		FilterBean countryBean = new FilterBean();
		countryBean.setFilterName("Country");
		List<String> countryValues = new ArrayList<>();
		countryValues.add("CA");
		countryBean.setFilterValues(countryValues);
		filters.add(countryBean);

		FilterBean facetsBean = new FilterBean();
		facetsBean.setFilterName("Facets");
		List<String> facetValue = new ArrayList<>();
		if(selector.equalsIgnoreCase(CLUTCH_TOOL)){
			facetValue.add("80807571");
			facetValue.add("2251047735");
		}
		else if(selector.equalsIgnoreCase(TORQUE_TOOL)){
			facetValue.add("2561133431");
			facetValue.add("2380976062");
			facetValue.add("1634292276");
		}
		facetsBean.setFilterValues(facetValue);
		filters.add(facetsBean);
		
		FilterBean returnFacets = new FilterBean();
		returnFacets.setFilterName("ReturnFacetsFor");
		List<String> returnFacetValues = new ArrayList<>();
		if(selector.equalsIgnoreCase(CLUTCH_TOOL)){
			returnFacetValues.add("Hydraulic_Linkage");
			returnFacetValues.add("Clutch_Size");
			returnFacetValues.add("Damper_Spring_Count");
			returnFacetValues.add("Torque_Capactity");
			returnFacetValues.add("Portfolio_Rating");
			returnFacetValues.add("Input_Shaft_Type");
			returnFacetValues.add("Torsional_Spring_Rate");
			returnFacetValues.add("Pre_Damper");
			returnFacetValues.add("Transmission_Type");
			returnFacetValues.add("Wear_Through_Self_Adjust");
		}else if(selector.equalsIgnoreCase(TORQUE_TOOL)){
			returnFacetValues.add("Model");
			returnFacetValues.add("Make");
			returnFacetValues.add("Differential_Type");
			returnFacetValues.add("Gear_Ratio");
			returnFacetValues.add("Year");
			returnFacetValues.add("Vehicle_Usage");
			returnFacetValues.add("Sub_Brand");
		}
		returnFacets.setFilterValues(returnFacetValues);
		filters.add(returnFacets);

		if(selector.equalsIgnoreCase(TORQUE_TOOL)){
			FilterBean sortBy = new FilterBean();
			sortBy.setFilterName("SortBy");
			List<String> sortValue = new ArrayList<>();
			sortValue.add("desc");
			sortBy.setFilterValues(sortValue);
			filters.add(sortBy);
		}
		FilterBean skuCardParameters = new FilterBean();
		skuCardParameters.setFilterName("SKUCardParameters");
		List<String> skuCardValues = new ArrayList<>();
		if(selector.equalsIgnoreCase(CLUTCH_TOOL)){
			skuCardValues.add("Description");
			skuCardValues.add("Torque_Capacity");
			skuCardValues.add("Clutch_Size");
		}else if (selector.equalsIgnoreCase(TORQUE_TOOL)){
			skuCardValues.add("Model_Axle_Location");
			skuCardValues.add("Vehicle_Usage_Attr");
			skuCardValues.add("Model_Product_Notes");
		}
		skuCardParameters.setFilterValues(skuCardValues);
		filters.add(skuCardParameters);


		endecaServiceRequestBean.setFilters(filters);

	}

}
