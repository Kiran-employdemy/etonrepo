
package com.eaton.platform.core.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.servlets.config.SitemapFamilyProductServletConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.util.CommonUtil;

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/extensionids",
		})
@Designate(ocd = SitemapFamilyProductServletConfig.class)
public final class SitemapFamilyProductServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_PATH_SKU_PRODUCT = "/var/commerce/products/eaton";
	public static final String DEFAULT_XML_DAM_PATH ="/content/dam/eaton/resources/xml/";
	private static final String XML_TAG_PRODUCT_FAMILIY = "PRODUCT_FAMILY";
	private static final String XML_TAG_EXTENSION_ID = "EXTENSION_ID";
	private static final String XML_FILENAME ="sitemapFamilyProduct.xml";
	
	private static final String PROPERTY_CQ_COMMERCETYPE ="cq:commerceType";
	private static final String PROPERTY_VALUE_CQ_COMMERCETYPE ="product";
	private String skuProductPath;
	private String generatedXmlPath;
	private boolean generateXml;
	private File xmlFile;

	@Activate
	protected void activate(final  SitemapFamilyProductServletConfig config) {
		this.skuProductPath = config.family_product_path();
		this.generatedXmlPath = config.xml_file_path();
		this.generateXml = config.generate_xmlFile();
	}

	public static final Logger LOG = LoggerFactory.getLogger(SitemapFamilyProductServlet.class);

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		XMLStreamWriter streamWriter = null;
		response.setContentType(request.getResponseContentType());
		ResourceResolver resourceResolver = request.getResourceResolver();

		try {			
		    streamWriter = getXmlStreamWriter(response);
		    streamWriter.writeStartDocument();
			streamWriter.writeStartElement(XML_TAG_PRODUCT_FAMILIY);
			generateExtensionIdXml(resourceResolver, streamWriter);
			streamWriter.writeEndElement();
			streamWriter.writeEndDocument();						
			streamWriter.flush();
			streamWriter.close();

		} catch (XMLStreamException e) {
			throw new IOException(e);
		} finally {
			if (streamWriter != null)
				try {
					streamWriter.close();
				} catch (XMLStreamException ex) {
					if(LOG.isDebugEnabled()){
						LOG.debug(ex.getMessage());
					}
				}
			
		}
		if(generateXml && xmlFile.exists() ){
			try(InputStream is = new FileInputStream(xmlFile)){
				Session session = resourceResolver.adaptTo(Session.class);
				if(null!=session){
				CommonUtil.writeFileIntoDAM(resourceResolver,session,xmlFile,is,generatedXmlPath);
				response.getWriter().println("xml file has been generated");
				session.save();
				session.logout();
				}
			}catch(RepositoryException ex){
				LOG.error(ex.getMessage());
			}
		}
		
		
	}

	/**
	 * @param resourceResolver
	 * @param streamWriter
	 * @throws XMLStreamException
	 * 
	 *             Method to generate xml listed down extendion ids for product
	 *             family.
	 */
	private void generateExtensionIdXml(ResourceResolver resourceResolver, XMLStreamWriter streamWriter)
			throws XMLStreamException {
		Resource res = resourceResolver.getResource(skuProductPath);
		if (null != res) {
			Node eatonNode = res.adaptTo(Node.class);
			try {
				NodeIterator localeNodes = null;
				if (eatonNode != null) {
					localeNodes = eatonNode.getNodes();
				}
				if (null != localeNodes) {
					while (localeNodes.hasNext()) {

						Node localeNode = localeNodes.nextNode();
						if (localeNode.hasNodes()) {
							NodeIterator productNodes = localeNode.getNodes();
							while (null!=productNodes && productNodes.hasNext()) {
								Node productNode = productNodes.nextNode();
								if(isProductType(productNode)){
								streamWriter.writeStartElement(XML_TAG_EXTENSION_ID);
								String productTitle = productNode.getProperty(javax.jcr.Property.JCR_TITLE).getValue()
										.toString();
								streamWriter.writeCharacters(productTitle);
								streamWriter.writeEndElement();
								}
							}

						} else {
							LOG.debug("No product node found at " + localeNode.getPath());
						}

					}
				}
			} catch (RepositoryException e) {
				LOG.error("Error found at " + e.getMessage());
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No Resource found at " + skuProductPath);
			}
		}
	}
	
	/**
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private XMLStreamWriter getXmlStreamWriter(SlingHttpServletResponse response) throws IOException, XMLStreamException
	{
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	
	XMLStreamWriter streamWriter=null;
	if(generateXml){
		xmlFile =new File(XML_FILENAME);
		streamWriter = outputFactory.createXMLStreamWriter(new FileWriter(xmlFile));				
	}else{
		streamWriter = outputFactory.createXMLStreamWriter(response.getWriter());
	}
	return streamWriter;
}

	
	/**
	 * @param productNode
	 * @return true if product node has product property else false.
	 * 
	 */
	private Boolean isProductType(Node productNode){
		try {
			if(productNode!=null && productNode.hasProperty(PROPERTY_CQ_COMMERCETYPE)){
			String commerceType =productNode.getProperty(PROPERTY_CQ_COMMERCETYPE).getValue().getString();
			if(StringUtils.isNotBlank(commerceType)&&commerceType.equalsIgnoreCase(PROPERTY_VALUE_CQ_COMMERCETYPE)){
			return true;
			 }
			}
		} catch (RepositoryException e) {
			LOG.error(e.getMessage());
		}
		return false;
	}
}