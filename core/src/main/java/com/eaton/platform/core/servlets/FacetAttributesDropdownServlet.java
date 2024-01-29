package com.eaton.platform.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.pim.util.PIMUtil;

import javax.servlet.Servlet;
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/facetAttributesDropdown",
		})
public class FacetAttributesDropdownServlet extends SlingSafeMethodsServlet{

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(FacetAttributesDropdownServlet.class);
	
	
	
	// AdminService reference
		@Reference
		private transient AdminService adminService;//SonarQube private or transient issue
	
		/** The Constant GLOBAL_ATTR_NAME. */
		private static final  String GLOBAL_ATTR_NAME = "attributeName";
		
		/** The Constant GLOBAL_ATTR_DISPLAY_NAME. */
		private static final  String GLOBAL_ATTR_DISPLAY_NAME = "attributeDisplayName";
		
		/** The Constant INDEX_START_STRING. */
		private static final  String INDEX_START_STRING = "_cq_dialog.html";
		
		/** The Constant INDEX_END_STRING. */
		private static final  String INDEX_END_STRING = "/jcr:content";
		
		/** The Constant PIM_PAGE_PATH. */
		private static final  String PIM_PAGE_PATH = "pimPagePath";
		

		
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		LOG.info("******** FacetAttributesDropdownServlet execution begin ***********");

			ResourceResolver resourceResolver = request.getResourceResolver();
			String requestString = request.getPathInfo();
			String pagePath = getPagePathFromRequest(requestString);

			PageManager pm = request.getResourceResolver().adaptTo(PageManager.class);
			Page currentPage =null;
			if(pm!=null){
				currentPage = pm.getPage(pagePath);
			}
			String pdhNodePath =null;
			if(currentPage != null){
				pdhNodePath = getPdhNodePath(currentPage);
			}
			// Create an ArrayList to hold data
			List<Resource> dropdownList = new ArrayList<>();
			ValueMap valueMap = null;

			Resource globalAttributeResource = resourceResolver.getResource(CommonConstants.GLOBAL_ATTR_IMPORT_PATH);

			// set fallback
			request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

			valueMap = new ValueMapDecorator(new HashMap<String, Object>());
			// populate the map with default empty option
			valueMap.put(CommonConstants.VALUE, StringUtils.EMPTY);
			valueMap.put(CommonConstants.TEXT, CommonConstants.DROPDOWN_DEFAULT_OPTION);

			dropdownList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(),
					JcrConstants.NT_UNSTRUCTURED, valueMap));

			// prepare an unsorted Map which contains the "attributeName" and "attributeDisplayName" as key value pair
			HashMap<String, String> unsortedMap = new HashMap<>();

			if(globalAttributeResource != null){
				Iterator<Resource> resourceList = globalAttributeResource.listChildren();

				while(resourceList.hasNext()){
					Resource resourceItem = resourceList.next();

					String optionValue = CommonUtil.getStringProperty(resourceItem.getValueMap(), GLOBAL_ATTR_NAME);
					String optionText = CommonUtil.getStringProperty(resourceItem.getValueMap(), GLOBAL_ATTR_DISPLAY_NAME);

					if(StringUtils.isNotEmpty(optionValue) && StringUtils.isNotEmpty(optionText)){
						unsortedMap.put(optionText, optionValue);
					}
				}
				// sort the unsorted valueMap and prepare the dropdownlist by iterating sorted order values
				dropdownList=PIMUtil.prepareDropDownList(resourceResolver, unsortedMap);
			}

			if (LOG.isInfoEnabled()) {
				LOG.debug("FacetAttributesDropdownServlet DropdownList Size : " + dropdownList.size());
			}
			// Create a DataSource that is used to populate the drop-down control
			DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
			request.setAttribute(DataSource.class.getName(), dataSource);
		LOG.info("******** FacetAttributesDropdownServlet execution ended ***********");

	}

	private String getPagePathFromRequest(String requestPath){
		String returnString = null;

		int indexStart = requestPath.indexOf(INDEX_START_STRING);
		indexStart = indexStart+15;
		int indexEnd = requestPath.indexOf(INDEX_END_STRING);

		returnString = requestPath.substring(indexStart, indexEnd);

		return returnString;
	}
		
		
	private String getPdhNodePath(Page page)
	{
		if(page.getProperties().get(PIM_PAGE_PATH) != null){
		String pimPath = page.getProperties().get(PIM_PAGE_PATH).toString();

		}
		return null;
	}
		
}
	
	

