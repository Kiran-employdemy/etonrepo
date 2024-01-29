package com.eaton.platform.core.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.util.CommonUtil;

import javax.servlet.Servlet;

/**
 * <html> Description: This servlet pre-populate the drop-down field,View from OSGI config </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/ecom/content/linklistDropdown",
		})
public class LinkListDropDownServlet extends SlingSafeMethodsServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(LinkListDropDownServlet.class);
	
	/** The Constant PRIMARY_NAVIGATION_OPTION. */
	private static final String PRIMARY_NAVIGATION_OPTION = "primary-navigation";
	
	@Reference
	private transient EatonConfigService eatonConfigService;//SonarQube private or transient issue
	
	/** The admin service. */
	@Reference
	private transient AdminService adminService;//SonarQube private or transient issue
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache
	 * .sling.api.SlingHttpServletRequest,
	 * org.apache.sling.api.SlingHttpServletResponse)
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

		LOG.debug("LinkListDropDownServlet :: doGet() :: Start");
		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			// set fallback
			request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
			// get admin resource resolver to resolve resource under /etc/cloudservices

			ResourceResolver resolver = request.getResource().getResourceResolver();

			// get the URL of the page on which this servlet is referred
			String refererURL = CommonUtil.getRefererURL(request);
			// get content path
			String resourcePath = CommonUtil.getContentPath(adminResourceResolver, refererURL);
			Resource currentPageRes = adminResourceResolver.resolve(resourcePath);
			Page currentPage = currentPageRes.adaptTo(Page.class);
			boolean isHomePage = CommonUtil.isHomePagePath(currentPage);
			// get the string array list of views in text||value pair
			List<String> linkListViews = eatonConfigService.getConfigServiceBean().getLinkListViews();

			List<String> viewList = new ArrayList<>();
			for(String view : linkListViews) {
				viewList.add(view);
			}

			// Create an ArrayList to hold data
			List<Resource> viewsResourceList = new ArrayList<>();

			// Add Link List views -text and value pair in drop down!
			for (String viewResource : viewList) {

				// allocate memory to the Map instance
				ValueMap valueMap = new ValueMapDecorator(new HashMap<String, Object>());
				if(viewResource.contains("||")) {
					String[] data = viewResource.split("\\|\\|"); // delimiter is --> ||
					if(null != data[0] && null != data[1]) {
						// Specify the value and text values
						String dropDownText = data[0].trim();
						String dropDownValue = data[1].trim();

						// populate the map
						valueMap.put(CommonConstants.VALUE, dropDownValue);
						valueMap.put(CommonConstants.TEXT, dropDownText);

						// remove Primary Navigation option from dropdown field if the page is not a home page
						if(!isHomePage &&  valueMap.containsValue(PRIMARY_NAVIGATION_OPTION)) {
							valueMap.remove(CommonConstants.VALUE, dropDownValue);
							valueMap.remove(CommonConstants.TEXT, dropDownText);
						}
						if(!valueMap.isEmpty()) {
							viewsResourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
						}
					}
				}
			}
			// Create a DataSource that is used to populate the drop-down control
			DataSource dataSource = new SimpleDataSource(viewsResourceList.iterator());
			request.setAttribute(DataSource.class.getName(), dataSource);

			LOG.debug("LinkListDropDownServlet :: doGet() :: Exit");
		}
	}
}
