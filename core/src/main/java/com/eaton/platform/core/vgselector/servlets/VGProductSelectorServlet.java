package com.eaton.platform.core.vgselector.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.commons.lang3.StringUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.models.vgSelector.ConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.core.vgselector.utils.VGSelectorUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueSelectorResponse;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;

/**
 * <html> Description: This servlet is executed via AJAX call made
 *  on change of from drop-down option selection</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2018
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_POST,
				ServletConstants.SLING_SERVLET_PATHS + VGCommonConstants.SERVLET_PATH,
		})
public class VGProductSelectorServlet extends SlingAllMethodsServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8616330297710201811L;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(VGProductSelectorServlet.class);

	/** The config factory. */
	@Reference
	private transient ConfigurationManagerFactory configFactory;
	
	/** The endeca service. */
	@Reference
	private transient EndecaService endecaService;
	
	/** The endeca config service. */
	@Reference
	private transient EndecaConfig endecaConfigService;
	
	/** The admin service. */
	@Reference
	private transient AdminService adminService;
	
	/** The selector tool type. */
	private String selectorToolType;
	
	/** The country. */
	private String country;
	
	/** The endeca service request bean. */
	private EndecaServiceRequestBean endecaServiceRequestBean = null;
	
	/** The site resource sling model. */
	private ConfigModel siteResourceSlingModel = null;
	
	/** The page size. */
	private int pageSize;
	
	/**
	 * Test Servlet to test the Endeca Service.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.debug("VGProductSelectorServlet :: doPost() :: Started");
		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			List<String> returnFacets = new ArrayList<>();
			List<String> facetsValues = new ArrayList<>();
			String dropDownOptions = request.getParameter(VGCommonConstants.DROPDOWN_OPTIONS_AJAX);
			String searchTerm = request.getParameter(VGCommonConstants.SEARCH_TERM);
			if(null == searchTerm) {
                searchTerm = StringUtils.EMPTY;
            }
			// get admin resource resolver to resolve resource under /etc/cloudservices

			// get refererURL from request since current page is not available in fixed path servelts
			String refererURL = CommonUtil.getRefererURL(request);
			//get content path
			String resourcePath = CommonUtil.getContentPath(adminResourceResolver, refererURL);
			resourcePath = StringUtils.substringBefore(resourcePath, VGCommonConstants.JCR_CONTENT);

			Resource currentPageRes = adminResourceResolver.resolve(resourcePath);

			// get the configuration set in the Cloud Config Tab of the form page
			siteResourceSlingModel = VGSelectorUtil.populateSiteConfiguration(configFactory, adminResourceResolver, currentPageRes);
			selectorToolType = siteResourceSlingModel.getSelectorToolType();
			pageSize = siteResourceSlingModel.getPageSize();

			setFacetValues(dropDownOptions, returnFacets, facetsValues);

			// get Data from Endeca
			if (null != endecaService) {
                populateEndecaData(currentPageRes, adminResourceResolver, response, returnFacets, facetsValues);
            } else {
                LOGGER.error("Issue in getting Endeca Service instance");
            }

			LOGGER.debug("VGProductSelectorServlet :: doPost() :: Ended");
		}
	}

	/**
	 * Sets the facet values.
	 *
	 * @param dropDownOptions the drop down options
	 * @param returnFacets the return facets
	 * @param facetsValues the facets values
	 */
	private void setFacetValues(String dropDownOptions,
			List<String> returnFacets, List<String> facetsValues) {
		try {
	        // create the json array from String rules
	        JsonArray jsonRules = new JsonParser().parse(dropDownOptions).getAsJsonArray();
	        // iterate over the rules 
	        for (int i=0; i<jsonRules.size();i++){
	            JsonObject obj = jsonRules.get(i).getAsJsonObject();

	            String returnFacetForValue = obj.get(VGCommonConstants.FORM_ELEMENT_NAME).getAsString();
	            String facetValue = obj.get(VGCommonConstants.FORM_ELEMENT_VALUE).getAsString();
	            
	            if(StringUtils.isBlank(facetValue) || StringUtils.equalsIgnoreCase(selectorToolType,VGCommonConstants.TORQUE_PAGE)) {
	            	returnFacets.add(returnFacetForValue);
	            }
	            if(StringUtils.isNotBlank(facetValue)) {
	            	if(StringUtils.equals(VGCommonConstants.PORTFOLIO_RATING, returnFacetForValue)) {
	            		setPortfolioRatingFacetValue(facetsValues, facetValue);
	            	} else {
	            		facetsValues.add(facetValue);
	            	}
	            }
	        }
	        
	    } catch (Exception e){
	    	LOGGER.error("JSON Exception occured");
	    }
	}
	
	/* (non-Javadoc)
	 * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException, IOException {
		// Do nothing
	}

	/**
	 * Sets the portfolio rating facet value.
	 *
	 * @param facetsValues the facets values
	 * @param facetValue the facet value
	 */
	private void setPortfolioRatingFacetValue(List<String> facetsValues,
			String facetValue) {
		int selectedYear = Integer.parseInt(facetValue);
		Calendar now = Calendar.getInstance();   // Gets the current date and time
		int currentYear = now.get(Calendar.YEAR);       // The current year
		if(currentYear - selectedYear < 3) {
			facetsValues.add(siteResourceSlingModel.getBest());
		} else if(currentYear - selectedYear >= 3 && currentYear - selectedYear < 10) {
			facetsValues.add(siteResourceSlingModel.getBetter());
			facetsValues.add(siteResourceSlingModel.getBest());
		} else if(currentYear - selectedYear >= 10) {
			facetsValues.add(siteResourceSlingModel.getGood());
			facetsValues.add(siteResourceSlingModel.getBest());
			facetsValues.add(siteResourceSlingModel.getBetter());
		}
	}

	/**
	 * Populate endeca data.
	 *
	 * @param currentRes the current res
	 * @param resourceResolver the resource resolver
	 * @param response the response
	 * @param returnFacets the return facets
	 * @param facetsValues the facets values
	 */
	public void populateEndecaData(Resource currentRes, ResourceResolver resourceResolver, SlingHttpServletResponse response, List<String> returnFacets, List<String> facetsValues) {
		
		LOGGER.debug("VGProductSelectorServlet :: populateEndecaData() :: Start");
		try {
			
			endecaServiceRequestBean = new EndecaServiceRequestBean();
			
			// set the language in the endeca request
			setLanguageRequestBean(currentRes);
			
			List<String> skuCard = new ArrayList<>();
			if(skuCard.isEmpty()){
				skuCard.add(StringUtils.EMPTY);
			}
			if(returnFacets.isEmpty()){
				returnFacets.add(StringUtils.EMPTY);
			}
			String defaultSort = VGCommonConstants.ASCENDING_SORT;			
			// set Endeca request Bean and for page rendering component set search term to NO_RESULT
			endecaServiceRequestBean = VGSelectorUtil.populateEndecaServiceRequestBean(currentRes, resourceResolver, endecaConfigService, endecaServiceRequestBean, selectorToolType, "NO_RESULT", returnFacets, skuCard, facetsValues, country, 0, pageSize, defaultSort);

			LOGGER.debug("Endeca Request : {}", endecaServiceRequestBean);
			// Populate Clutch Tool Response
			if (StringUtils.equals(VGCommonConstants.CLUTCH_PAGE, selectorToolType)) {
				ClutchSelectorResponse clutchSelectorResponseBean = endecaService.getClutchToolDetails(endecaServiceRequestBean);
				LOGGER.debug(" Clutch Details Response from Endeca : {}", clutchSelectorResponseBean);
				
				response.setContentType(VGCommonConstants.APPLICATION_JSON);
			    response.getWriter().write(convertClutchBeanTOJSON(clutchSelectorResponseBean));
			    
			} else if(StringUtils.equals(VGCommonConstants.TORQUE_PAGE, selectorToolType)) {
				TorqueSelectorResponse torqueSelectorResponseBean = endecaService.getTorqueToolDetails(endecaServiceRequestBean);
				LOGGER.debug(" Torque Details Response from Endeca : {}", torqueSelectorResponseBean);

				response.setContentType(VGCommonConstants.APPLICATION_JSON);
			    response.getWriter().write(convertTorqueBeanTOJSON(torqueSelectorResponseBean));
			}
		} catch(Exception e){
			LOGGER.error("Exception while fetching response from endeca"+e.getMessage());
		}
		
		LOGGER.debug("VGProductSelectorHelper :: populateEndecaData() :: End");
	}
	
	/**
	 * Sets the language request bean.
	 *
	 * @param currentRes the new language request bean
	 */
	private void setLanguageRequestBean(Resource currentRes) {
//		 Setting Language Locale & country
		
		Page currentPage = currentRes.adaptTo(Page.class);
		if(null != currentPage) {
			Locale languageValue = currentPage.getLanguage(false);
			if (languageValue != null && ((languageValue.getLanguage() != null) && (!languageValue.getLanguage().equals(StringUtils.EMPTY)))) {
				if ((languageValue.getCountry() != null) && (!languageValue.getCountry().equals(StringUtils.EMPTY))) {
					country = languageValue.getCountry();
					endecaServiceRequestBean.setLanguage(CommonUtil.getUpdatedLocaleFromPagePath(currentPage));
					Page countryPage = currentPage.getAbsoluteParent(2);
					String countryString = countryPage.getName();
					if((!country.equalsIgnoreCase(countryString)) && (!countryString.equals(VGCommonConstants.LANGUAGE_MASTERS))){
						country = countryString.toUpperCase();
					}
				}
			}
		}
	}
	
	/**
	 * This method converts the request bean to JSON String using Jackson API
	 * parser.
	 *
	 * @param torqueSelectorResponse the torque selector response
	 * @return JSON String
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String convertTorqueBeanTOJSON(TorqueSelectorResponse torqueSelectorResponse) throws IOException {
		LOGGER.debug("Entry into convertTorqueBeanTOJSON method");
		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest;

		try {
			// Object to JSON in String using Jackson mapper
			jsonRequest = mapper.writeValueAsString(torqueSelectorResponse);

		} catch (JsonGenerationException e) {
			throw new JsonGenerationException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch (JsonMappingException e) {
			throw new JsonMappingException(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("JSON request :"+jsonRequest);
		LOGGER.debug("Exit from convertTorqueBeanTOJSON method");

		return jsonRequest;
	}
	
	/**
	 * This method converts the request bean to JSON String using Jackson API
	 * parser.
	 *
	 * @param clutchSelectorResponse the clutch selector response
	 * @return JSON String
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String convertClutchBeanTOJSON(ClutchSelectorResponse clutchSelectorResponse) throws IOException {
		LOGGER.debug("Entry into convertClutchBeanTOJSON method");
		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest;

		try {
			// Object to JSON in String using Jackson mapper
			jsonRequest = mapper.writeValueAsString(clutchSelectorResponse);

		} catch (JsonGenerationException e) {
			throw new JsonGenerationException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch (JsonMappingException e) {
			throw new JsonMappingException(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("JSON request :"+jsonRequest);
		LOGGER.debug("Exit from convertClutchBeanTOJSON method");

		return jsonRequest;
	}
	
}
