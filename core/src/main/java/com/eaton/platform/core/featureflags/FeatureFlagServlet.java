package com.eaton.platform.core.featureflags;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.eaton.platform.core.constants.ServletConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;

/**
 * This servlet returns a datasource with the current list of experimental features for advanced search component
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/shared/feature-flags",
        })
public class FeatureFlagServlet extends SlingSafeMethodsServlet {

    public static final String MNT_OVERRIDE_APPS = "/mnt/override/apps";
    public static final String CQ_DIALOG_PATH_PART = "/cq:dialog";
    @Reference
    private FeatureFlagRegistryService featureFlagRegistryService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Resource parentResource = request.getResource().getParent();
        if (parentResource == null) {
            request.setAttribute(DataSource.class.getName(), new SimpleDataSource(Collections.emptyIterator()));
            return;
        }
        String resolutionPath = parentResource.getPath();
        String resolutionPathWithoutOverridePath = resolutionPath.replace(MNT_OVERRIDE_APPS, "");
        String componentPath = resolutionPathWithoutOverridePath.substring(1, resolutionPathWithoutOverridePath.indexOf(CQ_DIALOG_PATH_PART));
        DataSource dataSource = new SimpleDataSource(featureFlagRegistryService.getExperimentalFeaturesFor(componentPath, request).iterator());
        request.setAttribute(DataSource.class.getName(), dataSource);
    }

}
