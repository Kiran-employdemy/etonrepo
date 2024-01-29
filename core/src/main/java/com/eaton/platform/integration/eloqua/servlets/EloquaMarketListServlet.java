package com.eaton.platform.integration.eloqua.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EloquaFormConfigService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This servlet populates the Market LOV drop-down field in Eloqua cloud configuation page
 * author - TCS
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/dropdown/marketoptions",
		})
public class EloquaMarketListServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(EloquaMarketListServlet.class);
	
	// ConfigurationManagerFactory reference
	@Reference
	private transient ConfigurationManagerFactory configManagerFctry;//SonarQube private or transient issue
	
	// AdminService reference
	@Reference
	private transient AdminService adminService;//SonarQube private or transient issue

	@Reference
	private transient EloquaFormConfigService eloquaFormConfigService;
	
	/** The Constant ELOQUA_MARKET_PROPERTY_NAME. */
	private static final String ELOQUA_MARKET_PROPERTY_NAME = "marketLov";

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

		try (ResourceResolver adminResourceResolver = adminService.getReadService()){
			LOG.info("******** EloquaMarketListServlet servlet execution started ***********");
			String[] marketArr = CommonConstants.EMPTY_ARRAY;
			
			// set fallback
			request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
			
			// get configured LOVs for Market field from Eloqua cloud configuration
			marketArr = eloquaFormConfigService.getLOVFromConfig(request, adminResourceResolver, configManagerFctry, ELOQUA_MARKET_PROPERTY_NAME);
			
			// Create an ArrayList to hold data
			List<Resource> marketLOV = new ArrayList<Resource>();
			JsonObject marketObj = null;
	
			ValueMap valueMap =  new ValueMapDecorator(new HashMap<String, Object>());
			// populate the map with default empty option
			valueMap.put(CommonConstants.VALUE, StringUtils.EMPTY);
			valueMap.put(CommonConstants.TEXT, CommonConstants.DROPDOWN_DEFAULT_OPTION);
			
			marketLOV.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
			if(null != marketArr && marketArr.length > 0){
				for(String item : marketArr){
					// create json object
					marketObj = new JsonParser().parse(item).getAsJsonObject();
					
					// allocate memory to the Map instance
					valueMap = new ValueMapDecorator(new HashMap<String, Object>());
	
					String dropDownValue = null;
					String dropDownText = null;
	
					// Specify the value and text values
					if(marketObj.has(CommonConstants.VALUE) && StringUtils.isNotEmpty(marketObj.get(CommonConstants.VALUE).getAsString())){
						dropDownValue = marketObj.get(CommonConstants.VALUE).getAsString();
					}
					if(marketObj.has(CommonConstants.TEXT) && StringUtils.isNotEmpty(marketObj.get(CommonConstants.TEXT).getAsString())){
						dropDownText = marketObj.get(CommonConstants.TEXT).getAsString();
					}
					
					// populate the map
					if(null != dropDownValue && null != dropDownText){
						valueMap.put(CommonConstants.VALUE, dropDownValue);
						valueMap.put(CommonConstants.TEXT, dropDownText);
	
						marketLOV.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
					}
				}
					
			}
			
			// Create a DataSource that is used to populate the drop-down control
			DataSource dataSource = new SimpleDataSource(marketLOV.iterator());
			request.setAttribute(DataSource.class.getName(), dataSource);
		} catch (Exception e) {
			LOG.error("JSONException while creating Market LOV dropdown :::"+e.getMessage());
		}
		
		LOG.info("******** EloquaMarketListServlet servlet execution ended ***********");
	}

	
}