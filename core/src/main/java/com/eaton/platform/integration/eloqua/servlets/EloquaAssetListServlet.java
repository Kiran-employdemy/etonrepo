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
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This servlet populates the Asset LOV drop-down field in Eloqua cloud configuation page
 * 
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/dropdown/assetoptions",
		})
public class EloquaAssetListServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(EloquaAssetListServlet.class);
	
	// ConfigurationManagerFactory reference
	@Reference
	private transient ConfigurationManagerFactory configManagerFctry;//SonarQube private or transient issue
	
	// AdminService reference
	@Reference
	private transient AdminService adminService;//SonarQube private or transient issue

	@Reference
	private transient EloquaFormConfigService eloquaFormConfigService;
	
	/** The Constant ELOQUA_ASSET_PROPERTY_NAME. */
	private static final String ELOQUA_ASSET_PROPERTY_NAME = "assetLov";

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

		try (ResourceResolver adminResourceResolver = adminService.getReadService()){
			LOG.info("******** EloquaAssetListServlet servlet execution started ***********");
			String[] assetArr = CommonConstants.EMPTY_ARRAY;

			// set fallback
			request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
			
			// get configured LOVs for Asset field from Eloqua cloud configuration
			assetArr = eloquaFormConfigService.getLOVFromConfig(request, adminResourceResolver, configManagerFctry, ELOQUA_ASSET_PROPERTY_NAME);
			
			// Create an ArrayList to hold data
			List<Resource> assetLOV = new ArrayList<Resource>();
			JsonObject assetObj = null;
	
			ValueMap valueMap =  new ValueMapDecorator(new HashMap<String, Object>());
			// populate the map with default empty option
			valueMap.put(CommonConstants.VALUE, StringUtils.EMPTY);
			valueMap.put(CommonConstants.TEXT, CommonConstants.DROPDOWN_DEFAULT_OPTION);
			
			assetLOV.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
			if(null != assetArr && assetArr.length > 0){
				for(String item : assetArr){
					// create json object
					assetObj = new JsonParser().parse(item).getAsJsonObject();
					
					// allocate memory to the Map instance
					valueMap = new ValueMapDecorator(new HashMap<String, Object>());
	
					String dropDownValue = null;
					String dropDownText = null;
	
					// Specify the value and text values
					if(assetObj.has(CommonConstants.VALUE) && StringUtils.isNotEmpty(assetObj.get(CommonConstants.VALUE).getAsString())){
						dropDownValue = assetObj.get(CommonConstants.VALUE).getAsString();
					}
					if(assetObj.has(CommonConstants.TEXT) && StringUtils.isNotEmpty(assetObj.get(CommonConstants.TEXT).getAsString())){
						dropDownText = assetObj.get(CommonConstants.TEXT).getAsString();
					}
					
					// populate the map
					if(null != dropDownValue && null != dropDownText){
						valueMap.put(CommonConstants.VALUE, dropDownValue);
						valueMap.put(CommonConstants.TEXT, dropDownText);
	
						assetLOV.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
					}
				}
					
			}
			
			// Create a DataSource that is used to populate the drop-down control
			DataSource dataSource = new SimpleDataSource(assetLOV.iterator());
			request.setAttribute(DataSource.class.getName(), dataSource);
		} catch (Exception e) {
			LOG.error("JSONException while creating Asset LOV dropdown :::"+e.getMessage());
		}
		
		LOG.info("******** EloquaAssetListServlet servlet execution ended ***********");
	}

	
}