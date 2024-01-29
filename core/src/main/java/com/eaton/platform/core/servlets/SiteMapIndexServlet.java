
package com.eaton.platform.core.servlets;

import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.eaton.platform.core.constants.*;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.JcrQueryUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.*;
import static org.apache.sling.query.SlingQuery.$;
//TODO consider to remove this servlet. Looks like it doesn't work properly.
// The resourceResolver is closed inside the getSiteMapPagePathList and exception is thrown on line 85
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/structure/eaton-cummins-edit-template-page",
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/structure/eaton-edit-template-page",
		ServletConstants.SLING_SERVLET_EXTENSION_XML
		})
public final class SiteMapIndexServlet extends SlingSafeMethodsServlet {

	public static final Logger LOG = LoggerFactory.getLogger(SiteMapIndexServlet.class);
	private static final String SITEMAP_INDEX ="sitemapindex";
	private static final String SITEMAP ="sitemap";
	private static final String LOC = "loc";
	private static final String PRODUCT_FAMILY_PAGES = "productFamilyPages";
	private static final String PAGE_NAME_MATCH_REGEX = "pageNameMatchRegex";

	@Reference
	AdminService adminService;

	@Override
	protected void doGet(final SlingHttpServletRequest request,final SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOG.info("Inside sitemap servlet");
		response.setCharacterEncoding(SiteMapConstants.UTF_8);
		response.setContentType(SiteMapConstants.APPLICATION_XML);
		final Optional<Resource> resourceOptional = Optional.of(request.getResource());
		resourceOptional.ifPresent(resource -> generateSiteMap(resource, response));
	}

	private void generateSiteMap(final Resource resource, final SlingHttpServletResponse response) {
		final Optional<Resource> siteMapResourceOptional = $(resource)
				.find(CommonConstants.RSRC_TYPE_CONTENT_SITEMAP)
				.asList().stream().findFirst();
		if (siteMapResourceOptional.isPresent()) {
			final ValueMap valueMap = siteMapResourceOptional.get().getValueMap();
			final String[] productFamilyPages = valueMap.containsKey(PRODUCT_FAMILY_PAGES) ?
					valueMap.get(PRODUCT_FAMILY_PAGES, new String[0]) : null;
			final String siteMapRegex = valueMap.containsKey(PAGE_NAME_MATCH_REGEX) ?
					valueMap.get(PAGE_NAME_MATCH_REGEX,StringUtils.EMPTY) : null;
			final String siteMapRootPath = valueMap.containsKey(CommonConstants.SITEMAP_ROOT_PATH) ?
					valueMap.get(CommonConstants.SITEMAP_ROOT_PATH,StringUtils.EMPTY)
					: CommonUtil.getSiteRootPrefixByPagePath(resource.getPath());
			final List<Hit> siteMapPagePathList = getSiteMapPagePathList(siteMapRootPath);
			List<String> siteMapListOfAllPages = new ArrayList<>();
			if (null != productFamilyPages && productFamilyPages.length > 0) {
				siteMapListOfAllPages.addAll(Arrays.asList(productFamilyPages));
			}
			if (null != siteMapPagePathList && siteMapPagePathList.size() > 0) {
				for (final Hit siteMap:siteMapPagePathList) {
					try {
						if(null != siteMap && StringUtils.isNotEmpty(siteMapRegex) &&
								siteMap.getNode().getName().matches(siteMapRegex) &&
								!siteMap.getPath().contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)) {
								siteMapListOfAllPages.add(siteMap.getPath());
						}
					} catch (RepositoryException e) {
						LOG.error("RepositoryException while getPath from query in generateSiteMap() method) ", e);
					}
				}
			}
			   createXmlStream(response, siteMapListOfAllPages);
			}
		}

	private List<Hit> getSiteMapPagePathList(final String siteMapRootPath) {
		List<Hit> productFamilyPageHitList = null;
		try (ResourceResolver resourceResolver = adminService.getReadService()) {
			final Map< String, String > queryParams = new HashMap< >();
			final Session session = resourceResolver.adaptTo(Session.class);
			final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
			if(null != queryBuilder && null != session) {
				queryParams.put(JcrQueryConstants.PROP_TYPE, CommonConstants.NODE_TYPE);
				queryParams.put(JcrQueryConstants.PROP_PATH, siteMapRootPath);
				queryParams.put(CommonConstants.PROP_NAME,CommonConstants.JCR_CONTENT_PAGE_TYPE);
				queryParams.put(CommonConstants.PROP_VALUE, CommonConstants.PAGE_TYPE_SITEMAP_PAGE);
				productFamilyPageHitList = JcrQueryUtils.excuteGenericQuery(session, queryBuilder, queryParams);
			}
		}
		return productFamilyPageHitList;
	}

	private void createXmlStream(final SlingHttpServletResponse response,final List<String> siteMapPagesList) {
		final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter streamWriter = null;
		ResourceResolver readService = null;
		try {
			readService = adminService.getReadService();
			streamWriter = outputFactory.createXMLStreamWriter(response.getWriter());
			streamWriter.writeStartDocument();
			streamWriter.writeStartElement(SITEMAP_INDEX);
			streamWriter.writeNamespace(StringUtils.EMPTY, SiteMapConstants.NS);
			for (final String siteMapPage : siteMapPagesList) {
				streamWriter.writeStartElement(SITEMAP);
				streamWriter.writeStartElement(LOC);

				final String productFamilyPageUrl = CommonUtil.dotHtmlLink(siteMapPage, readService);
				streamWriter.writeCharacters(productFamilyPageUrl.concat(CommonConstants.HTML_EXTN));
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
		}finally {
			try {
				if (readService != null) {
					readService.close();
				}
				if(streamWriter != null) {
					streamWriter.close();
				}
			} catch (XMLStreamException ex) {
				if (LOG.isDebugEnabled()) {
					LOG.error("XMLStreamException while createXmlStream() ", ex);
				}
			}
		}
	}
}