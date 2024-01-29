package com.eaton.platform.integration.pim.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonConfigService;

import javax.servlet.Servlet;

/**
 * This servlet pre-populates the Middle tab drop-down field
 * author - TCS
 */

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/middletab",
		})
public class MiddletabDropDownServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(MiddletabDropDownServlet.class);
	
	/** The admin service. */
	@Reference
	private transient AdminService adminService;//SonarQube private or transient issue
	
	/** Service to get OSGi configurations */
    @Reference
    private transient  ConfigurationAdmin configAdmin;//SonarQube private or transient issue
    
    /** The eaton config service. */
    @Reference
    private transient EatonConfigService eatonConfigService;//SonarQube private or transient issue
    
    /** The middle tab list page path list. */
    private transient List<String> middleTabListPagePathList = new ArrayList<>();//SonarQube private or transient issue
    
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

		LOG.info("******** MiddletabDropDownServlet execution started ***********");
		// Create an ArrayList to hold data
		List<Resource> dropdownList = new ArrayList<Resource>();
		ValueMap valueMap = null;

		middleTabListPagePathList = eatonConfigService.getConfigServiceBean().getMiddleTabListPagePathList();

		// set fallback
		request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
		
		if(null != middleTabListPagePathList){
			for (String element : middleTabListPagePathList) {
				// allocate memory to the Map instance
				valueMap = new ValueMapDecorator(new HashMap<String, Object>());
			
				// populate the map
				valueMap.put(CommonConstants.VALUE, element);
				valueMap.put(CommonConstants.TEXT, element);

				dropdownList.add(new ValueMapResource(null, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
				}
		}
	
			// Create a DataSource that is used to populate the drop-down control
			DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
			request.setAttribute(DataSource.class.getName(), dataSource);
			
		LOG.info("******** MiddletabDropDownServlet execution ended ***********");
	}

}