package com.eaton.platform.core.servlets.sitemap;

import com.eaton.platform.core.bean.CountryLangCodeLastmodConfigBean;
import com.eaton.platform.core.bean.CountryLanguageCodeLastmodBean;
import com.eaton.platform.core.constants.*;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CountryLangCodeLastmodConfigService;
import com.eaton.platform.core.util.CommonUtil;

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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

@Component(service = Servlet.class,
		immediate = true,
		property = {
			ServletConstants.SLING_SERVLET_METHODS_GET,
			ServletConstants.SLING_SERVLET_PATHS + "/eaton/sitemap"
		})
public final class MasterSitemapServlet extends SlingSafeMethodsServlet {
	public static final long serialVersionUID = 2148181491826914878L;
	public static final Logger LOG = LoggerFactory.getLogger(MasterSitemapServlet.class);
	public static final String SITEMAP_INDEX ="sitemapindex";
	public static final String SITEMAP ="sitemap";
	public static final String SKUSITEMAP ="skusitemap";
	public static final String LOC = "loc";
	public static final String LASTMOD = "lastmod";
	/** The Constant X_DEFAULT. */
	private static final String X_DEFAULT = "x-default";
	/** The sling helper. */

	@Reference
	public transient CountryLangCodeLastmodConfigService countryLangCodeLastmodConfigService;

	@Reference
	public transient AdminService adminService;

	@Override
	protected void doGet(final SlingHttpServletRequest request,final SlingHttpServletResponse response)
			throws ServletException, IOException {

		LOG.debug("Inside master sitemap servlet");
		response.setCharacterEncoding(SiteMapConstants.UTF_8);
		response.setContentType(SiteMapConstants.APPLICATION_XML);
		final Optional<Resource> resourceOptional = Optional.of(request.getResource());
		resourceOptional.ifPresent(resource -> generateSiteMap(resource, response));
	}

	private void generateSiteMap(final Resource resource, final SlingHttpServletResponse response) {
		LOG.debug("MasterSitemapServlet :: generateSiteMap() :: Start");
		CountryLangCodeLastmodConfigBean countryLangCodeLastmodConfigBean = null;
		String sitemapUrl = StringUtils.EMPTY;
		String countryCode = StringUtils.EMPTY;
		String languageCode = StringUtils.EMPTY;
		Map<String, CountryLanguageCodeLastmodBean> countryLangCodeList = null;
		if (countryLangCodeLastmodConfigService != null) {
			countryLangCodeLastmodConfigBean = countryLangCodeLastmodConfigService.getCountryLangCodeLastmodConfigBean();
			countryLangCodeList = countryLangCodeLastmodConfigBean.getCountryLanguageCodesLastmodMap();
		}
		final Map<String, String> pageurlLastmodMap = new HashMap<>();
		if (countryLangCodeList != null) {
			for (Entry<String, CountryLanguageCodeLastmodBean> countryLanguageCodeEntry : countryLangCodeList.entrySet()) {
				String lastmodified = StringUtils.EMPTY;
				CountryLanguageCodeLastmodBean countryLanguageCodeBean = countryLanguageCodeEntry.getValue();
				sitemapUrl = getSitemapUrl(sitemapUrl, pageurlLastmodMap, lastmodified, countryLanguageCodeBean);
			}
		}
		createXmlStream(response, pageurlLastmodMap);
		LOG.debug("MasterSitemapServlet :: generateSiteMap() :: Exit");
	}

	private String getSitemapUrl(String sitemapUrl, Map<String, String> pageurlLastmodMap, String lastmodified, CountryLanguageCodeLastmodBean countryLanguageCodeBean) {
		String countryCode;
		String languageCode;
		if (countryLanguageCodeBean != null) {
			countryCode = countryLanguageCodeBean.getCountryCode();
			languageCode = countryLanguageCodeBean.getLanguageCode();
			if (adminService != null) {
				try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
					if (adminResourceResolver != null) {
						sitemapUrl = CommonConstants.SLASH_STRING + countryCode + CommonConstants.SLASH_STRING + languageCode + CommonConstants.PERIOD + SITEMAP + SiteMapConstants.XML_EXT;
						sitemapUrl = CommonUtil.dotHtmlLink(sitemapUrl, adminResourceResolver);
						LOG.debug(sitemapUrl);
						final String nodePath = CommonConstants.CONTENT_ROOT_FOLDER + countryCode + CommonConstants.SLASH_STRING + languageCode + CommonConstants.SLASH_STRING + CommonConstants.JCR_CONTENT_STR;
						final Resource resObj = adminResourceResolver.getResource(nodePath);
						lastmodified = getLastModified(resObj);
					}
				}
			}
			if (!sitemapUrl.contains(X_DEFAULT) && sitemapUrl != null && !sitemapUrl.isEmpty()) {
				pageurlLastmodMap.put(sitemapUrl, lastmodified);
			}
		}
		return sitemapUrl;
	}

	private String getLastModified(Resource resObj) {
		if (null != resObj) {
			Node node = resObj.adaptTo(Node.class);
			try {
				if (node != null) {
					if (node.hasProperty(CommonConstants.CQ_LAST_MODIFIED)) {
						return node.getProperty(CommonConstants.CQ_LAST_MODIFIED).getString();
					} else if (node.hasProperty(CommonConstants.JCR_CREATED)) {
						return node.getProperty(CommonConstants.JCR_CREATED).getString();
					}
				}
			} catch (RepositoryException e) {
				LOG.error("RepositoryException while generating sitemap", e);
			}
		}
		return StringUtils.EMPTY;
	}

	protected void createXmlStream(final SlingHttpServletResponse response, final  Map< String, String > pageurlLastmodMap) {
		LOG.debug("MasterSitemapServlet :: createXmlStream() :: Start");
		final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter streamWriter = null;
		ResourceResolver readService = null;
		try {
			readService = adminService.getReadService();
			streamWriter = outputFactory.createXMLStreamWriter(response.getWriter());
			streamWriter.writeStartDocument(SiteMapConstants.UTF_8, "1.0");
			streamWriter.writeStartElement(SITEMAP_INDEX);
			streamWriter.writeNamespace(StringUtils.EMPTY, SiteMapConstants.NS);
			for (Entry<String, String> siteMapPage : pageurlLastmodMap.entrySet()) {
				streamWriter.writeStartElement(SITEMAP);
				streamWriter.writeStartElement(LOC);
				final String productFamilyPageUrl = CommonUtil.dotHtmlLink(siteMapPage.getKey(), readService);
				streamWriter.writeCharacters(productFamilyPageUrl);
				streamWriter.writeEndElement();
				if(!siteMapPage.getValue().isEmpty()){
					streamWriter.writeStartElement(LASTMOD);
					streamWriter.writeCharacters(siteMapPage.getValue());
					streamWriter.writeEndElement();
				}
				streamWriter.writeEndElement();
				
				streamWriter.writeStartElement(SITEMAP);
				streamWriter.writeStartElement(LOC);
				String skuPageUrl = CommonUtil.dotHtmlLink(siteMapPage.getKey(), readService);
				skuPageUrl = skuPageUrl.replace(SITEMAP, SKUSITEMAP);
				streamWriter.writeCharacters(skuPageUrl);
				streamWriter.writeEndElement();
				if(!siteMapPage.getValue().isEmpty()){
					streamWriter.writeStartElement(LASTMOD);
					streamWriter.writeCharacters(siteMapPage.getValue());
					streamWriter.writeEndElement();
				}
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
		LOG.debug("MasterSitemapServlet :: createXmlStream() :: Exit");
	}
}