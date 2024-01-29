package com.eaton.platform.core.servlets;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.tagging.Tag;
import com.day.crx.JcrConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.pim.util.PIMUtil;
import org.apache.commons.lang.StringUtils;
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

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/facetGroupList",
                ServletConstants.SLING_SERVLET_SELECTORS + "eatonproperties"
        })
public class FacetGroupListServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(FacetGroupListServlet.class);

    @Reference
    private transient AdminService adminService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.info("******** TagAttributesDropdownServlet execution begin ***********");
        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            // Set fallback.
            request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
            final String pagePath = StringUtils.substringAfter(request.getPathInfo(), CommonConstants.HTML_EXTN);
            final String globalTagAttrImportPath = CommonUtil.getMatchedTagPathBySiteRootPathPrefix(pagePath, CommonConstants.globalAttributeImportPathConfig);
            final HashMap<String, String> unsortedMap = new HashMap<>();
            final Iterator<Tag> tagIterator = request.getResourceResolver().resolve(globalTagAttrImportPath).adaptTo(Tag.class).listChildren();
            tagIterator.forEachRemaining(tag -> {
                if (StringUtils.isNotEmpty(tag.getTitle())) {
                    unsortedMap.put(tag.getTitle(), tag.getTitle());
                }
            });
            // Create a DataSource that is used to populate the drop-down control.
            final List<Resource> dropdownList = PIMUtil.prepareDropDownList(adminResourceResolver, unsortedMap);
            final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);
        }
        LOG.info("******** TagAttributesDropdownServlet execution ended ***********");
    }
}

