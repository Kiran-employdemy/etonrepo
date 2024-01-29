package com.eaton.platform.core.servlets.sitemap;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.constants.SiteMapConstants;
import com.eaton.platform.core.services.SiteMapGenerationService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
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
import java.util.*;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_SELECTORS + "sitemap",
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES +"eaton/components/structure/eaton-edit-template-page",
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES +"/apps/eaton/components/structure/eaton-folder-page-component",
                ServletConstants.SLING_SERVLET_EXTENSION_XML
        })
public final class SiteMapGenerationServlet extends SlingSafeMethodsServlet {
	
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
    private static final String NO_INDEX_NO_FOLLOW  = "noindex,nofollow";
    private static final String PRODUCTFAMILYPAGE  = "product-family-page";
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteMapGenerationServlet.class);


    @Reference
    private transient Externalizer externalizer;

    @Reference
    private transient SiteMapGenerationService siteMapGenerationService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException, ServletException {

        LOGGER.debug("doGet :: START");
        response.setCharacterEncoding(SiteMapConstants.UTF_8);
        response.setContentType(SiteMapConstants.APPLICATION_XML);
        ResourceResolver resourceResolver = request.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page page = null;
        if (pageManager != null) {
            page = pageManager.getContainingPage(request.getResource());
        } else {
            LOGGER.warn("Unable to get page manager. Page not defined.");
        }

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        try {
            XMLStreamWriter stream = outputFactory.createXMLStreamWriter(response.getWriter());
            stream.writeStartDocument(SiteMapConstants.UTF_8, "1.0");

            stream.writeStartElement("", "urlset", SiteMapConstants.NS);
            stream.writeNamespace("", SiteMapConstants.NS);

            // first do the current page
            write(page, stream, request);

            writePageTree(page, stream, request);

            if (!siteMapGenerationService.getDamAssetTypes().isEmpty()) {
                for (Resource assetFolder : getAssetFolders(page, resourceResolver)) {
                    writeAssets(stream, assetFolder, resourceResolver, request);
                }
            } else {
                LOGGER.warn("Dam Asset Types in Site Map Generation Service is empty");
            }

            stream.writeEndElement();

            stream.writeEndDocument();
        } catch (XMLStreamException e) {
            LOGGER.error("XMLStreamException: {}", e.getMessage(), e);
            throw new IOException(e);
        }

        LOGGER.debug("doGet :: END");
    }
    
    /**
     * Recursive function to write the page tree
     * 
     * @param page
     * @param request 
     * @param stream
     */
    private void writePageTree(Page page, XMLStreamWriter stream, SlingHttpServletRequest request) {
        LOGGER.debug("writePageTree :: START");
        for (Iterator<Page> children = page.listChildren(); children.hasNext();) {
            Page child = children.next();
            if (isHidden(child)) {
                continue;
            }
            try {
                write(child, stream, request);
            } catch (XMLStreamException e) {
                LOGGER.error("XMLStreamException: {}", e.getMessage(), e);
            }
            writePageTree(child, stream, request);
        }
        LOGGER.debug("writePageTree :: END");
    }

    private Collection<Resource> getAssetFolders(Page page, ResourceResolver resolver) {
        LOGGER.debug("getAssetFolders :: START");
        List<Resource> allAssetFolders = new ArrayList<>();
        ValueMap properties = page.getProperties();
        String[] configuredAssetFolderPaths = {};
        if(properties != null){
            configuredAssetFolderPaths = properties.get(siteMapGenerationService.getDamAssetProperty(), String[].class);
        } else {
            LOGGER.warn("Cannot find page properties for page ({}). configuredAssetFolderPaths not set.", page.getPath());
        }
        if (configuredAssetFolderPaths != null) {
            // Sort to aid in removal of duplicate paths.
            Arrays.sort(configuredAssetFolderPaths);
            String prevPath = "#";
            for (String configuredAssetFolderPath : configuredAssetFolderPaths) {
                // Ensure that this folder is not a child folder of another
                // configured folder, since it will already be included when
                // the parent folder is traversed.
                if (StringUtils.isNotBlank(configuredAssetFolderPath) && !configuredAssetFolderPath.equals(prevPath)
                        && !StringUtils.startsWith(configuredAssetFolderPath, prevPath + "/")) {
                    Resource assetFolder = resolver.getResource(configuredAssetFolderPath);
                    if (assetFolder != null) {
                        prevPath = configuredAssetFolderPath;
                        allAssetFolders.add(assetFolder);
                    }
                }
            }
        }
        LOGGER.debug("getAssetFolders :: END");
        return allAssetFolders;
    }

    private void write(Page page, XMLStreamWriter stream, SlingHttpServletRequest request) throws XMLStreamException {
        LOGGER.debug("write :: START");
        if (isHidden(page) || isSecurePage(page)) {
            LOGGER.debug("Secure or Hidden Page Found --> Excluded ({})", page.getPath());
            LOGGER.debug("write :: END");
            return;
        }
        stream.writeStartElement(SiteMapConstants.NS, SiteMapConstants.URL);
        String loc;

        loc = externalizer.externalLink(request.getResourceResolver(), CommonUtil.getExternalizerDomainNameBySiteRootPath(page.getPath()),page.getPath());
        loc = loc.contains(CommonConstants.HTML_EXTN) ? loc : StringUtils.join(loc, CommonConstants.HTML_EXTN);
        LOGGER.debug("Page ({}) externalized url: {}", page.getPath(), loc);

        writeElement(stream, SiteMapConstants.LOC, loc);

        if (siteMapGenerationService != null && siteMapGenerationService.getIncludeLastModified()) {
            Calendar cal = page.getLastModified();
            if (cal != null) {
                writeElement(stream, SiteMapConstants.LASTMOD, DATE_FORMAT.format(cal));
            }
        }

        if (siteMapGenerationService != null && siteMapGenerationService.getIncludeInheritValue()) {
            HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap = new HierarchyNodeInheritanceValueMap(
                    page.getContentResource());
            writeFirstPropertyValue(stream, SiteMapConstants.CHANGEFREQ, siteMapGenerationService.getChangefreqProperties(), hierarchyNodeInheritanceValueMap);
            writeFirstPropertyValue(stream, SiteMapConstants.PRIORITY, siteMapGenerationService.getPriorityProperties(), hierarchyNodeInheritanceValueMap);
        } else if(siteMapGenerationService != null)  {
            ValueMap properties = page.getProperties();

                writeFirstPropertyValue(stream, SiteMapConstants.CHANGEFREQ, siteMapGenerationService.getChangefreqProperties(), properties);
                writeFirstPropertyValue(stream, SiteMapConstants.PRIORITY, siteMapGenerationService.getPriorityProperties(), properties);
        }else {
            LOGGER.debug("write :: END");
            return;
        }

        stream.writeEndElement();
        
        includeProductFamilySitemap(page, stream, loc);
        LOGGER.debug("write :: END");
    }
    
    private void includeProductFamilySitemap(Page page, XMLStreamWriter stream, String loc) throws XMLStreamException {
        LOGGER.debug("includeProductFamilySitemap :: START");
        
        if (page.getContentResource() != null) {

            String pageTemplate = page.getContentResource().getValueMap().get(CommonConstants.CQ_TEMPLATE_PROPERTY, String.class);
            LOGGER.debug("pageTemplate set to {} for page ({})", pageTemplate, page.getPath());

            if(StringUtils.isNotBlank(pageTemplate) && pageTemplate.contains(PRODUCTFAMILYPAGE)) {
                LOGGER.debug("Page is product family type page.");
                loc = loc.replace(CommonConstants.HTML_EXTN, SiteMapConstants.SKUMAP+SiteMapConstants.XML_EXT);
                LOGGER.debug("Page ({}) externalized url: {}", page.getPath(), loc);
                LOGGER.debug("Page ({}) externalized url: {}", page.getPath(), loc);
                stream.writeStartElement(SiteMapConstants.NS, SiteMapConstants.URL);
                writeElement(stream, SiteMapConstants.LOC, loc);
                if (siteMapGenerationService != null && siteMapGenerationService.getIncludeLastModified()) {
                    Calendar cal = page.getLastModified();
                    if (cal != null) {
                        writeElement(stream, SiteMapConstants.LASTMOD, DATE_FORMAT.format(cal));
                    }
                }
                if (siteMapGenerationService != null && siteMapGenerationService.getIncludeInheritValue()) {
                    HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap = new HierarchyNodeInheritanceValueMap(
                            page.getContentResource());
                    writeFirstPropertyValue(stream, SiteMapConstants.CHANGEFREQ, siteMapGenerationService.getChangefreqProperties(), hierarchyNodeInheritanceValueMap);
                    writeFirstPropertyValue(stream, SiteMapConstants.PRIORITY, siteMapGenerationService.getPriorityProperties(), hierarchyNodeInheritanceValueMap);
                } else if(siteMapGenerationService != null)  {
                    ValueMap properties = page.getProperties();
                    writeFirstPropertyValue(stream, SiteMapConstants.CHANGEFREQ, siteMapGenerationService.getChangefreqProperties(), properties);
                    writeFirstPropertyValue(stream, SiteMapConstants.PRIORITY, siteMapGenerationService.getPriorityProperties(), properties);
                }else {
                    LOGGER.debug("includeProductFamilySitemap :: END");
                    return;
                }
                stream.writeEndElement();
            }
        }

        LOGGER.debug("includeProductFamilySitemap :: END");
    }

    private boolean isHidden(final Page page) {
        LOGGER.debug("isHidden :: START");
        boolean externalExcludePropertyValue = page.getProperties().get(siteMapGenerationService.getExternalExcludeProperty(), false);
        LOGGER.debug("Page ({}) property {}: {}", page.getPath(), siteMapGenerationService.getExternalExcludeProperty(), externalExcludePropertyValue);
        boolean roboText = false;
        if(page.getProperties().containsKey(CommonConstants.META_ROBOT_TAGS)){
            roboText = page.getProperties().get(CommonConstants.META_ROBOT_TAGS).toString().equalsIgnoreCase(NO_INDEX_NO_FOLLOW);
            LOGGER.debug("Page ({}) {} page property: {}", page.getPath(), CommonConstants.META_ROBOT_TAGS, roboText);
        } else {
            LOGGER.warn("Page ({}) {} page property not found.", page.getPath(), CommonConstants.META_ROBOT_TAGS);
        }
        
        if (externalExcludePropertyValue || roboText) {
            LOGGER.debug("Page ({}) and all of its subpages have been hidden from the xml sitemap.", page.getPath());
        }
        LOGGER.debug("isHidden :: END");
        return externalExcludePropertyValue || roboText;
    }

    private void writeAsset(Asset asset, XMLStreamWriter stream, SlingHttpServletRequest request) throws XMLStreamException {
        LOGGER.debug("writeAsset :: START");
        if (isSecureAsset(asset)) {
            LOGGER.debug("Secure Asset Found --> Excluded ({})", asset.getPath());
            LOGGER.debug("writeAsset :: END");
            return;
        }
        stream.writeStartElement(SiteMapConstants.NS, SiteMapConstants.URL);

        String loc = externalizer.externalLink(request.getResourceResolver(), CommonUtil.getExternalizerDomainNameBySiteRootPath(asset.getPath()), asset.getPath());
        LOGGER.debug("Asset ({}) externalized link: {}", asset.getPath(), loc);
        writeElement(stream, SiteMapConstants.LOC, loc);

        if (siteMapGenerationService != null && siteMapGenerationService.getIncludeLastModified()) {
            long lastModified = asset.getLastModified();
            if (lastModified > 0) {
                writeElement(stream, SiteMapConstants.LASTMOD, DATE_FORMAT.format(lastModified));
            }
        } else {
            LOGGER.debug("Last Modified not included");
        }

        Resource contentResource = asset.adaptTo(Resource.class);
        if(contentResource != null && contentResource.getChild(JcrConstants.JCR_CONTENT) != null){
            contentResource = contentResource.getChild(JcrConstants.JCR_CONTENT);
        }
        if (contentResource != null && siteMapGenerationService != null) {
            if (siteMapGenerationService.getIncludeInheritValue()) {
                HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap = new HierarchyNodeInheritanceValueMap(
                        contentResource);
                writeFirstPropertyValue(stream, SiteMapConstants.CHANGEFREQ, siteMapGenerationService.getChangefreqProperties(), hierarchyNodeInheritanceValueMap);
                writeFirstPropertyValue(stream, SiteMapConstants.PRIORITY, siteMapGenerationService.getPriorityProperties(), hierarchyNodeInheritanceValueMap);
            } else {
                ValueMap properties = contentResource.getValueMap();
                writeFirstPropertyValue(stream, SiteMapConstants.CHANGEFREQ, siteMapGenerationService.getChangefreqProperties(), properties);
                writeFirstPropertyValue(stream, SiteMapConstants.PRIORITY, siteMapGenerationService.getPriorityProperties(), properties);
            }
        }

        stream.writeEndElement();
        LOGGER.debug("writeAsset :: END");
    }

    private void writeAssets(final XMLStreamWriter stream, final Resource assetFolder, final ResourceResolver resolver, final SlingHttpServletRequest request)
            throws XMLStreamException {
        LOGGER.debug("writeAssets :: START");
        for (Iterator<Resource> children = assetFolder.listChildren(); children.hasNext();) {
            Resource assetFolderChild = children.next();
            if (assetFolderChild.isResourceType(DamConstants.NT_DAM_ASSET)) {
                Asset asset = assetFolderChild.adaptTo(Asset.class);

                if (asset != null && siteMapGenerationService.getDamAssetTypes().contains(asset.getMimeType())) {
                    writeAsset(asset, stream, request);
                }
            } else {
                writeAssets(stream, assetFolderChild, resolver, request);
            }
        }
        LOGGER.debug("writeAssets :: END");
    }

    private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
                                         final String[] propertyNames, final ValueMap properties) throws XMLStreamException {
        LOGGER.debug("writeFirstPropertyValue :: START");
        for (String prop : propertyNames) {
            String value = properties.get(prop, String.class);
            LOGGER.debug("property value: {}", value);
            if (value != null) {
                writeElement(stream, elementName, value);
                LOGGER.debug("writeFirstPropertyValue :: END");
                break;
            }
        }
        LOGGER.debug("writeFirstPropertyValue :: END");
    }

    private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
                                         final String[] propertyNames, final InheritanceValueMap properties) throws XMLStreamException {
        LOGGER.debug("writeFirstPropertyValue :: START");
        for (String prop : propertyNames) {
            String value = properties.get(prop, String.class);
            if (value == null) {
                value = properties.getInherited(prop, String.class);
            }
            LOGGER.debug("property value: {}", value);
            if (value != null) {
                writeElement(stream, elementName, value);
                LOGGER.debug("writeFirstPropertyValue :: END");
                break;
            }
        }
        LOGGER.debug("writeFirstPropertyValue :: END");
    }

    private void writeElement(final XMLStreamWriter stream, final String elementName, final String text)
            throws XMLStreamException {
        LOGGER.debug("writeElement :: START");
        LOGGER.debug("element name: {}", elementName);
        LOGGER.debug("element text: {}", text);
        stream.writeStartElement(SiteMapConstants.NS, elementName);
        stream.writeCharacters(text);
        stream.writeEndElement();
        LOGGER.debug("writeElement :: END");
    }

    /**
     * Check if the page type is Secure
     * @param page
     * @return
     */
    private boolean isSecurePage(final Page page) {
        return page.getProperties().get(CommonConstants.SECURE_PROP_NAME_SECURE_PAGE, false);
    }

    /**
     * Check if Asset Type is Secure
     * @param asset
     * @return
     */
    private boolean isSecureAsset(final Asset asset) {
        return  (boolean) asset.getMetadata().getOrDefault(SecureConstants.METADATA_SECURE_ASSET, false);
    }

}
