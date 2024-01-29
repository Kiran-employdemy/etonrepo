package com.eaton.platform.core.servlets;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.CustomRolloutConfigService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;



/**
 * RolloutPropertyListServlet
 * This class is used to provide populate Rollout Properties List in blueprint tab of page properties dialog.
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/authoring/rolloutpagepropertylist"
        })
public class RolloutPropertyListServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(RolloutPropertyListServlet.class);

    @Reference
    private CustomRolloutConfigService customRolloutConfigService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOGGER.info("******** RolloutPropertyListServlet execution begin ***********");
        if(CommonUtil.getRunModes().contains(Externalizer.AUTHOR)) {
            final ResourceResolver resourceResolver = request.getResourceResolver();
            final Map<String, String> rolloutPropertiesMap = customRolloutConfigService.getRolloutPageProperties();
            List<Resource> propertyResourceList = new ArrayList<>();

            TreeMap<String, String> rolloutPropertiesTreeMap = new TreeMap<>(rolloutPropertiesMap);
            for (Map.Entry<String, String> entry : rolloutPropertiesTreeMap.entrySet()) {
                ValueMapDecorator valueMapDecorator = getResourceList(entry.getKey(), entry.getValue());
                propertyResourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMapDecorator));
            }

            final DataSource dataSource = new SimpleDataSource(propertyResourceList.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);
        }
    }

    private static ValueMapDecorator getResourceList(String text, String value)  {
        LOGGER.debug("Start of RolloutPropertyListServlet - getResourceList().");
        final ValueMapDecorator valueMapDecorator = new ValueMapDecorator(new HashMap<String, Object>());
        valueMapDecorator.put(CommonConstants.VALUE, value);
        valueMapDecorator.put(CommonConstants.TEXT, text);
        LOGGER.debug("End of RolloutPropertyListServlet - getResourceList().");
        return valueMapDecorator;
    }
}
