package com.eaton.platform.core.servlets;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.simple.JSONObject;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This servlet pre-populates the Multilingual Content drop-down fields.
 */
@Component(service = Servlet.class,
    immediate = true,
    property = {
        ServletConstants.SLING_SERVLET_METHODS_GET,
        ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/multilingual",
        ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/multilingual",
        ServletConstants.SLING_SERVLET_EXTENSION_JSON
    })
public class MultilingualDropDownServlet extends SlingSafeMethodsServlet {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(MultilingualDropDownServlet.class);

    /** The admin service. */
    @Reference
    private transient AdminService adminService;//SonarQube private or transient issue

    /**  Service to get OSGi configurations. */
    @Reference
    private transient ConfigurationAdmin configAdmin;//SonarQube private or transient issue

    /** The Constant LANGUAGE_ROOT. */
    private static final String LANGUAGE_ROOT = "/content/cq:tags/eaton/language";

    /* (non-Javadoc)
     * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.debug("MultilingualDropDownServlet :: doGet() :: Start");

        // set fallback
        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            ResourceResolver reqResourceResolver = request.getResourceResolver();

            // Create an ArrayList to hold data
            List<Resource> dropdownList = new ArrayList<>();

            List<Resource> multilingualList = getMultilingualList(adminResourceResolver);
            for (Resource multilingualRes : multilingualList) {
                // allocate memory to the Map instance
                ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
                // Specify the value and text values
                valueMap.put(CommonConstants.TEXT, multilingualRes.getValueMap().get(CommonConstants.JCR_TITLE).toString() + " (" + multilingualRes.getName() + ")");
                valueMap.put(CommonConstants.VALUE, multilingualRes.getName());
                // populate the map
                dropdownList.add(new ValueMapResource(reqResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
            }

            getSortedList(dropdownList);

            // Create a DataSource that is used to populate the drop-down control
            request.setAttribute(DataSource.class.getName(), new SimpleDataSource(dropdownList.iterator()));

            if (request.getRequestPathInfo().getExtension().equals("json")) {
                response.getWriter().write(getJsonObject(dropdownList).toString());
            }
        } catch (RepositoryException ex) {
            LOG.error("An error occurred while getting the list of multilingual options.\n{}", ex);
        }

        LOG.debug("MultilingualDropDownServlet :: doGet() :: Exit");
    }

    /**
     * Gets the sorted list.
     *
     * @param dropdownList The list.
     */
    private void getSortedList(List<Resource> dropdownList) {
        Collections.sort(dropdownList, new Comparator<Resource>() {
            public int compare(Resource res1, Resource res2) {
                int comparisonValue = 0;
                comparisonValue = res1.getValueMap().get(CommonConstants.TEXT).toString()
                    .compareToIgnoreCase(res2.getValueMap().get(CommonConstants.TEXT).toString());
                return comparisonValue;
            }
        });
    }

    /**
     * Gets the multilingual options.
     *
     * @param resourceResolver Resolver used to retrieve the multilingual options.
     * @return The multilingual options.
     * @throws RepositoryException If an error occurs while retrieving the multilingual options.
     */
    private static List<Resource> getMultilingualList(ResourceResolver resourceResolver) throws RepositoryException {
        List<Resource> multilingualList = new ArrayList<>();
        Resource rootResource = resourceResolver.getResource(LANGUAGE_ROOT);
        Iterator<Resource> it = rootResource != null ? rootResource.listChildren() : new ArrayList<Resource>().iterator();
        while(it.hasNext()) {
            multilingualList.add(it.next());
        }
        return multilingualList;
    }

    /**
     * Gets the multilingual options in a json object.
     *
     * @param dropdownList The multilingual options.
     * @return The multilingual options JSON.
     */
    private static JSONObject getJsonObject(List<Resource> dropdownList) {
        JSONObject json = new JSONObject();
        for (Resource res : dropdownList) {
            json.put(res.getValueMap().get(CommonConstants.VALUE).toString(), res.getValueMap().get(CommonConstants.TEXT).toString());
        }
        return json;
    }
}
