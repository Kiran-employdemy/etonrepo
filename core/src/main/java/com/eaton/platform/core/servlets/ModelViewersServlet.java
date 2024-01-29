package com.eaton.platform.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.ModelViewerCloudConfig;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.integration.pim.util.PIMUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/eatonmodelviewers"
        })
public class ModelViewersServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ModelViewersServlet.class);

    @Reference
    private CloudConfigService cloudConfigService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.info("******** TxnmyAttributeDropDownFormServlet execution begin ***********");
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final String pagePathFromRequest = PIMUtil.getPagePathFromRequest(request);
        final Resource pageResource = resourceResolver.resolve(pagePathFromRequest);
        final Optional<ModelViewerCloudConfig> threeDCloudConfig = cloudConfigService.getModelViewerCloudConfig(pageResource);
        final HashMap<String, String> modelViewers = new HashMap<>();

        if (threeDCloudConfig.isPresent()) {
            threeDCloudConfig.get().getModelViewers().stream()
                    .forEach(modelViewer -> modelViewers.put(modelViewer.getTitle(), modelViewer.getName()));
        }

        final List<Resource> dropdownList = PIMUtil.prepareDropDownList(resourceResolver, modelViewers);

        //Change Please Select text to None.
        if (dropdownList.size() > 0) {
            dropdownList.get(0).getValueMap().put("text", "None");
        }

        final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
        request.setAttribute(DataSource.class.getName(), dataSource);
    }
}
