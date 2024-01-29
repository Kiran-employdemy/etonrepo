package com.eaton.platform.core.servlets.sitemap;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.constants.SiteMapConstants;
import com.eaton.platform.core.models.productgrid.ProductGridModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.familymodule.FamilyModuleBean;
import org.apache.commons.lang3.StringUtils;
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
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.sling.query.SlingQuery.$;

/**
 * The Class SiteMapSkuIndexServlet.
 *
 */
@Component(service = Servlet.class, immediate = true, property = { ServletConstants.SLING_SERVLET_METHODS_GET,
		ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/structure/eaton-edit-template-page",
		ServletConstants.SLING_SERVLET_SELECTORS + "skumap", ServletConstants.SLING_SERVLET_EXTENSION_XML })
public final class SiteMapSkuIndexServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = -3213749562976898387L;
	private static final Logger LOG = LoggerFactory.getLogger(SiteMapSkuIndexServlet.class);
	private static final String SITEMAP_INDEX = "urlset";
	private static final String SITEMAP = "url";
	private static final String LOC = "loc";
	private static final String RSRC_TYPE_CONTENT_PRODUCTGRID = "eaton/components/product/product-grid";

	@Reference
	private AdminService adminService;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOG.info("Inside sitemap servlet");
		LOG.debug("SiteMapSkuIndexServlet : Start from doGet() method");
		response.setCharacterEncoding(SiteMapConstants.UTF_8);
		response.setContentType(SiteMapConstants.APPLICATION_XML);
		final Optional<Resource> resourceOptional = Optional.of(request.getResource());
		resourceOptional.ifPresent(resource -> generateSiteMap(resource, request, response));
		LOG.debug("SiteMapSkuIndexServlet : Exit from doGet() method");
	}

	private void generateSiteMap(final Resource resource, final SlingHttpServletRequest request,
								 final SlingHttpServletResponse response) {
		LOG.debug("SiteMapSkuIndexServlet : Start from generateSiteMap() method");
		final Optional<Resource> siteMapResourceOptional = $(resource).find(RSRC_TYPE_CONTENT_PRODUCTGRID).asList()
				.stream().findFirst();
		if (siteMapResourceOptional.isPresent()) {
			List<String> skuPageURLsList = new ArrayList<>();
			ProductGridModel pgModel = request.adaptTo(ProductGridModel.class);
			List<FamilyModuleBean> familyModuleBeanList = pgModel.getFamilyModuleBeanList();
			if (null != familyModuleBeanList && !familyModuleBeanList.isEmpty()) {
				getSkuPageUrlList(skuPageURLsList, pgModel, familyModuleBeanList);
				createXmlStream(response, skuPageURLsList);
			}
		}
		LOG.debug("SiteMapSkuIndexServlet : Exit from generateSiteMap() method");
	}

	private void getSkuPageUrlList(List<String> skuPageURLsList, ProductGridModel pgModel, List<FamilyModuleBean> familyModuleBeanList) {
		for (FamilyModuleBean familyModuleBean : familyModuleBeanList) {
			String[] skusArray = familyModuleBean.getSkusArray();
			if(skusArray.length > 0) {
				for(int i=0; i<skusArray.length;i++) {
					skuPageURLsList.add(pgModel.getBaseSKUPath() + CommonConstants.PERIOD
							+ skusArray[i] + CommonConstants.HTML_EXTN);
				}
			}
		}
	}

	protected void createXmlStream(final SlingHttpServletResponse response, final List<String> siteMapPagesList) {
		LOG.debug("SiteMapSkuIndexServlet : Start from createXmlStream() method");
		final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter streamWriter = null;
		ResourceResolver readService = null;
		try {
			readService = adminService.getReadService();
			streamWriter = outputFactory.createXMLStreamWriter(response.getWriter());
			streamWriter.writeStartDocument(SiteMapConstants.UTF_8, "1.0");
			streamWriter.writeStartElement(SITEMAP_INDEX);
			streamWriter.writeNamespace(StringUtils.EMPTY, SiteMapConstants.NS);
			for (final String siteMapPage : siteMapPagesList) {
				streamWriter.writeStartElement(SITEMAP);
				streamWriter.writeStartElement(LOC);
				final String pageUrl = CommonUtil.dotHtmlLink(siteMapPage, readService);
				streamWriter.writeCharacters(pageUrl);
				streamWriter.writeEndElement();
				streamWriter.writeEndElement();
			}
			streamWriter.writeEndElement();
			streamWriter.writeEndDocument();
			streamWriter.flush();
			streamWriter.close();
		} catch (XMLStreamException ex) {
			LOG.error("XMLStreamException while createXmlStream() ", ex);
		} catch (IOException ex) {
			LOG.error("IO Exception while creating xml stream ", ex);
		} finally {
			try {
				if (readService != null) {
					readService.close();
				}
				if (streamWriter != null) {
					streamWriter.close();
				}
			} catch (XMLStreamException ex) {
				if (LOG.isDebugEnabled()) {
					LOG.error("XMLStreamException while createXmlStream() ", ex);
				}
			}
		}
		LOG.debug("SiteMapSkuIndexServlet : Exit from createXmlStream() method");
	}
}
