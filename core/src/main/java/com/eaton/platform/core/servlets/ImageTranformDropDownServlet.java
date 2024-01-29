package com.eaton.platform.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
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

import javax.servlet.Servlet;

/**
 * This servlet pre-populates the Image Transformation Content Id drop-down field
 * author - TCS.
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/ecom/content/imageTransform",
		})
public class ImageTranformDropDownServlet extends SlingSafeMethodsServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ImageTranformDropDownServlet.class);

	/** The admin service. */
	@Reference
	private transient AdminService adminService;//SonarQube private or transient issue

	/**  Service to get OSGi configurations. */
    @Reference
    private transient ConfigurationAdmin configAdmin;//SonarQube private or transient issue

	private String imageTransformerImpl = "com.adobe.acs.commons.images.impl.NamedImageTransformerImpl";


	/* (non-Javadoc)
	 * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

		LOG.debug("ImageTranformDropDownServlet :: doGet() :: Start");
		try (ResourceResolver adminResourceResolver = adminService.getReadService()){
			ValueMap parameters = request.getResource().getChild("datasource").getValueMap();

			// set fallback
			request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

			List<Resource> namedImageConfigList = new ArrayList<>();
			// configuration resource
			Resource configRes = adminResourceResolver.getResource(CommonConstants.CONFIG_PATH);
			if (null != configRes && configRes.hasChildren()) {
				Iterator<Resource> configResItr = configRes.listChildren();
					while (configResItr.hasNext()) {
						Resource configItemRes = configResItr.next();
						if (null != configItemRes && configItemRes.getName().contains(imageTransformerImpl)) {
							if (configItemRes.getChild(CommonConstants.JCR_CONTENT_STR) != null) {
								namedImageConfigList.add(configItemRes.getChild(CommonConstants.JCR_CONTENT_STR));
							} else {
								namedImageConfigList.add(configItemRes);
							}
						}
					}
			}
			ValueMap vm = null;
			StringWriter writer = null;
			List<String> namePropList = new ArrayList<>();
			for (Resource namedImageConfigRes : namedImageConfigList) {
				writer = new StringWriter();
				vm = namedImageConfigRes.getValueMap();
				if (vm.containsKey(CommonConstants.JCR_DATA)) {
					IOUtils.copy((InputStream) vm.get(CommonConstants.JCR_DATA), writer, StandardCharsets.UTF_8);
					if (null != writer.toString()) {
						String nameProp = StringUtils.EMPTY;
						nameProp = StringUtils.substringAfter(writer.toString(), "name=\"");
						nameProp = StringUtils.substringBefore(nameProp, "transforms=");
						nameProp = StringUtils.remove(nameProp, "\"");
						if (StringUtils.isNotBlank(nameProp)) {
							namePropList.add(nameProp);
						}
					}
				} else if(StringUtils.isNotBlank(vm.get("name").toString())){
						namePropList.add(vm.get("name").toString());
				}
			}

			getSortedList(namePropList);
			// Create an ArrayList to hold data
			List<Resource> dropdownList = new ArrayList<>();

			if (!namePropList.isEmpty()) {
				for (String item : namePropList) {
					// populate the map for mobile,desktop,tablet
					if (item.contains(CommonConstants.DESKTOP) && parameters.get(CommonConstants.SELECTOR).equals(CommonConstants.DESKTOP)) {
						getDropdownList(new ValueMapDecorator(new HashMap<>()), item,
								CommonConstants.DEFAULT_DESKTOP_TRANSFORMATION, dropdownList, request.getResourceResolver());
					}
					if (item.contains(CommonConstants.MOBILE) && parameters.get(CommonConstants.SELECTOR).equals(CommonConstants.MOBILE)) {
						getDropdownList(new ValueMapDecorator(new HashMap<>()), item, CommonConstants.DEFAULT_MOBILE_TRANSFORMATION
								, dropdownList, request.getResourceResolver());
					}
					if (item.contains(CommonConstants.TABLET) && parameters.get(CommonConstants.SELECTOR).equals(CommonConstants.TABLET)) {
						getDropdownList(new ValueMapDecorator(new HashMap<>()), item, CommonConstants.DEFAULT_TABLET_TRANSFORMATION
								, dropdownList, request.getResourceResolver());
					}

				}

				// Create a DataSource that is used to populate the drop-down control
				DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
				request.setAttribute(DataSource.class.getName(), dataSource);

			}
		}
		LOG.debug("ImageTranformDropDownServlet :: doGet() :: Exit");
	}

	/**
	 * Gets the dropdown list
	 *
	 * @param valueMap the valueMap
	 * @param item	the dropdown text and value
	 * @param defaultTransformationDevice the default value for transformation device
	 * @param dropdownList the list to add values
	 * @param resourceResolver the resource resolver
	 * @return the dropdownList
	 */
	private static void getDropdownList(ValueMap valueMap, String item,
								  String defaultTransformationDevice, List<Resource> dropdownList,
								  ResourceResolver resourceResolver) {
		valueMap.put(CommonConstants.VALUE, item);
		valueMap.put(CommonConstants.TEXT, item);
		if(item.equalsIgnoreCase(defaultTransformationDevice)){
		dropdownList.add(0,new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
		}
		else{
			dropdownList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
		}
	}

	/**
	 * Gets the sorted list.
	 *
	 * @param namePropList the list
	 * @return the sorted list
	 */
	private void getSortedList(List<String> namePropList) {
		Collections.sort(namePropList, String::compareToIgnoreCase);

		// after sorting add the default configurations for desktop, mobile and tablet in the end of list
			namePropList.add(CommonConstants.DEFALUT_DESKTOP);
			namePropList.add(CommonConstants.DEFAULT_MOBILE);
			namePropList.add(CommonConstants.DEFAULT_TABLET);

	}

}