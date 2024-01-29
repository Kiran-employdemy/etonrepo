package com.eaton.platform.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.constants.ServletConstants;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Servlet;

/**
 * This servlet pre-populates the How To Buy dropdown in author tab of scaffold page
 * author - TCS
 */

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/howtobuyTitlelist",

})
public class HowToBuyDropDownServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(HowToBuyDropDownServlet.class);

	// AdminService reference
	@Reference
	private transient AdminService adminService;

	/** The Constant PIM_SUPPORT_COUNTRY_LIST. */
	private static final String PIM_HOWTOBUY_TITLE_LIST = "howtoBuyTitleList";

	/** The Constant PIM_SITE_CONFIG_PAGE_PATH. */
	private static final String PIM_SITE_CONFIG_PAGE_FOLDER_PATH = "/etc/cloudservices/siteconfig/";

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

		LOG.info("******** HowToBuyDropDownServlet execution started 2***********");
		try {
				ResourceResolver resourceResolver = request.getResourceResolver();
				Page siteConfigPage = null;
				String[] howToBuyArr = CommonConstants.EMPTY_ARRAY;
				// item parameter exists in request when editing already created PIM page
				String currentPagePath = request.getParameter("item");
				// get request path info when creating a PIM page for the first time
				String reqPath = request.getPathInfo();
				Resource currentRes = null;
				String countryRes = null;
				// get admin resource resolver to resolve resource under /etc/

				if(null != currentPagePath){
					currentRes = resourceResolver.resolve(currentPagePath);
					 Resource res= currentRes.getParent();
					 if(res!=null)
						 countryRes = res.getName();
				} else {
					countryRes = StringUtils.substringAfterLast(reqPath, "/");
				}
				/*Get country for CommonPIM which is configured in PFP parent page properties.
				  All common PIM nodes stored in /var/commerce/products/eaton/country_name/shared/... */
				if(!countryRes.contains(CommonConstants.UNDER_SCORE)){
					Resource resCommonPIM = currentRes.getParent().getParent();
					if(resCommonPIM != null){
						countryRes = resCommonPIM.getName();
					}
				}
				/*assumption is that there exists a siteconfig page with the country folder name i.e.
				when creating a PIM page under folder /var/commerce/products/eaton/en_us, there must exist a siteconfig page
				with name en_us under /etc/cloudservices/siteconfig so that How to Buy Icon list is pulled  */

				// if cloud config object is not null, get the details
				if(null != countryRes){
					siteConfigPage = resourceResolver.resolve(PIM_SITE_CONFIG_PAGE_FOLDER_PATH.concat(countryRes)).adaptTo(Page.class);
				}

				if(null != siteConfigPage){
					// get Support Country values configured in cloud config page
					howToBuyArr = siteConfigPage.getProperties().get(PIM_HOWTOBUY_TITLE_LIST, String[].class);
				}

				// Create an ArrayList to hold data
				List<Resource> howToBuyLOV = new ArrayList<Resource>();
				JsonObject howToBuyObj = null;

				ValueMap valueMap =  new ValueMapDecorator(new HashMap<String, Object>());
				// populate the map with default empty option
				if(null != howToBuyArr && howToBuyArr.length > 0){
					for(String item : howToBuyArr){
						// create json object
						howToBuyObj = new JsonParser().parse(item).getAsJsonObject();

						// allocate memory to the Map instance
						valueMap = new ValueMapDecorator(new HashMap<String, Object>());

						// Specify the value and text values

						if(howToBuyObj.has(CommonConstants.VALUE) && StringUtils.isNotEmpty(howToBuyObj.get(CommonConstants.VALUE).getAsString())){
							valueMap.put(CommonConstants.VALUE, howToBuyObj.get(CommonConstants.VALUE).getAsString());
							if(howToBuyObj.has(CommonConstants.TEXT) && StringUtils.isNotEmpty(howToBuyObj.get(CommonConstants.TEXT).getAsString())){
						    valueMap.put(CommonConstants.TEXT, howToBuyObj.get(CommonConstants.TEXT).getAsString());
							}
						    if(howToBuyObj.has(CommonConstants.HOW_TO_BUY_ICON) && StringUtils.isNotEmpty(howToBuyObj.get(CommonConstants.HOW_TO_BUY_ICON).getAsString())){
						    valueMap.put(CommonConstants.HOW_TO_BUY_ICON, howToBuyObj.get("howtoBuyIcon").getAsString());
						    }
						    if(howToBuyObj.has(CommonConstants.HOW_TO_BUY_DROPDOWN_ICON) && StringUtils.isNotEmpty(howToBuyObj.get(CommonConstants.HOW_TO_BUY_DROPDOWN_ICON).getAsString())){
							valueMap.put(CommonConstants.HOW_TO_BUY_DROPDOWN_ICON, howToBuyObj.get("howtoBuyDropdownIcon").getAsString());
					         }

						}
							howToBuyLOV.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
						}
					}


				// Create a DataSource that is used to populate the drop-down control
				DataSource dataSource = new SimpleDataSource(howToBuyLOV.iterator());
				request.setAttribute(DataSource.class.getName(), dataSource);
		} catch (Exception e) {
			LOG.error("Exception while creating How to buy icon List dropdown :::"+e.getMessage());
		}


		LOG.info("******** HowToBuyDropDownServlet execution ended ***********");
	}

}