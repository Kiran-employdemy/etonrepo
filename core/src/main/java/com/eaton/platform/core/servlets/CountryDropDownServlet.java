package com.eaton.platform.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.Servlet;

import static com.eaton.platform.core.constants.CommonConstants.CONTENT_ROOT_FOLDER;
import static com.eaton.platform.core.constants.CommonConstants.LANGUAGE_MASTERS_NODE_NAME;
import static com.eaton.platform.core.constants.CommonConstants.VALUE;
import static com.eaton.platform.core.constants.CommonConstants.TEXT;
import static com.eaton.platform.core.constants.CommonConstants.DROPDOWN_DEFAULT_OPTION;
import static com.eaton.platform.core.constants.CommonConstants.JCR_CONTENT_STR;
import static com.eaton.platform.core.constants.CommonConstants.JCR_TITLE;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/countrylist",
        })
public class CountryDropDownServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(CountryDropDownServlet.class);

    private static final String ALL_OPTION_TEXT = "All";
    private static final String ALL_OPTION_VALUE = "ALL";

    @Reference
    private transient AdminService adminService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.info("******** SupportCountryDropDownServlet execution started ***********");

        try {
            ResourceResolver resourceResolver = request.getResourceResolver();
            final List<Resource> countryOptions = new ArrayList<>();
            String contentRootFolderPath = request.getResource().getValueMap().get(CommonConstants.CONTENT_ROOT_FOLDER_PATH_PROPERTY_NAME, CONTENT_ROOT_FOLDER);
            countryOptions.add(defaultOption(resourceResolver));
            countryOptions.addAll(countryOptions(resourceResolver,contentRootFolderPath));
            countryOptions.add(allOption(resourceResolver));

            final DataSource dataSource = new SimpleDataSource(countryOptions.iterator());

            request.setAttribute(DataSource.class.getName(), dataSource);
        } catch (Exception e) {
            LOG.error("Exception while creating Support Country List dropdown :::" + e.getMessage());
        }

        LOG.info("******** SupportCountryDropDownServlet execution ended ***********");
    }

    private static List<ValueMapResource> countryOptions(ResourceResolver adminResourceResolver, final String contentRootFolderPath) {
        final List<ValueMapResource> options = new ArrayList<>();

        final Resource countriesResource = adminResourceResolver.getResource(contentRootFolderPath);
        if(countriesResource != null) {
            final Iterable<Resource> countries = countriesResource.getChildren();
            countries.forEach(country -> {
                if (!country.getName().equals(LANGUAGE_MASTERS_NODE_NAME)) {
                    final Resource jcrContent = country.getChild(JCR_CONTENT_STR);

                    if (jcrContent != null) {
                        final ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
                        valueMap.put(VALUE, country.getName().toUpperCase());
                        valueMap.put(TEXT, jcrContent.getValueMap().get(JCR_TITLE));

                        options.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(),
                            JcrConstants.NT_UNSTRUCTURED, valueMap));
                    }
                }
            });
        }

        return options;
    }

    private static ValueMapResource defaultOption(ResourceResolver adminResourceResolver) {
        final ValueMap option = new ValueMapDecorator(new HashMap<>());
        option.put(VALUE, StringUtils.EMPTY);
        option.put(TEXT, DROPDOWN_DEFAULT_OPTION);
        return new ValueMapResource(adminResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, option);
    }

    private static ValueMapResource allOption(ResourceResolver adminResourceResolver) {
        final ValueMap option = new ValueMapDecorator(new HashMap<>());
        option.put(VALUE, ALL_OPTION_VALUE);
        option.put(TEXT, ALL_OPTION_TEXT);
        return new ValueMapResource(adminResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, option);
    }
}