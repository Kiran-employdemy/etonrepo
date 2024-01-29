package com.eaton.platform.core.servlets;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.SmartCropConfigService;
import org.apache.jackrabbit.oak.spi.security.authorization.accesscontrol.AccessControlConstants;
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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This servlet pre-populates the Dynamic Rendition Content Id drop-down field.
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/ecom/content/dynamicRendition"
        })
public class DynamicRenditionDropDownServlet extends SlingSafeMethodsServlet {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(DynamicRenditionDropDownServlet.class);

    /** The admin service. */
    @Reference
    private transient AdminService adminService;

    @Reference
    private volatile transient List<SmartCropConfigService> smartCropConfigs;

    /* (non-Javadoc)
     * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        LOG.debug("DynamicRenditionDropDownServlet :: doGet() :: Start");

        ValueMap parameters = request.getResource().getChild("datasource").getValueMap();

        LOG.info("DynamicRenditionDropDownServlet :: selector " + parameters.get("selector"));

        // Set fallback
        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            // Create an ArrayList to hold data
            List<Resource> dropdownList = new ArrayList<>();

            List<String> dynamicRenditions = getDynamicRenditions(adminResourceResolver);
            for (String dynamicRendition : dynamicRenditions) {
                // allocate memory to the Map instance
                ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
                // Specify the value and text values
                valueMap.put(CommonConstants.TEXT, dynamicRendition);
                valueMap.put(CommonConstants.VALUE, CommonConstants.QUESTION_MARK_CHAR + CommonConstants.DOLLAR_CHAR + dynamicRendition + CommonConstants.DOLLAR_CHAR);
                // populate the map for mobile,desktop,tablet
                dropdownList.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
            }

            for (SmartCropConfigService smartCropConfig : smartCropConfigs) {
                // allocate memory to the Map instance
                ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
                // Specify the value and text values
                valueMap.put(CommonConstants.TEXT, smartCropConfig.getSmartCropTitle());
                valueMap.put(CommonConstants.VALUE, smartCropConfig.getSmartCropUrlSuffix());
                // populate the map for mobile,desktop,tablet
                dropdownList.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
            }

            getSortedList(dropdownList);

            // Set default option for mobile,desktop,tablet
            for (int i = 0; i < dropdownList.size(); i++) {
                String dropdownText = dropdownList.get(i).getValueMap().get(CommonConstants.TEXT).toString();
                boolean isDefaultDesktop = dropdownText.equalsIgnoreCase(CommonConstants.DEFAULT_DESKTOP_DYNAMIC_RENDITION) && parameters.get(CommonConstants.SELECTOR).equals(CommonConstants.DESKTOP);
                boolean isDefaultTablet = dropdownText.equalsIgnoreCase(CommonConstants.DEFAULT_TABLET_TRANSFORMATION) && parameters.get(CommonConstants.SELECTOR).equals(CommonConstants.TABLET);
                boolean isDefaultMobile = dropdownText.equalsIgnoreCase(CommonConstants.DEFAULT_MOBILE_DYNAMIC_RENDITION) && parameters.get(CommonConstants.SELECTOR).equals(CommonConstants.MOBILE);
                if (isDefaultDesktop || isDefaultTablet || isDefaultMobile) {
                    Resource defaultOption = dropdownList.remove(i);
                    dropdownList.add(0, defaultOption);
                }
            }

            // Create a DataSource that is used to populate the drop-down control
            DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);
        } catch (RepositoryException ex) {
            LOG.error("An error occurred while getting the list of image presets.\n{}", ex);
        }

        LOG.debug("DynamicRenditionDropDownServlet :: doGet() :: Exit");
    }

    /**
     * Gets the dynamic renditions.
     *
     * @param resourceResolver Resolver used to retrieve the dynamic renditions.
     * @return The dynamic renditions.
     * @throws RepositoryException If an error occurs while retrieving the dynamic renditions.
     */
    private static List<String> getDynamicRenditions(ResourceResolver resourceResolver) throws RepositoryException {
        List<String> dynamicRenditionList = new ArrayList<>();
        Resource presetsResource = resourceResolver.getResource(S7damConstants.IMAGE_PRESETS);
        Iterator<Resource> it = presetsResource != null ? presetsResource.listChildren() : new ArrayList<Resource>().iterator();
        while(it.hasNext()) {
            Resource resource = it.next();
            Node node = resource.adaptTo(Node.class);
            if(!node.isNodeType(AccessControlConstants.NT_REP_ACL)) {
                dynamicRenditionList.add(resource.getName());
            }
        }
        return dynamicRenditionList;
    }

    /**
     * Gets the sorted list.
     *
     * @param list the list.
     * @return The sorted list.
     */
    private static void getSortedList(List<Resource> list) {
        Collections.sort(list, new Comparator<Resource>() {
            public int compare(Resource value1, Resource value2) {
                int comparisonValue = 0;
                comparisonValue = value1.getValueMap().get(CommonConstants.TEXT).toString().compareToIgnoreCase(value2.getValueMap().get(CommonConstants.TEXT).toString());
                return comparisonValue;
            }
        });
    }
}
