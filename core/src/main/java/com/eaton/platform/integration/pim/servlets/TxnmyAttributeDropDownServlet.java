package com.eaton.platform.integration.pim.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.services.TxnmyAttributeDropDownService;
import com.eaton.platform.core.util.CommonUtil;
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

/**
 * This servlet pre-populates the Taxonomy Attribute drop-down field in teaser
 * tab of page properties author - TCS
 */

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/txnmyattributes",
		})
public class TxnmyAttributeDropDownServlet extends SlingSafeMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(TxnmyAttributeDropDownServlet.class);
    private static final long serialVersionUID = 1L;

    @Reference
    private CloudConfigService cloudConfigService;

    @Reference
    private TxnmyAttributeDropDownService txnmyAttributeDropDownService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        LOG.info("******** TxnmyAttributeDropDownServlet execution started 1***********");

        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

        final String pagePath = PIMUtil.getPagePathFromRequest(request);
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        HashMap<String, String> unsortedMap = new HashMap<>();
        if (null != pageManager && null != pagePath && CommonUtil.startsWithAnySiteContentRootPath(pagePath)) {
            final Page currentPage = pageManager.getPage(pagePath);
            if (null != currentPage) {
                unsortedMap =  txnmyAttributeDropDownService.constructDropdownOptions(resourceResolver, currentPage);
            } else {
                LOG.error("TxnmyAttributeDropDownServlet: Could not retrieve a PIM path from the page: " + pagePath);
            }
        } else {
            unsortedMap = txnmyAttributeDropDownService.getAttributesFromPimResource(resourceResolver, pagePath, Optional.empty());
        }

        final List<Resource> dropdownList = PIMUtil.prepareDropDownList(resourceResolver, unsortedMap);

        if (LOG.isInfoEnabled()) {
            LOG.debug("TxnmyAttributeDropDownServlet DropdownList Size : " + dropdownList.size());
        }

        // Create a DataSource that is used to populate the drop-down control
        final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
        request.setAttribute(DataSource.class.getName(), dataSource);

        LOG.info("******** TxnmyAttributeDropDownServlet execution ended ***********");
    }
}