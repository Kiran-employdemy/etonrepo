package com.eaton.platform.integration.pim.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.pim.util.PIMUtil;

import javax.servlet.Servlet;

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/GlobalAt",
				ServletConstants.SLING_SERVLET_EXTENSION_JSON
		})
public class GlobalAttrServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalAttrServlet.class);

	// AdminService reference
	@Reference private transient AdminService adminService;
	private String PATH = CommonConstants.GLOBAL_ATTR_IMPORT_PATH;
	private String value;
	private String text;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		try (ResourceResolver resourceResolver = adminService.getReadService()) {
                Resource resource = resourceResolver.getResource(PATH);
			    // prepare an unsorted Map which contains the "attributeName" and "attributeDisplayName" as key value pair
			    HashMap<String, String> unsortedMap = new HashMap<>();
			    Iterator<Resource> resourceList = null;
			    if (resource != null) {
				    resourceList = resource.listChildren();
			    }
			    JsonObject eachOption;
			    JsonArray optionsArray = new JsonArray();
			    /**fix for EAT-1280, displayName could be same for different attributes from pdh import but not attributeName
			     * and thus author can see uniques values on siteconfiguration
			     * However a check for attributeDisplayName is there to ensure global attribute has
			     * attributeDisplayName that can be shown on sku page specification
			     **/
			    if (resourceList != null) {
				    while (resourceList.hasNext()) {
					    Resource resourceItem = resourceList.next();
					    ValueMap valuemap = resourceItem.getValueMap();
					    text = (String) valuemap.get("attributeDisplayName");
					    value = (String) valuemap.get("attributeName");
					    if(StringUtils.isNotEmpty(text) && StringUtils.isNotEmpty(value)){
						    unsortedMap.put(value, value);
						}
			         }
				    // sort the unsorted valueMap
				    Map<String, String> sortedMap = PIMUtil.sortMapByValues(unsortedMap);
				    // prepare the droupdownlist by iterating sorted order values
				    for (Entry<String, String> entry : sortedMap.entrySet()) {
					    String displayValue = entry.getValue();
					    eachOption = new JsonObject();
					    eachOption.add(CommonConstants.TEXT, new Gson().toJsonTree(displayValue));
					    eachOption.add(CommonConstants.VALUE, new Gson().toJsonTree(displayValue));
					    optionsArray.add(eachOption);
				    }
			    }
			    JsonObject finalJsonResponse = new JsonObject();
			    // Adding this finalJsonResponse object to showcase optionsRoot
			    // property functionality
			    finalJsonResponse.add("root", optionsArray);
			    response.getWriter().println(finalJsonResponse.toString());
		}  catch (IOException e) {
			LOGGER.error("IOException occured while getting Print Writer from SlingServletResponse : ", e);
		}  catch (Exception e) {
			LOGGER.error("Exception occured while adding data to JSON Object : ", e);
		}
	}
}
