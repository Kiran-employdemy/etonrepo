/*
 * Creating the servlet SkusitemapServlet to create an Orphan Sku sitemap
 * */
package com.eaton.platform.core.servlets.sitemap;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.SiteMapConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CountryLangCodeLastmodConfigService;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang3.StringUtils;
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
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component(service = Servlet.class,
		immediate = true,
		property = {
			ServletConstants.SLING_SERVLET_METHODS_GET,
			ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/structure/eaton-edit-template-page",
			ServletConstants.SLING_SERVLET_SELECTORS + "skusitemap",
			ServletConstants.SLING_SERVLET_EXTENSION_XML
		})
/**
 * Class SkusitemapServlet to create an Orphan Sku sitemap
 **/
public final class SkusitemapServlet extends SlingSafeMethodsServlet {
	public static final long serialVersionUID = 2148181491826914878L;
	private static final Logger LOG = LoggerFactory.getLogger(SkusitemapServlet.class);
	public static final String URLSET = "urlset";
	public static final String URL = "url";
	public static final String LOC = "loc";
	public static final String LASTMOD = "lastmod";

	/** The CountryLangCodeLastmodConfigService */
	@Reference
	public transient CountryLangCodeLastmodConfigService countryLangCodeLastmodConfigService;

	/** The AdminService */
	@Reference
	public transient AdminService adminService;
	
	/** The EndecaRequestService */
	@Reference
	public transient EndecaRequestService endecaRequestService;
	
	/** The EatonConfigService */
	@Reference
	public transient EndecaService endecaService;
	
	/** The EatonConfigService */
	@Reference
	public transient EatonConfigService configService;

	@Override
	protected void doGet(final SlingHttpServletRequest request,final SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOG.debug("Inside Sku sitemap servlet");
		response.setCharacterEncoding(SiteMapConstants.UTF_8);
		response.setContentType(SiteMapConstants.APPLICATION_XML);
		final Optional<Resource> resourceOptional = Optional.of(request.getResource());
		resourceOptional.ifPresent(resource -> generateSiteMap(resource, request, response));
	}

	private void generateSiteMap(final Resource resource, final SlingHttpServletRequest request,
			final SlingHttpServletResponse response) {
		LOG.debug("SkusitemapServlet :: generateSiteMap() :: Start");
		final PageManager pageManager = request.getResource().getResourceResolver().adaptTo(PageManager.class);
		final Page page = pageManager.getContainingPage(resource.getPath());
		final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
				.getOrphanSkuSiteMapEndecaRequestBean(page,
						CommonConstants.DEFAULT_STARTING_RECORD);
		final String[] skuArray;
		if(endecaService!=null) {
			final SKUListResponseBean skuListResponseBean = endecaService.getSKUList(endecaServiceRequestBean,resource);
			if(skuListResponseBean!=null && skuListResponseBean.getFamilyModuleResponse().getTotalCount()!=null && skuListResponseBean.getFamilyModuleResponse().getTotalCount()>0) {
				skuArray = skuListResponseBean.getFamilyModuleResponse().getFamilyModule().get(0).getSkusArray();
				String skuPageName=StringUtils.EMPTY;
				if(configService!=null) {
					skuPageName = configService.getConfigServiceBean().getSkupagename();
				}
				final String baseSKUPath = CommonUtil.getSKUPagePath(page, skuPageName);
				String skuPagePath;
				List<String> skuPageURLsList = new ArrayList<>();
				for(int arrayCount=0;arrayCount<skuArray.length;arrayCount++) {
					if(!baseSKUPath.isEmpty()) {
						skuArray[arrayCount]=checkSlash(skuArray[arrayCount]);
						skuPagePath = baseSKUPath.concat(CommonConstants.PERIOD+skuArray[arrayCount]+CommonConstants.HTML_EXTN);
						skuPageURLsList.add(skuPagePath);
					}
				}
				createXmlStream(response, skuPageURLsList);
				LOG.debug("SkusitemapServlet :: generateSiteMap() :: Exit");			
			}
		}
	}

	public static String checkSlash(String checkSlash) {
		if(checkSlash.contains("/")) {
			checkSlash = checkSlash.replaceAll("/", "%7B%7D");
		}
		return checkSlash;
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
			streamWriter.writeStartElement(URLSET);
			streamWriter.writeNamespace(StringUtils.EMPTY, SiteMapConstants.NS);
			for (final String siteMapPage : siteMapPagesList) {
				streamWriter.writeStartElement(URL);
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
