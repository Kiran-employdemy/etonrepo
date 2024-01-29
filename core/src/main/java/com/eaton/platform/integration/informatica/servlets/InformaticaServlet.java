package com.eaton.platform.integration.informatica.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.jcr.Session;
import javax.servlet.Servlet;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.integration.informatica.bean.InformaticaConfigServiceBean;
import com.eaton.platform.integration.informatica.services.InformaticaOSGIService;
import com.eaton.platform.integration.informatica.services.InformaticaService;

/**
 * This servlet with proper selector call InformaticaOSGIService to get the
 * respective OSGI configuration bean from AEM OSGI configurations
 * 
 * @author TCS
 * 
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/InformaticaBatch",
		})
public class InformaticaServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(InformaticaServlet.class);
	private static final String GLOBAL_ATTR = "global-attribute";
	private static final String TAXONOMY_ATTR = "taxonomy-attribute";
	private static final String PRODUCT_FAMILY = "product-family";

	private InformaticaConfigServiceBean informaticaConfigServiceBean;
	private String userId;

	@Reference
	private transient InformaticaOSGIService informaticaOSGIService;// SonarQube
																	// private
																	// or
																	// transient
																	// issue

	@Reference
	private transient InformaticaService informaticaService;// SonarQube private
															// or transient
															// issue

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

		LOG.debug("******** InfomaticaServlet servlet execution started ***********");
		response.setContentType("text/html");
		// method to get the type of request
		String requestType = getRequestType(request);
		 userId = getCurrentUserId(request);
		if (LOG.isInfoEnabled()) {
			LOG.info("******** InfomaticaServlet request Type is  " + requestType);
			LOG.info("******** Current Looged in User   " + userId);
		}
		if (requestType != null) {

			informaticaConfigServiceBean = informaticaOSGIService.getConfigServiceBean();
			// method to import data
			String status = importData(requestType);
			response.getOutputStream().println(" InfomaticaService execution ended with	Status : <br>  " + status);
			

			LOG.info("******** InformaticaService execution ended with  {} ***********", status);
		}

		LOG.debug("******** InformaticaServlet servlet execution ended  for Request Type {} ***********", requestType);

	}

	/**
	 * This method call respective method to importa XML to AEM on the basis of
	 * requestType
	 * 
	 * @param requestType
	 * @return
	 */
	private String importData(String requestType) {

		String status = null;

		LOG.debug("********importData started ***********");
		try {
			if (requestType.equalsIgnoreCase(GLOBAL_ATTR)) {
				status = informaticaService.processInformaticaGlobalAttrData(informaticaConfigServiceBean, userId);
			}

			if (requestType.equalsIgnoreCase(PRODUCT_FAMILY)) {
				status = informaticaService.processInformaticaProductFamilyData(informaticaConfigServiceBean,userId);
			}

			if (requestType.equalsIgnoreCase(TAXONOMY_ATTR)) {
				// process Taxonomy Attributes bean from Informatica OSGI
				// Configuration Service
				status = informaticaService.processInformaticaTaxAttrData(informaticaConfigServiceBean,userId);
			}
			return status;
		} catch (Exception e) {
			LOG.error(" Exception Occured ", e.getMessage(), e);
		}
		return status;
	}

	/**
	 * This method is to get requestType on basis of Selectors value
	 * 
	 * @param request
	 * @return
	 */
	private String getRequestType(SlingHttpServletRequest request) {

		String[] selectors = request.getRequestPathInfo().getSelectors();
		String requestType = null;
		if (selectors.length != 0) {
			if (LOG.isInfoEnabled()) {
				LOG.info("selector Value is " + selectors[0]);
			}
			if (Arrays.asList(selectors).contains(PRODUCT_FAMILY)) {
				requestType = PRODUCT_FAMILY;
			}
			if (Arrays.asList(selectors).contains(TAXONOMY_ATTR)) {
				requestType = TAXONOMY_ATTR;
			}
			if (Arrays.asList(selectors).contains(GLOBAL_ATTR)) {
				requestType = GLOBAL_ATTR;
			}
			if (LOG.isInfoEnabled()) {
				LOG.info("RequestType is  " + requestType);
			}
		} else {
			LOG.info("There is no selectors mentioned in requested URL");
		}
		return requestType;

	}
	
	 public String getCurrentUserId(SlingHttpServletRequest request) {
		  ResourceResolver resolver = request.getResourceResolver();
		  String userId = null ;
		  if(resolver != null){
		  Session session = resolver.adaptTo(Session.class);
		  userId = session.getUserID();
		  }
		  return userId;

		 }

}
