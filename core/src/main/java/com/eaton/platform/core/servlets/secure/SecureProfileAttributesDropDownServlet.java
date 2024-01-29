package com.eaton.platform.core.servlets.secure;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ProfileAttributesOptions;
import com.eaton.platform.core.constants.ServletConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Servlet to list all the User Profile Attributes as drop down options.
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/secureProfileFieldsDropdown",
        })
public class SecureProfileAttributesDropDownServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SecureProfileAttributesDropDownServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOGGER.info("******** SecureProfileAttributesDropDownServlet execution begin ***********");
        List<Resource> dropdownList = new ArrayList<>();
        for(ProfileAttributesOptions profileFieldOptions : ProfileAttributesOptions.values()){
            ValueMap vm = new ValueMapDecorator(new HashMap<>());
            vm.put( CommonConstants.TEXT, profileFieldOptions.name());
            vm.put(CommonConstants.VALUE, profileFieldOptions.getValue());
            dropdownList.add(new ValueMapResource(request.getResourceResolver(), new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm));
        }
        // Create a DataSource that is used to populate the drop-down control
        final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
        request.setAttribute(DataSource.class.getName(), dataSource);
    }
}
