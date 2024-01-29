
package com.eaton.platform.core.servlets;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.constants.SiteMapConstants;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteMapService;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/sitemap",
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES +"eaton/components/structure/eaton-edit-template-page",
				ServletConstants.SLING_SERVLET_EXTENSION_XML
		})
public final class EatonSitemapServlet extends SlingSafeMethodsServlet {

	public static final Logger LOGGER = LoggerFactory.getLogger(EatonSitemapServlet.class);

	@Reference
	private EatonSiteMapService eatonSiteMapService;

	@Reference
	private AdminService adminService;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		try (ResourceResolver resourceResolver = adminService.getWriteService()) {
			response.setCharacterEncoding(SiteMapConstants.UTF_8);
			response.setContentType(SiteMapConstants.APPLICATION_XML);
			if (null != eatonSiteMapService) {
                final String[] selectors = request.getRequestPathInfo().getSelectors();
                if (selectors.length > 0) {
                    final String siteMapType = selectors[0];
                    XMLStreamWriter xmlStream = createXmlStream(response);
                    final Optional<EatonSiteConfigModel> eatonSiteConfigModel = Optional.ofNullable(request
                            .adaptTo(EatonSiteConfigModel.class));
                    final String resourcePath = request.getRequestPathInfo().getResourcePath();
                    switch (siteMapType) {
                        case SiteMapConstants.MASTER: eatonSiteMapService.getSubCategorySiteMap(resourceResolver,
                                xmlStream);
                            break;

                        case SiteMapConstants.SUBCATEGORY:
                            eatonSiteMapService.getProductFamilySiteMap(resourceResolver, xmlStream, resourcePath,
                                    request);
							break;

                        case SiteMapConstants.SKU_MAP: eatonSiteMapService.getProductSkuSiteMap(resourceResolver,
                                xmlStream, eatonSiteConfigModel, resourcePath, request);
                            break;

                        default:
                            LOGGER.warn("Selector Value invalid");
                            break;

                    }
                }
            }
		}
	}

	private XMLStreamWriter createXmlStream(final SlingHttpServletResponse response) {
		final XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		XMLStreamWriter stream = null;
		try {
			stream = outputFactory.createXMLStreamWriter(response.getWriter());
			stream.writeStartDocument(SiteMapConstants.DOC_VERSION);
			stream.writeStartElement(StringUtils.EMPTY, SiteMapConstants.URLSET, SiteMapConstants.NS);
			stream.writeNamespace(StringUtils.EMPTY, SiteMapConstants.NS);
		} catch (XMLStreamException e) {
			LOGGER.error("XMLStreamException while creating xml stream", e);
		} catch (IOException e) {
			LOGGER.error("IO Exception while creating xml stream", e);
		}

		return stream;
	}
}