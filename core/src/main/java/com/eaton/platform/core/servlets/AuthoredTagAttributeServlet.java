package com.eaton.platform.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.EndecaFacetTag;
import com.eaton.platform.core.models.ProductGridSlingModel;
import com.eaton.platform.core.models.ResourceDecorator;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.pim.util.PIMUtil;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/authoredTagAttributes",
        })
public class AuthoredTagAttributeServlet extends SlingSafeMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(AuthoredTagAttributeServlet.class);

    @Reference
    private transient AdminService adminService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.info("******** AuthoredTagAttributeServlet execution begin ***********");
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final String pagePathFromRequest = PIMUtil.getPagePathFromRequest(request);
        if (StringUtils.isNotEmpty(pagePathFromRequest)) {
            final Locale locale = resourceResolver.resolve(pagePathFromRequest).adaptTo(Page.class).getLanguage(true);
            final Resource pageResource = resourceResolver.resolve(pagePathFromRequest);
            if (null != pageResource) {
                final Optional<Resource> productGridResource = pageResource.adaptTo(ResourceDecorator.class).findByResourceType(CommonConstants.PRODUCT_GRID_RESOURCE_TYPE);
                final HashMap<String, String> unsortedMap = new HashMap<>();
                if (productGridResource.isPresent()) {
                    final ProductGridSlingModel productGridSlingModel = productGridResource.get().adaptTo(ProductGridSlingModel.class);
                    if (null != productGridSlingModel) {
                        final List<String> facetTags = productGridSlingModel.getFacetTags();
                        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                        facetTags.forEach(Facet -> {
                            final String facetId = EndecaFacetTag.convertId(Facet);
                            if (facetId != null) {
                                final Tag selectedTag = tagManager.resolve(facetId);
                                if (null != selectedTag) {
                                    unsortedMap.put(selectedTag.getTitle(locale), selectedTag.getTagID());
                                }
                            }
                        });
                        try (ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
                            final List<Resource> dropdownList = PIMUtil.prepareDropDownList(adminReadResourceResolver, unsortedMap);
                            final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
                            request.setAttribute(DataSource.class.getName(), dataSource);
                        }
                        LOG.info("******** AuthoredTagAttributeServlet execution end ***********");
                    }
                } else {
                    LOG.warn("Resource is not preset in AuthoredTagAttributeServlet");
                }
            }
        }
    }
}
